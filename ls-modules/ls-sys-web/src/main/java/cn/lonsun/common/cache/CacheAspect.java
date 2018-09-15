/*
 * CacheAspect.java         2015年8月24日 <br/>
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

package cn.lonsun.common.cache;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.cache.jedis.JedisCache;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.util.ReflectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import javax.annotation.Resource;
import java.util.List;

/**
 * 缓存切面 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月24日 <br/>
 */
@Aspect
public class CacheAspect {
    // 缓存判断
    private Boolean isLoadList = false;
    private List<String> cachePage = null;
    @Resource
    private JedisCache jedisCache;

    /**
     * 拦截service层中的save*、delete*、update*开始的方法，当出现对类进行操作时，使得redis缓存失效，重新加载
     *
     * @param joinPoint
     * @author fangtinghua
     */
    @After("execution (* cn.lonsun..service.impl.*.save*(..)) || execution (* cn.lonsun..service.impl.*.delete*(..))"
            + " || execution (* cn.lonsun..service.impl.*.update*(..))")
    public void flushCache(JoinPoint joinPoint) {
        if (!isLoadList) {
            synchronized (isLoadList) {
                if (!isLoadList) {
                    isLoadList = true;
                    cachePage = CacheHandler.getCacheKeys();
                }
            }
        }
        // 获取泛型类型
        Class<?> clazz = ReflectionUtils.getSuperClassGenricType(joinPoint.getTarget().getClass());
        if (null != clazz) {
            String name = clazz.getName();
            if (null != cachePage && cachePage.contains(name) && !BaseContentEO.class.getName().equals(name)) {// 存在缓存中，排除文章(手动更新)
                if (IndicatorEO.class.getName().equals(name) || ContentModelEO.class.getName().equals(name) // 如果操作的是站点、栏目、菜单、内容模型
                        || SiteConfigEO.class.getName().equals(name) || ColumnConfigEO.class.getName().equals(name)) {
                    jedisCache.evictGroup(ColumnMgrEO.class.getName(), SiteMgrEO.class.getName(), IndicatorEO.class.getName(),
                            ContentModelEO.class.getName(), SiteConfigEO.class.getName(), ColumnConfigEO.class.getName());// 栏目关联树失效
                }
                jedisCache.evictGroup(name);// 缓存失效
            }
        }
    }
}