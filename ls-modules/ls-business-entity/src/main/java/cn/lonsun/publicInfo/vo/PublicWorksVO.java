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

package cn.lonsun.publicInfo.vo;

import cn.lonsun.core.base.entity.ABaseEntity;
import cn.lonsun.publicInfo.internal.entity.PublicLeadersEO;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 每日工作动态 <br/>
 * 
 * @date 2016年11月8日 <br/>
 * @author liuk <br/>
 * @version v1.0 <br/>
 */
public class PublicWorksVO{

    private Long id;// 主键
    private Long leadersId;// 领导id
    private PublicLeadersEO leaders;// 单位领导
    private String jobContent;// 工作内容
    private String remark;// 备注
    private Boolean enable = true;// 启用、禁用
    private Long siteId;// 站点id
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd")
    private Date workDate;// 工作动态日期

    /** 创建人ID */
    private Long createUserId;

    /**
     * 创建组织
     * */
    private Long createOrganId;

    /**
     * 创建时间 用户前端日期类型字符串自动转换 用户Date类型转换Json字符类型的格式化.
     * 注：数据库如果为TimeStamp类型存储的时间，在json输出的时候默认返回格林威治时间，需要增加timezone属性
     * */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * 更新人ID
     * */
    @Column(name = "UPDATE_USER_ID")
    private Long updateUserId;

    /**
     * 更新时间 用户前端日期类型字符串自动转换 用户Date类型转换Json字符类型的格式化.
     * 注：数据库如果为TimeStamp类型存储的时间，在json输出的时候默认返回格林威治时间，需要增加timezone属性
     * */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;//


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

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getCreateOrganId() {
        return createOrganId;
    }

    public void setCreateOrganId(Long createOrganId) {
        this.createOrganId = createOrganId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}