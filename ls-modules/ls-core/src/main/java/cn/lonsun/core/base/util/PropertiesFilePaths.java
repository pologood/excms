package cn.lonsun.core.base.util;

/**
 * 配置文件路径
 *  
 * @author xujh 
 * @date 2014年9月24日 下午5:08:35
 * @version V1.0
 */
public interface PropertiesFilePaths {
	
	/**
	 * 开关配置文件路径
	 */
	public static String SWITCH_PATH = "/WEB-INF/classes/switch.properties";
	
	/**
	 * 系统环境变量配置文件路径
	 */
	public static String SYSTEM_ENVIRONMENT_PATH = "/WEB-INF/classes/system-config.properties";

	/**
	 * 可以跳过权限直接访问的资源
	 */
	public static String LEGAL_ACCESS_RESOURCES_PATH = "/WEB-INF/classes/legalAccessResources.properties";
	
	/**
	 * 系统退出所有应用的webService调用接口
	 */
	public static String WEB_SERVICE_LOGOUT_PATH = "/WEB-INF/classes/webService/logout.properties";
	
	/**
	 * 系统其他配置项
	 */
	public static String OTHER_PROPERTIES_PATH = "/WEB-INF/classes/other.properties";
}
