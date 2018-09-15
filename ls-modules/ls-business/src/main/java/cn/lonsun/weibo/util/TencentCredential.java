package cn.lonsun.weibo.util;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.weibo.entity.WeiboConfEO;
import cn.lonsun.weibo.service.IWeiboConfService;

import com.qq.connect.api.OpenID;

/**
 * @author gu.fei
 * @version 2015-12-10 18:40
 */
public class TencentCredential {

    private static IWeiboConfService weiboConfService = null;

    private static String openID;

    private static String token;

    static {
        Long siteId = LoginPersonUtil.getSiteId();
        weiboConfService = SpringContextHolder.getBean("weiboConfService");
        WeiboConfEO eo = weiboConfService.getByType(WeiboConfEO.Type.Tencent.toString(),siteId);
        token = eo.getToken();
        try {
            OpenID open = new OpenID(token);
            openID = open.getUserOpenID();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refresh() {
        Long siteId = LoginPersonUtil.getSiteId();
        weiboConfService = SpringContextHolder.getBean("weiboConfService");
        WeiboConfEO eo = weiboConfService.getByType(WeiboConfEO.Type.Tencent.toString(),siteId);
        token = eo.getToken();
        openID = eo.getOpenID();
    }

    public static String getOpenID() {
        return openID;
    }

    public static void setOpenID(String openID) {
        TencentCredential.openID = openID;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        TencentCredential.token = token;
    }
}
