package cn.lonsun.supervise.columnupdate.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.quartz.Trigger;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_SUPERVISE_COLUMN_WARN")
public class ColumnUpdateWarnEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //主键ID

    @Column(name = "SCHED_ID")
    private Long schedId; //任务调度IDs

    @Column(name = "TASK_TYPE")
    private String taskType; //任务类型

    @Column(name = "PARENT_COLUMN_ID")
    private String parentColumnId; //父栏目ID

    @Column(name = "COLUMN_ID")
    private Long columnId; //栏目ID

    @Column(name = "COLUMN_NAME")
    private String columnName; //栏目名称

    @Column(name = "PUBLIC_ORGAN_ID")
    private String publicOrganId;//信息公开OrganId

    @Column(name = "PUBLIC_CODE")
    private String publicCode; //信息公开编码

    @Column(name = "TIME_MODE")
    private String timeMode; //定时方式 day、week、month

    @Column(name = "CRON_EXPRESS")
    private String cronExpress; //cron表达式

    @Column(name = "START_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startDate; //开始时间

    @Column(name = "SPACE_OF_DAY")
    private Integer spaceOfDay; //间隔天数

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

    @Column(name="CHECK_RESULT")
    private String checkResult; //检查结果

    @Column(name = "PLAN_UPDATE_NUM")
    private Long planUpdateNum; //计划更新数量

    @Column(name = "REAL_UPDATE_NUM")
    private Long realUpdateNum; //栏目实际更新数量

    @Column(name = "RUN_STATUS")
    private String runStatus = Trigger.TriggerState.NONE.toString(); //任务运行状态 默认禁止

    @Column(name = "UNREPLY_DATE_NUM")
    private Long unreplyDateNum; //未及时回复留言天数

    @Column(name = "UNREPLY_GUEST_NUM")
    private Long unreplyGuestNum; //未及时回复留言数量

    @Column(name = "IS_QUALIFIED")
    private Integer isQualified = 0; //是否达标 0：达标 1：不达标

    @Column(name = "SITE_ID")
    private Long siteId;//站点ID

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

    public String getParentColumnId() {
        return parentColumnId;
    }

    public void setParentColumnId(String parentColumnId) {
        this.parentColumnId = parentColumnId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getPublicOrganId() {
        return publicOrganId;
    }

    public void setPublicOrganId(String publicOrganId) {
        this.publicOrganId = publicOrganId;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String publicCode) {
        this.publicCode = publicCode;
    }

    public String getTimeMode() {
        return timeMode;
    }

    public void setTimeMode(String timeMode) {
        this.timeMode = timeMode;
    }

    public String getCronExpress() {
        return cronExpress;
    }

    public void setCronExpress(String cronExpress) {
        this.cronExpress = cronExpress;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getSpaceOfDay() {
        return spaceOfDay;
    }

    public void setSpaceOfDay(Integer spaceOfDay) {
        this.spaceOfDay = spaceOfDay;
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

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public Long getPlanUpdateNum() {
        return planUpdateNum;
    }

    public void setPlanUpdateNum(Long planUpdateNum) {
        this.planUpdateNum = planUpdateNum;
    }

    public Long getRealUpdateNum() {
        return realUpdateNum;
    }

    public void setRealUpdateNum(Long realUpdateNum) {
        this.realUpdateNum = realUpdateNum;
    }

    public String getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus;
    }

    public Long getUnreplyDateNum() {
        return unreplyDateNum;
    }

    public void setUnreplyDateNum(Long unreplyDateNum) {
        this.unreplyDateNum = unreplyDateNum;
    }

    public Long getUnreplyGuestNum() {
        return unreplyGuestNum;
    }

    public void setUnreplyGuestNum(Long unreplyGuestNum) {
        this.unreplyGuestNum = unreplyGuestNum;
    }

    public Integer getIsQualified() {
        return isQualified;
    }

    public void setIsQualified(Integer isQualified) {
        this.isQualified = isQualified;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}