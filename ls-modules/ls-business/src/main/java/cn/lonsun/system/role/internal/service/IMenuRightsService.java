package cn.lonsun.system.role.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
public interface IMenuRightsService extends IBaseService<RbacMenuRightsEO> {

    /**
     * 根据角色获取菜单权限
     * @param roleIds
     * @return
     */
    public List<RbacMenuRightsEO> getEOsByRoleIds(Long[] roleIds);

    /**
     * 删除角色的菜单权限
     * @param roleId
     */
    public void delByRoleId(Long roleId);

    /**
     * 删除角色的菜单权限
     * @param roleId
     */
    public void delByRoleIdAndSiteId(Long roleId,Long siteId);
}
