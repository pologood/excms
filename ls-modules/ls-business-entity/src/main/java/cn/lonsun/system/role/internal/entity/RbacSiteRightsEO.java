package cn.lonsun.system.role.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-9-24 14:52
 */
@Entity
@Table(name="RBAC_SITE_RIGHTS")
public class RbacSiteRightsEO extends ABaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name="ROLE_ID")
    private Long roleId;

    @Column(name="INDICATOR_ID")
    private Long indicatorId;

    @Column(name="OPT_CODE")
    private String optCode;

    @Column(name="TYPE")
    private String type;

    @Column(name="SITE_ID")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getOptCode() {
        return optCode;
    }

    public void setOptCode(String optCode) {
        this.optCode = optCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
