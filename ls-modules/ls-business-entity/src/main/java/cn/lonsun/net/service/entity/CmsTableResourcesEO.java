package cn.lonsun.net.service.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name = "CMS_NET_TABLE_RESOURCES")
public class CmsTableResourcesEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "UPLOAD_URL")
    private String uploadUrl;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "ORGAN_ID")
    private String organId;

    @Column(name = "JOIN_DATE")
    private String joinDate;

    @Column(name = "SITE_ID")
    private Long siteId;

    @Transient
    private String organName;

    @Transient
    private String cIds;

    @Transient
    private String cNames;

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

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getcIds() {
        return cIds;
    }

    public void setcIds(String cIds) {
        this.cIds = cIds;
    }

    public String getcNames() {
        return cNames;
    }

    public void setcNames(String cNames) {
        this.cNames = cNames;
    }
}