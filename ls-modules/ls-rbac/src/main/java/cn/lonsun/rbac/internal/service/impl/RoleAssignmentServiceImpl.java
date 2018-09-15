/*
 * RoleServiceImpl.java         2014年8月26日 <br/>
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

package cn.lonsun.rbac.internal.service.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.RoleAssignmentVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.service.ILogService;
import cn.lonsun.rbac.internal.dao.IRoleAssignmentDao;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 角色绑定服务类
 * 
 * @author xujh
 * 
 */
@Service("roleAssignmentService")
public class RoleAssignmentServiceImpl extends MockService<RoleAssignmentEO>
		implements IRoleAssignmentService {

	@Autowired
	private IRoleAssignmentDao roleAssignmentDao;
	
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IOrganService organService;
	@Autowired
	private ILogService logService;


	/**
	 * 获取用户(userId)拥有管理菜单(indicatorId)的角色ID集合
	 *
	 * @param userId
	 * @param indicatorId
	 * @return
	 */
	public List<Long> getRoleIds(Long userId,Long indicatorId){
		return roleAssignmentDao.getRoleIds(userId, indicatorId);
	}
	
	/**
	 * 更新角色赋予关系中的organId
	 *
	 * @param srcOrganId
	 * @param userId
	 * @param newOrganId
	 */
	public void updateOrganId(Long srcOrganId,Long userId,Long newOrganId){
		if(srcOrganId==null||userId==null||newOrganId==null){
			throw new NullPointerException();
		}
		if(srcOrganId.longValue()!=newOrganId.longValue()){
			roleAssignmentDao.updateOrganId(srcOrganId, userId, newOrganId);
		}
	}
	@Override
	public void delete(Long organId,Long userId){
		roleAssignmentDao.delete(organId, userId);
	}
	
	public List<RoleAssignmentVO> getRoleAssignments(Long[] unitIds, Long[] roleIds){
		return roleAssignmentDao.getRoleAssignments(unitIds, roleIds);
	}
	
	@Override
	public List<RoleAssignmentEO> getRoleAssignments(String[] types, String blurryName){
		if(types==null||types.length<=0){
			//设置types默认为公用角色和单位拥有的角色
			types = new String[]{RoleEO.Type.Public.toString(), RoleEO.Type.Private.toString()};
		}
		//blurryName为改查询方法的核心，不允许为空
		if(StringUtils.isEmpty(blurryName)){
			throw new IllegalArgumentException();
		}
		return roleAssignmentDao.getRoleAssignments(types, blurryName);
	}
	
	@Override
	public void updateAssignments(Long organId,Long userId,List<Long> roleIds){
		List<RoleAssignmentEO> srcs = getAssignments(organId, userId);
		//删除所有的角色赋予关系
		if(roleIds==null||roleIds.size()<=0&&srcs!=null&&srcs.size()>0){
			delete(srcs);
			return;
		}
		if(srcs!=null&&srcs.size()>0){
			Iterator<RoleAssignmentEO>  iterator = srcs.iterator();
			while(iterator.hasNext()){
				RoleAssignmentEO ra = iterator.next();
				if(roleIds.contains(ra.getRoleId())){
					//去除待更新的与数据库中重复的内容，其余的需要进行删除
					RoleEO role = roleService.getEntity(RoleEO.class, ra.getRoleId());
					if(role != null && !role.getName().equals(ra.getRoleName())){
						ra.setRoleName(role.getName());
						updateEntity(ra);
					}
					iterator.remove();
					//去除已存在的,其余的需要新增到数据库中
					roleIds.remove(ra.getRoleId());
				}
			}
		}
		if(srcs!=null&&srcs.size()>0){
			delete(srcs);
		}
		if(roleIds!=null&&roleIds.size()>0){
			List<RoleAssignmentEO> ras = new ArrayList<RoleAssignmentEO>(roleIds.size());
			List<RoleEO> roles = roleService.getRoles(roleIds);
			if(roles!=null&&roles.size()>0){
				OrganEO unit = organService.getDirectlyUpLevelUnit(organId);
				for(RoleEO role:roles){
					RoleAssignmentEO roleAssignment = new RoleAssignmentEO();
					roleAssignment.setUnitId(unit.getOrganId());
					roleAssignment.setOrganId(organId);
					roleAssignment.setUserId(userId);
					roleAssignment.setRoleId(role.getRoleId());
					roleAssignment.setRoleType(role.getType());
					roleAssignment.setRoleName(role.getName());
					roleAssignment.setRoleCode(role.getCode());
					ras.add(roleAssignment);
				}
			}
			saveEntities(ras);
		}
	}
	
	private List<RoleAssignmentEO> getAssignments(Long organId, Long userId, String[] types){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("organId", organId);
		params.put("userId", userId);
		params.put("roleType", types);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		return getEntities(RoleAssignmentEO.class, params);
	}
	
	private RoleAssignmentEO getEO(Long unitId, Long organId, Long userId, RoleEO role){
		RoleAssignmentEO roleAssignment = new RoleAssignmentEO();
		roleAssignment.setUnitId(unitId);
		roleAssignment.setOrganId(organId);
		roleAssignment.setUserId(userId);
		roleAssignment.setRoleId(role.getRoleId());
		roleAssignment.setRoleType(role.getType());
		roleAssignment.setRoleName(role.getName());
		roleAssignment.setRoleCode(role.getCode());
		return roleAssignment;
	}
	
	@Override
	public void updateAssignments4UnitManager(Long organId,Long userId,List<Long> roleIds){
		//获取单位管理者有权限修改角色赋予关系的当前角色列表
		String[] types = new String[]{RoleEO.Type.Private.toString(), RoleEO.Type.Public.toString()};
		List<RoleEO> legalRoles = roleService.getRoles(roleIds, types);
		//获取用于现有的角色赋予关系列表-系统角色也需要排除在外
		List<RoleAssignmentEO> srcs = getAssignments(organId, userId,types);
		//1.新保存的赋予关系为空，原有的不为空，那么原有的都需要删除
		if(legalRoles==null&&srcs!=null){
			delete(srcs);
			return;
		}
		//2.都不为空,进行比较
		if(srcs!=null&&srcs.size()>0&&legalRoles!=null&&legalRoles.size()>0){
			//构建一个临时的Map存放合法的角色，key-roleId，方便后续的比对，不用for循环里套for循环去比对
			Map<Long, RoleEO> map = new HashMap<Long, RoleEO>(legalRoles.size());
			for(RoleEO role:legalRoles){
				map.put(role.getRoleId(), role);
			}
			Iterator<RoleAssignmentEO>  iterator = srcs.iterator();
			while(iterator.hasNext()){
				//现有的角色赋予关系与新的角色赋予比较
				Long roleId = iterator.next().getRoleId();
				RoleEO role = map.get(roleId);
				if(role==null){//表示原有的角色赋予关系和新的赋予关系都存在，那么可以忽略
					//从列表中移除，剩余的都是新的关系中没有的，那么我们需要删除这些新关系中没有的
					iterator.remove();
				}else{//移除后剩余的就是需要新增的
					legalRoles.remove(role);
				}
			}
		}
		//剩余那么新的全部添加
		if(legalRoles!=null&&legalRoles.size()>0){
			OrganEO unit = organService.getDirectlyUpLevelUnit(organId);
			List<RoleAssignmentEO> ras = new ArrayList<RoleAssignmentEO>();
			for(RoleEO role:legalRoles){
				ras.add(getEO(unit.getOrganId(), organId, userId, role));
			}
			saveEntities(ras);
		}
	}
	
	@Override
	public void update(RoleEO role, Long[] unitIds, Long[] userIds){
		if (role == null) {
			throw new NullPointerException();
		}
		if(unitIds==null||unitIds.length<=0|| userIds==null||userIds.length<=0){
			//获取已有的角色赋予关系
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("roleId", role.getRoleId());
			params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
			List<RoleAssignmentEO> dbAssignments = getEntities(RoleAssignmentEO.class, params);
			delete(dbAssignments);
		}else{
			List<PersonEO> ps = roleService.getPersonsByUserIds(unitIds, userIds);
			List<RoleAssignmentEO> assignments = new ArrayList<RoleAssignmentEO>(ps.size());
			for(PersonEO person:ps){
				RoleAssignmentEO roleAssignment = new RoleAssignmentEO();
				roleAssignment.setUnitId(person.getUnitId());
				roleAssignment.setOrganId(person.getOrganId());
				roleAssignment.setUserId(person.getUserId());
				roleAssignment.setRoleId(role.getRoleId());
				roleAssignment.setRoleType(role.getType());
				roleAssignment.setRoleName(role.getName());
				roleAssignment.setRoleCode(role.getCode());
				assignments.add(roleAssignment);
			}
			//获取已有的角色赋予关系
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("roleId", role.getRoleId());
			params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
			List<RoleAssignmentEO> dbAssignments = getEntities(RoleAssignmentEO.class, params);
			if(dbAssignments!=null&&dbAssignments.size()>0){
				//过滤出待新增、修改以及删除的角色赋予关系
				Iterator<RoleAssignmentEO> iterator = dbAssignments.iterator();
				while(iterator.hasNext()){
					boolean isContinue = false;
					RoleAssignmentEO dbAssignment = iterator.next();
					Iterator<RoleAssignmentEO> ti = assignments.iterator();
					while(ti.hasNext()){
						RoleAssignmentEO assignment = ti.next();
						//经过循环处理，dbAssignments中只剩下待删除的，assignments中只剩下需要新增的
						if(dbAssignment.equals(assignment)){
							iterator.remove();
							ti.remove();
							isContinue = true;
							break;
						}
					}
					if(isContinue){
						continue;
					}
				}
				delete(dbAssignments);
			}
			saveEntities(assignments);
		}
		
	}
	
	@Override
	public void save(RoleEO role, Long[] unitIds, Long[] userIds){
		if (role == null) {
			throw new NullPointerException();
		}
		if(unitIds==null||unitIds.length<=0|| userIds==null||userIds.length<=0){
			return;
		}
		List<PersonEO> ps = roleService.getPersonsByUserIds(unitIds, userIds);
		List<RoleAssignmentEO> assignments = new ArrayList<RoleAssignmentEO>(ps.size());
		for(PersonEO person:ps){
			RoleAssignmentEO roleAssignment = new RoleAssignmentEO();
			roleAssignment.setUnitId(person.getUnitId());
			roleAssignment.setOrganId(person.getOrganId());
			roleAssignment.setUserId(person.getUserId());
			roleAssignment.setRoleId(role.getRoleId());
			roleAssignment.setRoleType(role.getType());
			roleAssignment.setRoleName(role.getName());
			roleAssignment.setRoleCode(role.getCode());
			assignments.add(roleAssignment);
		}
		saveEntities(assignments);
	}
	@Override
	public void delete(RoleEO role, Long[] organIds, Long[] userIds) {
		if (role == null) {
			throw new NullPointerException();
		}
		if(organIds.length>0&&userIds.length>0){
			for(int i = 0;i<organIds.length;i++) {
				//获取已有的角色赋予关系
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("roleId", role.getRoleId());
				params.put("organId", organIds[i]);
				params.put("userId", userIds[i]);
				params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
				RoleAssignmentEO dbAssignments = getEntity(RoleAssignmentEO.class, params);
				delete(dbAssignments);
			}
		}
	}

	
	
	@Override
	public List<RoleAssignmentEO> getAssignments(Long organId, Long userId){
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("organId", organId);
//		params.put("userId", userId);
//		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
//		return getEntities(RoleAssignmentEO.class, params);
		return roleAssignmentDao.getAssignments(organId,userId);
	}

	@Override
	public List<RoleAssignmentEO> getRoleAssignments(Long roleId) {
		if (roleId == null || roleId <= 0) {
			throw new NullPointerException();
		}
		return roleAssignmentDao.getRoleAssignments(roleId);
	}

	@Override
	public Pagination getPageRoleAssignments(PageQueryVO vo, Long roleId) {
		return roleAssignmentDao.getPageRoleAssignments(vo,roleId);
	}

	@Override
	public boolean hasAssignmented(Long roleId) {
		if (roleId == null || roleId <= 0) {
			throw new NullPointerException();
		}
		return roleAssignmentDao.hasAssignmented(roleId);
	}


	@Override
	public void save(Long organId,Long userId, Long[] roleIds) {
		if(organId==null||userId==null||roleIds==null){
			return;
		}
		List<RoleAssignmentEO> assignments = new ArrayList<RoleAssignmentEO>(roleIds.length);
		List<RoleEO> roles = roleService.getRoles(roleIds);
		if(roles!=null&&roles.size()>0){
			OrganEO unit = organService.getDirectlyUpLevelUnit(organId);
			for(RoleEO role:roles){
				RoleAssignmentEO roleAssignment = new RoleAssignmentEO();
				roleAssignment.setUnitId(unit.getOrganId());
				roleAssignment.setOrganId(organId);
				roleAssignment.setUserId(userId);
				roleAssignment.setRoleId(role.getRoleId());
				roleAssignment.setRoleName(role.getName());
				roleAssignment.setRoleCode(role.getCode());
				roleAssignment.setRoleType(role.getType());
				assignments.add(roleAssignment);
			}
		}
		saveEntities(assignments);
	}

	@Override
	public List<RoleAssignmentEO> getAssignments(Long userId) {
		return roleAssignmentDao.getAssignments(userId);
	}

	@Override
	public void deleteAssignments(Long userId) {
		if(userId==null){
			throw new NullPointerException();
		}
		delete(getAssignments(userId));
	}

	@Override
	public List<RoleAssignmentEO> getUnitAssignments(Long unitId) {
		return roleAssignmentDao.getUnitAssignments(unitId);
	}

	@Override
	public List<Long> getOrganIdsWhichIsAssignedRoles(List<Long> organIds) {
		return roleAssignmentDao.getOrganIdsWhichIsAssignedRoles(organIds);
	}

	@Override
	public List<RoleAssignmentEO> getAssignmentsByUnitIdAndRoleId(Long[] unitIds, Long[] roleIds) {
		return roleAssignmentDao.getAssignmentsByUnitIdAndRoleId(unitIds,roleIds);
	}

	@Override
	public List<RoleAssignmentEO> getAllassignments(List<?> persons) {
		return roleAssignmentDao.getAllassignments(persons);
	}

	public IRoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(IRoleService roleService) {
		this.roleService = roleService;
	}
}
