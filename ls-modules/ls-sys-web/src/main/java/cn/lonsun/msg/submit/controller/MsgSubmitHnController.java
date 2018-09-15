package cn.lonsun.msg.submit.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.msg.submit.hn.entity.CmsMsgSubmitHnEO;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToColumnHnEO;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToUserHnEO;
import cn.lonsun.msg.submit.hn.entity.vo.CmsMsgToColumnHnVO;
import cn.lonsun.msg.submit.hn.service.IMsgSubmitHnService;
import cn.lonsun.msg.submit.hn.service.IMsgToColumnHnService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.system.role.internal.service.IMenuRoleService;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.service.IWeChatPushMsgService;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;
import cn.lonsun.weibo.service.IWeiboRadioContentService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lonsun.util.ColumnUtil.getSiteUrl;

/**
 * @author gu.fei
 * @version 2015-11-26 9:57
 */
@Controller
@RequestMapping("/msg/submit/hn")
public class MsgSubmitHnController extends BaseController {

    private static final String FILE_BASE = "/msg/submit/hn";

    @Autowired
    private IMsgSubmitHnService msgSubmitHnService;

    @Autowired
    private IWeiboRadioContentService weiboRadioContentService;

    @Autowired
    private IMsgToColumnHnService msgToColumnHnService;

    @Autowired
    private IWeChatPushMsgService weChatPushMsgService;

