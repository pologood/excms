package cn.lonsun.webservice.processEngine.vo;

import cn.lonsun.webservice.processEngine.enums.EParticipationType;
import cn.lonsun.webservice.processEngine.enums.ETransactType;

import javax.persistence.Enumerated;

/**
 * Created by lonsun on 2014/12/15.
 */
public class ActivityVO {
    /**
     * 主键
     */
    private Long elementId;

    /**
     * 名称
     */
    private String name;

    /**
     * (0-活动；1-事件， 2-网关)
     */
    private Integer category;

    /**
     * 网关类型（0-并行网关， 1-串行网关）
     */
    private Integer gatewayType;

    /**
     * 事件类型(0-startevent， 1-endevent)
     */

    private Integer eventType;

    /**
     * 活动类型(0-service task， 1-user task )
     */

    private Integer activityType;

    /**
     * 办理方式（0-多单人；1-多人串办；2-多人并办）
     */
    @Enumerated
    private ETransactType transactType;

    /**
     * 参与者类型
     */

    private EParticipationType participation;

    /**
     * 处理人ID
     */

    private String assigneeId;

    /**
     * 处理人名称
     */

    private String assigneeName;

    /**
     * 表单类型(0-自定义表单;1链接)
     */
    private Integer formType;

    /**
     * 自定义表单ID
     */
    private Long formId;

    /**
     * 表单超链接
     */
    private String formHref;

    /**
     * 意见类型(0-无批示意见；1-对应字段批示意见；2-自动追加批示意见)
     */
    private Integer commentType;

    /**
     * 创建组织
     */
    private Long createOrgid;

    /**
     * 创建用户ID
     */
    private Long createUserid;

    /**
     * 过程定义
     */
    private Long processId;

    /**
     * 是否关键节点（关键节点不可跳过）
     */
    private Boolean isCriticalActivity = false;

    /**
     * 是否限制日期
     */
    private Boolean isLimit = false;

    /**
     * 限制天数-单位秒
     */
    private Integer limitDay;

    /**
     * 是否提前提醒
     */
    private Boolean limitAhead = false;

    /**
     * 提前时间-单位秒
     */
    private Long aheadTime;

    private String expValue;

    private String expDesc;

    /**
     * 外部调用class名称
     */
    private String fullClassName;

    /**
     * 回退类型
     */
    private Integer fallbackSet;
    /**
     * 办理人部门
     */
    private String assigneeOrgId;
    /**
     * 办理人单位
     */
    private String assigneeUnitId;
    /**
     * 是否有审仳方式选择
     */
    private Integer hasTransact;
    /**
     * 审批方式范围
     */
    private String transactScope;
    /**
     * 是否代理
     */
    private Integer canAgent;

    /**
     * 发起人循环代理
     */

    private Integer canPeriodicAgent;
    //支持退回发起人
    private Integer canAutoToOwner;
    private String assigneeOrgName;

    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(Integer gatewayType) {
        this.gatewayType = gatewayType;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    public ETransactType getTransactType() {
        return transactType;
    }

    public void setTransactType(ETransactType transactType) {
        this.transactType = transactType;
    }

    public EParticipationType getParticipation() {
        return participation;
    }

    public void setParticipation(EParticipationType participation) {
        this.participation = participation;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Integer getFormType() {
        return formType;
    }

    public void setFormType(Integer formType) {
        this.formType = formType;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public String getFormHref() {
        return formHref;
    }

    public void setFormHref(String formHref) {
        this.formHref = formHref;
    }

    public Integer getCommentType() {
        return commentType;
    }

    public void setCommentType(Integer commentType) {
        this.commentType = commentType;
    }

    public Long getCreateOrgid() {
        return createOrgid;
    }

    public void setCreateOrgid(Long createOrgid) {
        this.createOrgid = createOrgid;
    }

    public Long getCreateUserid() {
        return createUserid;
    }

    public void setCreateUserid(Long createUserid) {
        this.createUserid = createUserid;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Boolean getIsCriticalActivity() {
        return isCriticalActivity;
    }

    public void setIsCriticalActivity(Boolean isCriticalActivity) {
        this.isCriticalActivity = isCriticalActivity;
    }

    public Boolean getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Boolean isLimit) {
        this.isLimit = isLimit;
    }

    public Integer getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(Integer limitDay) {
        this.limitDay = limitDay;
    }

    public Boolean getLimitAhead() {
        return limitAhead;
    }

    public void setLimitAhead(Boolean limitAhead) {
        this.limitAhead = limitAhead;
    }

    public Long getAheadTime() {
        return aheadTime;
    }

    public void setAheadTime(Long aheadTime) {
        this.aheadTime = aheadTime;
    }

    public String getExpValue() {
        return expValue;
    }

    public void setExpValue(String expValue) {
        this.expValue = expValue;
    }

    public String getExpDesc() {
        return expDesc;
    }

    public void setExpDesc(String expDesc) {
        this.expDesc = expDesc;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public Integer getFallbackSet() {
        return fallbackSet;
    }

    public void setFallbackSet(Integer fallbackSet) {
        this.fallbackSet = fallbackSet;
    }

    public String getAssigneeOrgId () {
        return assigneeOrgId;
    }

    public void setAssigneeOrgId (String assigneeOrgId) {
        this.assigneeOrgId = assigneeOrgId;
    }

    public Integer getHasTransact() {
        return hasTransact;
    }

    public void setHasTransact(Integer hasTransact) {
        this.hasTransact = hasTransact;
    }

    public String getTransactScope() {
        return transactScope;
    }

    public void setTransactScope(String transactScope) {
        this.transactScope = transactScope;
    }

    public Integer getCanAgent() {
        return canAgent;
    }

    public void setCanAgent(Integer canAgent) {
        this.canAgent = canAgent;
    }

    public Integer getCanPeriodicAgent() {
        return canPeriodicAgent;
    }

    public void setCanPeriodicAgent(Integer canPeriodicAgent) {
        this.canPeriodicAgent = canPeriodicAgent;
    }

    public Integer getCanAutoToOwner() {
        return canAutoToOwner;
    }

    public void setCanAutoToOwner(Integer canAutoToOwner) {
        this.canAutoToOwner = canAutoToOwner;
    }

    public String getAssigneeOrgName() {
        return assigneeOrgName;
    }

    public void setAssigneeOrgName(String assigneeOrgName) {
        this.assigneeOrgName = assigneeOrgName;
    }

    public String getAssigneeUnitId() {
        return assigneeUnitId;
    }

    public void setAssigneeUnitId(String assigneeUnitId) {
        this.assigneeUnitId = assigneeUnitId;
    }
}
