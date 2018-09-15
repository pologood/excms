package cn.lonsun.site.words.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author gu.fei
 * @version 2016-5-4 15:46
 */
public class FileUtil {

    /**
     * 根据资源路径获取资源流
     * @param urlStr
     * @return
     */
    public static InputStream getFileFromUrl(String urlStr) throws Exception{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        return inputStream;
    }
}
