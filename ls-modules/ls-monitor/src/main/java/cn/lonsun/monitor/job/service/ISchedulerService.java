/*
 * SchedulerService.java         2016年3月22日 <br/>
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

package cn.lonsun.monitor.job.service;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.monitor.job.util.HibernateHandler;
import cn.lonsun.monitor.job.util.HibernateSessionUtil;
import cn.lonsun.monitor.job.util.QuartzManagerUtil;
import cn.lonsun.supervise.util.CronUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * 定时任务接口 <br/>
 *
 * @date 2016年3月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public abstract class ISchedulerService implements Job {
    // 定时任务service
    private IScheduleMonitorJobService scheduleJobService = SpringContextHolder.getBean(IScheduleMonitorJobService.class);

    /**
     * 业务执行方法
     *
     * @author fangtinghua
     */
    public abstract void execute(String json);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 下次任务执行时间
        Date date = context.getTrigger().getNextFireTime();

        //增加时间判断
        if(null == date) {
            JobDataMap dataMap = context.getMergedJobDataMap();
            ScheduleJobEO job = (ScheduleJobEO)dataMap.get("data");
            if(null != job.getCronExpression()) {
                date = CronUtil.getNextCronTime(job.getCronExpression());
            }
        }
        final Date nextFireDate = date;
        // 业务参数
        final ScheduleJobEO scheduleJobEO = (ScheduleJobEO) context.getMergedJobDataMap().get(QuartzManagerUtil.data);
        // 业务方法执行
        if (!HibernateSessionUtil.execute(new HibernateHandler<Boolean>() {

            @Override
            public Boolean execute() throws Throwable {
                ISchedulerService.this.execute(scheduleJobEO.getJson());
                if (null == nextFireDate) {
                    scheduleJobService.delete(ScheduleJobEO.class, scheduleJobEO.getId());
                }
                return true;
            }

            @Override
            public Boolean complete(Boolean result, Throwable exception) {
                return null == exception;
            }
        })) {// 抛出异常
            throw new JobExecutionException("定时任务执行失败.");
        }
    }
}