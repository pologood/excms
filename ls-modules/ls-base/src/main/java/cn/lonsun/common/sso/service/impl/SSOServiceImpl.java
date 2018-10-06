package cn.lonsun.common.sso.service.impl;

import org.springframework.stereotype.Service;

import cn.lonsun.common.sso.service.ISSOService;
import cn.lonsun.common.vo.PersonInfoVO;


/**
 * 单点登录服务接口实现
 */
@Service("ssoService")
public class SSOServiceImpl implements ISSOService{
	


    /**
     * 单点登录code验证
     * @param uid
     * @param token
     * @return
     * @throws Exception
     */
	public PersonInfoVO validateToken(String uid, String token,Long organId){
//		return indirectSsoService.loginValidat( uid, token,organId);
		return null;
	}
}

