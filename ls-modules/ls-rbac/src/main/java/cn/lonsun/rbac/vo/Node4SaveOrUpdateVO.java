package cn.lonsun.rbac.vo;

import java.io.Serializable;


/**
 * 所有保存和更新都需要返回此对象告诉前端执行结果
 * @author xujh
 *
 */
public class Node4SaveOrUpdateVO implements Serializable{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6801945338794270508L;
	
	public Node4SaveOrUpdateVO(){
		
	}
	public Node4SaveOrUpdateVO(Long id,Long pId,String name,boolean update){
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.update = update;
	}
	//id
	private Long id;
	//parentId
	private Long pId;
	//操作对象的name属性
	private String name;
	//true表示更新操作，false表示新增操作
	private Boolean update;
	//是否是兼职人员
	private Boolean isPluralistic = Boolean.FALSE;
	
	private String icon;
	
	private Boolean isParent;
	
	private Long sortNum;
	
	private String nodeType;//节点类型
	
	private String positions;//职务
	
	private String dn;
	private Long userId;


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPId() {
		return pId;
	}

	public void setPId(Long pId) {
		this.pId = pId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getUpdate() {
		return update;
	}

	public void setUpdate(Boolean update) {
		this.update = update;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Long getSortNum() {
		return sortNum;
	}
	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getPositions() {
		return positions;
	}
	public void setPositions(String positions) {
		this.positions = positions;
	}
	public void setPluralistic(boolean isPluralistic) {
		this.isPluralistic = isPluralistic;
	}
	public Boolean getIsPluralistic() {
		return isPluralistic;
	}
	public void setIsPluralistic(Boolean isPluralistic) {
		this.isPluralistic = isPluralistic;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
}
