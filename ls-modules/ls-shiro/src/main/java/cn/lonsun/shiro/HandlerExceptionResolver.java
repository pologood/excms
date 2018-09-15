/*
 * HandlerExceptionResolver.java         2016年6月23日 <br/>
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

package cn.lonsun.shiro;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.shiro.util.ShiroExceptionUtil;

/**
 * 统一异常处理 <br/>
 *
 * @date 2016年6月23日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class HandlerExceptionResolver implements org.springframework.web.servlet.HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 处理异常
        ShiroExceptionUtil.processException(request, response, ex);
        return null;
    }
}