package cn.lonsun.core.util;

import java.util.regex.Pattern;

/**
 * 
 * <p>类名 : DateValidateUtils</p>
 * <p>功能模块 : 我的日程</p>
 * <p>描述 : 验证时间的工具类</p>
 * <p>公司 : lonsun</p>
 * @author 朱磊
 * @date 2014年9月9日
 */
public class DateValidateUtils {
	private static final String DATEFORMAT_REX = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
	
	/**
	 * <p>功能描述 : 验证日期时间的格式是否合法</p>
	 * @param date
	 * @return
	 */
	public static boolean isLegalDate(final String date){
		return Pattern.compile(DATEFORMAT_REX).matcher(date).matches();
	}
}
