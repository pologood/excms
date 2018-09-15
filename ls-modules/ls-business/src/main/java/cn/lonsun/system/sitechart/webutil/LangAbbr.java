package cn.lonsun.system.sitechart.webutil;

import java.util.HashMap;
import java.util.Map;

public class LangAbbr {

	public static Map<String,String> langAbbr=new HashMap<String, String>(){
		private static final long serialVersionUID = 1212243354L;
		{
			put("zh-cn", "中文 (中国)");
			put("zh-hk", "中文 (香港)");
			put("zh-tw", "中文 (台湾)");
			put("en","英文");
			put("en-us","英文 (美国)");
			put("ja","日文");
			put("ja-jp","日文 (日本)");
			put("ko","韩文");
		}
	};
}
