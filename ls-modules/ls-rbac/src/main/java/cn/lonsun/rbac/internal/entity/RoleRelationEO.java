package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 角色关联关系
 *
 * @author xujh
 * @version 1.0
 * 2015年5月20日
 *
 */
@Entity
@Table(name="RBAC_ROLE_RELATION")
public class RoleRelationEO extends AMockEntity {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1104788618757338405L;

	/**
	 * 角色关系类型
	 *
	 * @author xujh
	 * @version 1.0
	 * 2015年5月20日
	 *
	 */
	public enum Type{
		Extend,//扩展-继承关系
		Manage//管理-允许赋予其他用户
	}
	//主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	//源角色ID-用户拥有的角色ID
	@Column(name = "ROLE_ID")
	private Long roleId;
	//目标角色ID-对roleId角色进行拓展的角色ID
	@Column(name = "TARGET_ROLE_ID")
	private Long targetRoleId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getTargetRoleId() {
		return targetRoleId;
	}

	public void setTargetRoleId(Long targetRoleId) {
		this.targetRoleId = targetRoleId;
	}
}
