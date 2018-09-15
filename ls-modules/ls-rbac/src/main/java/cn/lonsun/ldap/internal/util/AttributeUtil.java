package cn.lonsun.ldap.internal.util;

import javax.naming.directory.Attribute;

import org.apache.commons.lang.StringUtils;

/**
 * 用于Attribute获取存储中其中的值
 * @author Administrator
 *
 */
public class AttributeUtil {
	
	/**
	 * 取值
	 * @param key
	 * @param attr
	 * @return
	 */
	public static String getValue(String key,Attribute attr){
		if(StringUtils.isEmpty(key)){
			throw new NullPointerException();
		}
		String as = attr==null?null:attr.toString();
		if(as!=null){
			as = as.split(": ")[1];
		}
		return as;
	}

}
