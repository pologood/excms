package cn.lonsun.core.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.lonsun.core.base.util.StringUtils;

/**
 * Hql工具类
 *
 * @author xujh
 * @version 1.0
 * 2014年12月11日
 *
 */
public class HqlUtil {
	
	/**
	 * Hql字符串like处理，不会对入参进行验证，请在调用前确定参数正确性
	 *
	 * @param objKey 属性所属的对象在hql中的名称
	 * @param map 支持多个属性的key(属性名)-value(属性值)
	 * @param hql
	 * @param values
	 */
	public static void prepareLike(String objKey,Map<String,String> map,StringBuffer hql,List<Object> values){
		Set<String> keys = map.keySet();
		for(String key:keys){
			String value = map.get(key);
			prepareLike(objKey, key, value, hql, values);
		}
	}
	
	/**
	 * Hql"="处理，不会对入参进行验证，请在调用前确定参数正确性
	 *
	 * @param objKey 属性所属的对象在hql中的名称
	 * @param map 支持多个属性的key(属性名)-value(属性值)
	 * @param hql
	 * @param values
	 */
	public static void prepareEqual(String objKey,Map<String,String> map,StringBuffer hql,List<Object> values){
		Set<String> keys = map.keySet();
		for(String key:keys){
			String value = map.get(key);
			prepareEqual(objKey, key, value, hql, values);
		}
	}
	
	/**
	 * Hql字符串like处理，不会对入参进行验证，请在调用前确定参数正确性
	 *
	 * @param objKey 属性所属的对象在hql中的名称
	 * @param eKey 属性名
	 * @param eValue 属性值
	 * @param hql
	 * @param values
	 */
	public static void prepareLike(String objKey,String eKey,String eValue,StringBuffer hql,List<Object> values){
		if(!StringUtils.isEmpty(eValue)){
			String target = "%".concat(SqlUtil.prepareParam4Query(eValue)).concat("%");
			hql.append(" and ").append(objKey).append(".").append(eKey).append(" like ?  escape '\\' ");
			values.add(target);
		}
	}
	
	/**
	 * Hql"="处理，不会对入参进行验证，请在调用前确定参数正确性
	 *
	 * @param objKey 属性所属的对象在hql中的名称
	 * @param eKey 属性名
	 * @param eValue 属性值
	 * @param hql
	 * @param values
	 */
	public static void prepareEqual(String objKey,String eKey,Object eValue,StringBuffer hql,List<Object> values){
		hql.append(" and ").append(objKey).append(".").append(eKey);
		if(eValue==null){
			hql.append(" is null");
		}else{
			if (eValue instanceof String) {
				eValue = SqlUtil.prepareParam4Query(eValue.toString());
			}
			hql.append(" = ?");
			values.add(eValue);
		}
	}

}
