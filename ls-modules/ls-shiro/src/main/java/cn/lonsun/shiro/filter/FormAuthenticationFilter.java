/*
 * FormAuthenticationFilter.java         2016年6月23日 <br/>
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
import cn.lonsun.log.internal.entity.LoginHistoryEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.shiro.IpLimitException;
import cn.lonsun.shiro.session.ehcache.EhcacheSessionDAO;
import cn.lonsun.shiro.util.AjaxRequestUtil;
import cn.lonsun.shiro.util.ShiroExceptionUtil;
import cn.lonsun.system.systemlog.internal.entity.CmsLoginHistoryEO;
import cn.lonsun.system.systemlog.internal.service.ICmsLoginHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * authc <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月23日 <br/>
 */
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter implements UrlAutherticationFilter {
    @Resource
    private EhcacheSessionDAO ehcacheSessionDAO;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private ICmsLoginHistoryService loginHistoryService;

    /**
     * 默认url可以访问
     */
    @Override
    public boolean isUrlAllowed(ServletRequest request, ServletResponse response) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginRequest(request, response) && !isLoginSubmission(request, response)) {
            SecurityUtils.getSubject().logout();// 当请求路径为登录页面时，当用户已经登录过，注销当前session
        }
        return super.isAccessAllowed(request, response, mappedValue) && isUrlAllowed(request, response);
    }

    /**
     * 异常处理
     *
     * @see org.apache.shiro.web.servlet.AdviceFilter#afterCompletion(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, java.lang.Exception)
     */
    @Override
    public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
        if (null != exception) {
            ShiroExceptionUtil.processException(WebUtils.toHttp(request), WebUtils.toHttp(response), exception);
        } else {
            super.afterCompletion(request, response, exception);
        }
    }

    /**
     * cookie rememerMe
     *
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#isRememberMe(javax.servlet.ServletRequest)
     */
    @Override
    protected boolean isRememberMe(ServletRequest request) {
        return true;
    }

    /**
     * 跳转到登录页面
     *
     * @see org.apache.shiro.web.filter.AccessControlFilter#saveRequestAndRedirectToLogin(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     */
    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        if (AjaxRequestUtil.isAjax(request)) {// 返回ajax
            String description = "登录超时！";
            // 请求类型
            String dataType = StringUtils.lowerCase(WebUtils.getCleanParam(request, InternalAccount.USER_DATATYPE));
            if ("html".equals(dataType)) {// 请求的是html
                StringBuffer script = new StringBuffer();
               /* script.append("<script>Ls.tipsErr('").append(description).append("',function(){if(confirm");
                script.append("('是否重新登录？')){window.top.location='").append(super.getLoginUrl()).append("'}})</script>");*/
                script.append("<script>top.Ls.reload();</script>");
                AjaxRequestUtil.printString(response, script.toString());
            } else {
                ResultVO vo = new ResultVO();
                vo.setStatus(ResultVO.Status.Timeout.getValue());
                vo.setDesc(description);
                AjaxRequestUtil.printAjax(response, vo);
            }
        } else {
            // 判断当前请求地址是否是登录地址，不是则返回404
            if (!isLoginRequest(request, response)) { // 非登录请求
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_NOT_FOUND);// 404
                return;
            }
            super.saveRequestAndRedirectToLogin(request, response);
        }
    }

    /**
     * 登录失败
     *
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginFailure(org.apache.shiro.authc.AuthenticationToken,
     * org.apache.shiro.authc.AuthenticationException,
     * javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        if (AjaxRequestUtil.isAjax(request)) {// 返回ajax
            ResultVO vo = new ResultVO();
            vo.setStatus(ResultVO.Status.Failure.getValue());
            if (e instanceof UnknownAccountException || e instanceof LockedAccountException || e instanceof IpLimitException) {
                vo.setDesc(e.getMessage());
            } else {
                vo.setDesc("密码错误！");
                org.apache.shiro.authc.UsernamePasswordToken t = (org.apache.shiro.authc.UsernamePasswordToken) token;
                if (t != null) {
                    CmsLoginHistoryEO history = new CmsLoginHistoryEO();
                    history.setUid(t.getUsername());
                    history.setCreateUser(t.getUsername());
                    history.setDescription("密码错误");
                    Session session = SecurityUtils.getSubject().getSession();
                    Object ip = session.getAttribute(InternalAccount.MDC_IP);
                    Object obj = session.getAttribute(InternalAccount.PERSON_PERSON);
                    history.setLoginIp(null == ip ? "" : ip.toString());
                    if (obj != null) {
                        PersonEO person = (PersonEO) obj;
                        history.setUnitId(person.getUnitId());
                        history.setUnitName(person.getUnitName());
                        history.setOrganName(person.getOrganName());
                        history.setCreateUser(person.getName());
                    }
                    history.setLoginStatus(LoginHistoryEO.LoginStatus.Failure.toString());
                    history.setBrowser(BrowserUtils.checkBrowse(WebUtils.toHttp(request)));
                    history.setOs(BrowserUtils.getClientOS(WebUtils.toHttp(request)));
                    loginHistoryService.saveEntity(history);
                }
            }
            return AjaxRequestUtil.printAjax(response, vo);
        }
        return super.onLoginFailure(token, e, request, response);
    }

    /**
     * 踢出用户
     *
     * @author fangtinghua
     */
    public void kickoutUser() {
        Session subjectSession = SecurityUtils.getSubject().getSession();
        Session currentSession = ehcacheSessionDAO.readSession(subjectSession.getId());
        Collection<Session> sessionList = ehcacheSessionDAO.getActiveSessions();
        if (null != sessionList && !sessionList.isEmpty()) {// 异步执行
            taskExecutor.execute(new KickUserRunnable(currentSession, sessionList));
        }
    }

    /**
     * 设置session超时
     *
     * @param response
     * @author fangtinghua
     */
    public void setTimeout(ServletResponse response) {
        // 在请求头中存储session超时判断
        WebUtils.toHttp(response).setHeader("sessionstatus", String.valueOf(ResultVO.Status.Timeout.getValue()));
    }
}