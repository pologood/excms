/*
 * FormAuthenticationFilter.java         2016年6月14日 <br/>
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

package cn.lonsun.shiro.filter;

import cn.lonsun.core.util.BrowserUtils;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.log.internal.entity.LoginHistoryEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.IPersonUserService;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.rbac.internal.service.impl.UserServiceImpl;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.shiro.security.UsernamePasswordToken;
import cn.lonsun.shiro.util.AjaxRequestUtil;
import cn.lonsun.shiro.util.RSAUtils;
import cn.lonsun.system.role.internal.service.IRoleAsgService;
import cn.lonsun.system.systemlog.internal.entity.CmsLoginHistoryEO;
import cn.lonsun.system.systemlog.internal.service.ICmsLoginHistoryService;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录、验证 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月14日 <br/>
 */
public class MgrFormAuthenticationFilter extends FormAuthenticationFilter {
    // 参数
    protected static final String PARAM_MENU_ID = "menuId";
    protected static final String PARAM_SITE_ID = "siteId";
    protected static final String PARAM_COLUMN_ID = "columnId";
    protected static final String PARAM_INDICATOR_ID = "indicatorId";
    protected static final String PARAM_ORGAN_ID = "organId";
    protected static final String PARAM_CAT_ID = "catId";
    protected static final String PARAM_CAT_TYPE = "type";

    @Value("${user.login.pc.enable:false}")
    private boolean pcEnable;// 是否开启设备登录限制
    @Resource
    private IPersonUserService personUserService;
    @Resource
    private IRoleAsgService roleAsgService;
    @Resource
    private IUserService userService;
    @Resource
    private ICmsLoginHistoryService loginHistoryService;

    /**
     * url权限控制
     * <p>
     * cn.lonsun.shiro.filter.FormAuthenticationFilter#isUrlAllowed(javax.servlet.ServletRequest)
     */
    @Override
    public boolean isUrlAllowed(ServletRequest request, ServletResponse response) {
        // 请求的url
        String requestURI = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
        // 排除登录请求或者首页请求
        if (isLoginRequest(request, response) || pathsMatch(super.getSuccessUrl(), request)) {
            return super.isUrlAllowed(request, response);
        }
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        Object menuId = request.getParameter(PARAM_MENU_ID);// 菜单id
        Object sessionMenuId = session.getAttribute(InternalAccount.USER_MENUID);// session中的菜单id
        if (null == menuId && null == sessionMenuId) {// 当两者都为空时，说明没有操作的菜单，返回无权限
            // throw new UnauthorizedException("request not has parameter [menuId]");
        }
        if (null != menuId && !menuId.equals(sessionMenuId)) {// 检查菜单和菜单url权限
            session.setAttribute(InternalAccount.USER_MENUID, menuId);
            // currentUser.checkPermission(String.format(PermissionKey.KEY_MENU_ID, menuId));
            // currentUser.checkPermission(String.format(PermissionKey.KEY_MENU_ID_URL, menuId, requestURI));
        }
        boolean isDevelopAmin = LoginPersonUtil.isRoot();// 开发商
        boolean isSuperAdmin = LoginPersonUtil.isSuperAdmin();// 超级管理员
        boolean isSiteAdmin = LoginPersonUtil.isSiteAdmin();// 站点管理员
        if (isDevelopAmin || isSuperAdmin) {// root或者超管，只区分菜单权限
            return super.isUrlAllowed(request, response);
        }
        Object siteId = request.getParameter(PARAM_SITE_ID);// 站点id
        Object sessionSiteId = session.getAttribute(InternalAccount.USER_SITEID);// session中的站点id
        if (null == siteId && null == sessionSiteId) {// 当两者都为空时，说明没有操作的站点，返回无权限
            // throw new UnauthorizedException("request not has parameter [siteId]");
        }
        if (null != siteId && !siteId.equals(sessionSiteId)) {// 检查站点权限
            // currentUser.checkPermission(String.format(PermissionKey.KEY_SITE_ID, siteId));
        }
        if (isSiteAdmin) {// 站点管理员只判断站点权限
            return super.isUrlAllowed(request, response);
        }
        // 判断栏目权限
        Object columnId = request.getParameter(PARAM_COLUMN_ID);// 栏目id
        if (null == columnId) {
            columnId = request.getParameter(PARAM_INDICATOR_ID);// 栏目id
        }
        // 如果栏目id不为空，说明当前的操作是针对栏目
        if (null != columnId && !columnId.equals(session.getAttribute(InternalAccount.USER_INDICATORID))) {
            session.setAttribute(InternalAccount.USER_INDICATORID, columnId);
            // currentUser.checkPermission(String.format(PermissionKey.KEY_COLUMN_ID, columnId));
            // currentUser.checkPermission(String.format(PermissionKey.KEY_COLUMN_ID_BUTTON_URL, requestURI));
        }
        // 判断信息公开
        Object organId = request.getParameter(PARAM_ORGAN_ID);// 信息公开单位id
        Object catId = request.getParameter(PARAM_CAT_ID);// 信息公开目录id
        Object catType = request.getParameter(PARAM_CAT_TYPE);// 信息公开类型
        if (null != catId && !catId.equals(session.getAttribute(InternalAccount.PUBLIC_CAT_ID))) {
            session.setAttribute(InternalAccount.PUBLIC_CAT_ID, catId);
        }
        if (null != catType && !catType.equals(session.getAttribute(InternalAccount.PUBLIC_CAT_TYPE))) {
            session.setAttribute(InternalAccount.PUBLIC_CAT_TYPE, catType);
        }
        if (null != organId && !organId.equals(session.getAttribute(InternalAccount.PUBLIC_ORGANID))) {
            session.setAttribute(InternalAccount.PUBLIC_ORGANID, organId);
            // currentUser.checkPermission(String.format(PermissionKey.KEY_PUBLIC_ORGAN_ID_CAT_ID, organId, catId));
            // currentUser.checkPermission(String.format(PermissionKey.KEY_PUBLIC_ORGAN_ID_CAT_ID_BUTTON_URL, organId, catId, requestURI));
        }
        return super.isUrlAllowed(request, response);
    }

