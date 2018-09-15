/*
 * IndicatorServiceImpl.java         2015年8月25日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.rbac.indicator.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.indicator.entity.MenuEO;
import cn.lonsun.rbac.indicator.service.IIndicatorService;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IIndicatorPermissionService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.system.role.internal.cache.MenuUserHideCache;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;
import cn.lonsun.system.role.internal.entity.RbacUserMenuRightsEO;
import cn.lonsun.system.role.internal.service.IMenuRoleService;
import cn.lonsun.system.role.internal.service.IMenuUserHideService;
import cn.lonsun.system.role.internal.service.IRoleAsgService;
import cn.lonsun.system.role.internal.service.IUserMenuRightsService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import cn.lonsun.util.TreeUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月25日 <br/>
 */
@Service("ex_8_IndicatorServiceImpl")
public class IndicatorServiceImpl extends MockService<IndicatorEO> implements IIndicatorService {

    @Resource
    private cn.lonsun.indicator.internal.service.IIndicatorService indicatorService;

    @Resource
    private IIndicatorPermissionService indicatorPermissionService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Resource
    private IRoleAsgService roleAsgService;

    @Resource
    private IMenuRoleService menuRoleService;

    @Resource
    private IRoleService roleService;

    @Resource
    private IMenuUserHideService menuUserHideService;

    @Resource
    private IUserMenuRightsService userMenuRightsService;

