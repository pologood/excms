package cn.lonsun.content.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.content.internal.job.GuestBookTaskImpl;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IForwardRecordService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.job.util.ScheduleJobUtil;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.GuestListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

import static cn.lonsun.util.ModelConfigUtil.getCongfigVO;

/**
 * @author hujun
 * @ClassName: GuestBookServiceImpl
 * @Description: 留言管理业务逻辑
 * @date 2015年10月26日
 */
@Service("guestBookService")
public class GuestBookServiceImpl extends MockService<GuestBookEO> implements
        IGuestBookService {
    @DbInject("guestBook")
    private IGuestBookDao guestBookDao;
    @Autowired
    private IForwardRecordService recordService;

    @Autowired
    private IOrganService organService;
    @Autowired
    private IBaseContentService contentService;

    public Pagination getPage(GuestBookPageVO pageVO) {
        /*Long organId = LoginPersonUtil.getOrganId();
        pageVO.setReceiveId(organId);*/
        Pagination page = guestBookDao.getPage(pageVO);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<GuestBookEditVO> list = (List<GuestBookEditVO>) page.getData();
            try {
                list = GuestTimeUtil.setTimes(list);
                page.setData(list);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return page;

    }



    @Override
    public GuestBookEO saveGuestBook(GuestBookEO eo, Long siteId, Long columnId) {
        String dateStr = DateUtil.getYearMonthStr();
        Long count = countGuestbook2(siteId, columnId + "", null, null, null) + 1;
        String countStr = "";
        if (count < 10) {
            countStr += "00" + count;
        } else if (10 <= count && count < 100) {
            countStr += "0" + count;
        } else {
            countStr += count;
        }
        String randomCode = RandomCode.shortUrl(eo.getBaseContentId() + "");
        eo.setRandomCode(randomCode);
        eo.setDocNum(dateStr + countStr);
        saveEntity(eo);
        SysLog.log("添加留言 >> ID：" + eo.getId(),
                "GuestBookEO", CmsLogEO.Operation.Add.toString());

        return eo;
    }

    @Override
    public GuestBookEO reply(GuestBookEditVO vo) {
        if ("handled,replyed".contains(vo.getDealStatus())) {
            BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, vo.getBaseContentId());
            if (contentEO != null) {
                ScheduleJobUtil.addScheduleJob("留言定时任务", GuestBookTaskImpl.class.getName(), "0 0 0 * * ?", vo.getId() + "");
            }
        }

        GuestBookEO eo1 = getEntity(GuestBookEO.class, vo.getId());
        eo1.setReplyDate(vo.getReplyDate());
        eo1.setUserName(LoginPersonUtil.getPersonName());
        eo1.setPersonId(LoginPersonUtil.getPersonId());
        eo1.setDealStatus(vo.getDealStatus());
        eo1.setResponseContent(vo.getResponseContent());
        if (null != vo.getRecType() && vo.getRecType() == 1) {
            eo1.setReplyUserId(vo.getReplyUserId());
            eo1.setReplyUserName(vo.getReplyUserName());
            if(LoginPersonUtil.isSiteAdmin()|| LoginPersonUtil.isSuperAdmin()){
                eo1.setReplyUnitId(vo.getReplyUnitId());
                eo1.setReplyUnitName(vo.getReplyUnitName());
            }else{
                eo1.setReplyUnitName(LoginPersonUtil.getUnitName());
                eo1.setReplyUnitId(LoginPersonUtil.getUnitId());
            }

        }else {
            eo1.setReplyUnitId(LoginPersonUtil.getUnitId());
            eo1.setReplyUnitName(LoginPersonUtil.getUnitName());
        }
        eo1.setIsRead(0);
        updateEntity(eo1);
        SysLog.log("回复留言 >> ID：" + vo.getId(),
                "GuestBookEO", CmsLogEO.Operation.Update.toString());
        return eo1;
    }

    @Override
    public void remove(Long id) {
        guestBookDao.remove(id);
        SysLog.log("删除留言 >> ID：" + id,
                "GuestBookEO", CmsLogEO.Operation.Delete.toString());
    }


    //单个发布&批量发布
    @Override
    public void publish(Long[] ids, Integer status) {
        guestBookDao.publish(ids, status);
        SysLog.log("发布留言 >> ID：" + ArrayFormat.ArrayToString(ids),
                "GuestBookEO", CmsLogEO.Operation.Update.toString());
    }

    @Override
    public void forward(Long id, Long receiveId, String receiveUserCode, Integer recType,String localUnitId) {
        GuestBookEO eo = getEntity(GuestBookEO.class, id);
        if (eo == null) return;
        if (recType!=null&&recType == 0) {
            eo.setReceiveId(receiveId);
        }
        if (recType!=null&&recType == 1) {
            eo.setReceiveUserCode(receiveUserCode);
            eo.setReceiveId(receiveId);
        }
        eo.setRecType(recType);
        eo.setForwardDate(new Date());
        eo.setLocalUnitId(localUnitId);
        eo.setIsRead(0);
        updateEntity(eo);
        SysLog.log("转办留言>> ID：" + id, "GuestBookEO", CmsLogEO.Operation.Update.toString());
    }

    @Override
    public void batchDelete(Long[] ids) {
        guestBookDao.batchDelete(ids);
        SysLog.log("删除留言>> ID：" + ArrayFormat.ArrayToString(ids), "GuestBookEO", CmsLogEO.Operation.Delete.toString());
    }

    @Override
    public void modifySave(GuestBookEO eo) {

    }

    @Override
    public Long count(Integer i) {
        return guestBookDao.count(i);
    }

    @Override
    public Long countData(Long columnId) {
        return guestBookDao.checkDelete(columnId);
    }

	/*@Override
    public Pagination getNoAuditPage(GuestBookPageVO pageVO) {
		Pagination page = guestBookDao.getNoAuditPage(pageVO);
		return page;
	}*/

	/*@Override
    public Pagination getRecycleBinPage(GuestBookPageVO pageVO) {
		return guestBookDao.getRecycleBinPage(pageVO);
	}*/

    @Override
    public Object queryRemoved(Long id) {
        return guestBookDao.queryRemoved(id);
    }

    @Override
    public void recovery(Long id) {
        guestBookDao.recovery(id);
        SysLog.log("恢复留言>> ID：" + id, "GuestBookEO", CmsLogEO.Operation.Update.toString());
    }

    @Override
    public void completelyDelete(Long id) {
        guestBookDao.completelyDelete(id);
        SysLog.log("彻底删除留言>> ID：" + id, "GuestBookEO", CmsLogEO.Operation.Update.toString());
    }

    @Override
    public void batchRecovery(Long[] ids) {
        guestBookDao.batchRecovery(ids);
        SysLog.log("恢复留言>> ID：" + ArrayFormat.ArrayToString(ids), "GuestBookEO", CmsLogEO.Operation.Update.toString());
    }

    @Override
    public void batchCompletelyDelete(Long[] ids) {
        guestBookDao.batchCompletelyDelete(ids);
        SysLog.log("彻底删除留言>> ID：" + ArrayFormat.ArrayToString(ids), "GuestBookEO", CmsLogEO.Operation.Update.toString());
    }

    @Override
    public GuestBookEO noAuditGuestBook(Long id) {
        return guestBookDao.noAuditGuestBook(id);
    }

    @Override
    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list = guestBookDao.getStatisticsList(queryVO);

        if (list != null && list.size() >= queryVO.getNum()) {
            return list;
        } else {

            SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, queryVO.getSiteId());
            List<OrganEO> newList = new ArrayList<OrganEO>();
            if (siteEO != null && !StringUtils.isEmpty(siteEO.getUnitIds())) {
                Long[] arr = AppUtil.getLongs(siteEO.getUnitIds(), ",");
                if (arr != null && arr.length > 0) {
                    newList = organService.getOrgansByDn(arr[0], OrganEO.Type.Organ.toString());//单位
                }
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
            }
            if (newList == null || newList.size() == 0) {
                return list;
            }
            if (list == null) {
                list = new ArrayList<ContentChartVO>();
            }
            List<ContentChartVO> list_1 = new ArrayList<ContentChartVO>();
            list_1.addAll(list);
            if (list.size() == 0) {
                for (OrganEO eo : newList) {
                    if (list.size() < queryVO.getNum()) {
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getOrganId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    } else {
                        break;
                    }
                }
            } else {
                boolean flag = true;
                for (OrganEO eo : newList) {
                    flag = true;
                    if (list.size() >= queryVO.getNum()) {
                        break;
                    }
                    for (int i = 0; i < list_1.size(); i++) {
                        if (eo.getOrganId().equals(list_1.get(i).getOrganId())) {
                            list.get(i).setOrganName(eo.getName());
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getOrganId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    }
                }
            }
        }
        if (list != null && list.size() > 1) {
            for (int i = 0; i < list.size(); i++) {
//                if (i > 0 && i % 2 == 1) {
//                    list.get(i).setOrganName("\n" + list.get(i).getOrganName());
//                }
                Integer rate = list.get(i).getRate();
                list.get(i).setRate(rate == null ? 0 : (Math.round(rate)));
            }
        }
        return list;
    }

    @Override
    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list = guestBookDao.getTypeList(queryVO);
        if (list == null || list.size() == 0) {
            return list;
        } else {
            for (ContentChartVO vo : list) {
                DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getType());
                if (dictVO != null) {
                    vo.setType(dictVO.getKey());
                } else {
                    vo.setType(null);
                }
            }
        }
        return list;
    }

    @Override
    public Pagination getGuestPage(StatisticsQueryVO queryVO) {
        List<GuestListVO> list = getGuestList(queryVO);
        Pagination page = new Pagination();
        int index = Integer.parseInt(String.valueOf(queryVO.getPageIndex()));
        int size = queryVO.getPageSize();
        int startRow = index * size;
        int endRow = (index + 1) * size;
        if (list != null) {
            if (list.size() < endRow) {
                endRow = list.size();
            }
        }

        page.setData(list.subList(startRow, endRow));
        page.setTotal(Long.parseLong(String.valueOf(list.size())));
        page.setPageSize(size);
        page.setPageIndex(queryVO.getPageIndex());
        return page;
    }

    @Override
    public List<GuestListVO> getGuestList(StatisticsQueryVO queryVO) {
        List<GuestListVO> listData = guestBookDao.getGuestList(queryVO);
        List<GuestListVO> newList = new ArrayList<GuestListVO>();
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, queryVO.getSiteId());
        List<OrganEO> organList = new ArrayList<OrganEO>();
        if (siteEO != null && !StringUtils.isEmpty(siteEO.getUnitIds())) {
            Long[] arr = AppUtil.getLongs(siteEO.getUnitIds(), ",");
            if (arr != null && arr.length > 0) {
                organList = organService.getOrgansByDn(arr[0], OrganEO.Type.Organ.toString());//单位
            }
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
        }
        if (listData != null && listData.size() >= 0) {

            String organName = queryVO.getOrganName();
            for (int i = 0; i < organList.size(); i++) {
                GuestListVO vo = new GuestListVO();
                vo.setOrganName(organList.get(i).getName());
                vo.setOrganId(organList.get(i).getOrganId());
                for (GuestListVO guestListVO : listData) {
                    if (vo.getOrganId().equals(guestListVO.getOrganId())) {
                        vo.setDealCount(guestListVO.getDealCount() == null ? 0 : guestListVO.getDealCount());
                        vo.setRecCount(guestListVO.getRecCount() == null ? 0 : guestListVO.getRecCount());
                        vo.setUndoCount(vo.getRecCount()-vo.getDealCount());
                        vo.setRate(Math.round(100*(vo.getDealCount()/vo.getRecCount())));
                    }
                }
                newList.add(vo);
                if (!StringUtils.isEmpty(organName)) {
                    if (!organList.get(i).getName().contains(organName)) {
                        newList.remove(vo);
                    }
                }
            }
        }
        return newList;
    }

    @Override
    public GuestBookEO saveGusetBook(GuestBookEditVO vo) {
        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, vo);
        GuestBookEO guestBookEO = new GuestBookEO();
        AppUtil.copyProperties(guestBookEO, vo);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        guestBookEO.setPersonIp(IpUtil.getIpAddr(ContextHolderUtils.getRequest()));
        guestBookEO.setBaseContentId(id);
        if(!AppUtil.isEmpty(vo.getAddDate())){
            guestBookEO.setAddDate(vo.getAddDate());
        }else{
            guestBookEO.setAddDate(new Date());
        }
        if(!AppUtil.isEmpty(vo.getCreateDate())){
            guestBookEO.setCreateDate(vo.getCreateDate());
        }
