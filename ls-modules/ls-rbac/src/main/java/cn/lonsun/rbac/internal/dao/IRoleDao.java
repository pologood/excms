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
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.vo.RolePaginationQueryVO;


/**
 * 角色管理ORM接口
 *
 * @author xujh
 * @version 1.0
 * 2015年1月7日
 *
 */
public interface IRoleDao extends IMockDao<RoleEO> {
	
	/**
	 * 根据角色关联关系中主角色ID为roleId获取角色类型为type的角色集合
	 *
	 * @param roleId
	 * @param type
	 * @return
	 */
	public List<RoleEO> getRolesByRoleRelation(List<Long> roleIds,String type);
	
	/**
	 * 获取用户(userId)拥有类型为businessTypeId的角色集合
	 *
	 * @param organId
	 * @param userId
	 * @param businessTypeId
	 * @return
	 */
	public List<RoleEO> getRoles(Long organId,Long userId,Long businessTypeId);
	
	/**
	 * 获取类型为businessTypeId的角色集合
	 *
	 * @param businessTypeId
	 * @return
	 */
	public List<RoleEO> getRoles(Long businessTypeId);
	
	/**
     * 获取范围为scope的角色集合
     *
     * @param businessTypeId
     * @return
     */
    public List<RoleEO> getRolesByScope(String scope);

	/**
	 *
	 * @return
	 */
	public List<RoleEO> getRoles(Long organId,Long userId,String scope);
	
	/**
	 * 获取角色ID范围在roleIds内，并且角色类型在types内的角色集合
	 *
	 * @param roleIds
	 * @param types
	 * @return
	 */
	public List<RoleEO> getRoles(List<Long> roleIds,String[] types);
	
	/**
	 * 获取角色分页列表
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPagination(RolePaginationQueryVO query);
	/**
	 * 根据角色编码前缀获取角色列表
	 *
	 * @param roleCodePrefix
	 * @return
	 */
	public List<RoleEO> getRoles(String roleCodePrefix);
	
	/**
	 * 获取用户userId下角色类型为type的所有角色
	 * @param userId
	 * @param type
	 * @return
	 */
	public List<RoleEO> getRoles(Long userId,String type);
	
	/**
	 * 获取用户userId下角色类型为type的所有角色
	 * @param userId
	 * @param type
	 * @return
	 */
	public List<RoleEO> getRoles(Long userId,String[] types);
	
	/**
	 * 获取用户userId在单位organId中的角色类型在types中的所有角色
	 * @param userId
	 * @param organId
	 * @param types
	 * @return
	 */
	public List<RoleEO> getRoles(Long userId,Long organId,String[] types);
	

    /**
     * 判断角色编码是否存在
     *
     * @author yy
     * @param code
     * @return
     */
    public boolean isCodeExisted(String code);

    /**
     * 判断角色名称是否存在
     *
     * @author yy
     * @param name
     * @param type
     * @param organId
     * @return
     */
    public boolean isNameExisted(String name, String type, Long organId);

    /**
     * 根据角色类型和组织ID查询角色信息
     *
     * @author yy
     * @param type
     * @param organId
     * @return
     */
    public List<RoleEO> getRoles(String type, Long organId);

    /**
     * 根据businessId查询
     *
     * @author yy
     * @param businessTypeId
     * @return
     */
    public List<RoleEO> getByBusinessId(Long businessTypeId);
    
    /**
     * 获取单位unitId使用的角色列表
     *
     * @param unitId
     * @return
     */
    public List<RoleEO> getUnitUsedRoles(Long unitId,Long[] roleIds);


	/**
	 * 获取当前用户创建的角色
	 *
	 * @return
	 */
	public List<RoleEO> getCurUserRoles(Long organId,Long userId);

	/**
	 * 根据站点ID查询角色
	 * @param siteId
	 * @return
	 */
	public List<RoleEO> getRolesBySiteId(Long siteId);

	/**
	 * 根据站点ID查询角色
	 * @param siteId
	 * @return
	 */
	public List<RoleEO> getRolesBySiteId(Long siteId,Long organId);
}

