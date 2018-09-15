package cn.lonsun.datacollect.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2016-2-17 14:07
 */
@Entity
@Table(name = "CMS_DB_COLLECT_TASK")
public class DBCollectTaskEO extends AMockEntity {

    public enum dbType {
        orcale,
        mysql
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "COLUMN_ID")
    private Long columnId;

    @Column(name = "CSITE_ID")
    private Long cSiteId;

    @Transient
    private String columnName;

    @Column(name = "TASK_NAME")
    private String taskName;

    @Column(name = "DB_TYPE")
    private String dbType;

    @Column(name = "IP")
    private String ip;

    @Column(name = "PORT")
    private Integer port;

    @Column(name = "DATA_BASE")
    private String dataBase;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "COLLECT_TABLE")
    private String collectTable;

    @Column(name = "COLLECT_CONDITION")
    private String collectCondition;

    @Column(name = "COLLECT_COUNT")
    private Integer collectCount;

    @Column(name = "SITE_ID")
    private Long siteId;//主页地址

    @Column(name = "COLLECT_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date collectDate; //采集时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getcSiteId() {
        return cSiteId;
    }

    public void setcSiteId(Long cSiteId) {
        this.cSiteId = cSiteId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCollectTable() {
        return collectTable;
    }

    public void setCollectTable(String collectTable) {
        this.collectTable = collectTable;
    }

    public String getCollectCondition() {
        return collectCondition;
    }

    public void setCollectCondition(String collectCondition) {
        this.collectCondition = collectCondition;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Date getCollectDate() {
        return collectDate;
    }

    public void setCollectDate(Date collectDate) {
        this.collectDate = collectDate;
    }
}
