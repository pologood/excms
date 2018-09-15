package cn.lonsun.rbac.internal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.core.base.entity.AMockEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 人员信息，与LDAP中的字段保持同步
 * 
 * @author xujh
 * 
 */
@Entity
@Table(name = "rbac_person")
public class PersonEO extends AMockEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4992251924186539147L;

	// 物理主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PERSON_ID")
	private Long personId;
	// 姓名
	@Column(name = "NAME_")
	private String name;
	// 出生日期
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	// 用户前端日期类型字符串自动转换
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	// 用户Date类型转换Json字符类型的格式化,注：数据库如果为TimeStamp类型存储的时间，在json输出的时候默认返回格林威治时间，需要增加timezone属性
//	@Column(name = "BIRTH_")
//	private Date birth;
	// 职务，多个职务通过逗号","隔开
	@Column(name = "POSITIONS_")
	private String positions;
	// 移动电话
	@Column(name = "MOBILE_")
	private String mobile;
	// 办公电话
	@Column(name = "OFFICE_PHONE")
	private String officePhone;
	// 办公地址
	@Column(name = "OFFICE_ADDRESS")
	private String officeAddress;
	// 邮件
	@Column(name = "MAIL_")
	private String mail;
	// 邮件-用于文件直传，对用户不可见
	@Column(name = "MAIL_SEND_FILES")
	private String mailSendFiles;
	// 描述
	@Column(name = "PERSON_DESC")
	private String desc;
	// 头像
	@Column(name = "JPEG_PHOTO")
	private String jpegPhoto;
	// 头像名称
	@Column(name = "JPEG_PHOTO_NAME")
	private String jpegPhotoName;
	// 是否兼职用户，每加入一个单位都添加一条新的记录
	@Column(name = "IS_PLURALISTIC")
	private Boolean isPluralistic = false;// 0表示非兼职，1表示兼职
	// 姓名全拼
	@Column(name = "FULL_PY")
	private String fullPy;
	// 姓名简拼
	@Column(name = "SIMPLE_PY")
	private String simplePy;
	// 排序编码-用于查询排序
	@Column(name = "SORT_NUM")
	private Long sortNum = Long.valueOf(0);
	// 源personId,兼职person对应的主person主键
	@Column(name = "SRC_PERSON_ID")
	private Long srcPersonId;
	// 所属部门/处室主键
	@Column(name = "ORGAN_ID")
	private Long organId;
	// 所属部门/处室名称
	@Column(name = "ORGAN_NAME")
	private String organName;
	// 所属单位主键
	@Column(name = "UNIT_ID")
	private Long unitId;
	// 所属单位名称
	@Column(name = "UNIT_NAME")
	private String unitName;
	// 用户在LDAP上的DN
	@Column(name = "DN_")
	private String dn;
	// 用户ID
	@Column(name = "USER_ID")
	private Long userId;
	@Column(name = "UID_")
	private String uid;
	// 文件直传的最大空间容量
	@Column(name = "max_capacity")
	private Integer maxCapacity;
	//是否外部平台人员
	@Column(name="IS_EXTERNAL_PERSON",updatable=false)
	private Boolean isExternalPerson = Boolean.FALSE;
	//平台编码，与isExternalPerson组合使用
	@Column(name="PLATFORM_CODE",updatable=false)
	private  String platformCode;

	@Transient
	private String siteRights;

	@Transient
	private String fullOrganName;

	@Transient
	private Date lastLoginDate;

	public String getJpegPhotoName() {
		return jpegPhotoName;
	}

	public void setJpegPhotoName(String jpegPhotoName) {
		this.jpegPhotoName = jpegPhotoName;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
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

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public String getJpegPhoto() {
		return jpegPhoto;
	}

	public void setJpegPhoto(String jpegPhoto) {
		this.jpegPhoto = jpegPhoto;
	}

	public Boolean getIsPluralistic() {
		return isPluralistic;
	}

	public void setIsPluralistic(Boolean isPluralistic) {
		this.isPluralistic = isPluralistic;
	}

	public Long getSortNum() {
		return sortNum;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getSrcPersonId() {
		return srcPersonId;
	}

	public void setSrcPersonId(Long srcPersonId) {
		this.srcPersonId = srcPersonId;
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

	public Boolean getIsExternalPerson() {
		return isExternalPerson;
	}

	public void setIsExternalPerson(Boolean isExternalPerson) {
		this.isExternalPerson = isExternalPerson;
	}

	public String getPlatformCode() {
		return platformCode;
	}

	public void setPlatformCode(String platformCode) {
		this.platformCode = platformCode;
	}

	public String getSiteRights() {
		return siteRights;
	}

	public void setSiteRights(String siteRights) {
		this.siteRights = siteRights;
	}

	public String getFullOrganName() {
		return fullOrganName;
	}

	public void setFullOrganName(String fullOrganName) {
		this.fullOrganName = fullOrganName;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
}
