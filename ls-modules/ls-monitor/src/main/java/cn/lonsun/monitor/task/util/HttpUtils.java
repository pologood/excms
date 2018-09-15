package cn.lonsun.monitor.task.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * 工具类
 * @author gu.fei
 * @version 2017-09-20 8:31
 */
public class HttpUtils {

    /**
     * 检测地址是否可以访问
     * @param urlStr 加上协议的地址 例如：http://wwww.xxx.com
     * @param timeout 超时时间
     * @return
     * @throws IOException
     */
    public static int isConnect(String urlStr,int timeout) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(timeout);
        return conn.getResponseCode();
    }

    public static void main(String[] args) {
        try {
            int i = isConnect("http://www.baidu.com",1);
            if(i == 200) {
                System.out.println("网站可以连通");
            }
        } catch (MalformedURLException m) {
            System.out.println("URL协议、格式或者路径错误");
        }  catch (SocketTimeoutException s) {
            System.out.println("链接超时");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
