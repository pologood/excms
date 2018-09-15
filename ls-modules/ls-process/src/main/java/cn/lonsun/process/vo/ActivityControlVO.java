package cn.lonsun.process.vo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.RecOrganVO;
import cn.lonsun.common.vo.RecRoleVO;
import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.webservice.processEngine.enums.EParticipationType;
import cn.lonsun.webservice.processEngine.enums.ETransactType;

import java.util.List;

/**
 * Created by zhusy on 2015-6-6.
 */
public class ActivityControlVO {

    private Long elementId;//活动定义ID

    private String name;//活动名称

    private Boolean isEndActivity;//是否结束活动

    private EParticipationType participation;//参与者类型

    private Boolean isLimit;//是否限期

    private Boolean hasTransact;//是否有审批方式

    private String transactScope;//审批方式范围

    private Boolean canAgent;//是否允许代填

    private Integer canPeriodicAgent;//是否允许发起人代填

    private String assigneeId;

    private String assigneeName;

    private String assigneeOrgId;

    private String assigneeOrgName;

    private List<ReceiverVO> handlePersons;//指定的办理人

    private List<RecOrganVO> handleOrgans;//指定的办理部门

    private List<RecRoleVO> handleRoles;//指定的办理角色

    private String defaultTransact;//默认审批方式

    private Boolean isNeedSelect = Boolean.TRUE;//是否需要选择



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

    public Boolean getIsEndActivity() {
        return isEndActivity;
    }

    public void setIsEndActivity(Boolean isEndActivity) {
        this.isEndActivity = isEndActivity;
    }

    public EParticipationType getParticipation() {
        return participation;
    }

    public void setParticipation(EParticipationType participation) {
        this.participation = participation;
    }

    public Boolean getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Boolean isLimit) {
        this.isLimit = isLimit;
    }

    public Boolean getHasTransact() {
        return hasTransact;
    }

    public void setHasTransact(Boolean hasTransact) {
        this.hasTransact = hasTransact;
    }

    public String getTransactScope() {
        return transactScope;
    }

    public void setTransactScope(String transactScope) {
        this.transactScope = transactScope;
    }

    public Boolean getCanAgent() {
        return canAgent;
    }

    public void setCanAgent(Boolean canAgent) {
        this.canAgent = canAgent;
    }

    public Integer getCanPeriodicAgent() {
        return canPeriodicAgent;
    }

    public void setCanPeriodicAgent(Integer canPeriodicAgent) {
        this.canPeriodicAgent = canPeriodicAgent;
    }

    public List<ReceiverVO> getHandlePersons() {
        return handlePersons;
    }

    public void  setHandlePersons(List<ReceiverVO> handlePersons) {
        this.handlePersons = handlePersons;
    }

    public String getDefaultTransact() {
        if(!hasTransact){
           return ETransactType.single.toString();
        }else if(!AppUtil.isEmpty(transactScope)){
            if(transactScope.indexOf(ETransactType.single.toString()) > -1){
                return ETransactType.single.toString();
            }else if(transactScope.indexOf(ETransactType.serial.toString()) > -1){
                return ETransactType.serial.toString();
            }else if(transactScope.indexOf(ETransactType.parallel.toString()) > -1){
                return ETransactType.parallel.toString();
            }
        }
        return ETransactType.single.toString();
    }

    public List<RecOrganVO> getHandleOrgans() {
        return handleOrgans;
    }

    public void setHandleOrgans(List<RecOrganVO> handleOrgans) {
        this.handleOrgans = handleOrgans;
    }

    public List<RecRoleVO> getHandleRoles() {
        return handleRoles;
    }

    public void setHandleRoles(List<RecRoleVO> handleRoles) {
        this.handleRoles = handleRoles;
    }

    public Boolean getIsNeedSelect() {
        if(EParticipationType.candidate.equals(participation)){//参与者类型为候选人
            isNeedSelect = false;
        }else if(EParticipationType.role.equals(participation)){//参与着类型为角色
            //只有当流程中设置的角色指定了单位才不需要显示选择按钮
            if(null != handleRoles && handleRoles.size() > 0){
                boolean flag = false;
                for(RecRoleVO vo : handleRoles){
                    if(null == vo.getRoleId() || null == vo.getUnitId()){
                        flag = true;
                        break;
                    }
                }
                isNeedSelect = flag;
            }
        }else if(EParticipationType.promoter.equals(participation)){//参与者类型为发起人
            isNeedSelect = false;
        }else if(EParticipationType.dept.equals(participation)){
            if(null != handleOrgans && handleOrgans.size() > 0){
                isNeedSelect = false;
            }
        }
        return isNeedSelect;
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

    public String getAssigneeOrgId() {
        return assigneeOrgId;
    }

    public void setAssigneeOrgId(String assigneeOrgId) {
        this.assigneeOrgId = assigneeOrgId;
    }

    public String getAssigneeOrgName() {
        return assigneeOrgName;
    }

    public void setAssigneeOrgName(String assigneeOrgName) {
        this.assigneeOrgName = assigneeOrgName;
    }
}
