/*
 * RedisSessionDAO.java         2016年6月8日 <br/>
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

package cn.lonsun.shiro.session.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import cn.lonsun.shiro.session.OnlineSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * redis session管理 <br/>
 *
 * @date 2016年6月8日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class RedisSessionDAO extends AbstractSessionDAO {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    // 失效时间
    private int expire;
    @Resource
    private ShiroRedisCache shiroRedisCache;// redis缓存操作

    /**
     * The Redis key for the sessions
     */
    private String key = "shiro_redis_session:";

    /**
     * 类初始化完成后清空缓存中活动的Session
     */
    @PostConstruct
    public void clearActiveSessions(){
        logger.info("清空缓存中活动的Session");
        try{
            List<OnlineSession> sessionList = shiroRedisCache.getValues(OnlineSession.class, key + "*");
            if (null != sessionList && !sessionList.isEmpty()) {
                logger.info("缓存中活动的Session个数："+sessionList.size() + "个");
                for(OnlineSession session : sessionList){
                    delete(session);
                }
                logger.info("清空完成");
            }else{
                logger.info("当前缓存中没有活动的Session");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Session session) {
        shiroRedisCache.delete(this.getSessionKey(session.getId()));
    }

    @Override
    public Collection<Session> getActiveSessions() {
        List<OnlineSession> sessionList = shiroRedisCache.getValues(OnlineSession.class, key + "*");
        Collection<Session> collectionList = new ArrayList<Session>();
        if (null != sessionList && !sessionList.isEmpty()) {
            collectionList.addAll(sessionList);
        }
        return collectionList;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        String k = this.getSessionKey(session.getId());
        shiroRedisCache.saveOrUpdate(k, (OnlineSession) session);
        shiroRedisCache.expire(k, this.getExpire());
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        String k = this.getSessionKey(sessionId);
        shiroRedisCache.saveOrUpdate(k, (OnlineSession) session);
        shiroRedisCache.expire(k, this.getExpire());
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        String k = this.getSessionKey(sessionId);
        shiroRedisCache.expire(k, this.getExpire());
        return shiroRedisCache.getValue(OnlineSession.class, k);
    }

    /**
     * 获取redis键
     *
     * @author fangtinghua
     * @param sessionId
     * @return
     */
    private String getSessionKey(Serializable sessionId) {
        return key + sessionId.toString();
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}