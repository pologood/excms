package cn.lonsun.site.serverInfo.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2017-08-01 15:51
 */
@Entity
@Table(name = "CMS_SITE_CONTROL")
public class SiteControlEO extends AMockEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //站点ID
    @Column(name = "SITE_ID")
    private Long siteId;

    //文件名称
    @Column(name = "FILE_NAME")
    private String fileName;

    //状态
    @Column(name = "STATUS")
    public Integer status = 0;

    @Transient
    private String siteName;

    @Transient
    private String domian;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDomian() {
        return domian;
    }

    public void setDomian(String domian) {
        this.domian = domian;
    }
}
