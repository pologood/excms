/*
 * OrganCatalogQueryVO.java         2015年12月22日 <br/>
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

package cn.lonsun.publicInfo.vo;

/**
 * 组织栏目查询对象 <br/>
 *
 * @date 2015年12月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class OrganCatalogQueryVO {

    private Long siteId;// 站点id
    private Long organId;// 组织id
    private Long parentId;// 父id
    private Boolean catalog = Boolean.FALSE;// 是否查询出配置过单位目录的单位信息，默认查询出全部单位
    private Boolean all = Boolean.TRUE;// 是否区分权限，默认不区分权限

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getCatalog() {
        return catalog;
    }

    public void setCatalog(Boolean catalog) {
        this.catalog = catalog;
    }

    public Boolean getAll() {
        return all;
    }

    public void setAll(Boolean all) {
        this.all = all;
    }
}