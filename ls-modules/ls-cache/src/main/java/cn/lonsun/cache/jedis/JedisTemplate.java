/*
 * JedisTemplate.java         2015年8月21日 <br/>
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

package cn.lonsun.cache.jedis;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * jedis操作模板模式 <br/>
 *
 * @date 2015年8月21日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class JedisTemplate {

    @Resource
    private JedisPool jedisPool;

    public <T> T execute(JedisHandler<T> jedisHandler) {
        T result = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedisHandler.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}