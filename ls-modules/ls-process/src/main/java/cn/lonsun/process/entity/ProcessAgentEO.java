package cn.lonsun.process.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 流程代理EO
 */
@Entity
@Table(name="FORM_PROCESS_AGENT")
public class ProcessAgentEO extends ABaseEntity implements Serializable{


	private static final long serialVersionUID = -1288290411888419391L;

	/** 主键 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="AGENT_ID")
	private Long agentId;

	/** 被代填人用户ID */
	@Column(name="BE_AGENT_USER_ID")
	private Long beAgentUserId;

	/** 被代填人部门ID */
	@Column(name="BE_AGENT_ORGAN_ID")
	private Long beAgentOrganId;

	/** 被代填人姓名 */
	@Column(name="BE_AGENT_PERSON_NAME")
	private String beAgentPersonName;

	/** 被代填人部门名称 */
	@Column(name="BE_AGENT_ORGAN_NAME")
	private String beAgentOrganName;

	/** 代填人userIds */
	@Column(name="AGENT_USER_IDS")
	private String agentUserIds;

	/** 代填人部门IDS */
	@Column(name="AGENT_ORGAN_IDS")
	private String agentOrganIds;

	/** 代填人姓名S */
	@Column(name="AGENT_PERSON_NAMES")
	private String agentPersonNames;

	/** 代填人部门名称S */
	@Column(name="AGENT_ORGAN_NAMES")
	private String agentOrganNames;

	/** 创建单位ID */
	@Column(name="CREATE_UNIT_ID")
	private Long createUnitId;

	/** 流程模块编码 */
	@Column(name="MODULE_CODE")
	private String moduleCode;


	public void setAgentId(Long AgentId){
		this.agentId = AgentId;
	}

	public Long getAgentId(){
		 return agentId;
	}

	public void setBeAgentUserId(Long BeAgentUserId){
		this.beAgentUserId = BeAgentUserId;
	}

	public Long getBeAgentUserId(){
		 return beAgentUserId;
	}

	public void setBeAgentOrganId(Long BeAgentOrganId){
		this.beAgentOrganId = BeAgentOrganId;
	}

	public Long getBeAgentOrganId(){
		 return beAgentOrganId;
	}

	public void setAgentUserIds(String AgentUserIds){
		this.agentUserIds = AgentUserIds;
	}

	public String getAgentUserIds(){
		 return agentUserIds;
	}

	public void setAgentOrganIds(String AgentOrganIds){
		this.agentOrganIds = AgentOrganIds;
	}

	public String getAgentOrganIds(){
		 return agentOrganIds;
	}

	public void setAgentPersonNames(String AgentPersonNames){
		this.agentPersonNames = AgentPersonNames;
	}

	public String getAgentPersonNames(){
		 return agentPersonNames;
	}


	public Long getCreateUnitId() {
		return createUnitId;
	}

	public void setCreateUnitId(Long createUnitId) {
		this.createUnitId = createUnitId;
	}

	public String getBeAgentPersonName() {
		return beAgentPersonName;
	}

	public void setBeAgentPersonName(String beAgentPersonName) {
		this.beAgentPersonName = beAgentPersonName;
	}

	public String getBeAgentOrganName() {
		return beAgentOrganName;
	}

	public void setBeAgentOrganName(String beAgentOrganName) {
		this.beAgentOrganName = beAgentOrganName;
	}

	public String getAgentOrganNames() {
		return agentOrganNames;
	}

	public void setAgentOrganNames(String agentOrganNames) {
		this.agentOrganNames = agentOrganNames;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
}
