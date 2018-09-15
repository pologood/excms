/*
 * LoginMapUtil.java         2016年10月24日 <br/>
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

import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.shiro.login.ShiroTimeoutCallback;
import cn.lonsun.shiro.map.TimeoutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 登录map工具类 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年10月24日 <br/>
 */
public class LoginMapUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoginMapUtil.class);

    private static Boolean isInit = false;
    private static Map<Long, UserEO> userMap = null;// 超时map
    private static Map<String, Date> userRequestMap = new ConcurrentHashMap<String, Date>();//用户session刷新时间

    public static void put(UserEO userEO, int timeOut) {
        if (!isInit) {
            synchronized (isInit) {
                if (!isInit) {
                    isInit = true;
                    userMap = new TimeoutMap<Long, UserEO>(TimeUnit.MINUTES, timeOut, new ShiroTimeoutCallback());
                }
            }
        }
        Long userId = userEO.getUserId();
        if (!userMap.containsKey(userId)) {
            userMap.put(userId, userEO);
        }
    }

    public static void refreshRequestTime(String sessionId) {
        userRequestMap.put(sessionId, new Date());
    }

    /**
     * 检查登录时间是否过期
     *
     * @param sessionId
     * @return
     */
    public static boolean checkLoginTimeExpired(String sessionId) {
        if (!userRequestMap.containsKey(sessionId)) {//不包含也相当于过期，也给登录
            logger.info("----------------------userRequestMap.containsKey(sessionId)-"+userRequestMap.containsKey(sessionId));
            return true;
        }
        long time = System.currentTimeMillis();
        logger.info("time="+time);
        logger.info("userRequestMap.get(sessionId).getTime()="+userRequestMap.get(sessionId).getTime());
        return (time - userRequestMap.get(sessionId).getTime()) / 1000 >= 6;
    }
}