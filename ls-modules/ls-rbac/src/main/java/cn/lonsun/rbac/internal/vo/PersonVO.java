package cn.lonsun.rbac.internal.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 用户分页显示对象
 *
 * @author xujh
 * @version 1.0
 * 2015年2月10日
 *
 */
public class PersonVO {
	private Long personId;
	
	private String name;

	private String organName;
	
	private String uid;
	
	private Integer loginType;
	
	private Integer loginTimes;
	
	private Boolean isPluralistic;
	
	private Long srcPersonId;
	
	private String lastLoginIp;

	private String status;

	private Long userId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date lastLoginDate;
	
	private Long sortNum;
	
	private Long organId;
	
	private Long unitId;
	
	private String unitName;
	
	private String positions;
	private String officePhone;
	private String mobile;
	private String mail;
	private String officeAddress;
	
	private String dn;

	private String fullOranName;
	
	private Boolean delLevel = true;
	
	
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

	public String getOrganName() {
		return organName;
	}

	public void setOrganName(String organName) {
		this.organName = organName;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public Integer getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(Integer loginTimes) {
		this.loginTimes = loginTimes;
	}

	public Boolean getIsPluralistic() {
		return isPluralistic;
	}

	public void setIsPluralistic(Boolean isPluralistic) {
		this.isPluralistic = isPluralistic;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Long getSortNum() {
		return sortNum;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
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

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public Long getSrcPersonId() {
		return srcPersonId;
	}

	public void setSrcPersonId(Long srcPersonId) {
		this.srcPersonId = srcPersonId;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getFullOranName() {
		return fullOranName;
	}

	public void setFullOranName(String fullOranName) {
		this.fullOranName = fullOranName;
	}

	public Boolean getDelLevel() {
		return delLevel;
	}

	public void setDelLevel(Boolean delLevel) {
		this.delLevel = delLevel;
	}


	
}
