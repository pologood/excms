/*
 * 2014-12-15 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.vo;

import cn.lonsun.webservice.processEngine.enums.ETransactType;
import cn.lonsun.webservice.processEngine.util.AppValueUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class StartProcessParam {
    private Long nextActivityId;
    private Long recordId;
    private String title;
    private List<AssigneeVO> taskAssigneeList;
    private List<AssigneeVO> taskReaderList;
    private ETransactType transactType;
    private Map<String,String> data;
    private Map<String,Object> formData;
    /**
     * 是否限期
     */
    private Boolean isLimit = false;
    /**
     * 限期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date duedate;
    public StartProcessParam(){

    }
    public static StartProcessParam newInstance() {
        return new StartProcessParam();
    }
    /**
     * 下一步活动
     * @return
     */
    public Long getNextActivityId () {
        return nextActivityId;
    }

    public StartProcessParam setNextActivityId (Long nextActivityId) {
        this.nextActivityId = nextActivityId;
        return this;
    }

    /**
     * 任务标题
     * @return
     */
    public String getTitle () {
        return title;
    }

    public StartProcessParam setTitle (String title) {
        this.title = title;
        return this;
    }

    /**
     *  外部业务表单id
     * @return
     */
    public Long getRecordId () {
        return recordId;
    }

    public StartProcessParam setRecordId (Long recordId) {
        this.recordId = recordId;
        return this;
    }

    /**
     * 任务处理人
     * @return
     */
    public List<AssigneeVO> getTaskAssigneeList () {
        return taskAssigneeList;
    }

    public StartProcessParam setTaskAssigneeList (List<AssigneeVO> taskAssigneeList) {
        this.taskAssigneeList = taskAssigneeList;
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }

    public StartProcessParam setData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    /**
     * 任务办理方式
     * @return
     */
    public ETransactType getTransactType () {
        return transactType;
    }

    public StartProcessParam setTransactType (ETransactType transactType) {
        this.transactType = transactType;
        return this;
    }

    public List<AssigneeVO> getTaskReaderList() {
        return taskReaderList;
    }

    public StartProcessParam setTaskReaderList(List<AssigneeVO> taskReaderList) {
        this.taskReaderList = taskReaderList;
        return this;
    }

    public Boolean getIsLimit() {
        return isLimit;
    }

    public StartProcessParam setIsLimit(Boolean isLimit) {
        this.isLimit = isLimit;
        return this;
    }

    public Date getDuedate() {
        return duedate;
    }

    public StartProcessParam setDuedate(Date duedate) {
        this.duedate = duedate;
        return this;
    }

    public Map<String, Object> getFormData() {
        return formData;
    }

    public StartProcessParam setFormData(Map<String, Object> formData) {
        this.formData = AppValueUtil.copyIndex0ByMap(formData);
        return this;
    }
}
