package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.baidu.BaiduPushVO;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IForwardRecordService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.content.vo.GuestBookPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.role.internal.site.service.IRoleSiteOptService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lonsun.activemq.MessageSender.sendMessage;
import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.util.DataDictionaryUtil.getItem;
import static cn.lonsun.util.ModelConfigUtil.getCongfigVO;

/**
 * @author hujun
 * @ClassName: GuestBookController
 * @Description: 留言管理控制器
 * @date 2015年10月26日 下午1:30:34
 */
@Controller
@RequestMapping(value = "guestBook")
public class GuestBookController extends BaseController {
    @Autowired
    private IGuestBookService guestBookService;
    @Autowired
    private IForwardRecordService forwardRecordService;
    @Autowired
    private IBaseContentService contentService;
    @Autowired
    private IContentModelService contModelService;

    @Autowired
    private IRoleSiteOptService optService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private TaskExecutor taskExecutor;
    static Logger logger = LoggerFactory.getLogger(GuestBookController.class);

    // 进入页面
    @RequestMapping(value = "index")
    public String guestBookList() {
        return "/content/guestbook";
    }

    /**
     * 获取内容模型中配置的接收单位和留言类型
     * @param columnId
     * @return
     */
    @RequestMapping(value = "getRec")
    @ResponseBody
    public Object getRec(Long columnId) {
        List<ContentModelParaVO> recList = ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(), null);
        List<ContentModelParaVO> codeList = ModelConfigUtil.getGuestBookType(columnId, LoginPersonUtil.getSiteId());
        if (recList == null || recList.size() <= 0) {
            return codeList;
        } else {
            if (codeList != null && codeList.size() > 0) {
                recList.addAll(codeList);
            }
            return recList;
        }

    }

    /**
     * 获取留言分页
     * @param pageVO
     * @return
     */
    @RequestMapping(value = "getPage")
    @ResponseBody
    public Object getPage(GuestBookPageVO pageVO) {
        return getObject(guestBookService.getPage(pageVO));
    }

    /**
     * 打开回复页面
     * @param id
     * @param columnId
     * @param model
     * @return
     */
    @RequestMapping(value = "guestBookReply")
    public String guestBookReply(Long id, Long columnId, Model model) {
        Integer recType = null;
        Integer isTurn = null;
        ColumnTypeConfigVO configVO = getCongfigVO(columnId, LoginPersonUtil.getSiteId());
        if (configVO != null) {
            recType = configVO.getRecType();
            isTurn = configVO.getTurn();
        }
        List<ContentModelParaVO> statusList = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        if (statusList == null || statusList.size() <= 0) {
            model.addAttribute("status", 0);
        } else {
            model.addAttribute("status", 1);
        }
        model.addAttribute("statusList", statusList);
        model.addAttribute("baseContentId", id);
        model.addAttribute("recType", recType);
        model.addAttribute("isTurn", isTurn);
        Boolean isAdmin=false;
        isAdmin=LoginPersonUtil.isSuperAdmin()||LoginPersonUtil.isSiteAdmin();
        model.addAttribute("isAdmin",isAdmin);
        return "/content/guestbook_reply";
    }

    /**
     * 根据主表ID获取留言信息
     * @param id
     * @return
     */
    @RequestMapping("getVO")
    @ResponseBody
    public Object getVO(Long id) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        BaseContentEO contentEO = getEntity(BaseContentEO.class, id);
        if (null == contentEO) {
            contentEO = contentService.getRemoved(id);
        }
        if (null == contentEO) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言为空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseContentId", id);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<GuestBookEO> list = guestBookService.getEntities(GuestBookEO.class, map);
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
        if (vo.getReplyDate() == null) {
            vo.setReplyDate(new Date());
        }
        ColumnTypeConfigVO configVO = getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
        if (configVO != null) {
            if (!StringUtils.isEmpty(vo.getDealStatus()) && !StringUtils.isEmpty(configVO.getStatusCode())) {
                if (!configVO.getStatusCode().contains(vo.getDealStatus())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", vo.getDealStatus());
                    if (dictVO != null) {
                        vo.setStatusName(dictVO.getKey());
                    }
                }
            }
        }
        if (vo.getIsPublic() == null) {
            vo.setIsPublic(0);
        }
        if (vo.getIsPublicInfo() == null) {
            vo.setIsPublicInfo(0);
        }
        return getObject(vo);
    }


    /**
     * 回复留言
     * @param guestBookEO
     * @return
     */
    @RequestMapping("reply")
    @ResponseBody
    public Object reply(GuestBookEditVO guestBookEO) {
        if (guestBookEO.getReplyDate().before(guestBookEO.getAddDate())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "回复日期应大于留言日期");
        }
        if (!StringUtils.isEmpty(guestBookEO.getResponseContent())) {
            GuestBookEO eo = guestBookService.reply(guestBookEO);
            if (eo == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "保存出错");
            }
            MessageSenderUtil.publishContent(new MessageStaticEO(guestBookEO.getSiteId(), guestBookEO.getColumnId(), new Long[]{guestBookEO.getBaseContentId()}).
                setType(MessageEnum.PUBLISH.value()), 1);
            Integer recourceType = eo.getResourceType();
            BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, guestBookEO.getBaseContentId());
            if (contentEO == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错！");
            }
            if ("replyed".equals(guestBookEO.getDealStatus()) || "handled".equals(guestBookEO.getDealStatus())) {
                if (recourceType != null && recourceType == 3) {
                    if (contentEO.getCreateUserId() != null) {
                        MemberEO memberEO = memberService.getEntity(MemberEO.class, contentEO.getCreateUserId());
                        if (memberEO != null) {
                            String bdId = memberEO.getBduserid();
                            if (!StringUtils.isEmpty(bdId)) {
                                String type = memberEO.getBdtype();
                                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, contentEO.getSiteId());
                                String msgUrl = indicatorEO.getUri() + "/mobile/guestBookShow?id=" + contentEO.getId();
                                BaiduPushVO pushVO = new BaiduPushVO();
                                pushVO.setMsgCode(BaseContentEO.TypeCode.guestBook.toString());
                                pushVO.setChannelId(bdId);
                                pushVO.setMsgColumnId(contentEO.getColumnId());
                                pushVO.setTitle(contentEO.getTitle());
                                pushVO.setMsgUrl(msgUrl);
                                pushVO.setPushType("single");
                                if (MemberEO.BDTYPE.ANDROID.toString().equals(type)) {
                                    pushVO.setDeviceType(3);
                                    pushVO.setDescription("您的留言已办结");
                                    Long num = guestBookService.getUnReadNum(contentEO.getSiteId(), null, contentEO.getCreateUserId());
                                    pushVO.setBadge(num == null ? 0L : num);
                                    //BaiduPushUtil.pushAndroidSingleMsg(bdId, contentEO.getColumnId(), contentEO.getTitle(), "您的留言已转办", msgUrl);
                                    BaiduPushUtil.sendPushMsg("android", pushVO);

                                } else if (MemberEO.BDTYPE.IOS.toString().equals(type)) {
                                    pushVO.setDeviceType(4);
                                    pushVO.setDescription("您的留言已办结");
                                    Long num = guestBookService.getUnReadNum(contentEO.getSiteId(), null, contentEO.getCreateUserId());
                                    pushVO.setBadge(num == null ? 0L : num);
                                    //BaiduPushUtil.pushIosSingleMsg(bdId, contentEO.getColumnId(), contentEO.getTitle(), "您的留言已办结", msgUrl);
                                    BaiduPushUtil.sendPushMsg("ios", pushVO);
                                }
                            }
                        }
                    }
                }
            }
            return getObject();
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言回复内容不可为空！");
        }

    }


    /**
     * 打开转办页面
     * @param id
     * @param columnId
     * @param model
     * @return
     */
    @RequestMapping(value = "guestBookForward")
    public String guestBookForward(Long id, Long columnId, Model model) {
        Long siteId = LoginPersonUtil.getSiteId();
        List<ContentModelParaVO> list = contModelService.getParam(columnId, siteId, 1);
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(columnId, siteId);
        if (configVO != null) {
            model.addAttribute("recType", configVO.getRecType());
            model.addAttribute("isLocal", configVO.getIsLocalUnit());
            model.addAttribute("return", configVO.getTurn());
            if (configVO.getIsLocalUnit() != null && configVO.getIsLocalUnit() == 1) {
                List<DataDictVO> localList = DataDictionaryUtil.getItemList("local_unit", siteId);
                model.addAttribute("localList", localList);
            }
        }
        model.addAttribute("list", list);
        model.addAttribute("id", id);
        return "/content/guestbook_forward";
    }

    /**
     * 保存转办记录同时更改留言表里的接受单位
     * @param id
     * @param receiveId
     * @param receiveUserCode
     * @param remarks
     * @param receiveName
     * @param recType
     * @param localUnitId
     * @param isLocal
     * @return
     */
    @RequestMapping("forward")
    @ResponseBody
    public Object forward(Long id, Long receiveId, String receiveUserCode, String remarks,
                          String receiveName, Integer recType, String localUnitId, Integer isLocal) {
        // GuestBookForwardRecordEO eo = new GuestBookForwardRecordEO();
        // eo.setGuestBookId(guestBookId);
        // eo.setReceiveOrganId(receiveId);
        // forwardRecordService.saveRecord(eo);
        if (isLocal != null && isLocal == 1 && StringUtils.isEmpty(localUnitId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择流转区域");
        }
        if (recType != null && recType == 0 && AppUtil.isEmpty(receiveId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择流转单位");
        }
        if (recType != null && recType == 1) {
            if (StringUtils.isEmpty(receiveUserCode) && receiveId == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择流转人或单位");
            }

        }

        final Long userId = LoginPersonUtil.getUserId();
        GuestBookEO guestBookEO = guestBookService.getEntity(GuestBookEO.class, id);
        final BaseContentEO contentEO = getEntity(BaseContentEO.class, guestBookEO.getBaseContentId());
        String strIds = "";
        if (receiveId != null) {
            List<TreeNodeVO> list = optService.getUserAuthForColumn(receiveId, contentEO.getColumnId());
            if (list == null || list.size() <= 0) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该单位下没有办理人员");
            }
            for (int i = 0; i < list.size(); i++) {
                strIds += list.get(i).getUserId() + ",";
            }
            if (StringUtils.isEmpty(strIds)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该单位下没有办理人员");
            }
        }

        guestBookService.forward(id, receiveId, receiveUserCode, recType, localUnitId);
        GuestBookForwardRecordEO eo = new GuestBookForwardRecordEO();
        eo.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
        eo.setUserName(LoginPersonUtil.getPersonName());
        eo.setGuestBookId(id);
        if (recType != null && recType == 0) {
            eo.setReceiveOrganId(receiveId);
        }
        if (recType != null && recType == 1) {
            eo.setReceiveUserCode(receiveUserCode);
            eo.setReceiveOrganId(receiveId);
        }
        eo.setRemarks(remarks);
        eo.setReceiveName(receiveName);
        forwardRecordService.saveRecord(eo);
        final String receiveIds = strIds;
        if (StringUtils.isEmpty(receiveIds)) {
            return getObject();
        }
        Integer resourceType = guestBookEO.getResourceType();
        if (resourceType != null && resourceType == 3) {
            if (contentEO.getCreateUserId() != null) {
                MemberEO memberEO = memberService.getEntity(MemberEO.class, contentEO.getCreateUserId());
                if (memberEO != null) {
                    String bdId = memberEO.getBduserid();
                    if (!StringUtils.isEmpty(bdId)) {
                        String type = memberEO.getBdtype();
                        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, contentEO.getSiteId());
                        String msgUrl = indicatorEO.getUri() + "/mobile/guestBookShow?id=" + contentEO.getId();
                        BaiduPushVO pushVO = new BaiduPushVO();
                        pushVO.setMsgCode(BaseContentEO.TypeCode.guestBook.toString());
                        pushVO.setChannelId(bdId);
                        pushVO.setMsgColumnId(contentEO.getColumnId());
                        pushVO.setTitle(contentEO.getTitle());
                        pushVO.setMsgUrl(msgUrl);
                        pushVO.setPushType("single");
                        if (MemberEO.BDTYPE.ANDROID.toString().equals(type)) {
                            pushVO.setDeviceType(3);
                            pushVO.setDescription("您的留言已转办");
                            Long num = guestBookService.getUnReadNum(contentEO.getSiteId(), null, contentEO.getCreateUserId());
                            pushVO.setBadge(num == null ? 0L : num);
                            //BaiduPushUtil.pushAndroidSingleMsg(bdId, contentEO.getColumnId(), contentEO.getTitle(), "您的留言已转办", msgUrl);
                            BaiduPushUtil.sendPushMsg("android", pushVO);

                        } else if (MemberEO.BDTYPE.IOS.toString().equals(type)) {
                            pushVO.setDeviceType(4);
                            pushVO.setDescription("您的留言已转办");
                            Long num = guestBookService.getUnReadNum(contentEO.getSiteId(), null, contentEO.getCreateUserId());
                            pushVO.setBadge(num == null ? 0L : num);
                            //BaiduPushUtil.pushIosSingleMsg(bdId, contentEO.getColumnId(), contentEO.getTitle(), "您的留言已办结", msgUrl);
                            BaiduPushUtil.sendPushMsg("ios", pushVO);
                        }
                    }
                }
            }
        }
        // 异步执行
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                logger.error("==============>  留言转办 <================");
                // 绑定session至当前线程中
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                sendMessge(contentEO, receiveIds, userId);
                // 关闭session
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                logger.error("=============>  留言转办完成 <=====================");
            }
        });

        return getObject();

    }

    /**
     * 发送消息
     * @param eo
     * @param receiveIds
     * @param userId
     */
    private void sendMessge(BaseContentEO eo, String receiveIds, Long userId) {
        MessageSystemEO message = new MessageSystemEO();
        message.setSiteId(eo.getSiteId());
        message.setColumnId(eo.getColumnId());
        message.setMessageType(MessageSystemEO.TIP);
        message.setModeCode(BaseContentEO.TypeCode.guestBook.toString());
        message.setRecUserIds(receiveIds);
        // message.setRecOrganIds(videoEO.getCreateOrganId() + "");
        // message.setCreateOrganId(videoEO.getCreateOrganId());
        message.setCreateUserId(userId);
        // message.setLink("/videoNews/videoPlayer?id="+eo.getId());
        message.setResourceId(eo.getId());
        message.setTitle("留言转办");
        message.setContent(eo.getTitle());
        message.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
        sendMessage(message);
    }


    /**
     * 批量发布留言
     * @param ids
     * @param siteId
     * @param columnId
     * @param type
     * @return
     */
    @RequestMapping("batchPublish")
    @ResponseBody
    public Object batchPublish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long siteId, Long columnId, Integer type) {
        List<BaseContentEO> list = contentService.getEntities(BaseContentEO.class, ids);
        if (list != null && list.size() > 0) {
            for (BaseContentEO eo : list) {
                if (eo.getPublishDate() == null) {
                    eo.setPublishDate(new Date());
                }
                eo.setIsPublish(2);
                if(type!=null&&type==1){
                    SysLog.log("发布内容 >> ID：" + eo.getId()+",标题："+eo.getTitle(),
                            "BaseContentEO", CmsLogEO.Operation.Update.toString());
                }else if(type!=null&&type==0){
                    SysLog.log("取消发布内容 >> ID：" + eo.getId()+",标题："+eo.getTitle(),
                            "BaseContentEO", CmsLogEO.Operation.Update.toString());
                }
            }
            contentService.updateEntities(list);
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class, list);
        boolean rel = false;
        if(type==1){
            rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.PUBLISH.value()), 1);
        }else{
            rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.UNPUBLISH.value()), 0);
        }
        return getObject(rel);

    }


    /**
     * 单个留言发布
     * @param id
     * @return
     */
    @RequestMapping("changePublish")
    @ResponseBody
    public Object changePublish(Long id) {
        BaseContentEO eo = contentService.getEntity(BaseContentEO.class, id);
        Integer isPublish = eo.getIsPublish();
        boolean rel = false;
        if (isPublish == 1) {// 取消发布
            eo.setIsPublish(2);
            contentService.updateEntity(eo);
            rel = sendMessage(new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()));
            SysLog.log("取消发布内容 >> ID：" + eo.getId()+",标题："+eo.getTitle(),
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());
            if (rel) {
                return getObject(0);
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "取消发布失败！");
            }
        } else {// 发布
            eo.setPublishDate(new Date());
            eo.setIsPublish(2);
            contentService.saveEntity(eo);
            rel = sendMessage(new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()));
            SysLog.log("发布内容 >> ID：" + eo.getId()+",标题："+eo.getTitle(),
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());
            if (rel) {
                return getObject(1);
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "发布失败！");
            }
        }

    }

    /**
     * 删除留言
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(Long id) {
        BaseContentEO contentEO = getEntity(BaseContentEO.class, id);
        if (contentEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言不存在！");
        }
        CacheHandler.delete(BaseContentEO.class, contentEO);
        // 只删除主表（假删）
        contentService.delete(BaseContentEO.class, id);
        MessageSenderUtil.publishContent(
            new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()), 2);
        SysLog.log("删除留言 >> ID：" + id+",留言标题："+contentEO.getTitle(),
                "GuestBookEO", CmsLogEO.Operation.Delete.toString());
        return getObject();
    }


    /**
     * 批量删除留言
     * @param ids
     * @return
     */
    @RequestMapping("batchDelete")
    @ResponseBody
    public Object batchDelete(@RequestParam("ids[]") Long[] ids) {
        if(ids==null||ids.length<1){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的项！");
        }
        BaseContentEO baseContentEO=getEntity(BaseContentEO.class,ids[0]);
        // 批量删除主表（假删）
        contentService.delete(BaseContentEO.class, ids);
        List<BaseContentEO> list = contentService.getEntities(BaseContentEO.class, ids);
        if (list != null && list.size() > 0) {
            for (BaseContentEO contentEO : list) {
                CacheHandler.delete(BaseContentEO.class, contentEO);
                SysLog.log("删除留言 >> ID：" + contentEO.getId()+",留言标题："+contentEO.getTitle(),
                        "GuestBookEO", CmsLogEO.Operation.Delete.toString());
            }
        }
        MessageSenderUtil.publishContent(
                new MessageStaticEO(baseContentEO.getSiteId(), baseContentEO.getColumnId(), ids).setType(MessageEnum.UNPUBLISH.value()), 2);
        // guestBookService.batchDelete(ids);
        return getObject();
    }


    /**
     * 打开转办记录页面
     * @param id
     * @param map
     * @return
     */
    @RequestMapping("queryForwardRecord")
    public String queryForwardRecord(Long id, ModelMap map) {
        map.put("id", id);
        return "/content/guestbook_record";
    }

    /**
     * 获取转办记录数据
     * @param pageIndex
     * @param pageSize
     * @param id
     * @return
     */
    @RequestMapping("getRecord")
    @ResponseBody
    public Object getRecord(Long pageIndex, Integer pageSize, Long id) {
        return getObject(forwardRecordService.getRecord(pageIndex, pageSize, id));

    }


    /**
     * 打开修改页面
     * @param id
     * @param columnId
     * @param model
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("modify")
    public String modify(Long id, Long columnId, Model model) throws UnsupportedEncodingException {
        model.addAttribute("baseContentId", id);
        model.addAttribute("columnId", columnId);
        model.addAttribute("siteId", LoginPersonUtil.getSiteId());
        Integer recType = null;
        List<ContentModelParaVO> recList = ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(), null);
        if (recList != null && recList.size() > 0) {
            recType = recList.get(0).getRecType();
        }
        model.addAttribute("recType", recType);
        Integer codeType = 1;
        List<ContentModelParaVO> codeList = ModelConfigUtil.getGuestBookType(columnId, LoginPersonUtil.getSiteId());
        if (codeList == null || codeList.size() == 0) {
            codeList = null;
            codeType = 0;
        }
        if (recList == null || recList.size() == 0) {
            recList = null;
        }
        model.addAttribute("recList", recList);
        model.addAttribute("codeList", codeList);
        model.addAttribute("codeType", codeType);
        return "/content/guestbook_modify";
    }

    /**
     * 打开修改页面(工作流专用)
     * @param id
     * @param columnId
     * @param model
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("editGuestBook")
    public String editGuestBook(Long id, Long columnId, Model model) throws UnsupportedEncodingException {
        model.addAttribute("baseContentId", id);

        if (AppUtil.isEmpty(columnId)) {
            BaseContentEO contentEO = getEntity(BaseContentEO.class, id);
            columnId = contentEO.getColumnId();
        }

        model.addAttribute("columnId", columnId);
        model.addAttribute("siteId", LoginPersonUtil.getSiteId());
        Integer recType = null;
        List<ContentModelParaVO> recList = ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(), null);
        if (recList != null && recList.size() > 0) {
            recType = recList.get(0).getRecType();
        }
        model.addAttribute("recType", recType);
        Integer codeType = 1;
        List<ContentModelParaVO> codeList = ModelConfigUtil.getGuestBookType(columnId, LoginPersonUtil.getSiteId());
        if (codeList == null || codeList.size() == 0) {
            codeList = null;
            codeType = 0;
        }
        if (recList == null || recList.size() == 0) {
            recList = null;
        }
        model.addAttribute("recList", recList);
        model.addAttribute("codeList", codeList);
        model.addAttribute("codeType", codeType);

        List<ContentModelParaVO> statusList = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        if (statusList == null || statusList.size() <= 0) {
            model.addAttribute("status", 0);
        } else {
            model.addAttribute("status", 1);
        }
        model.addAttribute("statusList", statusList);

        return "/content/guestbook_edit";
    }


    /**
     * 修改后保存
     * @param vo
     * @return
     */
    @RequestMapping("modifySave")
    @ResponseBody
    public Object modifySave(GuestBookEditVO vo) {
        if (StringUtils.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言标题为空！");
        }
        if (StringUtils.isEmpty(vo.getPersonName())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人姓名为空！");
        }

        if (vo.getIsPublic() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择是否公开");
        }
        if (AppUtil.isEmpty(vo.getAddDate())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言日期为空！");
        }
        if (!AppUtil.isEmpty(vo.getReplyDate())) {
            if (vo.getAddDate().after(vo.getReplyDate())) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "留言日期应小于回复日期！");
            }
        }
        GuestBookEO eo = guestBookService.getEntity(GuestBookEO.class, vo.getId());
        BaseContentEO eo1 = contentService.getEntity(BaseContentEO.class, vo.getBaseContentId());
        eo.setGuestBookContent(vo.getGuestBookContent());
        eo.setPersonIp(vo.getPersonIp());
        eo.setPersonName(vo.getPersonName());
        eo.setAddDate(vo.getAddDate());
        eo.setIsPublic(vo.getIsPublic());
        eo.setIsPublicInfo(vo.getIsPublicInfo());
        if (vo.getRecType() != null) {
            if (vo.getRecType() == 0) {
                eo.setReceiveId(vo.getReceiveId());
            } else if (vo.getRecType() == 1) {
                eo.setReceiveUserCode(vo.getReceiveUserCode());
            }
        }
        if (!StringUtils.isEmpty(vo.getClassCode())) {
            eo.setClassCode(vo.getClassCode());
        }
        eo1.setTitle(vo.getTitle());
        guestBookService.updateEntity(eo);
        contentService.updateEntity(eo1);
        CacheHandler.saveOrUpdate(BaseContentEO.class, eo1);
        SysLog.log("修改留言 >> ID：" + vo.getId(),
                "GuestBookEO", CmsLogEO.Operation.Update.toString());
        return getObject();

    }


    /**
     * 留言管理统计概况
     * @param i
     * @return
     */
    @RequestMapping("count")
    @ResponseBody
    public Object count(Integer i) {
        Long count = guestBookService.count(i);
        return getObject(count);

    }


    /**
     * 返回当前栏目留言条数
     * @param columnId
     * @return
     */
    @RequestMapping("countData")
    public Long countData(Long columnId) {
        return guestBookService.countData(columnId);
    }

    /**
     * 去往留言新增页面
     * @param columnId
     * @param pageIndex
     * @param model
     * @return
     */
    @RequestMapping("add")
    public String add(Long columnId, Long pageIndex, Model model) {
        if (columnId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目为空");
        }
        Integer recType = null;
        List<ContentModelParaVO> recList = ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(), null);
        if (recList != null && recList.size() > 0) {
            recType = recList.get(0).getRecType();
        }
        model.addAttribute("recType", recType);
        Integer codeType = 1;
        List<ContentModelParaVO> codeList = ModelConfigUtil.getGuestBookType(columnId, LoginPersonUtil.getSiteId());
        if (codeList == null || codeList.size() == 0) {
            codeList = null;
            codeType = 0;
        }
        if (recList == null || recList.size() == 0) {
            recList = null;
        }
        model.addAttribute("recList", recList);
        model.addAttribute("codeList", codeList);
        model.addAttribute("columnId", columnId);
        model.addAttribute("codeType", codeType);
        model.addAttribute("pageIndex", pageIndex);
        return "/content/guestbook_add";
    }

    /**
     * 给新增的留言初始化数据
     * @return
     */
    @RequestMapping("addVO")
    @ResponseBody
    public Object addVO() {
        GuestBookEditVO vo = new GuestBookEditVO();
        vo.setSiteId(LoginPersonUtil.getSiteId());
        vo.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
        vo.setAddDate(new Date());
        return getObject(vo);
    }

    /**
     * 后台新增留言
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "saveVO")
    @ResponseBody
    public Object saveVO(GuestBookEditVO vo) {
        if (StringUtils.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言标题为空！");
        }
        if (StringUtils.isEmpty(vo.getPersonName())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人姓名为空！");
        }
        if (vo.getPersonPhone() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人联系电话为空！");
        }

        if (vo.getReceiveId() != null) {
            List<TreeNodeVO> list = optService.getUserAuthForColumn(vo.getReceiveId(), vo.getColumnId());
            if (list == null || list.size() <= 0) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该单位下没有办理人员");
            }
            String strIds = "";
            for (int i = 0; i < list.size(); i++) {
                strIds += list.get(i).getUserId() + ",";
            }
            if (StringUtils.isEmpty(strIds)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该单位下没有办理人员");
            }
        }
        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, vo);
        GuestBookEO guestBookEO = new GuestBookEO();
        AppUtil.copyProperties(guestBookEO, vo);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        guestBookEO.setPersonIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
        guestBookEO.setBaseContentId(id);
        guestBookEO.setCreateUnitId(LoginPersonUtil.getUnitId());
        DataDictVO dictVO = getItem("petition_purpose", guestBookEO.getClassCode());
        if (dictVO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言类型未配置");
        } else {
            guestBookEO.setClassName(dictVO.getKey());
        }
        guestBookService.saveGuestBook(guestBookEO, vo.getSiteId(), vo.getColumnId());
        return getObject(1);
    }

    /**
     * 获取内容模型中配置的留言类型
     * @param columnId
     * @return
     */
    @RequestMapping("getClassCode")
    @ResponseBody
    public Object getClassCode(Long columnId) {
        List<ContentModelParaVO> list = null;
        if (columnId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目为空");
        } else {
            list = ModelConfigUtil.getGuestBookType(columnId, LoginPersonUtil.getSiteId());
        }
        return getObject(list);
    }

    /**
     * 获取某栏目下指定天数内未回复的留言
     * @param columnId
     * @param day
     * @return
     */
    @RequestMapping("getUnReplyCount")
    @ResponseBody
    public Object getUnReplyCount(Long columnId, int day) {
        Long count = guestBookService.getUnReplyCount(columnId, day);
        return getObject(count);
    }

    /**
     * 获取某站点下的所有留言
     * @param siteId
     * @return
     */
    @RequestMapping("getGuestBookBySiteId")
    @ResponseBody
    public Object getGuestBookBySiteId(Long siteId) {
        List<GuestBookEditVO> list = guestBookService.getGuestBookBySiteId(siteId);
        return getObject(list);
    }

    /**
     * 根据编号和密码查询留言
     * @param randomCode
     * @param docNum
     * @return
     */
    @RequestMapping("getByRandomCode")
    @ResponseBody
    public Object getByRandomCode(String randomCode, String docNum) {
        GuestBookEditVO editVO = guestBookService.searchEO(randomCode, docNum, LoginPersonUtil.getSiteId());
        return getObject(editVO);
    }

    // 打开修改页面
    @RequestMapping("doPrint")
    public String doPrint(Long id, Long pageIndex, Integer isPrint, Model model) {
        GuestBookEditVO vo = guestBookService.getVO(id);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("vo", vo);
        model.addAttribute("contentId", id);
        model.addAttribute("isPrint",isPrint);
        Date date=vo.getAddDate();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str=sdf.format(date);
        model.addAttribute("addDate",str);
        IndicatorEO indicatorEO=CacheHandler.getEntity(IndicatorEO.class,vo.getColumnId());
        if(indicatorEO!=null){
            model.addAttribute("columnName",indicatorEO.getName());
        }
        return "/content/guestbook_print";
    }

    /**
     * 获取在内容模型中配置的办理状态
     * @param columnId
     * @return
     */
    @RequestMapping("getStatusList")
    @ResponseBody
    public Object getStatusList(Long columnId) {
        List<ContentModelParaVO> list = null;
        if (columnId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目为空");
        } else {
            list = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        }
        return getObject(list);
    }

    /**
     * 保存word文档
     * @param id
     * @param model
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping("exportWord")
    public Object exportWord(Long id, Model model, HttpServletResponse response) {

        GuestBookEditVO vo = guestBookService.getVO(id);
        byte[] bt = null;
        try {
            bt = DowloadUtil.Write(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fileName = vo.getTitle() + ".doc";
        fileName = fileName.replaceAll("\\s", "");
        response.setContentType("application/octet-stream");
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO-8859-1"));
            response.getOutputStream().write(null == bt ? new byte[0] : bt);
            response.flushBuffer();
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ajaxOk();
    }

}
