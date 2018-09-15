/*
 * JobInit.java         2016年3月25日 <br/>
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

package cn.lonsun.common.job;

import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.util.QuartzManagerUtil;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 定时任务初始化 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年3月25日 <br/>
 */
public class JobInit {

    @Resource
    private IScheduleJobService scheduleJobService;

    public void init() {
        Map<String, Object> paramMap = Collections.emptyMap();
        List<ScheduleJobEO> scheduleJobEOList = scheduleJobService.getEntities(ScheduleJobEO.class, paramMap);
        if (null != scheduleJobEOList && !scheduleJobEOList.isEmpty()) {// 放入定时任务容器
            for (ScheduleJobEO scheduleJobEO : scheduleJobEOList) {
                String status = scheduleJobEO.getStatus();
                boolean result = QuartzManagerUtil.saveOrUpdateJob(scheduleJobEO);
                if (result) {
                    if (!status.equals(scheduleJobEO.getStatus())) {// 更新状态
                        scheduleJobService.updateEntity(scheduleJobEO);
                    }
                } else {// 当定时任务添加不成功时，删除
                    scheduleJobService.deleteWithoutQuartz(scheduleJobEO);
                }
            }
        }
    }
}