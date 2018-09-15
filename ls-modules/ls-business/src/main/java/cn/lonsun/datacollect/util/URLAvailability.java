package cn.lonsun.datacollect.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author gu.fei
 * @version 2016-07-30 10:33
 */
public class URLAvailability {

    private static Logger logger = LoggerFactory.getLogger(URLAvailability.class);
    private static URL url;
    private static HttpURLConnection con;
    private static int state = -1;

    /**
     * 功能：检测当前URL是否可连接或是否有效,
     * 描述：最多连接网络 5 次, 如果 5 次都不成功，视为该地址不可用
     *
     * @param urlStr 指定URL网络地址
     * @return URL
     */
    public synchronized static boolean isConnect(String urlStr) {
        int counts = 0;
        if (urlStr == null || urlStr.length() <= 0) {
            return false;
        }
        while (counts < 3) {
            try {
                url = new URL(urlStr);
                con = (HttpURLConnection) url.openConnection();
                state = con.getResponseCode();
                if (state == 200) {
                    break;
                }
            } catch (Exception ex) {
                counts++;
                logger.info("URL不可用，连接第 " + counts + " 次");
                continue;
            }
        }

        if (state == 200) {
            logger.info("URL可用！");
            return true;
        }

        return false;
    }
}
