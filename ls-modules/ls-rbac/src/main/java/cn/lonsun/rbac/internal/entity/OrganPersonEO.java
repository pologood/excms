package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 组织/单位与用户的中间表，方便按组织对用户进行查询
 * @author Administrator
 *
 */
@Entity
@Table(name="rbac_organ_person")
public class OrganPersonEO extends AMockEntity {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6873704507173509821L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="organ_person_id")
	private Long organPersonId;
	
	@Column(name="organ_id")
	private Long organId;
	
	@Column(name="organ_dn")
	private String organDn;
	
	@Column(name="person_id")
	private Long personId;

	public Long getOrganPersonId() {
		return organPersonId;
	}

	public void setOrganPersonId(Long organPersonId) {
		this.organPersonId = organPersonId;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getOrganDn() {
		return organDn;
	}

	public void setOrganDn(String organDn) {
		this.organDn = organDn;
	}
}