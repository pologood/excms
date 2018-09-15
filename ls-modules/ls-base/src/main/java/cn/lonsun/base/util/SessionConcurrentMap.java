package cn.lonsun.base.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpSession;

/**
 * Session管理容器
 *
 * @author xujh
 * @version 1.0
 * 2015年1月26日
 *
 */
public class SessionConcurrentMap {
	//当前用户在Map中存储的key
	public static String CURRENT_USER_KEY = "user";
	//ServletContext中当前用户的数量对应的key
	public static String CURRENT_ACTIVE_USER_COUNT = "activeUserCount";
	//线程安全的Map，用于存放所有已登录用户的session
	public static ConcurrentMap<String,HttpSession> SESSION_MAP = new ConcurrentHashMap<String, HttpSession>(800);

}
