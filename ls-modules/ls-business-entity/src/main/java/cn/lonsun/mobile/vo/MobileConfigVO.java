package cn.lonsun.mobile.vo;

/**
 * @author DooCal
 * @ClassName: MobileConfigVO
 * @Description:
 * @date 2016/7/5 10:31
 */
public class MobileConfigVO {

    // 类型 nav focus articleNews special
    private String type;

    // 同步到栏目的ID
    private String columnIds;

    private String checkedIds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnIds() {
        return columnIds;
    }

    public void setColumnIds(String columnIds) {
        this.columnIds = columnIds;
    }

    public String getCheckedIds() {
        return checkedIds;
    }

    public void setCheckedIds(String checkedIds) {
        this.checkedIds = checkedIds;
    }
}
