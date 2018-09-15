package cn.lonsun.wechatmgr.internal.wechatapiutil;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.wechatmgr.internal.entity.WeChatLogEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatLogService;
import cn.lonsun.wechatmgr.internal.service.IWeChatUserService;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Created by zhangchao on 2016/10/9.
 */
public class WeChatSyn implements Runnable{
    private static IWeChatLogService weChatLogService= SpringContextHolder.getBean("weChatLogService");

    private static IWeChatUserService weChatUserService=SpringContextHolder.getBean("weChatUserService");

    private WeChatLogEO weChatLog;

    public static Logger log = Logger.getLogger(WeChatUser.class);

    @Override
    public void run() {
        log.info("WeChat taskExecutor start >>>");

        WeChatUserEO weChatUser = null;
        String msgType = weChatLog.getMsgType();
        String openid = weChatLog.getOpenid();
        Long siteId = weChatLog.getSiteId();
        //查询用户详细信息
        weChatUser= ApiUtil.getUserInfo(weChatLog.getOpenid(),siteId);
        //取消关注
        if("unsubscribe".equals(msgType)) {
            //取消关注时，数据库中拉取用户信息
            weChatUser = weChatUserService.getUserByOpenId(openid);
            weChatUserService.deleteUserByOpenId(openid);
        }
        //关注
        if("subscribe".equals(msgType)){
            if(weChatUser != null){
                weChatUser.setSiteId(siteId);
                weChatUser.setRecordStatus("Normal");
                weChatUser.setCreateDate(new Date());
                weChatUserService.saveEntity(weChatUser);
            }
        }
        //保存日志信息
        if(weChatUser != null){
            weChatLog.setOpenid(weChatUser.getOpenid());
            weChatLog.setProvince(weChatUser.getProvince());
            weChatLog.setCity(weChatUser.getCity());
            weChatLog.setCountry(weChatUser.getCountry());
            weChatLog.setNickname(weChatUser.getNickname());
            weChatLog.setHeadimgurl(weChatUser.getHeadimgurl());
            weChatLogService.saveEntity(weChatLog);
        }

        log.info("WeChat taskExecutor end>>>");
    }

    public WeChatLogEO getWeChatLog() {
        return weChatLog;
    }

    public void setWeChatLog(WeChatLogEO weChatLog) {
        this.weChatLog = weChatLog;
    }
}
