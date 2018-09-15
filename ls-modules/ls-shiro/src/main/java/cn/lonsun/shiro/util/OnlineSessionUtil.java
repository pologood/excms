/*
 * OnlineSessionUtil.java         2016年6月21日 <br/>
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

package cn.lonsun.shiro.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import cn.lonsun.rbac.login.InternalAccount;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.shiro.session.OnlineSession;
import cn.lonsun.shiro.session.ehcache.EhcacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * session管理 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月21日 <br/>
 */
public class OnlineSessionUtil {
    @Value("${user.login.pc.switchMutiLogin:false}")
    private static boolean enable;// 切换多用户登录处理方式  true表示踢掉原用户  false  表示禁止新用户登录
    // sessionDAO
    private static final Logger logger = LoggerFactory.getLogger(OnlineSessionUtil.class);
    private static EhcacheSessionDAO ehcacheSessionDAO = SpringContextHolder.getBean(EhcacheSessionDAO.class);

    /**
     * 根据sessionId获取session
     *
     * @param sessionId
     * @return
     * @author fangtinghua
     */
    public static OnlineSession getSessionById(String sessionId) {
        return (OnlineSession) ehcacheSessionDAO.readSession(sessionId);
    }

    /**
     * 获取当前用户session
     *
     * @return
     * @author fangtinghua
     */
    public static OnlineSession getCurrentSession() {
        String sessionId = SecurityUtils.getSubject().getSession().getId().toString();
        return getSessionById(sessionId);
    }

    /**
     * 获取所有session对象
     *
     * @return
     */
    public static Collection<Session> getAllSessionList() {
        return ehcacheSessionDAO.getActiveSessions();
    }

    /**
     * 判断是否已经登录
     *
     * @return
     */
    public static boolean checkUserLoginTimeAndIsAlreadyLogin() {
        OnlineSession currentSession = OnlineSessionUtil.getCurrentSession();
        Collection<Session> sessionList = OnlineSessionUtil.getAllSessionList();
        if (null != sessionList && !sessionList.isEmpty()) {
            for (Session session : sessionList) {
               // logger.info("-----------------currentSession"+currentSession+"___________session="+session);
                if (!currentSession.equals(session)) {
                    continue;
                }
                logger.info("-----------------currentSession.equals(session)"+session);
                if (LoginMapUtil.checkLoginTimeExpired(session.getId().toString())) {
                    logger.info("-----------------session.setTimeout(0)  执行");
                    session.setTimeout(0);//失效
                } else {
                    return enable;
                }
            }
        }
        return false;
    }

    /**
     * 是否是手机设备
     *
     * @return
     * @author fangtinghua
     */
    public static boolean isPhoneDevice() {
        OnlineSession session = getCurrentSession();
        return isPhoneDevice(session.getDeviceType());
    }

    /**
     * 是否手机设备
     *
     * @param deviceType
     * @return
     * @author fangtinghua
     */
    public static boolean isPhoneDevice(String deviceType) {
        return OnlineSession.DeviceType.Android.toString().equals(deviceType) || OnlineSession.DeviceType.iPhone.toString().equals(deviceType);
    }
}