/*
 * RoleServiceImpl.java         2015年9月1日 <br/>
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

package cn.lonsun.rbac.indicator.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.lonsun.rbac.indicator.service.IEXRoleService;
import cn.lonsun.rbac.internal.service.IIndicatorPermissionService;
import cn.lonsun.rbac.internal.service.IPermissionService;

/**
 * 角色管理 <br/>
 *
 * @date 2015年9月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service("ex_8_RoleServiceImpl")
public class EXRoleServiceImpl implements IEXRoleService {
    @Resource
    private IIndicatorPermissionService indicatorPermissionService;
    @Resource
    private IPermissionService permissionService;

    @Override
    public Long save(Long roleId, String rights) {
        indicatorPermissionService.updateRoleAndIndicator(roleId, rights);
        permissionService.updatePermission(roleId, rights);
        return roleId;
    }
}