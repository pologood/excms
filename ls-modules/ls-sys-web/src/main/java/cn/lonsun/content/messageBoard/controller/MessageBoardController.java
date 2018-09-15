package cn.lonsun.content.messageBoard.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.baidu.BaiduPushVO;
import cn.lonsun.base.util.TreeNodeUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.content.controller.KnowledgeBaseController;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IKnowledgeBaseService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardApplyEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.*;
import cn.lonsun.content.messageBoard.service.*;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.*;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.ldap.internal.util.Constants;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.statistics.MessageBoardListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.util.DataDictionaryUtil.getItem;

/**
 * @author hujun
 * @ClassName: GuestBookController
 * @Description: 留言管理控制器
 * @date 2015年10月26日 下午1:30:34
 */
@Controller
@RequestMapping(value = "messageBoard")
public class MessageBoardController extends BaseController {

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IMessageBoardReplyService replyService;

    @Autowired
    private IMessageBoardApplyService applyService;

    @Autowired
    private IMessageBoardForwardService forwardRecordService;
    @Autowired
    private IBaseContentService contentService;
    @Autowired
    private IContentModelService contModelService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IMessageBoardOperationService operationService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IKnowledgeBaseService knowledgeBaseService;

    @Autowired
    private KnowledgeBaseController knowledgeBaseController;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private IIndicatorService indicatorService;
    static Logger logger = LoggerFactory.getLogger(MessageBoardController.class);

    // 进入页面
    @RequestMapping(value = "index")
    public String guestBookList() {
        return "/content/messageBoard/message_board";
    }

