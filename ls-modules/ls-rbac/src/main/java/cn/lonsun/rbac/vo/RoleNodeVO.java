/*
 * RoleNodeVO.java         2014年9月15日 <br/>
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

package cn.lonsun.rbac.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.core.base.entity.AMockEntity;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 公共、系统角色数查询
 *	 
 * @date     2014年9月15日 
 * @author 	 yy 
 * @version	 v1.0 
 */
public class RoleNodeVO implements Serializable {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2367065873576167670L;
    private Long id;
    private Long pid;
    private Boolean isParent;
    private Integer isPredefine;
    private Boolean hasChildren;
    
    public enum Type{
        System,//系统级的，系统管理独有
        Public,//共用的
        Private//私有的-一般属于某个部门
    }
    private Long roleId;
    //角色类型
    private String type = Type.Public.toString();
    //角色编码，需要预定义，不可随意修改
    private String code;
    //角色名称，系统角色名称不能重复、公用角色名称不能重复，同单位下角色名称不能重复
    private String name;
    //角色所属组织或部门，只有这些组织或部门的用户才能拥有该角色
    private Long organId;
    private Long businessTypeId;
    //角色描述
    private String description;
    
    private String recordStatus = AMockEntity.RecordStatus.Normal.toString(); //记录状态，正常：Normal,已删除:Removed
    private Long createUserId;//创建人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createDate = new Date();//创建时间
    private Long updateUserId;//更新人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date updateDate = new Date();//更新时间
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getPid() {
        return pid;
    }
    public void setPid(Long pid) {
        this.pid = pid;
    }
    public boolean getIsParent() {
        return isParent;
    }
    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }
    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public Long getOrganId() {
        return organId;
    }
    public void setOrganId(Long organId) {
        this.organId = organId;
    }
    public Long getBusinessTypeId() {
        return businessTypeId;
    }
    public void setBusinessTypeId(Long businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getRecordStatus() {
        return recordStatus;
    }
    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
    public Long getCreateUserId() {
        return createUserId;
    }
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Long getUpdateUserId() {
        return updateUserId;
    }
    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    public Boolean getHasChildren() {
        return hasChildren;
    }
    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
	public Integer getIsPredefine() {
		return isPredefine;
	}
	public void setIsPredefine(Integer isPredefine) {
		this.isPredefine = isPredefine;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	
	@Override
	public boolean equals(Object obj) {
		//如果roleId有值，且相同，那么认为他们equals
		boolean flag = false;
		if(obj!=null&&obj instanceof RoleNodeVO){
			RoleNodeVO node = (RoleNodeVO)obj;
			Long roleId = node.getRoleId();
			if(roleId!=null){
				if(getRoleId().longValue()==roleId.longValue()){
					flag = true;
				}else{
					flag = false;
				}
			}
		}
		return flag;
	}
	
}

