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

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.log.internal.service.ILogService;
import cn.lonsun.rbac.internal.dao.IRoleDao;
import cn.lonsun.rbac.internal.entity.*;
import cn.lonsun.rbac.internal.service.*;
import cn.lonsun.rbac.vo.RoleNodeVO;
import cn.lonsun.rbac.vo.RolePaginationQueryVO;
import cn.lonsun.type.internal.entity.BusinessTypeEO;
import cn.lonsun.type.internal.service.IBusinessTypeService;
import cn.lonsun.type.internal.vo.BusinessTypeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色服务类
 * 
 * @date 2014年8月26日
 * @author yy
 * @version v1.0
 */
@Service("roleService")
public class RoleServiceImpl extends MockService<RoleEO> implements IRoleService {
	/**
	 * 异常提示信息枚举
	 * 
	 * @author xujh
	 *
	 */
	@SuppressWarnings("unused")
	private enum TipsMessage {
		RoleAssignmented("请先解除角色绑定.");
		private String value;

		private TipsMessage(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	@Autowired
	private IRoleDao roleDao;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPersonService personService;
	@Autowired
	private IRoleAssignmentService roleAssignmentService;
	@Autowired
	private IBusinessTypeService businessTypeService;
	@Autowired
	private IIndicatorPermissionService indicatorPermissionService;
	@Autowired
	private IPermissionService permissionService;
	@Autowired
	private IOrganService organService;
	@Autowired
	private ILogService logService;
	@Autowired
	private IRoleRelationService roleRelationService;

	@Override
	public RoleEO getRoleByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		return getEntity(RoleEO.class, params);
	}

	@Override
	public List<RoleEO> getRoles(List<Long> roleIds, String[] types) {
		return roleDao.getRoles(roleIds, types);
	}

	@Override
	public List<RoleEO> getRoles4DeveloperAndSuperAdmin(boolean isDeveloper) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		params.put("isPredefine", 1);
		params.put("type", RoleEO.Type.System.toString());
		return getEntities(RoleEO.class, params);
	}

	@Override
	public List<PersonEO> getPersonsByUserIds(Long[] unitIds, Long[] userIds) {
		return personService.getPersonsByUserIds(unitIds, userIds);
	}

	@Override
	public void saveAssignments(RoleEO role, Long[] unitIds, Long[] userIds) {
		roleAssignmentService.save(role, unitIds, userIds);
	}

	@Override
	public void updateAssignments(RoleEO role, Long[] unitIds, Long[] userIds) {
		roleAssignmentService.update(role, unitIds, userIds);
	}

	@Override
	public List<PersonEO> getPersonsByRoleId(Long roleId) {
		return personService.getPersonsByRoleId(roleId);
	}

	@Override
	public List<RoleAssignmentEO> getAssignment(Long roleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", roleId);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		return roleAssignmentService.getEntities(RoleAssignmentEO.class, params);
	}

	@Override
	public Pagination getPagination(RolePaginationQueryVO query) {
		return roleDao.getPagination(query);
	}

	/**
	 * 根据角色ID数组获取角色列表
	 *
	 * @param roleIds
	 * @return
	 */
	public List<RoleEO> getRoles(List<Long> roleIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleId", roleIds);
		return getMockDao().getEntities(RoleEO.class, map);
	}

	@Override
	public List<RoleEO> getRoles(Long[] roleIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleId", roleIds);
		return getMockDao().getEntities(RoleEO.class, map);
	}

	@Override
	public boolean isCodeExisted(String code) {
		return roleDao.isCodeExisted(code);
	}

	@Override
	public boolean isNameExisted(String name, String type, Long organId) {
		if (null == StringUtils.trimToNull(name) || null == StringUtils.trimToNull(type) || (type.equals(RoleEO.Type.Private.toString()) && null == organId)) {
			return true;
		}
		return roleDao.isNameExisted(name, type, organId);
	}

	@Override
	public Long save(RoleEO role) {
		// 不可缺少的参数验证
		if (role == null || StringUtils.isEmpty(role.getName()) || StringUtils.isEmpty(role.getCode()) || StringUtils.isEmpty(role.getType())) {
			throw new IllegalArgumentException();
		}
		if (isCodeExisted(role.getCode())) {
			throw new BaseRunTimeException(TipsMode.Key.toString(), "RoleEO.CodeRepeated");
		}
		if (isNameExisted(role.getName(), role.getType(), role.getOrganId())) {
			throw new BaseRunTimeException(TipsMode.Key.toString(), "RoleEO.NameRepeated");
		}
		if (null != role.getOrganId() && role.getOrganId() > 0) {
			OrganEO organ = organService.getEntity(OrganEO.class, role.getOrganId());
			if (organ.getHasRoles() != 1) {
				organ.setHasRoles(1);
				organService.updateEntity(organ);
			}
		}
		return saveEntity(role);
	}

