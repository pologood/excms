package cn.lonsun.webservice.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

/**
 * Session管理器
 *  
 * @author xujh 
 * @date 2014年10月16日 上午9:25:39
 * @version V1.0
 */
public class SessionContainer {
	
	/**
	 * session容器,key-sessionId,value-session
	 */
	private static Map<String,HttpSession> sessions = new HashMap<String,HttpSession>(500);
	
	/**
	 * 向session容器中添加session
	 * @param key
	 * @param value
	 */
	public static void put(String uid,HttpSession session){
		if(StringUtils.isEmpty(uid)||uid==null){
			throw new NullPointerException();
		}
		sessions.put(uid, session);
	}
	
	/**
	 * 注销session
	 * @param key
	 */
	public static void invalidate(String uid,String sessionId){
		if(StringUtils.isEmpty(uid)||StringUtils.isEmpty(sessionId)){
			throw new NullPointerException();
		}
		HttpSession session = sessions.get(uid);
		if(session!=null&&session.getId().equals(sessionId)){
			//让session失效
			session.invalidate();
		}
		//从容器中移除session
		sessions.remove(uid);
	}
	
	/**
	 * 移除容器中已失效的session，需要做一个定时任务来运行
	 */
	public static void removeInvalidated(){
		Set<String> keys = sessions.keySet();
		if(keys!=null&&keys.size()>0){
			for(String key:keys){
				HttpSession session = sessions.get(key);
				if(session==null){
					sessions.remove(key);
				}
			}
		}
	}
}
