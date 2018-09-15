/*
 * 2014-12-16 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.vo;

import cn.lonsun.webservice.processEngine.enums.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskVO {
    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 流程实例ID
     */
    private Long procinstId;

    /**
     * 执行服务ID
     */
    private Long executionId;

    /**
     * 流程定义ID
     */
    private Long processId;

    /**
     * 活动定义ID
     */
    private Long activityId;

    /**
     * 任务名称
     */
    private String name;

    private ETaskType type;

    /**
     * 描述
     */
    private String descr;

    /**
     * 任务状态
     */
    private ETaskStateType state;

    /**
     * 表单链接
     */
    private String formHref;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 创建时间
     */
    private Date create;
    /**
     * 签收时间
     */
    private Date signDate;
    /**
     * 任务到达时间
     */
    private Date arriveDate;
    /**
     * 到期时间
     */
    private Date duedate;

    /**
     * 是否已到期
     */
    private Boolean isDuedate;

    /**
     * 活动定义
     */
    private Long actinstId;

    /**
     * 默认名称(活动名称)
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

    /**
     * 用户ID
     */
    private Long assigneeId;

    /**
     * 用户名称
     */
    private String assigneeName;

    /**
     * 代理人Id
     */
    private String agentId;

    /**
     * 代理人姓名
     */
    private String agentName;
    private String agentOrgId;

    /**
     * 泳道
     */
    private Long swimlaneId;
    /**
     * 任务分配人单位ID
     */
    private Long assigneeOrgId;
    /**
     * 泳道名称
     */
    private String swimlaneName;
    /**
     * 泳道组织ID
     */
    private Long swimlaneUnitId;
    /**
     * 类型
     */
    private EParticipationType assignType;

    /**
     * 办理类型
     */
    private ETransactType handletype;

    /**
     * 办理循序， 此字段只用户多人串班的情况
     */
    private Integer handleIndex;

    /**
     * 任务所有人ID
     */
    private Long ownerId;

    /**
     * 任务所有人姓名
     */
    private String ownerName;

    /**
     * 任务所属人单位ID
     */
    private Long ownerUnitId;

    private Boolean hidden;

    /**
     * 回退类型
     */
    private EFallBack fallbackSet;

    private Boolean isFallback;
    /** 流程模块定义 */
    private Long moduleId;
    /**
     *
     * 流程模块编码
     */
    private String moduleCode;

    /** 业务实体主键 */
    private Long recordId;



    /** 创建用户ID */
    private Long createUserId;
    /** 创建用户 */
    private String createUser;
    /** 创建组织ID */
    private Long createOrgId;
    /** 创建组织 */
    private String createOrg;
    /** 创建单位ID */
    private Long createUnitId;
    /** 创建单位名称 */
    private String createUnitName;
    //支持退回发起人
    private Integer canAutoToOwner;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getProcinstId() {
        return procinstId;
    }

    public void setProcinstId(Long procinstId) {
        this.procinstId = procinstId;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ETaskType getType() {
        return type;
    }

    public void setType(ETaskType type) {
        this.type = type;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public ETaskStateType getState() {
        return state;
    }

    public void setState(ETaskStateType state) {
        this.state = state;
    }

    public String getFormHref() {
        return formHref;
    }

    public void setFormHref(String formHref) {
        this.formHref = formHref;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getCreate() {
        return create;
    }

    public void setCreate(Date create) {
        this.create = create;
    }

    public Date getDuedate() {
        return duedate;
    }

    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }

    public Long getActinstId() {
        return actinstId;
    }

    public void setActinstId(Long actinstId) {
        this.actinstId = actinstId;
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

    public Long getSwimlaneId() {
        return swimlaneId;
    }

    public void setSwimlaneId(Long swimlaneId) {
        this.swimlaneId = swimlaneId;
    }

    public String getSwimlaneName() {
        return swimlaneName;
    }

    public void setSwimlaneName(String swimlaneName) {
        this.swimlaneName = swimlaneName;
    }

    public EParticipationType getAssignType() {
        return assignType;
    }

    public void setAssignType(EParticipationType assignType) {
        this.assignType = assignType;
    }

    public ETransactType getHandletype() {
        return handletype;
    }

    public void setHandletype(ETransactType handletype) {
        this.handletype = handletype;
    }

    public Integer getHandleIndex() {
        return handleIndex;
    }

    public void setHandleIndex(Integer handleIndex) {
        this.handleIndex = handleIndex;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public EFallBack getFallbackSet() {
        return fallbackSet;
    }

    public void setFallbackSet(EFallBack fallbackSet) {
        this.fallbackSet = fallbackSet;
    }

    public Boolean getIsFallback() {
        return isFallback;
    }

    public void setIsFallback(Boolean isFallback) {
        this.isFallback = isFallback;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }



    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }



    public Long getOwnerUnitId() {
        return ownerUnitId;
    }

    public void setOwnerUnitId(Long ownerUnitId) {
        this.ownerUnitId = ownerUnitId;
    }

    public Boolean getIsDuedate() {
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date(System.currentTimeMillis());
        try {
            isDuedate =  duedate !=null && (duedate.compareTo(sdf.parse(sdf.format(now)))<0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isDuedate;
    }

    public void setIsDuedate(Boolean isDuedate) {
        this.isDuedate = isDuedate;
    }

    public Long getAssigneeOrgId() {
        return assigneeOrgId;
    }

    public void setAssigneeOrgId(Long assigneeOrgId) {
        this.assigneeOrgId = assigneeOrgId;
    }

    public String getAgentOrgId() {
        return agentOrgId;
    }

    public void setAgentOrgId(String agentOrgId) {
        this.agentOrgId = agentOrgId;
    }

    public Long getSwimlaneUnitId() {
        return swimlaneUnitId;
    }

    public void setSwimlaneUnitId(Long swimlaneUnitId) {
        this.swimlaneUnitId = swimlaneUnitId;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }


    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Long getCreateOrgId() {
        return createOrgId;
    }

    public void setCreateOrgId(Long createOrgId) {
        this.createOrgId = createOrgId;
    }

    public String getCreateOrg() {
        return createOrg;
    }

    public void setCreateOrg(String createOrg) {
        this.createOrg = createOrg;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

    public String getCreateUnitName() {
        return createUnitName;
    }

    public void setCreateUnitName(String createUnitName) {
        this.createUnitName = createUnitName;
    }

    public Integer getCanAutoToOwner() {
        return canAutoToOwner;
    }

    public void setCanAutoToOwner(Integer canAutoToOwner) {
        this.canAutoToOwner = canAutoToOwner;
    }

    public TaskVO() {
    }
}
