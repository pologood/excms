package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import javax.persistence.*;

@Entity
@Table(name="CMS_BBS_PLATE")
public class BbsPlateEO extends AMockEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PLATE_ID")
	private Long plateId;

	//(站点id)	
	@Column(name="SITE_ID")
	private Long siteId;
	//父节点
	@Column(name="PARENT_ID")
	private Long parentId;
	//父节点串
	@Column(name="PARENT_IDS")
	private String parentIds;
	//版块名称
	@Column(name="NAME_")
	private String name;
	//允许发帖
	@Column(name="can_thread")
	private Integer canThread=1;
	//允许回复
	@Column(name="can_Post")
	private Integer canPost=1;
	//允许上传附件
	@Column(name="can_Upload")
	private Integer canUpload=1;
	//仅系统用户可见
	@Column(name="user_Can_View")
	private Integer userCanView=0;
	//仅版主用户可见
	@Column(name="manager_Can_View")
	private Integer managerCanView=0;
	//版块图标
	@Column(name="icon")
	private String icon;
	//本版块版主
	@Column(name="manager")
	private String manager;
	//激活此板块
	@Column(name="status")
	private Integer status=1;
	//规则
	@Column(name="rule")
	private String rule;
	//主题默认排序方式
	@Column(name="sort_Mode")
	private Integer sortMode=0;
	//主题默认排序字段
	@Column(name="sort_Field")
	private Integer sortField=0;
	//屏蔽
	@Column(name="is_screen")
	private Integer isScreen;
	//关闭
	@Column(name="IS_CLOSE")
	private String isClose;
	//内容模型
	@Column(name="CONTENT_MODEL_CODE")
	private String contentModelCode;
	//排列序号
	@Column(name="SORT_NUM")
	private Long sortNum;
	//是否有子项
	@Column(name = "HAS_CHILD")
	private Integer hasChild = Integer.valueOf(0);

	//描述
	@Column(name = "DESC_")
	private String description;

	//单位IDs
	@Transient
	private String unitIds;
	//单位名称集
	@Transient
	private String unitNames;

	public Integer getIsScreen() {
		return isScreen;
	}

	public void setIsScreen(Integer isScreen) {
		this.isScreen = isScreen;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
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

	public Integer getHasChild() {
		return hasChild;
	}

	public void setHasChild(Integer hasChild) {
		this.hasChild = hasChild;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnitIds() {
		return unitIds;
	}

	public String getUnitNames() {
		return unitNames;
	}

	public void setUnitIds(String unitIds) {
		this.unitIds = unitIds;
	}

	public void setUnitNames(String unitNames) {
		this.unitNames = unitNames;
	}
	
	
	

}
