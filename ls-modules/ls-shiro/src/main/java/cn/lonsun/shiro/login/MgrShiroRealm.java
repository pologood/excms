/*
 * MgrShiroRealm.java         2016年6月22日 <br/>
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

package cn.lonsun.shiro.login;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.indicator.entity.MenuEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.IPersonUserService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.shiro.IpLimitException;
import cn.lonsun.shiro.security.UsernamePasswordToken;
import cn.lonsun.shiro.support.PermissionKey;
import cn.lonsun.shiro.util.GeetestLib;
import cn.lonsun.shiro.util.LoginMapUtil;
import cn.lonsun.shiro.util.OnlineSessionUtil;
import cn.lonsun.shiro.util.PermissionCacheLoader;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.system.globalconfig.internal.service.ILimitIPService;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;
import cn.lonsun.system.role.internal.entity.RbacUserInfoOpenRightsEO;
import cn.lonsun.system.role.internal.entity.RbacUserSiteRightsEO;
import cn.lonsun.system.role.internal.service.*;
import cn.lonsun.util.LoginPersonUtil;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.*;

/**
 * 系统管理shiro验证 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月22日 <br/>
 */
public  class MgrShiroRealm extends AuthorizingRealm {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(MgrShiroRealm.class);

    @Value("${user.login.enable:false}")
    private boolean enable;// 是否开启登录次数限制
    @Value("${user.login.times:3}")
    private int times;// 次数限制
    @Value("${user.login.interval:10}")
    private int interval;// 恢复时间间隔
    @Value("${user.login.pc.enable:false}")
    private boolean pcEnable;// 是否开启设备登录限制
    @Value("${gee.geetest_id:gee.geetest_id}")
    private String geetest_id;
    @Value("${gee.geetest_key:gee.geetest_key}")
    private String geetest_key;
    @Value("${gee.gtServerStatusSessionKey:gee.gtServerStatusSessionKey}")
    private String gtServerStatusSessionKey;
    @Resource(name = "userService")
    private IUserService userService;
    @Resource
    private IPersonUserService personUserService;
    @Resource
    private IRoleAssignmentService roleAssignmentService;
    @Resource
    private IRoleAsgService roleAsgService;
    @Resource
    private ILimitIPService limitIPService;
    @Resource
    private IMenuRoleService menuRoleService;// 菜单权限
    @Resource
    private ISiteRightsService siteRightsService;// 站点栏目权限
    @Resource
    private IInfoOpenRightsService infoOpenRightsService;// 信息公开权限

    @Resource
    private IUserMenuRightsService userMenuRightsService;
    @Resource
    private IUserSiteRightsService userSiteRightsService;
    @Resource
    private IUserInfoOpenRightsService userInfoOpenRightsService;

    private final String sessionAuthorizationInfoKey = "SessionUserAuthorizationInfo";

