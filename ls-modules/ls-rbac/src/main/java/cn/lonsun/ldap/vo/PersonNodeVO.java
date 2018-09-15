package cn.lonsun.ldap.vo;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.UserEO.STATUS;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 组织架构树管理前后端交互人员信息VO
 *
 * @author xujh
 * @version V1.0
 * @Description:
 * @date 2014年9月23日 下午10:09:36
 */
public class PersonNodeVO {

    // 节点类型
    private String nodeType = TreeNodeVO.Type.Person.toString();
    private Long id;

    private Long personId;
    // 姓名
    private String name;
    // 出生日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date birth;
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
    // 是否开通政务邮箱
    private Boolean isCreateMSF = Boolean.FALSE;
    //默认支持手机访问
    private Boolean isSupportMobile = Boolean.TRUE;

    private String tokenNum;

    // 邮件-用于文件直传，对用户不可见
    private String mailSendFiles;
    // 头像
    private String jpegPhoto;
    // 头像名称
    private String jpegPhotoName;
    // 描述
    private String desc;
    // 是否兼职用户，每加入一个单位都添加一条新的记录
    private Boolean isPluralistic = Boolean.FALSE;// 0表示非兼职，1表示兼职
    // 姓名全拼
    private String fullPy;
    // 姓名简拼
    private String simplePy;
    // 排序编码-用于查询排序
    private Long sortNum;
    // 源personId,兼职person对应的主person主键
    private Long srcPersonId;
    // 所属单位
    private Long pid;
    // 用户在LDAP上的DN
    private String dn;
    private Long organId;
    private String organName;
    private Long unitId;
    private String unitName;
    private String legalIps;
    private String forbidIps;
    private String onlyLegalIps;
    private Long userId;
    private String uid;
    private String recordStatus = AMockEntity.RecordStatus.Normal.toString(); // 记录状态，正常：Normal,已删除:Removed
    private Long createUserId;// 创建人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createDate = new Date();// 创建时间
    private Long updateUserId;// 更新人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date updateDate = new Date();// 更新时间
    private Boolean isParent = Boolean.FALSE;
    // 密码
    private String password;
    private String status = STATUS.Enabled.toString();
    private String mobileCode;
    private Integer supportMobile = Integer.valueOf(0);
    private Integer loginTimes = 0;
    private Integer loginType = 0;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date lastLoginDate;
    private String lastLoginIp;
    private String personDn;
    private String personName;
    private String icon;
    private String roleIds;
    private String roleNames;

    private String upRoleIds;
    private String upRoleNames;

    private String viRoleIds;
    private String viRoleNames;

    private Boolean updateRole = false;

    //原始密码
    private String originalPassword;

    public String getUpRoleIds() {
        return upRoleIds;
    }

    public void setUpRoleIds(String upRoleIds) {
        this.upRoleIds = upRoleIds;
    }

    public String getUpRoleNames() {
        return upRoleNames;
    }

    public void setUpRoleNames(String upRoleNames) {
        this.upRoleNames = upRoleNames;
    }


    public String getViRoleIds() {
        return viRoleIds;
    }

    public void setViRoleIds(String viRoleIds) {
        this.viRoleIds = viRoleIds;
    }

    public String getViRoleNames() {
        return viRoleNames;
    }

    public void setViRoleNames(String viRoleNames) {
        this.viRoleNames = viRoleNames;
    }


    private String roleTypes;
    //文件直传默认容量
    private Integer maxCapacity;

    private String srcPersonInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
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

    public String getJpegPhoto() {
        return jpegPhoto;
    }

    public void setJpegPhoto(String jpegPhoto) {
        this.jpegPhoto = jpegPhoto;
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

    public Long getSrcPersonId() {
        return srcPersonId;
    }

    public void setSrcPersonId(Long srcPersonId) {
        this.srcPersonId = srcPersonId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

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

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getJpegPhotoName() {
        return jpegPhotoName;
    }

    public void setJpegPhotoName(String jpegPhotoName) {
        this.jpegPhotoName = jpegPhotoName;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getLegalIps() {
        return legalIps;
    }

    public void setLegalIps(String legalIps) {
        this.legalIps = legalIps;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }


    public String getTokenNum() {
        return tokenNum;
    }

    public void setTokenNum(String tokenNum) {
        this.tokenNum = tokenNum;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(String roleTypes) {
        this.roleTypes = roleTypes;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
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

    public String getSrcPersonInfo() {
        return srcPersonInfo;
    }

    public void setSrcPersonInfo(String srcPersonInfo) {
        this.srcPersonInfo = srcPersonInfo;
    }

    public Boolean getIsCreateMSF() {
        return isCreateMSF;
    }

    public void setIsCreateMSF(Boolean isCreateMSF) {
        this.isCreateMSF = isCreateMSF;
    }

    public Boolean getIsSupportMobile() {
        return isSupportMobile;
    }

    public void setIsSupportMobile(Boolean isSupportMobile) {
        this.isSupportMobile = isSupportMobile;
    }

    public void setIsPluralistic(Boolean isPluralistic) {
        this.isPluralistic = isPluralistic;
    }

    public Boolean getIsPluralistic() {
        return isPluralistic;
    }

    public Boolean getUpdateRole() {
        return updateRole;
    }

    public void setUpdateRole(Boolean updateRole) {
        this.updateRole = updateRole;
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
