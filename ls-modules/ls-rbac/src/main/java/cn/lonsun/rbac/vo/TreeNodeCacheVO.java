package cn.lonsun.rbac.vo;

import cn.lonsun.common.vo.TreeNodeVO;

public class TreeNodeCacheVO extends TreeNodeVO {
	// 是否有单位容器
	private Boolean hasVirtualOrgan = Boolean.FALSE;
	// 是否有单位
	private Boolean hasOrgans = Boolean.FALSE;
	// 是否有子部门/处室
	private Boolean hasOrganUnits = Boolean.FALSE;
	// 是否有虚拟部门
	private Boolean hasFictitiousUnits = Boolean.FALSE;
	// 是否有人员-针对部门/处室或虚拟部门
	private Boolean hasPersons = Boolean.FALSE;
	// 针对角色
	private Boolean hasRoles = Boolean.FALSE;
	public Boolean getHasVirtualOrgan() {
		return hasVirtualOrgan;
	}
	public void setHasVirtualOrgan(Boolean hasVirtualOrgan) {
		this.hasVirtualOrgan = hasVirtualOrgan;
	}
	public Boolean getHasOrgans() {
		return hasOrgans;
	}
	public void setHasOrgans(Boolean hasOrgans) {
		this.hasOrgans = hasOrgans;
	}
	public Boolean getHasOrganUnits() {
		return hasOrganUnits;
	}
	public void setHasOrganUnits(Boolean hasOrganUnits) {
		this.hasOrganUnits = hasOrganUnits;
	}
	public Boolean getHasFictitiousUnits() {
		return hasFictitiousUnits;
	}
	public void setHasFictitiousUnits(Boolean hasFictitiousUnits) {
		this.hasFictitiousUnits = hasFictitiousUnits;
	}
	public Boolean getHasPersons() {
		return hasPersons;
	}
	public void setHasPersons(Boolean hasPersons) {
		this.hasPersons = hasPersons;
	}
	public Boolean getHasRoles() {
		return hasRoles;
	}
	public void setHasRoles(Boolean hasRoles) {
		this.hasRoles = hasRoles;
	}
}
