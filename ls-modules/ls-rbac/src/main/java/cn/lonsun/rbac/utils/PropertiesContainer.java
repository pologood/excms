package cn.lonsun.rbac.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 配置文件中读取的内容容器
 *  
 * @author xujh 
 * @date 2014年10月17日 下午2:59:44
 * @version V1.0
 */
public class PropertiesContainer {
	
	//静态对象
	private static PropertiesContainer instance = new PropertiesContainer();
	//存储配置的容器
	private Map<String,Map<String,String>> container = new HashMap<String,Map<String,String>>();
	
	public enum FileNames{
		SystemProperties("system-config.properties");//系统管理配置
		
		private String value;
		private FileNames(String value){
			this.value = value;
		}
		public String getValue(){
			return value;
		}
	}
	
	public static PropertiesContainer getInstance(){
		return instance;
	}
	
	/**
	 * 添加配置集合
	 * @param key
	 * @param properties
	 */
	public void put(String key,Map<String,String> properties){
		validateKey(key);
		container.put(key, properties);
	}
	
	/**
	 * 获取配置集合
	 * @param key
	 * @return
	 */
	public Map<String,String> getProperties(String key){
		validateKey(key);
		return container.get(key);
	}
	
	/**
	 * 验证key是否为null
	 * @param key
	 */
	private void validateKey(String key){
		if(StringUtils.isEmpty(key)||key.trim().length()<=0){
			throw new NullPointerException();
		}
	}
}
