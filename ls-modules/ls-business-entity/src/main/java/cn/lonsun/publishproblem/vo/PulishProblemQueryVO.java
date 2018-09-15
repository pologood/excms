package cn.lonsun.publishproblem.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by huangxx on 2017/9/4.
 */
public class PulishProblemQueryVO extends PageQueryVO {

    private Integer isPublish;

    private String title;

    private String columnName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
