package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.RoleAssignmentVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;

/**
 * 角色绑定服务类
 * @author xujh
 *
 */
public interface IRoleAssignmentService extends IMockService<RoleAssignmentEO> {
	
	/**
	 * 获取用户(userId)拥有管理菜单(indicatorId)的角色ID集合
	 *
	 * @param userId
	 * @param indicatorId
	 * @return
	 */
	public List<Long> getRoleIds(Long userId,Long indicatorId);
	
	/**
	 * 删除部门OrganId下的用户userId的角色赋予关系
	 *
	 * @param organId
	 * @param userId
	 */
	public void delete(Long organId,Long userId);
	/**
	 * 获取RoleAssignmentVO
	 *
	 * @param unitIds
	 * @param roleIds
	 * @return
	 */
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
	 * @param types  传递空的对象时，取默认类型-Public和Private类型
	 * @param blurryName 不允许为空
	 * @return
	 */
	public List<RoleAssignmentEO> getRoleAssignments(String[] types,String blurryName);
	
	/**
	 * 更新用户(userId)在组织(organId)下的角色赋予关系
	 * @param organId
	 * @param userId
	 * @param roleIds
	 */
	public void updateAssignments(Long organId,Long userId,List<Long> roleIds);
	
	/**
	 * 单位管理员更新用户角色赋予关系，其中用户的System类型的角色不允许管理员更新
	 *
	 * @param organId
	 * @param userId
	 * @param roleIds
	 */
	public void updateAssignments4UnitManager(Long organId,Long userId,List<Long> roleIds);
	
	/**
	 * 获取用户(userId)在组织(organId)下的角色赋予关系
	 * @param organId
	 * @param userId
	 * @return
	 */
	public List<RoleAssignmentEO> getAssignments(Long organId,Long userId);
	
	/**
	 * 更新角色赋予关系中的organId
	 *
	 * @param srcOrganId
	 * @param userId
	 * @param newOrganId
	 */
	public void updateOrganId(Long srcOrganId,Long userId,Long newOrganId);
	
	/**
	 * 获取用户(userId)的角色赋予关系
	 * @param userId
	 * @return
	 */
	public List<RoleAssignmentEO> getAssignments(Long userId);
	
	/**
	 * 删除用户（userId）的所有角色
	 * @param userId
	 * @return
	 */
	public void deleteAssignments(Long userId);
	
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
	 * 为用户绑定角色
	 * @param organId
	 * @param userId
	 * @param roleIds
	 */
	public void save(Long organId,Long userId,Long[] roleIds);
	
	
	/**
	 * 添加角色与人员的赋予关系
	 *
	 * @param role
	 * @param unitIds
	 * @param userIds
	 */
	public void save(RoleEO role,Long[] unitIds,Long[] userIds);
	
	/**
	 * 更新角色与人员的赋予关系，包括被删除的和新增的两部分
	 *
	 * @param role
	 * @param persons
	 */
	public void update(RoleEO role,Long[] unitIds,Long[] userIds);

	/**
	 * 删除角色与人员的赋予关系
	 *
	 * @param role
	 * @param persons
	 */
	public void delete(RoleEO role,Long[] organId,Long[] userIds);
	
	/**
	 * 获取单位下已使用的角色列表
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
