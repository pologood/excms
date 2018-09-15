/*
 * ISSOService.java         2014年10月17日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.common.sso.service;

import cn.lonsun.common.vo.PersonInfoVO;


/**
 * 单点登录服务接口
 *
 * @author xujh
 * @version 1.0
 * 2015年1月28日
 *
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

