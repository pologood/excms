/*
 * CacheProcessor.java         2015年8月24日 <br/>
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

import cn.lonsun.cache.jedis.JedisCache;
import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * 缓存加载 <br/>
 *
 * @date 2015年8月24日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class CacheProcessor {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(CacheProcessor.class);

    @Resource
    private JedisCache cache;
    @Resource
    private SessionFactory sessionFactory;

    /**
     * 加载缓存
     *
     * @author fangtinghua
     * @param config
     * @param entity
     * @param key
     * @param codes
     * @return
     */
    public String load(CacheConfig config, CacheEntity entity, String key, Serializable... codes) {
        // 结果
        String result = null;
        // hibernate session对象
        Session session = null;
        try {
            StringBuffer hql = new StringBuffer();
            hql.append("FROM ");
            String className = config.getClazz();
            hql.append(className.substring(className.lastIndexOf(".") + 1));
            hql.append(" WHERE 1=1 ");
            // 加上查询条件
            Map<String, Class> ids = entity.getId();
            if ((codes == null && ids.size() > 0 ) || ids.size() != codes.length) {
                logger.error("参数个数不匹配!");
                return null;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            Set<Map.Entry<String, Class>> idSet = ids.entrySet();
            int index = 0;
            for(Map.Entry<String, Class> field : idSet){
                String mapKey = "cache_v_" + index;
                hql.append(" AND ").append(field.getKey());
                hql.append(" = :").append(mapKey);
                map.put(mapKey, castValue(codes[index], field.getValue()));
                index++;
            }
            if (config.isMock()) {// 逻辑删除
                hql.append(" AND recordStatus = '" + AMockEntity.RecordStatus.Normal.toString());
                hql.append("'");
            }
            // 增加排序功能
            String order = config.getOrder();
            if (!StringUtils.isEmpty(order)) {
                hql.append(" ORDER BY ").append(order);
            }
            // 查询
            session = sessionFactory.openSession();
            Query query = session.createQuery(hql.toString());
            setParameters(map, query);
            List<?> list = query.list();// 查询结果
            Object o = null;// 存储结果
            if (null != list && !list.isEmpty()) {
                if (entity.isGroup()) {
                    o = list;
                } else {// 标准的key-value(主键-单个实例)
                    o = list.get(0);
                }
            }
            result = JSON.toJSONString(o);
            cache.saveOrUpdate(className, key, result);
            int seconds = config.getSeconds();// 过期时间
            if (seconds > 0) {
                cache.expire(className, config.getSeconds());// 设置过期时间
            }
        } catch (Throwable e) {
            logger.error("缓存加载失败!", e);
        } finally {
            try {
                if (null != session) {
                    session.close();
                }
            } catch (HibernateException e) {
                logger.error("session关闭失败", e);
            }
        }
        return result;
    }


    public Object castValue(Object value, Class type){
        if(value == null){
            return null;
        }
        if(type == Long.class){
            return Long.valueOf(String.valueOf(value));
        }
        if(type == Integer.class){
            return Integer.valueOf(String.valueOf(value));
        }
        if(type == String.class){
            return String.valueOf(value);
        }
        if(type == Double.class){
            return Double.valueOf(String.valueOf(value));
        }
        if(type == Float.class){
            return Float.valueOf(String.valueOf(value));
        }
        return value;
    }

    /**
     * 为Query设置查询参数
     * @param params
     * @param q
     */
    private void setParameters(final Map<String, Object> params, final Query q) {
        if (null != params && params.size() > 0) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                Object value = params.get(key);
                if (value instanceof Object[]) {
                    q.setParameterList(key, (Object[]) value);
                } else if (value instanceof Collection<?>) {
                    q.setParameterList(key, (Collection<?>) value);
                } else if (value instanceof List<?>) {
                    q.setParameterList(key, (List<?>) value);
                }  else if(value instanceof Date){
                    q.setTimestamp(key, (Date) value);
                } else {
                    q.setParameter(key, value);
                }
            }
        }
    }
}