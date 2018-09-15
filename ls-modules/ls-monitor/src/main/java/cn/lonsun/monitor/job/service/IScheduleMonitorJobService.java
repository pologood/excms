/*
 * ScheduleJobService.java         2016年3月25日 <br/>
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

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.internal.vo.ScheduleJobQueryVO;

/**
 * 定时任务service <br/>
 *
 * @date 2016年3月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IScheduleMonitorJobService extends IBaseService<ScheduleJobEO> {
    // 任务类型
    public static String jobType = "job_type";

    /**
     * 查询分页
     *
     * @author fangtinghua
     * @param queryVO
     * @return
     */
    Pagination getPagination(ScheduleJobQueryVO queryVO);

    /**
     * 暂停
     *
     * @author fangtinghua
     * @param scheduleJobEO
     */
    ScheduleJobEO pauseJob(ScheduleJobEO scheduleJobEO);

    /**
     * 恢复
     *
     * @author fangtinghua
     * @param scheduleJobEO
     */
    ScheduleJobEO resumeJob(ScheduleJobEO scheduleJobEO);

    /**
     * 立即执行
     *
     * @author fangtinghua
     * @param scheduleJobEO
     */
    ScheduleJobEO triggerJob(ScheduleJobEO scheduleJobEO);

    /**
     * 根据clazz和json查询定时任务
     *
     * @author fangtinghua
     * @param clazz
     * @param json
     * @return
     */
    ScheduleJobEO getScheduleJobByClazzAndJson(String clazz,Long siteId, String json);

    /**
     * 删除任务，不操作容器
     *
     * @author fangtinghua
     * @param scheduleJobEO
     */
    void deleteWithoutQuartz(ScheduleJobEO scheduleJobEO);
}