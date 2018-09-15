/*
 * OnlineSessionFactory.java         2016年6月14日 <br/>
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

package cn.lonsun.shiro.session;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.web.session.mgt.WebSessionContext;

import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.rbac.login.InternalAccount;

/**
 * 自定义session工厂 <br/>
 *
 * @date 2016年6月14日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class OnlineSessionFactory implements SessionFactory {
    // pc访问头
    private static String[] pcHeaders = new String[] { "Windows 98", "Windows ME", "Windows 2000", "Windows XP", "Windows NT", "Ubuntu" };

    @Override
    public Session createSession(SessionContext initData) {
        if (null == initData || !(initData instanceof WebSessionContext)) {
            return null;
        }
        WebSessionContext sessionContext = (WebSessionContext) initData;
        HttpServletRequest request = (HttpServletRequest) sessionContext.getServletRequest();
        OnlineSession onlineSession = new OnlineSession();
        onlineSession.setIp(RequestUtil.getIpAddr(request));
        onlineSession.setAttribute(InternalAccount.MDC_IP, onlineSession.getIp());
        onlineSession.setUserAgent(request.getHeader("User-Agent"));
        onlineSession.setDeviceType(this.getDeviceType(onlineSession.getUserAgent()));
        return onlineSession;
    }

    /**
     * 获取设备类型
     *
     * @author fangtinghua
     * @param userAgent
     * @return
     */
    private String getDeviceType(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return OnlineSession.DeviceType.Other.toString();
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Android")) {
            return OnlineSession.DeviceType.Android.toString();
        }
        if (StringUtils.containsIgnoreCase(userAgent, "iPhone")) {
            return OnlineSession.DeviceType.iPhone.toString();
        }
        if (StringUtils.containsIgnoreCase(userAgent, "iPad")) {
            return OnlineSession.DeviceType.iPad.toString();
        }
        for (String header : pcHeaders) {
            if (StringUtils.containsIgnoreCase(userAgent, header)) {
                return OnlineSession.DeviceType.PC.toString();
            }
        }
        return OnlineSession.DeviceType.Other.toString();
    }
}