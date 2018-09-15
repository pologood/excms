package cn.lonsun.staticcenter.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.MD5Util;
import cn.lonsun.wechatmgr.internal.service.ICoreService;

/**
 * 
 * @ClassName: WeChatAuthApiController
 * @Description: 微信接入端口
 * @author Hewbing
 * @date 2016年4月5日 上午10:38:33
 *
 */
@Controller
@RequestMapping("weChatAuthApi")
public class WeChatAuthApiController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(WeChatAuthApiController.class);
    @Resource
    private ICoreService coreService;
	
    
    private static String Token="";
    
    private static  String echostr; 
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
         if(!accessing(request)){  
        	 logger.info("服务器接入失败.......");  
             return ;  
         }  
        String echostr=getEchostr();  
        if(echostr!=null && !"".equals(echostr)){  
        	logger.info("服务器接入生效..........");  
                try {
					response.getWriter().print(echostr);//完成相互认证  
				} catch (IOException e) {
					e.printStackTrace();
				}
        } 
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
        	WeChatAuthApiController.echostr =echostr;  
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
	 * @Title: apiPost
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
}
