/*
 * IRoleDao.java         2014年8月26日 <br/>
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

package cn.lonsun.rbac.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;


/**
 * 指示器权限DAO借口
* @Description: 
* @author yy 
* @date 2014年9月23日 下午10:12:26
* @version V1.0
 */
public interface IIndicatorPermissionDao extends IMockDao<IndicatorPermissionEO> {
	
    /**
     * 删除角色的权限
     *
     * @author yy
     * @param roleId
     * @return
     */
    public void deleteByRoleAndIndicator(Long roleId);


    /**
     * 根据roleId查询
     *
     * @author yy
     * @param roleId
     * @return
     */
    public List<IndicatorPermissionEO> getByRoleId(Long roleId);

    /**
     * 根据角色和权限查询
     *
     * @author yy
     * @param roleId
     * @param right
     */
    public IndicatorPermissionEO getByRoleAndIndicator(Long roleId, Long right);
    
    /**
     * 删除权限
     *
     * @param roleId
     * @param indicatorIds
     */
    public void deletePermissions(Long roleId,List<Long> indicatorIds);
    /**
     * 根据角色ID获取所有的正常使用的IndicatorId集合
     * @param roleId
     * @return
     */
    public List<Long> getIndicatorIds(Long roleId);

}

