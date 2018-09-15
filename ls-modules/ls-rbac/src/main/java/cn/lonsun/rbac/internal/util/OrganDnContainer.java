package cn.lonsun.rbac.internal.util;

import java.util.HashMap;
import java.util.Map;

public class OrganDnContainer {
	
	//容器
	private static Map<String,String> map = new HashMap<String,String>(3000);
	
	/**
	 * 存储
	 *
	 * @param simpleDn
	 * @param name
	 */
	public static void put(String simpleDn,String name){
		map.put(simpleDn, name);
	}
	
	/**
	 * 获取姓名
	 *
	 * @param simpleDn
	 * @return
	 */
	public static String getName(String simpleDn){
		return map.get(simpleDn);
	}
	
	/**
	 * 移除已存在的内容
	 *
	 * @param simpleDn
	 */
	public static void remove(String simpleDn){
		map.remove(simpleDn);
	}
}
