package cn.lonsun.content.messageBoard.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardApplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardApplyVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardOperationVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardApplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardOperationService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @ClassName: AcceptCenterController
 * @Description: 留言受理中心
 */
@Controller
@RequestMapping(value = "acceptCenter")
public class AcceptCenterController extends BaseController {

    private final static String FILE_BASE = "/content/messageBoard";
    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IMessageBoardApplyService applyService;

    @Autowired
    private IMessageBoardForwardService forwardService;

    @Autowired
    private IContentModelService contModelService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IMessageBoardOperationService operationService;

    @Autowired
    private IIndicatorService indicatorService;
    // 进入页面
    @RequestMapping(value = "index")
    public String index(String typeCode,ModelMap model) {
        model.put("typeCode", typeCode);
        return "/content/messageBoard/index";
    }

    /**
     * 查询某个站点下内容模型code值为code的所有栏目
     * @return
     */
    @RequestMapping(value = "getColumnByContentModelCode")
    @ResponseBody
    public Object getColumnByContentModelCode(){
        List<ColumnMgrEO> columnMgrEOList = columnConfigService.getColumnByTypeCode(LoginPersonUtil.getSiteId(), BaseContentEO.TypeCode.messageBoard.toString());
          return getObject(columnMgrEOList);
    }

    // 打开待审核记录页面
    @RequestMapping("applyRecord")
    public String applyRecord(Long id, ModelMap map) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        map.put("messageBoardId", id);
        return "/content/messageBoard/message_board_apply_record";
    }

    // 获取待审核记录数据
    @RequestMapping("getApplyRecord")
    @ResponseBody
    public Object getApplyRecord(Long pageIndex, Integer pageSize, Long id) {
        return getObject(applyService.getRecord(pageIndex, pageSize, id));

    }

    // 打开审核页面
    @RequestMapping("disposeEdit")
    public String disposeEdit(Long id, ModelMap map) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        map.put("disposeId", id);
        return "/content/messageBoard/message_board_dispose_edit";
    }

    // 审核
    @RequestMapping("dispose")
    @ResponseBody
    public Object dispose(MessageBoardApplyVO applyVO) {

        if (null == applyVO.getId()) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择审核对象");
        }

        MessageBoardApplyEO applyEO = applyService.getEntity(MessageBoardApplyEO.class, applyVO.getId());

        if(null==applyEO){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "审核出错");
        }else{
                applyService.dispose(applyVO);
        }
        return getObject();
    }

    // 打开收回列表页面
    @RequestMapping("recoverRecord")
    public String recoverRecord(Long id, Long columnId,ModelMap map) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        List<ContentModelParaVO> list = contModelService.getParam(columnId, LoginPersonUtil.getSiteId(),null);
        Integer recType = null;
        if (list != null && list.size() > 0) {
            recType = list.get(0).getRecType();
            map.put("recType", recType);
        }
        map.put("messageBoardId", id);
        return "/content/messageBoard/message_board_recover_record";
    }

    // 打开收回页面
    @RequestMapping("recoverEdit")
    public String recoverEdit(Long id, ModelMap map) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        map.put("forwardId", id);
        return "/content/messageBoard/message_board_recover_edit";
    }

    // 收回
    @RequestMapping("recover")
    @ResponseBody
    public Object recover(MessageBoardOperationVO operationVO) {
        forwardService.recover(operationVO);
        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,operationVO.getBaseContentId());
        IndicatorEO indicatorEO = (IndicatorEO)this.indicatorService.getEntity(IndicatorEO.class, operationVO.getColumnId());
        if (indicatorEO != null) {
            SysLog.log(indicatorEO.getName() + "：收回（" + contentEO.getTitle() + "），办理单位（" + LoginPersonUtil.getOrganName() + "）", "MessageBoardEO", CmsLogEO.Operation.Delete.toString());
        }
        return getObject();
    }

    // 打开分配页面
    @RequestMapping(value = "messageBoardAssign")
    public String messageBoardAssign(Long id,Long columnId, Model model) throws UnsupportedEncodingException {
        ColumnTypeConfigVO configVO=ModelConfigUtil.getCongfigVO(columnId,LoginPersonUtil.getSiteId());
        if(configVO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"内容模型不存在");
        }
        Integer recType = configVO.getRecType();
        Integer isTurn=configVO.getTurn();
        model.addAttribute("recType", recType);
        model.addAttribute("isTurn", isTurn);
        List<MessageBoardForwardVO> forwardVOList = forwardService.getAllForwardByMessageBoardId(id);
        String  receiveOrganIds ="";
        String  receiveUnitNames ="";
        String  recUserIds = "";
        String  recUserNames = "";
        if((recType!=null&&recType==0)||(isTurn!=null&&isTurn==1)) {
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    receiveOrganIds = receiveOrganIds + forwardVOList.get(i).getReceiveOrganId();
                    receiveUnitNames = receiveUnitNames + forwardVOList.get(i).getReceiveUnitName();
                } else {
                    receiveOrganIds = receiveOrganIds + ',' + forwardVOList.get(i).getReceiveOrganId();
                    receiveUnitNames = receiveUnitNames + ',' + forwardVOList.get(i).getReceiveUnitName();
                }
            }
            model.addAttribute("receiveOrganIds", receiveOrganIds);
            model.addAttribute("receiveUnitNames", receiveUnitNames);
        }else{
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    recUserIds = recUserIds + forwardVOList.get(i).getReceiveUserCode();
                    recUserNames = recUserNames + forwardVOList.get(i).getReceiveUserName();
                } else {
                    recUserIds = recUserIds + ',' + forwardVOList.get(i).getReceiveUserCode();
                    recUserNames = recUserNames + ',' + forwardVOList.get(i).getReceiveUserName();
                }
            }
            model.addAttribute("recUserIds",recUserIds);
            model.addAttribute("recUserNames",recUserNames);

        }
        model.addAttribute("messageBoardId", id);
        model.addAttribute("columnId", columnId);
        return "/content/messageBoard/message_board_assign";
    }



    //批量发布
    @ResponseBody
    @RequestMapping("batchPublish")
    public Object batchPublish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long siteId, @RequestParam(value = "columnIds[]", required = false) Long[] columnIds) {
        baseContentService.changePublish(new ContentPageVO(siteId, null, 1, ids, null));
        for (Long columnId : columnIds) {
            MessageSender.sendMessage(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.PUBLISH.value()));
        }
        return ResponseData.success("发布成功!");
    }

    //删除
    @ResponseBody
    @RequestMapping("batchDelete")
    public Object batchDelete(@RequestParam("ids[]") Long[] ids) {
        baseContentService.delContent(ids);
        return ResponseData.success("删除成功!");
    }


}
