package cn.lonsun.webservice.processEngine.vo;

import cn.lonsun.webservice.processEngine.enums.EProcInstState;
import cn.lonsun.webservice.processEngine.util.AppUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by lonsun on 2014/12/24.
 */
public class HistLogVO {
    public HistLogVO(){}
    /** 主键 */
    private Long hlogId;
    /** 流程定义 */
    /** 表单ID */
    private Long formId;
    /** 活动ID */
    /** 活动名称 */
    /** 描述 */
    private String descr;
    /** 状态(发起/办理/传阅/签收/退回/办结) */
    private EProcInstState state;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date create;
    /**
     * 签收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date signDate;
    /***
     * 办理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date taskEndDate;

    /**
     * 提交下一步时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date actEndDate;
    /**
     * 停留时间
     */
    private Long duration;
    /**上一步骤名称*/
    private String prevName;
    /**上一步处理人 */
    private String preAssignee;
    /**实际处理人ID */
    private Long handleUserId;
    /**实际处理人*/
    private String handleUsername;
    /**代理人 */
    private String agentId;
    /**代理人姓名*/
    private String agentName;
    /**任务分配处理人ID */
    private Long assigneeId;
    /** 任务分配处理人姓名 */
    private String assigneeName;
    /** 流程定义 */
    private Long processId;
    /**流程实例ID*/
    private Long procinstId;

    /**活动实例ID*/
    private Long actinstId;
    /** 活动ID */
    private Long activityId;
    /** 活动名称 */
    private String activityName;

    private Long htaskId;

    public Long getHlogId() {
        return hlogId;
    }

    public void setHlogId(Long hlogId) {
        this.hlogId = hlogId;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public EProcInstState getState() {
        return state;
    }

    public void setState(EProcInstState state) {
        this.state = state;
    }

    public Date getCreate() {
        return create;
    }

    public void setCreate(Date create) {
        this.create = create;
    }

    public Date getSignDate() {
        return signDate;
    }
    public String getSignDateStr() {
        if(null != signDate){
            return AppUtil.formatTimeToString(signDate,"yyyy-MM-dd HH:mm:ss");
        }
        return "";
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public Date getTaskEndDate() {
        return taskEndDate;
    }

    public String getTaskEndDateStr() {
        if(null != taskEndDate){
            return AppUtil.formatTimeToString(taskEndDate,"yyyy-MM-dd HH:mm:ss");
        }
        return "";
    }

    public void setTaskEndDate(Date taskEndDate) {
        this.taskEndDate = taskEndDate;
    }

    public Date getActEndDate() {
        return actEndDate;
    }

    public void setActEndDate(Date actEndDate) {
        this.actEndDate = actEndDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
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

    public Long getHandleUserId() {
        return handleUserId;
    }

    public void setHandleUserId(Long handleUserId) {
        this.handleUserId = handleUserId;
    }

    public String getHandleUsername() {
        return handleUsername;
    }

    public void setHandleUsername(String handleUsername) {
        this.handleUsername = handleUsername;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getProcinstId() {
        return procinstId;
    }

    public void setProcinstId(Long procinstId) {
        this.procinstId = procinstId;
    }

    public Long getActinstId() {
        return actinstId;
    }

    public void setActinstId(Long actinstId) {
        this.actinstId = actinstId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Long getHtaskId() {
        return htaskId;
    }

    public void setHtaskId(Long htaskId) {
        this.htaskId = htaskId;
    }
}
