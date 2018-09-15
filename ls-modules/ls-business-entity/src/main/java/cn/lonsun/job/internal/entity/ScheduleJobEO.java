/*
 * ScheduleJobEO.java         2016年3月23日 <br/>
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

package cn.lonsun.job.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 定时任务 <br/>
 *
 * @date 2016年3月23日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Entity
@Table(name = "CMS_SCHEDULE_JOB")
public class ScheduleJobEO extends ABaseEntity {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;// 任务id
    @Column(name = "SITE_ID")
    private Long siteId;// 站点id
    @Column(name = "NAME")
    private String name;// 任务名称
    @Column(name = "TYPE")
    private String type;// 任务类型
    @Column(name = "CLAZZ")
    private String clazz;// 类路径
    @Column(name = "CRON_EXPRESSION")
    private String cronExpression;// 表达式
    @Column(name = "STATUS")
    private String status;// 状态
    @Column(name = "JSON")
    private String json;// 业务数据
    @Transient
    private String typeName;// 任务类型名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public ScheduleJobEO setSiteId(Long siteId) {
        this.siteId = siteId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ScheduleJobEO setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public ScheduleJobEO setType(String type) {
        this.type = type;
        return this;
    }

    public String getClazz() {
        return clazz;
    }

    public ScheduleJobEO setClazz(String clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public ScheduleJobEO setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJson() {
        return json;
    }

    public ScheduleJobEO setJson(String json) {
        this.json = json;
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}