/*
 * OrganConfigEO.java         2015年12月9日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.publicInfo.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 单位机构配置 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月9日 <br/>
 */
@Entity
@Table(name = "CMS_ORGAN_CONFIG")
public class OrganConfigEO extends ABaseEntity {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ORGAN_ID")
    private Long organId;
    @Column(name = "CAT_ID")
    private Long catId;
    @Column(name = "CONTENT_MODEL_CODE")
    private String contentModelCode;
    @Column(name = "PUBLIC_ORGAN_ID")
    private Long publicOrganId;
    @Column(name = "LINK_PAGE_IDS")
    private String linkPageIds;
    @Column(name = "IS_INSTITUTION_LEVEL")
    private Boolean isInstitutionLevel = false;// 公开制度是否按级别分类
    @Column(name = "IS_GUIDE_LEVEL")
    private Boolean isGuideLevel = false;// 公开指南是否按级别分类
    @Column(name = "SORT_NUM")
    private Long sortNum;
    @Column(name = "IS_ENABLE")
    private Boolean isEnable = true;// 是否启用，默认开启信息公开的单位都是启用状态

    @Transient
    private String linkPageNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public String getContentModelCode() {
        return contentModelCode;
    }

    public void setContentModelCode(String contentModelCode) {
        this.contentModelCode = contentModelCode;
    }

    public Long getPublicOrganId() {
        return publicOrganId;
    }

    public void setPublicOrganId(Long publicOrganId) {
        this.publicOrganId = publicOrganId;
    }

    public String getLinkPageIds() {
        return linkPageIds;
    }

    public void setLinkPageIds(String linkPageIds) {
        this.linkPageIds = linkPageIds;
    }

    public Boolean getIsInstitutionLevel() {
        return isInstitutionLevel;
    }

    public void setIsInstitutionLevel(Boolean isInstitutionLevel) {
        this.isInstitutionLevel = isInstitutionLevel;
    }

    public Boolean getIsGuideLevel() {
        return isGuideLevel;
    }

    public void setIsGuideLevel(Boolean isGuideLevel) {
        this.isGuideLevel = isGuideLevel;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    public String getLinkPageNames() {
        return linkPageNames;
    }

    public void setLinkPageNames(String linkPageNames) {
        this.linkPageNames = linkPageNames;
    }
}