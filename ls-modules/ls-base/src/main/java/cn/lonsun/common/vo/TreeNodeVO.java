package cn.lonsun.common.vo;

import java.util.List;

/**
 * 选人、单位、部门/处室数据传递VO
 * 
 * @author xujh
 * @version 1.0 2014年12月12日
 * 
 */
public class TreeNodeVO {

	/**
	 * 树节点类型
	 * 
	 * @author xujh
	 * @version 1.0 2014年12月12日
	 * 
	 */
	public enum Type {
		VirtualNode("虚拟节点"),//虚节点，此类型等效于单位的容器，选单位界面不能选中
		Organ("单位"), // 单位
		OrganUnit("部门"),// 部门
		Virtual("虚拟部门"),//虚拟部门
		Person("人员"),//
		Role("角色");
		
		private String name;
		
		private Type(String name){
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * 组织架构树上的Icon
	 * 
	 * @Description:
	 * @author xujh
	 * @date 2014年9月23日 上午8:26:22
	 * @version V1.0
	 */
	public enum Icon {
		VirtualNode(""),
		Organ("/assets/images/organ.gif"), // 组织
		OrganUnit("/assets/images/organunit.gif"), // 单位
		Virtual(""), // 虚拟单位
		Male("/assets/images/person.gif"), // 男人
		Role("");//角色
		private String value;

		private Icon(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	// 树节点ID，为了确保不重复，id由节点对应实体类型+主键构成，"PersonEO"+personId和"OrganEO"+organId
	private String id;
	// 树节点父ID
	private String pid;
	// 节点名称
	private String name;
	// 节点类型
	private String type;
	// 用户主键
	private Long userId;
	//人员ID
	private Long personId;
	//人员姓名
	private String personName;
	//个人移动电话
	private String mobile;
	// 是否是父节点
	private Boolean isParent = Boolean.FALSE;
	// 图标
	private String icon;
	// 单位ID-节点类型为Person时存储
	private Long unitId;
	// 单位名称-节点类型为Person时存储
	private String unitName;
	// 部门、处室或虚拟单位ID-节点类型为Person时存储
	private Long organId;
	// 部门、处室或虚拟单位名称-节点类型为Person时存储
	private String organName;
	
	private Long roleId;
	
	private String roleName;

	private String roleCode;
	private String dn;
	//是否外部平台人员
	private Boolean isExternalPerson = Boolean.FALSE;
	//是否外部平台单位/部门-是否外部平台单位
	private Boolean isExternalOrgan = Boolean.FALSE;
	//平台编码，与isExternalPerson或isExternalOrgan组合使用
	private  String platformCode;
	//子节点
	private List<TreeNodeVO> children;

	private boolean checked;

	private boolean enabled = Boolean.TRUE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public List<TreeNodeVO> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNodeVO> children) {
		this.children = children;
	}
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public Boolean getIsExternalPerson() {
		return isExternalPerson;
	}

	public void setIsExternalPerson(Boolean isExternalPerson) {
		this.isExternalPerson = isExternalPerson;
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

	/**
	 * 当前节点是否是node的子节点
	 *
	 * @param node
	 * @return
	 */
	public boolean isChild(TreeNodeVO node){
		boolean isChild = false;
		if(getPid()!=null&&getPid().equals(node.getId())){
			isChild = true;
		}
		return isChild;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
