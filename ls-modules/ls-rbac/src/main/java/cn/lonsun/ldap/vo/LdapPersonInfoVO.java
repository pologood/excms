package cn.lonsun.ldap.vo;

/**
 * LDAP中人员信息
 *
 * @author xujh
 * @version 1.0
 * 2015年5月16日
 *
 */
public class LdapPersonInfoVO {
	//姓名
	private String name;
	// 职务，多个职务通过逗号","隔开
	private String positions;
	// 移动电话
	private String mobile;
	// 办公电话
	private String officePhone;
	// 办公地址
	private String officeAddress;
	// 邮件
	private String mail;
	// 邮件-用于文件直传，对用户不可见
	private String mailSendFiles;
	// 是否兼职用户，每加入一个单位都添加一条新的记录
	private Boolean isPluralistic = false;// 0表示非兼职，1表示兼职
	// 源personDn
	private Long srcPersonDn;
	// 姓名全拼
	private String fullPy;
	// 姓名简拼
	private String simplePy;
	// 排序编码-用于查询排序
	private Long sortNum = Long.valueOf(0);
	// 用户在LDAP上的DN
	private String dn;
	// 账号
	private String uid;
	// 账号状态
	private String status;
	// 平台编码，与isExternalPerson组合使用
	private String platformCode;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPositions() {
		return positions;
	}
	public void setPositions(String positions) {
		this.positions = positions;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOfficePhone() {
		return officePhone;
	}
	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
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
	public String getMailSendFiles() {
		return mailSendFiles;
	}
	public void setMailSendFiles(String mailSendFiles) {
		this.mailSendFiles = mailSendFiles;
	}
	public Boolean getIsPluralistic() {
		return isPluralistic;
	}
	public void setIsPluralistic(Boolean isPluralistic) {
		this.isPluralistic = isPluralistic;
	}
	public Long getSrcPersonDn() {
		return srcPersonDn;
	}
	public void setSrcPersonDn(Long srcPersonDn) {
		this.srcPersonDn = srcPersonDn;
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
	public Long getSortNum() {
		return sortNum;
	}
	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPlatformCode() {
		return platformCode;
	}
	public void setPlatformCode(String platformCode) {
		this.platformCode = platformCode;
	}
}
