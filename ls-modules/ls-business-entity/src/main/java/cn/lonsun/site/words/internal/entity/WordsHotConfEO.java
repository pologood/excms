package cn.lonsun.site.words.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_WORDS_HOT_CONF")
public class WordsHotConfEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name="HOT_NAME")
    private String hotName;

    @Column(name="HOT_URL")
    private String hotUrl;

    @Column(name="OPEN_TYPE")
    private Integer openType;

    @Column(name="HOT_TYPE_ID")
    private Long hotTypeId;

    @Column(name="URL_DESC")
    private String urlDesc;

    @Column(name="SITE_ID")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHotName() {
        return hotName;
    }

    public void setHotName(String hotName) {
        this.hotName = hotName;
    }

    public String getHotUrl() {
        return hotUrl;
    }

    public void setHotUrl(String hotUrl) {
        this.hotUrl = hotUrl;
    }

    public Integer getOpenType() {
        return openType;
    }

    public void setOpenType(Integer openType) {
        this.openType = openType;
    }

    public Long getHotTypeId() {
        return hotTypeId;
    }

    public void setHotTypeId(Long hotTypeId) {
        this.hotTypeId = hotTypeId;
    }

    public String getUrlDesc() {
        return urlDesc;
    }

    public void setUrlDesc(String urlDesc) {
        this.urlDesc = urlDesc;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
