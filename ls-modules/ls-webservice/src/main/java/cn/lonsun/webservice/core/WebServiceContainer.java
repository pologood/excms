package cn.lonsun.webservice.core;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.webservice.vo.axis2.WebServiceVO;

import java.util.HashMap;
import java.util.Map;

/**
 * WebService存储容器
 *  
 * @author xujh 
 * @date 2014年11月5日 上午9:51:25
 * @version V1.0
 */
public class WebServiceContainer {
	/**
	 * 防止实例化
	 */
	private WebServiceContainer(){
		throw new Error();
	}
	//存放服务
	private static Map<String,WebServiceVO> activedServices = new HashMap<String,WebServiceVO>();
	//外平台服务存放容器
	private static Map<String,Map<String,WebServiceVO>> externalServices = new HashMap<String, Map<String,WebServiceVO>>();
	
	/**
	 * 外平台服务存放
	 *
	 * @param platfromCode
	 * @param webserviceCode
	 * @param service
	 */
	public static void put(String platfromCode,String webserviceCode,WebServiceVO service){
		Map<String,WebServiceVO> map = externalServices.get(platfromCode);
		if(map==null){
			map = new HashMap<String, WebServiceVO>();
		}
		map.put(webserviceCode, service);
	}

	public static void clear(){
		activedServices.clear();
	}
	
	/**
	 * 外平台服务获取
	 *
	 * @param platfromCode
	 * @param webserviceCode
	 * @return
	 */
	public static WebServiceVO get(String platfromCode,String webserviceCode){
		WebServiceVO service = null;
		Map<String,WebServiceVO> map = externalServices.get(platfromCode);
		if(map!=null){
			service = map.get(webserviceCode);
		}
		return service;
	}
	
	public static void put(String code,WebServiceVO service){
		activedServices.put(code, service);
	}
	
	public static WebServiceVO get(String code){
		if(StringUtils.isEmpty(code)){
			throw new NullPointerException();
		}
		return activedServices.get(code);
	}

}
