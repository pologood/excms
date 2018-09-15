package cn.lonsun.webservice.processEngine.vo;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Map;

/**
 * Created by lonsun on 2014/12/16.
 */
public class QueryTask {
    /***
     * 模块名称
     */
    private String moduleCode;
    /**
     * 任务名称
     */
    private String title;
    /**
     * 上一步骤名称
     */
    private String prevName;
    /**
     * 上一步处理人
     */
    private String preAssignee;

    private String name;

    /**业务数据*/
    private Map<String,String> data;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date arriveStartDate;//创建日期开始时间

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date arriveEndDate;//创建日期结束时间

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrevName() {
        return prevName;
    }

    public void setPrevName(String prevName) {
        this.prevName = prevName;
    }

    public String getPreAssignee() {
        return preAssignee;
    }

    public void setPreAssignee(String preAssignee) {
        this.preAssignee = preAssignee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Date getArriveStartDate() {
        return arriveStartDate;
    }

    public void setArriveStartDate(Date arriveStartDate) {
        this.arriveStartDate = arriveStartDate;
    }

    public Date getArriveEndDate() {
        return arriveEndDate;
    }

    public void setArriveEndDate(Date arriveEndDate) {
        this.arriveEndDate = arriveEndDate;
    }
}
