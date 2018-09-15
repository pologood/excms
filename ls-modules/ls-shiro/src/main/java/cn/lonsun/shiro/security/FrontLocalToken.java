/*
 * FrontLocalToken.java         2016年6月22日 <br/>
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

import java.util.HashMap;
import java.util.Map;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;

/**
 * 本平台登录令牌 <br/>
 *
 * @date 2016年6月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class FrontLocalToken extends FrontToken {
    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    private String uid;

    public FrontLocalToken(String uid, String password, Long siteId, String bduserid, String bdtype) {
        super(siteId, bduserid, bdtype);
        this.uid = uid;
        this.setUsername(uid);
        this.setPassword(password != null ? password.toCharArray() : null);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public MemberEO getMember(IMemberService memberService) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", this.getUid());
        params.put("siteId", this.getSiteId());
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return memberService.getEntity(MemberEO.class, params);
    }

    @Override
    public Object getCredentials(MemberEO memberEO) {
        return memberEO.getPassword();
    }
}