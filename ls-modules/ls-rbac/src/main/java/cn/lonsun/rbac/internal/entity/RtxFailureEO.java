package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * RTX同步记录，用于记录同步失败的内容，方便手动同步
 *
 * @author xujh
 * @version 1.0
 * 2015年4月22日
 *
 */
@Entity
@Table(name="rbac_rtx_sync")
public class RtxFailureEO extends ABaseEntity {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -461497357663788312L;
	
	/**
	 * 同步的节点类型
	 */
	
	public enum Type{
		Organ,//组织
		Person//人员
	}
	/**
	 * 操作类型，即进行什么操作时同步RTX失败
	 */
	public enum Operation{
		Save,//新增
		Update,//修改
		Delete//删除
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="id")
	private Long id;
	//同步节点类型，分为Person和Organ
	@Column(name="type")
	private String type;
	//单位或部门ID，与人员ID互斥存在
	@Column(name="organ_id")
	private Long organId;
	//人员ID，与单位或部门ID互斥存在
	@Column(name="person_id")
	private Long personId;
	//账号对应密码，因为密码md5加密存入数据库，而rtx同步需要明文，所以如果同步失败，那么需要保存明文密码以备手动同步使用
	@Column(name="password")
	private String password;
	//操作,增、删、改
	@Column(name="operation")
	private String operation;
	//失败描述信息
	@Column(name="description")
	private String description;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
//	public String getUid() {
//		return uid;
//	}
//	public void setUid(String uid) {
//		this.uid = uid;
//	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
