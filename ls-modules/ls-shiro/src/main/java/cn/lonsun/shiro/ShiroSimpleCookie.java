/*
 * ShiroSimpleCookie.java         2016年6月25日 <br/>
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

import cn.lonsun.shiro.filter.DelegatingFilterProxy;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 重写cookie name <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月25日 <br/>
 */
public class ShiroSimpleCookie extends SimpleCookie {
    //sessionId
    private static final String PARAM_SESSION_ID = "sessionId";

    /**
     * 创建构造方法，调用父类方法
     * <p>
     * Creates a new instance of ShiroSimpleCookie.
     */
    public ShiroSimpleCookie() {
        super();
    }

    public ShiroSimpleCookie(String name) {
        super(name);
    }

    public ShiroSimpleCookie(Cookie cookie) {
        super(cookie);
    }

    /**
     * 重写读取session cookie值的地方，也可以从参数获取
     *
     * @see org.apache.shiro.web.servlet.SimpleCookie#readValue(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String readValue(HttpServletRequest request, HttpServletResponse ignored) {
        String sessionId = null;// sessionId
        Object isMultipart = request.getAttribute(DelegatingFilterProxy.REQUEST_ATTRIBUTE_ISMULTIPART);
        if (null != isMultipart && (Boolean) isMultipart) {// 是上传请求，从get参数中获取sessionId
            sessionId = request.getParameter(PARAM_SESSION_ID);
        }
        return StringUtils.isEmpty(sessionId) ? super.readValue(request, ignored) : sessionId;
    }
}