package cn.lonsun.indicator.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.internal.CacheHelper;

import javax.persistence.*;

/**
 * 指示器
 *
 * @author xujh
 */
@Entity
@Table(name = "RBAC_INDICATOR")
public class IndicatorEO extends AMockEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7816070323184160799L;

    public enum Type {
        Shortcut, // 快捷方式
        Menu, // 菜单项
        ToolBarButton, // 工具栏按钮
        Other, // 权限集合（不显示）
        CMS_Menu, // cms菜单
        CMS_Button, // cms按钮
        CMS_Site, // cms站点
        CMS_Section,// cms栏目
        COM_Section,//公共栏目
        SUB_Site//虚子站
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
     * @version V1.0
     * @date 2014年9月29日 下午3:13:22
     */
    public enum OperatorCode {
        Add, // 新增
        Delete, // 删除
        Update, // 修改
        Query, // 查询
        Enable, // 启用
        Unable, // 禁用
        Export, // 导出
        Move // 移动
    }

    public String columnPath(String domain){
        String parentNames = this.getParentNames();
        if(StringUtils.isEmpty(parentNames)){
            return "<a href='"+domain+"/"+ this.getNamePinyin() +"/index.html' target='_blank' >"+ this.getName() +"</a>";
        }
        StringBuilder sb = new StringBuilder();
        String[] parentName = parentNames.split(">");
        String[] parentNamePinyin = this.getParentNamesPinyin().split("/");
        int index = 0;
        for(String p : parentName){
            sb.append("<a href='").append(domain).append("/");
            for(int i = 0; i <= index;i++){
                try {
                    sb.append(parentNamePinyin[i]).append("/");
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
            sb.append("index.html' target='_blank' >").append(p).append("</a>");
            sb.append(" &gt; ");
            index++;
        }
        sb.append("<a href='").append(domain).append("/").append(this.getParentNamesPinyin())
                .append("/").append(this.getNamePinyin()).append("/index.html' target='_blank' >")
                .append(this.getName()).append("</a>");
        return sb.toString();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "indicator_id")
    private Long indicatorId;
    // 类型
    @Column(name = "type")
    private String type;
    // 指示器编码
    @Column(name = "code")
    private String code;
    // 名称
    @Column(name = "name")
    private String name;
    // 名称
    @Column(name = "name_pinyin", updatable = false)
    private String namePinyin;
    //简名称
    @Column(name = "shortname")
    private String shortName;
    // 系统编码，用于标识属于哪个系统，由a-z,A-Z,0-9组成，不支持其他字符
    @Column(name = "system_code")
    private String systemCode;
    // 应用服务器地址
    @Column(name = "host")
    private String host;
    // WebService服务器地址
    @Column(name = "web_service_host")
    private String webServiceHost;
    // 访问链接
    @Column(name = "uri")
    private String uri;
    // 资源ID
    @Column(name = "resource_id")
    private Long resourceId;
    // 是否可用
    @Column(name = "is_enable")
    private Boolean isEnable = Boolean.TRUE;
    // 是否在桌面展示-针对桌面图标
    @Column(name = "is_show_in_desktop")
    private Boolean isShowInDesktop = Boolean.TRUE;
    // 父菜单的主键
    @Column(name = "parent_id")
    private Long parentId;
    // 所有父节点
    @Column(name = "parent_ids", updatable = false)
    private String parentIds;
    // 所有父节点
    @Column(name = "parent_names", updatable = false)
    private String parentNames;
    // 所有父节点
    @Column(name = "parent_names_pinyin", updatable = false)
    private String parentNamesPinyin;
    // 是否系统定义-用户可以自定义菜单
    @Column(name = "is_system")
    private Boolean isSystem = Boolean.FALSE;
    // 用户ID-用于用户自定义
    @Column(name = "user_id")
    private Long userId;
    // 是否显示子菜单
    @Column(name = "is_show_sons")
    private Boolean isShowSons = Boolean.FALSE;
    // 是否为本引用的首页
    @Column(name = "is_index")
    private Boolean isIndex = Boolean.FALSE;
    // 序号
    @Column(name = "sort_num")
    private Integer sortNum;
    // 业务类别主键
    @Column(name = "business_type_id")
    private Long businessTypeId;
    // 描述
    @Column(name = "description")
    private String description;
    // 窗口打开模式，如果type为Define，那么width和height必须大于0，否则忽略这两个属性值
    @Column(name = "size_model")
    private String sizeModel = SizeModel.Adaptation.toString();
    // 窗口宽度，单位为px
    @Column(name = "width")
    private Integer width;
    // 窗口高度，单位为px
    @Column(name = "height")
    private Integer height;
    // 是否是父亲
    @Column(name = "is_parent")
    private Integer isParent = Integer.valueOf(0);
    // 是否有消息
    @Column(name = "has_message")
    private Boolean hasMessage = Boolean.FALSE;
    // 开发商是否可见
    @Column(name = "is_shown4_developer")
    private Boolean isShown4Developer = Boolean.FALSE;
    // 超管是否可见
    @Column(name = "is_shown4_admin")
    private Boolean isShown4Admin = Boolean.TRUE;
    // 系统管理员是否可见
    @Column(name = "is_shown4_system_manager")
    private Boolean isShown4SystemManager = Boolean.TRUE;
    // 外部用户是否可见-部分功能外部用户是不可见的
    @Column(name = "is_shown4_external_user")
    private Boolean isShown4ExternalUser = Boolean.TRUE;

    @Column(name = "siteId")
    private Long siteId;
    // 顶部显示
    @Column(name = "is_top")
    private Boolean isTop = Boolean.TRUE;

    // 图标
    @Column(name = "text_icon")
    private String textIcon;

    @Column(name = "open")
    private Boolean open = Boolean.FALSE;

    @Column(name = "pname")
    private String pname;

    @Transient
    private boolean isLeaf;

    @Transient
    boolean checked;

    @Transient
    private Long total; //数量

    @Transient
    private String keywords; //关键字

    @Transient
    private String excColumns;

    @Transient
    private String usersList;

    @Transient
    private String userNames;

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public Boolean getIsShown4Developer() {
        return isShown4Developer;
    }

    public void setIsShown4Developer(Boolean isShown4Developer) {
        this.isShown4Developer = isShown4Developer;
    }

    public Boolean getIsShown4Admin() {
        return isShown4Admin;
    }

    public void setIsShown4Admin(Boolean isShown4Admin) {
        this.isShown4Admin = isShown4Admin;
    }

    public Boolean getIsShown4SystemManager() {
        return isShown4SystemManager;
    }

    public Boolean getIsShown4ExternalUser() {
        return isShown4ExternalUser;
    }

    public void setIsShown4ExternalUser(Boolean isShown4ExternalUser) {
        this.isShown4ExternalUser = isShown4ExternalUser;
    }

    public void setIsShown4SystemManager(Boolean isShown4SystemManager) {
        this.isShown4SystemManager = isShown4SystemManager;
    }

    public Boolean getHasMessage() {
        return hasMessage;
    }

    public void setHasMessage(Boolean hasMessage) {
        this.hasMessage = hasMessage;
    }

    public String getWebServiceHost() {
        return webServiceHost;
    }

    public void setWebServiceHost(String webServiceHost) {
        this.webServiceHost = webServiceHost;
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

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getTextIcon() {
        return textIcon;
    }

    public void setTextIcon(String textIcon) {
        this.textIcon = textIcon;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getExcColumns() {
        return excColumns;
    }

    public void setExcColumns(String excColumns) {
        this.excColumns = excColumns;
    }

    public String getUsersList() {
        return usersList;
    }

    public void setUsersList(String usersList) {
        this.usersList = usersList;
    }

    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
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

    public String getParentNamesPinyin() {
        return parentNamesPinyin;
    }

    public void setParentNamesPinyin(String parentNamesPinyin) {
        this.parentNamesPinyin = parentNamesPinyin;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }

    public String getUrlPath(){
        StringBuilder sb = new StringBuilder();
        if(this.parentNamesPinyin != null && !this.parentNamesPinyin.equals("")){
            sb.append(this.parentNamesPinyin).append("/");
        }
        sb.append(this.namePinyin);
        return sb.toString();
    }
}