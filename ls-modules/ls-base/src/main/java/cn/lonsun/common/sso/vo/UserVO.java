package cn.lonsun.common.sso.vo;

/**
 * 保存登陆后的信息
 *
 * @author xujh
 * @version 1.0
 * 2015年1月26日
 *
 */
public class UserVO {

	// 帐号ID
	private Long userId;
	// 帐号
	private String uid;
	// 人员ID
	private Long personId;
	// 人员姓名
	private String personName;
	// 所属非兼职组织ID
	private Long organId;
	// 所属非兼职组织名称
	private String organName;
	// 所属单位ID
	private Long unitId;
	// 所属单位名称
	private String unitName;
	// 政务邮箱
	private String coreMail;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Long getPersonId() {
		return personId;
	}
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public Long getOrganId() {
		return organId;
	}
	public void setOrganId(Long organId) {
		this.organId = organId;
	}
	public String getOrganName() {
		return organName;
	}
	public void setOrganName(String organName) {
		this.organName = organName;
	}
	public Long getUnitId() {
		return unitId;
	}
	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getCoreMail() {
		return coreMail;
	}
	public void setCoreMail(String coreMail) {
		this.coreMail = coreMail;
	}
}
