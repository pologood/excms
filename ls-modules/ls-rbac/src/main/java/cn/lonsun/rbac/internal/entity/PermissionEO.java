package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 权限
 * 
 * @author xujh
 * 
 */
@Entity
@Table(name = "RBAC_PERMISSION")
public class PermissionEO extends AMockEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 887038364193211201L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "permission_id")
	private Long permissionId;
	@Column(name = "resource_id")
	private Long resourceId;
	// 资源
	@Column(name = "uri")
	private String uri;
	@Column(name = "role_id")
	private Long roleId;

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
