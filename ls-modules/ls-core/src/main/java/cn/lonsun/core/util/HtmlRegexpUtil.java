package cn.lonsun.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式
 * @author <a href="mailto:417003654@qq.com">daPeng tao</a>
 * @version v1.00 2012-11-29
 */
public class HtmlRegexpUtil {
	private final static String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签
	@SuppressWarnings("unused")
	private final static String regxpForImgTag = "<\\s*img\\s+([^>]*)\\s*>"; // 找出IMG标签
	@SuppressWarnings("unused")
	private final static String regxpForImaTagSrcAttrib = "src=\"([^\"]+)\""; // 找出IMG标签的SRC属性
	
	
	/**
	 * 过滤所有以"<"开头以">"结尾的标签
	 * @param str
	 * @return
	 */
	public static String filterHtml(String str) { 
		Pattern pattern = Pattern.compile(regxpForHtml); 
		Matcher matcher = pattern.matcher(str); 
		StringBuffer sb = new StringBuffer(); 
		boolean result1 = matcher.find();
		 while (result1) { 
			 matcher.appendReplacement(sb, ""); 
			 result1 = matcher.find();			 
		 }
		 matcher.appendTail(sb);
		 return sb.toString(); 
	}
	/***
	 * 过滤指定标签 
	 * @param str
	 * @param tag
	 * @return
	 */
	public static String fiterHtmlTag(String str, String tag) { 
		String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>"; 
		Pattern pattern = Pattern.compile(regxp);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer(); 
		boolean result1 = matcher.find(); 
		 while (result1) {
			 matcher.appendReplacement(sb, ""); 
			 result1 = matcher.find(); 
		 }
		 matcher.appendTail(sb);
		 return sb.toString();
	}
	/**
	 * 替换指定的标签 
	 * @param str 要替换的标签
	 * @param beforeTag 要替换的标签属性值
	 * @param tagAttrib 新标签开始标记
	 * @param startTag 新标签结束标记
	 * @param endTag 替换img标签的src属性值为[img]属性值[/img] 
	 * @return
	 */
	public static String replaceHtmlTag(String str, String beforeTag,
			String tagAttrib, String startTag, String endTag) { 
		String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>"; 
		String regxpForTagAttrib = tagAttrib + "=\"([^\"]+)\""; 
		Pattern patternForTag = Pattern.compile(regxpForTag);
		Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib); 
		Matcher matcherForTag = patternForTag.matcher(str); 
		StringBuffer sb = new StringBuffer(); 
		boolean result = matcherForTag.find();
		while (result) { 
			StringBuffer sbreplace = new StringBuffer();
			Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag.group(1));
			if (matcherForAttrib.find()) { 
				matcherForAttrib.appendReplacement(sbreplace, startTag + matcherForAttrib.group(1) + endTag);				
			}
			matcherForTag.appendReplacement(sb, sbreplace.toString());
			result = matcherForTag.find();
			
		}
		matcherForTag.appendTail(sb); 
		return sb.toString();
	}
	
	public static String FilterHtmlCode(String s) {
		if (s == null || s.trim().equals("")) {
			s = " ";
			return s;
		}
		try {
			s = s.replaceAll("<[^<>]+>", "");
			s = Replace(s, " ", "");
			s = Replace(s, "&nbsp;", "");
		} catch (Exception e) {
		}
		return s;
	}

	public static String Replace(String source, String oldString,
			String newString) {
		if (source == null)
			return null;
		StringBuffer output = new StringBuffer();
		int lengOfsource = source.length();
		int lengOfold = oldString.length();
		int posStart = 0;
		int pos;
		while ((pos = source.indexOf(oldString, posStart)) >= 0) {
			output.append(source.substring(posStart, pos));
			output.append(newString);
			posStart = pos + lengOfold;
		}
		if (posStart < lengOfsource) {
			output.append(source.substring(posStart));
		}
		return output.toString();
	}
}
