package cn.lonsun.staticcenter.util;

import weibo4j.http.AccessToken;
import weibo4j.http.HttpClient;
import weibo4j.model.WeiboException;

/**
 * 微博登录工具类
 * @author: liuk
 * @version: v1.0
 * @date:2018/4/24 17:42
 */
public class WeiBoLoginUtil {

    public static AccessToken getAccessTokenByCode(String code, String accessTokenURL, String appId,
                                            String appSecret, String redirectURI) throws WeiboException {
        return new weibo4j.http.AccessToken(new HttpClient().post(accessTokenURL,
                new weibo4j.model.PostParameter[] {
                        new weibo4j.model.PostParameter("client_id", appId),
                        new weibo4j.model.PostParameter("client_secret", appSecret),
                        new weibo4j.model.PostParameter("grant_type", "authorization_code"),
                        new weibo4j.model.PostParameter("code", code),
                        new weibo4j.model.PostParameter("redirect_uri", redirectURI) }, false, null));
    }
}
