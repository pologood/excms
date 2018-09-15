package cn.lonsun.wechatmgr.internal.wechatapiutil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.WeChatAccountsInfoEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatAccountsInfoService;

/**
 * 
 * @ClassName: HttpAccessUtil
 * @Description:Java Http request Util
 * @author Hewbing
 * @date 2015年12月11日 下午5:10:11
 *
 */
public class HttpAccessUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpAccessUtil.class);
    private static IWeChatAccountsInfoService weChatAccountsInfoService = SpringContextHolder.getBean("weChatAccountsInfoService");

    /**
     * 
     * @Title: HttpGet
     * @Description: GET request
     * @param url
     *            request url
     * @return JSONObject return type
     * @throws
     */
    public static JSONObject HttpGet(String url) {
        JSONObject json = null;
        try {
            URL l = new URL(url);
            URI uri = new URI(l.getProtocol(), l.getHost(), l.getPath(), l.getQuery(), null);
            HttpGet request = new HttpGet(uri);
            HttpResponse httpResponse = HttpClients.createDefault().execute(request);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String entityUtils = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                json = JSONObject.fromObject(entityUtils);
                logger.info("Connect " + url + " HttpGet Success ! return JSONObject >> " + entityUtils);
                if (entityUtils.contains("\"errcode\":40014") || entityUtils.contains("\"errcode\":42001")) {
                    Long siteId = LoginPersonUtil.getSiteId();
                    WeChatAccountsInfoEO info = weChatAccountsInfoService.getInfoBySite(siteId);
                    TokenHander.CatchMap.remove(info.getAppId());
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "TOKEN 失效或被重置 请稍候再试！");
                }
            } else {
                logger.error("Connect HttpGet Error >> " + url);
                throw new BaseRunTimeException(TipsMode.Message.toString(), "访问微信服务器失败，请稍候再试");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 
     * @Title: HttpPost
     * @Description: POST request
     * @param url
     *            request url
     * @param jsonData
     *            request param
     * @return JSONObject return type
     * @throws
     */
    public static JSONObject HttpPost(String url, String jsonData) {
        JSONObject json = null;
        try {
            URL l = new URL(url);
            URI uri = new URI(l.getProtocol(), l.getHost(), l.getPath(), l.getQuery(), null);
            HttpPost post = new HttpPost(uri);
            CloseableHttpClient client = HttpClients.createDefault();
            StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);// 解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
            HttpResponse httpResponse = client.execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String entityUtils = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                json = JSONObject.fromObject(entityUtils);
                logger.info("Connect " + url + " HttpPost Success! return JSONObject >> " + entityUtils);
                if (entityUtils.contains("\"errcode\":40014") || entityUtils.contains("\"errcode\":42001")) {
                    Long siteId = LoginPersonUtil.getSiteId();
                    WeChatAccountsInfoEO info = weChatAccountsInfoService.getInfoBySite(siteId);
                    TokenHander.CatchMap.remove(info.getAppId());
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "TOKEN 失效或被重置 请稍候再试！");
                }
            } else {
                logger.error("Connect HttpPost Error >> " + url);
                throw new BaseRunTimeException(TipsMode.Message.toString(), "访问微信服务器失败，请稍候再试");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
