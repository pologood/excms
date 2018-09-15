package cn.lonsun.special.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 专题
 */
@Entity
@Table(name = "CMS_SPECIAL")
public class SpecialEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SITE_ID")
    private Long siteId;

    /**
     * 名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 主题ID
     */
    @Column(name = "THEME_ID")
    private Long themeId;

    /**
     * 首页模板ID
     */
    @Column(name = "INDEX_ID")
    private Long indexId;

    /**
     * 对应栏目的ID
     */
    @Column(name = "COLUMN_ROOT_ID")
    private Long columnRootId;

    /**
     * 模板项的根ID
     */
    @Column(name = "TEMPLATE_ROOT_ID")
    private Long templateRootId;

    /**
     * @return
     */
    @Column(name = "SPECIAL_STATUS")
    private Long specialStatus;

    /**
     * 默认皮肤
     */
    @Column(name = "DEFAULT_SKIN")
    private String defaultSkin;

    /**
     * 自定义组件样式列表
     *
     * @return
     */
    @Column(name = "STYLE_LIST")
    private String styleList;

    /**
     * 专题类型区分是栏目专题，还是子站专题。 0 栏目专题 1 子站专题
     *
     * @return
     */
    @Column(name = "SPECIAL_TYPE")
    private Integer specialType = 0;

    //模板对象JSON字符串
    @Column(name = "TEMPLATE_LIST")
    private String templateList;

    //栏目对象JSON字符串
    @Column(name = "COLUMN_LIST")
    private String columnList;

    //模型对象JSON字符串
    @Column(name = "MODEL_LIST")
    private String modelList;

    //自定义背景图片路径
    @Column(name = "PAGE_BACKGROUND")
    private String pageBackground;

    //可用组件列表
    @Column(name = "COMPONENTS")
    private String components;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public String getName() {
        return name;
    }

    public void setThemeId(Long ThemeId) {
        this.themeId = ThemeId;
    }

    public Long getThemeId() {
        return themeId;
    }

    public Long getIndexId() {
        return indexId;
    }

    public void setIndexId(Long indexId) {
        this.indexId = indexId;
    }

    public Long getColumnRootId() {
        return columnRootId;
    }

    public void setColumnRootId(Long columnRootId) {
        this.columnRootId = columnRootId;
    }

    public Long getTemplateRootId() {
        return templateRootId;
    }

    public void setTemplateRootId(Long templateRootId) {
        this.templateRootId = templateRootId;
    }

    public Long getSpecialStatus() {
        return specialStatus;
    }

    public void setSpecialStatus(Long specialStatus) {
        this.specialStatus = specialStatus;
    }

    public String getDefaultSkin() {
        return defaultSkin;
    }

    public void setDefaultSkin(String defaultSkin) {
        this.defaultSkin = defaultSkin;
    }

    public String getStyleList() {
        return styleList;
    }

    public void setStyleList(String styleList) {
        this.styleList = styleList;
    }

    public Integer getSpecialType() {
        return specialType;
    }

    public void setSpecialType(Integer specialType) {
        this.specialType = specialType;
    }

    public String getTemplateList() {
        return templateList;
    }

    public void setTemplateList(String templateList) {
        this.templateList = templateList;
    }

    public String getColumnList() {
        return columnList;
    }

    public void setColumnList(String columnList) {
        this.columnList = columnList;
    }

    public String getModelList() {
        return modelList;
    }

    public void setModelList(String modelList) {
        this.modelList = modelList;
    }

    public String getPageBackground() {
        return pageBackground;
    }

    public void setPageBackground(String pageBackground) {
        this.pageBackground = pageBackground;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }
}
