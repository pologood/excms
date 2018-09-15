package cn.lonsun.core.util;

import org.apache.commons.lang.StringEscapeUtils;


public final class SqlUtil {
	/**
	 * 工具类，不允许实例化
	 */
	private SqlUtil(){
		throw new Error();
	}
	
	/**
	 * 查询参数预处理
	 * 防sql注入，并替换掉特殊字符%和_，
	 * like的sql需要特殊处理：like结束后需要添加 escape '\\'"
	 * @param param
	 * @return
	 */
	public static String prepareParam4Query(String param){
		if(param!=null){
			param = StringEscapeUtils.escapeSql(param);
			param = param.replace("%", "\\%");
			param = param.replace("_", "\\_");
		}
		return param;
	}
}