//        if(!StringUtils.isEmpty(guestBookEO.getClassCode())){
//            ColumnTypeConfigVO configVO=ModelConfigUtil.getCongfigVO(contentEO.getColumnId(),contentEO.getSiteId());
//            if(configVO==null||StringUtils.isEmpty(configVO.getClassCodes())||(!configVO.getClassCodes().contains(guestBookEO.getClassCode()))){
//                throw new BaseRunTimeException(TipsMode.Message.toString(), "该留言类型未在内容模型中配置");
//            }
//        }

        if(StringUtils.isEmpty(vo.getDocNum())){
            String dateStr = DateUtil.getYearMonthStr();
            Long count = countGuestbook(contentEO.getSiteId(), contentEO.getColumnId() + "", null, null, null) + 1;
            String countStr = "";
            if (count < 10) {
                countStr += "00" + count;
            } else if (10 <= count && count < 100) {
                countStr += "0" + count;
            } else {
                countStr += count;
            }
            guestBookEO.setDocNum(dateStr + countStr);
        }else{
            guestBookEO.setDocNum(vo.getDocNum());
        }
        if(StringUtils.isEmpty(vo.getRandomCode())){
            String randomCode = RandomCode.shortUrl(id + "");
            guestBookEO.setRandomCode(randomCode);
        }else{
            guestBookEO.setRandomCode(vo.getRandomCode());
        }
        SysLog.log("添加留言 >> ID：" + vo.getId(),
                "GuestBookEO", CmsLogEO.Operation.Add.toString());
        saveEntity(guestBookEO);
        return guestBookEO;
    }

    @Override
    public Long getUnReplyCount(Long columnId, int day) {
        return guestBookDao.getUnReplyCount(columnId, day);
    }

    @Override
    public List<GuestBookEditVO> getUnReply(Long columnId, int day) {
        return guestBookDao.getUnReply(columnId,day);
    }

    @Override
    public List<GuestBookEditVO> getGuestBookBySiteId(Long siteId) {
        return guestBookDao.getGuestBookBySiteId(siteId);
    }

    @Override
    public List<GuestBookEO> getGuestBookList(Long[] ids) {
        return guestBookDao.getGuestBookList(ids);
    }

    @Override
    public GuestBookEditVO searchEO(String randomCode, String docNum,Long siteId) {

        GuestBookEditVO vo= guestBookDao.searchEO(randomCode,docNum,siteId);
        if(vo!=null){
            if (vo.getRecType() != null) {
                if (vo.getRecType().equals(0)) {
                    if (vo.getReceiveId() != null) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveId());
                        if (organEO != null) {
                            vo.setReceiveName(organEO.getName());
                        }
                    }
                } else {
                    if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                        if (dictVO != null) {
                            vo.setReceiveUserName(dictVO.getKey());
                        }
                    }
                }
            }
            if(!StringUtils.isEmpty(vo.getClassCode())){
                DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
                if (dictVO != null) {
                    vo.setClassName(dictVO.getKey());
                }
            }
            if (!StringUtils.isEmpty(vo.getCommentCode())) {
                DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", vo.getCommentCode());
                if (dictVO != null) {
                    vo.setCommentName(dictVO.getKey());
                }
            } else {
                DataDictVO dictVO = DataDictionaryUtil.getDefuatItem("guest_comment", vo.getSiteId());
                if (dictVO != null) {
                    vo.setCommentCode(dictVO.getCode());
                }
            }
            Pagination page=recordService.getRecord(0L,5,vo.getId());
            if(page!=null&&page.getData()!=null&&page.getData().size()>0){
                List<GuestBookForwardRecordEO> list=(List<GuestBookForwardRecordEO>)page.getData();
                vo.setRemarks(list.get(0).getRemarks());
            }
        }
        return vo;
    }

    @Override
    public GuestBookEditVO getVO(Long id) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, id);
        if (null == contentEO) {
            contentEO = contentService.getRemoved(id);
        }
        if (null == contentEO) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言为空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseContentId", id);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<GuestBookEO> list = getEntities(GuestBookEO.class, map);
        if (null == list || list.size() <= 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言为空");
        }
        GuestBookEO eo = list.get(0);
        GuestBookEditVO vo = new GuestBookEditVO();
        AppUtil.copyProperties(vo, eo);
        vo.setIsPublish(contentEO.getIsPublish());
        vo.setTitle(contentEO.getTitle());
        vo.setColumnId(contentEO.getColumnId());
        vo.setSiteId(contentEO.getSiteId());
        ColumnTypeConfigVO configVO = getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
        if (configVO == null) {
            return vo;
        }
        if (configVO.getRecType() != null) {
            if (configVO.getRecType()==0) {
                if (vo.getReceiveId() != null) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveId());
                    if (organEO != null) {
                        vo.setReceiveName(organEO.getName());
                    }
                }
            } else if (configVO.getRecType()==1)  {
                if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                    if (dictVO != null) {
                        vo.setReceiveUserName(dictVO.getKey());
                    }
                }
                if (vo.getReplyUnitId() != null) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReplyUnitId());
                    if (organEO != null) {
                        vo.setReplyUnitName(organEO.getName());
                    }
                }
                vo.setIsTurn(configVO.getTurn());
            }
            vo.setRecType(configVO.getRecType());

        }
        if(!StringUtils.isEmpty(vo.getClassCode())){
            DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
            if (dictVO != null) {
                vo.setClassName(dictVO.getKey());
            }
        }
        return vo;
    }

    @Override
    public Long countGuestbook(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) {
        return guestBookDao.countGuestbook(siteId, columnIds, startDate, isPublish, isResponse);
    }

    @Override
    public Long countGuestbook2(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) {
        return guestBookDao.countGuestbook2(siteId, columnIds, startDate, isPublish, isResponse);
    }

    @Override
    public List<GuestListVO> replyOKRank(Long siteId, String columnIds, Integer isPublish) {
        List<GuestListVO> list = guestBookDao.replyOKRank(siteId, columnIds, isPublish);
        List<GuestListVO> list_1 = new ArrayList<GuestListVO>();
//        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        List<OrganEO> newList = new ArrayList<OrganEO>();
//        if (siteEO != null && !StringUtils.isEmpty(siteEO.getUnitIds())) {
//            Long[] arr = AppUtil.getLongs(siteEO.getUnitIds(), ",");
//            if (arr != null && arr.length > 0) {
//                newList = organService.getOrgansByDn(arr[0], OrganEO.Type.Organ.toString());//单位
//            }
//        } else {
//            throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
//        }
        // ModelConfigUtil.getParam()
        if(!StringUtils.isEmpty(columnIds)){
            Long[] arr = AppUtil.getLongs(columnIds, ",");
            String idStr="";
            for(int i=0;i<arr.length;i++){
                ColumnTypeConfigVO configVO= ModelConfigUtil.getCongfigVO(arr[i],siteId);
                if(configVO!=null&&configVO.getRecType()==0&&!StringUtils.isEmpty(configVO.getRecUnitIds())){
                    Long[] unitIdArr=AppUtil.getLongs(configVO.getRecUnitIds(),",");
                    for(Long unitId:unitIdArr){
                        if(!idStr.contains(unitId+",")){
                            idStr+=unitId+",";
                        }
                    }
                }
            }
            if(!StringUtils.isEmpty(idStr)){
                Long[] idAr=AppUtil.getLongs(idStr,",");
                newList=organService.getEntities(OrganEO.class,idAr);
            }
        }
        if (newList == null || newList.size() == 0) {
            return new ArrayList<GuestListVO>();
        }
        for (OrganEO eo : newList) {
            GuestListVO chartVO = new GuestListVO();
            chartVO.setOrganId(eo.getOrganId());
            chartVO.setOrganName(eo.getName());
            list_1.add(chartVO);
        }
        if (list == null || list.size() == 0) {
            return list_1;
        }
        for (GuestListVO vo : list) {
            for (GuestListVO eo : list_1) {
                if (eo.getOrganId().equals(vo.getOrganId())) {
                    eo.setRecCount(vo.getRecCount()==null?0:vo.getRecCount());
                    eo.setDealCount(vo.getDealCount()==null?0:vo.getDealCount());
                    eo.setUndoCount(vo.getUndoCount()==null?0:vo.getUndoCount());
                    Long unSatCount=vo.getUnSatCount()==null?0:vo.getUnSatCount();
                    if(eo.getRecCount()==0L){//没有接收留言
                        eo.setRate(100);
                    }else{
                        if(eo.getDealCount()==0L){//接收但没有处理
                            eo.setRate(0);
                        }else{
                            if(unSatCount==0L){//接收处理了，没有不满意的
                                eo.setRate(100);
                            }else{
                                Long ra=100*(1-unSatCount/eo.getDealCount());
                                Integer rate=Integer.parseInt(String.valueOf(ra));
                                eo.setRate(rate==null?0: (Math.round(rate)));
                            }
                        }
                    }
                    break;
                }
            }
        }
        Collections.sort(list_1, new Comparator<GuestListVO>() {

            /*
             * int compare(GuestListVO o1, GuestListVO o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
            public int compare(GuestListVO o1, GuestListVO o2) {
                if (o1.getRate() < o2.getRate()) {
                    return 1;
                }
                if (o1.getRate() == o2.getRate()) {
                    return 0;
                }
                return -1;
            }
        });
        return list_1;
    }

    @Override
    public Pagination getMobilePage(GuestBookPageVO pageVO) {
        return guestBookDao.getMobilePage(pageVO);
    }

    @Override
    public GuestBookAnalyseVO getAnalys(GuestBookStatusVO vo) {
        GuestBookAnalyseVO gv = new GuestBookAnalyseVO();
        //统计总留言数和回复数，计算回复率
        GuestBookNumVO gb = guestBookDao.getAnalys(vo);
        if (vo.isAmount()) {
            gv.setAmount(gb.getAmount() == null ? 0 : gb.getAmount());
        }
        if (vo.isReplyNum()) {
            gv.setReplyNum(gb.getReplyNum() == null ? 0 : gb.getReplyNum());
        }
        if (vo.isReplyRate()) {
            gv.setReplyRate(gb.getReplyRate() + "%");
        }

        if(vo.isSelectNum()){
            Long selectNum=guestBookDao.getSelectNum(vo.getSiteId(),vo.getColumnIds());
            gv.setSelectNum(selectNum==null?0:selectNum);
        }

        //统计类型，建议、咨询、投诉、举报
        if (vo.isReportNum() || vo.isConsultNum() || vo.isSuggestNum() || vo.isComplainNum()) {
            List<GuestBookTypeVO> list = guestBookDao.getAnalysType(vo.getColumnIds(), vo.getSiteId(),null);
            for (GuestBookTypeVO gt : list) {
                if (gt.getClassCode().equals("do_report") && vo.isReportNum()) {
                    gv.setReportNum(gt.getCount() == null ? 0 : gt.getCount());
                } else if (gt.getClassCode().equals("do_consult") && vo.isConsultNum()) {
                    gv.setConsultNum(gt.getCount() == null ? 0 : gt.getCount());
                } else if (gt.getClassCode().equals("do_suggest") && vo.isSuggestNum()) {
                    gv.setSuggestNum(gt.getCount() == null ? 0 : gt.getCount());
                } else if (gt.getClassCode().equals("do_complain") && vo.isComplainNum()) {
                    gv.setComplainNum(gt.getCount() == null ? 0 : gt.getCount());
                }
            }
        }
        //统计类型，建议回复、咨询回复、投诉回复、举报回复
        if (vo.isReportReplyNum() || vo.isConsultReplyNum() || vo.isSuggestReplyNum() || vo.isComplainReplyNum()) {
            List<GuestBookTypeVO> list = guestBookDao.getAnalysType(vo.getColumnIds(), vo.getSiteId(),1);
            for (GuestBookTypeVO gt : list) {
                if (gt.getClassCode().equals("do_report") && vo.isReportReplyNum()) {
                    gv.setReportReplyNum(gt.getCount() == null ? 0 : gt.getCount());
                } else if (gt.getClassCode().equals("do_consult") && vo.isConsultReplyNum()) {
                    gv.setConsultReplyNum(gt.getCount() == null ? 0 : gt.getCount());
                } else if (gt.getClassCode().equals("do_suggest") && vo.isSuggestReplyNum()) {
                    gv.setSuggestReplyNum(gt.getCount() == null ? 0 : gt.getCount());
                } else if (gt.getClassCode().equals("do_complain") && vo.isComplainReplyNum()) {
                    gv.setComplainReplyNum(gt.getCount() == null ? 0 : gt.getCount());
                }
            }
        }

        //获得当天开始时间
        String todayDate = DateUtil.getStrToday();
        //今日留言
        if (vo.isTodayAmount()) {
            gv.setTodayAmount(guestBookDao.getDateTotalCount(vo.getColumnIds(), todayDate, vo.getSiteId(), null));
        }
        //今日回复
        if (vo.isTodayReplyNum()) {
            gv.setTodayReplyNum(guestBookDao.getDateReplyCount(vo.getColumnIds(), todayDate, vo.getSiteId(), null));
        }
        //今日在办
        if (vo.isTodayReplyNum()) {
            gv.setTodayHandlingNum(guestBookDao.getDateHandingCount(vo.getColumnIds(), todayDate, vo.getSiteId(), null));
        }

        //获得本年开始时间
        String yearDate = DateUtil.getStrYear();
        //本年度留言、受理
        if (vo.isYearAmount()) {
            gv.setYearAmount(guestBookDao.getDateTotalCount(vo.getColumnIds(), yearDate, vo.getSiteId(), null));
        }
        //本年度回复、办结
        if (vo.isYearReplyNum()) {
            gv.setYearReplyNum(guestBookDao.getDateReplyCount(vo.getColumnIds(), yearDate, vo.getSiteId(), null));
        }
        //本年度在办
        if (vo.isYearHandlingNum()) {
            gv.setYearHandlingNum(guestBookDao.getDateHandingCount(vo.getColumnIds(), yearDate, vo.getSiteId(), null));
        }

        //在办
        if (vo.isHandlingNum()) {
            gv.setHandlingNum(guestBookDao.getDateHandingCount(vo.getColumnIds(), null, vo.getSiteId(), null));
        }


        String currentQuarterStartTime = DateUtil.getCurrentQuarterStartTime();//获得本季度开始时间

        //本季度受理
        if(vo.isCurQuarterAmount()){
            gv.setCurQuarterAmount(guestBookDao.getDateTotalCount(vo.getColumnIds(),currentQuarterStartTime,vo.getSiteId(),null));
        }
        //本季度办结
        if(vo.isCurQuarterReplyNum()){
            gv.setCurQuarterReplyNum(guestBookDao.getDateReplyCount(vo.getColumnIds(),currentQuarterStartTime,vo.getSiteId(),null));
        }
        //本季度在办
        if(vo.isCurQuarterHandlingNum()){
            gv.setCurQuarterHandlingNum(guestBookDao.getDateHandingCount(vo.getColumnIds(),currentQuarterStartTime,vo.getSiteId(),null));
        }


        String firstMonth =DateUtil.getStrLastMonth();//得到上月开始时间
        String lastMonth = DateUtil.getStrMonth(); //得到上月结束时间
        //上月受理
        if (vo.isMonthAmount()) {
            gv.setMonthAmount(guestBookDao.getDateTotalCount(vo.getColumnIds(), firstMonth, vo.getSiteId(), lastMonth));
        }
        //上月办结
        if (vo.isMonthReplyNum()) {
            gv.setMonthReplyNum(guestBookDao.getDateReplyCount(vo.getColumnIds(), firstMonth, vo.getSiteId(), lastMonth));
        }
        //上月在办
        if (vo.isMonthHandlingNum()) {
            gv.setMonthHandlingNum(guestBookDao.getDateHandingCount(vo.getColumnIds(), firstMonth, vo.getSiteId(), lastMonth));
        }

        String startDay=DateUtil.getStrMonth();
        //本月受理
        if(vo.isCurMonthAmount()){
            gv.setCurMonthAmount(guestBookDao.getDateTotalCount(vo.getColumnIds(), startDay, vo.getSiteId(), null));
        }
        //本月办结
        if (vo.isCurMonthReplyNum()) {
            gv.setCurMonthReplyNum(guestBookDao.getDateReplyCount(vo.getColumnIds(), startDay, vo.getSiteId(), null));
        }
        //本月在办
        if (vo.isCurMonthHandlingNum()) {
            gv.setCurMonthHandlingNum(guestBookDao.getDateHandingCount(vo.getColumnIds(), startDay, vo.getSiteId(), null));
        }



        return gv;
    }

    @Override
    public List<GuestBookSearchVO> getAllGuestBook() {
        return guestBookDao.getAllGuestBook();
    }

    @Override
    public List<GuestListVO> replyOrderByOrgan(String columnIds, Long siteId,String organIds, int num) {
        StatisticsQueryVO queryVO=new StatisticsQueryVO();
        queryVO.setColumnIds(columnIds);
        queryVO.setSiteId(siteId);
        queryVO.setStartDate(DateUtil.getStrYear());
        queryVO.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
        List<GuestListVO> list=guestBookDao.replyOrderByOrgan(queryVO);
        List<GuestListVO> newList=new ArrayList<GuestListVO>();
        if(num<=0){
            return null;
        }
        if(list!=null&&list.size()>0){
            if(StringUtils.isEmpty(organIds)){//没有单位黑名单
                if(num>list.size()){
                    num=list.size();
                }
                newList=list.subList(0,num);
                for(GuestListVO vo:newList){
                    if(vo.getOrganId()!=null){
                        OrganEO organEO=CacheHandler.getEntity(OrganEO.class,vo.getOrganId());
                        if(organEO!=null){
                            vo.setOrganName(organEO.getName());
                        }
                    }
                }
            }else{//有单位黑名单
                List<GuestListVO> list_1=new ArrayList<GuestListVO>();
                list_1.addAll(list);
                for(GuestListVO vo:list){
                    if(organIds.contains(vo.getOrganId()+"")){
                        list_1.remove(vo);
                    }
                }
                if(num>list_1.size()){
                    num=list_1.size();
                }
                newList=list_1.subList(0,num);
                for(GuestListVO vo:newList){
                    if(vo.getOrganId()!=null){
                        OrganEO organEO=CacheHandler.getEntity(OrganEO.class,vo.getOrganId());
                        if(organEO!=null){
                            vo.setOrganName(organEO.getName());
                        }
                    }
                }
            }
        }
        return newList;
    }

    @Override
    public List<GuestBookEditVO> listGuestBook(Long siteId, String columnIds, Integer isReply, String createUserId,Integer num) {
        List<GuestBookEditVO> list=guestBookDao.listGuestBook(siteId, columnIds, isReply,createUserId,num);
        if(list!=null&&list.size()>0){
            for(GuestBookEditVO vo:list){
                if (vo.getReceiveId() != null) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveId());
                    if (organEO != null) {
                        vo.setReceiveName(organEO.getName());
                    }
                }
            }
        }
        return list;
    }

    @Override
    public Long getUnReadNum(Long siteId, Long  columnId, Long createUserId) {
        return guestBookDao.getUnReadNum(siteId,columnId,createUserId);
    }

    @Override
    public void setRead(Long contentId) {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("baseContentId",contentId);
        map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
        List<GuestBookEO> list=getEntities(GuestBookEO.class,map);
        if(list!=null&&list.size()>0){
            GuestBookEO eo=list.get(0);
            eo.setIsRead(1);
            updateEntity(eo);
        }
    }

    @Override
    public List<GuestBookEditVO> getFromSite(Long siteId, Long recUnitId,Integer num) {
        return guestBookDao.getFromSite(siteId,recUnitId,num);
    }

    @Override
    public GuestBookEO getGuestBookByContentId(Long contentId) {
        return guestBookDao.getGuestBookByContentId(contentId);
    }

    @Override
    public Pagination getMobilePage2(ContentPageVO contentPageVO) {
        return guestBookDao.getMobilePage2(contentPageVO);
    }

}
