package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.rbac.indicator.entity.MenuEO;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IIndicatorPermissionService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.system.role.internal.cache.MenuRightsCache;
import cn.lonsun.system.role.internal.cache.MenuUserHideCache;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;
import cn.lonsun.system.role.internal.service.IMenuRoleService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.TreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gu.fei
 * @version 2015-10-30 10:58
 */
@Service
public class MenuRoleService extends MockService<IndicatorEO> implements IMenuRoleService {

    @Resource
    private IIndicatorPermissionService indicatorPermissionService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private IIndicatorService indicatorService;
    
    @Resource
    private IRoleService roleService;

    private MenuSort menuSort = new MenuSort();

    public static class MenuSort implements Comparator<MenuEO>{
        @Override
        public int compare(MenuEO m1, MenuEO m2) {
            return m1.getSortNum().compareTo(m2.getSortNum());
        }
    }

    @Override
    public List<MenuEO> getMenu(boolean all, Long roleId) {
        List<Long> checkedList = null;// 角色选择权限列表
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Menu.toString());// 结果

        // 对应关系
        Map<Long, IndicatorEO> mapIEO = new HashMap<Long, IndicatorEO>();
        if (!AppUtil.isEmpty(list) && !list.isEmpty()) {
            for (IndicatorEO eo : list) {
                mapIEO.put(eo.getIndicatorId(), eo);
            }
        }
        boolean isDevelopAmin = LoginPersonUtil.isRoot();// 开发商
        boolean isSuperAdmin = LoginPersonUtil.isSuperAdmin();// 超级管理员
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();
        // 所有用户都需要经过字段过滤，当用户为超级管理员或者root时不需要经过角色过滤
        if (isDevelopAmin || isSuperAdmin) {// 只有当用户为开发商时，才会进入菜单管理，其他用户都需要进行权限过滤
            if (!all) {// 非查询所有
                // 删除开发商或者超级管理员不可见的菜单
                for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
                    IndicatorEO eo = it.next();
                    if ((isDevelopAmin && !eo.getIsShown4Developer()) || (isSuperAdmin && !eo.getIsShown4Admin())) {
                        it.remove();
                    }
                }
            }
            if (!AppUtil.isEmpty(roleId)) {// 角色选中菜单
                // 需要根据角色类型再过滤一次菜单
                RoleEO roleEO = roleService.getEntity(RoleEO.class, roleId);
                boolean siteRole = RoleEO.RoleCode.site_admin.toString().equals(roleEO.getCode());
                for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
                    IndicatorEO eo = it.next();
                    if ((siteRole && !eo.getIsShown4SystemManager())) {
                        it.remove();
                    }
                }
                // 处理权限选中菜单
                List<IndicatorPermissionEO> ps = indicatorPermissionService.getByRoleId(roleId);
                if (null != ps && !ps.isEmpty()) {
                    checkedList = new ArrayList<Long>();
                    for (IndicatorPermissionEO eo : ps) {
                        checkedList.add(eo.getIndicatorId());
                    }
                }
            }
        } else if(siteAdmin){// 站点管理员
            if (!AppUtil.isEmpty(roleId)) {// 角色选中菜单
                RoleEO roleEO = roleService.getRoleByCode(RoleEO.RoleCode.site_admin.toString());
                List<IndicatorPermissionEO> pckmenus = indicatorPermissionService.getByRoleId(roleEO.getRoleId());
                Map<Long,Boolean> pckmaps = new HashMap<Long, Boolean>();
                for(IndicatorPermissionEO pereo : pckmenus) {
                    pckmaps.put(pereo.getIndicatorId(),true);
                }
                // 需要根据角色类型再过滤一次菜单
                for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
                    IndicatorEO eo = it.next();
                    if (!eo.getIsShown4SystemManager() || null == pckmaps.get(eo.getIndicatorId())) {
                        it.remove();
                    }
                }
                // 处理权限选中菜单
                List<IndicatorPermissionEO> ps = indicatorPermissionService.getByRoleId(roleId);
                if (null != ps && !ps.isEmpty()) {
                    checkedList = new ArrayList<Long>();
                    for (IndicatorPermissionEO eo : ps) {
                        checkedList.add(eo.getIndicatorId());
                    }
                }

                removeUserHideMenu(list);
            }
        } else {
            if (!AppUtil.isEmpty(roleId)) {// 角色选中菜单
                List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(LoginPersonUtil.getOrganId(), LoginPersonUtil.getUserId());
                Set<IndicatorPermissionEO> pckmenus = new HashSet<IndicatorPermissionEO>();
                if(null != roles) {
                    for(RoleAssignmentEO eo : roles) {
                        if(eo.getRoleCode().contains(RoleEO.RoleCode.site_admin.toString())) {
                            continue;
                        }
                        Map<String,Object> param = new HashMap<String, Object>();
                        param.put("roleId",eo.getRoleId());
                        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                        //当前用户在当前站点下是站点管理员
                        param.put("siteId", LoginPersonUtil.getSiteId());
                        List<IndicatorPermissionEO> pckmenust = indicatorPermissionService.getEntities(IndicatorPermissionEO.class,param);
                        if(null != pckmenust) {
                            pckmenus.addAll(pckmenust);
                        }
                    }
                }
                Map<Long,Boolean> pckmaps = new HashMap<Long, Boolean>();
                for(IndicatorPermissionEO pereo : pckmenus) {
                    pckmaps.put(pereo.getIndicatorId(),true);
                }
                // 需要根据角色类型再过滤一次菜单
                for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
                    IndicatorEO eo = it.next();
                    if (!eo.getIsShown4ExternalUser() || null == pckmaps.get(eo.getIndicatorId())) {
                        it.remove();
                    }
                }
                // 处理权限选中菜单
                List<IndicatorPermissionEO> ps = indicatorPermissionService.getByRoleId(roleId);
                if (null != ps && !ps.isEmpty()) {
                    checkedList = new ArrayList<Long>();
                    for (IndicatorPermissionEO eo : ps) {
                        checkedList.add(eo.getIndicatorId());
                    }
                }
                removeUserHideMenu(list);
            }
        }

        return buildMenuList(list, checkedList,roleId);
    }

    @Override
    public List<MenuEO> getDeveloperMenu() {
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Menu.toString());// 结果

        // 需要根据角色类型再过滤一次菜单
        for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
            IndicatorEO eo = it.next();
            if (!eo.getIsShown4Developer()) {
                it.remove();
            }
        }
        // 删除开发商或者超级管理员不可见的菜单
        return TreeUtil.buildTree(buildMenuList(list));
    }

    @Override
    public List<MenuEO> getSuperAdminMenu() {
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Menu.toString());// 结果

        // 需要根据角色类型再过滤一次菜单
        for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
            IndicatorEO eo = it.next();
            if (!eo.getIsShown4Admin()) {
                it.remove();
            }
        }
        // 删除开发商或者超级管理员不可见的菜单
        return TreeUtil.buildTree(buildMenuList(list));
    }

    @Override
    public List<MenuEO> getSiteAdminMenu() {
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Menu.toString());// 结果
        List<MenuEO> menuList = new ArrayList<MenuEO>();
        RoleEO roleEO = roleService.getRoleByCode(RoleEO.RoleCode.site_admin.toString());
        List<IndicatorPermissionEO> pckmenus = indicatorPermissionService.getByRoleId(roleEO.getRoleId());
        Map<Long,Boolean> pckmaps = new HashMap<Long, Boolean>();
        for(IndicatorPermissionEO pereo : pckmenus) {
            pckmaps.put(pereo.getIndicatorId(),true);
        }
        // 需要根据角色类型再过滤一次菜单
        for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
            IndicatorEO eo = it.next();
            if (!eo.getIsShown4SystemManager() || null == pckmaps.get(eo.getIndicatorId())) {
                it.remove();
            }
        }
        removeUserHideMenu(list);
        // 删除开发商或者超级管理员不可见的菜单
        return TreeUtil.buildTree(buildMenuList(list, menuList));
    }

    /**
     * 获取普通用户菜单
     * @return
     */
    @Override
    public List<MenuEO> getUserMenu(Long userId, Long organId, Long siteId) {
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Menu.toString());// 结果
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        Set<IndicatorPermissionEO> pckmenus = new HashSet<IndicatorPermissionEO>();
        if(null != roles) {
            for(RoleAssignmentEO eo : roles) {
                if(eo.getRoleCode().contains(RoleEO.RoleCode.site_admin.toString())) {
                    continue;
                }
                Map<String,Object> param = new HashMap<String, Object>();
                param.put("roleId",eo.getRoleId());
                param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                //当前用户在当前站点下是站点管理员
//                param.put("siteId", siteId);
                List<IndicatorPermissionEO> pckmenust = indicatorPermissionService.getEntities(IndicatorPermissionEO.class,param);
                if(null != pckmenust) {
                    pckmenus.addAll(pckmenust);
                }
            }
        }
        Map<Long,Boolean> pckmaps = new HashMap<Long, Boolean>();
        for(IndicatorPermissionEO pereo : pckmenus) {
            pckmaps.put(pereo.getIndicatorId(),true);
        }
        // 需要根据角色类型再过滤一次菜单
        for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
            IndicatorEO eo = it.next();
            if (!eo.getIsShown4ExternalUser() || null == pckmaps.get(eo.getIndicatorId())) {
                it.remove();
            }
        }
        removeUserHideMenu(list);
        // 删除开发商或者超级管理员不可见的菜单
        return TreeUtil.buildTree(buildMenuList(list));
    }
    /**
     * 获取普通用户菜单
     * @return
     */
    @Override
    public List<MenuEO> getUserMenu() {
        return getUserMenu(LoginPersonUtil.getUserId(), LoginPersonUtil.getOrganId(), LoginPersonUtil.getSiteId());
    }


    private List<MenuEO> buildMenuList(List<IndicatorEO> list, List<MenuEO> menuList) {
        for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext();) {
            IndicatorEO eo = it.next();
            MenuEO menu = new MenuEO();
            menu.setId(eo.getIndicatorId());
            menu.setpId(eo.getParentId());
            menu.setName(eo.getName());
            menu.setUri(eo.getUri());
            menu.setSortNum(eo.getSortNum());
            menu.setIsEnable(eo.getIsEnable() ? "1" : "0");
            menu.setMenuCode(eo.getCode());
            List<IndicatorEO> opts = getCurUserHasRights(eo.getIndicatorId());// 按钮
            menu.setRights(opts);
            menuList.add(menu);
        }
        Collections.sort(menuList, menuSort);
        return menuList;
    }

    private List<MenuEO> buildMenuList(List<IndicatorEO> list) {
        return buildMenuList(list, new ArrayList<MenuEO>());
    }

    /**
     *
     * 构建菜单
     *
     * @author fangtinghua
     * @param list
     * @return
     */
    private List<MenuEO> buildMenuList(List<IndicatorEO> list, List<Long> checkedList, Long roleId) {
        if (null == list || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<MenuEO> menuList = new ArrayList<MenuEO>();
        for (IndicatorEO eo : list) {
            if(null == eo) {
                continue;
            }
            MenuEO menu = new MenuEO();
            menu.setId(eo.getIndicatorId());
            menu.setpId(eo.getParentId());
            menu.setName(eo.getName());
            menu.setUri(eo.getUri());
            menu.setSortNum(eo.getSortNum());
            menu.setIsEnable(eo.getIsEnable() ? "1" : "0");
            menu.setChecked(null == checkedList ? false : checkedList.contains(eo.getIndicatorId()));
            menu.setMenuCode(eo.getCode());
            List<IndicatorEO> opts = getCurUserHasRights(eo.getIndicatorId());// 按钮

            if (null != opts && !opts.isEmpty() && null != roleId) {
                Map<String,RbacMenuRightsEO> map = getCurRoleHasRights(roleId);
                for (Iterator<IndicatorEO> it = opts.iterator(); it.hasNext();) {
                    IndicatorEO indicatorEO = it.next();
                    RbacMenuRightsEO rightsEO = map.get(eo.getIndicatorId() + "_" + indicatorEO.getCode() + "_" + (LoginPersonUtil.isSuperAdmin()?"-1": LoginPersonUtil.getSiteId()));
                    if(!AppUtil.isEmpty(rightsEO)) {
                        indicatorEO.setChecked(true);
                    } else {
                        indicatorEO.setChecked(false);
                    }
                }
            }

            menu.setRights(opts);
            menuList.add(menu);

        }
        Collections.sort(menuList, menuSort);
        // 增加父子结构
        return TreeUtil.buildTree(menuList);
    }

    public List<IndicatorEO> getCurUserHasRights(Long menuId) {
        Long siteId = LoginPersonUtil.getSiteId();
        List<IndicatorEO> opts = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, menuId);
        boolean superAdmin = LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot();

        // 过滤查找出类型为按钮的对象
        if (null != opts && !opts.isEmpty()) {
            for (Iterator<IndicatorEO> it = opts.iterator(); it.hasNext();) {
                IndicatorEO indicatorEO = it.next();
                if (!IndicatorEO.Type.CMS_Button.toString().equals(indicatorEO.getType())) {
                    it.remove();
                }
            }
        } else {
            return null;
        }

        if(!superAdmin) {
            //普通管理员获取分配的角色权限
            Long organId = LoginPersonUtil.getOrganId();
            Long userId = LoginPersonUtil.getUserId();
            List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

            List<IndicatorEO> normal = new ArrayList<IndicatorEO>();

            for(IndicatorEO eo:opts) {
                for(RoleAssignmentEO role : roles) {
                    RbacMenuRightsEO rbacEO = MenuRightsCache.get(role.getRoleId(),menuId,eo.getCode(),(LoginPersonUtil.isSiteAdmin()?-1L:siteId));
                    if(null != rbacEO) {
                        normal.add(eo);
                    } else {
                        continue;
                    }
                }
            }

            return normal;
        }

        return opts;
    }

    /**
     * 获取当前角色拥有的菜单操作权限
     * @param roleId
     * @return
     */
    private Map<String,RbacMenuRightsEO> getCurRoleHasRights(Long roleId) {
        List<RbacMenuRightsEO> list = CacheHandler.getList(RbacMenuRightsEO.class, CacheGroup.CMS_ROLE_ID,roleId);
        Map<String,RbacMenuRightsEO> map = new HashMap<String, RbacMenuRightsEO>();
        if(null == list) {return map;}
        for(RbacMenuRightsEO eo:list) {
            String key = eo.getMenuId() + "_" + eo.getOptCode() + "_" + eo.getSiteId();
            map.put(key,eo);
        }

        return map;
    }


    @Override
    public boolean isMenuShow(String url) {
        boolean superAdmin = LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot();

        if(superAdmin) {
            return true;
        }

        Map<String,Object> params =new HashMap<String, Object>();
        params.put("uri",url);
        List<IndicatorEO> list = indicatorService.getEntities(IndicatorEO.class,params);

        Map<Long,Boolean> map = new HashMap<Long, Boolean>();
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        for(RoleAssignmentEO role : roles) {
            List<IndicatorPermissionEO> pss = indicatorPermissionService.getByRoleId(role.getRoleId());
            if (null != pss && !pss.isEmpty()) {
                for (IndicatorPermissionEO eo : pss) {
                    map.put(eo.getIndicatorId(), true);
                }
            }
        }

        IndicatorEO indicatorEO = (null != list && !list.isEmpty())?list.get(0):new IndicatorEO();
        return map.containsKey(indicatorEO.getIndicatorId());
    }

    /**
     * 删除针对当前用户隐藏的菜单
     * @param list
     */
    private void removeUserHideMenu(List<IndicatorEO> list) {
        //需要对当前用户隐藏的集合
        List<IndicatorEO> removeList = new ArrayList<IndicatorEO>();
        // 过滤菜单
        for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext(); ) {
            IndicatorEO eo = it.next();
            if(null != MenuUserHideCache.get(eo.getIndicatorId(), LoginPersonUtil.getOrganId(),LoginPersonUtil.getUserId())) {
                List<IndicatorEO> child = getChildList(list,eo.getIndicatorId(),new ArrayList<IndicatorEO>());
                removeList.add(eo);
                if(null != child) {
                    removeList.addAll(child);
                }
            }
        }
        list.removeAll(removeList);
    }

    //获取父id下的子集合
    private List<IndicatorEO> getChildList(List<IndicatorEO> list,Long pId,List<IndicatorEO> reList) {
        for (IndicatorEO indicatorEO : list) {
            if (null != indicatorEO.getParentId() && indicatorEO.getParentId().intValue() == pId.intValue()) {//查询下级菜单
                reList.add(indicatorEO);
                if (ifChilds(list, indicatorEO.getIndicatorId())) {
                    getChildList(list, indicatorEO.getIndicatorId(), reList);
                }
            }
        }
        return reList;
    }

    //判断是否存在子集
    private boolean ifChilds(List<IndicatorEO> list,Long pId) {
        boolean flag = false;
        for (IndicatorEO indicatorEO : list) {
            if (null != indicatorEO.getParentId() && indicatorEO.getParentId().intValue() == pId.intValue()) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