	@Override
	public Long save(RoleEO role, String rights, Long organId, Long userId, Long indicatorId) {
		Long roleId = save(role);
		indicatorPermissionService.saveRoleAndIndicator(roleId, rights);
		permissionService.savePermission(roleId, rights);
		// 获取用户拥有角色管理菜单权限的角色ID集合,lonsun_root无需处理
		if (userId != null && indicatorId != null) {
			List<Long> roleIds = roleAssignmentService.getRoleIds(userId, indicatorId);
			// 将用户添加的角色赋予拥有角色管理菜单的角色进行管理
			if (roleIds != null && roleIds.size() > 0) {
				// 新增角色关系
				List<RoleRelationEO> relations = new ArrayList<RoleRelationEO>();
				for (Long id : roleIds) {
					if (id == null) {
						continue;
					}
					RoleRelationEO relation = new RoleRelationEO();
					relation.setRoleId(id);
					relation.setTargetRoleId(roleId);
					relations.add(relation);
				}
				roleRelationService.saveEntities(relations);
			}
		}
		logService.saveLog("[系统管理]新增角色，名称：" + role.getName(), "RoleEO", "Add");
		return roleId;
	}

	@Override
	public void delete(Long roleId) throws BusinessException {
		List<RoleAssignmentEO> roleAssignmentEOs = roleAssignmentService.getRoleAssignments(roleId);
		if (null != roleAssignmentEOs && roleAssignmentEOs.size() > 0) {
			roleAssignmentService.delete(roleAssignmentEOs);
		}
		// 解除应用、菜单、按钮与角色的绑定
		List<IndicatorPermissionEO> indicatorPermissions = indicatorPermissionService.getByRoleId(roleId);
		if (null != indicatorPermissions && indicatorPermissions.size() > 0) {
			indicatorPermissionService.delete(indicatorPermissions);
		}
		// 解除资源与角色的绑定
		List<PermissionEO> permissions = permissionService.getPermissionByRole(roleId);
		if (null != permissions && permissions.size() > 0) {
			permissionService.delete(permissions);
		}
		// 解除角色之间的关系
		roleRelationService.deleteByRoleIdAndTargetRoleId(roleId);
		RoleEO r = getEntity(RoleEO.class, roleId);
		// 修改组织里hasRoles标记为0
		if (null != r.getOrganId() && r.getOrganId() > 0) {
			List<RoleEO> list = roleDao.getRoles(RoleEO.Type.Private.toString(), r.getOrganId());
			if (list.size() == 1) {
				OrganEO organ = organService.getEntity(OrganEO.class, r.getOrganId());
				if (organ.getHasRoles() == 1) {
					organ.setHasRoles(0);
					organService.updateEntity(organ);
				}
			}
		}
		delete(RoleEO.class, roleId);
		logService.saveLog("[系统管理]删除角色，名称：" + r.getName(), "RoleEO", "Delete");
	}

	@Override
	public void update(RoleEO role, String rights) throws BusinessException {
		// 不可缺少的参数验证
		if (role == null || StringUtils.isEmpty(role.getName()) || StringUtils.isEmpty(role.getType())) {
			throw new IllegalArgumentException();
		}
		RoleEO roleOld = roleDao.getEntity(RoleEO.class, role.getRoleId());
		if (!roleOld.getName().equals(role.getName())) {
			if (isNameExisted(role.getName(), role.getType(), role.getOrganId())) {
				throw new BusinessException(TipsMode.Key.toString(), "RoleEO.NameRepeated");
			}
		}
		if (!roleOld.getCode().equals(role.getCode())) {
			if (isCodeExisted(role.getCode())) {
				throw new BusinessException(TipsMode.Key.toString(), "RoleEO.CodeRepeated");
			}
		}
		AppUtil.copyProperties(roleOld, role);
		roleDao.update(roleOld);
		// 获取角色赋予关系列表，因为角色赋予关系记录中冗余了角色的一些字段，因此需要同步更新
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", role.getRoleId());
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		List<RoleAssignmentEO> assignments = roleAssignmentService.getEntities(RoleAssignmentEO.class, params);
		if (assignments != null && assignments.size() > 0) {
			for (RoleAssignmentEO assignment : assignments) {
				assignment.setRoleCode(role.getCode());
				assignment.setRoleName(role.getName());
				assignment.setRoleType(role.getType());
			}
			roleAssignmentService.updateEntities(assignments);
		}
		indicatorPermissionService.updateRoleAndIndicator(role.getRoleId(), rights);
		permissionService.updatePermission(role.getRoleId(), rights);
		logService.saveLog("[系统管理]更新角色，名称：" + role.getName(), "RoleEO", "Update");
	}

