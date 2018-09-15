package cn.lonsun.weibo.util;

//import net.sf.json.JSONObject;

import cn.lonsun.weibo.entity.WeiboConfEO;
import com.qq.connect.utils.QQConnectConfig;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import weibo4j.util.BareBonesBrowserLaunch;
import weibo4j.util.WeiboConfig;

/**
 * @author gu.fei
 * @version 2016-3-25 14:39
 */
public class TokenGetUtil {

    /**
     * 获取code
     *
     * @throws Exception
     */
    public static void getAccessCode(WeiboConfEO eo) throws Exception {
        if (eo.getType().equals(WeiboConfEO.Type.Sina.toString())) {
            String url = WeiboConfig.getValue("authorizeURL").trim() + "?client_id=" + eo.getAppKey() + "&redirect_uri=" + eo.getValidUrl() + "&response_type=code";
            BareBonesBrowserLaunch.openURL(url);
        } else if (eo.getType().equals(WeiboConfEO.Type.Tencent.toString())) {
            String url = QQConnectConfig.getValue("authorizeURL").trim() + "?client_id=" + eo.getAppKey() + "&redirect_uri=" + eo.getValidUrl() + "&response_type=code";
            BareBonesBrowserLaunch.openURL(url);
        }
    }

    /**
     * 获取code
     *
     * @throws Exception
     */
    public static String getAccessUrl(WeiboConfEO eo) throws Exception {
        String url = null;
        if (eo.getType().equals(WeiboConfEO.Type.Sina.toString())) {
            url = WeiboConfig.getValue("authorizeURL").trim() + "?client_id=" + eo.getAppKey() + "&redirect_uri=" + eo.getValidUrl() + "&response_type=code";
        } else if (eo.getType().equals(WeiboConfEO.Type.Tencent.toString())) {
            url = QQConnectConfig.getValue("authorizeURL").trim() + "?client_id=" + eo.getAppKey() + "&redirect_uri=" + eo.getValidUrl() + "&response_type=code";
        }

        return url;
    }

    /**
     * 获取token
     *
     * @param code
     * @return
     * @throws Exception
     */
    public static String getSinaAccessToken(String code) throws Exception {
        String responseDate = "";
        String token = "";
        //本机运行时会报证书错误
        PostMethod postMethod = new PostMethod("https://api.weibo.com/oauth2/access_token");
        postMethod.addParameter("grant_type", "authorization_code");
        postMethod.addParameter("code", code);
        postMethod.addParameter("redirect_uri", WeiboConfig.getValue("redirect_URI"));
        postMethod.addParameter("client_id", WeiboConfig.getValue("client_ID"));
        postMethod.addParameter("client_secret", WeiboConfig.getValue("client_SERCRET"));

//        postMethod.addParameter("client_id", "1144861424");
//        postMethod.addParameter("client_secret","9e3ee83d8c584d5cfbd53de5a0658083");
//        postMethod.addParameter("redirect_uri","http://www.yunwar.com");
        HttpClient client = new HttpClient();
        client.executeMethod(postMethod);
        responseDate = postMethod.getResponseBodyAsString();
        if (!responseDate.equals("") && responseDate.indexOf("access_token") != -1) {
            JSONObject jsonData = JSONObject.fromObject(responseDate);
            token = (String) jsonData.get("access_token");
        }
        return token;
    }

    /**
     * 获取token
     *
     * @param code
     * @return
     * @throws Exception
     */
    public static String getTencentAccessToken(String code) throws Exception {
        String responseDate = "";
        String token = "";
        //本机运行时会报证书错误
        PostMethod postMethod = new PostMethod("https://graph.qq.com/oauth2.0/token");
        postMethod.addParameter("grant_type", "authorization_code");
        postMethod.addParameter("code", code);
        postMethod.addParameter("client_id", QQConnectConfig.getValue("app_ID"));
        postMethod.addParameter("client_secret", WeiboConfig.getValue("app_KEY"));
        postMethod.addParameter("redirect_uri", WeiboConfig.getValue("redirect_URI"));

//        postMethod.addParameter("client_id", "1144861424");
//        postMethod.addParameter("client_secret","9e3ee83d8c584d5cfbd53de5a0658083");
//        postMethod.addParameter("redirect_uri","http://www.yunwar.com");
        HttpClient client = new HttpClient();
        client.executeMethod(postMethod);
        responseDate = postMethod.getResponseBodyAsString();
        if (!responseDate.equals("") && responseDate.indexOf("access_token") != -1) {
            JSONObject jsonData = JSONObject.fromObject(responseDate);
            token = (String) jsonData.get("access_token");
        }
        return token;
    }

    public static void main(String[] args) {
        try {
            System.out.println(getSinaAccessToken("f6fa2938b701804494b6fc2a39d52c39"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
