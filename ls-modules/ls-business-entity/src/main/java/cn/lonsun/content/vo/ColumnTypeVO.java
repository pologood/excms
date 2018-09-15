package cn.lonsun.content.vo;

/**
 * @author gu.fei
 * @version 2016-5-12 14:43
 */
public class ColumnTypeVO {

    private Long columnId;

    private Long siteId;

    private String columnName;

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
