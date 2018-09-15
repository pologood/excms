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

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.dao.ILoginHistoryDao;
import cn.lonsun.log.internal.entity.LoginHistoryEO;
import cn.lonsun.log.internal.service.ILoginHistoryService;

/**
 * 登录历史日志服务类
 * 
 * @date 2014年8月19日
 * @author yy
 * @version v1.0
 */
@Service("rbacLoginHistoryService")
public class LoginHistoryServiceImpl extends MockService<LoginHistoryEO>
		implements ILoginHistoryService {
	@Autowired
	private ILoginHistoryDao rbacLoginHistoryDao;

	@Override
	public Pagination getPage(Long pageIndex, Integer pageSize, Date startDate,
			Date endDate, String type, String key) {
		return (Pagination) rbacLoginHistoryDao.getPage(pageIndex, pageSize,
				startDate, endDate, type, key);
	}

	@Override
	public List<LoginHistoryEO> getAllLogs(Date startDate, Date endDate,
			String type, String key) {
		return rbacLoginHistoryDao.getAllLogs(startDate, endDate, type, key);
	}
}
