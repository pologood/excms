/*
 * ShiroSessionListener.java         2016年6月15日 <br/>
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

import javax.annotation.Resource;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.shiro.session.ehcache.EhcacheSessionDAO;

/**
 * session监听 <br/>
 *
 * @date 2016年6月15日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ShiroSessionListener implements SessionListener {

    private static final Logger logger = LoggerFactory.getLogger(ShiroSessionListener.class);

    @Resource
    private EhcacheSessionDAO ehcacheSessionDAO;

    @Override
    public void onStart(Session session) {
        logger.info("ShiroSessionListener session [{}] 创建", session.getId());
    }

    @Override
    public void onStop(Session session) {
        ehcacheSessionDAO.delete(session);
        logger.info("ShiroSessionListener session [{}] 销毁", session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        ehcacheSessionDAO.delete(session);
        logger.info("ShiroSessionListener session [{}] 过期", session.getId());
    }
}