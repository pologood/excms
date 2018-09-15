package cn.lonsun.system.role.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.rbac.indicator.entity.MenuEO;
import cn.lonsun.system.role.internal.entity.RbacUserMenuRightsEO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
public interface IUserMenuRightsService extends IBaseService<RbacUserMenuRightsEO> {

    /**
     * 获取用户的菜单权限
     * @param userId
     * @return
     */
    List<MenuEO> getMenu(Long userId);

    /**
     * 获取用户拥有的菜单权限
     * @param userId
     * @return
     */
    List<MenuEO> getCheckMenu(Long userId);

    /**
     * 获取指定站点下用户拥有的菜单权限
     * @param userId
     * @return
     */
    List<MenuEO> getCheckMenu(Long userId,Long siteId);

    /**
     * 获取指定站点下用户拥有的菜单权限
     * @param userId
     * @return
     */
    List<RbacUserMenuRightsEO> getCheckMenuRights(Long userId,Long siteId);

    /**
     * 保存菜单权限
     * @param userId
     * @param optRights
     */
    void saveMenuRights(Long userId,String optRights);
}
