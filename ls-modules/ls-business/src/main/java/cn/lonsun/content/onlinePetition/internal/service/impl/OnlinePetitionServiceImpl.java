package cn.lonsun.content.onlinePetition.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.onlinePetition.internal.dao.IOnlinePetitionDao;
import cn.lonsun.content.onlinePetition.internal.entity.OnlinePetitionEO;
import cn.lonsun.content.onlinePetition.internal.entity.RunRecordEO;
import cn.lonsun.content.onlinePetition.internal.service.IOnlinePetitionService;
import cn.lonsun.content.onlinePetition.internal.service.IRunRecordService;
import cn.lonsun.content.onlinePetition.vo.OnlinePetitionVO;
import cn.lonsun.content.onlinePetition.vo.PetitionQueryVO;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lonsun.util.DataDictionaryUtil.getItem;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */
@Service("onlinePetitionService")
public class OnlinePetitionServiceImpl extends MockService<OnlinePetitionEO> implements IOnlinePetitionService {

    @Autowired
    private IOnlinePetitionDao petitionDao;

    @Autowired
    private IBaseContentService baseService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Autowired
    private IRunRecordService recordService;

    @Autowired
    private IOrganService organService;


    @Override
    public Pagination getPage(PetitionQueryVO pageVO) {
        return petitionDao.getPage(pageVO);
    }

    @Override
    public void saveVO(OnlinePetitionVO vo) {
        BaseContentEO baseEO = new BaseContentEO();
        OnlinePetitionEO eo = new OnlinePetitionEO();
        if(vo.getId()==null){
            baseEO.setTitle(vo.getTitle());
            baseEO.setAuthor(vo.getAuthor());
            baseEO.setColumnId(vo.getColumnId());
            baseEO.setSiteId(vo.getSiteId());
            baseEO.setTypeCode(vo.getTypeCode());
            baseEO.setPublishDate(vo.getPublishDate());
            baseService.saveEntity(baseEO);
            CacheHandler.saveOrUpdate(BaseContentEO.class,baseEO);
            SysLog.log("添加网上信访 >> ID：" + baseEO.getId() + "，标题：" + baseEO.getTitle(),
                    "BaseContentEO", CmsLogEO.Operation.Add.toString());

            eo.setAttachId(vo.getAttachId());
            eo.setAttachName(vo.getAttachName());
            eo.setRecUnitId(vo.getRecUnitId());
            eo.setRecUnitName(vo.getRecUnitName());
            eo.setAddress(vo.getAddress());
            eo.setOccupation(vo.getOccupation());
            eo.setPhoneNum(vo.getPhoneNum());
            eo.setColumnId(vo.getColumnId());
            eo.setSiteId(vo.getSiteId());
            eo.setContent(vo.getContent());
            eo.setCategoryCode(vo.getCategoryCode());
            eo.setPurposeCode(vo.getPurposeCode());
            eo.setContentId(baseEO.getId());
            String randomCode= RandomCode.shortUrl(baseEO.getId()+"");
            eo.setCheckCode(randomCode);
            eo.setIp(RequestUtil.getIpAddr(LoginPersonUtil.getRequest()));
            saveEntity(eo);
        }else{
            baseEO=baseService.getEntity(BaseContentEO.class, vo.getId());
            baseEO.setTitle(vo.getTitle());
            baseEO.setAuthor(vo.getAuthor());
            baseEO.setPublishDate(vo.getPublishDate());
            baseService.updateEntity(baseEO);
            CacheHandler.saveOrUpdate(BaseContentEO.class,baseEO);
            SysLog.log("更新网上信访 >> ID：" + baseEO.getId() + "，标题：" + baseEO.getTitle(),
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());

            eo=getEntity(OnlinePetitionEO.class,vo.getPetitionId());
            if(!vo.getAttachId().equals(eo.getAttachId())){
                FileUploadUtil.deleteFileCenterEO(eo.getAttachId());
            }
            eo.setAttachId(vo.getAttachId());
            eo.setAttachName(vo.getAttachName());
            eo.setRecUnitId(vo.getRecUnitId());
            eo.setRecUnitName(vo.getRecUnitName());
            eo.setAddress(vo.getAddress());
            eo.setOccupation(vo.getOccupation());
            eo.setPhoneNum(vo.getPhoneNum());
            eo.setContent(vo.getContent());
            eo.setCategoryCode(vo.getCategoryCode());
            eo.setPurposeCode(vo.getPurposeCode());
            eo.setIp(RequestUtil.getIpAddr(LoginPersonUtil.getRequest()));
            updateEntity(eo);
        }
        //编辑器上传
        if (BaseContentEO.TypeCode.onlinePetition.toString().equals(vo.getTypeCode())) {
            ContentMongoEO _eo = new ContentMongoEO();
            _eo.setId(baseEO.getId());
            _eo.setContent(vo.getContent());
            contentMongoService.save(_eo);
        }
        if (!AppUtil.isEmpty(vo.getAttachId())) {
            FileUploadUtil.setStatus(eo.getAttachId(), 1, baseEO.getId(), vo.getColumnId(), vo.getSiteId());
        }
    }

