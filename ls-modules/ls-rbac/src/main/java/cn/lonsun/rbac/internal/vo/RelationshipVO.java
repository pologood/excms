package cn.lonsun.rbac.internal.vo;

/**
 * 上下级关系VO
 *  
 * @author xujh 
 * @date 2014年10月30日 上午10:39:34
 * @version V1.0
 */
public class RelationshipVO {
	//对应personId
	private Long id;
	//对应leaderPersonId
	private Long pid;
	
	private String name;
	
	private String icon;
	
	private Boolean isParent;
	
	private Integer subordinateCount;
	
	private Long relationshipId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public Integer getSubordinateCount() {
		return subordinateCount;
	}

	public void setSubordinateCount(Integer subordinateCount) {
		this.subordinateCount = subordinateCount;
	}

	public Long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(Long relationshipId) {
		this.relationshipId = relationshipId;
	}
}
