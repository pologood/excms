package cn.lonsun.system.sitechart.webutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlHostUtil {

	public static String getHost(String url){
		  if(url==null||url.trim().equals("")){
		   return "";
		  }
		  String host = "";
		  Pattern p =  Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
		  Matcher matcher = p.matcher(url); 
		  if(matcher.find()){
		   host = matcher.group(); 
		  }
		  return host;
	}
	public static void main(String[] args) {
		System.out.println(getHost("http://cloudgov.lonsun.cn/SortHtml/422/15474984423.html"));
	}
}
