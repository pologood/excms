package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 人员上下级关系
 * 
 * @author xujh
 * @date 2014年10月24日 上午10:09:19
 * @version V1.0
 */
@Entity
@Table(name = "rbac_relationship")
public class RelationshipEO extends ABaseEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6524441282164555675L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "relationship_id")
	private Long relationshipId;
	// personId
	@Column(name = "person_id")
	private Long personId;
	// 姓名
	@Column(name = "name_")
	private String name;
	// 用户所属组织ID
	@Column(name = "organ_id")
	private Long organId;
	@Transient
	private String organName;
	//单位ID
	@Column(name = "unit_id")
	private Long unitId;
	@Transient
	private String unitName;
	// 用户ID
	@Column(name = "user_id")
	private Long userId;
	// 领导personId
	@Column(name = "leader_person_id")
	private Long leaderPersonId;
	// 领导姓名
	@Column(name = "leader_name")
	private String leaderName;
	// 领导所属组织ID
	@Column(name = "leader_organ_id")
	private Long leaderOrganId;
	//下属数量
	@Column(name = "subordinate_count")
	private Integer subordinateCount = Integer.valueOf(0);

	public Long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(Long relationshipId) {
		this.relationshipId = relationshipId;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getLeaderPersonId() {
		return leaderPersonId;
	}

	public void setLeaderPersonId(Long leaderPersonId) {
		this.leaderPersonId = leaderPersonId;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public Long getLeaderOrganId() {
		return leaderOrganId;
	}

	public void setLeaderOrganId(Long leaderOrganId) {
		this.leaderOrganId = leaderOrganId;
	}

	public Integer getSubordinateCount() {
		return subordinateCount;
	}

	public void setSubordinateCount(Integer subordinateCount) {
		this.subordinateCount = subordinateCount;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
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
}
