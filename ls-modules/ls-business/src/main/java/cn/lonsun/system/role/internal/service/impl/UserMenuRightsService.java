package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.rbac.indicator.entity.MenuEO;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IIndicatorPermissionService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.system.role.internal.dao.IUserMenuRightsDao;
import cn.lonsun.system.role.internal.entity.RbacUserMenuRightsEO;
import cn.lonsun.system.role.internal.service.IUserMenuRightsService;
import cn.lonsun.util.LoginPersonUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
@Service
public class UserMenuRightsService extends BaseService<RbacUserMenuRightsEO> implements IUserMenuRightsService {

    @Resource
    private IUserMenuRightsDao userMenuRightsDao;
    @Resource
    private IIndicatorPermissionService indicatorPermissionService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private IIndicatorService indicatorService;

    @Resource
    private IRoleService roleService;

    @Override
    public List<MenuEO> getMenu(Long userId) {
        Map<String, Boolean> checkedMap = getCheckedMenuMap(userId, LoginPersonUtil.getSiteId());
        //超管全部菜单
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Menu.toString());// 结果

        boolean isDevelopAmin = LoginPersonUtil.isRoot();// 开发商
        boolean isSuperAdmin = LoginPersonUtil.isSuperAdmin();// 超级管理员
        boolean isSiteAdmin = LoginPersonUtil.isSiteAdmin();
        // 所有用户都需要经过字段过滤，当用户为超级管理员或者root时不需要经过角色过滤
        if (isSuperAdmin) {
            // 删除超级管理员不可见的菜单
            for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext(); ) {
                IndicatorEO eo = it.next();
                if (isDevelopAmin && !eo.getIsShown4Developer() || !eo.getIsShown4Admin()) {
                    it.remove();
                }
            }
        } else if (isSiteAdmin) {// 站点管理员
            RoleEO roleEO = roleService.getRoleByCode(RoleEO.RoleCode.site_admin.toString());
            List<IndicatorPermissionEO> pckmenus = indicatorPermissionService.getByRoleId(roleEO.getRoleId());
            Map<Long, Boolean> pckmaps = new HashMap<Long, Boolean>();
            for (IndicatorPermissionEO pereo : pckmenus) {
                pckmaps.put(pereo.getIndicatorId(), true);
            }
            // 需要根据角色类型再过滤一次菜单
            for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext(); ) {
                IndicatorEO eo = it.next();
                if (!eo.getIsShown4SystemManager() || null == pckmaps.get(eo.getIndicatorId())) {
                    it.remove();
                }
            }
        }

        List<MenuEO> menuList = new ArrayList<MenuEO>();
        for (IndicatorEO eo : list) {
            if (null == eo) {
                continue;
            }
            MenuEO menu = new MenuEO();
            menu.setId(eo.getIndicatorId());
            menu.setpId(eo.getParentId());
            menu.setName(eo.getName());
            menu.setUri(eo.getUri());
            menu.setSortNum(eo.getSortNum());
            menu.setIsEnable(eo.getIsEnable() ? "1" : "0");
            if (null != checkedMap.get(String.valueOf(menu.getId())) && checkedMap.get(String.valueOf(menu.getId()))) {
                menu.setChecked(true);
            }
            menu.setMenuCode(eo.getCode());
            menuList.add(menu);
        }
        return menuList;
    }

    @Override
    public List<MenuEO> getCheckMenu(Long userId) {
        return getCheckMenu(userId, null);
    }

    @Override
    public List<MenuEO> getCheckMenu(Long userId, Long siteId) {
        Map<String, Boolean> checkedMap = getCheckedMenuMap(userId, siteId);
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Menu.toString());// 结果
        List<MenuEO> menuList = new ArrayList<MenuEO>();
        for (IndicatorEO eo : list) {
            if (null == eo) {
                continue;
            }
            if (null != checkedMap.get(String.valueOf(eo.getIndicatorId())) && checkedMap.get(String.valueOf(eo.getIndicatorId()))) {
                MenuEO menu = new MenuEO();
                menu.setId(eo.getIndicatorId());
                menu.setpId(eo.getParentId());
                menu.setName(eo.getName());
                menu.setUri(eo.getUri());
                menu.setSortNum(eo.getSortNum());
                menu.setIsEnable(eo.getIsEnable() ? "1" : "0");
                menu.setChecked(true);
                menu.setMenuCode(eo.getCode());
                menuList.add(menu);
            }
        }
        return menuList;
    }

    @Override
    public List<RbacUserMenuRightsEO> getCheckMenuRights(Long userId, Long siteId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("siteId", siteId);
        return this.getEntities(RbacUserMenuRightsEO.class, map);
    }

    @Override
    public void saveMenuRights(Long userId, String optRights) {
        JSONArray json = JSONArray.fromObject(optRights);
        List<RbacUserMenuRightsEO> menuRightsEOs = (List<RbacUserMenuRightsEO>) JSONArray.toCollection(json, RbacUserMenuRightsEO.class);
        userMenuRightsDao.deleteByUserId(userId);
        for (RbacUserMenuRightsEO eo : menuRightsEOs) {
            eo.setSiteId(LoginPersonUtil.getSiteId());
        }
        this.saveEntities(menuRightsEOs);
    }

    /**
     * 获取菜单
     *
     * @param userId
     * @return
     */
    private Map<String, Boolean> getCheckedMenuMap(Long userId, Long siteId) {
        Map<String, Boolean> rstMap = new HashMap<String, Boolean>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        if (null != siteId) {
            map.put("siteId", siteId);
        }
        List<RbacUserMenuRightsEO> list = this.getEntities(RbacUserMenuRightsEO.class, map);
        if (null != list && !list.isEmpty()) {
            for (RbacUserMenuRightsEO menu : list) {
                String key = String.valueOf(menu.getMenuId());
                if (!StringUtils.isEmpty(menu.getOptCode())) {
                    key += "_" + menu.getOptCode();
                }
                rstMap.put(key, true);
            }
        }
        return rstMap;
    }
}
