/*
 * AnonymousFilter.java         2016年6月24日 <br/>
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

package cn.lonsun.shiro.filter;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 重写anon拦截器，在响应头中加入session超时判断 <br/>
 *
 * @date 2016年6月24日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class AnonymousFilter extends org.apache.shiro.web.filter.authc.AnonymousFilter {
    @Resource
    private FormAuthenticationFilter formAuthenticationFilter;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            formAuthenticationFilter.setTimeout(response);// 在请求头中存储session超时判断
        }
        return super.onPreHandle(request, response, mappedValue);
    }
}