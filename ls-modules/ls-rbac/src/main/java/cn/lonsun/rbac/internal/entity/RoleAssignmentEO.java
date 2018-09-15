package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 角色权限
 * 
 * @author xujh
 * 
 */
@Entity
@Table(name = "rbac_role_Assignment")
public class RoleAssignmentEO extends AMockEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1456471802719010521L;
	// 主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "role_assignment_id")
	private Long roleAssignmentId;
	// 部门直属单位ID-角色被赋予者直属的单位ID
	@Column(name = "unit_id")
	private Long unitId;
	// 角色所属部门/处室ID--角色被赋予者直属的部门ID
	@Column(name = "organ_id")
	private Long organId;
	// 角色所属用于ID
	@Column(name = "user_id")
	private Long userId;
	// 角色ID
	@Column(name = "role_id")
	private Long roleId;
	// 角色名称
	@Column(name = "role_name")
	private String roleName;
	// 角色编码
	@Column(name = "role_code")
	private String roleCode;
	// 角色类型
	@Column(name = "role_type")
	private String roleType;

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

	public Long getRoleAssignmentId() {
		return roleAssignmentId;
	}

	public void setRoleAssignmentId(Long roleAssignmentId) {
		this.roleAssignmentId = roleAssignmentId;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	@Override
	public boolean equals(Object obj) {
		RoleAssignmentEO r = (RoleAssignmentEO) obj;
		boolean isEquals = true;
		if (r.getRoleAssignmentId() != null
				&& getRoleAssignmentId() != null
				&& r.getRoleAssignmentId().longValue() != getRoleAssignmentId().longValue()) {
			isEquals = false;
		} else {
			if (isEquals && r.getOrganId()!=null&&getOrganId()!=null&&r.getOrganId().longValue() != r.getOrganId().longValue()) {
				isEquals = false;
			}
			if (isEquals && r.getUserId()!=null&&getUserId()!=null&&r.getUserId().longValue() != getUserId().longValue()) {
				isEquals = false;
			}
			if (isEquals && r.getRoleId()!=null&&getRoleId()!=null&&r.getRoleId().longValue() != getRoleId().longValue()) {
				isEquals = false;
			}
			if (isEquals && r.getUnitId()!=null&&getUnitId()!=null&&r.getUnitId().longValue() != getUnitId().longValue()) {
				isEquals = false;
			}
			if (isEquals &&r.getRoleCode()!=null&& !r.getRoleCode().equals(getRoleCode())) {
				isEquals = false;
			}
			if (isEquals &&r.getRoleType()!=null&& !r.getRoleType().equals(getRoleType())) {
				isEquals = false;
			}
			if (isEquals && r.getRoleName()!=null&&!r.getRoleName().equals(getRoleName())) {
				isEquals = false;
			}
		}
		return isEquals;
	}
}
