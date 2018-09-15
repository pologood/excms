package cn.lonsun.rbac.vo;

import java.util.Date;

import cn.lonsun.rbac.internal.entity.UserEO.STATUS;

public class UserVO {
	
	private Long userId;
	//用户名
	private String uid;
	//密码
	private String password;
	//登录类型，分为3类：用户名和密码登录(0)、动态令牌登录(1)、用户名和密码或动态令牌登录(2)
	private Integer loginType = Integer.valueOf(0);
	//账号状态Enabled,Unable
	private String status = STATUS.Enabled.toString();
	//移动端标识编码
	private String mobileCode;
	//是否支持移动端登录
	private Integer supportMobile = Integer.valueOf(0);
	//登录次数
	private Integer loginTimes = 0;
	//最后登录时间
	private Date lastLoginDate;
	//最后登录IP
	private String lastLoginIp;
	//运行访问的合法Ip，运行通配符"*"，多个IP通过逗号","隔开，如果为空，那么允许所有IP访问
	private String legalIps;
	//对应person在LDAP中的DN
	private String personDn;
	//用户所属人员的姓名
	private String personName;

	private Long organId;
	
	private String organName;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMobileCode() {
		return mobileCode;
	}

	public void setMobileCode(String mobileCode) {
		this.mobileCode = mobileCode;
	}

	public Integer getSupportMobile() {
		return supportMobile;
	}

	public void setSupportMobile(Integer supportMobile) {
		this.supportMobile = supportMobile;
	}

	public Integer getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(Integer loginTimes) {
		this.loginTimes = loginTimes;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public String getLegalIps() {
		return legalIps;
	}

	public void setLegalIps(String legalIps) {
		this.legalIps = legalIps;
	}

	public String getPersonDn() {
		return personDn;
	}

	public void setPersonDn(String personDn) {
		this.personDn = personDn;
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
	
}
