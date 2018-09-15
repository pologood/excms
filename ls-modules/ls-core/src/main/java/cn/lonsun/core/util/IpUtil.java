package cn.lonsun.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {
	private static Logger logger = LoggerFactory.getLogger(IpUtil.class);
	private static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		String s = null;
		if (!(o instanceof String)) {
			s = o.toString();
		} else {
			s = (String) o;
		}

		if (s == null || s.trim().length() == 0 || "null".equals(s)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取登录用户IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		return RequestUtil.getIpAddr(request);
	}

}
