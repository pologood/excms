package cn.lonsun.datacollect.vo;

/**
 * @author gu.fei
 * @version 2016-1-26 14:54
 */
public class ColumnVO {

    private String columnName;

    private String dataType;

    private String dataLength;

    private String comments;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
