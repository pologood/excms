package cn.lonsun.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 正则表达式验证工具
 * @author xujh
 *
 */
public class RegexUtil {
	
	/**
	 * ip地址验证
	 * @param ip
	 * @return
	 */
	public static boolean isIp(String ip){
		if(StringUtils.isEmpty(ip)){
			return false;
		}
		Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(ip); //以验证127.400.600.2为例
		return matcher.matches();
	}
	
	
    /**
     * 判断电话
     *
     * @param phonenumber
     * @return
     */
    public static boolean isTelephone(String phonenumber) {
        String phone = "0\\d{2,3}-\\d{7,8}";
        Pattern p = Pattern.compile(phone);
        Matcher m = p.matcher(phonenumber);
        return m.matches();
    }

    /**
     * 判断手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobile) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 判断邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
	
	/**
	 * LDAP地址验证
	 * @param ip
	 * @return
	 */
	public static boolean isLdapUrl(String url){
		if(StringUtils.isEmpty(url)){
			return false;
		}
		Pattern pattern = Pattern.compile("^ldap://\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b:389$");
		Matcher matcher = pattern.matcher(url); //以验证ldap://61.191.24.162:389为例
		return matcher.matches();
	}
	
	/**
	 * 是否是字母和数字的组合(a-z,A-Z,0-9)
	 *
	 * @param target
	 * @return
	 */
	public static boolean isCombinationOfCharactersAndNumbers(String target){
		if(StringUtils.isEmpty(target)){
			return false;
		}
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
		Matcher matcher = pattern.matcher(target); 
		return matcher.matches();
	}
	
	/**
	 * 是否是中文、英文字符和数组的组合
	 *
	 * @param target
	 * @return
	 */
	public static boolean isCombinationOfChineseAndCharactersAndNumbers(String target){
		if(StringUtils.isEmpty(target)){
			return false;
		}
		Pattern pattern = Pattern.compile("[]“”、（）【】，。！？《》a-zA-Z0-9\u4E00-\u9FA5]+");
		Matcher matcher = pattern.matcher(target);
		return matcher.matches();
	}
}
