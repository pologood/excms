package cn.lonsun.monitor.task.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * @author gu.fei
 * @version 2017-09-28 9:12
 */
@Entity
@Table(name="MONITOR_SITE_STATIS")
public class MonitorSiteStatisEO extends AMockEntity {

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private Long id;

    //站点ID
    @Column(name="SITE_ID")
    private Long siteId;

    //站点ID
    @Column(name="SITE_NAME")
    private String siteName;

    //任务ID
    @Column(name="TASK_ID")
    private Long taskId;

    //是否合格
    @Column(name = "IS_QUALIFIED")
    private Integer isQualified;

    //总的得分
    @Column(name = "MONITOR_SCORE")
    private String monitorScore;

    //单项否决-站点无法访问
    @Column(name = "SITE_DENY")
    private Integer siteDeny;

    //单项否决-站点更新
    @Column(name = "SITE_UPDATE")
    private Integer siteUpdate;

    //单项否决-栏目更新
    @Column(name = "COLUMN_UPDATE")
    private Integer columnUpdate;

    //单项否决-严重错误
    @Column(name = "ERROR")
    private Integer error;

    //单项否决-互动回应
    @Column(name = "REPLY")
    private Integer reply;

    //综合评分-网站可用性
    @Column(name = "SITE_USE")
    private String siteUse;

    //综合评分-首页信息更新
    @Column(name = "INDEX_INFO_UPDATE")
    private String indexInfoUpdate;

    //综合评分-信息更新
    @Column(name = "INFO_UPDATE")
    private String infoUpdate;

    //综合评分-实时服务
    @Column(name = "SERVICE")
    private String service;

    //综合评分-互动回应
    @Column(name = "REPLY_SCOPE")
    private String replyScope;

    @Transient
    private Double score;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getIsQualified() {
        return isQualified;
    }

    public void setIsQualified(Integer isQualified) {
        this.isQualified = isQualified;
    }

    public String getMonitorScore() {
        return monitorScore;
    }

    public void setMonitorScore(String monitorScore) {
        this.monitorScore = monitorScore;
    }

    public Integer getSiteDeny() {
        return siteDeny;
    }

    public void setSiteDeny(Integer siteDeny) {
        this.siteDeny = siteDeny;
    }

    public Integer getSiteUpdate() {
        return siteUpdate;
    }

    public void setSiteUpdate(Integer siteUpdate) {
        this.siteUpdate = siteUpdate;
    }

    public Integer getColumnUpdate() {
        return columnUpdate;
    }

    public void setColumnUpdate(Integer columnUpdate) {
        this.columnUpdate = columnUpdate;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public Integer getReply() {
        return reply;
    }

    public void setReply(Integer reply) {
        this.reply = reply;
    }

    public String getSiteUse() {
        return siteUse;
    }

    public void setSiteUse(String siteUse) {
        this.siteUse = siteUse;
    }

    public String getIndexInfoUpdate() {
        return indexInfoUpdate;
    }

    public void setIndexInfoUpdate(String indexInfoUpdate) {
        this.indexInfoUpdate = indexInfoUpdate;
    }

    public String getInfoUpdate() {
        return infoUpdate;
    }

    public void setInfoUpdate(String infoUpdate) {
        this.infoUpdate = infoUpdate;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getReplyScope() {
        return replyScope;
    }

    public void setReplyScope(String replyScope) {
        this.replyScope = replyScope;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
