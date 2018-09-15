package cn.lonsun.webservice.api;

/**
 * 用户退出服务接口，所有单独部署的应用都需要实现一个继承该借口的webService
 *  
 * @author xujh 
 * @date 2014年10月8日 下午5:02:47
 * @version V1.0
 */
public interface ILogoutWebService {
	
	/**
	 * 用户退出服务，所有单独部署的应用都需要实现该方法<br>
	 * 如果用户登录，那么注销session，如果未登录，那么忽略请求
	 * @param uid
	 * @param sessionId
	 */
	public boolean logout(String uid,String sessionId);

}
