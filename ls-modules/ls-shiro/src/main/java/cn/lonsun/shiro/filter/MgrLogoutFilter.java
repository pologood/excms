/*
 * LogoutFilter.java         2016年6月22日 <br/>
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
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.shiro.util.AjaxRequestUtil;
import cn.lonsun.system.systemlog.internal.entity.CmsLoginHistoryEO;
import cn.lonsun.system.systemlog.internal.service.ICmsLoginHistoryService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 登出 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月22日 <br/>
 */
public class MgrLogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter {

    @Resource
    private ICmsLoginHistoryService loginHistoryService;

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject currentUser = SecurityUtils.getSubject();
        UserEO user = (UserEO) currentUser.getPrincipal();
        Session session = currentUser.getSession();
        if (user != null) {
            CmsLoginHistoryEO history = new CmsLoginHistoryEO();
            history.setUid(user.getUid());
            history.setCreateUser(user.getPersonName());
            history.setDescription("登出成功");
            Object unitId = session.getAttribute(InternalAccount.UNIT_UNITID);
            Object unitName = session.getAttribute(InternalAccount.UNIT_UNITNAME);
            Object OrganName = session.getAttribute(InternalAccount.ORGAN_ORGANNAME);
            history.setUnitId(unitId == null ? null : (Long) unitId);
            history.setUnitName((String) unitName);
            history.setOrganName((String) OrganName);
            history.setLoginIp(user.getLastLoginIp());
            history.setLoginStatus(LoginHistoryEO.LoginStatus.Success.toString());
            history.setBrowser(BrowserUtils.checkBrowse(WebUtils.toHttp(request)));
            history.setOs(BrowserUtils.getClientOS(WebUtils.toHttp(request)));
            loginHistoryService.saveEntity(history);
        }
        return superPreHandle(request, response);
    }

    protected boolean superPreHandle(ServletRequest request, ServletResponse response) throws Exception {
        return super.preHandle(request, response);
    }

    @Override
    protected void issueRedirect(ServletRequest request, ServletResponse response, String redirectUrl) throws Exception {
        if (AjaxRequestUtil.isAjax(request)) {// 判断ajax请求
            ResultVO vo = new ResultVO();
            vo.setStatus(ResultVO.Status.SUCCESS.getValue());
            vo.setDesc("登出成功.");
            vo.setData(super.getRedirectUrl());
            AjaxRequestUtil.printAjax(response, vo);
        } else {
            super.issueRedirect(request, response, redirectUrl);
        }
    }
}