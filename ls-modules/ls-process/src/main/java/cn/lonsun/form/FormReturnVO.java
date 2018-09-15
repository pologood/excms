package cn.lonsun.form;

import java.util.Map;

/**
 * 表单数据更新后返回VO
 * Created by zhusy on 2016-7-30.
 */
public class FormReturnVO {

    private Long dataId;//数据主键

    private String title;//数据标题

    private Map<String,Object> data;//数据体,存储于工作流任务中,默认不用传


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
