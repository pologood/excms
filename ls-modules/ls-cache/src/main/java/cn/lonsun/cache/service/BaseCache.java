/*
 * BaseCache.java         2015年8月21日 <br/>
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

import java.util.List;

/**
 * 缓存基本操作接口 <br/>
 *
 * @date 2015年8月21日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface BaseCache {

    /**
     * 失效
     *
     * @author fangtinghua
     * @param group
     */
    void evictGroup(String group);

    /**
     * 
     * 获取缓存值
     *
     * @author fangtinghua
     * @param group
     * @return
     */
    List<String> getList(String group);

    /**
     * 
     * 获取缓存值
     *
     * @author fangtinghua
     * @param group
     * @param code
     * @return
     */
    String getValue(String group, String code);

    /**
     * 
     * 重新加载缓存
     *
     * @author fangtinghua
     * @param group
     */
    void reload(String group);

    /**
     * 
     * 加载全部缓存
     *
     * @author fangtinghua
     */
    void reloadAll();

    /**
     * 获取所有的缓存键值
     *
     * @author fangtinghua
     * @return
     */
    List<String> getCacheKeys();
}