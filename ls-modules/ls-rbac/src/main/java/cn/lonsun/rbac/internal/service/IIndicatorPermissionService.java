package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;

public interface IIndicatorPermissionService extends IMockService<IndicatorPermissionEO> {
	
	/**
	 * 验证用户是否有访问菜单parentId下编码为code的权限或按钮的权限
	 *
	 * @param organId
	 * @param userId
	 * @param parentId
	 * @param code
	 * @return
	 */
	public boolean hasPermission(Long organId,Long userId,Long parentId,String code);

    /**
     * 根据指示器ID查询权限
     *
     * @author yy
     * @param indecatorId
     * @return
     */
    public List<IndicatorPermissionEO> getByIndicatorId(Long indicatorId);

    /**
     * 删除角色的权限
     *
     * @author yy
     * @param roleId
     */
    public void deleteByRoleAndIndicator(Long roleId);
    
    /**
     * 保存权限
     *
     * @author yy
     * @param roleId
     * @param rights
     */
    public void saveRoleAndIndicator(Long roleId, String rights);
    
    /**
     * 根据角色ID获取所有的正常使用的IndicatorId集合
     * @param roleId
     * @return
     */
    public List<Long> getIndicatorIds(Long roleId);
    
    /**
     * 跟据roleId查询
     *
     * @author yy
     * @param roleId
     * @return
     */
    public List<IndicatorPermissionEO> getByRoleId(Long roleId);

    /**
     * 跟据roleId查询
     *
     * @author yy
     * @param roleId
     * @return
     */
    public List<IndicatorPermissionEO> getByRoleIds(Long[] roleId);

    /**
     * 更新权限
     *
     * @author yy
     * @param roleId
     * @param rights
     */
    public void updateRoleAndIndicator(Long roleId, String rights);
	
}
