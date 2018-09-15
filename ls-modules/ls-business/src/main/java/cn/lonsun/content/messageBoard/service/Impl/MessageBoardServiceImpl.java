package cn.lonsun.content.messageBoard.service.Impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IKnowledgeBaseService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.*;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardReplyDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardApplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.dao.IOrganDao;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.impl.OrganServiceImpl;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.MessageBoardListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.system.sitechart.vo.KVL;
import cn.lonsun.system.sitechart.vo.KVP;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

import static cn.lonsun.util.ModelConfigUtil.getCongfigVO;

/**
 * @ClassName: MessageBoardServiceImpl
 * @Description: 留言管理业务逻辑
 */
@Service("messageBoardService")
public class MessageBoardServiceImpl extends MockService<MessageBoardEO> implements IMessageBoardService {
    @Autowired
    private IMessageBoardDao messageBoardDao;

    @Autowired
    private IMessageBoardReplyDao messageBoardReplyDao;

    @Autowired
    private IMessageBoardReplyService replyService;

    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private IMessageBoardForwardService forwardService;

    @Autowired
    private IMessageBoardApplyService applyService;

    @Autowired
    private IKnowledgeBaseService knowledgeBaseService;

    @Autowired
    private IOrganService organService;
    @Autowired
    private OrganServiceImpl organServiceImpl;
    @Autowired
    private IOrganDao organDao;
    @Autowired
    private IContentModelService contModelService;

    @Value("${fileServerPath}")
    private String url;

    @Value("${fileServerNamePath}")
    private String nameUrl;

    public Pagination getPage(MessageBoardPageVO pageVO) {
        Pagination page = null;
        if (!RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId()) && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
            page = messageBoardDao.getUnitPage(pageVO);
        } else {
            page = messageBoardDao.getPage(pageVO);
        }

        if (page != null && page.getData() != null && page.getData().size() > 0) {

            List<MessageBoardEditVO> editVOList = (List<MessageBoardEditVO>) page.getData();
            MessageBoardEditVO vo = null;
            if (editVOList != null && editVOList.size() > 0) {
                for (int i = 0; i < editVOList.size(); i++) {
                    vo = editVOList.get(i);
                    if (!RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId()) && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
                        vo.setIsSuper(0);//判断是否是普通用户或超管
                    } else {
                        vo.setIsSuper(1);
                    }

                    Long messageBoardId = vo.getId();
                    List<MessageBoardForwardVO> forwardVOList = forwardService.getAllForwardByMessageBoardId(messageBoardId);
                    String recUnitNames="";
                    if (forwardVOList != null && forwardVOList.size() > 0) {
                        for(MessageBoardForwardVO forwardVO:forwardVOList){
                            if (!StringUtils.isEmpty(forwardVO.getReceiveUnitName())) {
                                recUnitNames+=forwardVO.getReceiveUnitName()+",";
                            } else {
                                OrganEO organEO = organService.getEntity(OrganEO.class, forwardVO.getReceiveOrganId());
                                if (organEO != null) {
                                    recUnitNames+=organEO.getName()+",";
                                }
                            }
                        }
                    }
                    if(!StringUtils.isEmpty(recUnitNames)){
                        recUnitNames=recUnitNames.substring(0,recUnitNames.length()-1);
                    }
                    vo.setReceiveUnitName(recUnitNames);
                    List<MessageBoardReplyVO> replyVOList = messageBoardReplyDao.getAllReply(messageBoardId);
                    vo.setReplyVOList(replyVOList);
                    vo.setUri(nameUrl);
                    //设置栏目名称
                    vo.setColumnName(ColumnUtil.getColumnName(vo.getColumnId(), vo.getSiteId()));

                    //设置问题分类字段
                    if (!AppUtil.isEmpty(vo.getKnowledgeBaseId())) {
                        KnowledgeBaseVO knowledgeBaseVO = null;
                        ContentPageVO query = new ContentPageVO();
                        query.setIdArray(new Long[]{vo.getKnowledgeBaseId()});
                        query.setSiteId(LoginPersonUtil.getSiteId());
                        query.setColumnId(6331498l);
                        query.setTypeCode(BaseContentEO.TypeCode.knowledgeBase.toString());
                        Pagination page1 = knowledgeBaseService.getPage(query);
                        List<KnowledgeBaseVO> list = (List<KnowledgeBaseVO>) page1.getData();
                        if (list.size() > 0) {
                            knowledgeBaseVO = list.get(0);
                            vo.setCategoryName(knowledgeBaseVO.getCategoryName());
                        }
                    }
                    if (!StringUtils.isEmpty(editVOList.get(i).getCommentCode())) {
                        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", editVOList.get(i).getCommentCode());
                        if (dictVO != null) {
                            editVOList.get(i).setCommentName(dictVO.getKey());
                        }
                    }
                }
            }
            try {
                editVOList = MessageBoardTimeUtil.setTimes(editVOList);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            page.setData(editVOList);
        }


