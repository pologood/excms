package cn.lonsun.base.util;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * http连接池 ADD REASON. <br/>
 *
 * @author fangtinghua
 * @date: 2016年1月13日 上午8:57:13 <br/>
 */
public class HttpClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

    public static final String REQUEST_AUTHORIZATION = HttpClientUtils.class.getName() + ".Authorization"; // http请求认证信息
    public static final String REQUEST_ENTITY = HttpClientUtils.class.getName() + ".Entity"; // http entity请求，主要用在post请求上，把内容直接当做entity提交
    public static final String REQUEST_GET = "GET"; // get请求
    public static final String REQUEST_POST = "POST";// post请求
    public static final String REQUEST_PUT = "PUT"; // put请求
    public static final String REQUEST_DELETE = "DELETE"; // delete请求

    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    private static final String encoding = "UTF-8";// 编码
    private static final int timeout = 120000;// 超时时间 2分钟
    private static PoolingHttpClientConnectionManager cm = null;
    private static CloseableHttpClient httpClient = null;
    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
            .setConnectionRequestTimeout(timeout).build();

    /**
     * 连接池初始化
     */
    static {
        Registry<ConnectionSocketFactory> registry =
                RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
        cm = new PoolingHttpClientConnectionManager(registry);
        // Create message constraints
        MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200).setMaxLineLength(2000).build();
        // Create connection configuration
        ConnectionConfig connectionConfig =
                ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE)
                        .setCharset(Consts.UTF_8).setMessageConstraints(messageConstraints).build();
        cm.setDefaultConnectionConfig(connectionConfig);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(30);
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new DefaultHttpRequestRetryHandler();
        httpClient = HttpClients.custom().setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();
    }

    /**
     * get请求
     *
     * @param url    请求url
     * @param params 请求参数
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        return request(REQUEST_GET, url, params);
    }

    /**
     * delete请求
     *
     * @param url    请求url
     * @param params 请求参数
     * @return
     */
    public static String delete(String url, Map<String, String> params) {
        return request(REQUEST_DELETE, url, params);
    }

    /**
     * post
     *
     * @param url    请求url
     * @param params 请求参数
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        return request(REQUEST_POST, url, params);
    }

    /**
     * put
     *
     * @param url    请求url
     * @param params 请求参数
     * @return
     */
    public static String put(String url, Map<String, String> params) {
        return request(REQUEST_PUT, url, params);
    }


    /**
     * http 请求，用来转换为业务json数据
     *
     * @param method 请求方法，为get、post、put、delete方法
     * @param url    请求的url路径
     * @param params 请求参数体
     * @return
     */
    public static String request(String method, String url, Map<String, String> params) {
        if (StringUtils.isEmpty(method) || StringUtils.isEmpty(url)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "http请求缺少method或url！");
        }
        // 构建请求参数体
        HttpRequestBase httpRequestBase = buildHttpRequest(method, url, params);
        return requestString(httpRequestBase);
    }

    /**
     * http 请求，多用于文件下载
     *
     * @param method 请求方法，为get、post、put、delete方法
     * @param url    请求的url路径
     * @param params 请求参数体
     * @return
     */
    public static byte[] requestByte(String method, String url, Map<String, String> params) {
        if (StringUtils.isEmpty(method) || StringUtils.isEmpty(url)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "http请求缺少method或url！");
        }
        // 构建请求参数体
        HttpRequestBase httpRequestBase = buildHttpRequest(method, url, params);
        return requestByte(httpRequestBase);
    }

    /**
     * 封装请求体参数
     *
     * @param method 请求方法，为get、post、put、delete方法
     * @param url    请求url
     * @param params 请求参数
     * @return
     * @update 20180314 by zhongjun:添加时间戳参数，防止每次请求都是相同的数据
     */
    private static HttpRequestBase buildHttpRequest(String method, String url, Map<String, String> params) {
        try {
            String authorization = "";// 认证信息
            HttpRequestBase httpRequestBase = null;
            // Create HttpGet or HttpDelete
            if (REQUEST_GET.equals(method) || REQUEST_DELETE.equals(method)) {
                URIBuilder builder = new URIBuilder(url);
                if (null != params && !params.isEmpty()) {
                    authorization = params.remove(REQUEST_AUTHORIZATION); // 获取认证信息
                    for (String key : params.keySet()) {
                        builder.addParameter(key, params.get(key));
                    }
                }
                builder.addParameter("timestamp", String.valueOf(System.currentTimeMillis()));
                URI uri = builder.build();
                httpRequestBase = REQUEST_GET.equals(method) ? new HttpGet(uri) : new HttpDelete(uri);
            }// Create HttpGet or HttpDelete
            else if (REQUEST_POST.equals(method) || REQUEST_PUT.equals(method)) {
                httpRequestBase = REQUEST_POST.equals(method) ? new HttpPost(url) : new HttpPut(url);
                if (null != params && !params.isEmpty()) {
                    authorization = params.remove(REQUEST_AUTHORIZATION); // 获取认证信息
                    String entity = params.remove(REQUEST_ENTITY); // entity内容
                    HttpEntityEnclosingRequestBase requestBase = (HttpEntityEnclosingRequestBase) httpRequestBase;
                    if (StringUtils.isNotEmpty(entity)) { // entity代表json提交
                        requestBase.setEntity(new StringEntity(entity, Consts.UTF_8));
                    } else {
                        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
                        for (Entry<String, String> entry : params.entrySet()) {
                            paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                        }
                        paramsList.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
                        requestBase.setEntity(new UrlEncodedFormEntity(paramsList, Consts.UTF_8));
                    }
                }
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "不支持的http方法请求！");
            }
            httpRequestBase.setConfig(requestConfig);
            httpRequestBase.setHeader("User-Agent", userAgent);// 模拟浏览器请求
            if (StringUtils.isNotEmpty(authorization)) { // 添加认证信息
                httpRequestBase.setHeader("Authorization", authorization);
            }
            return httpRequestBase;
        } catch (Throwable e) {
            LOGGER.error("http请求错误，请稍后重试！", e);
            throw new BaseRunTimeException(TipsMode.Message.toString(), "http请求错误，请稍后重试！");
        }
    }

    /**
     * http请求
     *
     * @param httpRequestBase 请求体
     * @return 返回字符格式
     */
    private static String requestString(HttpRequestBase httpRequestBase) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpRequestBase);
            int status = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == status) {// 正常响应
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, encoding);
            }
            LOGGER.error(String.format("http请求错误，状态码：%s！", status));
            throw new BaseRunTimeException(TipsMode.Message.toString(), "http请求错误，请稍后重试！");
        } catch (Throwable e) {
            LOGGER.error("http请求错误，请稍后重试！", e);
            throw new BaseRunTimeException(TipsMode.Message.toString(), "http请求错误，请稍后重试！");
        } finally {
            closeResponse(response, httpRequestBase);
        }
    }

    /**
     * http请求
     *
     * @param httpRequestBase 请求体
     * @return 返回字节格式
     */
    private static byte[] requestByte(HttpRequestBase httpRequestBase) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpRequestBase);
            int status = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == status) {// 正常响应
                HttpEntity entity = response.getEntity();
                return EntityUtils.toByteArray(entity);
            }
            LOGGER.error(String.format("http请求错误，状态码：%s！", status));
            throw new BaseRunTimeException(TipsMode.Message.toString(), "http请求错误，请稍后重试！");
        } catch (Throwable e) {
            LOGGER.error("http请求错误，请稍后重试！", e);
            throw new BaseRunTimeException(TipsMode.Message.toString(), "http请求错误，请稍后重试！");
        } finally {
            closeResponse(response, httpRequestBase);
        }
    }

    /**
     * 关闭
     *
     * @param response http响应
     * @param request  http请求
     */
    private static void closeResponse(CloseableHttpResponse response, HttpRequestBase request) {
        try {
            if (null != response) {
                response.close();
            }
            if (null != request) {
                request.releaseConnection();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        for(int i = 0; i<10; i++){
            System.out.println(HttpClientUtils.get("http://59.39.89.100/wcm/RefersServlet?areaCode=3&sType=1&currPage=1&pageSize=6", Collections.<String, String>emptyMap()));
            Thread.sleep(1000l);
        }
    }
}