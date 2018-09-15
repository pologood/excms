package cn.lonsun.rbac.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.vo.RoleNodeVO;
import cn.lonsun.rbac.vo.RolePaginationQueryVO;
import cn.lonsun.type.internal.entity.BusinessTypeEO;

import java.util.List;
import java.util.Map;

/**
 * 角色服务类
 * 
 * @author xujh
 *
 */
public interface IRoleService extends IMockService<RoleEO> {

    /**
     * 获取角色ID范围在roleIds内，并且角色类型在types内的角色集合
     *
     * @param roleIds
     * @param types
     * @return
     */
    public List<RoleEO> getRoles(List<Long> roleIds, String[] types);

    /**
     * 获取开发商拥有的角色
     *
     * @return
     */
    public List<RoleEO> getRoles4DeveloperAndSuperAdmin(boolean isDeveloper);

    /**
     * 获取person集合,组织ID需要和用户ID在顺序上匹配
     * 
     * @param organIds
     * @param userIds
     * @return
     */
    public List<PersonEO> getPersonsByUserIds(Long[] organIds, Long[] userIds);

    /**
     * 添加角色与人员的赋予关系
     *
     * @param role
     * @param unitIds
     * @param userIds
     */
    public void saveAssignments(RoleEO role, Long[] unitIds, Long[] userIds);

    /**
     * 更新角色与人员的赋予关系，包括被删除的和新增的两部分
     *
     * @param role
     * @param unitIds
     * @param userIds
     */
    public void updateAssignments(RoleEO role, Long[] unitIds, Long[] userIds);

    /**
     * 获取角色赋予关系列表
     *
     * @param roleId
     * @return
     */
    public List<RoleAssignmentEO> getAssignment(Long roleId);

    /**
     * 根据角色Id获取对应的人员信息
     *
     * @param roleId
     * @return
     */
    public List<PersonEO> getPersonsByRoleId(Long roleId);

    /**
     * 获取角色类型下的分类集合
     *
     * @param caseCode
     * @return
     */
    public List<BusinessTypeEO> getBusinessTypes(String caseCode);

    /**
     * 根据主键获取对象
     *
     * @param businessTypeId
     * @return
     */
    public BusinessTypeEO getBusinessType(Long businessTypeId);

    /**
     * 获取角色分页列表
     *
     * @param query
     * @return
     */
    Pagination getPagination(RolePaginationQueryVO query);

    /**
     * 根据角色编码获取角色
     *
     * @param code
     * @return
     */
    public RoleEO getRoleByCode(String code);

    /**
     * 根据角色编码前缀获取角色列表
     *
     * @param roleCodePrefix
     * @return
     */
    public List<RoleEO> getRoles(String roleCodePrefix);

    /**
     * 根据角色ID数组获取角色列表
     * 
     * @param roleIds
     * @return
     */
    public List<RoleEO> getRoles(List<Long> roleIds);

    /**
     * 根据角色ID数组获取角色列表
     * 
     * @param roleIds
     * @return
     */
    public List<RoleEO> getRoles(Long[] roleIds);

    /**
     * 获取用户userId下角色类型为type的所有角色
     * 
     * @param userId
     * @param type
     * @return
     */
    public List<RoleEO> getRoles(Long userId, String type);

    /**
     * 获取用户单位下角色类型为type的所有角色
     * 
     * @param type
     * @param organId
     * @return
     */
    public List<RoleEO> getRoles(String type, Long organId);

    /**
     * 获取用户userId下角色类型为type的所有角色
     * 
     * @param userId
     * @param types
     * @return
     */
    public List<RoleEO> getRoles(Long userId, String[] types);

    /**
     * 获取用户userId在单位organId中的角色类型在types中的所有角色
     * 
     * @param userId
     * @param organId
     * @param types
     * @return
     */
    public List<RoleEO> getRoles(Long userId, Long organId, String[] types);

    /**
     * 验证角色编码code是否已存在
     * 
     * @param code
     * @return
     */
    public boolean isCodeExisted(String code);

    /**
     * 验证角色名称name是否已存在</br> 1.如果角色类型为System或Public,那么验证同类型的角色名称是否存在重复;</br>
     * 2.如果角色类型为Private,那么验证同组织或单位的角色名称是否重复.
     * 
     * @param name
     *            角色名称
     * @param type
     *            角色类型
     * @param organId
     *            所属部门
     * @return
     */
    public boolean isNameExisted(String name, String type, Long organId);

    /**
     * 保存角色</br> 角色名称和编码不允许重复
     * 
     * @param role
     */
    public Long save(RoleEO role) throws BusinessException;

    /**
     * 添加角色，同时为为角色添加权限
     * 
     * @param role
     * @param rights
     * @param organId
     *            当前用户单位ID
     * @param userId
     *            当前用户ID
     * @param indicatorId
     */
    public Long save(RoleEO role, String rights, Long organId, Long userId, Long indicatorId);

    /**
     * 删除角色</br> 1.如果角色已经和资源绑定，那么抛出InUsingException</br>
     * 2.如果角色已经和用户绑定，那么抛出InUsingException</br>
     * 
     * @param roleId
     */
    public void delete(Long roleId) throws BusinessException;

    /**
     * 更新角色，角色编码和名称不允许重复
     * 
     * @param role
     */
    public void update(RoleEO role, String rights) throws BusinessException;

    /**
     * 根据角色类型和组织ID获取角色列表
     * 
     * @param type
     * @param organId
     * @return
     */
    public List<RoleNodeVO> getRoleNodes(String type, Long organId);

    /**
     * 根据类型查询公共、系统角色
     * 
     * @param type
     * @return
     */
    public List<RoleNodeVO> getRoleTreeNodes(String type);

    /**
     * 根据范围查询角色
     * 
     * @param scope
     * @return
     */
    public List<RoleNodeVO> getRoleTreeNodesByScope(String scope);

    /**
     * 获取部门organId下用户userId拥有的范围为scope的角色
     * 
     * @param organId
     * @param userId
     * @param scope
     * @return
     */
    public List<RoleNodeVO> getRoleTreeNodesByScope(Long organId, Long userId, String scope);

    /**
     * 获取部门organId下用户userId拥有的类型为type的角色
     * 
     * @param organId
     * @param userId
     * @param type
     * @return
     */
    public List<RoleNodeVO> getRoleTreeNodes(Long organId, Long userId, String type);

    /**
     * 查询组织下Role
     *
     * @author yy
     * @param roleId
     * @return
     */
    public RoleNodeVO getForOrganByRoleId(Long roleId);

    /**
     * 查询公共和系统Role
     *
     * @author yy
     * @param roleId
     * @return
     */
    public RoleNodeVO getByRoleId(Long roleId);

    /**
     * 根据角色ID获取所有的正常使用的IndicatorId集合
     * 
     * @param roleId
     * @return
     */
    public List<Long> getIndicatorIds(Long roleId);

    /**
     * 获取单位unitId使用的角色列表,roleIds-返回角色的范围
     *
     * @param unitId
     * @param roleIds
     * @return
     */
    public List<RoleEO> getUnitUsedRoles(Long unitId, Long[] roleIds);

    /*
    * 获取用户创建的角色
    * */
    public List<RoleEO> getCurUserRoles(Long organId, Long userId);

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


    /**
     * 获取当前用户所拥有的角色
     * @return
     */
    public List<RoleEO> getUserRoles(Long userId,Long organId);

	public Map<Long, List<RoleEO>> getRolesMap(List<?> data);
}
