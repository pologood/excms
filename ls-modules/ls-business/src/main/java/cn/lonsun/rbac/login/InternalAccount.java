/*
 * InternalAccount.java         2016年6月14日 <br/>
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

package cn.lonsun.rbac.login;

/**
 * 系统内置账号 <br/>
 *
 * @date 2016年6月14日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class InternalAccount {

    public static final String DEVELOPER_ADMIN_CODE = "lonsun_root";// 厂商
    public static final String SUPER_ADMIN_CODE = "super_admin";// 超级管理员
    public static final String SITE_ADMIN_CODE = "site_admin";// 站点管理员
    public static final String COLUMN_ADMIN_CODE = "Column";// 栏目管理员
    public static final String USER_USERID = "userId";// 用户id
    public static final String USER_UID = "uid";// 用户名
    public static final String USER_USERNAME = "userName";// 用户名
    public static final String PERSON_PERSON = "person";// 人员
    public static final String PERSON_PERSONID = "personId";// 人员id
    public static final String PERSON_PERSONNAME = "personName";// 人员名称
    public static final String ORGAN_ORGANID = "organId";// 组织id
    public static final String ORGAN_ORGANNAME = "organName";// 组织名称
    public static final String UNIT_UNITID = "unitId";// 单位名称
    public static final String UNIT_UNITNAME = "unitName";// 单位名称
    public static final String USER_SITEID = "siteId";// 站点id
    public static final String PRE_USER_SITEID = "preSiteId";// 站点id
    public static final String USER_MENUID = "menuId";// 菜单id
    public static final String USER_COLUMNID = "columnId";// 栏目id
    public static final String USER_INDICATORID = "indicatorId";// 栏目id
    public static final String PUBLIC_ORGANID = "public_organId";// 信息公开单位id
    public static final String PUBLIC_CAT_ID = "public_catId";// 信息公开目录id
    public static final String PUBLIC_CAT_TYPE = "public_type";// 信息公开类型
    public static final String USER_ISSUPERADMIN = "isSuperAdmin";// 是否超管
    public static final String USER_ISSITEADMIN = "isSiteAdmin";// 是否站管
    public static final String USER_ISCOLUMNADMIN = "isColumnAdmin";// 是否栏目管理员
    public static final String USER_MAINTENANCE_SITE = "maintenance_site";// 是否站管
    public static final String USER_RIGHTSCODE = "rightsCode";// 用户角色类型code
    public static final String USER_CALLBACK = "callback";// 回调函数
    public static final String USER_DATATYPE = "dataType";// 数据类型
    public static final String USER_DATAFLAG = "dataFlag";// object脱壳标识
    public static final String MDC_USERID = "userId";// 日志
    public static final String MDC_USERNAME = "userName";// 日志
    public static final String MDC_IP = "ip";// ip地址
    public static final String MEMBER = "member";// 会员信息
    public static final String SESSIONID = "sessionId";// 页面传入的sessionId
    public static final String LOGIN_CODE = "verification_code";// 验证码
    public static final String LOGIN_PHONE_CODE = "verification_phone_code";// 手机验证码
}