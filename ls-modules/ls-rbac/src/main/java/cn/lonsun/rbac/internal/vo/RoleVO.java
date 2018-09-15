/*
 * RoleVO.java         2014年9月11日 <br/>
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

package cn.lonsun.rbac.internal.vo;

import java.io.Serializable;
import java.util.List;

import cn.lonsun.api.rbac.vo.PersonInfoVO;
import cn.lonsun.indicator.internal.vo.IndicatorVO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.type.internal.entity.BusinessTypeEO;

/**
 * 角色VO
 *	 
 * @date     2014年9月11日 
 * @author 	 yy 
 * @version	 v1.0 
 */
public class RoleVO implements Serializable {

    /**
     * serialVersionUID:
     */
    private static final long serialVersionUID = 2792217962869927110L;

    private RoleEO info = new RoleEO();
    
    private String typeName;
    
    private List<BusinessTypeEO> businessTypes;
    
    private List<PersonInfoVO> personInfos;
    // 权限
    private List<IndicatorVO> tree;
    public RoleEO getInfo() {
        return info;
    }
    public void setInfo(RoleEO info) {
        this.info = info;
    }
    public List<IndicatorVO> getTree() {
        return tree;
    }
    public void setTree(List<IndicatorVO> tree) {
        this.tree = tree;
    }
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public List<BusinessTypeEO> getBusinessTypes() {
		return businessTypes;
	}
	public void setBusinessTypes(List<BusinessTypeEO> businessTypes) {
		this.businessTypes = businessTypes;
	}
	public List<PersonInfoVO> getPersonInfos() {
		return personInfos;
	}
	public void setPersonInfos(List<PersonInfoVO> personInfos) {
		this.personInfos = personInfos;
	}
}

