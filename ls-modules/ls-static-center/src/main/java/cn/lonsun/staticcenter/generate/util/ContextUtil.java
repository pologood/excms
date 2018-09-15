/*
 * ContextTaskQueue.java         2016年7月22日 <br/>
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

package cn.lonsun.staticcenter.generate.util;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.staticcenter.generate.CallBack;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 上下文执行相关缓存 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年7月22日 <br/>
 */
public class ContextUtil {

    private static Long DEFAULT_TASK_ID = -9999L;// 默认任务id
    private static Map<Long, Long> typeMap = new ConcurrentHashMap<Long, Long>();// 类型对象缓存
    private static Map<Long, Context> contextMap = new ConcurrentHashMap<Long, Context>();// 上下文缓存
    private static Map<Long, CountDownLatch> countMap = new ConcurrentHashMap<Long, CountDownLatch>();// 计数器对象缓存

    /**
     * 根据id终止任务
     *
     * @param callback
     * @throws InterruptedException
     * @throws GenerateException
     * @author fangtinghua
     */
    public static boolean overTask(Long taskId, CallBack<Context> callback) throws InterruptedException, GenerateException {
        taskId = isEmpty(taskId);
        Context context = contextMap.get(taskId);// 先取出上下文对象
        if (countMap.containsKey(taskId)) {
            CountDownLatch count = countMap.get(taskId);
            do {
                count.countDown();
            } while (count.getCount() > 0);
        }
        boolean over = true;
        while (over) { // 当且仅当计数器缓存中不包括任务时，才说明线程池任务全部终止
            over = countMap.containsKey(taskId);
            Thread.sleep(500);// 休眠
        }
        if (null != callback) {
            callback.execute(context);// 回调
        }
        return over;
    }

    /**
     * 异常结束
     *
     * @param callback
     * @throws InterruptedException
     * @author fangtinghua
     */
    public static boolean exceptionTask(CallBack<Context> callback) throws InterruptedException {
        for (Long taskId : contextMap.keySet()) {
            typeMap.put(taskId, StaticTaskEO.EXCEPTION);// 设置任务类型，用来的结束程序的时候判断
            overTask(taskId, callback);
        }
        return true;
    }

    /**
     * 任务开始
     *
     * @param taskId
     * @param type
     * @param CountDownLatch
     * @param context
     * @author fangtinghua
     */
    public static void startTask(Long taskId, Long type, CountDownLatch CountDownLatch, Context context) {
        taskId = isEmpty(taskId);
        typeMap.put(taskId, type);
        contextMap.put(taskId, context);
        countMap.put(taskId, CountDownLatch);
    }

    /**
     * 任务结束
     *
     * @param taskId
     * @author fangtinghua
     */
    public static void finishTask(Long taskId) {
        taskId = isEmpty(taskId);
        typeMap.remove(taskId);
        contextMap.remove(taskId);
        countMap.remove(taskId);
    }

    /**
     * 根据任务id获取类型
     *
     * @param taskId
     * @return
     * @author fangtinghua
     */
    public static Long getTypeByTaskId(Long taskId) {
        return typeMap.get(isEmpty(taskId));
    }

    /**
     * 设置站点域名
     *
     * @author fangtinghua
     */
    public static void setContextUri() {
        Context context = ContextHolder.getContext();
        Long siteId = null;
        String strId = context.getParamMap().get("siteId");
        if (NumberUtils.isNumber(strId)) {
            siteId = NumberUtils.toLong(strId);
        } else {
            siteId = context.getSiteId();
        }
        if (null != siteId && siteId > 0L) {
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
            AssertUtil.isEmpty(indicatorEO, "站点id:" + siteId + ",不存在.");
            String uri = indicatorEO.getUri();// 域名
            if (IndicatorEO.Type.SUB_Site.toString().equals(indicatorEO.getType())) {// 虚拟子站
                Long parentId = indicatorEO.getParentId();
                IndicatorEO parentEO = CacheHandler.getEntity(IndicatorEO.class, parentId);
                AssertUtil.isEmpty(parentEO, "站点id:" + parentId + ",不存在.");
                uri = parentEO.getUri();
            }
            context.setUri(uri).setSiteType(indicatorEO.getType());
        }
    }

    private static Long isEmpty(Long taskId) {
        return null == taskId ? DEFAULT_TASK_ID : taskId;
    }
}