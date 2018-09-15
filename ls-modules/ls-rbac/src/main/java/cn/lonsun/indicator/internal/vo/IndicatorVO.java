package cn.lonsun.indicator.internal.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.ResourceEO;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 指示器
 * @author xujh
 *
 */
public class IndicatorVO implements Serializable {
	
	/**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8947712373660777729L;
    
    public enum Type {
        Shortcut,//快捷方式
        Menu,//菜单项
        ToolBarButton,//工具栏按钮
        Other//权限集合（不显示）
    }
    public enum SizeModel {
        Pixel,//像素
        Percent,//百分比
        Max,//最大化
        Adaptation,//自适应 
        Define//指定尺寸
    }
    
    private Long id;
	private Long indicatorId;
	//类型
	private String type;
	//指示器编码
	private String code;
	//名称
	private String name;
	//应用入口
	private String host;
	//WebService服务器地址
	private String webServiceHost;
	//访问链接
	private String uri;
	//资源ID
	private Long resourceId;
	//关联窗口对象主键
	private Long windowId;
	//是否可用
	private String isEnable = "1";
	//父菜单的主键
	private Long parentId;
	//父菜单的主键
	private String parentIds;
	//父菜单的名称
	private String parentNames;
	private Long pid;
	private String pname;
    private boolean isParent = false;
	//private String icon;
	private boolean checked = false;
	
	//是否系统定义-用户可以自定义菜单
	private boolean isSystem = false;
	//用户ID-用于用户自定义
	private Long userId;
	//是否显示子菜单
	private String isShowSons = "0";
	//是否在桌面显示
	private String isShowInDesktop = "0";
	//是否只属于开发商拥有
	private String isOwnedByDeveloper = "0";
	//开发商是否可见
	private String isShown4Developer = "0";
	//超管是否可见
	private String isShown4Admin = "1";
	//系统管理员是否可见
	private String isShown4SystemManager = "1";
	//外部用户是否可见
	private String isShown4ExternalUser = "1";
	private String hasMessage = "0";
	//序号
	private Integer sortNum;
	// 业务类别主键
	private Long businessTypeId;
	private String description;
	private Boolean isIndex = Boolean.FALSE;
	private Long siteId;
    private Boolean open=false;

//	private String smallIconPath;
//	// 中图片图标
//	private String middleIconPath;
//	// 大图片图标
//	private String bigIconPath;
	// 权限
    private List<IndicatorVO> rights = new ArrayList<IndicatorVO>();
    
    private List<ResourceEO> resources = new ArrayList<ResourceEO>();
	
	private String sizeModel = SizeModel.Max.toString();//（Max//最大化, Adaptation//自适应, Define//指定尺寸）
    private Integer width; 
    private Integer height;
    
    private String path;
	private String systemCode;
	private String recordStatus = AMockEntity.RecordStatus.Normal.toString(); //记录状态，正常：Normal,已删除:Removed
    private Long createUserId;//创建人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createDate = new Date();//创建时间
    private Long updateUserId;//更新人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date updateDate = new Date();//更新时间
    private String icon;
	private String textIcon;
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
	public Long getWindowId() {
		return windowId;
	}
	public void setWindowId(Long windowId) {
		this.windowId = windowId;
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
    public String getIsEnable() {
        return isEnable;
    }
    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }
    public boolean getIsSystem() {
        return isSystem;
    }
    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }
    public String getIsShowSons() {
        return isShowSons;
    }
    public void setIsShowSons(String isShowSons) {
        this.isShowSons = isShowSons;
    }
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
    public Boolean getIsParent() {
        return isParent;
    }
    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }
    public String getRecordStatus() {
        return recordStatus;
    }
    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
    public Long getCreateUserId() {
        return createUserId;
    }
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Long getUpdateUserId() {
        return updateUserId;
    }
    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    public String getSizeModel() {
        return sizeModel;
    }
    public void setSizeModel(String sizeModel) {
        this.sizeModel = sizeModel;
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
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPname() {
        return pname;
    }
    public void setPname(String pname) {
        this.pname = pname;
    }
    public boolean getChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<IndicatorVO> getRights() {
        return rights;
    }
    public void setRights(List<IndicatorVO> rights) {
        this.rights = rights;
    }
    public List<ResourceEO> getResources() {
        return resources;
    }
    public void setResources(List<ResourceEO> resources) {
        this.resources = resources;
    }
    
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getTextIcon() {
		return textIcon;
	}
	public void setTextIcon(String textIcon) {
		this.textIcon = textIcon;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Boolean getIsIndex() {
		return isIndex;
	}
	public void setIsIndex(Boolean isIndex) {
		this.isIndex = isIndex;
	}
//	public String getSmallIconPath() {
//		return smallIconPath;
//	}
//	public void setSmallIconPath(String smallIconPath) {
//		this.smallIconPath = smallIconPath;
//	}
//	public String getMiddleIconPath() {
//		return middleIconPath;
//	}
//	public void setMiddleIconPath(String middleIconPath) {
//		this.middleIconPath = middleIconPath;
//	}
//	public String getBigIconPath() {
//		return bigIconPath;
//	}
//	public void setBigIconPath(String bigIconPath) {
//		this.bigIconPath = bigIconPath;
//	}
	public String getIsShowInDesktop() {
		return isShowInDesktop;
	}
	public void setIsShowInDesktop(String isShowInDesktop) {
		this.isShowInDesktop = isShowInDesktop;
	}
	public String getIsOwnedByDeveloper() {
		return isOwnedByDeveloper;
	}
	public void setIsOwnedByDeveloper(String isOwnedByDeveloper) {
		this.isOwnedByDeveloper = isOwnedByDeveloper;
	}
	public String getIsShown4Developer() {
		return isShown4Developer;
	}
	public void setIsShown4Developer(String isShown4Developer) {
		this.isShown4Developer = isShown4Developer;
	}
	public String getIsShown4Admin() {
		return isShown4Admin;
	}
	public void setIsShown4Admin(String isShown4Admin) {
		this.isShown4Admin = isShown4Admin;
	}
	public String getIsShown4SystemManager() {
		return isShown4SystemManager;
	}
	public void setIsShown4SystemManager(String isShown4SystemManager) {
		this.isShown4SystemManager = isShown4SystemManager;
	}
	public String getIsShown4ExternalUser() {
		return isShown4ExternalUser;
	}
	public void setIsShown4ExternalUser(String isShown4ExternalUser) {
		this.isShown4ExternalUser = isShown4ExternalUser;
	}
	public String getHasMessage() {
		return hasMessage;
	}
	public void setHasMessage(String hasMessage) {
		this.hasMessage = hasMessage;
	}
	public String getWebServiceHost() {
		return webServiceHost;
	}
	public void setWebServiceHost(String webServiceHost) {
		this.webServiceHost = webServiceHost;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public boolean isChecked() {
		return checked;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public String getParentNames() {
		return parentNames;
	}

	public void setParentNames(String parentNames) {
		this.parentNames = parentNames;
	}
}
