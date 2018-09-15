package cn.lonsun.wechatmgr.internal.wechatapiutil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.WeChatAccountsInfoEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatAccountsInfoService;

/**
 * @author Hewbing
 * @ClassName: TokenHander
 * @Description: 获取token并做7000秒缓存
 * @date 2015年12月12日 下午3:16:42
 */
public class TokenHander {

    private static final Logger logger = LoggerFactory.getLogger(TokenHander.class);

    private static IWeChatAccountsInfoService weChatAccountsInfoService = SpringContextHolder.getBean("weChatAccountsInfoService");

    public static final Map<String, Token> CatchMap = new HashMap<String, Token>();
    //获取 ACCESS_TOKEN
    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    //网页登入授权
    private static final String WEB_TOKEN_ERL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    /**
     * access_token 公众号的全局唯一接口调用凭据  有效时间 2小时
     *
     * @return
     */
    public static String getAccessToken() {
        Long siteId = LoginPersonUtil.getSiteId();
        //获取开发者凭据 （后期需要缓存处理）
        WeChatAccountsInfoEO info = weChatAccountsInfoService.getInfoBySite(siteId);
        String APPID = info.getAppId();
        String APPSECRET = info.getAppSecret();
        if (APPID == null || APPSECRET == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "微信配置为空，前往系统配置微信");
        }
        //获取access_token  公众号的全局唯一接口调用凭据  有效时间 2小时
        Token tokenCache = CatchMap.get(APPID);
        if(!AppUtil.isEmpty(tokenCache)&&!AppUtil.isEmpty(tokenCache.getToken())&&!AppUtil.isEmpty(tokenCache.getToken().getAccess_token())){
            Long toTime = (new Date()).getTime() - 7000000L;
            if (tokenCache.getGetTime() > toTime) {
                logger.info("CacheMap get TOKEN :" + tokenCache.getToken().getAccess_token());
                return tokenCache.getToken().getAccess_token();
            }
        }
        JSONObject json = HttpAccessUtil.HttpPost(TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET), null);
        AccessToken token = (AccessToken) JSONObject.toBean(json, AccessToken.class);
        Token t = new Token();
        t.setToken(token);
        t.setGetTime((new Date()).getTime());
        //缓存获取access_token
        CatchMap.put(APPID, t);
        logger.info("Get new TOKEN :" + token.getAccess_token());
        return token.getAccess_token();
    }

    public static String getAccessToken(Long siteId) {
        WeChatAccountsInfoEO info = weChatAccountsInfoService.getInfoBySite(siteId);
        String APPID = info.getAppId();
        String APPSECRET = info.getAppSecret();
        if (APPID == null || APPSECRET == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "微信配置为空，前往系统配置微信");
        }
        Token tokenCache = CatchMap.get(APPID);
        if (!AppUtil.isEmpty(tokenCache)) {
            Long toTime = (new Date()).getTime() - 7000000L;
            if (tokenCache.getGetTime() > toTime) {
                logger.info("CacheMap get TOKEN :" + tokenCache.getToken().getAccess_token());
                return tokenCache.getToken().getAccess_token();
            }
        }
        JSONObject json = HttpAccessUtil.HttpPost(TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET), null);
        AccessToken token = (AccessToken) JSONObject.toBean(json, AccessToken.class);
        Token t = new Token();
        t.setToken(token);
        t.setGetTime((new Date()).getTime());
        CatchMap.put(APPID, t);
        logger.info("Get new TOKEN :" + token.getAccess_token());
        return token.getAccess_token();
    }


    public static WebAccessToken getWebAccessToken(Long siteId, String code) {
        WeChatAccountsInfoEO info = weChatAccountsInfoService.getInfoBySite(siteId);
        String APPID = info.getAppId();
        String APPSECRET = info.getAppSecret();
        if (APPID == null || APPSECRET == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "微信配置为空，前往系统配置微信");
        }
        if (AppUtil.isEmpty(code)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请用微信客户端访问");
        }
        JSONObject json = HttpAccessUtil.HttpPost(WEB_TOKEN_ERL.replace("APPID", APPID).replace("SECRET", APPSECRET).replace("CODE", code), null);
        if (json.toString().contains("errcode")) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), json.getString("errmsg"));
        }
        WebAccessToken token = (WebAccessToken) JSONObject.toBean(json, WebAccessToken.class);
        logger.info("Get new WEB_TOKEN :" + token.getAccess_token());
        return token;
    }
}
