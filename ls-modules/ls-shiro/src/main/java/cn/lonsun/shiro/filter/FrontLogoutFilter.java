/*
 * FrontLogoutFilter.java         2016年6月23日 <br/>
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

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import org.apache.shiro.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 登出 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月23日 <br/>
 */
public class FrontLogoutFilter extends MgrLogoutFilter {
    @Resource
    private IMemberService memberService;

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //Subject currentUser = SecurityUtils.getSubject();
        //MemberEO member = (MemberEO) currentUser.getPrincipal();
        String memberId = WebUtils.getCleanParam(request, "memberId");
        if (!AppUtil.isEmpty(memberId)) {
            MemberEO member = memberService.getEntity(MemberEO.class, Long.valueOf(memberId));
            member.setBduserid("");
            memberService.updateEntity(member);
        }
        return super.superPreHandle(request, response);
    }
}