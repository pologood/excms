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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.dao.ILoginHistoryDao;
import cn.lonsun.log.internal.entity.LoginHistoryEO;

/**
 * 登录日志dao
 *
 * @date 2014年8月19日
 * @author yy
 * @version v1.0
 */
@Repository("rbacLoginHistoryDao")
public class LoginHistoryDaoImpl extends MockDao<LoginHistoryEO> implements ILoginHistoryDao {
	
	
	/**
	 * 根据日期获取登录次数
	 *
	 * @param date
	 * @param type
	 * @return
	 */
	@Override
	public long getLoginTimesByDate(String date,Integer type){
		String hql = "from LoginHistoryEO l where to_char(l.createDate,'yyyy-MM-dd')=? and (l.loginStatus=? or l.loginStatus=?)";
		List<Object> values = new ArrayList<Object>();
		values.add(date);
		values.add("Success");
		values.add("success");
		//如果type为空，表示忽略此项
		if(type!=null){
			hql = hql + " and l.type=?";
			values.add(type);
		}
		return getCount(hql,values.toArray());
	}
	
	/**
	 * 根据日期获取登录用户数量
	 *
	 * @param date
	 * @return
	 */
	@Override
	public long getLoginUserCountByDate(String date){
		String sql = "select distinct (l.uid_) from system_login_history l where to_char(l.create_date, 'yyyy-MM-dd') = ? and (l.login_status = 'Success' or l.login_status = 'success')";
		List<?> list = getObjectsBySql(sql, new Object[]{date});
		return list==null?0L:list.size();
	}
	
    @Override
    public Pagination getPage(Long pageIndex,Integer pageSize, Date startDate, Date endDate, String type, String key) {
        String hql = " from LoginHistoryEO t where  t.recordStatus='Normal' ";
        List<Object> values = new ArrayList<Object>();
        
        if(null!=startDate) {
            hql += " and t.createDate>? ";
            values.add(startDate);
        }
        if(null!=endDate) {
            hql += " and t.createDate<? ";
            values.add(endDate);
        }
        if(null!=StringUtils.trimToNull(type) && null!=StringUtils.trimToNull(key)) {
            if("deion".equals(type)){
            	type = "description";
            }
            hql += "and t."+type+" like '%"+key+"%'"; 
        }
        hql += " order by t.createDate desc ";
        return (Pagination) getPagination(pageIndex, pageSize, hql, values.toArray());
    }

	@Override
	public List<LoginHistoryEO> getAllLogs(Date startDate, Date endDate, String type,
			String key) {
		  String hql = " from LoginHistoryEO t where  t.recordStatus='Normal' ";
	        List<Object> values = new ArrayList<Object>();
	        
	        if(null!=startDate) {
	            hql += " and t.createDate>? ";
	            values.add(startDate);
	        }
	        if(null!=endDate) {
	            hql += " and t.createDate<? ";
	            values.add(endDate);
	        }
	        if(null!=StringUtils.trimToNull(type) && null!=StringUtils.trimToNull(key)) {
	            hql += "and t."+type+" like '%"+key+"%'"; 
	        }
	        hql += " order by t.createDate desc ";
	        return getEntitiesByHql(hql,values.toArray());
	}
}
