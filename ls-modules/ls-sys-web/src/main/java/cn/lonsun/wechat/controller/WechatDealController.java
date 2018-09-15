package cn.lonsun.wechat.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.SubscribeMsgEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatTurnEO;
import cn.lonsun.wechatmgr.internal.entity.WechatMsgEO;
import cn.lonsun.wechatmgr.internal.service.ISubscribeMsgService;
import cn.lonsun.wechatmgr.internal.service.IWeChatMsgService;
import cn.lonsun.wechatmgr.internal.service.IWeChatTurnService;
import cn.lonsun.wechatmgr.internal.wechatapiutil.ApiUtil;
import cn.lonsun.wechatmgr.vo.WeChatProcessVO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;
import cn.lonsun.wechatmgr.vo.WeResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by lonsun on 2016-10-9.
 */

@Controller
@RequestMapping(value = "weChatDeal")
public class WechatDealController extends BaseController {
    @Autowired
    private IWeChatMsgService weChatMsgService;
    @Autowired
    private IWeChatTurnService weChatTurnService;
    @Autowired
    private IOrganService organService;
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ISubscribeMsgService subscribeMsgService;
    /**
     * 办理页
     * @return
     */
    @RequestMapping("dealPage")
    public String dealPage(ModelMap map){
        map.put("site",LoginPersonUtil.isSiteAdmin());
        map.put("super",LoginPersonUtil.isSuperAdmin());
        return "/wechat/deal_page";
    }

    /**
     * 办理列表
     * @param pageQueryVO
     * @return
     */
    @RequestMapping("getDealList")
    @ResponseBody
    public Object getDealList(WeChatUserVO pageQueryVO){
        pageQueryVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination page = weChatMsgService.getUserResponse(pageQueryVO);
        return page;
    }


    /**
     * 回复页
     * @return
     */
    @RequestMapping("replyPage")
    public String replyPage(Long id,ModelMap map){
        map.put("id",id);
        return "/wechat/reply_Page";
    }

