package cn.lonsun.core.exception.util;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.lonsun.core.exception.BaseRunTimeException;

/**
 * 异常提示信息容器
 * @author 徐建华
 *
 */
public class ExceptionTipsMessage {
	
	private static final ExceptionTipsMessage tipsMessage = new ExceptionTipsMessage();;
	
	/**
	 * 系统级提示信息，不可修改
	 * @author Administrator
	 *
	 */
	public enum SystemTipsKey{
		SystemException,//系统异常
		HibernateException,//数据库操作出错，请联系管理员
		DBConnectFailed;//数据库连接失败
	}
	
	private ExceptionTipsMessage(){}
	
	private static Map<String, String> exceptionTipsMessages = new HashMap<String, String>();
	
	/**
	 * 向容器中新增异常信息
	 * @param key
	 * @param value
	 */
	public void add(String key,String value){
		exceptionTipsMessages.put(key, value);
	}
	
	/**
	 * 容器设置为不可修改
	 */
	public void unmodifiable(){
		Collections.unmodifiableMap(exceptionTipsMessages);
	}
	
	/**
	 * 获取异常提示信息
	 * @param be 异常
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String get(BaseRunTimeException be){
		String key = be.getKey();
		Object[] keyWords = be.getKeyWords();
		if(key!=null&&!"".equals(key)){
			String message = exceptionTipsMessages.get(key);
			String encode = cn.lonsun.core.base.util.StringUtils.getEncoding(message).toUpperCase();
			if(!encode.equals("UTF-8")){
				try {
					message = new String(message.getBytes(encode),"UTF-8");
					//对占位符进行处理
					if(keyWords!=null&&keyWords.length>0){
						message = MessageFormat.format(message, keyWords);
					}
				} catch (UnsupportedEncodingException e) {
					message = "系统异常";
					e.printStackTrace();
				}
			}
			return message;
		}else{
			return null;
		}
	}
	
	/**
	 * 获取异常提示信息
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String get(String key){
		if(key!=null&&!"".equals(key)){
			String message = exceptionTipsMessages.get(key);
			String encode = cn.lonsun.core.base.util.StringUtils.getEncoding(message).toUpperCase();
			if(!encode.equals("UTF-8")){
				try {
					message = new String(message.getBytes(encode),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					message = "系统异常";
					e.printStackTrace();
				}
			}
			return message;
		}else{
			return null;
		}
	}
	
	/**
	 * 获取异常提示信息
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String get(String key,Object[] keyWords){
		if(key!=null&&!"".equals(key)){
			String message = exceptionTipsMessages.get(key);
			String encode = cn.lonsun.core.base.util.StringUtils.getEncoding(message).toUpperCase();
			if(!encode.equals("UTF-8")){
				try {
					message = new String(message.getBytes(encode),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					message = "系统异常";
					e.printStackTrace();
				}
			}
			message = MessageFormat.format(message, keyWords);
			return message;
		}else{
			return null;
		}
	}
	
	/**
	 * 获取ExceptionTipsMessage对象，该对象是个单例
	 * @return
	 */
	public static ExceptionTipsMessage getInstance(){
		return tipsMessage;
	}
}
