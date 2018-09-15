/*
 * IExceptionLogDao.java         2014年8月19日 <br/>
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

package cn.lonsun.log.internal.dao;

import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.entity.LoginHistoryEO;


/**
 * 登录日志dao
 *	 
 * @date     2014年8月19日 
 * @author 	 yy
 * @version	 v1.0
 */
public interface ILoginHistoryDao extends IMockDao<LoginHistoryEO> {
	
	/**
	 * 根据日期获取登录次数
	 *
	 * @param date
	 * @param type 登录类型
	 * @return
	 */
	public long getLoginTimesByDate(String date,Integer type);
	
	/**
	 * 根据日期获取登录用户数量
	 *
	 * @param date
	 * @return
	 */
	public long getLoginUserCountByDate(String date);

    /**
     * 日志查询
     * 
     * @param request
     * @param pageIndex
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param type
     * @param key
     * @return
     */
    public Pagination getPage(Long pageIndex, Integer pageSize, Date startDate, Date endDate, String type, String key);

    /**
     * 获取所有日志
     * @param startDate
     * @param endDate
     * @param type
     * @param key
     * @return
     */
    public List<LoginHistoryEO> getAllLogs(Date startDate, Date endDate, String type, String key);
}

