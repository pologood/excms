/*
 * ScheduleJobDaoImpl.java         2016年3月25日 <br/>
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

package cn.lonsun.monitor.job.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.internal.vo.ScheduleJobQueryVO;
import cn.lonsun.monitor.job.dao.IScheduleMonitorJobDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2016年3月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Repository("scheduleMonitorJobDao")
public class ScheduleMonitorJobDaoImpl extends BaseDao<ScheduleJobEO> implements IScheduleMonitorJobDao {

    @Override
    public Pagination getPagination(ScheduleJobQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from ScheduleJobEO where 1=1");
        if(queryVO.getSiteId() != null){
            hql.append(" and siteId = "+queryVO.getSiteId());
        }
        if (!StringUtils.isEmpty(queryVO.getName())) {
            hql.append(" and name like '%" + SqlUtil.prepareParam4Query(queryVO.getName()) + "%'");
        }
        if (!StringUtils.isEmpty(queryVO.getType())) {
            hql.append(" and type = " + queryVO.getType());
        }
        if (!StringUtils.isEmpty(queryVO.getStatus())) {
            hql.append(" and status = " + queryVO.getStatus());
        }
        // 排序
        hql.append(" order by createDate desc");
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), new Object[] {});
    }

    @Override
    public ScheduleJobEO getScheduleJobByClazzAndJson(String clazz,Long siteId, String json) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from ScheduleJobEO where siteId = ? ");
        List<Object> values = new ArrayList<Object>();
        values.add(siteId);
        if (!StringUtils.isEmpty(clazz)) {
            hql.append(" and clazz = ? ");
            values.add(clazz);
        }
        if (!StringUtils.isEmpty(json)) {
            hql.append(" and to_char(json) = ? ");
            values.add(json);
        }
        return getEntityByHql(hql.toString(), values.toArray());
    }
}