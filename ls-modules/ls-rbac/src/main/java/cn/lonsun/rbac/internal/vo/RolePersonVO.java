package cn.lonsun.rbac.internal.vo;

import java.util.Date;

/**
 * 角色绑定用户的VO
 *
 * @author xujh
 * @version 1.0
 * 2015年2月12日
 *
 */
public class RolePersonVO {
	
	private Long roleAssignmentId;
	
	private String name;
	
	private String uid;
	
	private String organName;
	
	private String unitName;
	
	private Date createDate;

	public Long getRoleAssignmentId() {
		return roleAssignmentId;
	}

	public void setRoleAssignmentId(Long roleAssignmentId) {
		this.roleAssignmentId = roleAssignmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getOrganName() {
		return organName;
	}

	public void setOrganName(String organName) {
		this.organName = organName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
