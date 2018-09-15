package cn.lonsun.special.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 专题主题
 */
@Entity
@Table(name = "CMS_SPECIAL_SKINS")
public class SpecialSkinsEO extends AMockEntity implements Serializable {

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
     * 名称
     */
    @Column(name = "COLOR")
    private String color;

    /**
     * 专题路径
     */
    @Column(name = "PATH")
    private String path;

    /**
     * 主题ID
     */
    @Column(name = "THEME_ID")
    private Long themeId;

    /**
     * 专题ID
     */
    @Column(name = "SPECIAL_ID")
    private Long specialID;

    /**
     * 是否默认
     */
    @Column(name = "DEFAULTS")
    private Integer defaults;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public Long getSpecialID() {
        return specialID;
    }

    public void setSpecialID(Long specialID) {
        this.specialID = specialID;
    }

    public Integer getDefaults() {
        return defaults;
    }

    public void setDefaults(Integer defaults) {
        this.defaults = defaults;
    }
}
