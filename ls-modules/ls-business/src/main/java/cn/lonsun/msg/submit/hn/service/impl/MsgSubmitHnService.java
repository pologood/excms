package cn.lonsun.msg.submit.hn.service.impl;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.util.ContentOptRecordUtil;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.timingjob.jobimpl.NewsTopTaskImpl;
import cn.lonsun.job.util.ScheduleJobUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.msg.submit.hn.dao.IMsgSubmitHnDao;
import cn.lonsun.msg.submit.hn.entity.CmsMsgSubmitHnEO;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToColumnHnEO;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToUserHnEO;
import cn.lonsun.msg.submit.hn.service.IMsgSubmitHnService;
import cn.lonsun.msg.submit.hn.service.IMsgToColumnHnService;
import cn.lonsun.msg.submit.hn.service.IMsgToUserHnService;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;
import cn.lonsun.system.role.internal.entity.RbacSiteRightsEO;
import cn.lonsun.system.role.internal.service.IInfoOpenRightsService;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class MsgSubmitHnService extends MockService<CmsMsgSubmitHnEO> implements IMsgSubmitHnService {

    @Autowired
    private IMsgSubmitHnDao msgSubmitHnDao;

    @Autowired
    private IMsgToColumnHnService msgToColumnHnService;

    @Autowired
    private IMsgToUserHnService msgToUserHnService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private ISiteRightsService siteRightsService;

    @Autowired
    private IInfoOpenRightsService infoOpenRightsService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IUserSiteOptService userSiteOptService;

    @Override
    public Pagination getPageList(ParamDto dto) {
        Pagination page = msgSubmitHnDao.getPageList(dto);

        List<CmsMsgSubmitHnEO> submitHnEOs = (List<CmsMsgSubmitHnEO>) page.getData();
        if (null != submitHnEOs) {
            for (CmsMsgSubmitHnEO submitHnEO : submitHnEOs) {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("msgId", submitHnEO.getId());
                param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                param.put("isDefault", 0);
                List<CmsMsgToColumnHnEO> columnHnEOs = msgToColumnHnService.getEntities(CmsMsgToColumnHnEO.class, param);
                List<CmsMsgToUserHnEO> userHnHnEOs = msgToUserHnService.getEntities(CmsMsgToUserHnEO.class, param);
                if (null != columnHnEOs) {
                    submitHnEO.setColumnHnEOs(columnHnEOs);
                }

                if (null != userHnHnEOs) {
                    submitHnEO.setUserHnEOs(userHnHnEOs);
                }
            }
        }
        return page;
    }

    @Override
    public Pagination getToMePageList(ParamDto dto) {
        return msgSubmitHnDao.getToMePageList(dto);
    }

    @Override
    public Pagination getTobePageList(ParamDto dto) {
        //拥有发布权限的栏目
        dto = addOptAuthParam(dto, "publish");
        return msgSubmitHnDao.getTobePageList(dto);
    }

    @Override
    public Pagination getBePageList(ParamDto dto) {
        //拥有取消发布权限的栏目
        dto = addOptAuthParam(dto, "edit");
        return msgSubmitHnDao.getBePageList(dto);
    }

    @Override
    public Long getToMeCount() {
        return msgSubmitHnDao.getToMeCount();
    }

    @Override
    public Long getToBeCount(ParamDto dto) {
        //拥有发布权限的栏目
        dto = addOptAuthParam(dto, "publish");
        return msgSubmitHnDao.getToBeCount(dto);
    }

    @Override
    public Long getBeCount(ParamDto dto) {
        //拥有取消发布权限的栏目
        dto = addOptAuthParam(dto, "edit");
        return msgSubmitHnDao.getBeCount(dto);
    }

    @Override
    public Long saveEntity(CmsMsgSubmitHnEO eo) {
        Long msgId = super.saveEntity(eo);
        List<CmsMsgToColumnHnEO> columnHnEOs = eo.getColumnHnEOs();
        if (null != columnHnEOs && !columnHnEOs.isEmpty()) {
            //保存报送栏目
            for (CmsMsgToColumnHnEO columnHnEO : columnHnEOs) {
                columnHnEO.setMsgId(msgId);
            }

            msgToColumnHnService.saveEntities(columnHnEOs);
        }

        List<CmsMsgToUserHnEO> userHnEOs = eo.getUserHnEOs();
        if (null != userHnEOs && !userHnEOs.isEmpty()) {
            //保存报送栏目
            for (CmsMsgToUserHnEO userHnEO : userHnEOs) {
                userHnEO.setMsgId(msgId);
            }
            //保存传阅用户
            msgToUserHnService.saveEntities(userHnEOs);
        }

        return msgId;
    }

    @Override
    public void updateEO(CmsMsgSubmitHnEO eo) {

        //保存不存在的栏目信息
        List<CmsMsgToColumnHnEO> columnHnEOs = eo.getColumnHnEOs();
        if (null != columnHnEOs && !columnHnEOs.isEmpty()) {
            for (CmsMsgToColumnHnEO columnHnEO : columnHnEOs) {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("msgId", eo.getId());
                param.put("columnId", columnHnEO.getColumnId());
                List<CmsMsgToColumnHnEO> columns = msgToColumnHnService.getEntities(CmsMsgToColumnHnEO.class, param);
                if (null == columns || columns.isEmpty()) {
                    msgToColumnHnService.saveEntity(columnHnEO);
                }
            }
        }

        //保存为存在的传阅用户信息
        List<CmsMsgToUserHnEO> userHnEOs = eo.getUserHnEOs();
        if (null != userHnEOs && !userHnEOs.isEmpty()) {
            for (CmsMsgToUserHnEO userHnEO : userHnEOs) {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("msgId", eo.getId());
                param.put("organId", userHnEO.getOrganId());
                param.put("userId", userHnEO.getUserId());
                List<CmsMsgToUserHnEO> users = msgToUserHnService.getEntities(CmsMsgToUserHnEO.class, param);
                if (null == users || users.isEmpty()) {
                    userHnEO.setSiteId(LoginPersonUtil.getSiteId());
                    msgToUserHnService.saveEntity(userHnEO);
                }
            }
        }

        CmsMsgSubmitHnEO saveeo = this.getEntity(CmsMsgSubmitHnEO.class, eo.getId());
        saveeo.setTitle(eo.getTitle());
        saveeo.setProvider(eo.getProvider());
        saveeo.setSubmitUnitName(eo.getSubmitUnitName());
        saveeo.setAuthor(eo.getAuthor());
        saveeo.setPublishDate(eo.getPublishDate());
        saveeo.setSources(eo.getSources());
        saveeo.setContent(eo.getContent());
        saveeo.setImageLink(eo.getImageLink());
        if (saveeo.getStatus() == 1) {
            saveeo.setStatus(0);
        }
        super.updateEntity(saveeo);
    }

    @Override
    public void deleteEntities(Long[] msgIds) {
        if (null != msgIds && msgIds.length > 0) {
            for (Long msgId : msgIds) {
                msgSubmitHnDao.delete(CmsMsgSubmitHnEO.class, msgId);
                msgToColumnHnService.deleteByMsgId(msgId);
                msgToUserHnService.deleteByMsgId(msgId);
            }
        }
    }

    @Override
    public void readMsg(Long msgId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("msgId", msgId);
        param.put("organId", LoginPersonUtil.getOrganId());
        param.put("userId", LoginPersonUtil.getUserId());
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        CmsMsgToUserHnEO userHnEO = msgToUserHnService.getEntity(CmsMsgToUserHnEO.class, param);
        //设置为已阅状态
        userHnEO.setMsgReadStatus(1);
        msgToUserHnService.updateEntity(userHnEO);
    }

    @Override
    public void batchTransmit(Long[] msgIds, List<CmsMsgToUserHnEO> userHnEOs) {
        if (null != msgIds) {
            List<CmsMsgToUserHnEO> savelist = new ArrayList<CmsMsgToUserHnEO>();
            for (Long msgId : msgIds) {
                for (CmsMsgToUserHnEO userHnEO : userHnEOs) {
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("msgId", msgId);
                    param.put("organId", userHnEO.getOrganId());
                    param.put("userId", userHnEO.getUserId());
                    List<CmsMsgToUserHnEO> usersexist = msgToUserHnService.getEntities(CmsMsgToUserHnEO.class, param);
                    if (null == usersexist || usersexist.isEmpty()) {
                        CmsMsgToUserHnEO save = new CmsMsgToUserHnEO();
                        save.setMsgId(msgId);
                        save.setName(userHnEO.getName());
                        save.setOrganId(userHnEO.getOrganId());
                        save.setUserId(userHnEO.getUserId());
                        save.setUnitId(userHnEO.getUnitId());
                        save.setSiteId(LoginPersonUtil.getSiteId());
                        save.setCreateUnitId(LoginPersonUtil.getUnitId());
                        savelist.add(save);
                    }
                }
            }
            msgToUserHnService.saveEntities(savelist);
        }
    }

    @Override
    public void batchTransmitToColumn(Long[] msgIds, List<CmsMsgToColumnHnEO> columnHnEOs) {
        if (null != msgIds) {
            List<CmsMsgToColumnHnEO> savelist = new ArrayList<CmsMsgToColumnHnEO>();
            for (Long msgId : msgIds) {
                for (CmsMsgToColumnHnEO columnHnEO : columnHnEOs) {
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("msgId", msgId);
                    param.put("columnId", columnHnEO.getColumnId());
                    List<CmsMsgToColumnHnEO> columnsexist = msgToColumnHnService.getEntities(CmsMsgToColumnHnEO.class, param);
                    if (null == columnsexist || columnsexist.isEmpty()) {
                        CmsMsgToColumnHnEO save = new CmsMsgToColumnHnEO();
                        save.setMsgId(msgId);
                        save.setColumnId(columnHnEO.getColumnId());
                        save.setColumnName(columnHnEO.getColumnName());
                        save.setOrganId(columnHnEO.getOrganId());
                        save.setCode(columnHnEO.getCode());
                        save.setColumnTypeCode(columnHnEO.getColumnTypeCode());
                        save.setCreateUnitId(LoginPersonUtil.getUnitId());
                        save.setSiteId(columnHnEO.getSiteId());
                        save.setSiteName(columnHnEO.getSiteName());
                        savelist.add(save);
                    }
                }
            }
            msgToColumnHnService.saveEntities(savelist);
        }
    }

    @Override
    public void publish(CmsMsgSubmitHnEO eo) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("msgId", eo.getId());
        param.put("status", 0);
        List<CmsMsgToColumnHnEO> columnHnEOs = msgToColumnHnService.getEntities(CmsMsgToColumnHnEO.class, param);
        if (null != columnHnEOs && !columnHnEOs.isEmpty()) {
            for (CmsMsgToColumnHnEO columnHnEO : columnHnEOs) {
                Long contentId = columnHnEO.getContentId();
                if (columnHnEO.getColumnTypeCode().equals("DRIVING_PUBLIC")) {
                    //信息公开发
                } else if (columnHnEO.getColumnTypeCode().equals("CMS_Section")) {
                    //是否有操作权限
                    if (isColumnPublicAuth(columnHnEO.getColumnId(), columnHnEO.getSiteId())) {
                        //栏目已经发布过了
                        if (!AppUtil.isEmpty(contentId)) {
                            BaseContentEO bceo = baseContentService.getEntity(BaseContentEO.class, contentId);
                            if (null != bceo) {
                                //消息已经发布过了 但是被取消发布了
                                if (bceo.getIsPublish() == 0) {
                                    if (bceo.getPublishDate() == null) {
                                        bceo.setPublishDate(new Date());
                                    }
                                    //更新发布状态
                                    MessageSenderUtil.publishContent(new MessageStaticEO(bceo.getSiteId(), bceo.getColumnId(), new Long[]{contentId}).setType(MessageEnum.PUBLISH.value()), 1);
                                    SysLog.log("发布内容 >> ID：" + contentId + ",标题：" + bceo.getTitle(),
                                            "BaseContentEO", CmsLogEO.Operation.Update.toString());
                                    baseContentService.changePublish(new ContentPageVO(null, null, 1, new Long[]{contentId}, null));
                                    CacheHandler.saveOrUpdate(BaseContentEO.class, bceo);
                                    columnHnEO.setStatus(1);
                                    msgToColumnHnService.updateEntity(columnHnEO);
                                    try {
                                        //增加记录
                                        ContentOptRecordUtil.saveOptRecord(new Long[]{contentId}, 1, ContentOptRecordEO.Type.submit);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                //消息已经发布了但是被删除了被删除了 重新发布
                                simpleMsgPublish(columnHnEO, eo);
                            }
                        } else {
                            //消息第一次发布
                            simpleMsgPublish(columnHnEO, eo);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void batchPublish(Long[] msgIds) {

    }

    @Override
    public Pagination getColumnPageList(Long msgId, ParamDto dto) {
        //拥有取消发布权限的栏目
        dto = addOptAuthParam(dto, dto.getOptCode());
//        Pagination page = msgToColumnHnService.getColumnPageList(msgId,dto);
        /*if(null != page && null != page.getData()) {
            List<CmsMsgToColumnHnEO> columnHnEOs = (List<CmsMsgToColumnHnEO>)page.getData();
            if(null != columnHnEOs && !columnHnEOs.isEmpty()) {
                for(CmsMsgToColumnHnEO columnHnEO : columnHnEOs) {
                    if(null != columnHnEO.getSiteId()) {
                        IndicatorEO eo = indicatorService.getEntity(IndicatorEO.class,columnHnEO.getSiteId());
                        if(null != eo && eo.getType().equals(IndicatorEO.Type.CMS_Site.toString())) {
                            columnHnEO.setSiteName(eo.getName());
                        }
                    }
                }
            }
        }*/
        return msgToColumnHnService.getColumnPageList(msgId, dto);
    }

    @Override
    public void cancelPublish(Long msgId, Long[] columnIds) {
        if (null != msgId && null != columnIds) {
            CmsMsgSubmitHnEO submitHnEO = this.getEntity(CmsMsgSubmitHnEO.class, msgId);
            for (Long columnId : columnIds) {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("msgId", msgId);
                param.put("columnId", columnId);
                CmsMsgToColumnHnEO columnHnEO = msgToColumnHnService.getEntity(CmsMsgToColumnHnEO.class, param);
                Long contentId = columnHnEO.getContentId();
                if (null != contentId) {
                    BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, contentId);
                    if (null != contentEO && contentEO.getIsPublish() == 1) {
                        //更新发布状态
                        MessageSenderUtil.publishContent(new MessageStaticEO(columnHnEO.getSiteId(), columnId, new Long[]{contentId}).setType(MessageEnum.UNPUBLISH.value()), 2);
                        SysLog.log("取消发布内容 >> ID：" + contentId + ",标题：" + submitHnEO.getTitle(),
                                "BaseContentEO", CmsLogEO.Operation.Update.toString());
                        baseContentService.changePublish(new ContentPageVO(null, null, 0, new Long[]{contentId}, null));
                    }
                }

                columnHnEO.setStatus(0);
                msgToColumnHnService.updateEntity(columnHnEO);
            }
        }
    }

    @Override
    public void pushMsgToWeibo(Long msgId) {

    }

    @Override
    public void pushMsgToWeixin(Long msgId) {

    }

    /**
     * 单个信息发布
     *
     * @param columnHnEO
     * @param eo
     */
    private void simpleMsgPublish(CmsMsgToColumnHnEO columnHnEO, CmsMsgSubmitHnEO eo) {
        BaseContentEO contentEO = new BaseContentEO();
        contentEO.setColumnId(columnHnEO.getColumnId());
        contentEO.setSiteId(columnHnEO.getSiteId());
        contentEO.setTitle(eo.getTitle());
        contentEO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
        contentEO.setAuthor(eo.getAuthor());
        contentEO.setPublishDate(new Date());
        contentEO.setImageLink(eo.getImageLink());
        contentEO.setResources(eo.getSources());
        contentEO.setCreateOrganId(LoginPersonUtil.getOrganId());
        contentEO.setCreateUserId(LoginPersonUtil.getUserId());

        contentEO.setIsTop(eo.getIsTop());
        contentEO.setIsNew(eo.getIsNew());
        contentEO.setIsTitle(eo.getIsTitle());
        contentEO.setIsJob(eo.getIsJob());
        contentEO.setTopValidDate(eo.getTopValidDate());
        contentEO.setJobIssueDate(eo.getJobIssueDate());
        contentEO.setRedirectLink(eo.getRedirectLink());

        //报送消息
        Long id = baseContentService.saveArticleNews(contentEO, eo.getContent(), null, null, null, null);
        try {
            //增加记录
            ContentOptRecordUtil.saveOptRecord(new Long[]{id}, contentEO.getIsPublish(), ContentOptRecordEO.Type.submit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //引用到栏目
//        CopyReferVO vo = new CopyReferVO();
//        vo.setContentId(String.valueOf(eo.getId()));
//        baseContentService.synCloumnInfos(contentEO.getColumnId(),vo);

        //发布新闻
        MessageSenderUtil.publishContent(
                new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
        if (contentEO.getIsTop() == 1 && contentEO.getTopValidDate() != null) {
            ScheduleJobUtil.addScheduleJob("新闻置顶有效期", NewsTopTaskImpl.class.getName(), ScheduleJobUtil.dateToCronExpression(contentEO.getTopValidDate()),
                    String.valueOf(id));
        }
        /*//设置定时发布
        Integer isJob = 0;
        if (contentEO.getIsJob() == 1 && contentEO.getJobIssueDate() != null) {
            isJob = 1;
            ScheduleJobUtil.addOrDelScheduleJob("新闻定时发布日期", NewsIssueTaskImpl.class.getName(), isJob == 0 ? null : ScheduleJobUtil.dateToCronExpression(contentEO.getJobIssueDate()), String.valueOf(id), isJob);
        }*/
        contentEO.setPublishDate(new Date());
        contentEO.setIsPublish(1);
        baseContentService.updateEntity(contentEO);

        SysLog.log("发布内容 >> ID：" + id + ",标题：" + contentEO.getTitle(),
                "BaseContentEO", CmsLogEO.Operation.Update.toString());
        columnHnEO.setStatus(1);
        columnHnEO.setContentId(id);
        eo.setStatus(2);
        msgToColumnHnService.updateEntity(columnHnEO);
        this.updateEntity(eo);
    }

    /**
     * 栏目站点权限过滤
     *
     * @param columnId
     * @param siteId
     * @return
     */
    private boolean isColumnPublicAuth(Long columnId, Long siteId) {
        boolean flag = false;
        //针对普通用户增加栏目操作权限过滤功能
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        if (LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin()) {
            //超管
            flag = true;
        } else if (LoginPersonUtil.isSiteAdmin()) {
            //站点获取当前用户拥有的站点权限
            List<CmsUserSiteOptEO> siteOptEOs = userSiteOptService.getOpts(organId, userId);
            if (null != siteOptEOs && !siteOptEOs.isEmpty()) {
                for (CmsUserSiteOptEO eo : siteOptEOs) {
                    if (null != siteId && null != eo && null != eo.getSiteId()
                            && eo.getSiteId().intValue() == siteId.intValue()) {
                        flag = true;
                        break;
                    }
                }
            }
        } else {
            //普通用户过滤权限
            List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
            Long[] ids = new Long[roles.size()];
            int count = 0;
            for (RoleAssignmentEO eo : roles) {
                ids[count++] = eo.getRoleId();
            }
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("roleId", ids);
            param.put("siteId", siteId);
            param.put("optCode", "publish");
            param.put("indicatorId", columnId);
            List<RbacSiteRightsEO> crights = siteRightsService.getEntities(RbacSiteRightsEO.class, param);
            if (null != crights && !crights.isEmpty()) {
                flag = true;
            }
        }
        return flag;
    }

    private ParamDto addOptAuthParam(ParamDto dto, String opt) {
        //针对普通用户增加栏目操作权限过滤功能
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        if (!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()) {
            if (LoginPersonUtil.isSiteAdmin()) {
                //站点获取当前用户拥有的站点权限
                List<CmsUserSiteOptEO> siteOptEOs = userSiteOptService.getOpts(organId, userId);
                if (null != siteOptEOs && !siteOptEOs.isEmpty()) {
                    Set<Long> siteIds = new HashSet<Long>();
                    for (CmsUserSiteOptEO eo : siteOptEOs) {
                        if (null != eo && null != eo.getSiteId()) {
                            siteIds.add(eo.getSiteId());
                        }
                    }
                    dto.setSiteIds(siteIds);
                }
            } else {
                List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
                List<Long> ids = new ArrayList<Long>();
                List<Long> idsCAdmin = new ArrayList<Long>();
                List<Long> idsPAdmin = new ArrayList<Long>();
                for (RoleAssignmentEO eo : roles) {
                    if ("Column".equals(eo.getRoleType())) {
                        idsCAdmin.add(eo.getRoleId());
                    } else if ("PublicInfo".equals(eo.getRoleType())) {
                        idsPAdmin.add(eo.getRoleId());
                    } else {
                        ids.add(eo.getRoleId());
                    }
                }

                List<RbacSiteRightsEO> crights = new ArrayList<RbacSiteRightsEO>();
                List<RbacInfoOpenRightsEO> irights = new ArrayList<RbacInfoOpenRightsEO>();
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("roleId", ids);
                param.put("optCode", opt);
                if (!ids.isEmpty()) {
                    crights.addAll(siteRightsService.getEntities(RbacSiteRightsEO.class, param));
                    irights.addAll(infoOpenRightsService.getEntities(RbacInfoOpenRightsEO.class, param));
                }

                Map<String, Object> cparam = new HashMap<String, Object>();
                cparam.put("roleId", idsCAdmin);
                if (!idsCAdmin.isEmpty()) {
                    crights.addAll(siteRightsService.getEntities(RbacSiteRightsEO.class, cparam));
                    irights.addAll(infoOpenRightsService.getEntities(RbacInfoOpenRightsEO.class, cparam));
                }

                Map<String, Object> pparam = new HashMap<String, Object>();
                pparam.put("roleId", idsPAdmin);
                if (!idsPAdmin.isEmpty()) {
                    crights.addAll(siteRightsService.getEntities(RbacSiteRightsEO.class, pparam));
                    irights.addAll(infoOpenRightsService.getEntities(RbacInfoOpenRightsEO.class, pparam));
                }

                Set<Long> columns = new HashSet<Long>();
                if (null != crights) {
                    for (RbacSiteRightsEO rightsEO : crights) {
                        columns.add(rightsEO.getIndicatorId());
                    }

                }
                dto.setColumns(columns);
                Set<String> codes = new HashSet<String>();
                if (null != irights) {
                    for (RbacInfoOpenRightsEO rightsEO : irights) {
                        if (null != rightsEO.getCode()) {
                            codes.add(rightsEO.getCode());
                        }
                    }
                }
                dto.setCodes(codes);
            }
        }
        return dto;
    }
}
