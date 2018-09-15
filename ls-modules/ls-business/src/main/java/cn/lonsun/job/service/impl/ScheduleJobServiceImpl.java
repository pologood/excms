/*
 * ScheduleJobServiceImpl.java         2016年3月25日 <br/>
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

package cn.lonsun.job.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.dao.IScheduleJobDao;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.internal.vo.ScheduleJobQueryVO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.util.QuartzManagerUtil;
import cn.lonsun.util.DataDictionaryUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务service <br/>
 *
 * @date 2016年3月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class ScheduleJobServiceImpl extends BaseService<ScheduleJobEO> implements IScheduleJobService {

    @DbInject("scheduleJob")
    private IScheduleJobDao scheduleJobDao;

    @Override
    public Long saveEntity(ScheduleJobEO scheduleJobEO) {
        Long id = scheduleJobEO.getId();
        if (null == id) {
            super.saveEntity(scheduleJobEO);
        } else {
            super.updateEntity(scheduleJobEO);
        }
        QuartzManagerUtil.saveOrUpdateJob(scheduleJobEO);
        scheduleJobEO.setTypeName(DataDictionaryUtil.getItem(jobType, scheduleJobEO.getType()).getKey());
        return scheduleJobEO.getId();
    }

    @Override
    public void delete(Class<ScheduleJobEO> clazz, Long id) {
        ScheduleJobEO scheduleJobEO = new ScheduleJobEO();
        scheduleJobEO.setId(id);
        QuartzManagerUtil.deleteJob(scheduleJobEO);
        super.delete(clazz, id);// 删除数据库
    }

    @Override
    public void deleteWithoutQuartz(ScheduleJobEO scheduleJobEO) {
        super.delete(scheduleJobEO);// 删除数据库
    }

    @Override
    public Pagination getPagination(ScheduleJobQueryVO queryVO) {
        Pagination pagination = scheduleJobDao.getPagination(queryVO);
        List<?> list = pagination.getData();
        if (null != list && !list.isEmpty()) {
            for (Object o : list) {
                ScheduleJobEO eo = (ScheduleJobEO) o;
                eo.setTypeName(DataDictionaryUtil.getItem(jobType, eo.getType()).getKey());
            }
        }
        return pagination;
    }

    @Override
    public ScheduleJobEO pauseJob(ScheduleJobEO scheduleJobEO) {
        ScheduleJobEO updateEO = super.getEntity(ScheduleJobEO.class, scheduleJobEO.getId());
        QuartzManagerUtil.pauseJob(updateEO);
        super.updateEntity(updateEO);
        updateEO.setTypeName(DataDictionaryUtil.getItem(jobType, updateEO.getType()).getKey());
        return updateEO;
    }

    @Override
    public ScheduleJobEO resumeJob(ScheduleJobEO scheduleJobEO) {
        ScheduleJobEO updateEO = super.getEntity(ScheduleJobEO.class, scheduleJobEO.getId());
        QuartzManagerUtil.resumeJob(updateEO);
        super.updateEntity(updateEO);
        updateEO.setTypeName(DataDictionaryUtil.getItem(jobType, updateEO.getType()).getKey());
        return updateEO;
    }

    @Override
    public ScheduleJobEO triggerJob(ScheduleJobEO scheduleJobEO) {
        QuartzManagerUtil.triggerJob(scheduleJobEO);
        return scheduleJobEO;
    }

    @Override
    public ScheduleJobEO getScheduleJobByClazzAndJson(String clazz, String json) {
        return scheduleJobDao.getScheduleJobByClazzAndJson(clazz, json);
    }
}