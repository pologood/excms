package cn.lonsun.sms.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class SendSmsUtil {

	public static String httpGet(String url) throws ClientProtocolException, IOException{
		//get请求返回结果
		String result = "";
		DefaultHttpClient client = new DefaultHttpClient();
		//发送get请求
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity());
		} else {
			result = "error";
		}
		return result;
	}

	//	用户名          smsid    
	//	密码               smspwd
	//	接收号码  	to
	//	内容               content
	//	短信签名,格式【必须中文】  	smsTail
	//	当前域名        domain
	//	当前IP    serverIP
	//	客户端Ip       clientIP
	//	发送用户的userID  	sendUserID
	//	发送者姓名       sendUserName
	//	发送者号码       sendUserPhone
	//	接收者的姓名     acceptName
	//	附加文字            addSMSTxt
	public static String getUrl(String url,String smsid,String smspwd,String to,
			String content,String smsTail,String domain,String serverIP
			,String clientIP,Long sendUserID,String sendUserName,
			String sendUserPhone,String acceptName,String addSMSTxt){
		StringBuffer urlParams = new StringBuffer(url);
		try{
			urlParams.append("?").
			append("id").append("=").append(smsid).append("&").
			append("pwd").append("=").append(smspwd).append("&").
			append("to").append("=").append(to).append("&").
			append("content").append("=").append(StringUtils.isEmpty(content)?"":java.net.URLEncoder.encode(content,"gbk")).append("&").
			append("SMS_Tail").append("=").append(StringUtils.isEmpty(smsTail)?"":java.net.URLEncoder.encode(smsTail,"gbk")).append("&").
			append("domain").append("=").append(domain).append("&").
			append("serverIP").append("=").append(serverIP).append("&").
			append("ClientIP").append("=").append(clientIP).append("&").
			append("SendUserID").append("=").append(sendUserID).append("&").
			append("SendUserName").append("=").append(StringUtils.isEmpty(sendUserName)?"":java.net.URLEncoder.encode(sendUserName,"gbk")).append("&").
			append("SendUserPhone").append("=").append(sendUserPhone).append("&").
			append("acceptName").append("=").append(StringUtils.isEmpty(acceptName)?"":java.net.URLEncoder.encode(acceptName,"gbk")).append("&").
			append("addSMSTxt").append("=").append(StringUtils.isEmpty(addSMSTxt)?"":java.net.URLEncoder.encode(addSMSTxt,"gbk"));
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return urlParams.toString();
	}

	//	public static JSONObject httpPost(String url,JSONObject jsonParam){
	//		//post请求返回结果
	//		DefaultHttpClient httpClient = new DefaultHttpClient();
	//		JSONObject jsonResult = null;
	//		HttpPost method = new HttpPost(url);
	//		try {
	//			if (null != jsonParam) {
	//				//解决中文乱码问题
	//				System.out.println("参数："+jsonParam.toString());
	//				StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
	//				entity.setContentEncoding("UTF-8");
	//				entity.setContentType("application/json");
	//				method.setEntity(entity);
	//			}
	//			HttpResponse result = httpClient.execute(method);
	//			if (result.getStatusLine().getStatusCode() == 200) {
	//				String str = "";
	//				try {
	//					str = EntityUtils.toString(result.getEntity());
	//					jsonResult = JSONObject.fromObject(str);
	//					System.out.println(jsonResult);
	//				} catch (Exception e) {
	//				}
	//			}
	//		} catch (IOException e) {
	//		}
	//		return jsonResult;
	//	}

}
