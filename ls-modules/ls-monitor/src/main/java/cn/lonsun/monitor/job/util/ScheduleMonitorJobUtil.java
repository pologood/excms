/*
 * ScheduleJobUtil.java         2016年3月31日 <br/>
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

package cn.lonsun.monitor.job.util;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.monitor.job.service.IScheduleMonitorJobService;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * 定时任务业务调用工具类 <br/>
 *
 * @date 2016年3月31日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ScheduleMonitorJobUtil {
    // 日期转cronExpression
    private static final String pattern = "ss mm HH dd MM ? yyyy";
    // 定时任务业务
    private static final IScheduleMonitorJobService scheduleMonitorJobService = SpringContextHolder.getBean(IScheduleMonitorJobService.class);

    /**
     * 日期转换为表达式
     *
     * @author fangtinghua
     * @param date
     * @return
     */
    public static String dateToCronExpression(Date date) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 添加定时任务
     *
     * @author fangtinghua
     * @param name
     * @param clazz
     * @param cronExpression
     * @param json
     */
    public static void addScheduleJob(String name, String clazz,Long siteId, String cronExpression, String json) {
        // 如果已经存在，按照新的表达式执行
        ScheduleJobEO existEO = scheduleMonitorJobService.getScheduleJobByClazzAndJson(clazz,siteId, json);
        if (null != existEO) {
            existEO.setName(name).setCronExpression(cronExpression);
            scheduleMonitorJobService.saveEntity(existEO);
        } else {
            ScheduleJobEO eo = new ScheduleJobEO();
            eo.setSiteId(siteId);
//            DataDictVO vo = DataDictionaryUtil.getDefuatItem(IScheduleMonitorJobService.jobType, null);
            eo.setType("monitor");
            eo.setName(name).setClazz(clazz).setCronExpression(cronExpression).setJson(json);
            scheduleMonitorJobService.saveEntity(eo);
        }
    }

    /**
     * 添加定时任务
     * @author fangtinghua
     * @param name
     * @param clazz
     * @param cronExpression
     * @param json
     */
    public static void updateScheduleJob(String name, String clazz,Long siteId, String cronExpression, String json) {
        // 如果已经存在，按照新的表达式执行
        ScheduleJobEO existEO = scheduleMonitorJobService.getScheduleJobByClazzAndJson(clazz,siteId, json);
        if (null != existEO) {
            if(!existEO.getStatus().equals("Normal")) {
                QuartzManagerUtil.resumeJob(existEO);
            }
        } else {
            ScheduleJobEO eo = new ScheduleJobEO();
            eo.setSiteId(siteId);
//            DataDictVO vo = DataDictionaryUtil.getDefuatItem(IScheduleMonitorJobService.jobType, null);
            eo.setType("monitor");
            eo.setName(name).setClazz(clazz).setCronExpression(cronExpression).setJson(json);
            scheduleMonitorJobService.saveEntity(eo);
        }
    }

    /**
     * 添加定时任务
     *
     * @author fangtinghua
     * @param name
     * @param clazz
     * @param cronExpression
     * @param json
     * @param operate-add、del
     */
    public static void addOrDelScheduleJob(String name, String clazz,Long siteId, String cronExpression, String json, Integer operate) {
        // 如果已经存在，按照新的表达式执行
        ScheduleJobEO existEO = scheduleMonitorJobService.getScheduleJobByClazzAndJson(clazz,siteId, json);
        if (operate == 0) {
            if (null != existEO) {
                scheduleMonitorJobService.delete(ScheduleJobEO.class, existEO.getId());
            }
        } else {
            if (null != existEO) {
                existEO.setName(name).setCronExpression(cronExpression);
                scheduleMonitorJobService.saveEntity(existEO);
            } else {
                ScheduleJobEO eo = new ScheduleJobEO();
                eo.setSiteId(siteId);
//                DataDictVO vo = DataDictionaryUtil.getDefuatItem(IScheduleMonitorJobService.jobType, null);
                eo.setType("monitor");
                eo.setName(name).setClazz(clazz).setCronExpression(cronExpression).setJson(json);
                scheduleMonitorJobService.saveEntity(eo);
            }
        }
    }
}