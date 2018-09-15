package cn.lonsun.core.base.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义字符串工具类
 *  
 * @author xujh 
 * @date 2014年11月3日 上午9:14:51
 * @version V1.0
 */
public class StringUtils {
	
	/**
	 * a-z,0-9生成的获取随机字符串
	 *
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { //length表示生成字符串的长度
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }   

	
	/**
	 * 判断字符串是否为空，null、空字符串或空白字符串都判断为空
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s){
		return s == null||s.trim().length() == 0;
	}

	/**
	 * 将字符串转换成utf-8编码
	 *
	 * @param str
	 * @return
	 */
	public static String toUtf(String str){
		try {
			if(!isEmpty(str)){
				str =  new String(str.getBytes("iso8859-1"), "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 字符串分割
	 *
	 * @param target 需要被分割的字符串
	 * @param separator  分割符号,如果为空，会抛出IllegalArgumentException
	 * @return
	 */
	public static List<Long> getListWithLong(String target,String separator){
		if(isEmpty(target)){
			return null;
		}
		if(isEmpty(separator)){
			throw new IllegalArgumentException();
		}
		String[] elements = target.split(separator);
		List<Long> list = new ArrayList<Long>(elements.length);
		for(String element:elements){
			if(!StringUtils.isEmpty(element)){
				Long e = Long.valueOf(element);
				list.add(e);
			}
		}
		return list;
	}
	
	/**
	 * 字符串分割
	 *
	 * @param target 需要被分割的字符串
	 * @param separator  分割符号,如果为空，会抛出IllegalArgumentException
	 * @return
	 */
	public static Long[] getArrayWithLong(String target,String separator){
		if(isEmpty(target)){
			return null;
		}
		if(isEmpty(separator)){
			throw new IllegalArgumentException();
		}
		String[] elements = target.split(separator);
		Long[] array = new Long[elements.length];
		for(int i=0;i<elements.length;i++){
			String element = elements[i];
			Long e = Long.valueOf(element);
			array[i]= e;
		}
		return array;
	}
	
	/**
	 * 字符串分割
	 *
	 * @param target 需要被分割的字符串
	 * @param separator  分割符号,如果为空，会抛出IllegalArgumentException
	 * @return
	 */
	public static List<String> getListWithString(String target,String separator){
		if(isEmpty(target)){
			return null;
		}
		if(isEmpty(separator)){
			throw new IllegalArgumentException();
		}
		String[] elements = target.split(separator);
		List<String> list = new ArrayList<String>(elements.length);
		for(String element:elements){
			list.add(element);
		}
		return list;
	}
	
	/**
	 * 去除特殊字符
	 *
	 * @param target
	 * @return
	 */
	public static String replaceSpecialCharacter(String target){
		String regEx="[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】《》‘；：”“’。，、？]";
        Pattern   p   =   Pattern.compile(regEx);      
        Matcher   m   =   p.matcher(target);      
        return   m.replaceAll("").trim();
	}
	
	/**
	 * 获取字符串的编码格式
	 *
	 * @param str
	 * @return
	 */
	public static String getEncoding(String str) {
		String[] encodes = new String[]{"ISO-8859-1","UTF-8","GB2312","GBK"};
		String encode = "UTF-8";
		for(String e:encodes){
			try {
				//如果已获取到匹配的字符串格式，那么直接跳出循环
				if (str.equals(new String(str.getBytes(e), e))) {
					encode = e;
					break;
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		return encode;
    }

	/**
	 * 字符串多个空格替换成制定字符串
	 * @param s
	 * @param separator
     * @return
     */
	public static String rplcBlank(String s , String separator) {
		if(null == s) {
			return null;
		}
		if(null == separator) {
			separator = ",";
		}
		String regEx = "[' ']+"; // 一个或多个空格
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(s.trim());
		return m.replaceAll(separator).trim();
	}

	/**
	 * 提出字符串中多余空格
	 * @param s
	 * @return
     */
	public static String mergeBlank(String s) {
		int numberBlank = 0;
		String a1;//字符串的第一部分
		String a2;//字符串的第二部分
		for (int index = 0; index < s.length(); index++) {//循环整个字符串，判断是否有连续空格
			numberBlank = getBlankNumber(s, index);
			if (numberBlank >= 2) {//根据连续空格的个数以及当前的位置，截取字符串
				a1 = s.substring(0, index);
				a2 = s.substring(index + numberBlank - 1, s.length());
				s = a1 + a2;//合并字符串
			}
		}
		s = trim(s);
		s.replaceAll(" ",",");
		return s;
	}

	/**
	 * 去除字符串前后空格
	 * @param s
	 * @return
     */
	public static String trim(String s) {
		if (s.charAt(0) == ' ') {
			s = s.substring(1, s.length());
		}
		if (s.charAt(s.length() - 1) == ' ') {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	private static int getBlankNumber(String s, int index) {
		if (index < s.length()) {
			if (s.charAt(index) == ' ') {
				return getBlankNumber(s, index + 1) + 1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * 正则表达式匹配
	 * @param content
	 * @param regex
	 * @return
	 */
	public static List<String> getRegexList(String content,String regex){
		List<String> list = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);// 匹配的模式
		Matcher m = pattern.matcher(content);
		while (m.find()) {
			int i = 1;
			list.add(m.group(i));
			i++;
		}
		return list;
	}

	public static void main(String args[]) {
//		String s = " 我爱          五星红旗 ";
//		System.out.println(StringUtils.rplcBlank(s.trim(),","));
		String content = "<a class=\"ke-insertfile\" href=\"/download/58bfcdbbe4b07d69a53e4311\" target=\"_blank\"><img src=\"/assets/images/files/doc.png\" height=\"16\" width=\"16\" /> 附件-2017年度县政府重点工作任务分解表.doc</a></span></span> ";
		List<String> list = getRegexList(content,"<a\\s+[^<>]*\\s+href=\"/download/([^<>\"]*)\"[^<>]*>");
		System.out.println("");
	}
}
