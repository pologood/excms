/*
 * PublicWorksEO.java         2016年9月22日 <br/>
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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 每日工作动态 <br/>
 * 
 * @date 2016年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Entity
@Table(name = "CMS_PUBLIC_WORKS")
public class PublicWorksEO extends ABaseEntity {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;// 主键
    @Column(name = "LEARDERS_ID")
    private Long leadersId;// 领导id
    @OneToOne
    @JoinColumn(name = "LEARDERS_ID", updatable = false, insertable = false)
    private PublicLeadersEO leaders;// 单位领导
    @Column(name = "JOB_CONTENT")
    private String jobContent;// 工作内容
    @Column(name = "REMARK")
    private String remark;// 备注
    @Column(name = "ENABLE")
    private Boolean enable = true;// 启用、禁用
    @Column(name = "SITE_ID")
    private Long siteId;// 站点id
    @Column(name = "WORK_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd")
    private Date workDate;// 工作动态日期

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLeadersId() {
        return leadersId;
    }

    public void setLeadersId(Long leadersId) {
        this.leadersId = leadersId;
    }

    public PublicLeadersEO getLeaders() {
        return leaders;
    }

    public void setLeaders(PublicLeadersEO leaders) {
        this.leaders = leaders;
    }

    public String getJobContent() {
        return jobContent;
    }

    public void setJobContent(String jobContent) {
        this.jobContent = jobContent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }
}