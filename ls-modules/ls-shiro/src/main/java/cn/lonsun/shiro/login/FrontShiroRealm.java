/*
 * FrontShiroRealm.java         2016年6月22日 <br/>
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

import javax.annotation.Resource;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.shiro.security.FrontToken;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;

/**
 * 生成静态验证 <br/>
 *
 * @date 2016年6月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class FrontShiroRealm extends AuthorizingRealm {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(FrontShiroRealm.class);

    @Resource
    private IMemberService memberService;

    /**
     * 为当前登录的Subject授予角色和权限
     * 
     * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        // UserEO userEO = (UserEO) super.getAvailablePrincipal(principal);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(null);
        authorizationInfo.addStringPermissions(null);
        return authorizationInfo;
    }

    /**
     * 用户验证
     * 
     * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        FrontToken frontToken = (FrontToken) authcToken;
        logger.debug("验证当前Subject时获取到token：" + ReflectionToStringBuilder.toString(frontToken, ToStringStyle.MULTI_LINE_STYLE));
        MemberEO member = frontToken.getMember(memberService);
        if (null == member) {
            throw new UnknownAccountException("用户名不存在！");
        }
        Integer status = member.getStatus();
        if (status == null || MemberEO.Status.Unable.getStatus().equals(status)) {
            throw new LockedAccountException("您的账号已被锁定，请联系管理员！"); // 帐号锁定
        }
        // 身份、凭据
        return new SimpleAuthenticationInfo(member, frontToken.getCredentials(member), this.getName());
    }
}