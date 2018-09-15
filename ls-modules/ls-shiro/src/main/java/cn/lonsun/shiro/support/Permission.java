/*
 * Permission.java         2016年7月28日 <br/>
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

package cn.lonsun.shiro.support;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

/**
 * 页面权限判断 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年7月28日 <br/>
 */
@DefaultKey("shiro")
@ValidScope(Scope.APPLICATION)
public class Permission {

    /**
     * 是否有菜单权限
     *
     * @return
     * @author fangtinghua
     */
    public boolean hasMenu() {
        Session session = SecurityUtils.getSubject().getSession();
        return hasMenu(session.getAttribute(InternalAccount.USER_MENUID));
    }

    /**
     * 是否有菜单权限
     *
     * @param menuId
     * @return
     * @author fangtinghua
     */
    public boolean hasMenu(Object menuId) {
        if (null == menuId || NumberUtils.toLong(menuId.toString()) <= 0L) {// 判断
            return false;
        }
        Subject subject = SecurityUtils.getSubject();
        return subject.isPermitted(String.format(PermissionKey.KEY_MENU_ID, menuId));
    }

    /**
     * 是否有菜单按钮权限
     *
     * @param buttonCode
     * @return
     * @author fangtinghua
     */
    public boolean hasMenuButton(String buttonCode) {
        Session session = SecurityUtils.getSubject().getSession();
        return hasMenuButton(session.getAttribute(InternalAccount.USER_MENUID), buttonCode);
    }

    /**
     * 是否有菜单按钮权限
     *
     * @param menuId
     * @param buttonCode
     * @return
     * @author fangtinghua
     */
    public boolean hasMenuButton(Object menuId, String buttonCode) {
        Subject subject = SecurityUtils.getSubject();
        if (null == menuId || NumberUtils.toLong(menuId.toString()) <= 0L || StringUtils.isEmpty(buttonCode)) {// 判断
            return false;
        }
        return subject.isPermitted(String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, menuId, buttonCode));
    }

    /**
     * 是否有栏目按钮权限
     *
     * @param buttonCode
     * @return
     * @author fangtinghua
     */
    public boolean hasColumnButton(String buttonCode) {
        if (!isGeneralUser()) {
            return true;
        }
        Session session = SecurityUtils.getSubject().getSession();
        return hasColumnButton(session.getAttribute(InternalAccount.USER_INDICATORID), buttonCode);
    }

    /**
     * 是否有栏目按钮权限
     *
     * @param columnId
     * @param buttonCode
     * @return
     * @author fangtinghua
     */
    public boolean hasColumnButton(Object columnId, String buttonCode) {
        if (!isGeneralUser()) {
            return true;
        }
        Subject subject = SecurityUtils.getSubject();
        if (null == columnId || NumberUtils.toLong(columnId.toString()) <= 0L || StringUtils.isEmpty(buttonCode)) {// 判断
            return false;
        }
        return subject.isPermitted(String.format(PermissionKey.KEY_COLUMN_ID_BUTTON_CODE, columnId, buttonCode));
    }

    /**
     * 是否有信息公开按钮权限
     *
     * @param buttonCode
     * @return
     */
    public boolean hasPublicButton(String buttonCode) {
        if (!isGeneralUser()) {
            return true;
        }
        Session session = SecurityUtils.getSubject().getSession();
        Object organId = session.getAttribute(InternalAccount.PUBLIC_ORGANID);
        Object catId = session.getAttribute(InternalAccount.PUBLIC_CAT_ID);
        Object catType = session.getAttribute(InternalAccount.PUBLIC_CAT_TYPE);
        return hasPublicButton(organId, catId, catType, buttonCode);
    }

    /**
     * 是否有信息公开按钮权限
     *
     * @param organId
     * @param catId
     * @param buttonCode
     * @return
     */
    public boolean hasPublicButton(Object organId, Object catId, Object catType, String buttonCode) {
        if (!isGeneralUser()) {
            return true;
        }
        Subject subject = SecurityUtils.getSubject();
        if (null == organId || NumberUtils.toLong(organId.toString()) <= 0L || null == catType || StringUtils.isEmpty(buttonCode)) {// 判断
            return false;
        }
        String code = catType.toString();
        if (PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(code)) {// 查询目录
            if (null == catId || NumberUtils.toLong(catId.toString()) <= 0L) {// 判断
                return false;
            }
            PublicCatalogEO eo = CacheHandler.getEntity(PublicCatalogEO.class, catId.toString());
            if (null == eo) {
                return false;
            }
            code = eo.getCode();
        }
        return subject.isPermitted(String.format(PermissionKey.KEY_PUBLIC_ORGAN_ID_CAT_ID_BUTTON_CODE, organId, code, buttonCode));
    }

    /**
     * 是否普通用户
     *
     * @return
     */
    private boolean isGeneralUser() {
        boolean isDevelopAmin = LoginPersonUtil.isRoot();// 开发商
        boolean isSuperAdmin = LoginPersonUtil.isSuperAdmin();// 超级管理员
        boolean isSiteAdmin = LoginPersonUtil.isSiteAdmin();// 站点管理员
        return !isDevelopAmin && !isSuperAdmin && !isSiteAdmin;
    }
}