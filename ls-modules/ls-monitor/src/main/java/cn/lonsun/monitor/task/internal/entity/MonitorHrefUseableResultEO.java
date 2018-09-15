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
@Table(name="MONITOR_HREF_USEABLE_RESULT")
public class MonitorHrefUseableResultEO extends AMockEntity {


    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private Long id;

    //任务ID
    @Column(name="TASK_ID")
    private Long taskId;

    //检测时间
    @Column(name = "MONITOR_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date monitorDate;

    //访问地址
    @Column(name = "VISIT_URL")
    private String visitUrl;

    //访问地址
    @Column(name = "PARENT_URL")
    private String parentUrl;

    //是否可以访问
    @Column(name = "IS_VISITABLE")
    private Integer isVisitable = 0;

    //访问返回编码
    @Column(name = "RESP_CODE")
    private Integer respCode;

    //不可访问原因
    @Column(name = "REASON")
    private String reason;

    //是否首页
    @Column(name = "IS_INDEX")
    private Integer isIndex = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Date getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(Date monitorDate) {
        this.monitorDate = monitorDate;
    }

    public String getVisitUrl() {
        return visitUrl;
    }

    public void setVisitUrl(String visitUrl) {
        this.visitUrl = visitUrl;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public Integer getIsVisitable() {
        return isVisitable;
    }

    public void setIsVisitable(Integer isVisitable) {
        this.isVisitable = isVisitable;
    }

    public Integer getRespCode() {
        return respCode;
    }

    public void setRespCode(Integer respCode) {
        this.respCode = respCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getIsIndex() {
        return isIndex;
    }

    public void setIsIndex(Integer isIndex) {
        this.isIndex = isIndex;
    }
}
