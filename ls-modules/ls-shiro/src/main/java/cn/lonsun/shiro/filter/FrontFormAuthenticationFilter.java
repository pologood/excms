/*
 * FrontAuthFilter.java         2016年6月22日 <br/>
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

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import cn.lonsun.core.util.ResultVO;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.shiro.security.FrontLocalToken;
import cn.lonsun.shiro.security.FrontOtherToken;
import cn.lonsun.shiro.security.FrontToken;
import cn.lonsun.shiro.util.AjaxRequestUtil;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;

/**
 * 生成静态验证 <br/>
 *
 * @date 2016年6月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class FrontFormAuthenticationFilter extends FormAuthenticationFilter {
    @Resource
    private IMemberService memberService;

    /**
     * 允许访问
     * 
     * @see org.apache.shiro.web.filter.authc.AuthenticatingFilter#isAccessAllowed(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, java.lang.Object)
     */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 如果配置不需要登录，则直接返回true
        return super.isAccessAllowed(request, response, mappedValue);
    }

    /**
     * 手机端请求全部返回ajax
     * 
     * @see cn.lonsun.shiro.filter.FormAuthenticationFilter#saveRequestAndRedirectToLogin(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse)
     */
    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        this.setTimeout(response);
        ResultVO vo = new ResultVO();
        vo.setStatus(ResultVO.Status.Timeout.getValue());
        vo.setDesc("登录超时！");
        AjaxRequestUtil.printAjax(response, vo);
    }

    /**
     * 创建令牌
     * 
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#createToken(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse)
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String requestURI = getPathWithinApplication(request);
        if ("/mobile/checkLogin".equals(requestURI)) {// 本平台登录
            String uid = WebUtils.getCleanParam(request, "uid");
            String password = getPassword(request);
            Long siteId = NumberUtils.toLong(WebUtils.getCleanParam(request, "siteId"));
            String bduserid = WebUtils.getCleanParam(request, "bduserid");
            String bdtype = WebUtils.getCleanParam(request, "bdtype");
            return new FrontLocalToken(uid, DigestUtils.md5Hex(password), siteId, bduserid, bdtype);
        } else if ("/mobile/checkPlatform".equals(requestURI)) {// 第三方登录
            String openType = WebUtils.getCleanParam(request, "openType");
            String openId = WebUtils.getCleanParam(request, "openId");
            Long siteId = NumberUtils.toLong(WebUtils.getCleanParam(request, "siteId"));
            String bduserid = WebUtils.getCleanParam(request, "bduserid");
            String bdtype = WebUtils.getCleanParam(request, "bdtype");
            return new FrontOtherToken(openType, openId, siteId, bduserid, bdtype);
        }
        return super.createToken(request, response);
    }

    /**
     * 登录成功设置session属性
     * 
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginSuccess(org.apache.shiro.authc.AuthenticationToken,
     *      org.apache.shiro.subject.Subject, javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse)
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        FrontToken frontToken = (FrontToken) token;
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        MemberEO member = (MemberEO) currentUser.getPrincipal();// 会员
        // 设置会员id
        session.setAttribute(InternalAccount.USER_USERID, member.getId());
        MemberSessionVO sessionMember = new MemberSessionVO();
        BeanUtils.copyProperties(sessionMember, member);
        session.setAttribute(InternalAccount.MEMBER, sessionMember);
        // 设置最后登录ip 和 最后登录时间
        Object ip = session.getAttribute(InternalAccount.MDC_IP);
        member.setIp(null == ip ? "" : ip.toString());
        member.setBduserid(frontToken.getBduserid());
        member.setBdtype(frontToken.getBdtype());
        member.setLastLoginDate(new Date());
        memberService.updateEntity(member);
        // 踢出已经登录用户
        super.kickoutUser();
        // 判断ajax请求
        if (AjaxRequestUtil.isAjax(request)) {
            ResultVO vo = new ResultVO();
            vo.setStatus(ResultVO.Status.SUCCESS.getValue());
            vo.setDesc("登录成功.");
            vo.setData(member);
            return AjaxRequestUtil.printAjax(response, vo);
        }
        return super.onLoginSuccess(frontToken, subject, request, response);
    }

    /**
     * 重写方法，不区分post或者get请求
     * 
     * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#isLoginSubmission(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse)
     */
    @Override
    protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
        return true;
    }
}