        return page;

    }

    @Override
    public MessageBoardEO saveMessageBoard(MessageBoardEO eo, Long siteId, Long columnId) {
        String dateStr = DateUtil.getYearMonthDayStr();
        Long count = countMessageBoard(siteId, columnId + "", DateUtil.getToday(), null, null) + 1;
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
                "MessageBoardEO", CmsLogEO.Operation.Add.toString());

        return eo;
    }

    @Override
    public MessageBoardEO exportOldMessageBoard(MessageBoardEditVO editVO , MessageBoardReplyVO replyVO) {

        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, editVO);
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, editVO);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setBaseContentId(id);
        if (!AppUtil.isEmpty(editVO.getAddDate())) {
            messageBoardEO.setAddDate(editVO.getAddDate());
        } else {
            messageBoardEO.setAddDate(new Date());
        }
        if (!AppUtil.isEmpty(editVO.getCreateDate())) {
            messageBoardEO.setCreateDate(editVO.getCreateDate());
        }
        if(!StringUtils.isEmpty(editVO.getReceiveUnitName())||editVO.getReceiveUnitId()!=null){
            messageBoardEO.setForwardCount(1);
        }
        if (StringUtils.isEmpty(editVO.getDocNum())) {
            String dateStr = DateUtil.getYearMonthStr();
            Long count = countMessageBoard(contentEO.getSiteId(), contentEO.getColumnId() + "", DateUtil.getToday(), null, null) + 1;
            String countStr = "";
            if (count < 10) {
                countStr += "00" + count;
            } else if (10 <= count && count < 100) {
                countStr += "0" + count;
            } else {
                countStr += count;
            }
            messageBoardEO.setDocNum(dateStr + countStr);
        } else {
            messageBoardEO.setDocNum(editVO.getDocNum());
        }
        if (StringUtils.isEmpty(editVO.getRandomCode())) {
            String randomCode = RandomCode.shortUrl(id + "");
            messageBoardEO.setRandomCode(randomCode);
        } else {
            messageBoardEO.setRandomCode(editVO.getRandomCode());
        }
        saveEntity(messageBoardEO);
        MessageBoardForwardEO forwardEO=new MessageBoardForwardEO();
        forwardEO.setMessageBoardId(messageBoardEO.getId());
        forwardEO.setReceiveOrganId(editVO.getReceiveUnitId());
        forwardEO.setReceiveUnitName(editVO.getReceiveUnitName());
        forwardEO.setReceiveUserCode(editVO.getReceiveUserCode());
        forwardEO.setReceiveUserName(editVO.getReceiveUserName());
        forwardEO.setOperationStatus(AMockEntity.RecordStatus.Normal.toString());
        forwardEO.setDealStatus(messageBoardEO.getDealStatus());
        forwardEO.setReplyDate(messageBoardEO.getReplyDate());
        forwardEO.setCreateDate(messageBoardEO.getCreateDate());
        forwardService.saveEntity(forwardEO);
        MessageBoardReplyEO replyEO = new MessageBoardReplyEO();
        AppUtil.copyProperties(replyEO, replyVO);
        if (!AppUtil.isEmpty(replyEO.getReplyContent())) {
            replyEO.setForwardId(forwardEO.getId());
            replyEO.setMessageBoardId(messageBoardEO.getId());
            replyService.saveEntity(replyEO);
        }

        return messageBoardEO;
    }


    @Override
    public MessageBoardEO exportAQOldMessageBoard(MessageBoardEditVO editVO, List<MessageBoardForwardVO> forwardListVO, List<MessageBoardReplyVO> replyListVO){

        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, editVO);
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, editVO);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setBaseContentId(id);
        if (!AppUtil.isEmpty(editVO.getAddDate())) {
            messageBoardEO.setAddDate(editVO.getAddDate());
        } else {
            messageBoardEO.setAddDate(new Date());
        }
        if (!AppUtil.isEmpty(editVO.getCreateDate())) {
            messageBoardEO.setCreateDate(editVO.getCreateDate());
        }

        if (StringUtils.isEmpty(editVO.getDocNum())) {
            String dateStr = DateUtil.getYearMonthDayStr();
            Long count = countMessageBoard(contentEO.getSiteId(), contentEO.getColumnId() + "", DateUtil.getToday(), null, null) + 1;
            String countStr = "";
            if (count < 10) {
                countStr += "00" + count;
            } else if (10 <= count && count < 100) {
                countStr += "0" + count;
            } else {
                countStr += count;
            }
            messageBoardEO.setDocNum(dateStr + countStr);
        } else {
            messageBoardEO.setDocNum(editVO.getDocNum());
        }
        if (StringUtils.isEmpty(editVO.getRandomCode())) {
            String randomCode = RandomCode.shortUrl(id + "");
            messageBoardEO.setRandomCode(randomCode);
        } else {
            messageBoardEO.setRandomCode(editVO.getRandomCode());
        }

        saveEntity(messageBoardEO);
        if(forwardListVO!=null&&forwardListVO.size()>0) {
            for (int i = 0; i < forwardListVO.size(); i++) {
                MessageBoardForwardEO forwardEO = new MessageBoardForwardEO();
                AppUtil.copyProperties(forwardEO, forwardListVO.get(i));
                forwardEO.setMessageBoardId(messageBoardEO.getId());
                forwardService.saveEntity(forwardEO);
            }
        }
        if(replyListVO!=null&&replyListVO.size()>0) {
            for (int j = 0; j < replyListVO.size(); j++) {
                MessageBoardReplyEO replyEO = new MessageBoardReplyEO();
                AppUtil.copyProperties(replyEO, replyListVO.get(j));
                if (replyEO.getReplyContent() != null) {
                    replyEO.setMessageBoardId(messageBoardEO.getId());
                    replyService.saveEntity(replyEO);
                }
            }
        }

        return messageBoardEO;
    }


    @Override
    public MessageBoardEO exportExcl(MessageBoardEditVO editVO, MessageBoardReplyVO replyVO) {

        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, editVO);
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, editVO);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setPersonIp(IpUtil.getIpAddr(ContextHolderUtils.getRequest()));
        messageBoardEO.setBaseContentId(id);
        if (!AppUtil.isEmpty(editVO.getAddDate())) {
            messageBoardEO.setAddDate(editVO.getAddDate());
        } else {
            messageBoardEO.setAddDate(new Date());
        }
        if (!AppUtil.isEmpty(editVO.getCreateDate())) {
            messageBoardEO.setCreateDate(editVO.getCreateDate());
        }

        if (StringUtils.isEmpty(editVO.getDocNum())) {
            String dateStr = DateUtil.getYearMonthDayStr();
            Long count = countMessageBoard(contentEO.getSiteId(), contentEO.getColumnId() + "", DateUtil.getToday(), null, null) + 1;
            String countStr = "";
            if (count < 10) {
                countStr += "00" + count;
            } else if (10 <= count && count < 100) {
                countStr += "0" + count;
            } else {
                countStr += count;
            }
            messageBoardEO.setDocNum(dateStr + countStr);
        } else {
            messageBoardEO.setDocNum(editVO.getDocNum());
        }
        if (StringUtils.isEmpty(editVO.getRandomCode())) {
            String randomCode = RandomCode.shortUrl(id + "");
            messageBoardEO.setRandomCode(randomCode);
        } else {
            messageBoardEO.setRandomCode(editVO.getRandomCode());
        }

        saveEntity(messageBoardEO);

        MessageBoardReplyEO replyEO = new MessageBoardReplyEO();
        AppUtil.copyProperties(replyEO, replyVO);
        replyEO.setMessageBoardId(messageBoardEO.getId());
        replyService.saveEntity(replyEO);

        return messageBoardEO;
    }

    @Override
    public Long countMessageBoard(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) {
        return messageBoardDao.countMessageBoard(siteId, columnIds, startDate, isPublish, isResponse);
    }

    @Override
    public List<KVP> getSatisfactoryTypeCount(MessageBoardPageVO pageVO) {
        return messageBoardDao.getSatisfactoryTypeCount(pageVO);
    }

    @Override
    public List<KVL> getSatisfactoryUnitCount(MessageBoardPageVO pageVO) {
        return messageBoardDao.getSatisfactoryUnitCount(pageVO);
    }

    @Override
    public MessageBoardEditVO getVO(Long id) {
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
        List<MessageBoardEO> list = getEntities(MessageBoardEO.class, map);
        if (null == list || list.size() <= 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言为空");
        }
        MessageBoardEO eo = list.get(0);

        //获取答复内容
        List<MessageBoardReplyVO> replyVOList = messageBoardReplyDao.getAllReply(eo.getId());
        MessageBoardEditVO vo = new MessageBoardEditVO();
        vo.setReplyVOList(replyVOList);
        AppUtil.copyProperties(vo, eo);
        vo.setIsPublish(contentEO.getIsPublish());
        vo.setTitle(contentEO.getTitle());
        vo.setColumnId(contentEO.getColumnId());
        vo.setSiteId(contentEO.getSiteId());
        ColumnTypeConfigVO configVO = getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
        if (configVO == null) {
            return vo;
        }
        vo.setRecType(configVO.getRecType());
        if (configVO.getRecType() != null) {
            if (configVO.getRecType() == 0) {
                if (vo.getReceiveUnitId() != null) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveUnitId());
                    if (organEO != null) {
                        vo.setReceiveUnitName(organEO.getName());
                    }
                }
            } else if (configVO.getRecType() == 1) {
                if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                    if (dictVO != null) {
                        vo.setReceiveUserName(dictVO.getKey());
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(vo.getClassCode())) {
            DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
            if (dictVO != null) {
                vo.setClassName(dictVO.getKey());
            }
        }
        return vo;
    }

    @Override
    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list = messageBoardDao.getStatisticsList(queryVO);

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
                if (i > 0 && i % 2 == 1) {
                    list.get(i).setOrganName("\n" + list.get(i).getOrganName());
                }
                Integer rate = list.get(i).getRate();
                list.get(i).setRate(rate == null ? 0 : (Math.round(rate)));
            }
        }
        return list;
    }

    @Override
    public Pagination getUnitMessageBoardPage(StatisticsQueryVO queryVO) {
        List<MessageBoardListVO> list = getMessageBoardList(queryVO);
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
    public List<ContentChartVO> getSatisfactoryList(ContentChartQueryVO queryVO) {

        List<ContentChartVO> list = messageBoardDao.getSatisfactoryList(queryVO);

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
                if (i > 0 && i % 2 == 1) {
                    list.get(i).setOrganName("\n" + list.get(i).getOrganName());
                }
                Integer rate = list.get(i).getRate();
                list.get(i).setRate(rate == null ? 0 : (Math.round(rate)));
            }
        }
        return list;
    }

    @Override
    public List<MessageBoardListVO> getMessageBoardList(StatisticsQueryVO queryVO) {


        List<MessageBoardListVO> list = messageBoardDao.getMessageBoardUnitList(queryVO);
        List<MessageBoardListVO> newList = new ArrayList<MessageBoardListVO>();
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
        if (list != null && list.size() > 0) {

            String organName = queryVO.getOrganName();
            for (int i = 0; i < organList.size(); i++) {
                MessageBoardListVO vo = new MessageBoardListVO();
                vo.setOrganName(organList.get(i).getName());
                vo.setOrganId(organList.get(i).getOrganId());
                for (MessageBoardListVO messageBoardListVO : list) {
                    if (vo.getOrganId().equals(messageBoardListVO.getOrganId())) {
                        vo.setReceiveCount(messageBoardListVO.getReceiveCount() == null ? 0 : messageBoardListVO.getReceiveCount());
                        vo.setReplyCount(messageBoardListVO.getReplyCount() == null ? 0 : messageBoardListVO.getReplyCount());
                        vo.setNoReplyCount(vo.getReceiveCount() - vo.getReplyCount());
//                        vo.setNoReplyCount(messageBoardListVO.getNoReplyCount() == null ? 0 : messageBoardListVO.getNoReplyCount());
                        vo.setReplyRate(messageBoardListVO.getReplyRate() == null ? 0 : (Math.round(messageBoardListVO.getReplyRate())));
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
    public Long getCount(String type, Long columnId) {
        Long count = messageBoardDao.getCount(type, columnId);
        return count;
    }

    @Override
    public GuestBookAnalyseVO getAnalys(GuestBookStatusVO vo) {
        GuestBookAnalyseVO gv = new GuestBookAnalyseVO();
        //统计总留言数和回复数，计算回复率
        GuestBookNumVO gb = messageBoardDao.getAnalys(vo.getColumnIds(), vo.getSiteId());
        if (vo.isAmount()) {
            gv.setAmount(gb.getAmount() == null ? 0 : gb.getAmount());
        }
        if (vo.isReplyNum()) {
            gv.setAmount(gb.getAmount() == null ? 0 : gb.getAmount());
            if(gv.getAmount()==0){
                gv.setReplyRate("0%");
            }else{
                gv.setReplyNum(gb.getReplyNum() == null ? 0 : gb.getReplyNum());
                gv.setReplyRate((100*gv.getReplyNum()/gv.getAmount())+ "%");
            }
        }
        if (vo.isReplyRate()) {
            gv.setReplyRate(gb.getReplyRate() + "%");
        }

        if(vo.isSelectNum()){
            Long selectNum=messageBoardDao.getSelectNum(vo.getSiteId(),vo.getColumnIds());
            gv.setSelectNum(selectNum==null?0:selectNum);
        }
        //统计类型，建议、咨询、投诉、举报
        if (vo.isReportNum() || vo.isConsultNum() || vo.isSuggestNum() || vo.isComplainNum()) {
            List<GuestBookTypeVO> list = messageBoardDao.getAnalysType(vo.getColumnIds(), vo.getSiteId(), null);
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
            List<GuestBookTypeVO> list = messageBoardDao.getAnalysType(vo.getColumnIds(), vo.getSiteId(), 1);
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
            gv.setTodayAmount(messageBoardDao.getDateTotalCount(vo.getColumnIds(), todayDate, vo.getSiteId(), null));
        }
        //今日回复
        if (vo.isTodayReplyNum()) {
            gv.setTodayReplyNum(messageBoardDao.getDateReplyCount(vo.getColumnIds(), todayDate, vo.getSiteId(), null));
        }

        String startDay=DateUtil.getStrMonth();
        //本月受理
        if(vo.isCurMonthAmount()){
            gv.setCurMonthAmount(messageBoardDao.getDateTotalCount(vo.getColumnIds(), startDay, vo.getSiteId(), null));
        }
        //本月办结
        if (vo.isCurMonthReplyNum()) {
            gv.setCurMonthReplyNum(messageBoardDao.getDateReplyCount(vo.getColumnIds(), startDay, vo.getSiteId(), null));
        }

        //获得本年开始时间
        String yearDate = DateUtil.getStrYear();
        //本年度留言、受理
        if (vo.isYearAmount()) {
            gv.setYearAmount(messageBoardDao.getDateTotalCount(vo.getColumnIds(), yearDate, vo.getSiteId(), null));
        }
        //本年度回复、办结
        if (vo.isYearReplyNum()) {
            gv.setYearReplyNum(messageBoardDao.getDateReplyCount(vo.getColumnIds(), yearDate, vo.getSiteId(), null));
        }

        return gv;
    }

    @Override
    public Pagination getMemberPage(MessageBoardPageVO vo) {
        return messageBoardDao.getMemberPage(vo);
    }

    @Override
    public List<MessageBoardEditVO> listMessageBoard(Long siteId, String columnIds, Integer isReply, String createUserId, Integer num) {
        List<MessageBoardEditVO> list = messageBoardDao.listMessageBoard(siteId, columnIds, isReply, createUserId, num);
        return list;
    }


    @Override
    public MessageBoardEditVO searchEO(String randomCode, String docNum, Long siteId) {

        MessageBoardEditVO vo = messageBoardDao.searchEO(randomCode, docNum, siteId);
        String receiveUnitNames = "";
        String recUserNames = "";
        if (vo != null) {
            if (vo.getRecType() != null) {
                if (vo.getRecType().equals(0)) {
                    if (null != vo) {
                        List<MessageBoardForwardVO> forwardVOList = forwardService.getAllUnit(vo.getId());
                        for (int i = 0; i < forwardVOList.size(); i++) {
                            if (i == 0) {
                                receiveUnitNames = receiveUnitNames + forwardVOList.get(i).getReceiveUnitName();
                            } else {
                                receiveUnitNames = receiveUnitNames + ',' + forwardVOList.get(i).getReceiveUnitName();
                            }
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
            if (!StringUtils.isEmpty(vo.getClassCode())) {
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

        }
        return vo;
    }

    @Override
    public void saveComment(MessageBoardEO messageBoardEO) {
        updateEntity(messageBoardEO);
        List<MessageBoardReplyVO> replyEOList = replyService.getAllDealReply(messageBoardEO.getId());
        if (replyEOList != null && replyEOList.size() > 0) {
            for (MessageBoardReplyVO replyVO : replyEOList) {
                MessageBoardReplyEO replyEO = new MessageBoardReplyEO();
                AppUtil.copyProperties(replyEO, replyVO);
                replyEO.setCommentCode(messageBoardEO.getCommentCode());
                replyService.updateEntity(replyEO);
            }
        }
    }

    @Override
    public List<MessageBoardEditVO> getMessageBoardBySiteId(Long siteId) {
        return messageBoardDao.getMessageBoardBySiteId(siteId);
    }

    @Override
    public MessageBoardEO getMessageBoardByKnowledgeBaseId(Long knowledgeBaseId) {
        return messageBoardDao.getMessageBoardByKnowledgeBaseId(knowledgeBaseId);
    }

    @Override
    public Pagination getMobilePage(MessageBoardPageVO pageVO) {
        return messageBoardDao.getMobilePage(pageVO);
    }

    @Override
    public Long getUnReadNum(Long siteId, Long columnId, Long createUserId) {
        return messageBoardDao.getUnReadNum(siteId, columnId, createUserId);
    }

    @Override
    public Pagination getUnAuditCount(UnAuditContentsVO uaVO) {
        return messageBoardDao.getUnAuditCount(uaVO);
    }

    @Override
    public MessageBoardEO saveMessageBoardVO(MessageBoardEditVO editVO) {

        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, editVO);
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, editVO);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setPersonIp(IpUtil.getIpAddr(ContextHolderUtils.getRequest()));
        messageBoardEO.setBaseContentId(id);
        if (!AppUtil.isEmpty(editVO.getAddDate())) {
            messageBoardEO.setAddDate(editVO.getAddDate());
        } else {
            messageBoardEO.setAddDate(new Date());
        }
        if (!AppUtil.isEmpty(editVO.getCreateDate())) {
            messageBoardEO.setCreateDate(editVO.getCreateDate());
        }

        if (StringUtils.isEmpty(editVO.getDocNum())) {
            String dateStr = DateUtil.getYearMonthDayStr();
            Long count = countMessageBoard(contentEO.getSiteId(), contentEO.getColumnId() + "", DateUtil.getToday(), null, null) + 1;
            String countStr = "";
            if (count < 10) {
                countStr += "00" + count;
            } else if (10 <= count && count < 100) {
                countStr += "0" + count;
            } else {
                countStr += count;
            }
            messageBoardEO.setDocNum(dateStr + countStr);
        } else {
            messageBoardEO.setDocNum(editVO.getDocNum());
        }
        if (StringUtils.isEmpty(editVO.getRandomCode())) {
            String randomCode = RandomCode.shortUrl(id + "");
            messageBoardEO.setRandomCode(randomCode);
        } else {
            messageBoardEO.setRandomCode(editVO.getRandomCode());
        }

        saveEntity(messageBoardEO);
        return messageBoardEO;
    }

    @Override
    public List<MessageBoardSearchVO> getAllPulishMessageBoard() {
        return messageBoardDao.getAllPulishMessageBoard();
    }

    @Override
    public Long getNoDealCount() {
        return messageBoardDao.getNoDealCount();
    }

    @Override
    public Pagination getMobilePage2(ContentPageVO contentPageVO) {
        return messageBoardDao.getMobilePage2(contentPageVO);
    }

    @Override
    public Object getCallbackPage(MessageBoardPageVO pageVO) {
        return messageBoardDao.getCallbackPage(pageVO);
    }

    @Override
    public void setRead(Long contentId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseContentId", contentId);
        List<MessageBoardEO> list = getEntities(MessageBoardEO.class, map);
        if (list != null && list.size() > 0) {
            MessageBoardEO eo = list.get(0);
            eo.setIsRead(1);
            updateEntity(eo);
        }
    }

    public List<MessageBoardListVO> getMessageBoardUnitList(StatisticsQueryVO queryVO){

        List<MessageBoardListVO> list = messageBoardDao.getMessageBoardUnitList(queryVO);
        List<MessageBoardListVO> newList = new ArrayList<MessageBoardListVO>();
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
        if (list != null && list.size() >= 0) {

            String organName = queryVO.getOrganName();
            for (int i = 0; i < organList.size(); i++) {
                MessageBoardListVO vo = new MessageBoardListVO();
                vo.setOrganName(organList.get(i).getName());
                vo.setOrganId(organList.get(i).getOrganId());
                for (MessageBoardListVO messageBoardListVO : list) {
                    if (vo.getOrganId().equals(messageBoardListVO.getOrganId())) {
                        Long receiveCount = messageBoardListVO.getReceiveCount() == null ? 0 : messageBoardListVO.getReceiveCount();
                        Long replyCount = messageBoardListVO.getReplyCount() == null ? 0 : messageBoardListVO.getReplyCount();
                        Integer replyRate = messageBoardListVO.getReplyRate() == null ? 0 : (Math.round(messageBoardListVO.getReplyRate()));
                        //如果回复总数大于转办总数，则取默认值
                        if(receiveCount<replyCount){
                            replyCount = receiveCount;
                            replyRate = 100;
                        }
                        vo.setReceiveCount(receiveCount);
                        vo.setReplyCount(replyCount);
                        vo.setReplyRate(replyRate);
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
    public void exportOldAjjMessageBoard(MessageBoardEditVO editVO, List<MessageBoardReplyVO> replyVOList) {
        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, editVO);
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, editVO);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setBaseContentId(id);
        if (!AppUtil.isEmpty(editVO.getAddDate())) {
            messageBoardEO.setAddDate(editVO.getAddDate());
        } else {
            messageBoardEO.setAddDate(new Date());
        }
        if (!AppUtil.isEmpty(editVO.getCreateDate())) {
            messageBoardEO.setCreateDate(editVO.getCreateDate());
        }

        if (StringUtils.isEmpty(editVO.getDocNum())) {
            String dateStr = DateUtil.getYearMonthDayStr();
            Long count = countMessageBoard(contentEO.getSiteId(), contentEO.getColumnId() + "", DateUtil.getToday(), null, null) + 1;
            String countStr = "";
            if (count < 10) {
                countStr += "00" + count;
            } else if (10 <= count && count < 100) {
                countStr += "0" + count;
            } else {
                countStr += count;
            }
            messageBoardEO.setDocNum(dateStr + countStr);
        } else {
            messageBoardEO.setDocNum(editVO.getDocNum());
        }
        if (StringUtils.isEmpty(editVO.getRandomCode())) {
            String randomCode = RandomCode.shortUrl(id + "");
            messageBoardEO.setRandomCode(randomCode);
        } else {
            messageBoardEO.setRandomCode(editVO.getRandomCode());
        }

        saveEntity(messageBoardEO);
        if(replyVOList!=null&&replyVOList.size()>0) {
            for (int j = 0; j < replyVOList.size(); j++) {
                MessageBoardReplyEO replyEO = new MessageBoardReplyEO();
                AppUtil.copyProperties(replyEO, replyVOList.get(j));
                if (replyEO.getReplyContent() != null) {
                    replyEO.setMessageBoardId(messageBoardEO.getId());
                    replyService.saveEntity(replyEO);
                }
            }
        }
    }

    @Override
    public List<MessageBoardEditVO> getUnReply(Long columnId, int day) {
        return messageBoardDao.getUnReply(columnId,day);
    }
    @Override
    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list = messageBoardDao.getTypeList(queryVO);
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
    public MessageBoardEditVO queryRemoved(Long id) {
        MessageBoardEditVO editVO= messageBoardDao.queryRemoved(id);
        if(editVO!=null&&!StringUtils.isEmpty(editVO.getClassCode())){
            DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", editVO.getClassCode());
            if (dictVO != null) {
                editVO.setClassName(dictVO.getKey());
            }
        }
        return editVO;
    }

    @Override
    public void batchCompletelyDelete(Long[] messageBoardIds) {
        messageBoardDao.batchCompletelyDelete(messageBoardIds);
    }

    @Override
    public List getMessageTree(Long[] organIds, Long columnId) {
        if(organIds==null||organIds.length<=0){
            List<ContentModelParaVO> list = contModelService.getParam(columnId, LoginPersonUtil.getSiteId(), 1);
            if(list != null){
                organIds = new Long[list.size()];
                for(int i = 0 ;i< list.size();i++){
                    organIds[i]= list.get(i).getRecUnitId();
                }
            }
        }
        return organDao.getPersonInfosByPlatformCode(organIds, null, null, null);
    }

    @Override
    public Pagination getPageByQuery(MessageBoardSearchVO searchVO) {
        return messageBoardDao.getPageByQuery(searchVO);
    }

    @Override
    public void deleteReply(Long id, Long replyId) {
        Map<String ,Object> map  = new HashMap<String, Object>();
        map.put("baseContentId",id);
        MessageBoardEO messageBoardEO = getEntity(MessageBoardEO.class, map);
        if (messageBoardEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言不存在！");
        }
        Map<String ,Object> replyMap  = new HashMap<String, Object>();
        replyMap.put("messageBoardId",messageBoardEO.getId());
        replyMap.put("id",replyId);
        MessageBoardReplyEO replyEO = replyService.getEntity(MessageBoardReplyEO.class, replyMap);
        if(replyEO!=null) {
            if(replyEO.getForwardId()!=null){
                MessageBoardForwardEO forwardEO=forwardService.getEntity(MessageBoardForwardEO.class,replyEO.getForwardId());
                if(forwardEO!=null){
                    forwardEO.setReplyDate(null);
                    forwardEO.setDealStatus(null);
                    forwardService.updateEntity(forwardEO);
                }
            }
            // 只删除回复表（假删）
            replyService.delete(MessageBoardReplyEO.class, replyId);
        }
    }

    /**
     * 构造组织、单位和虚拟单位对应的TreeNodeVO对象
     *
     * @param nodeTypes
     * @param organ
     * @return
     */
    private TreeNodeVO getOrganTreeNode(String[] nodeTypes, OrganEO organ) {
        TreeNodeVO node = new TreeNodeVO();
        String id = OrganEO.class.getSimpleName().concat(organ.getOrganId().toString());
        node.setId(id);
        node.setDn(organ.getDn());
        //是否外平台
        node.setIsExternalOrgan(organ.getIsExternalOrgan());
        //平台编码
        node.setPlatformCode(organ.getPlatformCode());
        Long parentId = organ.getParentId();
        if (parentId != null) {
            String pid = OrganEO.class.getSimpleName().concat(
                    parentId.toString());
            node.setPid(pid);
        }
        node.setName(organ.getName());
        // 节点类型
        if (organ.getType().equals(TreeNodeVO.Type.Organ.toString())) {
            if (organ.getIsFictitious() == 1) {
                node.setType(TreeNodeVO.Type.VirtualNode.toString());
                node.setIcon(TreeNodeVO.Icon.VirtualNode.getValue());
            } else {
                node.setType(TreeNodeVO.Type.Organ.toString());
                node.setIcon(TreeNodeVO.Icon.Organ.getValue());
            }
            // 此处对应前端的单位
            node.setUnitId(organ.getOrganId());
            node.setUnitName(organ.getName());
        } else {
            if (organ.getIsFictitious() == 1) {
                node.setType(TreeNodeVO.Type.Virtual.toString());
                node.setIcon(TreeNodeVO.Icon.Virtual.getValue());
            } else {
                node.setType(TreeNodeVO.Type.OrganUnit.toString());
                node.setIcon(TreeNodeVO.Icon.OrganUnit.getValue());
            }
            // 此处对应前端的部门/处室
            node.setOrganId(organ.getOrganId());
            node.setOrganName(organ.getName());
        }
        // 是否是父节点处理
        Boolean isParent = Boolean.FALSE;
        if (nodeTypes == null) {
            isParent = Boolean.TRUE;
        } else {
            for (String nodeType : nodeTypes) {
                if (nodeType.equals(TreeNodeVO.Type.VirtualNode.toString())) {
                    if (organ.getHasVirtualNodes()!=null&&organ.getHasVirtualNodes() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
                if (nodeType.equals(TreeNodeVO.Type.Organ.toString())) {
                    if (organ.getHasOrgans() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
                if (nodeType.equals(TreeNodeVO.Type.OrganUnit.toString())) {
                    if (organ.getHasOrganUnits() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
                if (nodeType.equals(TreeNodeVO.Type.Virtual.toString())) {
                    if (organ.getHasFictitiousUnits() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
                if (nodeType.equals(TreeNodeVO.Type.Person.toString())) {
                    if (organ.getHasPersons() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        node.setIsParent(isParent);
        return node;
    }

    @Override
    public Long getSummaryCount(StatisticsQueryVO queryVO) {
        return messageBoardDao.getSummaryCount(queryVO);
    }
}
