package cn.lonsun.rbac.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * 用户分页查询条件
 *
 * @author xujh
 * @version 1.0
 * 2015年2月10日
 *
 */
public class PersonQueryVO extends PageQueryVO{
	
	private String name;
	private String unitName;
	private String mobile;
	private String officePhone;
	private String searchText;
	private Long organId;
	private Long personId;
	private String dn;
	private Integer isUnit;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
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
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public Long getOrganId() {
		return organId;
	}
	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}
	public Long getPersonId() {
		return personId;
	}
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	public Integer getIsUnit() {
		return isUnit;
	}
	public void setIsUnit(Integer isUnit) {
		this.isUnit = isUnit;
	}
	
}
