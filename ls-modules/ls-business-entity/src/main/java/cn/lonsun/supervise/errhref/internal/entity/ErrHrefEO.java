package cn.lonsun.supervise.errhref.internal.entity;

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
@Table(name="CMS_SUPERVISE_ERR_HREF")
public class ErrHrefEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //主键ID

    @Column(name = "SCHED_ID")
    private Long schedId; //任务调度ID

    @Column(name = "TASK_NAME")
    private String taskName; //任务名称

    @Column(name = "WEB_DOMAIN")
    private String webDomain; //网站域名

    @Column(name = "WEB_SITE")
    private String webSite; //检测网址

    @Column(name = "CHARSET")
    private String charset; //网站编码

    @Column(name = "DEPTH")
    private Integer depth;//检测深度

    @Column(name = "FILTER_HREF")
    private String filterHref; //过滤链接

    @Column(name = "CRON_ID")
    private Long cronId; //触发器配置ID

    @Column(name = "CRON_DESC")
    private String cronDesc; //cron表达式描述

    @Column(name = "PREV_RUN_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date prevRunDate; //上次运行时间

    @Column(name = "NEXT_RUN_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date nextRunDate; //下次运行时间

    @Column(name="CHECK_RESULT_NUM")
    private Long checkResultNum = 0L; //检查结果

    @Column(name="CHECK_RESULT")
    private String checkResult; //检查结果

    @Column(name="MSG_ALERT")
    private Integer msgAlert = 0; //是否开通短信提醒 0：不提醒 1：提醒

    @Column(name = "ALERT_FREQ")
    private Integer alertFreq; //短信每分钟提醒频次

    @Column(name = "RUN_STATUS")
    private Integer runStatus = 0; //任务运行状态 默认禁止

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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getWebDomain() {
        return webDomain;
    }

    public void setWebDomain(String webDomain) {
        this.webDomain = webDomain;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getFilterHref() {
        return filterHref;
    }

    public void setFilterHref(String filterHref) {
        this.filterHref = filterHref;
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
}