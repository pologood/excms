/*
 * PublicWorksQueryVO.java         2016年9月22日 <br/>
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

import cn.lonsun.common.vo.PageQueryVO;

/**
 * TODO <br/>
 * 
 * @date 2016年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PublicWorksQueryVO extends PageQueryVO {

    private Long siteId;// 站点id
    private String leadersName;// 领导名称
    private Long organId;// 单位id
    private String organName;// 单位名称
    private Boolean enable;// 启用、禁用

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getLeadersName() {
        return leadersName;
    }

    public void setLeadersName(String leadersName) {
        this.leadersName = leadersName;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}