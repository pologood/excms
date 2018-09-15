package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 组织单位设置编码，此编码为某些专门针对某些部门的应用服务，且为了不让客户能够进行修改，放置在开发商管理中
 * 
 * @author xujh
 * @version 1.0 2015年2月13日
 * 
 */
@Entity
@Table(name = "rbac_organ_code")
public class OrganCodeEO extends ABaseEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -53249925785365419L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ORGAN_CODE_ID")
	private Long organCodeId;
	@Column(name = "ORGAN_ID")
	private Long organId;
	@Column(name = "CODE")
	private String code;

	public Long getOrganCodeId() {
		return organCodeId;
	}

	public void setOrganCodeId(Long organCodeId) {
		this.organCodeId = organCodeId;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
