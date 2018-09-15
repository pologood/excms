package cn.lonsun.system.sitechart.webutil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchEnginesUtil {
	public static void main(String[] arg) {
		//String referer = "http://www.baidu.com/s?wd=%E5%85%89%E5%89%91&rsv_spt=1&rsv_iqid=0xba8395e600104a8e&issp=1&f=8&rsv_bp=0&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1";
		//String referer="http://www.so.com/s?ie=utf-8&shb=1&src=360sou_newhome&q=lonsun";
		//String referer="http://www.sogou.com/web?query=lonsun&_asf=www.sogou.com&_ast=&ie=utf8&pid=s.idx&cid=s.idx.se&rfrom=soso&unc=&w=01019900&sut=4057&sst0=1456124366444&lkt=6%2C1456124362387%2C1456124364975";
		//String referer="http://cn.bing.com/search?q=lonsun&qs=n&form=QBLH&pq=lonsun&sc=2-6&sp=-1&sk=&cvid=77100AC72C354232A723E95FCAB9A42C";
		//String referer="http://search.aol.com/aol/search?enabled_terms=&s_it=comsearch&q=lonsun";
		String referer="http://www.google.com.hk/link?url=wPnVSKXP1DGnCKjnEHUBFr4qqTMRNRe9GV6yTY6WM4u&wd=&eqid=be064b48000c115c0";
		//String referer="http://www.youdao.com/search?q=lonsun&ue=utf8&keyfrom=web.index";
		if (arg.length != 0) {
			referer = arg[0];
		}
		String searchEngine = getSearchEngine(referer);
		System.out.println("searchEngine:" + searchEngine);
		System.out.println("keyword:" + getKeyword(referer));
	}

	public static Map<String,String> initSE=new HashMap<String, String>(){
		private static final long serialVersionUID = 1839991514228133669L;
		{
		put("www.goole.com", "谷歌搜索");
		put("www.baidu.com", "百度搜索");
		put("www.sogou.com", "搜狗搜索(sogou)");
		put("www.yahoo.com", "雅虎搜索");
		put("www.163.com", "网易搜索");
		put("cn.bing.com", "必应搜索");
		put("www.soso.com", "搜狗搜索(soso)");
		put("www.lycos.com", "Lycos搜索");
		put("www.aol.com", "美国在线");
		put("www.zhongsou.com", "中搜搜索");
		put("www.alexa.com", "Alexa搜索");
		put("www.so.com", "360搜索");
		put("www.search.com", "Search搜索");
		put("www.youdao.com", "有道搜索");
		}
	};
	
	
	public static String getKeyword(String refererUrl) {
		StringBuffer sb = new StringBuffer();
		if (refererUrl != null) {
			sb.append("(google\\.[a-zA-Z]+/.+[\\&|\\?]q=([^\\&]*)")
					.append("|sogou\\.[a-zA-Z]+/.+[\\&|\\?]query=([^\\&]*)")
					.append("|163\\.[a-zA-Z]+/.+[\\&|\\?]q=([^\\&]*)")
					.append("|yahoo\\.[a-zA-Z]+/.+[\\&|\\?]p=([^\\&]*)")
					.append("|baidu\\.[a-zA-Z]+/.+[\\&|\\?]wd=([^\\&]*)")
					.append("|baidu\\.[a-zA-Z]+/.+[\\&|\\?]word=([^\\&]*)")
					.append("|lycos\\.[a-zA-Z]+/.*[\\&|\\?]query=([^\\&]*)")
					.append("|aol\\.[a-zA-Z]+/.+[\\&|\\?]q=([^\\&]*)")
					.append("|search\\.[a-zA-Z]+/.+[\\&|\\?]q=([^\\&]*)")
					.append("|soso\\.[a-zA-Z]+/.+[\\&|\\?]w=([^\\&]*)")
					.append("|zhongsou\\.[a-zA-Z]+/.+[\\&|\\?]w=([^\\&]*)")
					.append("|alexa\\.[a-zA-Z]+/.+[\\&|\\?]q=([^\\&]*)")
					.append("|so\\.[a-zA-Z]+/.+[\\&|\\?]q=([^\\&]*)")
					.append("|youdao\\.[a-zA-Z]+/.+[\\&|\\?]q=([^\\&]*)")
					.append("|cn\\.bing\\.[a-zA-Z]+/.+[\\&|\\?]q=([^\\&]*)")
					.append(")");
			Pattern p = Pattern.compile(sb.toString());
			Matcher m = p.matcher(refererUrl);
			return decoderKeyword(m, refererUrl);
		}
		return null;
	}

	public static String decoderKeyword(Matcher m, String referer) {
		String refererUrl=null;
		try {
			refererUrl=new String(referer.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String keyword = null;
		String encode = "UTF-8";
		String searchEngine = getSearchEngine(refererUrl);
		if (searchEngine != null) {
//			if ((checkCode("sogou|163|baidu|soso|zhongsou|so|cn.bing|aol",
//					searchEngine) || (checkCode("yahoo", searchEngine) && !checkCode(
//					"ei=utf-8", refererUrl.toLowerCase())))) {
//				encode = "GBK";
//			}

			if (m.find()) {
				for (int i = 2; i <= m.groupCount(); i++) {
					if (m.group(i) != null)// 在这里对关键字分组就用到了
					{
						try {
							keyword = URLDecoder.decode(m.group(i), encode);
						} catch (UnsupportedEncodingException e) {
							System.out.println(e.getMessage());
						}
						break;
					}
				}
			}
		}
		return keyword;
	}

	public static String getSearchEngine(String refUrl) {
		if (refUrl.length() > 11) {
			// p是匹配各种搜索引擎的正则表达式
			Pattern p = Pattern
					.compile("(www\\.google\\.com(:\\d{1,}){0,1}\\/|"
							+ "www\\.google\\.cn(:\\d{1,}){0,1}\\/|www\\.baidu\\.com(:\\d{1,}){0,1}\\/|"
							+ "www\\.yahoo\\.com(:\\d{1,}){0,1}\\/|www\\.iask\\.com(:\\d{1,}){0,1}\\/|"
							+ "www\\.sogou\\.com(:\\d{1,}){0,1}\\/|www\\.163\\.com(:\\d{1,}){0,1}\\/|"
							+ "www\\.lycos\\.com(:\\d{1,}){0,1}\\/|www\\.aol\\.com(:\\d{1,}){0,1}\\/|"
							+ "www\\.search\\.com(:\\d{1,}){0,1}\\/|"
							+ "www\\.soso.com(:\\d{1,}){0,1}\\/|www\\.zhongsou\\.com(:\\d{1,}){0,1}\\/|"
							+ "www\\.alexa\\.com(:\\d{1,}){0,1}\\/|www\\.so\\.com(:\\d{1,}){0,1}\\/"
							+ "|cn\\.bing\\.com(:\\d{1,}){0,1}\\/|www\\.youdao\\.com(:\\d{1,}){0,1}\\/)");
			Matcher m = p.matcher(refUrl);
			if (m.find()) {
				return insteadCode(
						m.group(1),
						"((:\\d{1,}){0,1}\\/|\\.cn(:\\d{1,}){0,1}\\/|\\.org(:\\d{1,}){0,1}\\/)",
						"");
			}
		}
		return null;
	}

	public static String insteadCode(String str, String regEx, String code) {
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		String s = m.replaceAll(code);
		return s;
	}

	public static boolean checkCode(String regEx, String str) {
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.find();
	}

}