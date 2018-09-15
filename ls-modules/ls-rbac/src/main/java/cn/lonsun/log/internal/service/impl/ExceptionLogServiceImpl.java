/*
 * ExceptionLogServiceImpl.java         2014年8月19日 <br/>
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

package cn.lonsun.log.internal.service.impl;

import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.log.internal.entity.ExceptionLogEO;
import cn.lonsun.log.internal.service.IExceptionLogService;


/**
 * 异常日志服务
 *
 * @author xujh
 * @version 1.0
 * 2015年2月13日
 *
 */
@Service("rbacExceptionLogService")
public class ExceptionLogServiceImpl extends BaseService<ExceptionLogEO> implements IExceptionLogService {

}

