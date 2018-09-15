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
@Table(name="MONITOR_INDEX_MANAGE")
public class MonitorIndexManageEO extends AMockEntity {


    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private Long id;

    //一级指标
    @Column(name = "A_INDEX")
    private String aIndex;

    //一级指标名称
    @Column(name = "A_INDEX_NAME")
    private String aIndexName;

    //二级指标
    @Column(name = "B_INDEX")
    private String bIndex;

    //二级指标名称
    @Column(name = "B_INDEX_NAME")
    private String bIndexName;

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

    //定时任务ID
    @Column(name = "SCHEDULE_ID")
    private Long scheduleId;

    //任务检测状态
    @Column(name = "TASK_STATUS")
    private Integer taskStatus = 0;

    //任务启用状态
    @Column(name = "ENABLE_STATUS")
    private Integer enableStatus = 0;

    //关联正在执行的任务ID
    @Column(name = "TASK_ID")
    private Long taskId;

    //站点ID
    @Column(name = "SITE_ID")
    private Long siteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getaIndex() {
        return aIndex;
    }

    public void setaIndex(String aIndex) {
        this.aIndex = aIndex;
    }

    public String getaIndexName() {
        return aIndexName;
    }

    public void setaIndexName(String aIndexName) {
        this.aIndexName = aIndexName;
    }

    public String getbIndex() {
        return bIndex;
    }

    public void setbIndex(String bIndex) {
        this.bIndex = bIndex;
    }

    public String getbIndexName() {
        return bIndexName;
    }

    public void setbIndexName(String bIndexName) {
        this.bIndexName = bIndexName;
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

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(Integer enableStatus) {
        this.enableStatus = enableStatus;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
