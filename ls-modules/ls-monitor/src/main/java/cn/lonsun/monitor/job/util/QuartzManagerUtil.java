/*
 * QuartzManager.java         2016年3月23日 <br/>
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

import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;

/**
 * 定时任务管理工具类 <br/>
 *
 * @date 2016年3月23日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class QuartzManagerUtil {
    // 业务数据
    public static final String data = "data";
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(QuartzManagerUtil.class);
    // 定时任务工厂
    private static final SchedulerFactoryBean FACTORY = SpringContextHolder.getBean(SchedulerFactoryBean.class);

    /**
     * 执行接口 ADD REASON. <br/>
     *
     * @date: 2016年3月23日 下午2:15:25 <br/>
     * @author fangtinghua
     */
    private interface Handler<X, Y, Z> {
        void execute(X x, Y y, Z z) throws Throwable;
    }

    /**
     * 验证表达式
     *
     * @author fangtinghua
     * @param cronExpression
     * @return
     */
    public static boolean isValidExpression(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * 添加或者更新定时任务
     *
     * @author fangtinghua
     * @param job
     * @return
     */
    public static boolean saveOrUpdateJob(final ScheduleJobEO job) {
        return execute(job, new Handler<Scheduler, TriggerKey, JobKey>() {

            /**
             * 检查时间
             *
             * @author fangtinghua
             * @param trigger
             * @throws BusinessException
             */
            private void checkDate(CronTrigger trigger) throws BusinessException {
                Date fireDate = TriggerUtils.computeEndTimeToAllowParticularNumberOfFirings((OperableTrigger) trigger, null, 1);
                if (null == fireDate || fireDate.getTime() <= new Date().getTime()) {// 如果任务执行时间已过
                    throw new BusinessException("the given trigger will never fire！");
                }
            }

            @Override
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public void execute(Scheduler x, TriggerKey y, JobKey z) throws Throwable {
                CronTrigger trigger = (CronTrigger) x.getTrigger(y);
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                // 不存在，创建一个
                if (null == trigger) {
                    Class clazz = Class.forName(job.getClazz());
                    JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(z).build();
                    // 加上任务对象
                    jobDetail.getJobDataMap().put(data, job);
                    trigger = TriggerBuilder.newTrigger().withIdentity(y).withSchedule(scheduleBuilder).build();
                    // 检查时间
                    this.checkDate(trigger);
                    // 添加
                    x.scheduleJob(jobDetail, trigger);
                } else {
                    // 按新的cronExpression表达式重新构建trigger
                    trigger = trigger.getTriggerBuilder().withIdentity(y).withSchedule(scheduleBuilder).build();
                    // 检查时间
                    this.checkDate(trigger);
                    // 按新的trigger重新设置job执行
                    x.rescheduleJob(y, trigger);
                }
            }
        });
    }

    /**
     * 暂停一个job
     *
     * @author fangtinghua
     * @param job
     * @return
     */
    public static boolean pauseJob(final ScheduleJobEO job) {
        return execute(job, new Handler<Scheduler, TriggerKey, JobKey>() {

            @Override
            public void execute(Scheduler x, TriggerKey y, JobKey z) throws SchedulerException {
                if (!x.checkExists(z)) {// 不存在
                    QuartzManagerUtil.saveOrUpdateJob(job);
                }
                x.pauseJob(z);
            }
        });
    }

    /**
     * 恢复一个job
     *
     * @author fangtinghua
     * @param job
     * @return
     */
    public static boolean resumeJob(final ScheduleJobEO job) {
        return execute(job, new Handler<Scheduler, TriggerKey, JobKey>() {

            @Override
            public void execute(Scheduler x, TriggerKey y, JobKey z) throws SchedulerException {
                if (!x.checkExists(z)) {// 不存在
                    QuartzManagerUtil.saveOrUpdateJob(job);
                } else {
                    x.resumeJob(z);
                }
            }
        });
    }

    /**
     * 删除一个job
     *
     * @author fangtinghua
     * @param job
     * @return
     */
    public static boolean deleteJob(final ScheduleJobEO job) {
        return execute(job, new Handler<Scheduler, TriggerKey, JobKey>() {

            @Override
            public void execute(Scheduler x, TriggerKey y, JobKey z) throws SchedulerException {
                if (x.checkExists(z)) {// 存在
                    x.deleteJob(z);
                }
            }
        });
    }

    /**
     * 立即执行job
     *
     * @author fangtinghua
     * @param job
     */
    public static boolean triggerJob(final ScheduleJobEO job) {
        return execute(job, new Handler<Scheduler, TriggerKey, JobKey>() {

            @Override
            public void execute(Scheduler x, TriggerKey y, JobKey z) throws SchedulerException {
                if (x.checkExists(z)) {// 存在
                    x.triggerJob(z);
                }
            }
        });
    }

    /**
     * 执行方法
     *
     * @author fangtinghua
     * @param job
     * @param handler
     * @return
     */
    private static boolean execute(ScheduleJobEO job, Handler<Scheduler, TriggerKey, JobKey> handler) {
        if (null == job) {
            return false;
        }
        try {
            String id = String.valueOf(job.getId());
            Scheduler scheduler = FACTORY.getScheduler();
            JobKey jobKey = JobKey.jobKey(id);
            TriggerKey triggerKey = TriggerKey.triggerKey(id);
            handler.execute(scheduler, triggerKey, jobKey);// 执行
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            // 设置任务状态
            job.setStatus(triggerState.name());
            return true;
        } catch (Throwable e) {
            logger.error("定时任务调用失败.", e);
            return false;
        }
    }
}