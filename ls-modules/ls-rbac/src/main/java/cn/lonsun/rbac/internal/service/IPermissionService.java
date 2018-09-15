package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.rbac.internal.entity.PermissionEO;

public interface IPermissionService extends IMockService<PermissionEO> {
	
	/**
	 * 验证角色(roleIds)是否有访问资源(uri)的权限
	 * @param roleIds
	 * @param uri
	 * @return
	 */
	public boolean hasPermission(List<Long> roleIds,String uri);

    /**
     * 查询资源权限
     *
     * @author yy
     * @param roleId
     */
    public List<PermissionEO> getPermissionByRole(Long roleId);
    
    /**
     * 保存资源权限
     *
     * @author yy
     * @param roleId
     * @param rights
     */
    public void savePermission(Long roleId, String rights);

    /**
     * 修改资源权限
     *
     * @author yy
     * @param roleId
     * @param rights
     */
    public void updatePermission(Long roleId, String rights);


}
