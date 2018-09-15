/*
 * FrontToken.java         2016年6月22日 <br/>
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

package cn.lonsun.shiro.security;

import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;

/**
 * 生成静态令牌 <br/>
 *
 * @date 2016年6月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public abstract class FrontToken extends org.apache.shiro.authc.UsernamePasswordToken {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    private Long siteId;
    private String bduserid;
    private String bdtype;

    public FrontToken(Long siteId, String bduserid, String bdtype) {
        this.siteId = siteId;
        this.bduserid = bduserid;
        this.bdtype = bdtype;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getBduserid() {
        return bduserid;
    }

    public void setBduserid(String bduserid) {
        this.bduserid = bduserid;
    }

    public String getBdtype() {
        return bdtype;
    }

    public void setBdtype(String bdtype) {
        this.bdtype = bdtype;
    }

    /**
     * 获取会员信息
     *
     * @author fangtinghua
     * @param memberService
     * @return
     */
    public abstract MemberEO getMember(IMemberService memberService);

    /**
     * 获取会员凭据
     *
     * @author fangtinghua
     * @param memberEO
     * @return
     */
    public abstract Object getCredentials(MemberEO memberEO);
}