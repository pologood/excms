package cn.lonsun.webservice.monitor.client.vo;

/**
 * @author liuk
 * @version 2017-11-23 11:45
 */
public class InteractDetailVO {


    //监测任务id(主表id)
    private Long reportId;

    //所属栏目
    private Long columnId;

    //栏目类型
    private String columnType;

    //栏目更新数量
    private Long updateCount;

    //未回复数量
    private Long unreplyCount;

    private String columnName;

    private String columnUrl;

    public Long getReportId() {

        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
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

    public Long getUnreplyCount() {
        return unreplyCount;
    }

    public void setUnreplyCount(Long unreplyCount) {
        this.unreplyCount = unreplyCount;
    }

    public String getColumnUrl() {
        return columnUrl;
    }

    public void setColumnUrl(String columnUrl) {
        this.columnUrl = columnUrl;
    }
}
