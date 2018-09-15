package cn.lonsun.webservice.vo.indicator;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 指示器
 * 
 * @author xujh
 * 
 */
public class IndicatorVO extends AMockEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public enum Type {
		Shortcut, // 快捷方式
		Menu, // 菜单项
		ToolBarButton, // 工具栏按钮
		Other// 权限集合（不显示）
	}

	public enum SizeModel {
		Pixel, // 像素
		Percent, // 百分比
		Max, // 最大化
		Adaptation, // 自适应
		Define// 指定尺寸
	}

	/**
	 * 按钮编码，用于标记操作类型
	 * 
	 * @author xujh
	 * @date 2014年9月29日 下午3:13:22
	 * @version V1.0
	 */
	public enum OperatorCode {
		Add,    // 新增
		Delete, // 删除
		Update, // 修改
		Query,  // 查询
		Enable, // 启用
		Unable, // 禁用
		Export, // 导出
		Move    // 移动
	}

	private Long indicatorId;
	// 类型
	private String type;
	// 指示器编码
	private String code;
	// 名称
	private String name;
	// 系统编码，用于标识属于哪个系统
	private String systemCode;
	//服务器地址
	private String host;
	// 访问链接
	private String uri;
	// 资源ID
	private Long resourceId;
	// 是否可用
	private Boolean isEnable = Boolean.TRUE;
	// 父菜单的主键
	private Long parentId;
	// 是否系统定义-用户可以自定义菜单
	private Boolean isSystem = Boolean.FALSE;
	// 用户ID-用于用户自定义
	private Long userId;
	// 是否显示子菜单
	private Boolean isShowSons = Boolean.FALSE;
	// 是否为本引用的首页
	private Boolean isIndex = Boolean.FALSE;
	
	private Boolean isShowInDesktop = Boolean.FALSE;
	// 序号
	private Integer sortNum;
	// 业务类别主键
	private Long businessTypeId;
	//描述
	private String description;
	// 文字图标
	private String textIcon;
	// 小图片图标
	private String smallIconPath;
	// 中图片图标
	private String middleIconPath;
	// 大图片图标
	private String bigIconPath;
	// 窗口打开模式，如果type为Define，那么width和height必须大于0，否则忽略这两个属性值
	private String sizeModel;
	// 窗口宽度，单位为px
	private Integer width;
	// 窗口高度，单位为px
	private Integer height;
	//是否是父亲
	private Integer isParent = Integer.valueOf(0);
	//是否需要推送消息
	private Boolean hasMessage = Boolean.FALSE;
	//外部用户是否显示
	private Boolean isShown4ExternalUser;
	//菜单级别
	private int menuLevel = 0;

	public Long getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(Long indicatorId) {
		this.indicatorId = indicatorId;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public Long getBusinessTypeId() {
		return businessTypeId;
	}

	public void setBusinessTypeId(Long businessTypeId) {
		this.businessTypeId = businessTypeId;
	}

	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}

	public Boolean getIsShowSons() {
		return isShowSons;
	}

	public void setIsShowSons(Boolean isShowSons) {
		this.isShowSons = isShowSons;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTextIcon() {
		return textIcon;
	}

	public void setTextIcon(String textIcon) {
		this.textIcon = textIcon;
	}

	public String getSmallIconPath() {
		return smallIconPath;
	}

	public void setSmallIconPath(String smallIconPath) {
		this.smallIconPath = smallIconPath;
	}

	public String getMiddleIconPath() {
		return middleIconPath;
	}

	public void setMiddleIconPath(String middleIconPath) {
		this.middleIconPath = middleIconPath;
	}

	public String getBigIconPath() {
		return bigIconPath;
	}

	public void setBigIconPath(String bigIconPath) {
		this.bigIconPath = bigIconPath;
	}

	public String getSizeModel() {
		return sizeModel;
	}

	public void setSizeModel(String sizeModel) {
		this.sizeModel = sizeModel;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public Integer getIsParent() {
		return isParent;
	}

	public void setIsParent(Integer isParent) {
		this.isParent = isParent;
	}

	public Boolean getIsIndex() {
		return isIndex;
	}

	public void setIsIndex(Boolean isIndex) {
		this.isIndex = isIndex;
	}

	public Boolean getIsShowInDesktop() {
		return isShowInDesktop;
	}

	public void setIsShowInDesktop(Boolean isShowInDesktop) {
		this.isShowInDesktop = isShowInDesktop;
	}

	public Boolean getHasMessage() {
		return hasMessage;
	}

	public void setHasMessage(Boolean hasMessage) {
		this.hasMessage = hasMessage;
	}

	public Boolean getIsShown4ExternalUser() {
		return isShown4ExternalUser;
	}

	public void setIsShown4ExternalUser(Boolean isShown4ExternalUser) {
		this.isShown4ExternalUser = isShown4ExternalUser;
	}

	public int getMenuLevel() {
		return menuLevel;
	}

	public void setMenuLevel(int menuLevel) {
		this.menuLevel = menuLevel;
	}
}
