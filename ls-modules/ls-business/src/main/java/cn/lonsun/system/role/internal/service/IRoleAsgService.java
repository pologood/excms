package cn.lonsun.system.role.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
public interface IRoleAsgService extends IMockService<RoleAssignmentEO> {

    /**
     * 获取角色下用户
     * @param roleId
     * @return
     */
    public Object getEOs(PageQueryVO vo,Long roleId);

    /**
     * 获取用户管理的站点id
     * @param organId
     * @param userId
     * @return
     */
    public Long[] getCurSiteIds(Long organId,Long userId);

    /**
     * 确认用户所属角色
     * @param roleCode
     * @param organId
     * @param userId
     * @return
     */
    public boolean confirmRole(String roleCode,Long organId,Long userId);

    /**
     * 用户添加角色
     * @param organId
     * @param userId
     * @param roleIds
     */
    public void addUserRole(Long organId, Long userId, List<Long> roleIds);

    /**
     * 添加用户
     * @param role
     * @param unitIds
     * @param userIds
     */
    public void addAsgEO(RoleEO role, Long[] unitIds, Long[] userIds);

    /**
     * 删除用户的角色
     * @param roleId
     * @param userIds
     * @param organIds
     */
    public void delAsgEO(Long roleId, String userIds, String organIds);

    /**
     * 移除用户所有的角色关系
     * @param organId
     * @param userId
     * @return
     */
    public void removeUserRoles(Long organId,Long userId);
}
