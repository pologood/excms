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

package cn.lonsun.job.dao.impl;

import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.util.LoginPersonUtil;
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
@Repository("scheduleJobMySqlDao")
public class ScheduleJobMySqlDaoImpl extends ScheduleJobDaoImpl {

    @Override
    public ScheduleJobEO getScheduleJobByClazzAndJson(String clazz, String json) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from ScheduleJobEO where siteId = ? ");
        List<Object> values = new ArrayList<Object>();
        values.add(LoginPersonUtil.getSiteId());
        if (!StringUtils.isEmpty(clazz)) {
            hql.append(" and clazz = ? ");
            values.add(clazz);
        }
        if (!StringUtils.isEmpty(json)) {
            hql.append(" and json = ? ");
            values.add(json);
        }
        return getEntityByHql(hql.toString(), values.toArray());
    }
}