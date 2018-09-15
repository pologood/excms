package cn.lonsun.rbac.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.rbac.internal.entity.PermissionEO;

public interface IPermissionDao extends IMockDao<PermissionEO> {
	
	
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
     * @return
     */
    public List<PermissionEO> getPermissionByRole(Long roleId);

    /**
     * 根据roleId和resourceId查询
     *
     * @author yy
     * @param roleId
     * @param redourceId
     * @return
     */
    public PermissionEO getByRoleAndResource(Long roleId, Long resourceId);
	

}
