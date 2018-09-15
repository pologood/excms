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
@Table(name="MONITOR_SERIOUS_ERROR_RESULT")
public class MonitorSeriousErrorResultEO extends AMockEntity {


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

    //文章ID
    @Column(name="CONTENT_ID")
    private Long contentId;

    //标题
    @Column(name = "TITLE")
    private String title;

    //检测到词汇
    @Column(name = "WORD")
    private String word;

    //文章类型
    @Column(name = "TYPE_CODE")
    private String typeCode;

    //严重错误来源
    @Column(name = "FROM_CODE")
    private String fromCode;

    //词汇类型
    @Column(name = "CHECK_TYPE")
    private String checkType;

    //结果
    @Column(name = "RESULT")
    private String result;

    //域名
    @Column(name = "DOMAIN")
    private String domain;

    //栏目ID
    @Column(name="COLUMN_ID")
    private Long columnId;

    @Transient
    private String siteName;

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

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
