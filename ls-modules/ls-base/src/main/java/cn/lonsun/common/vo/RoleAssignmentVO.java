package cn.lonsun.common.vo;

/**
 * 角色赋予关系
 *
 * @author xujh
 * @version 1.0
 * 2015年1月30日
 *
 */
public class RoleAssignmentVO {
	
	private Long roleAssignmentId;
	//部门直属单位ID-角色被赋予者直属的单位ID
	private Long unitId;
	
	private String unitName;
	//角色所属部门/处室ID--角色被赋予者直属的部门ID
	private Long organId;
	
	private String organName;
	//角色所属用于ID
	private Long userId;
	
	private String personName;
	
	private String mobile;
	//角色ID
	private Long roleId;
	//角色名称
	private String roleName;
	//角色编码
	private String roleCode;
	//角色类型
	private String roleType;
	public Long getRoleAssignmentId() {
		return roleAssignmentId;
	}
	public void setRoleAssignmentId(Long roleAssignmentId) {
		this.roleAssignmentId = roleAssignmentId;
	}
	public Long getUnitId() {
		return unitId;
	}
	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}
	public Long getOrganId() {
		return organId;
	}
	public void setOrganId(Long organId) {
		this.organId = organId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getOrganName() {
		return organName;
	}
	public void setOrganName(String organName) {
		this.organName = organName;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
