/*
 * ShiroWebService.java         2016年7月18日 <br/>
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

package cn.lonsun.webservice;

import cn.lonsun.webservice.to.WebServiceTO;

/**
 * 单点登录webservice接口 <br/>
 *
 * @date 2016年7月18日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface LoginService {

    /**
     * 获取鉴权信息
     *
     * @author fangtinghua
     * @param sessionId
     * @return
     */
    WebServiceTO getAuthenticationInfo(String sessionId);
}