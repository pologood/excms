/*
 * IScheduleJobDao.java         2016年3月25日 <br/>
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

package cn.lonsun.monitor.job.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.internal.vo.ScheduleJobQueryVO;

/**
 * TODO <br/>
 *
 * @date 2016年3月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IScheduleMonitorJobDao extends IBaseDao<ScheduleJobEO> {

    /**
     * 查询分页
     *
     * @author fangtinghua
     * @param queryVO
     * @return
     */
    Pagination getPagination(ScheduleJobQueryVO queryVO);

    /**
     * 根据clazz和json查询定时任务
     *
     * @author fangtinghua
     * @param clazz
     * @param json
     * @return
     */
    ScheduleJobEO getScheduleJobByClazzAndJson(String clazz,Long siteId, String json);
}