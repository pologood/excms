/*
 * ExceptionLogDaoImpl.java         2014年8月19日 <br/>
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

package cn.lonsun.log.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.log.internal.dao.IExceptionLogDao;
import cn.lonsun.log.internal.entity.ExceptionLogEO;

/**
 * TODO <br/>
 *
 * @date 2014年8月19日 <br/>
 * @author yy <br/>
 * @version v1.0 <br/>
 */
@Repository("rbacExceptionLogDao")
public class ExceptionLogDaoImpl extends BaseDao<ExceptionLogEO> implements IExceptionLogDao {

}
