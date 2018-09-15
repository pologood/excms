package cn.lonsun.special.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.special.internal.vo.SpecialThumbVO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * 专题主题
 */
@Entity
@Table(name = "CMS_SPECIAL_THEME")
public class SpecialThemeEO extends AMockEntity implements Serializable {

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
     * 图片路径
     */
    @Column(name = "IMG_PATH")
    private String imgPath;

    /**
     * 专题路径
     */
    @Column(name = "PATH")
    private String path;

    /**
     * 可选颜色
     */
    @Column(name = "SELECT_COLOR")
    private String selectColor;

    /**
     * 皮肤样式
     */
    @Transient
    private List<SpecialSkinsEO> skins;

    @Transient
    private List<SpecialThumbVO> thumb;

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

    public void setImgPath(String ImgPath) {
        this.imgPath = ImgPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setPath(String Path) {
        this.path = Path;
    }

    public String getPath() {
        return path;
    }

    public void setSelectColor(String SelectColor) {
        this.selectColor = SelectColor;
    }

    public String getSelectColor() {
        return selectColor;
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
}
