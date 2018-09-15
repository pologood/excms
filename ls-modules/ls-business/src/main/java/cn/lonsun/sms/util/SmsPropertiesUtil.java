package cn.lonsun.sms.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 短信配置工具类
 */
public class SmsPropertiesUtil {

	// ex8 发送短信配置
	public static String time = null;

	public static String smsid = null;

	public static String smspwd = null;

	public static String smsurl = null;

	public static String domain = null;

	//滁州市政务网站短信配置 Url
	public static String czHttpUrl = null;

	//集团账号
	public static String czEnterAccount = null;

	//用户名
	public static String czUserName = null;

	//密码
	public static String czUserPwd = null;

	private static Properties props = new Properties();
	static{
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sendsms.properties"));
			/*------------------------滁州市政务短信配置------------------------- */
			czHttpUrl = props.getProperty("httpUrl_cz");
			czEnterAccount =  props.getProperty("enterAccount_cz");
			czUserName =  props.getProperty("userName_cz");
			czUserPwd = props.getProperty("userPwd_cz");
			/*--------------------------龙讯oa短信配置----------------------- */
			smsid =  props.getProperty("smsid");
			smspwd =  props.getProperty("smspwd");
			smsurl = props.getProperty("smsurl");
			domain =  props.getProperty("domain");
			/*--------------------------公共配置----------------------- */
			time = props.getProperty("timeout");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
