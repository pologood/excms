package cn.lonsun.special.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 专题云模板下载记录
 * @author zhongjun
 */
@Entity
@Table(name = "CMS_SPECIAL_download_log")
public class SpecialDownloadLogEO extends AMockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SITE_ID")
    private Long siteId;

    @Column(name = "cloud_Id")
    private Long cloudId;

    @Column(name = "special_Id")
    private Long specialThemeId;

    @Column(name = "version")
    private Integer version;
    /**下载状态， 0：下载中，1：已下载，2：下载失败*/
    @Column(name = "status")
    private Long status = 0l;

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

    public Long getCloudId() {
        return cloudId;
    }

    public void setCloudId(Long cloudId) {
        this.cloudId = cloudId;
    }

    public Long getSpecialThemeId() {
        return specialThemeId;
    }

    public void setSpecialThemeId(Long specialThemeId) {
        this.specialThemeId = specialThemeId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
