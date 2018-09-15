package cn.lonsun.ldap.internal.util;

/**
 * LDAP常量定义
 * @author xujh
 *
 */
public interface Constants {
	//LDAP根节点的DN
	public static final String ROOT_DN = "dc=lonsun,dc=cn";
//	public static final String ROOT_DN = "dc=hefei,dc=gov,dc=net";
	
	public static final String ORGAN_DN_KEY = "o";
	
	public static final String ORGANUNIT_DN_KEY = "ou";

	public static final String PERSON_DN_KEY = "cn";
	/**
	 * LDAP树Cache是否开启，启动时读取配置文件来处理,0:不开启，1：开启
	 */
	public static final boolean IS_LDAP_CACHE_ON = false;
	/**
	 * 应用图标上传路径
	 */
	public static final  String INDICATOR_UPLOAD_PATH = "/upload/indicator/";
}
