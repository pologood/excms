package cn.lonsun.charts;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;

public class CnzzChartsUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(CnzzChartsUtil.class);

	private static Map<String,CnzzLoginCookie> cookieCache=new HashMap<String, CnzzLoginCookie>();
	
	public static String LOGIN_PATH="http://new.cnzz.com/v1/login.php?t=login&siteid=#SITEID#";
	
	public static String VISIT_SOURCE="http://tongji.cnzz.com/main.php?c=traf&a=overview&ajax=module=flash&siteid=#SITEID#&st=#ST#&et=#ET#&type=Pie&Period=Hour&Quota=pv&_=#now#";

	public static String SEARCH_ENGINES="http://tongji.cnzz.com/main.php?c=traf&a=search&ajax=module=flash&subTabIndex=0&engin=all&siteid=#SITEID#&st=#ST#&et=#ET#&type=Pie&Period=Hour&Quota=pv&search=101,4,104&_=1#now#";
	
	public static String FLOW_ANALYSIS="https://tongji.cnzz.com/main.php?c=flow&a=trend&ajax=module%3Dsummary|module%3DfluxList_currentPage%3D1_pageType%3D30&siteid=#SITEID#&st=#ST#&et=#ET#&_=#now#";
	
	public static String VISIT_AREA="http://tongji.cnzz.com/main.php?siteid=#SITEID#&c=visitor&a=districtnet&ajax=module=flash&moduleType=location&st=#ST#&et=#ET#&type=Pie&Quota=pv&_=#now#";
	
	public static String VISIT_LOCATION="http://tongji.cnzz.com/main.php?c=visitor&a=districtnet&ajax=module%3Dsummary|module%3DprovinceList_orderBy%3Dpv_orderType%3D-1_currentPage%3D1_pageType%3D30&siteid=#SITEID#&st=#ST#&et=#ET#&_=#now#";
	
	private static String loginCookie(Long siteId){
		SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
		String cnzzSite=siteEO.getStationId();
		String password=siteEO.getStationPwd();
		CnzzLoginCookie clc=cookieCache.get(cnzzSite);
		String loginCookie=null;
		Long nowTime=new Date().getTime();
		if(null!=clc&&clc.getSetTime()+10*60*1000>nowTime){
			loginCookie= clc.getCookie();
			logger.debug("GET CNZZ LOGIN COOKIE BY CACHE >>> "+loginCookie);
			System.out.println("缓存里获取Cookie:"+loginCookie);
		}else{
			loginCookie=formPost(cnzzSite, password);
			CnzzLoginCookie newCookie=new CnzzLoginCookie();
			newCookie.setSiteId(cnzzSite);
			newCookie.setCookie(loginCookie);
			newCookie.setSetTime(nowTime);
			cookieCache.put(cnzzSite, newCookie);
			logger.debug("GET CNZZ LOGIN COOKIE BY LOGIN CNZZ >>> "+loginCookie);
			System.out.println("登入后获取Cookie:"+loginCookie);
		}
		return loginCookie;
	}
	
	
	
	public static String formPost(String siteId, String password) {
		try {
			URL url = new URL(LOGIN_PATH.replace("#SITEID#", siteId));
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream(), "utf-8");
			out.write("password=" + password); // 向页面传递数据。post的关键所在！
			out.flush();
			out.close();
			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String sCurrentLine;
			String sTotalString;
			sCurrentLine = "";
			sTotalString = "";
			InputStream l_urlStream;
			l_urlStream = connection.getInputStream();
			String setCookie = connection.getHeaderField(7);
			logger.debug("Login CNZZ successed COOKIE >>>>> " + setCookie);
			// 传说中的三层包装阿！
			BufferedReader l_reader = new BufferedReader(new InputStreamReader(
					l_urlStream));
			while ((sCurrentLine = l_reader.readLine()) != null) {
				sTotalString += sCurrentLine;
			}
			logger.debug(sTotalString);
			return setCookie;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getVisitSource(String cookie, String siteId, String startTime,String endTime) {
		startTime=(new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		endTime=startTime;
		URL url;
		String line = "";
		String rel = "";
		try {
			url = new URL(VISIT_SOURCE.replace("#SITEID#",siteId).replace("#ST#", startTime).replace("#ET#", endTime));
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("Cookie", cookie);// 给服务器送登录后的cookie
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			line = br.readLine();
			while (line != null) {
				rel += new String(line.getBytes());
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rel;
	}
	
	public static String getChartsData(String path, Long siteId, String startTime,String endTime){
		String cookie=loginCookie(siteId);
		SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
		startTime=(new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		endTime=startTime;
		URL url;
		String line = "";
		String rel = "";
		try {
			url = new URL(path.replace("#SITEID#", siteEO.getStationId()).replace("#ST#", startTime).replace("#ET#", endTime).replace("#now#",String.valueOf(new Date().getTime())));
			System.out.println(url.toString());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("Cookie", cookie);// 给服务器送登录后的cookie
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			line = br.readLine();
			while (line != null) {
				rel += new String(line.getBytes());
				line = br.readLine();
			}
			if(rel.contains("error")){
				cookieCache.remove(siteEO.getStationId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rel;
	}
	
	public static void main(String[] args) {
		String rel=getChartsData(VISIT_LOCATION, 1257378886L, null, null);
		JSONObject json = JSONObject.fromObject(rel);
		Set<String> keyset = json.getJSONObject("data").getJSONObject("provinceList").getJSONObject("items").keySet();
		Collection<JSONObject> values = json.getJSONObject("data").getJSONObject("provinceList").getJSONObject("items").values();
		List<String> prv=new ArrayList<String>();
		List<Integer> num=new ArrayList<Integer>();
		String[] arr=keyset.toArray(new String[keyset.size()]);
		JSONObject[] val = values.toArray(new JSONObject[values.size()]);
		if(arr.length>=10){
			for(int i=0;i<10;i++){
				prv.add(arr[i]);
				System.out.println(arr[i]);
			}
		}else{
			for(int i=0;i<arr.length;i++){
				prv.add(arr[i]);
				System.out.println(arr[i]);
			}
			for(int i=arr.length;i<=10;i++){
				prv.add("");
			}
		}
		
		if(val.length>=10){
			for(int i=0;i<10;i++){
				num.add(val[i].getInt("pv"));
				System.out.println(val[i].get("pv"));
			}
		}else{
			for(int i=0;i<val.length;i++){
				num.add(val[i].getInt("pv"));
			}
		}

		System.out.println(prv);
		System.out.println(num);
	}
}
