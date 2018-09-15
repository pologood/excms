package cn.lonsun.core.util;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

/**
 * Session中取值工具
 * @author xujh
 */
public final class SessionUtil {
	
	/**
	 * 不允许实例化
	 */
	private SessionUtil(){
		throw new Error();
	}
	
	/**
	 * 清空session
	 *
	 * @param session
	 */
	public static void clear(HttpSession session){
        Enumeration<String> enums = session.getAttributeNames();
        if(enums!=null){
        	while(enums.hasMoreElements()){
        		String key = enums.nextElement();
        		session.removeAttribute(key);
        	}
        }
	}
	
	
	
	/**
	 * 在Session中获取对象类型为String的数据
	 * @param session
	 * @param key
	 * @return
	 * @throws ParamException
	 */
	public static String getStringProperty(HttpSession session,String key){
		if(session==null||key==null||"".equals(key)){
			return null;
		}
		Object object = session.getAttribute(key);
		return object==null?null:object.toString();
	}
	
	/**
	 * 在Session中获取对象类型为Long的数据
	 * @param session
	 * @param key
	 * @return
	 * @throws ParamException
	 */
	public static Long getLongProperty(HttpSession session,String key){
		if(session==null||key==null||"".equals(key)){
			return null;
		}
		Object object = session.getAttribute(key);
		return object==null?null:Long.valueOf(object.toString());
	}
	
	/**
	 * 在Session中获取对象类型为Integer的数据
	 * @param session
	 * @param key
	 * @return
	 * @throws ParamException
	 */
	public static Integer getIntegerProperty(HttpSession session,String key){
		if(session==null||key==null||"".equals(key)){
			return null;
		}
		Object object = session.getAttribute(key);
		return object==null?null:Integer.valueOf(object.toString());
	}
	
	/**
	 * 在Session中获取对象类型为Float的数据
	 * @param session
	 * @param key
	 * @return
	 * @throws ParamException
	 */
	public static Float getFloatProperty(HttpSession session,String key){
		if(session==null||key==null||"".equals(key)){
			return null;
		}
		Object object = session.getAttribute(key);
		return object==null?null:Float.valueOf(object.toString());
	}
	
	/**
	 * 在Session中获取对象类型为Double的数据
	 * @param session
	 * @param key
	 * @return
	 * @throws ParamException
	 */
	public static Double getDoubleProperty(HttpSession session,String key){
		if(session==null||key==null||"".equals(key)){
			return null;
		}
		Object object = session.getAttribute(key);
		return object==null?null:Double.valueOf(object.toString());
	}
}
