/*
 * PermissionKey.java         2016年7月28日 <br/>
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

/**
 * 权限key <br/>
 *
 * @date 2016年7月28日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PermissionKey {

    /** 页面判断 */
    // 菜单权限key，id是唯一
    public static final String KEY_MENU_ID = "menu-id-%s";
    // 菜单按钮权限key
    public static final String KEY_MENU_ID_BUTTON_CODE = "[%s|%s]";
    // 站点权限key，id是唯一
    public static final String KEY_SITE_ID = "site-id-%s";
    // 栏目权限key，id是唯一
    public static final String KEY_COLUMN_ID = "column-id-%s";
    // 栏目按钮权限key
    public static final String KEY_COLUMN_ID_BUTTON_CODE = "column-id-%s-button-code-%s";
    // 信息公开目录权限key，部门id+目录id唯一
    public static final String KEY_PUBLIC_ORGAN_ID_CAT_ID = "public-organId-%s-catId-%s";
    // 信息公开目录按钮权限key
    public static final String KEY_PUBLIC_ORGAN_ID_CAT_ID_BUTTON_CODE = "public-organId-%s-catId-%s-button-code-%s";

    /** url判断 */
    // 菜单权限key，id带上url
    public static final String KEY_MENU_ID_URL = "menu-id-%s-url-%s";
    // 菜单按钮权限key
    public static final String KEY_MENU_ID_BUTTON_URL = "menu-id-%s-button-url-%s";
    // 栏目按钮权限key
    public static final String KEY_COLUMN_ID_BUTTON_URL = "column-id-%s-button-url-%s";
    // 信息公开目录按钮权限key
    public static final String KEY_PUBLIC_ORGAN_ID_CAT_ID_BUTTON_URL = "public-organId-%s-catId-%s-button-url-%s";
}