    @RequestMapping(value = "getRec")
    @ResponseBody
    public Object getRec(Long columnId) {
        List<ContentModelParaVO> recList = ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(), null);
        List<ContentModelParaVO> codeList = ModelConfigUtil.getMessageBoardType(columnId, LoginPersonUtil.getSiteId());
        if (recList == null || recList.size() <= 0) {
            return codeList;
        } else {
            if (codeList != null && codeList.size() > 0) {
                recList.addAll(codeList);
            }
            return recList;
        }

    }

    // 获取留言分页
    @RequestMapping(value = "getPage")
    @ResponseBody
    public Object getPage(MessageBoardPageVO pageVO) {
        return getObject(messageBoardService.getPage(pageVO));
    }

    //获取留言退回收回记录分页
    @RequestMapping(value = "getOperationPage")
    @ResponseBody
    public Object getOperationPage(MessageBoardOperationVO pageVO) {
        return getObject(operationService.getPage(pageVO));
    }

    //获取审核记录分页
    @RequestMapping(value = "getApplyPage")
    @ResponseBody
    public Object getApplyPage(MessageBoardApplyVO pageVO) {
        return getObject(applyService.getPage(pageVO));
    }

    // 打开回复页面
    @RequestMapping(value = "messageBoardReply")
    public String messageBoardReply(Long id, Long messageBoardId, Long columnId, Integer isSuper, Model model) {
        Integer recType = null;
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(columnId, LoginPersonUtil.getSiteId());
        if (configVO != null) {
            recType = configVO.getRecType();
        }
        List<ContentModelParaVO> statusList = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        if (statusList == null || statusList.size() <= 0) {
            model.addAttribute("status", 0);
        } else {
            model.addAttribute("status", 1);
        }


        if (null == messageBoardId) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        Map<String, Object> parmas = new HashMap<String, Object>();
        Long userId = LoginPersonUtil.getUserId();
        parmas.put("messageBoardId", messageBoardId);
        parmas.put("createUserId", userId);
        parmas.put("isSuper", isSuper);
        parmas.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        MessageBoardReplyEO replyEO = replyService.getEntity(MessageBoardReplyEO.class, parmas);
        if (replyEO == null) {
            model.addAttribute("dealStatus", "");
        }else{
            model.addAttribute("dealStatus", replyEO.getDealStatus());
        }
        model.addAttribute("statusList", statusList);
        model.addAttribute("baseContentId", id);
        model.addAttribute("messageBoardId", messageBoardId);
        model.addAttribute("recType", recType);
        model.addAttribute("isSuper", isSuper);
        return "/content/messageBoard/message_board_reply";
    }

    // 打开申请延期页面
    @RequestMapping(value = "messageBoardApply")
    public String messageBoardApply(Long messageBoardId, Model model) {
        model.addAttribute("messageBoardId", messageBoardId);
        return "/content/messageBoard/message_board_apply";
    }

    // 打开收回页面
    @RequestMapping(value = "messageBoardRecover")
    public String messageBoardRecover(Long messageBoardId, Model model) {
        model.addAttribute("messageBoardId", messageBoardId);
        return "/content/messageBoard/message_board_recover";
    }


    // 打开图片
    @RequestMapping(value = "queryImage")
    public String queryImage(String image, Model model) {
        model.addAttribute("image", image);
        return "/content/messageBoard/message_board_image";
    }

    @RequestMapping("getApplyVO")
    @ResponseBody
    public Object getApplyVO(Long id) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("messageBoardId", id);
        map.put("createUserId", LoginPersonUtil.getUserId());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        MessageBoardApplyEO applyEO = null;
        applyEO = applyService.getEntity(MessageBoardApplyEO.class, map);
        if (applyEO == null) {
            applyEO = new MessageBoardApplyEO();
        }
        MessageBoardApplyVO vo = new MessageBoardApplyVO();
        AppUtil.copyProperties(vo, applyEO);
        return getObject(vo);
    }

    //获取已分配条数和未分配条数
    @RequestMapping("getCountVO")
    @ResponseBody
    public Object getCountVO(Long columnId) {
        if (ChuZhouMessageBoardOpenUtil.isOpen == 1&&LoginPersonUtil.getUnitId().equals(ChuZhouMessageBoardOpenUtil.mayorUnitId)) {
            columnId = ChuZhouMessageBoardOpenUtil.mayorColumnId;
        }

        if (ChuZhouMessageBoardOpenUtil.isOpen == 1&&LoginPersonUtil.getUnitId().equals(ChuZhouMessageBoardOpenUtil.secretaryUnitId)) {
            columnId = ChuZhouMessageBoardOpenUtil.secretaryColumnId;
        }

        if (ChuZhouMessageBoardOpenUtil.isOpen == 1&&LoginPersonUtil.getUnitId().equals(ChuZhouMessageBoardOpenUtil.departmentUnitId)) {
            columnId = ChuZhouMessageBoardOpenUtil.departmentColumnId;
        }


        Map<String, Object> map = new HashMap<String, Object>();
        Long noAssignCount = messageBoardService.getCount("noAssign", columnId);//未分配的条数
        Long assignedCount = messageBoardService.getCount("assigned", columnId);//已分配的条数
        Long backCount = operationService.getCount("back");//退回条数
        Long recoverCount = operationService.getCount("recover");//收回条数
        Long applyCount = applyService.getCount("");//已审核条数
        Long approvalCount = applyService.getCount(MessageBoardApplyEO.DisposeStatus.disposeWait.toString());//待审核条数
        map.put("assignedCount", assignedCount);
        map.put("noAssignCount", noAssignCount);
        map.put("backCount", backCount);
        map.put("recoverCount", recoverCount);
        map.put("applyCount", applyCount);
        map.put("approvalCount", approvalCount);
        return getObject(map);
    }

    // 申请延期
    @RequestMapping("apply")
    @ResponseBody
    public Object apply(MessageBoardApplyVO messageBoardApplyVO) {

        if (messageBoardApplyVO.getApplyDays() != null) {

            MessageBoardApplyEO eo = applyService.apply(messageBoardApplyVO);
            if (eo == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "保存出错");
            }
        }
        return getObject();
    }

    // 打開退回页面
    @RequestMapping(value = "messageBoardGoBack")
    public Object messageBoardGoBack(Long id, Model model) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        model.addAttribute("messageBoardId", id);
        return "/content/messageBoard/message_board_go_back";
    }


    // 退回
    @RequestMapping("goBack")
    @ResponseBody
    public Object goBack(MessageBoardForwardVO forwardVO) {

        if (null == forwardVO.getMessageBoardId()) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("messageBoardId", forwardVO.getMessageBoardId());
        map.put("receiveOrganId", LoginPersonUtil.getUnitId());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        MessageBoardForwardEO forwardEO = forwardRecordService.getEntity(MessageBoardForwardEO.class, map);
        if (forwardEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该条留言没有分配");
        } else {
            forwardVO.setId(forwardEO.getId());
            forwardRecordService.goBack(forwardVO);
        }
        return getObject();
    }


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
        List<MessageBoardEO> list = messageBoardService.getEntities(MessageBoardEO.class, map);
        if (null == list || list.size() <= 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言为空");
        }
        MessageBoardEO eo = list.get(0);
        MessageBoardEditVO vo = new MessageBoardEditVO();
        AppUtil.copyProperties(vo, eo);
        vo.setIsPublish(contentEO.getIsPublish());
        vo.setTitle(contentEO.getTitle());
        vo.setColumnId(contentEO.getColumnId());
        vo.setSiteId(contentEO.getSiteId());
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
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

    @RequestMapping("getReplyVO")
    @ResponseBody
    public Object getReplyVO(Long id,Integer isEdit,Long replyId) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        MessageBoardReplyEO replyEO=new MessageBoardReplyEO();
        if(isEdit!=null&&isEdit==1&&replyId!=null){
            replyEO = replyService.getEntity(MessageBoardReplyEO.class, replyId);
        }else{
            replyEO.setReplyDate(new Date());
        }
        MessageBoardReplyVO vo = new MessageBoardReplyVO();
        AppUtil.copyProperties(vo, replyEO);
        return getObject(vo);
    }

    // 回复留言
    @RequestMapping("reply")
    @ResponseBody
    public Object reply(MessageBoardReplyVO messageBoardReplyVO) {

        if (null == messageBoardReplyVO.getMessageBoardId()) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, messageBoardReplyVO.getMessageBoardId());
        if(messageBoardEO ==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        messageBoardReplyVO.setBaseContentId(messageBoardEO.getBaseContentId());
        if (!StringUtils.isEmpty(messageBoardReplyVO.getReplyContent())) {
            MessageBoardReplyEO eo = replyService.reply(messageBoardReplyVO);
            if (eo == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "保存出错");
            }
            if (eo.getDealStatus().equals("handled") || eo.getDealStatus().equals("replyed")) {
                messageBoardEO.setDealStatus(eo.getDealStatus());
                messageBoardEO.setIsRead(0);
                messageBoardEO.setReceiveUnitId(LoginPersonUtil.getUnitId());
                messageBoardEO.setReceiveUnitName(LoginPersonUtil.getUnitName());
                messageBoardEO.setReplyDate(messageBoardReplyVO.getReplyDate());
                messageBoardService.updateEntity(messageBoardEO);
            }
        }
        BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, messageBoardEO.getBaseContentId());
        if(contentEO.getIsPublish()!=null&&contentEO.getIsPublish().intValue()==1){
            MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(),
                    new Long[]{messageBoardEO.getBaseContentId()}).setType(MessageEnum.PUBLISH.value()), 1);
        }
        if (messageBoardEO.getDealStatus()!=null&&(messageBoardEO.getDealStatus().equals("handled") || messageBoardEO.getDealStatus().equals("replyed"))) {
            Integer recourceType = messageBoardEO.getResourceType();
            if (contentEO == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错！");
            }
            if (recourceType != null && recourceType == 3) {
                if (contentEO.getCreateUserId() != null) {
                    MemberEO memberEO = memberService.getEntity(MemberEO.class, contentEO.getCreateUserId());
                    if (memberEO != null) {
                        String bdId = memberEO.getBduserid();
                        if (!StringUtils.isEmpty(bdId)) {
                            String type = memberEO.getBdtype();
                            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, contentEO.getSiteId());
                            String msgUrl = indicatorEO.getUri() + "/mobile/messageBoardShow?id=" + contentEO.getId();
                            BaiduPushVO pushVO = new BaiduPushVO();
                            pushVO.setMsgCode(BaseContentEO.TypeCode.messageBoard.toString());
                            pushVO.setChannelId(bdId);
                            pushVO.setMsgColumnId(contentEO.getColumnId());
                            pushVO.setTitle(contentEO.getTitle());
                            pushVO.setMsgUrl(msgUrl);
                            pushVO.setPushType("single");
                            if (MemberEO.BDTYPE.ANDROID.toString().equals(type)) {
                                pushVO.setDeviceType(3);
                                pushVO.setDescription("您的留言已转办");
                                Long num = messageBoardService.getUnReadNum(contentEO.getSiteId(), null, contentEO.getCreateUserId());
                                pushVO.setBadge(num == null ? 0L : num);
                                //BaiduPushUtil.pushAndroidSingleMsg(bdId, contentEO.getColumnId(), contentEO.getTitle(), "您的留言已转办", msgUrl);
                                BaiduPushUtil.sendPushMsg("android", pushVO);

                            } else if (MemberEO.BDTYPE.IOS.toString().equals(type)) {
                                pushVO.setDeviceType(4);
                                pushVO.setDescription("您的留言已办结");
                                Long num = messageBoardService.getUnReadNum(contentEO.getSiteId(), null, contentEO.getCreateUserId());
                                pushVO.setBadge(num == null ? 0L : num);
                                //BaiduPushUtil.pushIosSingleMsg(bdId, contentEO.getColumnId(), contentEO.getTitle(), "您的留言已办结", msgUrl);
                                BaiduPushUtil.sendPushMsg("ios", pushVO);
                            }
                        }
                    }
                }
            }
        }
        IndicatorEO indicatorEO = indicatorService.getEntity(IndicatorEO.class, contentEO.getColumnId());
        if (indicatorEO != null) {
            SysLog.log(indicatorEO.getName() + "：回复（" + contentEO.getTitle() + "），办理单位（" + LoginPersonUtil.getOrganName() + "）", "MessageBoardReplyEO", CmsLogEO.Operation.Add.toString());
        }
        return getObject();
    }

    // 打开转办页面
    @RequestMapping(value = "messageBoardForward")
    public String messageBoardForward(Long id, Long columnId, Model model) throws UnsupportedEncodingException {
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(columnId, LoginPersonUtil.getSiteId());
        if(configVO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "内容模型配置为空");
        }
        model.addAttribute("isLocal", configVO.getIsLocalUnit());
        model.addAttribute("isTurn", configVO.getTurn());
        if (configVO.getIsLocalUnit() != null && configVO.getIsLocalUnit() == 1) {
            List<DataDictVO> localList = DataDictionaryUtil.getItemList("local_unit", LoginPersonUtil.getSiteId());
            model.addAttribute("localList", localList);
        }
        model.addAttribute("recType", configVO.getRecType());
        Integer recType = configVO.getRecType()==null?2:configVO.getRecType();
        Integer isTurn = configVO.getTurn()==null?0:configVO.getTurn();
        List<MessageBoardForwardVO> forwardVOList = forwardRecordService.getAllForwardByMessageBoardId(id);
        String receiveOrganIds = "";
        String receiveUnitNames = "";
        String recUserIds = "";
        String recUserNames = "";
        if (recType == 0||isTurn==1) {//选择受理单位
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    receiveOrganIds = receiveOrganIds + forwardVOList.get(i).getReceiveOrganId();
                    receiveUnitNames = receiveUnitNames + forwardVOList.get(i).getReceiveUnitName();
                } else {
                    receiveOrganIds = receiveOrganIds + ',' + forwardVOList.get(i).getReceiveOrganId();
                    receiveUnitNames = receiveUnitNames + ',' + forwardVOList.get(i).getReceiveUnitName();
                }
            }
        } else {//选择受理人
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    recUserIds = recUserIds + forwardVOList.get(i).getReceiveUserCode();
                    recUserNames = recUserNames + forwardVOList.get(i).getReceiveUserName();
                } else {
                    recUserIds = recUserIds + ',' + forwardVOList.get(i).getReceiveUserCode();
                    recUserNames = recUserNames + ',' + forwardVOList.get(i).getReceiveUserName();
                }
            }
        }
        model.addAttribute("recUserIds", recUserIds);
        model.addAttribute("recUserNames", recUserNames);
        model.addAttribute("receiveOrganIds", receiveOrganIds);
        model.addAttribute("receiveUnitNames", receiveUnitNames);
        model.addAttribute("messageBoardId", id);
        model.addAttribute("columnId", columnId);
        return "/content/messageBoard/message_board_forward";
    }


    /**
     * 跳转到选择单位
     *
     * @return
     */
    @RequestMapping("toSelectColumnTree")
    public String toSelectColumnTree(@RequestParam(value = "columnId", required = false, defaultValue = "") Long columnId,
                                     @RequestParam(value = "recType", required = false, defaultValue = "") Integer recType,
                                     @RequestParam(value = "isTurn", required = false, defaultValue = "") Integer isTurn,
                                     @RequestParam(value = "receiveUnitIds", required = false, defaultValue = "") String receiveUnitIds,
                                     @RequestParam(value = "temp", required = false, defaultValue = "") String temp,
                                     @RequestParam(value = "recUserIds", required = false, defaultValue = "") String recUserIds,
                                     @RequestParam(value = "usertemp", required = false, defaultValue = "") String usertemp,
                                     Model model) {
        model.addAttribute("columnId", columnId);
        model.addAttribute("temp", temp);
        model.addAttribute("receiveUnitIds", receiveUnitIds);
        model.addAttribute("usertemp", usertemp);
        model.addAttribute("recUserIds", recUserIds);
        model.addAttribute("isTurn",isTurn);
        if (recType == 0||(isTurn!=null&&isTurn==1)) {
            return "/content/messageBoard/message_board_unit_choose";
        } else {
            return "/content/messageBoard/message_board_user_choose";
        }
    }
    /**
     * 获取查询单位树
     *
     * @return
     */
    @RequestMapping("getMessageQueryTree")
    @ResponseBody
    public Object getMessageQueryTree( Long[] organIds, @RequestParam(value = "columnId", required = false, defaultValue = "") Long columnId) {
        List nodes = messageBoardService.getMessageTree(organIds, columnId);
        return getObject(nodes);
    }

    /**
     * 跳转到选择人员
     *
     * @return
     */
    @RequestMapping("getMessagePersonTree")
    @ResponseBody
    public Object getMessagePersonTree(Long siteId, @RequestParam(value = "columnId", required = false, defaultValue = "") Long columnId,
                                 Integer isTurn,Model model) {
        List<ContentModelParaVO> list = contModelService.getParam(columnId, LoginPersonUtil.getSiteId(), isTurn);
        if (list != null && list.size() > 0) {
            Integer recType = list.get(0).getRecType();
            model.addAttribute("recType", recType);
        }
        model.addAttribute("isTurn", isTurn);
        return getObject(list);
    }
    /**
     * 获取到选择单位
     */
    @RequestMapping("getMessageUnitTree")
    @ResponseBody
    public Object getMessageUnitTree( Long[] organIds,Long parentId,@RequestParam(defaultValue="false")Boolean isContainsExternal , @RequestParam(value = "columnId", required = false, defaultValue = "") Long columnId) {
        //默认为选人界面
        String[] nodeTypes = TreeNodeUtil.getNodeTypes(3);
        List<TreeNodeVO> nodes = null;
        if(parentId!=null){
            // nodes = ldapFacadeService.getSubNodes(isContainsExternal,nodeTypes,parentId,null);//获取该父节点下所有子节点修改成获取内容模型中的子节点
        }else{
            if(organIds!=null&&organIds.length<=0){
                organIds = null;
            }
            //所有一级节点改为内容模型中的一级节点

            //如果为空，那么默认认为不显示外单位
            if(isContainsExternal==null){
                isContainsExternal = Boolean.FALSE;
            }
            // 入参验证
            if (nodeTypes == null || nodeTypes.length <= 0) {
                // 如果参数错误，那么统一抛出IllegalArgumentException
                throw new IllegalArgumentException();
            }

            String parentDn = null;
            //范围控制
            if (organIds == null || organIds.length <= 0) {
                parentDn = Constants.ROOT_DN;
            }
            // 查询
            //如果不显示外平台，那么添加isExternalOrgan过滤
            List<OrganEO> organs = new ArrayList<OrganEO>();
            if(organIds==null||organIds.length<=0){
                //获取内容模型中的单位
                List<ContentModelParaVO> list = contModelService.getParam(columnId, LoginPersonUtil.getSiteId(), 1);
                //根据栏目id获取当前栏目的模型，进而获取其绑定的接收人
                //ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
                if(list != null){
                    for(int i = 0 ;i< list.size();i++){
                        OrganEO organ = organService.getEntity(OrganEO.class,list.get(i).getRecUnitId());
                        organs.add(organ);
                    }
                }
            }else{
                organs = organService.getOrgans(organIds, isContainsExternal);
            }
            List<TreeNodeVO> nodes2 = new ArrayList<TreeNodeVO>(organs.size());
            for (OrganEO organ : organs) {
                //说明ldap和db中都存在
                if(organ!=null){
                    TreeNodeVO node = getOrganTreeNode(nodeTypes, organ);
                    nodes2.add(node);
                }
            }
            nodes = nodes2;
        }
        //支持的节点类型
        return getObject(nodes);
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
    // 保存转办记录
    @RequestMapping("forward")
    @ResponseBody
    public Object forward(Long messageBoardId, String receiveUnitIds, String recUnitNames, String recUserIds, String recUserNames,String localUnitId,
                           Integer isLocal, String attachId, String attachName, String remarks, Integer defaultDays, Integer recType,Integer isTurn) {
        if (isLocal != null && isLocal == 1 && StringUtils.isEmpty(localUnitId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择流转区域");
        }
        if(recType != null && recType == 0 && StringUtils.isEmpty(receiveUnitIds)){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择流转单位");
        }else if(recType != null && recType == 1){
            if(isTurn==null){
                if(StringUtils.isEmpty(recUserIds)){
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择流转人");
                }
            }else{
                if(StringUtils.isEmpty(receiveUnitIds)){
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择流转单位");
                }
            }
        }
        MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, messageBoardId);
        final BaseContentEO contentEO = getEntity(BaseContentEO.class, messageBoardEO.getBaseContentId());
        if ((recType != null && recType == 0)||(isTurn!=null&&isTurn==1)) {

            String[] unitIds = receiveUnitIds.split(",");
            String[] unitNames = recUnitNames.split(",");
            Map<Long,String> personMap=new HashMap<Long, String>();

            for (int i = 0; i < unitIds.length; i++) {
                String strIds = "";
                Map<String, Object> map = new HashMap<String, Object>();
                Long unitId = Long.valueOf(unitIds[i]);
                map.put("unitId", unitId);
                map.put("recordStatus", PersonEO.RecordStatus.Normal.toString());
                if (unitId != null) {
                    List<PersonEO> list = personService.getEntities(PersonEO.class, map);
                    if (list == null || list.size() <= 0) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(), unitNames[i] + "下没有办理人员");
                    }
                    for (int j = 0; j < list.size(); j++) {
                        strIds += list.get(j).getUserId() + ",";
                    }
                    if (StringUtils.isEmpty(strIds)) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(), unitNames[i] + "下没有办理人员");
                    }
                    personMap.put(unitId,strIds);
                }
            }

            for (int i = 0; i < unitIds.length; i++) {


                Long unitId = Long.valueOf(unitIds[i]);
                String strIds = personMap.get(unitId);

                MessageBoardForwardEO forwardEO = new MessageBoardForwardEO();
                forwardEO.setMessageBoardId(messageBoardId);
                forwardEO.setRemarks(remarks);
                forwardEO.setLocalUnitId(localUnitId);
                forwardEO.setCreateDate(new Date());
                forwardEO.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
                forwardEO.setAttachId(attachId);
                forwardEO.setAttachName(attachName);
                forwardEO.setUsername(LoginPersonUtil.getPersonName());
                forwardEO.setReceiveOrganId(Long.valueOf(unitIds[i]));
                forwardEO.setReceiveUnitName(unitNames[i]);
                forwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
                if(defaultDays!=null&&defaultDays>0){
                    forwardEO.setDefaultDays(defaultDays);
                    Date dueDate=new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dueDate);
                    int day = 0;
                    while (day<forwardEO.getDefaultDays()) {
                        if (dueDate.getDay() != 6 && dueDate.getDay() != 0){
                            day++;
                        }
                        dueDate.setDate(dueDate.getDate() + 1);
                    }
                    forwardEO.setDueDate(dueDate);
                }
                forwardRecordService.forward(forwardEO,contentEO.getColumnId(),messageBoardEO);
                final String receiveIds = strIds;
                if (StringUtils.isEmpty(receiveIds)) {
                    return getObject();
                }
                final Long userId = LoginPersonUtil.getUserId();

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
            }
        }else if (recType != null && recType == 1) {
            String[] rcuserIds = recUserIds.split(",");
            String[] rcUserNames = recUserNames.split(",");
            for (int i = 0; i < rcuserIds.length; i++) {
                MessageBoardForwardEO forwardEO = new MessageBoardForwardEO();
                forwardEO.setMessageBoardId(messageBoardId);
                forwardEO.setReceiveUserCode(rcuserIds[i]);
                forwardEO.setReceiveUserName(rcUserNames[i]);
                forwardEO.setRemarks(remarks);
                forwardEO.setCreateDate(new Date());
                forwardEO.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
                forwardEO.setUsername(LoginPersonUtil.getPersonName());
                forwardEO.setAttachId(attachId);
                forwardEO.setAttachName(attachName);
                forwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
                forwardRecordService.forward(forwardEO,contentEO.getColumnId(),messageBoardEO);
            }
        }
        if(contentEO.getIsPublish()!=null&&contentEO.getIsPublish().intValue()==1){
            MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(),
                    new Long[]{messageBoardEO.getBaseContentId()}).setType(MessageEnum.PUBLISH.value()), 1);
        }
        IndicatorEO indicatorEO = (IndicatorEO)this.indicatorService.getEntity(IndicatorEO.class, contentEO.getColumnId());
        if (indicatorEO != null) {
            SysLog.log(indicatorEO.getName() + "：转办（" + contentEO.getTitle() + "），办理单位（" + LoginPersonUtil.getOrganName() + "），转办到（" + recUnitNames + ")", "MessageBoardEO", CmsLogEO.Operation.Add.toString());
        }
        return getObject();

    }

    private void sendMessge(BaseContentEO eo, String receiveIds, Long userId) {
        MessageSystemEO message = new MessageSystemEO();
        message.setSiteId(eo.getSiteId());
        message.setColumnId(eo.getColumnId());
        message.setMessageType(MessageSystemEO.TIP);
        message.setModeCode(BaseContentEO.TypeCode.messageBoard.toString());
        message.setRecUserIds(receiveIds);
        message.setCreateUserId(userId);
        message.setResourceId(eo.getId());
        message.setTitle("留言转办");
        message.setContent(eo.getTitle());
        message.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
        MessageSender.sendMessage(message);
    }


    // 批量发布留言
    @RequestMapping("batchPublish")
    @ResponseBody
    public Object batchPublish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long siteId, Long columnId, Integer type) {
        if(type==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        List<BaseContentEO> list = contentService.getEntities(BaseContentEO.class, ids);
        if (list != null && list.size() > 0) {
            for (BaseContentEO eo : list) {
                if (eo.getPublishDate() == null) {
                    eo.setPublishDate(new Date());
                }
                eo.setIsPublish(2);
            }
            contentService.updateEntities(list);
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class, list);
        boolean rel = false;
        if(type==0){
            rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.UNPUBLISH.value()), 2);
        }else if(type==1){
            rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.PUBLISH.value()), 1);

        }
        return getObject(rel);

    }

    // 单个留言发布
    @RequestMapping("changePublish")
    @ResponseBody
    public Object changePublish(Long id) {
        BaseContentEO eo = contentService.getEntity(BaseContentEO.class, id);
        Integer isPublish = eo.getIsPublish();
        boolean rel = false;
        if (isPublish == 1) {// 取消发布
            eo.setIsPublish(0);
            contentService.updateEntity(eo);
            rel = MessageSender.sendMessage(new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()));
            if (rel) {
                return getObject(0);
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "取消发布失败！");
            }
        } else {// 发布
            eo.setPublishDate(new Date());
            eo.setIsPublish(1);
            contentService.saveEntity(eo);
            rel = MessageSender.sendMessage(new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()));
            if (rel) {
                return getObject(1);
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "发布失败！");
            }
        }

    }

    // 删除留言
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(Long id) {
        BaseContentEO contentEO = getEntity(BaseContentEO.class, id);
        if (contentEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言不存在！");
        }
        Integer isPublish=contentEO.getIsPublish();
        CacheHandler.delete(BaseContentEO.class, contentEO);
        // 只删除主表（假删）
        contentService.delete(BaseContentEO.class, id);
        if(isPublish!=null&&isPublish.intValue()==1){
            MessageSenderUtil.publishContent(
                    new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()), 2);
        }
        IndicatorEO indicatorEO = (IndicatorEO)this.indicatorService.getEntity(IndicatorEO.class, contentEO.getColumnId());
        if (indicatorEO != null) {
            SysLog.log(indicatorEO.getName() + "：删除（" + contentEO.getTitle() + "），办理单位（" + LoginPersonUtil.getOrganName() + "）", "MessageBoardEO", CmsLogEO.Operation.Delete.toString());
        }
        return getObject();
    }
    // 打开回复页面
    @RequestMapping(value = "updateReply")
    public String updateReply(Long id, Long columnId, Model model) {

        Integer recType = null;
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(columnId, LoginPersonUtil.getSiteId());
        if (configVO != null) {
            recType = configVO.getRecType();
        }
        List<ContentModelParaVO> statusList = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        if (statusList == null || statusList.size() <= 0) {
            model.addAttribute("status", 0);
        } else {
            model.addAttribute("status", 1);
        }

        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "回复内容不存在");
        }

        MessageBoardReplyEO replyEO = replyService.getEntity(MessageBoardReplyEO.class, id);
        if (replyEO == null) {
            replyEO = new MessageBoardReplyEO();
            model.addAttribute("isEdit",0);
        }else{
            model.addAttribute("isEdit",1);
        }
        model.addAttribute("replyId", id);
        model.addAttribute("dealStatus", replyEO.getDealStatus());
        model.addAttribute("statusList", statusList);
        model.addAttribute("messageBoardId", replyEO.getMessageBoardId());
        model.addAttribute("recType", recType);
        model.addAttribute("isSuper", replyEO.getIsSuper());
        return "/content/messageBoard/message_board_reply";
    }


    // 删除回复内容
    @RequestMapping("deleteReply")
    @ResponseBody
    public Object deleteReply(Long id,Long replyId) {
        BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, id);
        if (contentEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言不存在！");
        }
        messageBoardService.deleteReply(id,replyId);
        if(contentEO.getIsPublish()!=null&&contentEO.getIsPublish()==1){
            MessageSenderUtil.publishContent(
                    new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()}).setType(MessageEnum.PUBLISH.value()), 1);
        }
        return getObject();
    }



    // 批量删除留言
    @RequestMapping("batchDelete")
    @ResponseBody
    public Object batchDelete(@RequestParam("ids[]") Long[] ids) {
        if(ids==null||ids.length<=0){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的留言！");
        }
        // 批量删除主表（假删）
        List<BaseContentEO> list = contentService.getEntities(BaseContentEO.class, ids);
        if (list != null && list.size() > 0) {
            Integer isPublish=0;
            for(BaseContentEO contentEO:list){
                if(contentEO!=null&&contentEO.getIsPublish()!=null&&contentEO.getIsPublish().intValue()==1){
                    isPublish=1;
                    break;
                }
            }
            contentService.delete(BaseContentEO.class, ids);
            CacheHandler.delete(BaseContentEO.class, list);
            Long siteId=list.get(0).getSiteId();
            Long columnId=list.get(0).getColumnId();
            if(isPublish!=null&&isPublish.intValue()==1){
                MessageSenderUtil.publishContent(
                        new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.UNPUBLISH.value()), 2);
            }
        }
        SysLog.log("批量删除留言 >> ID：" + ArrayFormat.ArrayToString(ids), "BaseContentEO", CmsLogEO.Operation.Delete.toString());
        return getObject();
    }

    // 打开转办记录页面
    @RequestMapping("queryForwardRecord")
    public String queryForwardRecord(Long id, ModelMap map) {
        map.put("id", id);
        return "/content/messageBoard/message_board_record";
    }

    // 获取转办记录数据
    @RequestMapping("getRecord")
    @ResponseBody
    public Object getRecord(Long pageIndex, Integer pageSize, Long id) {
        return getObject(forwardRecordService.getRecord(pageIndex, pageSize, id));

    }

    // 打开修改页面
    @RequestMapping("showCommon")
    public String showCommon(Long id, Long knowledgeBaseId,Model model) throws UnsupportedEncodingException {
        model.addAttribute("id", id);
        model.addAttribute("knowledgeBaseId", knowledgeBaseId);

        String types = "";
        //如果knowledgeBaseId不为空,获取问答知识库对象
        if (!AppUtil.isEmpty(knowledgeBaseId)) {
            ResultVO obj = (ResultVO)knowledgeBaseController.getKnowLedgeBaseById(knowledgeBaseId,
                    LoginPersonUtil.getSiteId(),6331498l,BaseContentEO.TypeCode.knowledgeBase.toString(),null);
            KnowledgeBaseVO knowledge = (KnowledgeBaseVO)obj.getData();
            types = knowledge.getCategoryCode();
        }
        //加载分类
        model.addAttribute("types", types);
        model.addAttribute("siteId", LoginPersonUtil.getSiteId());
        return "/content/messageBoard/message_board_modify2";
    }

    //取消常
    @RequestMapping("delCommon")
    @ResponseBody
    public Object delCommon(Long id,Long knowledgeBaseId) {
        KnowledgeBaseVO knowledgeBaseVO;
        MessageBoardEditVO vo = messageBoardService.getVO(id);

        //获取knowledgeBaseVO对象
        ResultVO obj = (ResultVO)knowledgeBaseController.getKnowLedgeBaseById(knowledgeBaseId,
                LoginPersonUtil.getSiteId(),6331498l,BaseContentEO.TypeCode.knowledgeBase.toString(),null);
        knowledgeBaseVO = (KnowledgeBaseVO)obj.getData();

        //删除问答知识库数据
        knowledgeBaseController.delete(new Long[]{knowledgeBaseId},new Long[]{knowledgeBaseVO.getContentId()});

        //更新messageBoard对象的 knowledgeBaseId
        MessageBoardEO eo = messageBoardService.getEntity(MessageBoardEO.class, vo.getId());
        eo.setKnowledgeBaseId(null);
        messageBoardService.updateEntity(eo);

        SysLog.log("取消常见问题"+" ：栏目（" + ColumnUtil.getColumnName(vo.getColumnId(), vo.getSiteId()) + "），标题（" + vo.getTitle()+"）", "KnowledgeBaseEO",
                CmsLogEO.Operation.Delete.toString());

        return getObject();
    }

    // 修改后保存
    @RequestMapping("commonSave")
    @ResponseBody
    public Object commonSave(Long id,Long knowledgeBaseId,String ids,String pIds,String names) {
        KnowledgeBaseEO knowledgeBaseEO;
        MessageBoardEditVO vo = messageBoardService.getVO(id);
        KnowledgeBaseVO knowledgeBaseVO;

        //如果knowledge为空,则说明没有加入到问答知识库,为新增
        if (AppUtil.isEmpty(knowledgeBaseId)) {
            knowledgeBaseVO = new KnowledgeBaseVO();
        }else {
            //更新
            ResultVO obj = (ResultVO)knowledgeBaseController.getKnowLedgeBaseById(knowledgeBaseId,
                    LoginPersonUtil.getSiteId(),6331498l,BaseContentEO.TypeCode.knowledgeBase.toString(),null);
            knowledgeBaseVO = (KnowledgeBaseVO)obj.getData();
        }

        knowledgeBaseVO.setContent(vo.getMessageBoardContent());
        //设置留言信息，取最新一条数据
        if (vo.getReplyVOList() != null && vo.getReplyVOList().size() > 0) {
            //这里的list本身就是按日期逆序排，所以直接去第一个即可
            knowledgeBaseVO.setReplyContent(vo.getReplyVOList().get(0).getReplyContent());
        }
        knowledgeBaseVO.setCategoryCode(ids);
        knowledgeBaseVO.setCategoryName(names);

        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class,vo.getColumnId());
        knowledgeBaseVO.setTitle(vo.getTitle());
        knowledgeBaseVO.setResources(columnMgrEO.getName());
        knowledgeBaseVO.setAuthor(vo.getPersonName());

        knowledgeBaseVO.setColumnId(6331498l);
        knowledgeBaseVO.setSiteId(LoginPersonUtil.getSiteId());

        knowledgeBaseEO = knowledgeBaseService.saveOrupdate(knowledgeBaseVO);

        //更新messageBoard对象的 knowledgeBaseId
        MessageBoardEO eo = messageBoardService.getEntity(MessageBoardEO.class, vo.getId());
        eo.setKnowledgeBaseId(knowledgeBaseEO.getKnowledgeBaseId());
        messageBoardService.updateEntity(eo);

        BaseContentEO contentEO = getEntity(BaseContentEO.class,knowledgeBaseEO.getContentId());
        if(knowledgeBaseVO.getIsPublish()!=null&&knowledgeBaseVO.getIsPublish()==1){
            //发布
            MessageSender.sendMessage(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()}).setType(MessageEnum.PUBLISH.value()));
        }

        SysLog.log("设置常见问题"+" ：栏目（" + ColumnUtil.getColumnName(vo.getColumnId(), vo.getSiteId()) + "），标题（" + vo.getTitle()+"）", "KnowledgeBaseEO",
                CmsLogEO.Operation.Add.toString());

        return getObject();
    }

    // 打开修改页面
    @RequestMapping("modify")
    public String modify(Long id, Long columnId, Integer type, Model model) throws UnsupportedEncodingException {
        model.addAttribute("baseContentId", id);
        model.addAttribute("columnId", columnId);
        if (type == null) {
            type = 0;
        }
        model.addAttribute("type", type);//受理中心页面跳转判断
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
        return "/content/messageBoard/message_board_modify";
    }

    // 修改后保存
    @RequestMapping("modifySave")
    @ResponseBody
    public Object modifySave(MessageBoardEditVO vo) {
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
        MessageBoardEO eo = messageBoardService.getEntity(MessageBoardEO.class, vo.getId());
        BaseContentEO eo1 = contentService.getEntity(BaseContentEO.class, vo.getBaseContentId());
        eo.setMessageBoardContent(vo.getMessageBoardContent());
        eo.setPersonIp(vo.getPersonIp());
        eo.setPersonName(vo.getPersonName());
        eo.setAddDate(vo.getAddDate());
        eo.setIsPublic(vo.getIsPublic());
        eo.setIsPublicInfo(vo.getIsPublicInfo());
        if (!StringUtils.isEmpty(vo.getClassCode())) {
            eo.setClassCode(vo.getClassCode());
        }
        eo1.setTitle(vo.getTitle());
        messageBoardService.updateEntity(eo);
        contentService.updateEntity(eo1);
        CacheHandler.saveOrUpdate(BaseContentEO.class, eo1);
        return getObject();

    }

    // 打开受理单页面
    @RequestMapping("doPrint")
    public String doPrint(Long baseContentId, Long id, Long columnId,Integer isPrint,Long pageIndex, Model model) {
        List<ContentModelParaVO> list = contModelService.getParam(columnId, LoginPersonUtil.getSiteId(), null);
        Integer recType = null;
        if (list != null && list.size() > 0) {
            recType = list.get(0).getRecType();
            model.addAttribute("recType", recType);
        }
        MessageBoardEditVO vo = messageBoardService.getVO(baseContentId);
        List<MessageBoardForwardVO> forwardVOList = forwardRecordService.getAllForwardByMessageBoardId(id);
        String receiveOrganIds = "";
        String receiveUnitNames = "";
        String recUserIds = "";
        String recUserNames = "";
        if (recType == 0) {//选择受理单位
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    receiveUnitNames = receiveUnitNames + forwardVOList.get(i).getReceiveUnitName();
                } else {
                    receiveUnitNames = receiveUnitNames + ',' + forwardVOList.get(i).getReceiveUnitName();
                }
            }
            model.addAttribute("receiveUnitNames", receiveUnitNames);
        }
        if (recType == 1) {//选择受理人
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    recUserNames = recUserNames + forwardVOList.get(i).getReceiveUserName();
                } else {
                    recUserNames = recUserNames + ',' + forwardVOList.get(i).getReceiveUserName();
                }
            }
            model.addAttribute("recUserNames", recUserNames);
        }
        model.addAttribute("isPrint",isPrint);
        model.addAttribute("isOpenAcceptance", ChuZhouMessageBoardOpenUtil.isOpenAcceptance);
        model.addAttribute("vo", vo);
        model.addAttribute("pageIndex", pageIndex);
        return "/content/messageBoard/message_board_print";
    }

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
        List<ContentModelParaVO> codeList = ModelConfigUtil.getMessageBoardType(columnId, LoginPersonUtil.getSiteId());
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
        return "/content/messageBoard/message_board_add";
    }

    @RequestMapping("addVO")
    @ResponseBody
    public Object addVO() {
        MessageBoardEditVO vo = new MessageBoardEditVO();
        vo.setSiteId(LoginPersonUtil.getSiteId());
        vo.setTypeCode(BaseContentEO.TypeCode.messageBoard.toString());
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
    public Object saveVO(MessageBoardEditVO vo) {
        if (StringUtils.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言标题为空！");
        }
        if (StringUtils.isEmpty(vo.getPersonName())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人姓名为空！");
        }
        if (vo.getPersonPhone() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人联系电话为空！");
        }

        final BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, vo);
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, vo);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setPersonIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
        messageBoardEO.setBaseContentId(id);
        messageBoardEO.setCreateUnitId(LoginPersonUtil.getUnitId());
        DataDictVO dictVO = getItem("petition_purpose", messageBoardEO.getClassCode());
        if (dictVO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言类型未配置");
        } else {
            messageBoardEO.setClassName(dictVO.getKey());
        }
        MessageBoardEO messageBoardEO1 = messageBoardService.saveMessageBoard(messageBoardEO, vo.getSiteId(), vo.getColumnId());

        if (null !=vo.getRecType() && (vo.getRecType() == 0 || vo.getRecType() == 1)) {
            String strIds = "";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("unitId", vo.getReceiveUnitId());
            map.put("recordStatus", PersonEO.RecordStatus.Normal.toString());
            if (vo.getReceiveUnitId() != null) {
                List<PersonEO> list = personService.getEntities(PersonEO.class, map);
                if (list == null || list.size() <= 0) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), vo.getReceiveUnitName() + "下没有办理人员");
                }
                for (int j = 0; j < list.size(); j++) {
                    strIds += list.get(j).getUserId() + ",";
                }
                if (StringUtils.isEmpty(strIds)) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), vo.getReceiveUnitName() + "下没有办理人员");
                }
                MessageBoardForwardEO forwardEO = new MessageBoardForwardEO();
                forwardEO.setMessageBoardId(vo.getId());
                forwardEO.setCreateDate(new Date());
                forwardEO.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
                forwardEO.setUsername(LoginPersonUtil.getPersonName());
                forwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
                forwardEO.setMessageBoardId(messageBoardEO1.getId());
                forwardEO.setCreateDate(new Date());
                forwardEO.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
                forwardEO.setUsername(LoginPersonUtil.getPersonName());
                forwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
                forwardEO.setRemarks("请快速办理");
                forwardEO.setDefaultDays(vo.getDefaultDays());
                if (vo.getRecType() == 0) {
                    forwardEO.setReceiveOrganId(vo.getReceiveUnitId());
                    forwardEO.setReceiveUnitName(vo.getReceiveUnitName());
                }
                if (vo.getRecType() == 1) {
                    forwardEO.setReceiveUserCode(vo.getReceiveUserCode());
                    forwardEO.setReceiveUserName(vo.getReceiveUserName());
                }
                forwardRecordService.saveEntity(forwardEO);

                final String receiveIds = strIds;
                if (StringUtils.isEmpty(receiveIds)) {
                    return getObject();
                }
                final Long userId = LoginPersonUtil.getUserId();

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
            }
        }

        return getObject(1);
    }


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
     * 导出多单位留言列表
     *
     * @param vo
     * @param response
     */
    @RequestMapping("exportMessageBoardList")
    public void exportMessageBoardList(StatisticsQueryVO vo, HttpServletResponse response) {
        List<MessageBoardListVO> list = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点为空");
        } else {
            vo.setSiteId(siteId);
        }
        if (!StringUtils.isEmpty(vo.getTypeCode())) {
            if (BaseContentEO.TypeCode.messageBoard.toString().equals(vo.getTypeCode())) {
                list = messageBoardService.getMessageBoardUnitList(vo);
            }
        }
        // 文件头s
        String[] titles = new String[] { "序号", "单位名称", "来信总数", "办结总数", "办结率"};
        // 内容
        List<String[]> datas = new ArrayList<String[]>();
        if (list != null && list.size() > 0) {
            int i = 1;
            for (MessageBoardListVO messageBoardListVO : list) {
                String[] row1 = new String[5];
                row1[0] = i++ + "";
                row1[1] = messageBoardListVO.getOrganName();
                row1[2] = messageBoardListVO.getReceiveCount() + "";
                row1[3] = messageBoardListVO.getReplyCount() + "";
                row1[4] = messageBoardListVO.getReplyRate() + "";
                datas.add(row1);
            }
        }
        // 导出
        String name = "信件统计";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
