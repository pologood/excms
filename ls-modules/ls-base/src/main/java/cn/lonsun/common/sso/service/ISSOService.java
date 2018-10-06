package cn.lonsun.common.sso.service;

import cn.lonsun.common.vo.PersonInfoVO;


/**
 * 单点登录服务接口
 */
public interface ISSOService {


    /**
     * 单点登录code验证
     * @param uid
     * @param token
     * @param organId
     * @return
     */
	public PersonInfoVO validateToken(String uid, String token,Long organId);
}