    /**
     * 保存回复信息
     * @param id
     * @param repMsgContent
     * @return
     */
    @RequestMapping("saveReply")
    @ResponseBody
    public Object saveReply(Long id,String repMsgContent){
        WechatMsgEO wechatMsgEO = weChatMsgService.getEntity(WechatMsgEO.class, id);
        if(null==wechatMsgEO){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"该留言信息已经删除");
        }
        final  String sendMsgContent=repMsgContent;
        final  String username=wechatMsgEO.getOriginUserName();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ApiUtil.customSend(sendMsgContent,username);
                Map<String,Object> map =new HashMap<String, Object>();
                map.put("siteId",LoginPersonUtil.getSiteId());
                map.put("msgType", SubscribeMsgEO.MSGTYPE.judge.toString());
                map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                SubscribeMsgEO subMsg = subscribeMsgService.getEntity(SubscribeMsgEO.class,map);
                if(null!=subMsg){
                    final  String judge =subMsg.getContent();
                    ApiUtil.customSend(judge,username);

                }
            }
        });
        wechatMsgEO.setIsRep(1);
        wechatMsgEO.setRepMsgContent(repMsgContent);
        wechatMsgEO.setRepMsgDate(new Date());
        weChatMsgService.updateEntity(wechatMsgEO);

        WeChatTurnEO weChatTurnEO =new WeChatTurnEO();
        weChatTurnEO.setMsgId(id);
        weChatTurnEO.setSiteId(LoginPersonUtil.getSiteId());
        weChatTurnEO.setOperateUnitId(LoginPersonUtil.getUnitId());
        weChatTurnEO.setOperateUnitName(LoginPersonUtil.getUnitName());
        weChatTurnEO.setOperateUserName(LoginPersonUtil.getUserName());
        weChatTurnEO.setType(WeChatTurnEO.TYPE.reply.toString());
        if(LoginPersonUtil.isSiteAdmin()){
            weChatTurnEO.setIsSiteAdmin(1);
        }
        if(LoginPersonUtil.isSuperAdmin()){
            weChatTurnEO.setIsSuperAdmin(1);
        }
        weChatTurnService.saveEntity(weChatTurnEO);

        return getObject();
    }

    /**
     * 转办列表
     * @return
     */
    @RequestMapping("turnPage")
    public String turnPage(Long id,ModelMap map){
        map.put("id",id);
        return "/wechat/turn_page";
    }

    /**
     * 获取单位
     * @return
     */

    @RequestMapping("getUints")
    @ResponseBody
    public Object getUints() {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteMgrEO eo = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        String unitIds = eo.getUnitIds();
        List<OrganEO> list = new ArrayList<OrganEO>();
        if (!AppUtil.isEmpty(unitIds)) {
            Long[] arr = AppUtil.getLongs(unitIds, ",");
            if (arr != null && arr.length > 0) {
                list = organService.getOrgansByDn(arr[0], OrganEO.Type.Organ.toString());
            }
        }
        return getObject(list);
    }
    /**
     * 转办
     * @param id
     * @param organId
     * @param organName
     * @return
     */
    @RequestMapping("saveTurn")
    @ResponseBody
    public Object saveTurn(Long id,String organId,String organName){

        System.out.print(LoginPersonUtil.getOrganId()+"......"+LoginPersonUtil.getUserId());
        System.out.print(LoginPersonUtil.isSiteAdmin()+"..."+LoginPersonUtil.isSuperAdmin());
        WechatMsgEO wechatMsgEO   = weChatMsgService.getEntity(WechatMsgEO.class,id);
        wechatMsgEO.setChangeUnitId(LoginPersonUtil.getUnitId());
        wechatMsgEO.setTurnUnitId(Long.valueOf(organId));
        //办理记录
        WeChatTurnEO weChatTurnEO =new WeChatTurnEO();
        weChatTurnEO.setMsgId(id);
        weChatTurnEO.setSiteId(LoginPersonUtil.getSiteId());
        weChatTurnEO.setOperateUnitId(LoginPersonUtil.getUnitId());
        weChatTurnEO.setOperateUnitName(LoginPersonUtil.getUnitName());
        weChatTurnEO.setOperateUserName(LoginPersonUtil.getUserName());
        weChatTurnEO.setTurnUnitId(Long.valueOf(organId));
        weChatTurnEO.setTurnUnitName(organName);
        weChatTurnEO.setType(WeChatTurnEO.TYPE.turn.toString());
        if(LoginPersonUtil.isSiteAdmin()){
            weChatTurnEO.setIsSiteAdmin(1);
        }
        if(LoginPersonUtil.isSuperAdmin()){
            weChatTurnEO.setIsSuperAdmin(1);
        }
        final  String sendMsgContent="您的留言已转办给"+organName;
        final  String username=wechatMsgEO.getOriginUserName();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ApiUtil.customSend(sendMsgContent,username);
            }
        });



        weChatTurnService.saveEntity(weChatTurnEO);
        weChatMsgService.updateEntity(wechatMsgEO);
        return getObject();
    }

    /**
     * 办理详情页
     * @param id
     * @param map
     * @return
     */
    @RequestMapping("procesPage")
    public String procesPage(Long id,ModelMap map){
        map.put("id",id);
        return "/wechat/proces_page";
    }

    /**
     * 办理详情信息
     * @param weChatProcessVO
     * @return
     */
    @RequestMapping("getProcessList")
    @ResponseBody
    public Object getProcessList(WeChatProcessVO weChatProcessVO){
      return weChatTurnService.getInforByMsgId(weChatProcessVO);


    }


    /**
     * 办理详情信息
     * @param weChatProcessVO
     * @return
     */
    @RequestMapping("getProcessListNew")
    @ResponseBody
    public Object getProcessListNew(WeChatProcessVO weChatProcessVO){
        return weChatTurnService.getProcessListNew(weChatProcessVO);
    }


    @RequestMapping("delMag")
    @ResponseBody
    public Object delMag(String ids){
        if(AppUtil.isEmpty(ids)){
           throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择删除项");
        }

        weChatMsgService.delete(WechatMsgEO.class,AppUtil.getLongs(ids,","));
        return getObject();
    }


    /**
     * 我的转办页
     * @return
     */
    @RequestMapping("myTurnPage")
    public String myTurnPage(ModelMap map){
        map.put("site",LoginPersonUtil.isSiteAdmin());
        map.put("super",LoginPersonUtil.isSuperAdmin());
        return "/wechat/turn_deal_page";
    }


    /**
     * 我转办的列表
     * @param pageQueryVO
     * @return
     */
    @RequestMapping("getTurnList")
    @ResponseBody
    public  Object getTurnList(WeChatUserVO pageQueryVO){
          Pagination page = weChatMsgService.getUserTurn(pageQueryVO);
          return page;
    }




}
