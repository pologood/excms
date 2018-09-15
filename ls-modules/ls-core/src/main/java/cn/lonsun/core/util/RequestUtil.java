package cn.lonsun.core.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestUtil {

    private RequestUtil() {
        throw new Error();
    }

    /**
     * 获取请求的IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (!StringUtils.isEmpty(ip) && ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }
        // 多个路由时，取第一个非unknown的ip
        String[] arr = ip.split(",");
        for (String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ip = str;
                break;
            }
        }
        return ip;
    }

    /**
     * 获取请求许可key
     *
     * @param request
     * @return
     */
    public static String getAccessKey(HttpServletRequest request) {
        String accessKey = null;
        Long userId = SessionUtil.getLongProperty(request.getSession(), "userId");
        if (userId != null) {
            String url = request.getRequestURI();
            String ip = RequestUtil.getIpAddr(request);
            // accessKey组成具有唯一性：userId_ip_请求链接
            accessKey = userId.toString().concat("_").concat(ip).concat("_").concat(url);
        }
        return accessKey;
    }

    /**
     * 在req中获取对象类型为String的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static String getStringProperty(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getAttribute(key);
        return object == null ? null : object.toString();
    }

    /**
     * 在req中获取对象类型为Long的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static Long getLongProperty(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getAttribute(key);
        return object == null ? null : Long.valueOf(object.toString());
    }

    /**
     * 在req中获取对象类型为Integer的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static Integer getIntegerProperty(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getAttribute(key);
        return object == null ? null : Integer.valueOf(object.toString());
    }

    /**
     * 在req中获取对象类型为Float的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static Float getFloatProperty(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getAttribute(key);
        return object == null ? null : Float.valueOf(object.toString());
    }

    /**
     * 在req中获取对象类型为Double的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static Double getDoubleProperty(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getAttribute(key);
        return object == null ? null : Double.valueOf(object.toString());
    }

    /**
     * 在req中获取对象类型为String的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static String getStringParameter(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getParameter(key);
        return object == null ? null : object.toString();
    }

    /**
     * 在req中获取对象类型为Long的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static Long getLongParameter(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getParameter(key);
        return object == null ? null : Long.valueOf(object.toString());
    }

    /**
     * 在req中获取对象类型为Integer的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static Integer getIntegerParameter(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getParameter(key);
        return object == null ? null : Integer.valueOf(object.toString());
    }

    /**
     * 在req中获取对象类型为Float的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static Float getFloatParameter(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getParameter(key);
        return object == null ? null : Float.valueOf(object.toString());
    }

    /**
     * 在req中获取对象类型为Double的数据
     *
     * @param req
     * @param key
     * @return
     */
    public static Double getDoubleParameter(ServletRequest req, String key) {
        if (req == null || key == null || "".equals(key)) {
            return null;
        }
        Object object = req.getParameter(key);
        return object == null ? null : Double.valueOf(object.toString());
    }

}