    /**
     * 允许访问
     *
     * @see org.apache.shiro.web.filter.authc.AuthenticatingFilter#isAccessAllowed(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, java.lang.Object)
     */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean isAccessAllowed = super.isAccessAllowed(request, response, mappedValue);
        if (isAccessAllowed) {// 当为登录操作时，进入不了此条件，所以登录成功记录不了ThreadLocal相关信息
            Session session = SecurityUtils.getSubject().getSession();
            // ThreadLocal设置
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ThreadUtil.LocalParamsKey.UserId.toString(), session.getAttribute(InternalAccount.USER_USERID));
            map.put(ThreadUtil.LocalParamsKey.Uid.toString(), session.getAttribute(InternalAccount.USER_USERNAME));
            map.put(ThreadUtil.LocalParamsKey.PersonName.toString(), session.getAttribute(InternalAccount.PERSON_PERSONNAME));
            map.put(ThreadUtil.LocalParamsKey.OrganId.toString(), session.getAttribute(InternalAccount.ORGAN_ORGANID));
            map.put(ThreadUtil.LocalParamsKey.OrganName.toString(), session.getAttribute(InternalAccount.ORGAN_ORGANNAME));
            map.put(ThreadUtil.LocalParamsKey.Callback.toString(), WebUtils.getCleanParam(request, InternalAccount.USER_CALLBACK));
            map.put(ThreadUtil.LocalParamsKey.DataFlag.toString(), WebUtils.getCleanParam(request, InternalAccount.USER_DATAFLAG));
            map.put(ThreadUtil.LocalParamsKey.IP.toString(), session.getAttribute(InternalAccount.MDC_IP));
            ThreadUtil.set(map);
            // 设置MDC
            Object userId = session.getAttribute(InternalAccount.USER_USERID);
            MDC.put(InternalAccount.MDC_USERID, null == userId ? "" : userId.toString());
            Object userName = session.getAttribute(InternalAccount.USER_USERNAME);
            MDC.put(InternalAccount.MDC_USERNAME, null == userName ? "" : userName.toString());
            Object ip = session.getAttribute(InternalAccount.MDC_IP);
            MDC.put(InternalAccount.MDC_IP, null == ip ? "" : ip.toString());
        }
        return isAccessAllowed;
    }

    /**
     * 登录成功设置session属性
     *
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginSuccess(org.apache.shiro.authc.AuthenticationToken,
     * org.apache.shiro.subject.Subject, javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     */
    @Override
    public boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        Subject currentUser = SecurityUtils.getSubject();
        UserEO user = (UserEO) currentUser.getPrincipal();
        Session session = currentUser.getSession();
        String rightsCode = "normal";
        // 日志信息
        CmsLoginHistoryEO history = new CmsLoginHistoryEO();
        history.setUid(user.getUid());
        Object ip = session.getAttribute(InternalAccount.MDC_IP);
        history.setLoginIp(null == ip ? "" : ip.toString());
        // 设置用户相关信息
        session.setAttribute(InternalAccount.USER_USERID, user.getUserId());
        session.setAttribute(InternalAccount.USER_USERNAME, user.getUid());
        session.setAttribute(InternalAccount.USER_UID, user.getUid());
        boolean isRoot = InternalAccount.DEVELOPER_ADMIN_CODE.equals(user.getUid());
        boolean isSuperAdmin = false;//超管
        // 设置人员信息，厂商除外
        if (!isRoot) {
            PersonEO person = (PersonEO) session.getAttribute(InternalAccount.PERSON_PERSON);
            // 统计登录次数
            user.setLastLoginDate(new Date());
            user.setLoginTimes(user.getLoginTimes() + 1);
            user.setRetryTimes(0);// 还原
            user.setLastLoginIp(null == ip ? "" : ip.toString());
            userService.updateEntity(user);
            session.setAttribute(InternalAccount.PERSON_PERSONID, person.getPersonId());
            session.setAttribute(InternalAccount.PERSON_PERSONNAME, person.getName());
            session.setAttribute(InternalAccount.ORGAN_ORGANID, person.getOrganId());
            session.setAttribute(InternalAccount.ORGAN_ORGANNAME, person.getOrganName());
            session.setAttribute(InternalAccount.UNIT_UNITID, person.getUnitId());
            session.setAttribute(InternalAccount.UNIT_UNITNAME, person.getUnitName());
            history.setUnitId(person.getUnitId());
            history.setUnitName(person.getUnitName());
            history.setOrganName(person.getOrganName());
            history.setCreateUser(person.getName());
            // 判断超管
            isSuperAdmin = (Boolean) session.getAttribute(InternalAccount.USER_ISSUPERADMIN);
            if (isSuperAdmin) {// 超级管理员
                rightsCode = "superAdmin";
            } else {
                boolean isSiteAdmin = roleAsgService.confirmRole(InternalAccount.SITE_ADMIN_CODE, person.getOrganId(), person.getUserId());
                session.setAttribute(InternalAccount.USER_ISSITEADMIN, isSiteAdmin);

                if (isSiteAdmin) {
                    Long[] ids = roleAsgService.getCurSiteIds(person.getOrganId(), person.getUserId());
                    //此处保存为 ,id1,id2,id3, 的格式，为了后面判断方便
                    StringBuilder sb = new StringBuilder();
                    sb.append(",");
                    for(Long id : ids){
                        sb.append(id).append(",");
                    }
                    session.setAttribute(InternalAccount.USER_MAINTENANCE_SITE, sb.toString());
                    rightsCode = "siteAdmin";// 站点管理员管理员
                }
            }
        } else {// root用户没有单位
            history.setCreateUser(user.getUid());
        }
        // 设置用户角色类型
        session.setAttribute(InternalAccount.USER_RIGHTSCODE, isRoot ? "root" : rightsCode);
        // 踢出已经登录用户
        if (pcEnable && !isRoot && !isSuperAdmin) {//不区分超管用户
            super.kickoutUser();
        }
        // 记录日志
        history.setLoginStatus(LoginHistoryEO.LoginStatus.Success.toString());
        history.setDescription(UserServiceImpl.LoginDescription.LoginSueess.getValue());
        history.setBrowser(BrowserUtils.checkBrowse(WebUtils.toHttp(request)));
        history.setOs(BrowserUtils.getClientOS(WebUtils.toHttp(request)));
        loginHistoryService.saveEntity(history);
        // 判断ajax请求
        if (AjaxRequestUtil.isAjax(request)) {
            ResultVO vo = new ResultVO();
            vo.setStatus(ResultVO.Status.SUCCESS.getValue());
            vo.setDesc("登录成功.");
            vo.setData(super.getSuccessUrl());
            return AjaxRequestUtil.printAjax(response, vo);
        }
        return super.onLoginSuccess(token, subject, request, response);
    }

    /**
     * 创建令牌
     *
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#createToken(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        String geetest_challenge=WebUtils.getCleanParam(request,"geetest_challenge");
        String geetest_validate=WebUtils.getCleanParam(request, "geetest_validate");
        String geetest_seccode=WebUtils.getCleanParam(request, "geetest_seccode");
        if ("true".equals(WebUtils.getCleanParam(request, "isEncryption"))) {//判断密码是否已经被加密
            password = RSAUtils.decryptStringByJs(password);
        }
        UsernamePasswordToken token = new UsernamePasswordToken(username, DigestUtils.md5Hex(password), geetest_challenge,geetest_validate,geetest_seccode);
//        UsernamePasswordToken token = new UsernamePasswordToken(username, "1", geetest_challenge,geetest_validate,geetest_seccode);
        // 传统的验证码
        String code = WebUtils.getCleanParam(request, "code");// 验证码
        token.setCode(code);
        return token;

    }


}