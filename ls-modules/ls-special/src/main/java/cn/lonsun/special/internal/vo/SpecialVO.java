package cn.lonsun.special.internal.vo;

import cn.lonsun.special.internal.entity.SpecialSkinsEO;

import java.util.Date;
import java.util.List;

/**
 * Created by doocal on 2016-10-16.
 */
public class SpecialVO {

    private Long id;

    private String name;

    private Long themeId;

    private String themePath;

    private String themeName;

    private String themeImgPath;

    private Date createDate;

    private Long tplId;

    private Long columnId;

    private List<SpecialSkinsEO> skins;

    private List<SpecialThumbVO> thumb;

    private Long specialStatus;

    private Integer specialType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThemePath() {
        return themePath;
    }

    public void setThemePath(String themePath) {
        this.themePath = themePath;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public String getThemeImgPath() {
        return themeImgPath;
    }

    public void setThemeImgPath(String themeImgPath) {
        this.themeImgPath = themeImgPath;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getTplId() {
        return tplId;
    }

    public void setTplId(Long tplId) {
        this.tplId = tplId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public List<SpecialSkinsEO> getSkins() {
        return skins;
    }

    public void setSkins(List<SpecialSkinsEO> skins) {
        this.skins = skins;
    }

    public List<SpecialThumbVO> getThumb() {
        return thumb;
    }

    public void setThumb(List<SpecialThumbVO> thumb) {
        this.thumb = thumb;
    }

    public Integer getSpecialType() {
        return specialType;
    }

    public void setSpecialType(Integer specialType) {
        this.specialType = specialType;
    }

    public Long getSpecialStatus() {
        return specialStatus;
    }

    public void setSpecialStatus(Long specialStatus) {
        this.specialStatus = specialStatus;
    }
}
