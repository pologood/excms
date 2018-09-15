/*
 * CMSScriptSessionListener.java         2015年11月28日 <br/>
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

package cn.lonsun.dwr;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;

/**
 * cms dwr scriptsession监听 <br/>
 *
 * @date 2015年11月28日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class CMSScriptSessionListener implements ScriptSessionListener {

    // 维护一个Map key为session的Id， value为ScriptSession对象
    private static final Map<String, ScriptSession> scriptSessionMap = new HashMap<String, ScriptSession>();

    @Override
    public void sessionCreated(ScriptSessionEvent event) {
        WebContext webContext = WebContextFactory.get();
        HttpSession session = webContext.getSession();
        ScriptSession scriptSession = event.getSession();
        scriptSessionMap.put(session.getId(), scriptSession); // 添加scriptSession
    }

    @Override
    public void sessionDestroyed(ScriptSessionEvent arg0) {
        WebContext webContext = WebContextFactory.get();
        HttpSession session = webContext.getSession();
        scriptSessionMap.remove(session.getId()); // 移除scriptSession
    }

    /**
     * 根据httpSessionId获取scriptSession
     *
     * @author fangtinghua
     * @param httpSessionId
     * @return
     */
    public static ScriptSession getScriptSessionByHttpSessionId(String httpSessionId) {
        return scriptSessionMap.get(httpSessionId);
    }
}