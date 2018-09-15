/*
 * ShiroWebSessionManager.java         2016年6月21日 <br/>
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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import cn.lonsun.shiro.util.LoginMapUtil;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import cn.lonsun.shiro.session.OnlineSession;

/**
 * shiro session管理 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月21日 <br/>
 */
public class ShiroWebSessionManager extends DefaultWebSessionManager {

    /**
     * 重写，防止每次刷新的时候都更新session，除非在必要的字段发生改变时
     *
     * @see org.apache.shiro.session.mgt.AbstractNativeSessionManager#touch(org.apache.shiro.session.mgt.SessionKey)
     */
    @Override
    public void touch(SessionKey key) throws InvalidSessionException {
        HttpServletRequest request = WebUtils.getHttpRequest(key);
        // dwr请求时，不刷新session，session过期冲突
        // 这里只是临时解决方案，需要写个filter过滤器，设置httpMode为非http请求，配置拦截路径解决问题
        if (!request.getRequestURI().startsWith("/dwr/")) {
            OnlineSession session = (OnlineSession) super.retrieveSession(key);
            session.setLastAccessTime(new Date());
        } else {
            LoginMapUtil.refreshRequestTime(key.getSessionId().toString());
        }
    }
}