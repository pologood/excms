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

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.RoleAssignmentVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;


/**
 * TODO				<br/>
 *	 
 * @date     2014年8月26日 	<br/>
 * @author 	 yy	<br/>
 * @version	 v1.0 		<br/>
 */
public interface IRoleAssignmentDao extends IMockDao<RoleAssignmentEO> {
	
	/**
	 * 获取用户(userId)拥有管理菜单(indicatorId)的角色ID集合
	 *
	 * @param userId
	 * @param indicatorId
	 * @return
	 */
	public List<Long> getRoleIds(Long userId,Long indicatorId);
	
	/**
	 * 更新角色赋予关系中的organId
	 *
	 * @param srcOrganId
	 * @param userId
	 * @param newOrganId
	 */
	public void updateOrganId(Long srcOrganId,Long userId,Long newOrganId);
	
	/**
	 * 删除部门OrganId下的用户userId的角色赋予关系
	 *
	 * @param organId
	 * @param userId
	 */
	public void delete(Long organId,Long userId);
	
	public List<RoleAssignmentVO> getRoleAssignments(Long[] unitIds,Long[] roleIds);
	
	/**
	 * 获取单位和角色之间关系
	 * @param unitIds，与roleIds顺序一一对应
	 * @param roleIds，与unitIds顺序一一对应
	 * @return
	 */
	public List<RoleAssignmentEO> getAssignmentsByUnitIdAndRoleId(Long[] unitIds,Long[] roleIds);
	
	/**
	 * 根据角色类型和模糊的角色名称获取角色赋予关系
	 *
	 * @param types
	 * @param blurryName
	 * @return
	 */
	public List<RoleAssignmentEO> getRoleAssignments(String[] types,String blurryName);
	
	/**
	 * 获取用户(userId)的角色赋予关系
	 * @param userId
	 * @return
	 */
	public List<RoleAssignmentEO> getAssignments(Long userId);
	
	/**
	 * 获取用户(userId)在组织(organId)下的角色赋予关系
	 * @param organId
	 * @param userId
	 * @return
	 */
	public List<RoleAssignmentEO> getAssignments(Long organId,Long userId);
	
	/**
	 * 根据角色ID获取已绑定的用户ID集合
	 * @param roleId
	 * @return
	 */
	public List<RoleAssignmentEO> getRoleAssignments(Long roleId);

	/**
	 * 根据分页查询
	 * @param vo
	 * @param roleId
	 * @return
	 */
	public Pagination getPageRoleAssignments(PageQueryVO vo,Long roleId);

	/**
	 * 角色是否已经被赋予用户
	 * @param roleId
	 * @return
	 */
	public boolean hasAssignmented(Long roleId);
	
	/**
	 * 获取角色被单位用户使用的不重复记录-每条角色与单位用户使用只返回一条记录
	 *
	 * @param unitId
	 * @return
	 */
	public List<RoleAssignmentEO> getUnitAssignments(Long unitId);
	
	/**
	 * 获取单位范围在organIds内已使用公共角色的单位ID列表
	 *
	 * @param organIds
	 * @return
	 */
	public List<Long> getOrganIdsWhichIsAssignedRoles(List<Long> organIds);

	public List<RoleAssignmentEO> getAllassignments(List<?> persons);

}

