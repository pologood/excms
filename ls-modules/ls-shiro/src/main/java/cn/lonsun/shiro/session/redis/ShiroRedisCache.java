/*
 * ShrioJedisCache.java         2016年6月14日 <br/>
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
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import cn.lonsun.cache.jedis.JedisHandler;
import cn.lonsun.cache.jedis.JedisTemplate;

/**
 * 序列化存储reids <br/>
 *
 * @date 2016年6月14日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class ShiroRedisCache {

    @Resource
    private JedisTemplate template;

    /**
     * 设置失效时间
     *
     * @author fangtinghua
     * @param key
     * @param seconds
     */
    public Long expire(final String key, final int seconds) {
        // 操作
        return template.execute(new JedisHandler<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.expire(key.getBytes(), seconds);
            }
        });
    }

    /**
     * 获取数据，基于key方式获取
     *
     * @author fangtinghua
     * @param key
     * @return
     */
    public <T> List<T> getValues(Class<T> clazz, final String key) {
        // 操作
        return template.execute(new JedisHandler<List<T>>() {
            @Override
            public List<T> execute(Jedis jedis) {
                byte[] bytes = jedis.get(key.getBytes());
                if (null == bytes) {
                    return null;
                }
                return SerializationUtils.deserialize(bytes);
            }
        });
    }

    /**
     * 获取数据，基于key方式获取
     *
     * @author fangtinghua
     * @param key
     * @return
     */
    public <T> T getValue(Class<T> clazz, final String key) {
        // 操作
        return template.execute(new JedisHandler<T>() {
            @Override
            public T execute(Jedis jedis) {
                byte[] bytes = jedis.get(key.getBytes());
                if (null == bytes) {
                    return null;
                }
                return SerializationUtils.deserialize(bytes);
            }
        });
    }

    /**
     * 添加或更新缓存，基于key、value方式存储
     *
     * @author fangtinghua
     * @param key
     * @param value
     */
    public String saveOrUpdate(final String key, final Serializable value) {
        // 操作
        return template.execute(new JedisHandler<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.set(key.getBytes(), SerializationUtils.serialize(value));
            }
        });
    }

    /**
     * 删除缓存，基于key方式
     *
     * @author fangtinghua
     * @param group
     */
    public Long delete(final String key) {
        // 操作
        return template.execute(new JedisHandler<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.del(key.getBytes());
            }
        });
    }
}