    @Override
    public OnlinePetitionVO getVO(Long id) {
        OnlinePetitionVO vo=petitionDao.getVO(id);
        if(vo!=null){
            if(!StringUtils.isEmpty(vo.getCategoryCode())){
                DataDictVO dictVO=DataDictionaryUtil.getItem("petition_category",vo.getCategoryCode());
                if(dictVO!=null){
                    vo.setCategoryCode(dictVO.getKey());
                }
            }
            if(!StringUtils.isEmpty(vo.getPurposeCode())){
                DataDictVO dictVO=DataDictionaryUtil.getItem("petition_purpose",vo.getPurposeCode());
                if(dictVO!=null){
                    vo.setPurposeCode(dictVO.getKey());
                }
            }
            if (vo.getRecUnitId() != null) {
                OrganEO eo = CacheHandler.getEntity(OrganEO.class, vo.getRecUnitId());
                if (eo != null) {
                    vo.setRecUnitName(eo.getName());
                }
            }
        }
        return vo;
    }

    @Override
    public void deleteVOs(String ids) {
        Long[] idArr = AppUtil.getLongs(ids, ",");
        if (idArr != null && idArr.length > 0) {
            String[] arr=new String[idArr.length];
            int j=0;
            for (int i=0;i<idArr.length;i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("contentId", idArr[i]);
                List<OnlinePetitionEO> list1 = getEntities(OnlinePetitionEO.class, map);

                //删除网上信访辅表
                if (list1 != null && list1.size() > 0) {
                    OnlinePetitionEO eo = list1.get(0);
                    if(!AppUtil.isEmpty(eo.getAttachId())){
                        arr[j++]=eo.getAttachId();
                    }
                }
                BaseContentEO contentEO=CacheHandler.getEntity(BaseContentEO.class,idArr[i]);
                if(contentEO!=null){
                    CacheHandler.delete(BaseContentEO.class,contentEO);
                }
            }
            //删除主表
            baseService.delete(BaseContentEO.class,idArr);
            SysLog.log("删除网上信访 >> ID：" + ids,
                    "BaseContentEO", CmsLogEO.Operation.Delete.toString());
            FileUploadUtil.deleteFileCenterEO(arr);
        }
    }

    @Override
    public void transfer(Long petitionId,Long recUnitId,String recUnitName,Integer recType,String remark) {
        OnlinePetitionEO eo=getEntity(OnlinePetitionEO.class, petitionId);
        if(eo!=null){
            if(eo.getRecType()==1){
                eo.setRecUserId(recUnitId);
                eo.setRecUserName(recUnitName);
            }else{
                eo.setRecUnitId(recUnitId);
                eo.setRecUnitName(recUnitName);
            }
            eo.setRecType(recType);
            updateEntity(eo);
            SysLog.log("转办网上信访 >> ID：" + eo.getId() ,
                    "OnlinePetitionEO", CmsLogEO.Operation.Update.toString());
        }
        RunRecordEO recordEO=new RunRecordEO();
        recordEO.setPetitionId(petitionId);
        recordEO.setTransUserName(LoginPersonUtil.getUserName());
        recordEO.setTransIp(RequestUtil.getIpAddr(LoginPersonUtil.getRequest()));
        recordEO.setTransToId(recUnitId);
        recordEO.setTransToName(recUnitName);
        recordEO.setRemark(remark);
        recordService.saveEntity(recordEO);
        SysLog.log("添加网上信访转办记录 >> ID：" + eo.getId() ,
                "RunRecordEO", CmsLogEO.Operation.Add.toString());
    }

    @Override
    public Long countData(Long columnId) {
        return petitionDao.countData(columnId);
    }

