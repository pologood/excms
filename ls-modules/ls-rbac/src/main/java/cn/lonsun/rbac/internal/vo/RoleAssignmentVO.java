package cn.lonsun.rbac.internal.vo;

/**
 * 角色赋予VO
 *
 * @author xujh
 * @version 1.0
 * 2015年2月2日
 *
 */
public class RoleAssignmentVO {
	
	private Long userId;
	
	private Long organId;
	
	private Long roleId;
	
	private String personName;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}
	
}