    @Autowired
    private IMenuRoleService menuRoleService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/index/index";
    }

    /**
     * 概览
     * @return
     */
    @RequestMapping("/overview")
    public String overview(ModelMap map) {
        String usermanageurl = "/person/userPage";
        map.put("isUserPageShow",menuRoleService.isMenuShow(usermanageurl));
        String webdomain = ColumnUtil.getSiteUrl(null, LoginPersonUtil.getSiteId());
        map.put("webdomain",webdomain);
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, LoginPersonUtil.getSiteId());
        map.put("siteName",eo.getName());
        return FILE_BASE + "/overview/overview";
    }

    /**
     * 消息查阅
     * @return
     */
    @RequestMapping("/lookMsg")
    public String lookMsg() {
        return FILE_BASE + "/lookmsg/index";
    }

    /**
     * 代发布信息
     * @return
     */
    @RequestMapping("/tobeMsg")
    public String tobeMsg() {
        return FILE_BASE + "/tobemsg/index";
    }

    /**
     * 已发布信息
     * @return
     */
    @RequestMapping("/beMsg")
    public String beMsg() {
        return FILE_BASE + "/bemsg/index";
    }

    /**
     * @return
     */
    @RequestMapping("/publishList")
    public String publishList() {
        return FILE_BASE + "/bemsg/publish_list";
    }


    /**
     * 查阅信息详细
     * @param map
     * @param msgId
     * @return
     */
    @RequestMapping("/tomeDetail")
    public String tomeDetail(ModelMap map, Long msgId) {
        CmsMsgSubmitHnEO eo = msgSubmitHnService.getEntity(CmsMsgSubmitHnEO.class, msgId);
        map.put("eo",eo);
        return FILE_BASE + "/lookmsg/msg_detail";
    }

    /**
     * 待发布信息查看详细界面
     * @return
     */
    @RequestMapping("/tobeMsgDetail")
    public String tobeMsgDetail() {
        return FILE_BASE + "/tobemsg/msg_detail";
    }

    /**
     * 发布信息查看详细界面
     * @return
     */
    @RequestMapping("/beMsgDetail")
    public String beMsgDetail() {
        return FILE_BASE + "/bemsg/msg_detail";
    }

    /**
     * 信息添加编辑界面
     * @return
     */
    @RequestMapping("/addOrEdit")
    public String addOrEdit() {
        return FILE_BASE + "/index/edit";
    }

    /**
     * 消息退回界面
     * @return
     */
    @RequestMapping("/backMsg")
    public String backMsg() {
        return FILE_BASE + "/tobemsg/back_msg";
    }

    /**
     * 发布消息编辑界面
     * @return
     */
    @RequestMapping("/publishEdit")
    public String publishEdit() {
        return FILE_BASE + "/tobemsg/publish_edit";
    }

    /**
     * 推送消息
     * @return
     */
    @RequestMapping("/pushMsg")
    public String pushMsg() {
        return FILE_BASE + "/bemsg/push_msg";
    }

    /**
     * @param map
     * @param msgId
     * @return
     */
    @RequestMapping("/msgDetail")
    public String msgDetail(ModelMap map, Long msgId) {
        CmsMsgSubmitHnEO eo = msgSubmitHnService.getEntity(CmsMsgSubmitHnEO.class, msgId);
        map.put("eo",eo);
        return FILE_BASE + "/index/msg_detail";
    }

    @ResponseBody
    @RequestMapping("/getOverViewData")
    public Object getOverViewData() {
        Map<String,Object> viewData = new HashMap<String, Object>();
        Long lookMsg = msgSubmitHnService.getToMeCount();
        Long tobeMsg = msgSubmitHnService.getToBeCount(new ParamDto());
        Long beMsg = msgSubmitHnService.getBeCount(new ParamDto());
        if(null != lookMsg) {
            viewData.put("lookMsg",lookMsg);
        }
        if(null != tobeMsg) {
            viewData.put("tobeMsg",tobeMsg);
        }
        if(null != beMsg) {
            viewData.put("beMsg",beMsg);
        }
        return getObject(viewData);
    }

    /**
     * 查询自己发布信息
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageList")
    public Object getPageList(ParamDto dto) {
        return msgSubmitHnService.getPageList(dto);
    }

    /**
     * 查询传阅给自己的信息
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("/getToMePageList")
    public Object getToMePageList(ParamDto dto) {
        return msgSubmitHnService.getToMePageList(dto);
    }

    /**
     * 查询待发布的信息
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTobePageList")
    public Object getTobePageList(ParamDto dto) {
        return msgSubmitHnService.getTobePageList(dto);
    }

    /**
     * 查询发布的信息
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("/getBePageList")
    public Object getBePageList(ParamDto dto) {
        return msgSubmitHnService.getBePageList(dto);
    }

    /**
     * 添加信息
     * @param eo
     * @param columnsList
     * @param usersList
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveEntity")
    public Object saveEntity(CmsMsgSubmitHnEO eo, String columnsList, String usersList) {
        if(null != columnsList) {
            List<CmsMsgToColumnHnVO> columnHnVOs = JSONObject.parseArray(columnsList,CmsMsgToColumnHnVO.class);
            List<CmsMsgToColumnHnEO> columnHnEOs = new ArrayList<CmsMsgToColumnHnEO>();
            for(CmsMsgToColumnHnVO vo : columnHnVOs) {
                CmsMsgToColumnHnEO columnHnEO = new CmsMsgToColumnHnEO();
                columnHnEO.setColumnId(vo.getId());
                columnHnEO.setColumnName(vo.getName());
                columnHnEO.setOrganId(vo.getOrganId());
                columnHnEO.setCode(vo.getCode());
                columnHnEO.setColumnTypeCode(vo.getType());
                columnHnEO.setCreateUnitId(LoginPersonUtil.getUnitId());
                columnHnEO.setSiteId(vo.getSiteId());
                columnHnEO.setSiteName(vo.getSiteName());
                columnHnEOs.add(columnHnEO);
            }
            eo.setColumnHnEOs(columnHnEOs);
        }

        if(null != usersList) {
            List<CmsMsgToUserHnEO> userHnEOs = JSONObject.parseArray(usersList,CmsMsgToUserHnEO.class);
            for(CmsMsgToUserHnEO userHnEO : userHnEOs) {
                userHnEO.setCreateUnitId(LoginPersonUtil.getUnitId());
                userHnEO.setSiteId(LoginPersonUtil.getSiteId());
            }
            eo.setUserHnEOs(userHnEOs);
        }

        eo.setCreateUnitId(LoginPersonUtil.getUnitId());
        msgSubmitHnService.saveEntity(eo);
        return ajaxOk();
    }

    /**
     * 更新报送信息
     * @param eo
     * @param columnsList
     * @param usersList
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateEntity")
    public Object updateEntity(CmsMsgSubmitHnEO eo, String columnsList, String usersList) {
        if(null != columnsList) {
            List<CmsMsgToColumnHnVO> columnHnVOs = JSONObject.parseArray(columnsList,CmsMsgToColumnHnVO.class);
            List<CmsMsgToColumnHnEO> columnHnEOs = new ArrayList<CmsMsgToColumnHnEO>();
            for(CmsMsgToColumnHnVO vo : columnHnVOs) {
                CmsMsgToColumnHnEO columnHnEO = new CmsMsgToColumnHnEO();
                columnHnEO.setMsgId(eo.getId());
                columnHnEO.setColumnId(vo.getId());
                columnHnEO.setColumnName(vo.getName());
                columnHnEO.setOrganId(vo.getOrganId());
                columnHnEO.setCode(vo.getCode());
                columnHnEO.setColumnTypeCode(vo.getType());
                columnHnEO.setCreateUnitId(LoginPersonUtil.getUnitId());
                columnHnEO.setSiteId(vo.getSiteId());
                columnHnEO.setSiteName(vo.getSiteName());
                columnHnEOs.add(columnHnEO);
            }
            eo.setColumnHnEOs(columnHnEOs);
        }

        if(null != usersList) {
            List<CmsMsgToUserHnEO> userHnEOs = JSONObject.parseArray(usersList,CmsMsgToUserHnEO.class);
            for(CmsMsgToUserHnEO userHnEO : userHnEOs) {
                userHnEO.setMsgId(eo.getId());
                userHnEO.setCreateUnitId(LoginPersonUtil.getUnitId());
                userHnEO.setSiteId(LoginPersonUtil.getSiteId());
            }
            eo.setUserHnEOs(userHnEOs);
        }

        eo.setStatus(0);
        eo.setCreateUnitId(LoginPersonUtil.getUnitId());
        msgSubmitHnService.updateEO(eo);
        return ajaxOk();
    }

    /**
     * 批量删除信息
     * @param msgIds
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteEntity")
    public Object updateEntity(@RequestParam(value="msgIds[]",required=false) Long[] msgIds) {
        msgSubmitHnService.deleteEntities(msgIds);
        return ajaxOk();
    }

    /**
     * 更改报送消息查阅状态
     * @param msgId
     * @return
     */
    @ResponseBody
    @RequestMapping("/readMsg")
    public Object readMsg(Long msgId) {
        msgSubmitHnService.readMsg(msgId);
        return ajaxOk();
    }

    /**
     * 批量转发
     * @param msgIds
     * @param usersList
     * @return
     */
    @ResponseBody
    @RequestMapping("/batchTransmit")
    public Object batchTransmit(@RequestParam(value="msgIds[]",required=false) Long[] msgIds, String usersList) {
        List<CmsMsgToUserHnEO> userHnEOs = null;
        if(null != usersList) {
            userHnEOs = JSONObject.parseArray(usersList, CmsMsgToUserHnEO.class);
        }
        if(null != userHnEOs && !userHnEOs.isEmpty()) {
            msgSubmitHnService.batchTransmit(msgIds,userHnEOs);
        }
        return ajaxOk();
    }

    /**
     * 批量转发到其他栏目
     * @param msgIds
     * @param columnsList
     * @return
     */
    @ResponseBody
    @RequestMapping("/batchTransmitToColumn")
    public Object batchTransmitToColumn(@RequestParam(value="msgIds[]",required=false) Long[] msgIds, String columnsList) {
        List<CmsMsgToColumnHnVO> columnHnVOs = null;
        if(null != columnsList) {
            columnHnVOs = JSONObject.parseArray(columnsList, CmsMsgToColumnHnVO.class);
        }
        if(null != columnHnVOs && !columnHnVOs.isEmpty()) {
            List<CmsMsgToColumnHnEO> columnHnEOs = new ArrayList<CmsMsgToColumnHnEO>();
            for(CmsMsgToColumnHnVO vo : columnHnVOs) {
                CmsMsgToColumnHnEO columnHnEO = new CmsMsgToColumnHnEO();
                columnHnEO.setColumnId(vo.getId());
                columnHnEO.setColumnName(vo.getName());
                columnHnEO.setOrganId(vo.getOrganId());
                columnHnEO.setCode(vo.getCode());
                columnHnEO.setColumnTypeCode(vo.getType());
                columnHnEOs.add(columnHnEO);
                columnHnEO.setSiteId(vo.getSiteId());
                columnHnEO.setSiteName(vo.getSiteName());
                columnHnEO.setCreateUnitId(LoginPersonUtil.getUnitId());
            }
            msgSubmitHnService.batchTransmitToColumn(msgIds,columnHnEOs);
        }
        return ajaxOk();
    }

    /**
     * 保存退回原因
     * @param msgId
     * @param backReason
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveBackMsg")
    public Object saveBackMsg(Long msgId,String backReason) {
        CmsMsgSubmitHnEO submitHnEO = msgSubmitHnService.getEntity(CmsMsgSubmitHnEO.class,msgId);
        submitHnEO.setBackUnitName(LoginPersonUtil.getUnitName());
        submitHnEO.setBackUserName(LoginPersonUtil.getUserName());
        submitHnEO.setBackReason(backReason);
        submitHnEO.setStatus(1);
        msgSubmitHnService.updateEntity(submitHnEO);
        return ajaxOk();
    }

    /**
     * 发布报送信息
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/publish")
    public Object publish(CmsMsgSubmitHnEO eo) {
        msgSubmitHnService.publish(eo);
        return ajaxOk();
    }

    /**
     * 批量发布报送信息-功能待用
     * @param msgIds
     * @return
     */
    @ResponseBody
    @RequestMapping("/batchPublish")
    public Object batchPublish(@RequestParam(value="msgIds[]",required=false) Long[] msgIds) {
        return ajaxOk();
    }

    /**
     *获取栏目列表
     * @param msgId
     * @return
     */
    @ResponseBody
    @RequestMapping("/getColumnPageList")
    public Object getColumnPageList(Long msgId, ParamDto dto) {
        return msgSubmitHnService.getColumnPageList(msgId,dto);
    }

    /**
     * 取消发布
     * @param msgId
     * @param columnIds
     * @return
     */
    @ResponseBody
    @RequestMapping("/cancelPublish")
    public Object cancelPublish(Long msgId,@RequestParam(value="columnIds[]",required=false) Long[] columnIds) {
        msgSubmitHnService.cancelPublish(msgId,columnIds);
        return ajaxOk();
    }

    /**
     * 推送信息到微博待发
     * @param msgId
     */
    @ResponseBody
    @RequestMapping("/pushMsgToWeibo")
    public Object pushMsgToWeibo(Long msgId,Long columnId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("msgId",msgId);
        param.put("columnId",columnId);
        CmsMsgSubmitHnEO submitHnEO = msgSubmitHnService.getEntity(CmsMsgSubmitHnEO.class,msgId);
        CmsMsgToColumnHnEO columnHnEO = msgToColumnHnService.getEntity(CmsMsgToColumnHnEO.class,param);
        String url = getSiteUrl(columnId,columnHnEO.getSiteId());
        String title = submitHnEO.getTitle();
        String pushmsg = title + " " + url;
        WeiboRadioContentEO eo = new WeiboRadioContentEO();
        eo.setContent(pushmsg);
        eo.setType(WeiboRadioContentEO.Type.Sina.toString());
        eo.setSiteId(columnHnEO.getSiteId());
        weiboRadioContentService.saveEntity(eo);
        columnHnEO.setPushWeiboStatus(1);
        msgToColumnHnService.updateEntity(columnHnEO);
        return ajaxOk("操作成功");
    }

    /**
     * 推送信息到微信
     * @param msgId
     */
    @ResponseBody
    @RequestMapping("/pushMsgToWeixin")
    public Object pushMsgToWeixin(Long msgId,Long columnId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("msgId",msgId);
        param.put("columnId",columnId);
        CmsMsgSubmitHnEO submitHnEO = msgSubmitHnService.getEntity(CmsMsgSubmitHnEO.class,msgId);
        CmsMsgToColumnHnEO columnHnEO = msgToColumnHnService.getEntity(CmsMsgToColumnHnEO.class,param);
        String url = getSiteUrl(columnId,columnHnEO.getSiteId());
        String title = submitHnEO.getTitle();
        String pushmsg = title + " " + url;
//        int rst = weChatPushMsgService.pushuTextMesagge(pushmsg);
//        if(rst == 1) {
//            return ajaxErr("操作失败");
//        }
        return ajaxErr("操作成功");
    }
}
