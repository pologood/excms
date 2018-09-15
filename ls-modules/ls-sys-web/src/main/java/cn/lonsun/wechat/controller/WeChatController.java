package cn.lonsun.wechat.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.MD5Util;
import cn.lonsun.wechatmgr.internal.entity.WeChatAccountsInfoEO;
import cn.lonsun.wechatmgr.internal.service.ICoreService;
import cn.lonsun.wechatmgr.internal.service.IWeChatAccountsInfoService;
import cn.lonsun.wechatmgr.internal.service.IWeChatMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

/**
 *
 * @ClassName: WeChatController
 * @Description: 与微信服务器连接交互
 * @author Hewbing
 * @date 2016年4月7日 下午5:11:42
 *
 */
@Controller
@RequestMapping(value = "weChat")
public class WeChatController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(WeChatController.class);



	@Resource
	private ICoreService coreService;

	@Autowired
	private IWeChatAccountsInfoService weChatAccountsInfoService;

	@Autowired
	private IWeChatMenuService weChatMenuService;

	private static String Token="";

	private static  String echostr;

	private final String token = "weixin_lonsun";

	/**
	 *
	 * @Title: apiGet
	 * @Description:微信服务端口接入验证
	 * @param request
	 * @param response   Parameter
	 * @return  void   return type
	 * @throws
	 */
	@RequestMapping(value="api",method=RequestMethod.GET)
	public void apiGet(HttpServletRequest request,HttpServletResponse response){
		logger.info("RemoteAddr: "+ request.getRemoteAddr());
		logger.info("QueryString: "+ request.getQueryString());
//         if(!accessing(request)){
//        	 logger.info("服务器接入失败.......");
//             return ;
//         }
//        String echostr=getEchostr();
//        if(echostr!=null && !"".equals(echostr)){
//        	logger.info("服务器接入生效..........");
//                try {
//					response.getWriter().print(echostr);//完成相互认证
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//        }
		System.out.println("开始签名校验");
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		logger.info("signature: "+signature);
		logger.info("timestamp: "+ timestamp);
		logger.info("nonce: "+ nonce);
		logger.info("echostr: "+ echostr);
		ArrayList<String> array = new ArrayList<String>();
		array.add(signature);
		array.add(timestamp);
		array.add(nonce);
		//排序
		String sortString = sort(token, timestamp, nonce);
		//加密
		String mytoken = SHA1(sortString);
		logger.info("mytoken: "+ mytoken);
		//校验签名
		if (mytoken != null && mytoken != "" && mytoken.equals(signature)) {
			System.out.println("签名校验通过。");
			//如果检验成功输出echostr，微信服务器接收到此输出，才会确认检验完成。
			try {
				response.getWriter().print(echostr);//完成相互认证
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("签名校验失败。");
		}


	}


	/**
	 * 加密方法
	 * @param decript
	 * @return
	 */
	public static String SHA1(String decript) {
		try {
			MessageDigest digest = MessageDigest
					.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 排序方法
	 * @param token
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static String sort(String token, String timestamp, String nonce) {
		String[] strArray = { token, timestamp, nonce };
		Arrays.sort(strArray);

		StringBuilder sbuilder = new StringBuilder();
		for (String str : strArray) {
			sbuilder.append(str);
		}

		return sbuilder.toString();
	}


	private boolean accessing(HttpServletRequest request){
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		if(AppUtil.isEmpty(signature)){
			return false;
		}
		if(AppUtil.isEmpty(timestamp)){
			return false;
		}
		if(AppUtil.isEmpty(nonce)){
			return false;
		}
		if(AppUtil.isEmpty(echostr)){
			return false;
		}
		StringBuffer url=request.getRequestURL();
		logger.info("REQUEST_URL >>>>>>>>>> "+url);
		try {
			CRC32 crc=new CRC32();
			String _token=(MD5Util.getMd5ByByte(MD5Util.getMd5ByByte(url.toString().getBytes("UTF-8")).getBytes("UTF-8")));
			crc.update(_token.getBytes("UTF-8"));
			Token=String.valueOf(crc.getValue());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] ArrTmp = { Token, timestamp, nonce };
		Arrays.sort(ArrTmp);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ArrTmp.length; i++) {
			sb.append(ArrTmp[i]);
		}
		String pwd = Encrypt(sb.toString());

		logger.info("signature:"+signature+"timestamp:"+timestamp+"nonce:"+nonce+"pwd:"+pwd+"echostr:"+echostr);

		if(trim(pwd).equals(trim(signature))){
			WeChatController.echostr =echostr;
			return true;
		}else{
			return false;
		}
	}


	private static String Encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;

		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			strDes = bytes2Hex(md.digest()); //to HexString
		} catch (NoSuchAlgorithmException e) {
			logger.error("Invalid algorithm.");
			return null;
		}
		return strDes;
	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	private static String trim(String str){
		return null !=str  ? str.trim() : str;
	}

	public static String getEchostr(){
		return echostr;
	}


	/**
	 *
	 * @Title: apiPost 威信消息推送方法
	 * @Description: POST METHOD business processes 
	 * @param request
	 * @param response   Parameter
	 * @return  void   return type
	 * @throws
	 */
	@RequestMapping(value="api",method=RequestMethod.POST)
	public void apiPost(HttpServletRequest request,HttpServletResponse response){

		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setContentType("text/xml;charset=UTF-8");
		//response.setCharacterEncoding("UTF-8");

		// 调用核心业务类接收消息、处理消息
		String respMessage = coreService.processRequest(request);

		// 响应消息
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(respMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
			out = null;
		}
	}

	@RequestMapping("config")
	public String config(){
		return "/wechat/config";
	}

	/**
	 *
	 * @Title: getConfig
	 * @Description: 获取该站点下微信配置
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getConfig")
	@ResponseBody
	public Object getConfig(){
		Long siteId=LoginPersonUtil.getSiteId();
		WeChatAccountsInfoEO info = weChatAccountsInfoService.getInfoBySite(siteId);
		if(info==null){
			info=new WeChatAccountsInfoEO();
		}
		return getObject(info);
	}

	/**
	 *
	 * @Title: saveConfig
	 * @Description: 保存微信配置
	 * @param config
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("saveConfig")
	@ResponseBody
	public Object saveConfig(WeChatAccountsInfoEO config){
		weChatAccountsInfoService.saveConfig(config);
		return getObject();
	}

	/**
	 *
	 * @Title: showQrCode
	 * @Description: 显示二维码
	 * @param url
	 * @param map
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("showQrCode")
	public String showQrCode(String url,ModelMap map){
		map.put("QRCODE", url);
		return "/wechat/show_qrcode";
	}

	@RequestMapping(value="index")
	public String index(){
		return "/wechat/wechat";
	}

	/**
	 *
	 * @Title: buildToken
	 * @Description: 生成TOKEN值
	 * @param apiUrl
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("buildToken")
	@ResponseBody
	public Object buildToken(String apiUrl){
		if(AppUtil.isEmpty(apiUrl)){
			return ajaxErr("API接口未配置，无法分配TOKEN");
		}
//		String token=token;
//		try {
//			CRC32 crc=new CRC32();
//			String _token=(MD5Util.getMd5ByByte(MD5Util.getMd5ByByte(apiUrl.getBytes("UTF-8")).getBytes("UTF-8")));
//			crc.update(_token.getBytes("UTF-8"));
//			token=String.valueOf(crc.getValue());
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return getObject(token);
	}

}
