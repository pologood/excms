package cn.lonsun.util;

import cn.lonsun.common.util.AppUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * Http请求工具类 
 * @author gu.fei
 * @version 2016-11-24 14:08
 */
public class HttpRequestUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

    //post请求方式
    private final static String REQUEST_POST = "POST";
    //get请求方式
    private final static String REQUEST_GET = "GET";

    /**
     *
     * @param url
     *      发送请求的URL
     * @param param
     *      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param requestType
     *      请求类型 post,get 默认get类型
     * @return
     */
    public static Object sendRequest(String url, String param,String requestType,String returnType) {
        if(null != requestType) {
            if(requestType.equalsIgnoreCase(REQUEST_POST)) {
                return sendPost(url,param,returnType);
            }
        }

        return  sendGet(url,param);
    }

    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static JSONObject sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        JSONObject json = null;
        try {

            if(!StringUtils.isEmpty(param)) {
                url += "?" + param;
            }

            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("contentType", "UTF-8");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            json = JSONObject.parseObject(result);
        } catch (Exception e) {
            logger.error("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return json;
    }
 
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static Object sendPost(String url, String param,String returnType) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        Object obj = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 打开和URL之间的连接
             
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");    // POST方法

            // 设置通用的请求属性
             
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
             
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream());
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            if(!AppUtil.isEmpty(returnType) && "jsonArray".equals(returnType)) {
                obj = JSONArray.parseArray(result);
            } else {
                obj = JSONObject.parseObject(result);
            }

        } catch (Exception e) {
            logger.error("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url        请求的URL地址
     * @param params 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static JSONObject doPost(String url, Map<String, String> params) {
        String response = null;
        HttpClient client = new HttpClient();
        HttpMethod method = new PostMethod(url);

        //设置Http Post数据
        if (params != null) {
            HttpMethodParams p = new HttpMethodParams();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                p.setParameter(entry.getKey(), entry.getValue());
            }
            method.setParams(p);
        }
        try {
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                response = method.getResponseBodyAsString();
            }
        } catch (IOException e) {
            logger.error("执行HTTP Post请求" + url + "时，发生异常！", e);
        } finally {
            method.releaseConnection();
        }

        JSONObject json = JSONObject.parseObject(response);
        return json;
    }

    public static JSONObject doPost(String url, Map<String, String> params, String charset) {
        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        // 设置Http Post数据
        method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=" + charset);
        if(params != null){
            Set<String> keySet = params.keySet();
            NameValuePair[] param = new NameValuePair[keySet.size()];
            int i = 0;
            for(String key : keySet){
                param[i] = new NameValuePair(key, params.get(key));
                i++;
            }
            method.setRequestBody(param);
        }
        InputStream responseBodyStream = null;
        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        try {
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                responseBodyStream = method.getResponseBodyAsStream();
                streamReader = new InputStreamReader(responseBodyStream, charset);
                reader = new BufferedReader(streamReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (IOException e) {
            logger.error("执行HTTP Post请求" + url + "时，发生异常！", e);
        } finally {
            try {
                responseBodyStream.close();
                streamReader.close();
                reader.close();
            } catch (IOException e) {
                logger.error("执行HTTP Post请求" + url + "时，发生异常，关闭流异常！", e);
                e.printStackTrace();
            }
            method.releaseConnection();
        }

        return JSONObject.parseObject(response.toString());
    }

    /**
     * 转UTF-8
     * @param xml
     * @return
     */
    public static String getUTF8XMLString(String xml) {
        StringBuffer sb = new StringBuffer();
        sb.append(xml);
        String xmString = "";
        String xmlUTF8="";
        try {
            xmString = new String(sb.toString().getBytes("UTF-8"));
            xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return xmlUTF8;
    }

    public static void main(String[] args) {
//        String str1 = "甯傚浗鍦熸埧浜у眬";
//        String str= null;
//        try {
//            str = new String(str1.getBytes("ISO-8859-1"),"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        System.out.println(str);
        //demo:代理访问
//        String url = "http://bsdt.chuzhou.gov.cn/bsdt/portal/pageProjectInfosList.do";
//        String para = "currentPageNo=1&pageSize=10&xzqhbm=341100000000&subjecttype=1&status=1&sxfl=1&sxmc=&sszt=&subjectValue=";

       /* String url = "http://chuz.ahzwfw.gov.cn/bsdt/zwfw/common/getItemTypeInfo.do";
        String para = "lxjp=A08&SsqdZoneCode=341100000000";
        Object sr = HttpRequestUtil.sendPost(url,para,null);*/
        String url = "http://mas.ahzwfw.gov.cn/xzspserver/services/interface/getItemsImp";
        String para = null;
        try {
            para = "areacode=340500000000&ssqdname="+ URLEncoder.encode("水电","UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject object = HttpRequestUtil.sendGet(url,para);
        if(null != object &&object.get("valid").equals(1)) {
            List<JSONObject> jsonList = (List<JSONObject>)object.get("data");
            if(null != jsonList && jsonList.size() > 0) {
                JSONObject jsonObject = jsonList.get(0);
                Iterator iterator = jsonObject.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String, String> entry = (Map.Entry<String, String>)iterator.next();
                    System.out.println("key:"+entry.getKey()+"--value:"+entry.getValue());
                }
            }

        }
        System.out.println(object);
    }
     
}