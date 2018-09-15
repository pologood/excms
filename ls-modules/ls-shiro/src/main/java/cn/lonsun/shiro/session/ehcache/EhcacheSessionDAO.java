/*
 * EhcacheSessionDAO.java         2016年6月15日 <br/>
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

package cn.lonsun.shiro.session.ehcache;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;

import cn.lonsun.shiro.session.OnlineSession;
import cn.lonsun.shiro.session.redis.RedisSessionDAO;

/**
 * TODO <br/>
 *
 * @date 2016年6月15日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class EhcacheSessionDAO extends CachingSessionDAO {

    @Resource
    private RedisSessionDAO redisSessionDAO;

    @Override
    protected void doUpdate(Session session) {
        // 如果会话过期/停止 没必要再更新
        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            return;
        }
        // 判断字段更新
        OnlineSession os = (OnlineSession) session;
        if (os.isChanged()) {// 如果字段改变
            redisSessionDAO.update(session);
        }
    }

    @Override
    protected void doDelete(Session session) {
        redisSessionDAO.delete(session);
    }

    @Override
    protected Serializable doCreate(Session session) {
        return redisSessionDAO.create(session);
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = getCachedSession(sessionId);
        if (null == session || null == session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)) {
            session = redisSessionDAO.readSession(sessionId);
            if (null != session) {
                super.cache(session, sessionId);
            }
        }
        return session;
    }
}