    /**
     * 为当前登录的Subject授予角色和权限，角色列表中放入当前用户角色code
     * 当为root或者超管，只区分菜单权限，当为站管时，需要查询出有权限的站点，当为普通用户时，需要区分菜单、栏目、信息公开等类别以及按钮权限
     *
     * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        SimpleAuthorizationInfo authorizationInfo = (SimpleAuthorizationInfo)LoginPersonUtil.getSession().getAttribute(sessionAuthorizationInfoKey);
        if(authorizationInfo == null){
            authorizationInfo = getSimpleAuthorizationInfo(principal);
            LoginPersonUtil.getSession().setAttribute(sessionAuthorizationInfoKey, authorizationInfo);
        }
        return authorizationInfo;
    }

    private SimpleAuthorizationInfo getSimpleAuthorizationInfo(PrincipalCollection principal) {
        UserEO userEO = (UserEO) super.getAvailablePrincipal(principal);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        List<MenuEO> menuRightList = new ArrayList<MenuEO>();// 菜单权限
        boolean isDevelopAdmin = LoginPersonUtil.isRoot();// 开发商
        boolean isSuperAdmin = LoginPersonUtil.isSuperAdmin();// 超级管理员
        boolean isSiteAdmin = LoginPersonUtil.isSiteAdmin();// 站点管理员

        // 查询角色列表
        Set<String> rolesSet = new HashSet<String>();

        Long[] roleIds = loadRoleSet(userEO.getUserId(), rolesSet);
        // 查询权限列表
        Set<String> permissionsSet = new HashSet<String>();
        //加载角色的栏目和信息公开权限列表
        if (null != roleIds && roleIds.length > 0) {
            loadRolePermission(permissionsSet, roleIds);
        }
        //加载用户的栏目和信息公开权限列表
        LoadUserPermission(userEO.getUserId(), permissionsSet);

        //<editor-fold desc="加载角色菜单">
        if (isDevelopAdmin) {
            menuRightList.addAll(menuRoleService.getDeveloperMenu());
        }else if (isSuperAdmin) {
            menuRightList.addAll(menuRoleService.getSuperAdminMenu());
        }else if(isSiteAdmin){
            menuRightList.addAll(menuRoleService.getSiteAdminMenu());
        }else{
            menuRightList.addAll(menuRoleService.getUserMenu());
        }
        //</editor-fold>

        //<editor-fold desc="加载用户菜单 ">
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userEO.getUserId());
//        map.put("siteId", LoginPersonUtil.getSiteId());
        List<MenuEO> userMenu = userMenuRightsService.getCheckMenu(userEO.getUserId());
        if(userMenu != null && !userMenu.isEmpty()){
            menuRightList.addAll(userMenu);
        }
        //</editor-fold>

        // 根据规则放入权限set中
        addMenuPermission(menuRightList, permissionsSet, isDevelopAdmin || isSuperAdmin);

        authorizationInfo.addRoles(rolesSet);
        authorizationInfo.addStringPermissions(permissionsSet);
        logger.info("用户权限信息：");
        logger.info("角色：{}", JSONArray.toJSONString(rolesSet));
        logger.info("权限数量：{}", JSONArray.toJSONString(permissionsSet));
        return authorizationInfo;
    }

    /**
     * 加载用户角色
     * @param userId
     * @param rolesSet
     * @return
     */
    private Long[] loadRoleSet(Long userId, Set<String> rolesSet) {
        Long[] roleIds = null;
        List<RoleAssignmentEO> roleAssignmentList = roleAssignmentService.getAssignments(userId);
        if (null != roleAssignmentList && !roleAssignmentList.isEmpty()) {
            int index = 0;
            roleIds = new Long[roleAssignmentList.size()];
            for (RoleAssignmentEO roleAssignmentEO : roleAssignmentList) {
                roleIds[index++] = roleAssignmentEO.getRoleId();
                rolesSet.add(roleAssignmentEO.getRoleCode());
            }
        }
        return roleIds;
    }

    /**
     * 加载用户权限
     * @param userId
     * @param permissionsSet
     */
    private void LoadUserPermission(Long userId, Set<String> permissionsSet) {
        //获取个人权限
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
//        map.put("siteId", LoginPersonUtil.getSiteId());
        List<RbacUserInfoOpenRightsEO> list = userInfoOpenRightsService.getEntities(RbacUserInfoOpenRightsEO.class,map);

        for (RbacUserInfoOpenRightsEO rbacInfoOpenRightsEO : list) {// 这里的rbacInfoOpenRightsEO.getCode()，其实是id值
            String code = rbacInfoOpenRightsEO.getCode();
            String optCode = rbacInfoOpenRightsEO.getOptCode();
            if (StringUtils.isNotEmpty(code)) {
                permissionsSet.add(String.format(PermissionKey.KEY_PUBLIC_ORGAN_ID_CAT_ID, rbacInfoOpenRightsEO.getOrganId(), code));// 信息公开目录
                if (StringUtils.isNotEmpty(optCode)) {
                    permissionsSet.add(String.format(PermissionKey.KEY_PUBLIC_ORGAN_ID_CAT_ID_BUTTON_CODE, rbacInfoOpenRightsEO.getOrganId(), code, optCode));// 信息公开按钮
                }
            }
        }
        map.clear();
        map.put("userId", userId);
//        map.put("siteId", LoginPersonUtil.getSiteId());
        List<RbacUserSiteRightsEO> siteRight = userSiteRightsService.getEntities(RbacUserSiteRightsEO.class, map);
        if(siteRight != null && !siteRight.isEmpty()){
            for (RbacUserSiteRightsEO userSiteRight : siteRight) {// 这里的rbacInfoOpenRightsEO.getCode()，其实是id值
                String type_id = PermissionKey.KEY_COLUMN_ID;
                String type_button = PermissionKey.KEY_COLUMN_ID_BUTTON_CODE;
                permissionsSet.add(String.format(type_id, userSiteRight.getIndicatorId()));// 栏目
                if (StringUtils.isNotEmpty(userSiteRight.getOptCode())) {
                    permissionsSet.add(String.format(type_button, userSiteRight.getIndicatorId(), userSiteRight.getOptCode()));// 菜单按钮
                }
            }
        }
    }

