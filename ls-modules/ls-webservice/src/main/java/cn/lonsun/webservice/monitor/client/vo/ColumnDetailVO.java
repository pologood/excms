package cn.lonsun.webservice.monitor.client.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author liuk
 * @version 2017-11-23 11:45
 */
public class ColumnDetailVO {


    //监测任务id(主表id)
    private Long reportId;

    //所属栏目
    private String columnId;

    //栏目类型
    private String columnType;

    //栏目类型
    private String infoType = "news";

    //栏目更新数量
    private Long updateCount;

    private String columnName;

    //最后发布日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastPublishDate;

    //应更新周期 天数
    private Integer updateCycle;

    //应更新周期 年、月、周
    private String updateCycleStr;

    //未更新天数
    private Integer unPublishDays;

    //发布日期，存的是yyyy-MM-dd类型的字符串
    private String publishDetail;

    //栏目的访问地址
    private String columnUrl;

    public Long getReportId() {

        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Long getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(Long updateCount) {
        this.updateCount = updateCount;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Date getLastPublishDate() {
        return lastPublishDate;
    }

    public void setLastPublishDate(Date lastPublishDate) {
        this.lastPublishDate = lastPublishDate;
    }

    public Integer getUpdateCycle() {
        return updateCycle;
    }

    public void setUpdateCycle(Integer updateCycle) {
        this.updateCycle = updateCycle;
    }

    public Integer getUnPublishDays() {
        return unPublishDays;
    }

    public void setUnPublishDays(Integer unPublishDays) {
        this.unPublishDays = unPublishDays;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getPublishDetail() {
        return publishDetail;
    }

    public void setPublishDetail(String publishDetail) {
        this.publishDetail = publishDetail;
    }

    public String getColumnUrl() {
        return columnUrl;
    }

    public void setColumnUrl(String columnUrl) {
        this.columnUrl = columnUrl;
    }

    public String getUpdateCycleStr() {
        return updateCycleStr;
    }

    public void setUpdateCycleStr(String updateCycleStr) {
        this.updateCycleStr = updateCycleStr;
    }
}
