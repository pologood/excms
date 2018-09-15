package cn.lonsun.staticcenter.generate.util;

/**
 * @author gu.fei
 * @version 2016-08-15 17:03
 */
public class UrlUtil {

    /**
     * 链接添加参数
     * @param url
     * @param param
     * @return
     */
    public static String addParam(String url,String param) {
        if(null == url) {
            return "";
        }

        if(url.contains("?")) {
            return url + "&" + param;
        } else {
            return url + "?" + param;
        }
    }
}
