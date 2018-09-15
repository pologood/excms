package cn.lonsun.resourceMonitor.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by huangxx on 2017/4/11.
 */
@Entity
@Table(name = "CMS_RESOURCE_MONITOR")
public class ResourceMonitorEO extends AMockEntity{

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "RESOURCE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long resourceId;

    @Column(name = "RESOURCE_NAME")
    private String resourceName;

    @Column(name = "RESOURCE_ADDRESS")
    private String resourceAddress;

    @Column(name = "RESOURCE_REMARK")
    private String resourceRemark;

    @Column(name = "SITE_ID")
    private Long siteId;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceAddress() {
        return resourceAddress;
    }

    public void setResourceAddress(String resourceAddress) {
        this.resourceAddress = resourceAddress;
    }

    public String getResourceRemark() {
        return resourceRemark;
    }

    public void setResourceRemark(String resourceRemark) {
        this.resourceRemark = resourceRemark;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
