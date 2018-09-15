/*
 * IndicatorPeermissionEO.java         2014年8月27日 <br/>
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

package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;


/**
 * 角色权限表
 *	 
 * @date     2014年8月27日 
 * @author 	 yy 
 * @version	 v1.0 
 */
@Entity
@Table(name="rbac_indicator_permission")
public class IndicatorPermissionEO extends AMockEntity {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="indicator_permission_id")
    private Long indicatorPermissionId;
    @Column(name="indicator_id")
    private Long indicatorId;
    @Column(name="indicator_type")
    private String indicatorType;
    @Column(name="role_id")
    private Long roleId;
    @Column(name="SITE_ID")
    private Long siteId;

    public Long getIndicatorPermissionId() {
        return indicatorPermissionId;
    }

    public void setIndicatorPermissionId(Long indicatorPermissionId) {
        this.indicatorPermissionId = indicatorPermissionId;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

	public String getIndicatorType() {
		return indicatorType;
	}

	public void setIndicatorType(String indicatorType) {
		this.indicatorType = indicatorType;
	}

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
