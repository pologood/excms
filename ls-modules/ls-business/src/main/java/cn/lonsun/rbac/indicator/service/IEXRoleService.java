/*
 * IRoleService.java         2015年9月1日 <br/>
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

package cn.lonsun.rbac.indicator.service;

/**
 * 角色service <br/>
 *
 * @date 2015年9月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IEXRoleService {

    /**
     * 添加角色，同时为为角色添加权限
     * 
     * @param roleId
     * @param rights
     */
    public Long save(Long roleId, String rights);
}