    /**
     * 加载角色权限，站点管理员和普通用户
     * @param roleIds
     * @param permissionsSet
     */
    private void loadRolePermission(Set<String> permissionsSet, Long... roleIds) {
        boolean isSiteAdmin = LoginPersonUtil.isSiteAdmin();// 站点管理员

        List<ColumnMgrEO> columnRightList = new ArrayList<ColumnMgrEO>();// 栏目权限
        List<RbacInfoOpenRightsEO> infoRightList = new ArrayList<RbacInfoOpenRightsEO>();// 信息公开权限
        if(!isSiteAdmin){
            //信息公开权限
            List<RbacInfoOpenRightsEO> infoList = infoOpenRightsService.getEOsByRoleIds(roleIds);
            if (null != infoList && !infoList.isEmpty()) {
                infoRightList.addAll(infoList);
            }
            for (Long roleId : roleIds) {
                List<ColumnMgrEO> columnList = (List<ColumnMgrEO>) siteRightsService.getRoleRights(roleId);
                if (null != columnList && !columnList.isEmpty()) {
                    columnRightList.addAll(columnList);
                }
            }
        }
        for (ColumnMgrEO columnMgrEO : columnRightList) {
            permissionsSet.add(String.format(PermissionKey.KEY_COLUMN_ID, columnMgrEO.getIndicatorId()));// 栏目
            List<FunctionEO> functionsList = columnMgrEO.getFunctions();// 按钮
            if (null != functionsList && !functionsList.isEmpty()) {
                for (FunctionEO eo : functionsList) {
                    permissionsSet.add(String.format(PermissionKey.KEY_COLUMN_ID_BUTTON_CODE, columnMgrEO.getIndicatorId(), eo.getAction()));// 菜单按钮
                }
            }
        }
        for (RbacInfoOpenRightsEO rbacInfoOpenRightsEO : infoRightList) {// 这里的rbacInfoOpenRightsEO.getCode()，其实是id值
            String code = rbacInfoOpenRightsEO.getCode();
            String optCode = rbacInfoOpenRightsEO.getOptCode();
            if (StringUtils.isNotEmpty(code)) {
                permissionsSet.add(String.format(PermissionKey.KEY_PUBLIC_ORGAN_ID_CAT_ID, rbacInfoOpenRightsEO.getOrganId(), code));// 信息公开目录
                if (StringUtils.isNotEmpty(optCode)) {
                    permissionsSet.add(String.format(PermissionKey.KEY_PUBLIC_ORGAN_ID_CAT_ID_BUTTON_CODE, rbacInfoOpenRightsEO.getOrganId(), code, optCode));// 信息公开按钮
                }
            }
        }
    }

