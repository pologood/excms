package cn.lonsun.weibo.util;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.weibo.service.IWeiboConfService;

/**
 * @author gu.fei
 * @version 2015-12-11 13:46
 */
public class PropertiesUtil {

    private static IWeiboConfService weiboConfService;

    public static void init() {
        weiboConfService = SpringContextHolder.getBean("weiboConfService");
/*
        //刷新sina微博配置
        WeiboConfEO sina = weiboConfService.getByType(WeiboConfEO.Type.Sina.toString());
        if(null != sina) {
            WeiboConfig.updateProperties("client_ID", AppUtil.isEmpty(sina.getAppKey())?"":sina.getAppKey());
            WeiboConfig.updateProperties("client_SERCRET", AppUtil.isEmpty(sina.getAppSecret())?"":sina.getAppSecret());
        }
        //刷新腾讯微博配置
        WeiboConfEO tencent = weiboConfService.getByType(WeiboConfEO.Type.Tencent.toString());
        if(null != tencent) {
            QQConnectConfig.updateProperties("app_ID",AppUtil.isEmpty(tencent.getAppKey())?"":tencent.getAppKey());
            QQConnectConfig.updateProperties("app_KEY", AppUtil.isEmpty(tencent.getToken())?"":tencent.getToken());
        }*/
    }

}
