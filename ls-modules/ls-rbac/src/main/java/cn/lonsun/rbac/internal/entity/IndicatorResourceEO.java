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
 * 指示器与资源关联表
 *	 
 * @date     2014年8月27日  
 * @author 	 yy	 
 * @version	 v1.0 	 
 */
@Entity
@Table(name="rbac_indicator_resource")
public class IndicatorResourceEO extends AMockEntity {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO) 
    @Column(name="indicator_resource_id")
    private Long indicatorResourceId;
    @Column(name="indicator_id")
    private Long indicatorId;
    @Column(name="resource_id")
    private Long resourceId;

    public Long getIndicatorResourceId() {
        return indicatorResourceId;
    }

    public void setIndicatorResourceId(Long indicatorResourceId) {
        this.indicatorResourceId = indicatorResourceId;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

}