    @Override
    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list=petitionDao.getStatisticsList(queryVO);
        if (list != null && list.size() >= 10) {
            return list;
        } else {
            SiteMgrEO siteEO=CacheHandler.getEntity(SiteMgrEO.class,queryVO.getSiteId());
            List<OrganEO> newList=new ArrayList<OrganEO>();
            if(siteEO!=null&&!StringUtils.isEmpty(siteEO.getUnitIds())){
                Long[] arr= AppUtil.getLongs(siteEO.getUnitIds(), ",");
                if(arr!=null&&arr.length>0){
                    newList=organService.getOrgansByDn(arr[0],OrganEO.Type.Organ.toString());//单位
                }
            }else{
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
                    if (list.size() < 10) {
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getOrganId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    } else {
                        break;
                    }
                }
            } else {
                boolean flag=true;
                for (OrganEO eo : newList) {
                    flag=true;
                    if (list.size() >= 10) {
                        break;
                    }
                    for (int i=0;i<list_1.size();i++) {
                        if (eo.getOrganId().equals(list_1.get(i).getOrganId())) {
                            list.get(i).setOrganName(eo.getName());
                            flag=false;
                            break;
                        }
                    }
                    if(flag){
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getOrganId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    }
                }
            }
        }
        for(int i=0;i<list.size();i++){
            if(i>0&&i%2==1){
                list.get(i).setOrganName("\n"+list.get(i).getOrganName());
            }
            Integer rate=list.get(i).getRate();
            list.get(i).setRate(rate==null?0:(Math.round(rate)));
        }
        return list;
    }

    @Override
    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list= petitionDao.getTypeList(queryVO);
        if(list==null||list.size()==0){
            return list;
        }else{
            for(ContentChartVO vo:list){
                DataDictVO dictVO= getItem("petition_purpose", vo.getType());
                if(dictVO!=null){
                    vo.setType(dictVO.getKey());
                }else{
                    vo.setType(null);
                }
            }
        }
        return list;
    }

    @Override
    public Pagination getPetitionPage(StatisticsQueryVO queryVO) {
        List<GuestListVO> list=getPetitionList(queryVO);
        Pagination page=new Pagination();
        int index = Integer.parseInt(String.valueOf(queryVO.getPageIndex()));
        int size=queryVO.getPageSize();
        int startRow=index*size;
        int endRow=(index+1)*size;
        if(list!=null){
            if(list.size()<endRow){
                endRow=list.size();
            }
        }

        page.setData(list.subList(startRow,endRow));
        page.setTotal( Long.parseLong(String.valueOf(list.size())));
        page.setPageSize(size);
        page.setPageIndex(queryVO.getPageIndex());
        return page;
    }
    @Override
    public List<GuestListVO> getPetitionList(StatisticsQueryVO queryVO) {
        List<GuestListVO> listData=petitionDao.getPetitionList(queryVO);
        List<GuestListVO> newList=new ArrayList<GuestListVO>();
        SiteMgrEO siteEO=CacheHandler.getEntity(SiteMgrEO.class,queryVO.getSiteId());
        List<OrganEO> organList=new ArrayList<OrganEO>();
        if(siteEO!=null&&!StringUtils.isEmpty(siteEO.getUnitIds())){
            Long[] arr= AppUtil.getLongs(siteEO.getUnitIds(), ",");
            if(arr!=null&&arr.length>0){
                organList=organService.getOrgansByDn(arr[0],OrganEO.Type.Organ.toString());//单位
            }
        }else{
            throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
        }
        if(listData!=null&&listData.size()>=0){

            String organName=queryVO.getOrganName();
            for(int i=0;i<organList.size();i++){
                GuestListVO vo=new GuestListVO();
                vo.setOrganName(organList.get(i).getName());
                vo.setOrganId(organList.get(i).getOrganId());
                for(GuestListVO guestListVO:listData){
                    if(vo.getOrganId().equals(guestListVO.getOrganId())) {
                        vo.setDealCount(guestListVO.getDealCount()==null?0:guestListVO.getDealCount());
                        vo.setRecCount(guestListVO.getRecCount()==null?0:guestListVO.getRecCount());
                        vo.setUndoCount(vo.getRecCount()-vo.getDealCount());
                        vo.setRate(Math.round(100*(vo.getDealCount()/vo.getRecCount())));
                    }
                }
                newList.add(vo);
                if(!StringUtils.isEmpty(organName)){
                    if(!organList.get(i).getName().contains(organName)){
                        newList.remove(vo);
                    }
                }
            }
        }
        return newList;
    }

    @Override
    public OnlinePetitionVO getByCheckCode(String checkCode,Long siteId) {
//        OnlinePetitionVO vo=new OnlinePetitionVO();
//        Map<String,Object> map=new HashMap<String, Object>();
//        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
//        map.put("checkCode",checkCode);
//        List<OnlinePetitionEO>list=getEntities(OnlinePetitionEO.class,map);
//        if(list!=null&&list.size()>0){
//            OnlinePetitionEO petitionEO=list.get(0);
//            if(petitionEO!=null){
//                BaseContentEO contentEO=CacheHandler.getEntity(BaseContentEO.class,petitionEO.getContentId());
//                if(contentEO!=null){
//                    AppUtil.copyProperties(vo,petitionEO);
//                    vo.setId(contentEO.getId());
//                    vo.setTitle(contentEO.getTitle());
//                    vo.setIsPublish(contentEO.getIsPublish());
//                    vo.setPublishDate(contentEO.getPublishDate());
//                    vo.setAuthor(contentEO.getAuthor());
//                    vo.setTypeCode(contentEO.getTypeCode());
//                    vo.setSiteId(contentEO.getSiteId());
//                }
//            }
//        }
        OnlinePetitionVO vo=petitionDao.getByCheckCode(checkCode,siteId);
        if(vo!=null){
            if (vo.getRecType().equals(0)) {
                if (vo.getRecUnitId() != null) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getRecUnitId());
                    if (organEO != null) {
                        vo.setRecUnitName(organEO.getName());
                    }
                }
            }
        }
        return vo;
    }
}
