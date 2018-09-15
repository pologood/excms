package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 组织/单位
 *
 * @author xujh
 *
 */
@Entity
@Table(name = "rbac_organ")
public class OrganEO extends AMockEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 472061403774490861L;

	public enum Type{
		VirtualOrgan,//虚拟单位，此类型等效于单位的容器，选单位界面不能选中
		Organ,//单位
		OrganUnit//部门
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ORGAN_ID")
	private Long organId;
	// 组织/单位名称
	@Column(name = "NAME_")
	private String name;
	// 组织/单位简称
	@Column(name = "SIMPLE_NAME")
	private String simpleName;
	// 组织类型：组织-Organ，单位-OrganUnit;
	@Column(name = "TYPE_")
	private String type;
	// 描述
	@Column(name = "DESCRIPTION_")
	private String description;
	// 父组织或单位的主键
	@Column(name = "PARENT_ID")
	private Long parentId;
	// 用户在LDAP上的DN
	@Column(name = "DN_")
	private String dn;
	// 排序码-
	@Column(name = "SORT_NUM")
	private Long sortNum;
	//是否有子容器
	@Column(name="HAS_VIRTUAL_NODES")
	private Integer hasVirtualNodes = Integer.valueOf(0);
	// 针对组织或单位
	@Column(name = "HAS_ORGANS")
	private Integer hasOrgans = Integer.valueOf(0);
	//是否有子部门/处室
	@Column(name = "HAS_ORGAN_UNITS")
	private Integer hasOrganUnits = Integer.valueOf(0);
	//是否有虚拟部门
	@Column(name = "HAS_FICTITIOUS_UNITS")
	private Integer hasFictitiousUnits = Integer.valueOf(0);
	//是否有人员-针对部门/处室或虚拟部门
	@Column(name = "HAS_PERSONS")
	private Integer hasPersons = Integer.valueOf(0);
	// 针对角色
	@Column(name = "HAS_ROLES")
	private Integer hasRoles = Integer.valueOf(0);
	// 是否是虚拟单位
	@Column(name = "IS_FICTITIOUS")
	private Integer isFictitious = Integer.valueOf(0);
	//简拼
	@Column(name = "SIMPLE_PY")
	private String simplePy;
	//全拼
	@Column(name = "FULL_PY")
	private String fullPy;
	//是否是外部单位-针对同一平台开设的外部单位
	@Column(name = "IS_EXTERNAL")
	private Boolean isExternal = Boolean.FALSE;
	//部门编码，用于唯一标识部门信息，此编码在厂商配置中设置
	@Column(name="CODE_")
	private String code;
	//是否外部平台单位/部门-是否外部平台单位
	@Column(name="IS_EXTERNAL_ORGAN")
	private Boolean isExternalOrgan = Boolean.FALSE;
	//平台编码，与isExternalOrgan组合使用
	@Column(name="PLATFORM_CODE")
	private  String platformCode;

	@Column(name="SITE_ID")
	private Long siteId;

	// 办公电话
	@Column(name = "OFFICE_PHONE")
	private String officePhone;
	// 办公地址
	@Column(name = "OFFICE_ADDRESS")
	private String officeAddress;

	// 服務电话
	@Column(name = "SERVE_PHONE")
	private String servePhone;

	// 服務地址
	@Column(name = "SERVE_ADDRESS")
	private String serveAddress;

	// 單位網址
	@Column(name = "ORGAN_URL")
	private String organUrl;

	// 单位负责人
	@Column(name = "HEAD_PERSON")
	private String headPerson;

	// 职务
	@Column(name = "POSITIONS_")
	private String positions;

	@Column(name = "IS_PUBLIC")
	private Integer isPublic;

	@Transient
	private String active;

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
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

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getHasOrgans() {
		return hasOrgans;
	}

	public void setHasOrgans(Integer hasOrgans) {
		this.hasOrgans = hasOrgans;
	}

	public Integer getHasPersons() {
		return hasPersons;
	}

	public void setHasPersons(Integer hasPersons) {
		this.hasPersons = hasPersons;
	}

	public Integer getHasRoles() {
		return hasRoles;
	}

	public void setHasRoles(Integer hasRoles) {
		this.hasRoles = hasRoles;
	}

	public Integer getIsFictitious() {
		return isFictitious;
	}

	public void setIsFictitious(Integer isFictitious) {
		this.isFictitious = isFictitious;
	}

	public Integer getHasOrganUnits() {
		return hasOrganUnits;
	}
	public void setHasOrganUnits(Integer hasOrganUnits) {
		this.hasOrganUnits = hasOrganUnits;
	}
	public Integer getHasFictitiousUnits() {
		return hasFictitiousUnits;
	}
	public void setHasFictitiousUnits(Integer hasFictitiousUnits) {
		this.hasFictitiousUnits = hasFictitiousUnits;
	}

	public String getSimplePy() {
		return simplePy;
	}

	public void setSimplePy(String simplePy) {
		this.simplePy = simplePy;
	}

	public String getFullPy() {
		return fullPy;
	}

	public void setFullPy(String fullPy) {
		this.fullPy = fullPy;
	}

	public Boolean getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(Boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getIsExternalOrgan() {
		return isExternalOrgan;
	}

	public void setIsExternalOrgan(Boolean isExternalOrgan) {
		this.isExternalOrgan = isExternalOrgan;
	}

	public String getPlatformCode() {
		return platformCode;
	}

	public void setPlatformCode(String platformCode) {
		this.platformCode = platformCode;
	}

	public Integer getHasVirtualNodes() {
		return hasVirtualNodes;
	}

	public void setHasVirtualNodes(Integer hasVirtualNodes) {
		this.hasVirtualNodes = hasVirtualNodes;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getOrganUrl() {
		return organUrl;
	}

	public void setOrganUrl(String organUrl) {
		this.organUrl = organUrl;
	}

	public String getServeAddress() {
		return serveAddress;
	}

	public void setServeAddress(String serveAddress) {
		this.serveAddress = serveAddress;
	}

	public Boolean getExternal() {
		return isExternal;
	}

	public void setExternal(Boolean external) {
		isExternal = external;
	}

	public String getServePhone() {
		return servePhone;
	}

	public void setServePhone(String servePhone) {
		this.servePhone = servePhone;
	}

	public Boolean getExternalOrgan() {
		return isExternalOrgan;
	}

	public void setExternalOrgan(Boolean externalOrgan) {
		isExternalOrgan = externalOrgan;
	}

	public Integer getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	public String getHeadPerson() {
		return headPerson;
	}

	public void setHeadPerson(String headPerson) {
		this.headPerson = headPerson;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}
}
