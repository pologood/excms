package cn.lonsun.govbbs.internal.vo;


public class BbsPlateVO implements java.io.Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long plateId;

	//(站点id)	
	private Long siteId;
	
	private Long parentId;

	private Long oldParentId;
	
	private String parentIds;
	
	private String name;
	
	private String isClose;
	
	private String contentModelCode;
	
	private Long sortNum;
	
	private Integer hasChild = Integer.valueOf(0);
	
	private String description;
	
	private Boolean isParent = false;
	
	private Boolean hasPost = false;

	private Integer canThread;

	private Integer canPost;

	private Integer canUpload;

	private Integer userCanView;

	private Integer managerCanView;

	private Integer icon;

	private Integer manager;

	private Integer status;

	private String rule;

	private Integer sortMode;

	private Integer sortField;

	public Long getOldParentId() {
		return oldParentId;
	}

	public void setOldParentId(Long oldParentId) {
		this.oldParentId = oldParentId;
	}

	public Boolean getParent() {
		return isParent;
	}

	public void setParent(Boolean parent) {
		isParent = parent;
	}

	public Integer getCanThread() {
		return canThread;
	}

	public void setCanThread(Integer canThread) {
		this.canThread = canThread;
	}

	public Integer getCanPost() {
		return canPost;
	}

	public void setCanPost(Integer canPost) {
		this.canPost = canPost;
	}

	public Integer getCanUpload() {
		return canUpload;
	}

	public void setCanUpload(Integer canUpload) {
		this.canUpload = canUpload;
	}

	public Integer getUserCanView() {
		return userCanView;
	}

	public void setUserCanView(Integer userCanView) {
		this.userCanView = userCanView;
	}

	public Integer getManagerCanView() {
		return managerCanView;
	}

	public void setManagerCanView(Integer managerCanView) {
		this.managerCanView = managerCanView;
	}

	public Integer getIcon() {
		return icon;
	}

	public void setIcon(Integer icon) {
		this.icon = icon;
	}

	public Integer getManager() {
		return manager;
	}

	public void setManager(Integer manager) {
		this.manager = manager;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Integer getSortMode() {
		return sortMode;
	}

	public void setSortMode(Integer sortMode) {
		this.sortMode = sortMode;
	}

	public Integer getSortField() {
		return sortField;
	}

	public void setSortField(Integer sortField) {
		this.sortField = sortField;
	}

	public Long getPlateId() {
		return plateId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public Long getParentId() {
		return parentId;
	}

	public String getParentIds() {
		return parentIds;
	}

	public String getName() {
		return name;
	}

	public String getIsClose() {
		return isClose;
	}

	public String getContentModelCode() {
		return contentModelCode;
	}

	public Long getSortNum() {
		return sortNum;
	}

	public Integer getHasChild() {
		return hasChild;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setPlateId(Long plateId) {
		this.plateId = plateId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIsClose(String isClose) {
		this.isClose = isClose;
	}

	public void setContentModelCode(String contentModelCode) {
		this.contentModelCode = contentModelCode;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}

	public void setHasChild(Integer hasChild) {
		this.hasChild = hasChild;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public Boolean getHasPost() {
		return hasPost;
	}

	public void setHasPost(Boolean hasPost) {
		this.hasPost = hasPost;
	}
	
}