	@Override
	public List<RoleNodeVO> getRoleNodes(String type, Long organId) {
		List<RoleNodeVO> rns = new ArrayList<RoleNodeVO>();
		List<RoleEO> roles = roleDao.getRoles(type, organId);
		for (RoleEO roleEO : roles) {
			RoleNodeVO n = new RoleNodeVO();
			AppUtil.copyProperties(n, roleEO);
			n.setId(roleEO.getRoleId());
			n.setName(roleEO.getName());
			n.setIsParent(false);
			n.setPid(roleEO.getOrganId());
			rns.add(n);
		}
		return rns;
	}

	@Override
	public List<BusinessTypeEO> getBusinessTypes(String caseCode) {
		// 如果caseCode为空，那么设置默认为公共角色
		if (StringUtils.isEmpty(caseCode)) {
			caseCode = RoleEO.Type.Public.toString();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", BusinessTypeEO.Type.Role.toString());
		params.put("caseCode", caseCode);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		return businessTypeService.getEntities(BusinessTypeEO.class, params);
	}

	@Override
	public BusinessTypeEO getBusinessType(Long businessTypeId) {
		return businessTypeService.getEntity(BusinessTypeEO.class, businessTypeId);
	}

	@Override
	public List<RoleNodeVO> getRoleTreeNodes(String type) {
		List<RoleNodeVO> rns = new ArrayList<RoleNodeVO>();
		List<BusinessTypeVO> bts = businessTypeService.getTreeByTypeWithCaseCode(BusinessTypeEO.Type.Role.toString(), type);
		for (BusinessTypeVO bt : bts) {
			RoleNodeVO n = new RoleNodeVO();
			n.setId(bt.getBusinessTypeId());
			n.setName(bt.getName());
			n.setHasChildren(false);
			n.setIsParent(true);
			rns.add(n);
			List<RoleEO> roles = roleDao.getRoles(bt.getBusinessTypeId());
			if (null != roles && roles.size() > 0) {
				n.setIsParent(true);
				n.setHasChildren(true);
			}
			if (roles != null && roles.size() > 0) {
				for (RoleEO role : roles) {
					n = new RoleNodeVO();
					AppUtil.copyProperties(n, role);
					n.setId(role.getRoleId());
					n.setName(role.getName());
					n.setIsParent(false);
					n.setIsPredefine(role.getIsPredefine());
					n.setHasChildren(false);
					n.setPid(role.getBusinessTypeId());
					rns.add(n);
				}
			}
		}
		return rns;
	}

	@Override
	public List<RoleNodeVO> getRoleTreeNodesByScope(String scope) {
		List<RoleNodeVO> rns = new ArrayList<RoleNodeVO>();
		List<RoleEO> roles = roleDao.getRolesByScope(scope);
		if (roles != null && roles.size() > 0) {
			for (RoleEO role : roles) {
				RoleNodeVO n = new RoleNodeVO();
				AppUtil.copyProperties(n, role);
				n.setId(role.getRoleId());
				n.setName(role.getName());
				n.setIsParent(false);
				n.setIsPredefine(role.getIsPredefine());
				n.setHasChildren(false);
				n.setPid(role.getBusinessTypeId());
				rns.add(n);
			}
		}
		return rns;
	}

	@Override
	public List<RoleNodeVO> getRoleTreeNodesByScope(Long organId, Long userId, String scope) {
		//to do
			return null;
	}


	@Override
	public List<RoleNodeVO> getRoleTreeNodes(Long organId, Long userId, String type) {
		List<RoleNodeVO> rns = new ArrayList<RoleNodeVO>();
		List<BusinessTypeVO> bts = businessTypeService.getTreeByTypeWithCaseCode(BusinessTypeEO.Type.Role.toString(), type);
		for (BusinessTypeVO bt : bts) {
			RoleNodeVO n = new RoleNodeVO();
			n.setId(bt.getBusinessTypeId());
			n.setName(bt.getName());
			n.setHasChildren(false);
			n.setIsParent(true);
			rns.add(n);
		}
		// 获取用户拥有的类型为System的角色，只有System类型的角色才有关联管理功能
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("roleType", RoleEO.Type.System.toString());
		map.put("recordStatus", RecordStatus.Normal.toString());
		List<RoleAssignmentEO> ras = roleAssignmentService.getEntities(RoleAssignmentEO.class, map);
		if (ras != null && ras.size() > 0) {
			List<Long> roleIds = new ArrayList<Long>(ras.size());
			for (RoleAssignmentEO ra : ras) {
				roleIds.add(ra.getRoleId());
			}
			List<RoleEO> targetRoles = roleDao.getRolesByRoleRelation(roleIds, type);
			if (targetRoles != null && targetRoles.size() > 0) {
				for (RoleEO role : targetRoles) {
					roleIds.add(role.getRoleId());
					RoleNodeVO node = new RoleNodeVO();
					AppUtil.copyProperties(node, role);
					node.setId(role.getRoleId());
					node.setRoleId(role.getRoleId());
					node.setName(role.getName());
					node.setIsParent(false);
					node.setIsPredefine(role.getIsPredefine());
					node.setHasChildren(false);
					node.setPid(role.getBusinessTypeId());
					if (!rns.contains(node)) {
						rns.add(node);
					}
				}
			}
		}
		return rns;
	}

	@Override
	public RoleNodeVO getForOrganByRoleId(Long roleId) {
		RoleNodeVO roleNode = new RoleNodeVO();
		RoleEO role = getEntity(RoleEO.class, roleId);
		AppUtil.copyProperties(roleNode, role);
		roleNode.setId(role.getRoleId());
		roleNode.setName(role.getName());
		roleNode.setIsParent(false);
		roleNode.setPid(role.getOrganId());
		return roleNode;
	}

	@Override
	public RoleNodeVO getByRoleId(Long roleId) {
		RoleNodeVO roleNode = new RoleNodeVO();
		RoleEO role = getEntity(RoleEO.class, roleId);
		AppUtil.copyProperties(roleNode, role);
		roleNode.setId(role.getRoleId());
		roleNode.setName(role.getName());
		roleNode.setIsParent(false);
		roleNode.setPid(role.getBusinessTypeId());
		return roleNode;
	}

	@Override
	public List<RoleEO> getRoles(Long userId, String type) {
		if (userId == null) {
			throw new NullPointerException();
		}
		return roleDao.getRoles(userId, type);
	}

	@Override
	public List<RoleEO> getRoles(Long userId, String[] types) {
		if (userId == null) {
			throw new NullPointerException();
		}
		return roleDao.getRoles(userId, types);
	}

	@Override
	public List<RoleEO> getRoles(Long userId, Long organId, String[] types) {
		if (userId == null) {
			throw new NullPointerException();
		}
		return roleDao.getRoles(userId, organId, types);
	}

	@Override
	public List<Long> getIndicatorIds(Long roleId) {
		return indicatorPermissionService.getIndicatorIds(roleId);
	}

	@Override
	public List<RoleEO> getRoles(String roleCodePrefix) {
		return roleDao.getRoles(roleCodePrefix);
	}

	@Override
	public List<RoleEO> getUnitUsedRoles(Long unitId, Long[] roleIds) {
		return roleDao.getUnitUsedRoles(unitId, roleIds);
	}

	@Override
	public List<RoleEO> getRoles(String type, Long organId) {
		return roleDao.getRoles(type, organId);
	}

	@Override
	public List<RoleEO> getCurUserRoles(Long organId, Long userId) {
		return roleDao.getCurUserRoles(organId,userId);
	}

	@Override
	public List<RoleEO> getRolesBySiteId(Long siteId) {
		return roleDao.getRolesBySiteId(siteId);
	}

	@Override
	public List<RoleEO> getRolesBySiteId(Long siteId, Long organId) {
		return roleDao.getRolesBySiteId(siteId,organId);
	}

	@Override
	public List<RoleEO> getUserRoles(Long userId, Long organId) {
		List<RoleAssignmentEO> list = roleAssignmentService.getAssignments(organId, userId);
		List<RoleEO> roles = new ArrayList<RoleEO>();
//		Set<RoleEO> set = new HashSet<RoleEO>();
//
//		for(RoleAssignmentEO eo : list) {
//			RoleEO role = roleDao.getEntity(RoleEO.class,eo.getRoleId());
//			set.add(role);
//		}
//		roles.addAll(set);
		Map<Long,RoleAssignmentEO> map = new HashMap<Long, RoleAssignmentEO>();
		for(RoleAssignmentEO eo : list) {
			if(!map.containsKey(eo.getRoleId())){
				map.put(eo.getRoleId(), eo);
				RoleEO role = roleDao.getEntity(RoleEO.class,eo.getRoleId());
				roles.add(role);
			}
		}
		return roles;
	}

	@Override
	public Map<Long, List<RoleEO>> getRolesMap(List<?> persons) {
		Map<Long, List<RoleEO>> map = null;
		try{
			List<RoleAssignmentEO> list = roleAssignmentService.getAllassignments(persons);
			if(list != null && list.size() > 0){
				map = new HashMap<Long, List<RoleEO>>();
				List<RoleEO> roles = null;
				for(RoleAssignmentEO ra:list){
					RoleEO role = roleDao.getEntity(RoleEO.class,ra.getRoleId());
					Long userId = ra.getUserId();
					if(map.containsKey(userId)){
						roles = map.get(userId);
						roles.add(role);
						map.put(userId, roles);
					}else{
						roles = new ArrayList<RoleEO>();
						roles.add(role);
						map.put(userId, roles);
					}
				}
			}
		}catch(Exception e){}
		return map;
	}
}