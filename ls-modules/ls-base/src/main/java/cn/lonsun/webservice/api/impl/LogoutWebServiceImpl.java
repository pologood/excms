package cn.lonsun.webservice.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.lonsun.webservice.api.ILogoutWebService;
import cn.lonsun.webservice.util.SessionContainer;

/**
 * 系统注销接口实现
 *  
 * @author xujh 
 * @date 2014年10月21日 下午4:21:59
 * @version V1.0
 */
@Service("logoutWebService")
public class LogoutWebServiceImpl implements ILogoutWebService{

	@Override
	public boolean logout(String uid,String sessionId) {
		if(!StringUtils.isEmpty(uid)){
			SessionContainer.invalidate(uid,sessionId);
		}
		return true;
	}

}
