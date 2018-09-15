package cn.lonsun.sms.czsms.util;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 与HTTP服务器交互的相关工具类
 * @author ipi
 */
@SuppressWarnings("deprecation")
public class HttpTools
{

    private static HttpTools tools = new HttpTools();
    public static HttpClient client = null;
    private static Logger log = Logger.getLogger("HttpTools");
    static
    {
        client = new HttpClient();

        // 设置连接超时时间(单位毫秒)
        client.setConnectionTimeout(30000);
        // 设置读数据超时时间(单位毫秒)
        client.setTimeout(30000);
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setMaxTotalConnections(5);
        connectionManager.setMaxConnectionsPerHost(2);
        // 把连接管理放置到httpClient
        client.setConnectionTimeout(30 * 1000);
        client.setTimeout(180 * 1000);
        client.setHttpConnectionManager(connectionManager);
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(0, false));// 失败自动重试0次

    }

    /**
     * 向Http服务器发送相应请求，将返回服务器响应的方法
     * @param httpServerUrl：Http请求地址
     * @param httpReqXml：请求的报文
     * @param map 参数
     * @param encoding 编码
     * @return：响应报文
     * @throws HttpException
     * @throws IOException
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public static String sendHttpReqToServer(String httpServerUrl,
                                             Map<String, String> map)
    {

        // System.out.println("开始向Http服务端发送请求，请求地址：" + httpServerUrl + ",请求报文："
        // +
        // httpReqXml);

        String response = null;

        PostMethod postMethod = null;
        BufferedReader br = null;
        try
        {
            postMethod = new PostMethod(httpServerUrl);
            postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(0, false));
            postMethod.getParams().setParameter(
                    HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
            // postMethod
            // .addRequestHeader("Content-Type", "text/html;charset=GBK");
            // postMethod
            // .setRequestHeader("Content-Type", "text/html;charset=GBK");
            // postMethod.addRequestHeader("Content-Type","text/html;charset=GBK");
            // if (httpReqXml != null && !httpReqXml.equals(""))
            // {
            // byte buf[] = httpReqXml.getBytes(encoding);
            // ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            // InputStream in = new BufferedInputStream(bais);
            // postMethod.setRequestBody(in);
            // // postMethod.setQueryString(httpReqXml);
            // }

            if (map != null)
            {
                /*
                 * Set<String> set = map.keySet(); for (String object : set) {
                 * postMethod.addParameter(object, map.get(object)); }
                 */

                for (Map.Entry<String, String> object : map.entrySet())
                {
                    postMethod.addParameter(object.getKey(), object.getValue());
                }
            }

            int statusCode = client.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK)
            {
                log.info("Http请求成功");
            }
            else
            {
                log.warn("Http请求失败:" + statusCode);
            }
            // String enCode = encoding;
            // if (StringUtils.isEmpty(enCode))
            // {
            // enCode = "UTF-8";
            // System.out.println("使用默认编码处理:" + enCode);
            // }
            InputStreamReader isr = new InputStreamReader(postMethod
                    .getResponseBodyAsStream(), "GBK");
            br = new BufferedReader(isr);
            String eachLine = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (eachLine != null)
            {
                sb.append(eachLine);
                eachLine = br.readLine();
            }

            response = sb.toString();
        }
        catch (Exception e)
        {
            log.error("向Http服务端发送请求时发生异常，请检查服务端地址和参数", e);
        }
        finally
        {
            try
            {
                if (br != null)
                    br.close();

                if (postMethod != null)
                    postMethod.releaseConnection();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            // client = null;
            postMethod = null;
        }
        log.debug("响应HTML："+response);
        return response;
    }

    /**
     *
     * 自定义HttpClient恢复策略，此处主要是屏蔽掉HttpClient的失败重发三次功能
     *
     */
    private static class CustomHttpMethodRetryHandler implements
            HttpMethodRetryHandler
    {

        public boolean retryMethod(HttpMethod arg0, IOException arg1, int arg2)
        {
            return false;
        }

    }
}
