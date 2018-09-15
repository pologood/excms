package cn.lonsun.rbac.internal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 用户
 * 
 * @author xujh
 * 
 */
@Entity
@Table(name = "rbac_user")
public class UserEO extends AMockEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4113274734680336448L;

    /**
     * 账号的状态
     * 
     * @author xujh
     * 
     */
    public enum STATUS {
        Enabled, // 可用
        Unable, // 禁用
        External// 外平台用户状态
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long userId;
    // 用户名
    @Column(name = "UID_")
    private String uid;
    // MD5密码
    @Column(name = "PASSWORD_")
    private String password;
    // 对称加密密码
    @Column(name = "DES_PASSWORD")
    private String desPassword;
    // 登录类型，分为3类：用户名和密码登录(0)、动态令牌登录(1)、用户名和密码或动态令牌登录(2)
    @Column(name = "LOGIN_TYPE")
    private Integer loginType = Integer.valueOf(0);
    // 令牌号
    @Column(name = "TOKEN_NUM")
    private String tokenNum;
    // 账号状态Enabled,Unable
    @Column(name = "STATUS")
    private String status = STATUS.Enabled.toString();
    // 移动端标识编码
    @Column(name = "MOBILE_CODE")
    private String mobileCode;
    // 是否支持移动端登录
    @Column(name = "SUPPORT_MOBILE")
    private Boolean isSupportMobile = Boolean.TRUE;
    // 登录次数
    @Column(name = "LOGIN_TIMES")
    private Integer loginTimes = 0;
    // 最后登录时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_LOGIN_DATE")
    private Date lastLoginDate;
    // 最后登录IP
    @Column(name = "LAST_LOGIN_IP")
    private String lastLoginIp;
    // 运行访问的合法Ip，运行通配符"*"，多个IP通过逗号","隔开，如果为空，那么允许所有IP访问
    @Column(name = "LEGAL_IPS")
    private String legalIps;
    // 禁止IP访问
    @Column(name = "FORBID_IPS")
    private String forbidIps;
    // 限制访问IP-只允许符合该条件的IP访问
    @Column(name = "ONLY_LEGAL_IPS")
    private String onlyLegalIps;
    // 对应person在LDAP中的DN
    @Column(name = "PERSON_DN")
    private String personDn;
    // 用户所属人员的姓名
    @Column(name = "PERSON_NAME")
    private String personName;
    // 是否有政务邮箱
    @Column(name = "is_create_msf")
    private Boolean isCreateMSF = Boolean.FALSE;
    @Column(name = "retry_times")
    private Integer retryTimes;// 失败重试次数

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

    public String getTokenNum() {
        return tokenNum;
    }

    public void setTokenNum(String tokenNum) {
        this.tokenNum = tokenNum;
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

    public Boolean getIsSupportMobile() {
        return isSupportMobile;
    }

    public void setIsSupportMobile(Boolean isSupportMobile) {
        this.isSupportMobile = isSupportMobile;
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

    public Boolean getIsCreateMSF() {
        return isCreateMSF;
    }

    public void setIsCreateMSF(Boolean isCreateMSF) {
        this.isCreateMSF = isCreateMSF;
    }

    public String getForbidIps() {
        return forbidIps;
    }

    public void setForbidIps(String forbidIps) {
        this.forbidIps = forbidIps;
    }

    public String getOnlyLegalIps() {
        return onlyLegalIps;
    }

    public void setOnlyLegalIps(String onlyLegalIps) {
        this.onlyLegalIps = onlyLegalIps;
    }

    public String getDesPassword() {
        return desPassword;
    }

    public void setDesPassword(String desPassword) {
        this.desPassword = desPassword;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }
}