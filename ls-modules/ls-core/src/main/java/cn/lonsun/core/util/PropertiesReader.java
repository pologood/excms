package cn.lonsun.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cn.lonsun.core.exception.BaseRunTimeException;

/**
 * 配置文件阅读器
 * @author 徐建华
 *
 */
public class PropertiesReader {
	
	/**
	 * 构造器
	 * @param path
	 */
	private PropertiesReader(String path){
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			properties.load(in);
			this.properties = properties;
			this.keys = properties.keySet();
			this.path = path;
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	//配置对象
	private Properties properties;
	//properties 对应的key集合
	private Set<Object> keys;
	//配置文件路径
	private String path;
	
	/**
	 * 获取配置文件阅读器对象,每个配置文件创建一个独立的阅读器
	 * @param path 配置文件路径
	 * @return
	 */
	public static synchronized PropertiesReader getInstance(String path){
		return new PropertiesReader(path);
	}
	
	/**
	 * 获取properties
	 * @return
	 */
	public Properties getProperties(){
		return properties;
	}
	
	/**
	 * 是否有参数key
	 * @param key
	 * @return
	 */
	public boolean contains(String key){
		return properties.containsKey(key);
	}
	
	/**
	 * 是否有参数key
	 * @param key
	 * @return
	 */
	public boolean containsValue(String value){
		return properties.containsValue(value);
	}
	
	/**
	 * 获取配置文件中所有的key
	 * @return
	 */
	public Set<Object> getKeys() {
		return keys;
	}

	/**
	 * 获取配置文件key对应的value
	 * @param key
	 * @return
	 */
	public String getValue(String key){
		if(StringUtils.isEmpty(key)){
			return null;
		}
		return properties.getProperty(key);
	}
	
	/**
	 * 获取配置文件中所有的键值对
	 * @return
	 */
	public Map<String,String> getValues(){
		Map<String,String> values = new HashMap<String,String>();
		if(keys!=null&&keys.size()>0){
			for(Object key:keys){
				if(key==null){
					continue;
				}
				String value = getValue(key.toString());
				values.put(key.toString(), value);
			}
		}
		return values;
	}
	
	
	/**
	 * 更新
	 * @param map properties中的键值对
	 */
	public void update(Map<String,String> map){
		if(map==null||map.size()<=0){
			return;
		}
		Set<String> keys = map.keySet();
		boolean isChanged = false;
		for(String key:keys){
			//如果更新的key不存在，那么直接跳过
			if(StringUtils.isEmpty(key)||!contains(key)){
				continue;
			}
			//属性值不能为空
			String value = map.get(key);
			if(StringUtils.isEmpty(value)){
				continue;
			}
			properties.setProperty(key, value);
			if(!isChanged){
				isChanged = true;
			}
		}
		if(isChanged){
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(this.path);
				//将Properties集合保存到流中 
				properties.store(fos, "system-config.properties"); 
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				throw new BaseRunTimeException();
			} finally{
				//关闭流 
				try {
					if(fos!=null){
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
