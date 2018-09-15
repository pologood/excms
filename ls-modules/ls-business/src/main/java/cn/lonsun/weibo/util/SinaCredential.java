package cn.lonsun.weibo.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.weibo.entity.WeiboConfEO;
import cn.lonsun.weibo.service.IWeiboConfService;
import weibo4j.Account;
import weibo4j.org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gu.fei
 * @version 2015-12-10 18:40
 */
public class SinaCredential {

    private static IWeiboConfService weiboConfService = SpringContextHolder.getBean("weiboConfService");

    //缓存不同站点下的微博配置信息
    private static ConcurrentHashMap<Long,WeiboConfEO> concurrentHashMapConf = new ConcurrentHashMap<Long, WeiboConfEO>();

    //缓存不同站点下面的UID
    private static ConcurrentHashMap<Long,String> concurrentHashMapUid = new ConcurrentHashMap<Long, String>();

    static {
//        concurrentHashMapConf = new ConcurrentHashMap<Long, WeiboConfEO>();
//        concurrentHashMapUid = new ConcurrentHashMap<Long, String>();
//        Long siteId = LoginPersonUtil.getSiteId();
//        weiboConfService = SpringContextHolder.getBean("weiboConfService");
//        WeiboConfEO eo = weiboConfService.getByType(WeiboConfEO.Type.Sina.toString(),siteId);
//        concurrentHashMapConf.put(siteId,eo);
    }

    /**
     * 刷新当前站点配置到缓存
     */
    public static void refresh(Long siteId) {
//        Long siteId = LoginPersonUtil.getSiteId();
        WeiboConfEO eo = weiboConfService.getByType(WeiboConfEO.Type.Sina.toString(),siteId);
        concurrentHashMapConf.put(siteId,eo);
    }

    public static String getAppKey(Long siteId) {
        WeiboConfEO eo = concurrentHashMapConf.get(LoginPersonUtil.getSiteId());
        if(null == eo || AppUtil.isEmpty(eo.getAppKey())) {
            refresh(siteId);
        }
        return eo.getAppKey();
    }

    public static String getAppSecret(Long siteId) {
        WeiboConfEO eo = concurrentHashMapConf.get(LoginPersonUtil.getSiteId());
        if(null == eo || AppUtil.isEmpty(eo.getAppSecret())) {
            refresh(siteId);
        }
        return eo.getAppSecret();
    }

    /**
     * 获取当前站点token值
     * @return
     */
    public static String getToken(Long siteId) {
        WeiboConfEO eo = concurrentHashMapConf.get(LoginPersonUtil.getSiteId());
        if(null == eo || AppUtil.isEmpty(eo.getToken())) {
            refresh(siteId);
        }

        if(AppUtil.isEmpty(eo.getToken())) {
            throw new BaseRunTimeException("token获取失败");
        }
        return eo.getToken();
    }

    /**
     * 获取用户UID
     * @return
     */
    public static String getUID(Long siteId) {
        if(AppUtil.isEmpty(getToken(siteId))) {
            throw new BaseRunTimeException("token获取失败!");
        }

        String uid = concurrentHashMapUid.get(LoginPersonUtil.getSiteId());
        if(AppUtil.isEmpty(uid)) {
            Account account = new Account(getToken(siteId));
            try {
                JSONObject jsonObj = account.getUid();
                uid = jsonObj.getString("uid");
                concurrentHashMapUid.put(LoginPersonUtil.getSiteId(),uid);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseRunTimeException("获取UID失败");
            }
        }

        if(AppUtil.isEmpty(uid)) {
            throw new BaseRunTimeException("获取UID失败");
        }
        return uid;
    }
}
