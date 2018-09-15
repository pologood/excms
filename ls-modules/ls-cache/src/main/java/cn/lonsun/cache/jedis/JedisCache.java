/*
 * JedisService.java         2015年8月21日 <br/>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

/**
 * jedis服务类，存储格式为json <br/>
 *
 * @date 2015年8月21日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class JedisCache {

    @Resource
    private JedisTemplate template;

    /**
     * 
     * 失效--等同于删除缓存
     *
     * @author fangtinghua
     * @param group
     */
    public Long evictGroup(final String... group) {
        // 操作
        return template.execute(new JedisHandler<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.del(group);
            }
        });
    }

    /**
     * 
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
                return jedis.expire(key, seconds);
            }
        });
    }

    /**
     * 
     * 判断对象是否存在，针对hash操作
     *
     * @author fangtinghua
     * @param group
     * @return
     */
    public boolean exists(final String group) {
        // 操作
        return template.execute(new JedisHandler<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.exists(group);
            }
        });
    }

    /**
     * 
     * 获取缓存对象，基于hash方式获取
     *
     * @author fangtinghua
     * @param group
     * @param code
     * @return
     */
    public List<String> getKeys(final String group) {
        // 操作
        return template.execute(new JedisHandler<List<String>>() {
            @Override
            public List<String> execute(Jedis jedis) {
                return new ArrayList<String>(jedis.hkeys(group));
            }
        });
    }

    /**
     * 
     * 获取缓存对象，基于key方式获取
     *
     * @author fangtinghua
     * @param key
     * @return
     */
    public String getValue(final String key) {
        // 操作
        return template.execute(new JedisHandler<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    /**
     * 
     * 获取缓存对象，基于hash方式获取
     *
     * @author fangtinghua
     * @param group
     * @param code
     * @return
     */
    public String getValue(final String group, final String code) {
        // 操作
        return template.execute(new JedisHandler<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.hget(group, code);
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
    public String saveOrUpdate(final String key, final String value) {
        // 操作
        return template.execute(new JedisHandler<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    /**
     * 添加或更新缓存，基于hash方式存储
     *
     * @author fangtinghua
     * @param group
     * @param code
     * @param value
     */
    public Long saveOrUpdate(final String group, final String code, final String value) {
        // 操作
        return template.execute(new JedisHandler<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.hset(group, code, value);
            }
        });
    }

    /**
     * 添加或更新缓存，基于hash方式存储
     *
     * @author fangtinghua
     * @param group
     * @param code
     * @param value
     */
    public String saveOrUpdate(final String group, final Map<String, String> map) {
        // 操作
        return template.execute(new JedisHandler<String>() {
            @Override
            public String execute(Jedis jedis) {
                Pipeline p = jedis.pipelined();
                p.hmset(group, map);
                p.sync();
                return "";// jedis.hmset(group, map);
            }
        });
    }

    /**
     * 
     * 删除缓存，基于hash方式
     *
     * @author fangtinghua
     * @param group
     * @param code
     */
    public Long delete(final String group, final String... code) {
        // 操作
        return template.execute(new JedisHandler<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.hdel(group, code);
            }
        });
    }

    public static void main(String[] args) {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
        Jedis jedis = pool.getResource();

        Map<String, String> user = new HashMap<String, String>();
        user.put("name", "minxr");
        user.put("pwd", "password");
        jedis.hmset("user", user);
        // 取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List
        // 第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数
        List<String> rsmap = jedis.hmget("user", "name");
        System.out.println(rsmap);
        // 删除map中的某个键值
        // jedis.hdel("user","pwd");
        System.out.println(jedis.hmget("user", "pwd")); // 因为删除了，所以返回的是null
        System.out.println(jedis.hlen("user")); // 返回key为user的键中存放的值的个数1
        System.out.println(jedis.exists("user"));// 是否存在key为user的记录 返回true
        System.out.println(jedis.hkeys("user"));// 返回map对象中的所有key [pwd, name]
        System.out.println(jedis.hvals("user"));// 返回map对象中的所有value [minxr,
                                                // password]
        Iterator<String> iter = jedis.hkeys("user").iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            System.out.println(key + ":" + jedis.hmget("user", key));
        }
        pool.close();
    }
}