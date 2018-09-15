package cn.lonsun.supervise.columnupdate.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_SUPERVISE_COLUMN_UPDATE")
public class ColumnUpdateEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum TastkType {
        article,
        guest,
        publicinfo
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //主键ID

    @Column(name = "SCHED_ID")
    private Long schedId; //任务调度IDs

    @Column(name = "TASK_TYPE")
    private String taskType; //任务类型

    @Column(name = "TASK_NAME")
    private String taskName; //任务名称

    @Column(name = "CRON_ID")
    private Long cronId; //触发器配置ID

    @Column(name = "CRON_DESC")
    private String cronDesc; //cron表达式描述

    @Column(name = "PREV_RUN_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date prevRunDate; //上次运行时间

    @Column(name = "PREV_SHOULD_RUN_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date prevShouldRunDate; //上次运行时间

    @Column(name = "NEXT_RUN_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date nextRunDate; //下次运行时间

    @Column(name="CHECK_RESULT_NUM")
    private Long checkResultNum; //检查结果

    @Column(name="CHECK_RESULT")
    private String checkResult; //检查结果

    @Column(name="MSG_ALERT")
    private Integer msgAlert = 0; //是否开通短信提醒 0：不提醒 1：提醒

    @Column(name = "ALERT_FREQ")
    private Integer alertFreq; //短信每分钟提醒频次

    @Column(name = "COLUMN_UPDATE_FREQ")
    private String columnUpdateFreq; //提醒频次 按月、周、日

    @Column(name = "COLUMN_UPDATE_NUM")
    private Long columnUpdateNum; //栏目更新的最少数量

    @Column(name = "COLUMN_IDS")
    private String columnIds; //需要更新的栏目

    @Column(name = "CSITE_IDS")
    private String cSiteIds;

    @Column(name = "RUN_STATUS")
    private Integer runStatus = 0; //任务运行状态 默认禁止

    @Column(name = "SITE_ID")
    private Long siteId;//站点ID

    @Column(name = "PUBLIC_NAMES")
    private String publicNames;

    @Column(name = "PUBLIC_TYPES")
    private String publicTypes;

    @Column(name = "ORGAN_IDS")
    private String organIds;

    @Transient
    private String columnNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchedId() {
        return schedId;
    }

    public void setSchedId(Long schedId) {
        this.schedId = schedId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getCronId() {
        return cronId;
    }

    public void setCronId(Long cronId) {
        this.cronId = cronId;
    }

    public String getCronDesc() {
        return cronDesc;
    }

    public void setCronDesc(String cronDesc) {
        this.cronDesc = cronDesc;
    }

    public Date getPrevRunDate() {
        return prevRunDate;
    }

    public void setPrevRunDate(Date prevRunDate) {
        this.prevRunDate = prevRunDate;
    }

    public Date getPrevShouldRunDate() {
        return prevShouldRunDate;
    }

    public void setPrevShouldRunDate(Date prevShouldRunDate) {
        this.prevShouldRunDate = prevShouldRunDate;
    }

    public Date getNextRunDate() {
        return nextRunDate;
    }

    public void setNextRunDate(Date nextRunDate) {
        this.nextRunDate = nextRunDate;
    }

    public Long getCheckResultNum() {
        return checkResultNum;
    }

    public void setCheckResultNum(Long checkResultNum) {
        this.checkResultNum = checkResultNum;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public Integer getMsgAlert() {
        return msgAlert;
    }

    public void setMsgAlert(Integer msgAlert) {
        this.msgAlert = msgAlert;
    }

    public Integer getAlertFreq() {
        return alertFreq;
    }

    public void setAlertFreq(Integer alertFreq) {
        this.alertFreq = alertFreq;
    }

    public String getColumnUpdateFreq() {
        return columnUpdateFreq;
    }

    public void setColumnUpdateFreq(String columnUpdateFreq) {
        this.columnUpdateFreq = columnUpdateFreq;
    }

    public Long getColumnUpdateNum() {
        return columnUpdateNum;
    }

    public void setColumnUpdateNum(Long columnUpdateNum) {
        this.columnUpdateNum = columnUpdateNum;
    }

    public String getColumnIds() {
        return columnIds;
    }

    public void setColumnIds(String columnIds) {
        this.columnIds = columnIds;
    }

    public String getcSiteIds() {
        return cSiteIds;
    }

    public void setcSiteIds(String cSiteIds) {
        this.cSiteIds = cSiteIds;
    }

    public Integer getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Integer runStatus) {
        this.runStatus = runStatus;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getPublicNames() {
        return publicNames;
    }

    public void setPublicNames(String publicNames) {
        this.publicNames = publicNames;
    }

    public String getPublicTypes() {
        return publicTypes;
    }

    public void setPublicTypes(String publicTypes) {
        this.publicTypes = publicTypes;
    }

    public String getOrganIds() {
        return organIds;
    }

    public void setOrganIds(String organIds) {
        this.organIds = organIds;
    }

    public String getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }
}