/*
 * IndicatorPermissionController.java         2014年8月27日 <br/>
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

package cn.lonsun.rbac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.rbac.internal.service.IIndicatorPermissionService;

/**
 * 指示器控制器
 * @Description:
 * @author yjj
 * @date 2014年9月23日 下午10:10:04
 * @version V1.0
 */
@Controller
@RequestMapping(value = "indicatorPermission", produces = { "application/json;charset=UTF-8" })
public class IndicatorPermissionController extends BaseController {
	@Autowired
	private IIndicatorPermissionService indicatorPermissionService;

	/**
	 * 保存角色权限信息
	 * 
	 * @author yy
	 * @param roleId
	 * @param indicators
	 * @return
	 */
	@RequestMapping(value = "save")
	@ResponseBody
	public Object save(Long roleId, List<IndicatorEO> indicators) {
		for (IndicatorEO indicator : indicators) {
			IndicatorPermissionEO indicatorPermissionEO = new IndicatorPermissionEO();
			indicatorPermissionEO.setIndicatorId(indicator.getIndicatorId());
			indicatorPermissionEO.setRoleId(roleId);
			indicatorPermissionService.saveEntity(indicatorPermissionEO);
		}
		return this.getObject();
	}

	/**
	 * 删除角色权限信息
	 * 
	 * @author yy
	 * @param roleId
	 * @param indicators
	 * @return
	 */
	@RequestMapping(value = "delete")
	@ResponseBody
	public Object delete(Long roleId) {
		indicatorPermissionService.deleteByRoleAndIndicator(roleId);
		return this.getObject();
	}
	
}
