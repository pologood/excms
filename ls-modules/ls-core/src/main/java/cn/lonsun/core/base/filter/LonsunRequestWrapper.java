package cn.lonsun.core.base.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;


/**
 * 对request对象进行复写，进行请求参数的特殊字符串转义操作
 * 
 * @author 徐建华
 * 
 */
public class LonsunRequestWrapper extends HttpServletRequestWrapper {

	public LonsunRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String[] getParameterValues(String name) {
		// Springmvc框架获取入参通过调用此方法
		String[] v = super.getParameterValues(name);
		if (v == null||v.length<=0) {
			return null;
		} else{
			String[] array = (String[]) v;
			int length = array.length;
			String[] vs = new String[length];
			for (int i = 0; i < length; i++) {
				// html特殊字符转义
				vs[i] = htmlEscape(array[i]);
			}
			return vs;
		}
	}

	@Override
	public String getParameter(String name) {
		// 用户一般获取入参通过调用此方法
		String v = super.getParameter(name);
		if (StringUtils.isEmpty(v)) {
			return null;
		} else{
			// html特殊字符转义
			return htmlEscape((String) v);
		}
	}
	
	private String htmlEscape(String value){
		if(value==null){
			return null;
		}
		/*value = value.replace("<", "&lt;");
		return value.replace(">", "&gt;");*/
		//过滤script，iframe，src等,//XSS攻击简单过滤
		value = value.replaceAll("[sS]{1}[cC]{1}[rR]{1}[iI]{1}[pP]{1}[tT]{1}", "");
		value = value.replaceAll("[iI]{1}[fF]{1}[rR]{1}[aA]{1}[mM]{1}[eE]{1}", "");
		value = value.replaceAll("[sS]{1}[rR]{1}[cC]{1}", "");
		return value;
	}
	
	public static void main(String[] args) {
		String value = "<script>alert();</script>";
		value = value.replaceAll("[sS]{1}[cC]{1}[rR]{1}[iI]{1}[pP]{1}[tT]{1}", "");
		value = value.replaceAll("[iI]{1}[fF]{1}[rR]{1}[aA]{1}[mM]{1}[eE]{1}", "");
		value = value.replaceAll("[sS]{1}[rR]{1}[cC]{1}", "");
		System.out.println(value);
	}
	
}
