/*
 * ShiroTimeoutCallback.java         2016年10月24日 <br/>
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

package cn.lonsun.shiro.login;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.shiro.map.TimeoutCallback;

/**
 * 用户登录时限之后恢复重新登录状态 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年10月24日 <br/>
 */
public class ShiroTimeoutCallback implements TimeoutCallback<Long, UserEO> {
    // 用户service
    private IUserService userService = SpringContextHolder.getBean("userService");

    @Override
    public void onTimeout(Long userId, UserEO userEO) {
        userEO = userService.getEntity(UserEO.class, userId);
        userEO.setRetryTimes(0);
        userService.updateEntity(userEO);
    }
}