/*
 * ThreadHolder.java         2016年5月23日 <br/>
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

package cn.lonsun.staticcenter.generate.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建上下文线程变量 <br/>
 *
 * @date 2016年5月23日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ThreadHolder {

    public enum LocalParamsKey {
        site, // 站点
        siteConfig, // 站点配置
        contentMode, // 内容模型
        modelTypeCode, // 栏目类型
        template, // 模板配置
        virtual, // 虚拟站点模板配置
        publicInfo// 信息公开信息
    }

    public static final ThreadLocal<Map<String, Object>> contextHolder = new ThreadLocal<Map<String, Object>>();

    private static Map<String, Object> getMap() {
        Map<String, Object> map = contextHolder.get();
        if (null == map) {
            map = new HashMap<String, Object>();
        }
        return map;
    }

    public static boolean containsContext(String key) {
        Map<String, Object> map = getMap();
        return map.containsKey(key);
    }

    public static void setContext(String key, Object value) {
        Map<String, Object> map = getMap();
        map.put(key, value);
        contextHolder.set(map);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getContext(Class<T> clazz, String key) {
        return (T) getMap().get(key);
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}