    /**
     * 添加菜单按钮权限,递归处理所有的菜单权限
     * @param menuRightList
     * @param permissionsSet
     */
    private void addMenuPermission(List<MenuEO> menuRightList, Set<String> permissionsSet, Boolean isAdmin) {
        //普通管理员获取分配的角色权限
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(null, userId);

        for(RoleAssignmentEO role : roles) {
            List<RbacMenuRightsEO> roleMenuList = CacheHandler.getList(RbacMenuRightsEO.class, CacheGroup.CMS_ROLE_ID, role.getRoleId());
            if(roleMenuList == null){
                continue;
            }
            for(RbacMenuRightsEO eo : roleMenuList) {
                IndicatorEO menuItem = CacheHandler.getEntity(IndicatorEO.class, eo.getMenuId());
                if (StringUtils.isNotEmpty(menuItem.getUri())) {
                    permissionsSet.add(String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, menuItem.getIndicatorId(), PermissionCacheLoader.url2Code(menuItem.getUri())));// 菜单按钮
                }
                permissionsSet.add(String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, eo.getMenuId(), eo.getOptCode()));// 菜单按钮
            }
        }

        for (MenuEO menuEO : menuRightList) {
            List<IndicatorEO> buttonList = menuEO.getRights();// 按钮
            if (StringUtils.isNotEmpty(menuEO.getUri())) {
                permissionsSet.add(String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, menuEO.getId(), PermissionCacheLoader.url2Code(menuEO.getUri())));// 菜单按钮
            }
            if (null != buttonList && !buttonList.isEmpty()) {
                for (IndicatorEO eo : buttonList) {
                    permissionsSet.add(String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, menuEO.getId(), eo.getCode()));// 菜单按钮
                }
            }
            if(menuEO.getChildren() != null){
                addMenuPermission(menuEO.getChildren(), permissionsSet, isAdmin);
            }
        }
    }

    /**
     * 用户验证
     *
     * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     */

    protected void doGetJYAuthenticationInfo(UsernamePasswordToken token, Session session) throws AuthenticationException {
        GeetestLib gtSdk = new GeetestLib(geetest_id, geetest_key, true);
        //从session中获取gt-server状态
        int gt_server_status_code = (Integer) session.getAttribute(gtServerStatusSessionKey);

        //从session中获取userid
        String userid =session.getAttribute("userid").toString();

        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("user_id", userid); //网站用户id
        param.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("ip_address", "127.0.0.1"); //传输用户请求验证时所携带的IP
        String challenge=token.getGeetest_challenge();
        String validate=token.getGeetest_validate();
        String seccode=token.getGeetest_seccode();
        int gtResult = 0;
        if (gt_server_status_code == 1) {
            //gt-server正常，向gt-server进行二次验证

            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
            logger.info("{}",gtResult);
        } else {
            // gt-server非正常情况下，进行failback模式验证
            logger.info("failback:use your own server captcha validate");
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
        }
        logger.info("{}",gtResult);
    }


    /**
     * 用户验证
     *
     * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        logger.debug("验证当前Subject时获取到token：" + ReflectionToStringBuilder.toString(token, ToStringStyle.MULTI_LINE_STYLE));
        // 校验ip
        Session session = SecurityUtils.getSubject().getSession();
        String ip = (String) session.getAttribute(InternalAccount.MDC_IP);
        if (!limitIPService.checkValidateIP(ip)) {
            throw new IpLimitException("此IP禁止访问本系统！"); // ip禁止访问
        }
        //如果设置了极验验证码，则使用极验登录方式
        if(null != geetest_id && !geetest_id.equals("gee.geetest_id") && null == token.getCode()){
            doGetJYAuthenticationInfo(token, session);
        }else{
            // 校验验证码
            String tokenCode = token.getCode();
            if (StringUtils.isEmpty(token.getCode())) {
                throw new UnknownAccountException("验证码不能为空！");
            }
            Object code = session.getAttribute(InternalAccount.LOGIN_CODE);
            if (code == null) {
                throw new UnknownAccountException("验证码已经失效！");
            }
            if (!tokenCode.equalsIgnoreCase(code.toString())) {
                throw new UnknownAccountException("验证码错误！");
            }
        }
        // 校验用户
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", token.getUsername());
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        UserEO user = userService.getEntity(UserEO.class, params);
        if (null == user) {
            throw new UnknownAccountException("用户名不存在！");
        }
        String status = user.getStatus();
        if (StringUtils.isEmpty(status) || UserEO.STATUS.Unable.toString().equals(status)) {
            throw new LockedAccountException("您的账号已被锁定，请联系管理员！"); // 帐号锁定
        }
        // 身份、凭据
//        return new SimpleAuthenticationInfo(user, "1", this.getName());
        return new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
    }

    /**
     * 密码验证
     *
     * @see org.apache.shiro.realm.AuthenticatingRealm#assertCredentialsMatch(org.apache.shiro.authc.AuthenticationToken,
     * org.apache.shiro.authc.AuthenticationInfo)
     */
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        CredentialsMatcher cm = getCredentialsMatcher();
        if (cm != null) {
            UserEO user = (UserEO) info.getPrincipals().getPrimaryPrincipal();// 用户
            // 当为root或者超管时，调用父类
            if (isRootOrSuperAdmin(user)) {
                super.assertCredentialsMatch(token, info);
                return;
            }
            // 判断是否已经登录过
            if (pcEnable && OnlineSessionUtil.checkUserLoginTimeAndIsAlreadyLogin()) {
                throw new LockedAccountException("用户已在其他设备登录！");
            }
            if (!enable) {// 没有开启登录次数限制
                super.assertCredentialsMatch(token, info);
                return;
            }
            Integer retryTimes = user.getRetryTimes() == null ? 0 : user.getRetryTimes();// 0,1,2
            if (retryTimes >= times) {// 任务超限，10分钟后重置数据库，在这里拦截的目的在于不管密码是否正确
                LoginMapUtil.put(user, interval);// 放入定时任务
                throw new LockedAccountException("密码错误次数超限，" + interval + "分钟后再登录！");
            }
            if (!cm.doCredentialsMatch(token, info)) {// 密码不匹配
                user.setRetryTimes(retryTimes + 1);
                userService.updateEntity(user);// 更新数据库
                int laveTimes = times - retryTimes - 1;// 剩余次数
                if (laveTimes > 0) {
                    throw new LockedAccountException("密码错误，您还可以尝试" + laveTimes + "次！");
                }
                LoginMapUtil.put(user, interval);// 放入定时任务
                throw new LockedAccountException("密码错误次数超限，" + interval + "分钟后再登录！");
            }
        } else {
            throw new AuthenticationException("A CredentialsMatcher must be configured in order to verify "
                    + "credentials during authentication.  If you do not wish for credentials to be examined, you " + "can configure an "
                    + AllowAllCredentialsMatcher.class.getName() + " instance.");
        }
    }

    /**
     * 判断是否root或超管角色
     *
     * @param user
     * @return
     * @author fangtinghua
     */
    private boolean isRootOrSuperAdmin(UserEO user) {
        // 是否root用户
        boolean result = InternalAccount.DEVELOPER_ADMIN_CODE.equals(user.getUid());
        if (!result) {
            // 设置用户信息
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute(InternalAccount.USER_USERID, user.getUserId());
            // 记录人员信息
            PersonEO p = personUserService.getPerson(user.getUserId());
            session.setAttribute(InternalAccount.PERSON_PERSON, p);
            // 判断是否超管
            result = roleAsgService.confirmRole(InternalAccount.SUPER_ADMIN_CODE, p.getOrganId(), p.getUserId());
            session.setAttribute(InternalAccount.USER_ISSUPERADMIN, result);
        }
        return result;
    }

    public IUserService getUserService() {
        return userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public IPersonUserService getPersonUserService() {
        return personUserService;
    }

    public void setPersonUserService(IPersonUserService personUserService) {
        this.personUserService = personUserService;
    }

    public IRoleAssignmentService getRoleAssignmentService() {
        return roleAssignmentService;
    }

    public void setRoleAssignmentService(IRoleAssignmentService roleAssignmentService) {
        this.roleAssignmentService = roleAssignmentService;
    }

    public IRoleAsgService getRoleAsgService() {
        return roleAsgService;
    }

    public void setRoleAsgService(IRoleAsgService roleAsgService) {
        this.roleAsgService = roleAsgService;
    }

    public ILimitIPService getLimitIPService() {
        return limitIPService;
    }

    public void setLimitIPService(ILimitIPService limitIPService) {
        this.limitIPService = limitIPService;
    }

    public IMenuRoleService getMenuRoleService() {
        return menuRoleService;
    }

    public void setMenuRoleService(IMenuRoleService menuRoleService) {
        this.menuRoleService = menuRoleService;
    }

    public ISiteRightsService getSiteRightsService() {
        return siteRightsService;
    }

    public void setSiteRightsService(ISiteRightsService siteRightsService) {
        this.siteRightsService = siteRightsService;
    }

    public IInfoOpenRightsService getInfoOpenRightsService() {
        return infoOpenRightsService;
    }

    public void setInfoOpenRightsService(IInfoOpenRightsService infoOpenRightsService) {
        this.infoOpenRightsService = infoOpenRightsService;
    }

    public IUserMenuRightsService getUserMenuRightsService() {
        return userMenuRightsService;
    }

    public void setUserMenuRightsService(IUserMenuRightsService userMenuRightsService) {
        this.userMenuRightsService = userMenuRightsService;
    }

    public IUserSiteRightsService getUserSiteRightsService() {
        return userSiteRightsService;
    }

    public void setUserSiteRightsService(IUserSiteRightsService userSiteRightsService) {
        this.userSiteRightsService = userSiteRightsService;
    }

    public IUserInfoOpenRightsService getUserInfoOpenRightsService() {
        return userInfoOpenRightsService;
    }

    public void setUserInfoOpenRightsService(IUserInfoOpenRightsService userInfoOpenRightsService) {
        this.userInfoOpenRightsService = userInfoOpenRightsService;
    }
}