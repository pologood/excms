package cn.lonsun.webservice.processEngine.vo;

import cn.lonsun.webservice.processEngine.enums.ETransactType;
import cn.lonsun.webservice.processEngine.util.AppValueUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2014/12/16.
 */
public class TaskHandleParam {
    private String title;

    private Long recordId;
    private Long nextActivityId;
    private List<AssigneeVO> taskAssigneeList;
    private List<AssigneeVO> taskReaderList;
    private HistDealwithcommentVO dealwithComment;
    private ETransactType transactType;
    private Map<String,String> data;
    private Map<String,Object> formData;

    /**
     * 日志描述
     */
    private String logDescr;

    /**
     * 是否限期
     */
    private Boolean isLimit = false;
    /**
     * 限期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date duedate;

    public TaskHandleParam(){}
    public static TaskHandleParam newInstance () {
        return new TaskHandleParam();
    }

    /**
     * 任务处理标题
     */
    public String getTitle () {
        return title;
    }

    public TaskHandleParam setTitle (String title) {
        this.title = title;
        return this;
    }

    /**
     * 下一步活动Id，如果为空，将会使用活动定义时指定的用户与角色
     */
    public Long getNextActivityId () {
        return nextActivityId;
    }

    public TaskHandleParam setNextActivityId (Long nextActivityId) {
        this.nextActivityId = nextActivityId;
        return this;
    }


    /**
     * 下一步任务处理人， 可以为空，当为空时将会使用活动定义中的用户
     */
    public List<AssigneeVO> getTaskAssigneeList () {
        return taskAssigneeList;
    }

    public TaskHandleParam setTaskAssigneeList (List<AssigneeVO> taskAssigneeList) {
        this.taskAssigneeList = taskAssigneeList;
        return this;
    }


    public Long getRecordId() {
        return recordId;
    }

    public TaskHandleParam setRecordId(Long recordId) {
        this.recordId = recordId;
        return this;
    }

    /**
     * 处理意见, 可以为空
     */
    public HistDealwithcommentVO getDealwithComment () {
        return dealwithComment;
    }

    public TaskHandleParam setDealwithComment (HistDealwithcommentVO dealwithComment) {
        this.dealwithComment = dealwithComment;
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }

    public TaskHandleParam setData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    /**
     * 办理方式，如果为空，使用活动定义的办理方式
     */
    public ETransactType getTransactType () {
        return transactType;
    }

    public TaskHandleParam setTransactType (ETransactType transactType) {
        this.transactType = transactType;
        return this;
    }

    public List<AssigneeVO> getTaskReaderList() {
        return taskReaderList;
    }

    public TaskHandleParam setTaskReaderList(List<AssigneeVO> taskReaderList) {
        this.taskReaderList = taskReaderList;
        return this;
    }

    public Boolean getIsLimit() {
        return isLimit;
    }

    public TaskHandleParam setIsLimit(Boolean isLimit) {
        this.isLimit = isLimit;
        return this;
    }

    public Date getDuedate() {
        return duedate;
    }

    public TaskHandleParam setDuedate(Date duedate) {
        this.duedate = duedate;
        return this;
    }

    public String getLogDescr() {
        return logDescr;
    }

    public TaskHandleParam setLogDescr(String logDescr) {
        this.logDescr = logDescr;
        return this;
    }

    public Map<String, Object> getFormData() {
        return formData;
    }

    public TaskHandleParam setFormData(Map<String, Object> formData) {
        this.formData = AppValueUtil.copyIndex0ByMap(formData);
        return this;
    }
}
