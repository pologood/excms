/*
 * CacheProxy.java         2015年8月24日 <br/>
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

package cn.lonsun.cache.service;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.jedis.JedisCache;
import com.alibaba.fastjson.JSON;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存代理 <br/>
 * 
 * @date 2015年8月24日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class CacheProxy {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean init = false;// 第一次初始化时，清空所有缓存
    @Resource
    private JedisCache cache;
    @Resource
    private CacheProcessor cacheProcessor;
    // 本地配置缓存
    private Map<String, CacheConfig> localConfig = new ConcurrentHashMap<String, CacheConfig>();

    /**
     * 获取缓存数据
     * 
     * @author fangtinghua
     * @param clazz
     * @param field
     * @return
     */
    public String getValue(String clazz, String field, Serializable... codes) {
        if (StringUtils.isEmpty(clazz) || StringUtils.isEmpty(field) || ArrayUtils.isEmpty(codes)) {// 为空返回null
            return null;
        }
        CacheConfig config = this.getCacheConfig(clazz);
        if (null == config) {
            return null;
        }
        List<CacheEntity> entityList = config.getList();
        Map<String, CacheEntity> entityMap = new HashMap<String, CacheEntity>();
        for (CacheEntity cacheEntity : entityList) {
            entityMap.put(cacheEntity.getField(), cacheEntity);
        }
        if (!entityMap.containsKey(field)) {
            return null;
        }
        // 获取key
        CacheEntity entity = entityMap.get(field);
        String key = this.getKey(entity, codes);// 键值
        String result = cache.getValue(clazz, key);
        if (StringUtils.isEmpty(result)) {// 缓存中为空，从数据库查，并放到缓存中去
            result = cacheProcessor.load(config, entity, key, codes);
        }
        return result;
    }

    /**
     * 更新数据
     * 
     * @author fangtinghua
     * @param clazz
     * @param list
     */
    public <T> void saveOrUpdate(Class<T> clazz, List<T> list) {
        if (null == list || list.isEmpty()) {// 为空
            return;
        }
        String name = clazz.getName();
        CacheConfig config = this.getCacheConfig(name);
        if (null == config) {
            return;
        }
        List<CacheEntity> entityList = config.getList();
        // 计算所有缓存数据
        Map<String, String> hashMap = new HashMap<String, String>();
        for (T t : list) {// 循环列表
            for (CacheEntity cacheEntity : entityList) {// 循环实体
                String key = this.getKey(cacheEntity, t);
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                if (!cacheEntity.isGroup()) {// 标准的key-value(主键-单个实例)
                    hashMap.put(key, JSON.toJSONString(t));
                } else {// 特殊的key-value(主键-List<实例>)
                    String v = "";
                    if (hashMap.containsKey(key)) {
                        v = hashMap.get(key);
                    } else {
                        v = cache.getValue(name, key);
                    }
                    List<T> l = null;
                    if (!StringUtils.isBlank(v) && !"null".equalsIgnoreCase(v)) {
                        l = JSON.parseArray(v, clazz);
                    } else {
                        l = new ArrayList<T>();
                    }
                    l.add(t);
                    hashMap.put(key, JSON.toJSONString(l));
                }
            }
        }
        if (!hashMap.isEmpty()) {
            cache.saveOrUpdate(name, hashMap);
            // 刷新时间
            int seconds = config.getSeconds();// 过期时间
            if (seconds > 0) {
                cache.expire(name, config.getSeconds());// 设置过期时间
            }
        }
    }

    /**
     * 删除数据
     * 
     * @author fangtinghua
     * @param clazz
     * @param list
     */
    public <T> void delete(Class<T> clazz, List<T> list) {
        if (null == list || list.isEmpty()) {// 为空
            return;
        }
        String name = clazz.getName();
        CacheConfig config = this.getCacheConfig(name);
        if (null == config) {
            return;
        }
        List<CacheEntity> entityList = config.getList();
        // 计算所有缓存数据
        List<String> deleteList = new ArrayList<String>();// 需要删除的列表
        Map<String, String> hashMap = new HashMap<String, String>();
        for (T t : list) {// 循环列表
            for (CacheEntity cacheEntity : entityList) {// 循环实体
                String key = this.getKey(cacheEntity, t);
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                if (!cacheEntity.isGroup()) {// 标准的key-value(主键-单个实例)
                    deleteList.add(key);
                } else {// 特殊的key-value(主键-List<实例>)
                    String v = "";
                    if (hashMap.containsKey(key)) {
                        v = hashMap.get(key);
                    } else {
                        v = cache.getValue(name, key);
                    }
                    if (!StringUtils.isEmpty(v) && !"null".equalsIgnoreCase(v)) {
                        List<T> l = JSON.parseArray(v, clazz);
                        l.remove(t);
                        hashMap.put(key, JSON.toJSONString(l));
                    }
                }
            }
        }
        if (!deleteList.isEmpty()) {// 删除列表
            cache.delete(name, deleteList.toArray(new String[] {}));
        }
        if (!hashMap.isEmpty()) {
            cache.saveOrUpdate(name, hashMap);
            // 刷新时间
            int seconds = config.getSeconds();// 过期时间
            if (seconds > 0) {
                cache.expire(name, config.getSeconds());// 设置过期时间
            }
        }
    }

    /**
     * 获取键值
     * 
     * @author fangtinghua
     * @param cacheEntity
     * @param codes
     * @return
     */
    private String getKey(CacheEntity cacheEntity, Serializable... codes) {
        String key = "";
        try {
            int index = 0;
            Set<String> ids = cacheEntity.getId().keySet();
            if(ids != null){
                for (String id : ids) {
                    if (index++ > 0) {
                        key += CacheGroup.CMS_SPLIT;
                    }
                    key += id + CacheGroup.CMS_SPLIT + String.valueOf(codes[index - 1]);
                }
            }
        } catch (Throwable e) {
            logger.error("获取缓存key失败", e);
            return null;
        }
        return key;
    }

    /**
     * 获取键值
     * 
     * @author fangtinghua
     * @param cacheEntity
     * @param o
     * @return
     */
    private String getKey(CacheEntity cacheEntity, Object o) {
        String key = "";
        try {
            int index = 0;
            Set<String> ids = cacheEntity.getId().keySet();
            if(ids != null) {
                for (String id : ids) {
                    if (index++ > 0) {
                        key += CacheGroup.CMS_SPLIT;
                    }
                    key += id + CacheGroup.CMS_SPLIT + BeanUtils.getProperty(o, id);
                }
            }
        } catch (Throwable e) {
            logger.error("获取缓存key失败", e);
            return null;
        }
        return key;
    }

    /**
     * 获取缓存配置信息
     * 
     * @author fangtinghua
     * @param clazz
     * @return
     */
    private CacheConfig getCacheConfig(String clazz) {
        if (localConfig.containsKey(clazz)) {
            return localConfig.get(clazz);
        }
        String entity = cache.getValue(CacheGroup.CMS_CACHE_CONFIG, clazz);
        if (StringUtils.isEmpty(entity)) {
            return null;
        }
        CacheConfig config = JSON.parseObject(entity, CacheConfig.class);
        localConfig.put(clazz, config);
        return config;
    }

    /**
     * 
     * 懒加载缓存
     * 
     * @author fangtinghua
     * @param clazz
     */
    public void reload(String clazz) {
        cache.evictGroup(clazz);
    }

    /**
     * 
     * 获取所有缓存键值
     * 
     * @author fangtinghua
     */
    public List<String> getCacheKeys() {
        return cache.getKeys(CacheGroup.CMS_CACHE_CONFIG);
    }

    /**
     * 加载所有缓存
     * 
     * @author fangtinghua
     * @param excludeList
     */
    public void reloadAll(List<String> excludeList) {
        List<String> keys = getCacheKeys();
        if (null != keys && !keys.isEmpty()) {
            if (null != excludeList && !excludeList.isEmpty()) {
                keys.removeAll(excludeList);// 差集
            }
            cache.evictGroup(keys.toArray(new String[] {}));
        }
    }

    /**
     * 添加到缓存中
     * 
     * @author fangtinghua
     * @param cacheConfig
     */
    public void addToCache(CacheConfig cacheConfig) {
        if (!init) {
            cache.evictGroup(CacheGroup.CMS_CACHE_CONFIG);
            init = true;
        }
        String clazz = cacheConfig.getClazz();
        cache.saveOrUpdate(CacheGroup.CMS_CACHE_CONFIG, clazz, JSON.toJSONString(cacheConfig));
    }
}