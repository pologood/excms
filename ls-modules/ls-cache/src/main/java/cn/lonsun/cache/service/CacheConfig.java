/*
 * CacheConfig.java         2015年8月24日 <br/>
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
import cn.lonsun.core.exception.BaseRunTimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存配置 <br/>
 *
 * @date 2015年8月24日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class CacheConfig {

    // 日志
    private  static  final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    private CacheConfig() {

    }

    private String clazz;// 唯一clazz，存储在redis中的第一键值
    private Class JavaClass;
    private List<CacheEntity> list;// 存储结构
    private int seconds;// 过期时间，单位秒
    private boolean isMock;// 默认为逻辑删除
    private String order;// 排序功能
    private Map<String, String> map;// 查询参数，暂时不需要，后续可以作为扩展查询使用

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public List<CacheEntity> getList() {
        return list;
    }

    public void setList(List<CacheEntity> list) {
        this.list = list;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public boolean isMock() {
        return isMock;
    }

    public void setMock(boolean isMock) {
        this.isMock = isMock;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public static Builder createBuilder(Class<?> clazz) {
        Builder builder = new Builder(clazz);
        return builder;
    }

    public static class Builder {
        private String clazz;// 唯一clazz，存储在redis中的第一键值
        private Class JavaClass;
        private List<CacheEntity> list = null;// 存储结构
        private int seconds = 0;// 过期时间，单位秒
        private boolean isMock = true;
        private String order = "";// 排序功能
        private Map<String, String> map = null;

        private Builder(Class<?> clazz) {
            this.JavaClass = clazz;
            this.setClazz(clazz.getName());
        }

        public Builder setClazz(String clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder setEntity(String field, boolean isGroup, String... id) {
            if (null == list) {
                list = new ArrayList<CacheEntity>();
            }
            Map<String, Class> map = new HashMap<String, Class>();
            setFieldType(map, id);
            CacheEntity entity = new CacheEntity(field, isGroup, map);
            list.add(entity);
            return this;
        }

        private void setFieldType(Map<String, Class> map, String[] id) {
            for(String item : id){
                try {
                    map.put(item, getFieldClass(JavaClass, item));
                } catch (Exception e) {
                    logger.error("缓存初始化失败，字段获取失败：{}.{}", clazz, item);
                    continue;
                }
            }
        }

        private Class getFieldClass(Class type, String field){
            try {
                return type.getDeclaredField(field).getType();
            } catch (Exception e) {//如果没有该字段，去父类找
                if(type.getSuperclass() != null){
                    return getFieldClass(type.getSuperclass(), field);
                }
            }
            throw new BaseRunTimeException("类型中没有该字段");
        }


        public Builder setEntity(String field, String... id) {
            return this.setEntity(field, false, id);
        }

        public Builder setEntity(boolean isGroup, String... id) {
            return this.setEntity(CacheGroup.CMS_JOIN_ID, isGroup, id);
        }

        public Builder setEntity(String id) {// 主键
            return this.setEntity(CacheGroup.CMS_ID, false, id);
        }

        public Builder setSeconds(int seconds) {
            this.seconds = seconds;
            return this;
        }

        public Builder setMock(boolean isMock) {
            this.isMock = isMock;
            return this;
        }

        public Builder setOrder(String order) {
            this.order = order;
            return this;
        }

        public Builder setParam(String key, String value) {
            if (null == map) {
                map = new HashMap<String, String>();
            }
            map.put(key, value);
            return this;
        }

        public Class getJavaClass() {
            return JavaClass;
        }

        public void setJavaClass(Class javaClass) {
            JavaClass = javaClass;
        }

        public CacheConfig build() {
            CacheConfig cacheConfig = new CacheConfig();

            cacheConfig.setClazz(clazz);
            cacheConfig.setJavaClass(JavaClass);
            cacheConfig.setSeconds(seconds);
            cacheConfig.setList(list);
            cacheConfig.setMock(isMock);
            cacheConfig.setOrder(order);
            cacheConfig.setMap(map);
            return cacheConfig;
        }
    }

    public Class getJavaClass() {
        return JavaClass;
    }

    public void setJavaClass(Class javaClass) {
        JavaClass = javaClass;
    }
}