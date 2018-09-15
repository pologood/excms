package cn.lonsun.ldap.vo;

/**
 * LDAP中内置人员VO
 *
 * @author xujh
 * @version 1.0
 * 2015年6月12日
 *
 */
public class InetOrgPersonVO {
	
	private String name;
	
	private String platformCode;
	
	private Long sortNum;
	
	private String positions;
	
	private Boolean isPluralistic;
	
	private String officePhone;
	
	private String mobile;
	
	private String officeAddress;
	
	private String mail;
	
	private String fullPy;
	
	private String simplePy;
	
	private String status;
	
	private String uid;
	
	private String md5Password;
	
	private String desPassword;
	
	private String dn;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlatformCode() {
		return platformCode;
	}

	public void setPlatformCode(String platformCode) {
		this.platformCode = platformCode;
	}

	public Long getSortNum() {
		return sortNum;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public Boolean getIsPluralistic() {
		return isPluralistic;
	}

	public void setIsPluralistic(Boolean isPluralistic) {
		this.isPluralistic = isPluralistic;
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

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFullPy() {
		return fullPy;
	}

	public void setFullPy(String fullPy) {
		this.fullPy = fullPy;
	}

	public String getSimplePy() {
		return simplePy;
	}

	public void setSimplePy(String simplePy) {
		this.simplePy = simplePy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMd5Password() {
		return md5Password;
	}

	public void setMd5Password(String md5Password) {
		this.md5Password = md5Password;
	}

	public String getDesPassword() {
		return desPassword;
	}

	public void setDesPassword(String desPassword) {
		this.desPassword = desPassword;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}
}
