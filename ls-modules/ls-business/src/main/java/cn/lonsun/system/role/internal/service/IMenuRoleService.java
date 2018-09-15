package cn.lonsun.system.role.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.indicator.entity.MenuEO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-10-30 10:58
 */
public interface IMenuRoleService extends IMockService<IndicatorEO>  {

    /**
     * 根据角色获取菜单
     * @param all
     * @param roleId
     * @return
     */
    public List<MenuEO> getMenu(boolean all, Long roleId);

    /**
     * 获取lonsun_root账号的菜单
     * @return
     */
    public List<MenuEO> getDeveloperMenu();

    /**
     * 获取超管账号菜单
     * @return
     */
    public List<MenuEO> getSuperAdminMenu();

    /**
     * 获取站管菜单
     * @return
     */
    public List<MenuEO> getSiteAdminMenu();


    /**
     * 获取普通用户的菜单
     * @param userId
     * @param organId
     * @param siteId
     * @return
     */
    public List<MenuEO> getUserMenu(Long userId, Long organId, Long siteId);

    /**
     * 获取当前登录的普通用户菜单
     * @return
     */
    public List<MenuEO> getUserMenu();

    /**
     * 根据url判断菜单权限
     * @param url
     * @return
     */
    public boolean isMenuShow(String url);

    /**
     * 根据menuId获取菜单权限
     * @param menuId
     * @return
     */
    public List<IndicatorEO> getCurUserHasRights(Long menuId);
}
