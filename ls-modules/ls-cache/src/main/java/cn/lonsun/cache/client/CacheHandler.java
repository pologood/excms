/*
 * CacheHandler.java         2015年8月24日 <br/>
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

package cn.lonsun.cache.client;

import cn.lonsun.cache.service.CacheConfig;
import cn.lonsun.cache.service.CacheProxy;
import cn.lonsun.core.util.SpringContextHolder;

import com.alibaba.fastjson.JSON;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存操作接口 <br/>
 *
 * @date 2015年8月24日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class CacheHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(CacheHandler.class);
    private static CacheProxy cacheProxy = SpringContextHolder.getBean(CacheProxy.class);

    private CacheHandler() {
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:17:43
     * @description: 根据code获取单个实体类，默认存储的第二键值后缀为空
     */
    public static <T> T getEntity(Class<T> clazz, Serializable... code) {
        return getEntity(clazz, CacheGroup.CMS_ID, code);
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:18:16
     * @description: 根据field、code获取单个实体类
     */
    public static <T> T getEntity(Class<T> clazz, String field, Serializable... code) {
        String name = clazz.getName();
        String entry = "";
        if (code.length == 0) {
            entry = getValue(name, CacheGroup.CMS_ID, field);
        } else {
            entry = getValue(name, field, code);
        }
        return JSON.parseObject(entry, clazz);
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:17:43
     * @description: 根据code获取实体类列表信息，默认存储的第二键值后缀为空
     */
    public static <T> List<T> getList(Class<T> clazz, Serializable... code) {
        return getList(clazz, CacheGroup.CMS_ID, code);
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:18:16
     * @description: 根据field、code获取实体类列表信息
     */
    public static <T> List<T> getList(Class<T> clazz, String field, Serializable... code) {
        String name = clazz.getName();
        String json = "";
        if (code.length == 0) {
            json = getValue(name, CacheGroup.CMS_ID, field);
        } else {
            json = getValue(name, field, code);
        }
        return JSON.parseArray(json, clazz);
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:17:43
     * @description: 根据code、fieldName获取实体类中字段信息，默认存储的第二键值后缀为空，defaultValue为默认值
     */
    public static <T, X> X getAttr(Class<T> clazz, String fieldName, X defaultValue, Serializable... code) {
        return getAttr(clazz, CacheGroup.CMS_ID, fieldName, defaultValue, code);
    }

    /**
     * 新增或者更新
     *
     * @author fangtinghua
     * @param clazz
     * @param t
     */
    public static <T> void saveOrUpdate(Class<T> clazz, T t) {
        List<T> list = new ArrayList<T>();
        list.add(t);
        saveOrUpdate(clazz, list);
    }

    /**
     * 新增或者更新
     *
     * @author fangtinghua
     * @param clazz
     * @param list
     */
    public static <T> void saveOrUpdate(Class<T> clazz, List<T> list) {
        cacheProxy.saveOrUpdate(clazz, list);
    }

    /**
     * 删除
     *
     * @author fangtinghua
     * @param clazz
     * @param t
     */
    public static <T> void delete(Class<T> clazz, T t) {
        List<T> list = new ArrayList<T>();
        list.add(t);
        delete(clazz, list);
    }

    /**
     * 删除
     *
     * @author fangtinghua
     * @param clazz
     * @param list
     */
    public static <T> void delete(Class<T> clazz, List<T> list) {
        cacheProxy.delete(clazz, list);
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:17:43
     * @description: 根据code、field、fieldName获取实体类中字段信息，defaultValue为默认值
     */
    @SuppressWarnings("unchecked")
    public static <T, X> X getAttr(Class<T> clazz, String field, String fieldName, X defaultValue, Serializable... code) {
        T t = getEntity(clazz, field, code);
        String o = "";
        try {
            o = BeanUtils.getProperty(t, fieldName);
        } catch (Exception e) {
            LOGGER.error("获取字段信息失败，字段不存在...", e);
        }
        return (X) (StringUtils.isEmpty(o) ? defaultValue : o);
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:21:38
     * @description: 根据clazz、code从缓存中获取对象
     */
    public static String getValue(String clazz, String field, Serializable... code) {
        return cacheProxy.getValue(clazz, field, code);
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:22:17
     * @description: 重新加载clazz缓存
     */
    public static void reload(String clazz) {
        cacheProxy.reload(clazz);
    }

    /**
     * @author: fangtinghua
     * @date: 2014-10-17 上午10:22:17
     * @description: 重新加载所有缓存
     */
    public static void reloadAll(List<String> excludeList) {
        cacheProxy.reloadAll(excludeList);
    }

    /**
     * 查询所有缓存键列表
     *
     * @author fangtinghua
     * @return
     */
    public static List<String> getCacheKeys() {
        return cacheProxy.getCacheKeys();
    }

    /**
     * 
     * 添加到缓存中
     *
     * @author fangtinghua
     * @param cacheConfig
     */
    public static void addToCache(CacheConfig cacheConfig) {
        cacheProxy.addToCache(cacheConfig);
    }
}