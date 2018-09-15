package cn.lonsun.monitor.task.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-09-28 9:12
 */
@Entity
@Table(name="MONITOR_TASK_MANAGE")
public class MonitorTaskManageEO extends AMockEntity {


    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private Long id;

    //开始时间
    @Column(name = "START_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    //结束时间
    @Column(name = "FINISH_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date finishDate;

    //是否合格
    @Column(name = "IS_QUALIFIED")
    private Integer isQualified = 0;

    //任务检测状态
    @Column(name = "SITE_DENY_STATUS")
    private Integer siteDenyStatus = 0;

    //任务检测状态
    @Column(name = "SITE_UPDATE_STATUS")
    private Integer siteUpdateStatus = 0;

    //任务检测状态
    @Column(name = "COLUMN_UPDATE_STATUS")
    private Integer columnUpdateStatus = 0;

    //任务检测状态
    @Column(name = "ERROR_STATUS")
    private Integer errorStatus = 0;

    //任务检测状态
    @Column(name = "REPLY_STATUS")
    private Integer replyStatus = 0;

    //任务检测状态
    @Column(name = "SITE_USE_STATUS")
    private Integer siteUseStatus = 0;

    //任务检测状态
    @Column(name = "INFO_UPDATE_STATUS")
    private Integer infoUpdateStatus = 0;

    //任务检测状态
    @Column(name = "SERVICE_STATUS")
    private Integer serviceStatus = 0;

    //任务检测状态
    @Column(name = "REPLY_SCOPE_STATUS")
    private Integer replyScopeStatus = 0;

    //任务检测结果
    @Column(name = "RESULT")
    private String result;

    //站点ID
    @Column(name = "SITE_ID")
    private Long siteId;

    //报告ID
    @Column(name = "REPORT_ID")
    private Long reportId;

    @Transient
    private String taskStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getIsQualified() {
        return isQualified;
    }

    public void setIsQualified(Integer isQualified) {
        this.isQualified = isQualified;
    }

    public Integer getSiteDenyStatus() {
        return siteDenyStatus;
    }

    public void setSiteDenyStatus(Integer siteDenyStatus) {
        this.siteDenyStatus = siteDenyStatus;
    }

    public Integer getSiteUpdateStatus() {
        return siteUpdateStatus;
    }

    public void setSiteUpdateStatus(Integer siteUpdateStatus) {
        this.siteUpdateStatus = siteUpdateStatus;
    }

    public Integer getColumnUpdateStatus() {
        return columnUpdateStatus;
    }

    public void setColumnUpdateStatus(Integer columnUpdateStatus) {
        this.columnUpdateStatus = columnUpdateStatus;
    }

    public Integer getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(Integer errorStatus) {
        this.errorStatus = errorStatus;
    }

    public Integer getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(Integer replyStatus) {
        this.replyStatus = replyStatus;
    }

    public Integer getSiteUseStatus() {
        return siteUseStatus;
    }

    public void setSiteUseStatus(Integer siteUseStatus) {
        this.siteUseStatus = siteUseStatus;
    }

    public Integer getInfoUpdateStatus() {
        return infoUpdateStatus;
    }

    public void setInfoUpdateStatus(Integer infoUpdateStatus) {
        this.infoUpdateStatus = infoUpdateStatus;
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Integer getReplyScopeStatus() {
        return replyScopeStatus;
    }

    public void setReplyScopeStatus(Integer replyScopeStatus) {
        this.replyScopeStatus = replyScopeStatus;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}
