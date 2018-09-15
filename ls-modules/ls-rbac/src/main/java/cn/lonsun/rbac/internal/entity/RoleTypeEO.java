package cn.lonsun.rbac.internal.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 角色分组关联实体
 * @author xujh
 *
 */
@Entity
@Table(name="rbac_role_type")
public class RoleTypeEO extends AMockEntity {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4267254011080687685L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	private Long roleTypeId;
	
	private Long roleId;
	
	private Long businessTypeId;
	
	public Long getRoleTypeId() {
		return roleTypeId;
	}

	public void setRoleTypeId(Long roleTypeId) {
		this.roleTypeId = roleTypeId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getBusinessTypeId() {
		return businessTypeId;
	}

	public void setBusinessTypeId(Long businessTypeId) {
		this.businessTypeId = businessTypeId;
	}

}
