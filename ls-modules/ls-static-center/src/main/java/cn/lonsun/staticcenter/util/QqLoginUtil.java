package cn.lonsun.staticcenter.util;

import com.qq.connect.QQConnectException;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.utils.QQConnectConfig;
import com.qq.connect.utils.http.HttpClient;
import com.qq.connect.utils.http.PostParameter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * QQ登录工具类
 * @author: liuk
 * @version: v1.0
 * @date:2018/4/24 17:42
 */
public class QqLoginUtil {

    /**
     * 获取accessToken
     * @param appId
     * @param appSecret
     * @param returnAuthCode
     * @param returnState
     * @param request
     * @return
     * @throws QQConnectException
     */
    public static AccessToken getAccessTokenByRequest(String appId,String appSecret, String returnAuthCode,
                                                      String returnState, String redirectUri,ServletRequest request) throws QQConnectException {
        String queryString = ((HttpServletRequest)request).getQueryString();
        if (queryString == null) {
            return new AccessToken();
        } else {
            HttpSession session = ((HttpServletRequest)request).getSession();
            String state = (String)session.getAttribute("qq_connect_state");
            if (state != null && !state.equals("")) {
                HttpClient client = new HttpClient();
                AccessToken accessTokenObj = null;
                if (!returnState.equals("") && !returnAuthCode.equals("")) {
                    if (!state.equals(returnState)) {
                        accessTokenObj = new AccessToken();
                    } else {
                        accessTokenObj = new AccessToken(client.post(QQConnectConfig.getValue("accessTokenURL"), new PostParameter[]{new PostParameter("client_id", appId), new PostParameter("client_secret", appSecret), new PostParameter("grant_type", "authorization_code"), new PostParameter("code", returnAuthCode), new PostParameter("redirect_uri", redirectUri)}, false));
                    }
                } else {
                    accessTokenObj = new AccessToken();
                }
                return accessTokenObj;
            } else {
                return new AccessToken();
            }
        }
    }


    /**
     * 获取qq用户信息
     * @param openid
     * @param appId
     * @param token
     * @return
     * @throws QQConnectException
     */
    public static UserInfoBean getUserInfo(String openid,String appId,String token) throws QQConnectException {
        return new UserInfoBean(new HttpClient().get(QQConnectConfig.getValue("getUserInfoURL"), new PostParameter[]{new PostParameter("openid", openid), new PostParameter("oauth_consumer_key", appId), new PostParameter("access_token", token), new PostParameter("format", "json")}).asJSONObject());
    }
}