    @Override
    public void save(IndicatorEO indicator) {
        Long indicatorId = indicator.getIndicatorId();
        if (null == indicatorId) {
            indicatorService.save(indicator);
            /*if (IndicatorEO.Type.CMS_Site.toString().equals(indicator.getType())) {
                SysLog.log("添加站点 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.CMS_Section.toString().equals(indicator.getType())) {
                SysLog.log("【站群管理】新增栏目，名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else*/ if (IndicatorEO.Type.CMS_Menu.toString().equals(indicator.getType())) {
                SysLog.log("添加菜单项 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.Shortcut.toString().equals(indicator.getType())) {
                SysLog.log("添加快捷方式 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.ToolBarButton.toString().equals(indicator.getType())) {
                SysLog.log("添加工具栏按钮 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.Menu.toString().equals(indicator.getType())) {
                SysLog.log("添加cms菜单 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.CMS_Button.toString().equals(indicator.getType())) {
                SysLog.log("添加cms按钮 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            } else if (IndicatorEO.Type.Other.toString().equals(indicator.getType())) {
                SysLog.log("添加权限集合 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Add.toString());
            }
        } else {
            indicatorService.updateEntity(indicator);
            //IndicatorEO cacheEO = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
            IndicatorEO cacheEO = indicatorService.getEntity(IndicatorEO.class, indicatorId);

            if (!cacheEO.getIsShown4Admin().equals(indicator.getIsShown4Admin())
                || !cacheEO.getIsShown4SystemManager().equals(indicator.getIsShown4SystemManager())
                || !cacheEO.getIsShown4ExternalUser().equals(indicator.getIsShown4ExternalUser())) {
                // 找到下面所有的子菜单更新超级管理员可见属性、站点管理员可见属性、普通用户可见属性
                List<IndicatorEO> list = new ArrayList<IndicatorEO>();
                this.getAllChildrenList(indicatorId, list, indicator);
                indicatorService.updateEntities(list);
            }
            /*if (IndicatorEO.Type.CMS_Site.toString().equals(indicator.getType())) {
                SysLog.log("修改站点 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.CMS_Section.toString().equals(indicator.getType())) {
                SysLog.log("【站群管理】更新栏目，名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else*/ if (IndicatorEO.Type.CMS_Menu.toString().equals(indicator.getType())) {
                SysLog.log("修改菜单项 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.Shortcut.toString().equals(indicator.getType())) {
                SysLog.log("修改快捷方式 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.ToolBarButton.toString().equals(indicator.getType())) {
                SysLog.log("修改工具栏按钮 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.Menu.toString().equals(indicator.getType())) {
                SysLog.log("修改cms菜单 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.CMS_Button.toString().equals(indicator.getType())) {
                SysLog.log("修改cms按钮 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            } else if (IndicatorEO.Type.Other.toString().equals(indicator.getType())) {
                SysLog.log("修改权限集合 >> ID：" + indicatorId + ",名称：" + indicator.getName(), "IndicatorEO", CmsLogEO.Operation.Update.toString());
            }
        }

        //增加菜单设置针对哪些用户隐藏
        if(IndicatorEO.Type.CMS_Menu.toString().equals(indicator.getType()) && null != indicator.getUsersList()) {
            //先物理删除
            if(!AppUtil.isEmpty(indicatorId)) {
                menuUserHideService.phyDelete(indicatorId);
            }
            List<RbacMenuUserHideEO> userHnEOs = JSONObject.parseArray(indicator.getUsersList(),RbacMenuUserHideEO.class);
            if(null != userHnEOs) {
                for(RbacMenuUserHideEO hide : userHnEOs) {
                    hide.setMenuId(indicatorId);
                }
            }
            menuUserHideService.saveEntities(userHnEOs);
            MenuUserHideCache.refresh();
        }
    }

//    @Override
//    public void updateEntity(IndicatorEO indicatorEO) {
//        indicatorService.updateEntity(indicatorEO);
//    }

    private void getAllChildrenList(Long indicatorId, List<IndicatorEO> list, IndicatorEO indicator) {
        List<IndicatorEO> childList = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, indicatorId);
        if (null != childList && !childList.isEmpty()) {
            for (IndicatorEO indicatorEO : childList) {
                indicatorEO.setIsShown4Admin(indicator.getIsShown4Admin());
                indicatorEO.setIsShown4SystemManager(indicator.getIsShown4SystemManager());
                indicatorEO.setIsShown4ExternalUser(indicator.getIsShown4ExternalUser());
                list.add(indicatorEO);
                this.getAllChildrenList(indicatorEO.getIndicatorId(), list, indicatorEO);
            }
        }
    }

    @Override
    public IndicatorEO getById(Long indicatorId) {
        return CacheHandler.getEntity(IndicatorEO.class, indicatorId);
    }

    @Override
    public List<IndicatorEO> getByParentId(Long parentId) {
        return CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, parentId);
    }

    @Override
    public void delete(Long indicatorId) {
        this.delete(new Long[]{indicatorId});
    }

    @Override
    public void delete(Long[] ids) {
        indicatorService.delete(IndicatorEO.class, ids);
    }

    @Override
    public void updateList(List<IndicatorEO> list) {
        indicatorService.updateEntities(list);
        CacheHandler.saveOrUpdate(IndicatorEO.class,list);
    }

    @Override
    public Pagination getButton(PageQueryVO vo, Long indicatorId) {
        Pagination page = new Pagination();
        if (null == indicatorId) {
            return page;
        }
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, indicatorId);// 按钮
        // 过滤查找出类型为按钮的对象
        if (null != list && !list.isEmpty()) {
            for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext(); ) {
                IndicatorEO eo = it.next();
                if (!IndicatorEO.Type.CMS_Button.toString().equals(eo.getType())) {
                    it.remove();
                }
            }
        }
        // 默认一页
        page.setData(list);
        page.setTotal(null == list ? 0L : (long) list.size());
        page.setPageIndex(vo.getPageIndex());
        page.setPageSize(vo.getPageSize());
        return page;
    }

    /**
     * 获取菜单 </br> 系统管理以及菜单管理是开发商必须可见的菜单选项</br>
     *
     * @see cn.lonsun.rbac.indicator.service.IIndicatorService#getMenu(boolean,
     * java.lang.Long)
     */
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
        boolean isSiteAdmin = LoginPersonUtil.isSiteAdmin();// 站点管理员
        // 所有用户都需要经过字段过滤，当用户为超级管理员或者root时不需要经过角色过滤
        if (isDevelopAmin || isSuperAdmin) {// 只有当用户为开发商时，才会进入菜单管理，其他用户都需要进行权限过滤
            if (!all) {// 非查询所有
                // 删除开发商或者超级管理员不可见的菜单
                for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext(); ) {
                    IndicatorEO eo = it.next();
                    if ((isDevelopAmin && !eo.getIsShown4Developer()) || (isSuperAdmin && !eo.getIsShown4Admin())) {
                        it.remove();
                    }
                }
            }
            if (!AppUtil.isEmpty(roleId)) {// 角色选中菜单
                // 需要根据角色类型再过滤一次菜单
                RoleEO roleEO = roleService.getEntity(RoleEO.class, roleId);
                boolean siteRole = "site_admin".equals(roleEO.getCode());
                for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext(); ) {
                    IndicatorEO eo = it.next();
                    if ((siteRole && !eo.getIsShown4SystemManager()) || !eo.getIsShown4ExternalUser()) {
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
        } else {// 根据用户角色选择菜单
            Long organId = LoginPersonUtil.getOrganId();
            Long userId = LoginPersonUtil.getUserId();
            Long siteId = LoginPersonUtil.getSiteId();

            List<RoleAssignmentEO> listRA = roleAssignmentService.getAssignments(organId, userId);
            Map<Long, IndicatorEO> map = new HashMap<Long, IndicatorEO>();
            for (RoleAssignmentEO eo : listRA) {
                List<IndicatorPermissionEO> ps = indicatorPermissionService.getByRoleId(eo.getRoleId());
                for (IndicatorPermissionEO ipEO : ps) {
                    if (mapIEO.containsKey(ipEO.getIndicatorId())) {
                        map.put(ipEO.getIndicatorId(), mapIEO.get(ipEO.getIndicatorId()));
                    }
                }
            }

            /********增加用户的权限-gf(2017-8-23)********/
            List<RbacUserMenuRightsEO> userMenuRights = userMenuRightsService.getCheckMenuRights(LoginPersonUtil.getUserId(),LoginPersonUtil.getSiteId());
            if(null != userMenuRights && !userMenuRights.isEmpty()) {
                for(RbacUserMenuRightsEO eo : userMenuRights) {
                    Long menuId = eo.getMenuId();
                    if (!map.containsKey(menuId) && mapIEO.containsKey(menuId)) {
                        map.put(menuId, mapIEO.get(menuId));
                    }
                }
            }
            /********增加用户的权限-gf********/

            list = new ArrayList<IndicatorEO>(map.values());
            // 过滤菜单
            for (Iterator<IndicatorEO> it = list.iterator(); it.hasNext(); ) {
                IndicatorEO eo = it.next();
                //@update 20180502 菜单绑定站点时，只在某个站点下才显示
                if(eo.getSiteId() != null && eo.getSiteId().longValue() != siteId.longValue()){
                    it.remove();
                    continue;
                }
                if ((isSiteAdmin && !eo.getIsShown4SystemManager()) || (!isSiteAdmin && !eo.getIsShown4ExternalUser())) {
                    it.remove();
                }
            }

            removeUserHideMenu(list);
        }
        return buildMenuList(list, checkedList);
    }

    /**
     * 构建菜单
     *
     * @param list
     * @return
     * @author fangtinghua
     */
    private List<MenuEO> buildMenuList(List<IndicatorEO> list, List<Long> checkedList) {
        if (null == list || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<MenuEO> menuList = new ArrayList<MenuEO>();
        for (IndicatorEO eo : list) {
            MenuEO menu = new MenuEO();
            menu.setId(eo.getIndicatorId());
            menu.setpId(eo.getParentId());
            menu.setName(eo.getName());
            menu.setUri(eo.getUri());
            menu.setTextIcon(eo.getTextIcon());
            menu.setSortNum(eo.getSortNum());
            menu.setIsTop(eo.getIsTop());
            menu.setIsEnable(eo.getIsEnable() ? "1" : "0");
            // 设置按钮权限
            // menu.setRights(menuRoleService.getCurUserHasRights(eo.getIndicatorId()));
            menu.setChecked(null == checkedList ? false : checkedList.contains(eo.getIndicatorId()));
            menu.setMenuCode(eo.getCode());
            menuList.add(menu);
        }
        Collections.sort(menuList, new Comparator<MenuEO>() { // 菜单排序
            public int compare(MenuEO m1, MenuEO m2) {
                return m1.getSortNum().compareTo(m2.getSortNum());
            }
        });
        // 增加父子结构
        return TreeUtil.buildTree(menuList);
    }

    /**
     * 删除需要对当前用户隐藏的菜单
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