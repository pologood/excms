package cn.lonsun.webservice.processEngine.vo;



import cn.lonsun.webservice.processEngine.enums.EParticipationType;
import cn.lonsun.webservice.processEngine.enums.ETransactType;

import java.io.Serializable;

/**
 * 任务分派人 <br/>
 *
 * @author lonsun_01
 * @date: 2014-9-30 下午3:03:08 <br/>
 */
public class AssigneeVO implements Serializable {
    public AssigneeVO(){}
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;
    /** 单位ID */
    private Long userOrgId;
    /** 单位名称 */
    private String userOrgName;
    /**
     * 泳道
     */
    private Long swimlaneId;

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
    private EParticipationType type;

    /**
     * 办理类型
     */
    private ETransactType handletype;

    /**
     * 代理人id字符串
     */
    private String agentIds;
    /**
     * 代理人名称
     */
    private String agentNames;

    private String agentOrgIds;

    public AssigneeVO(Long assigeneeId, String assigneeName, EParticipationType type) {
        this.type = type;
        switch (this.type) {
            case inDepUser:
            case crossDepUser:
                this.userId = assigeneeId;
                this.userName = assigneeName;
                break;
            case role:
                this.swimlaneId = assigeneeId;
                this.swimlaneName = assigneeName;
                break;
        }
    }

    public AssigneeVO(UserInfoVO userInfo) {
        this.userId = userInfo.getUserId();
        this.userName = userInfo.getUserName();
        this.userOrgId = userInfo.getOrgId();
        this.userOrgName = userInfo.getOrgName();

        type = EParticipationType.inDepUser;
    }

    public AssigneeVO setUserId (Long UserId) {
        this.userId = UserId;
        return this;
    }

    public Long getUserId () {
        return userId;
    }

    public AssigneeVO setUserName (String UserName) {
        this.userName = UserName;
        return this;
    }

    public String getUserName () {
        return userName;
    }

    public AssigneeVO setSwimlaneId (Long SwimlaneId) {
        this.swimlaneId = SwimlaneId;
        return this;
    }

    public Long getSwimlaneId () {
        return swimlaneId;
    }

    public AssigneeVO setSwimlaneName (String SwimlaneName) {
        this.swimlaneName = SwimlaneName;
        return this;
    }

    public Long getSwimlaneUnitId() {
        return swimlaneUnitId;
    }

    public AssigneeVO setSwimlaneUnitId(Long swimlaneUnitId) {
        this.swimlaneUnitId = swimlaneUnitId;
        return this;
    }

    public String getSwimlaneName () {
        return swimlaneName;
    }

    public AssigneeVO setType (EParticipationType Type) {
        this.type = Type;
        return this;
    }

    public EParticipationType getType () {
        return type;
    }

    public AssigneeVO setHandletype (ETransactType Handletype) {
        this.handletype = Handletype;
        return this;
    }



    public String getUserOrgName() {
        return userOrgName;
    }

    public void setUserOrgName(String userOrgName) {
        this.userOrgName = userOrgName;
    }

    public Long getUserOrgId() {
        return userOrgId;
    }

    public void setUserOrgId(Long userOrgId) {
        this.userOrgId = userOrgId;
    }

    public ETransactType getHandletype () {
        return handletype;
    }

    public String getAgentIds () {
        return agentIds;
    }

    public AssigneeVO setAgentIds (String agentIds) {
        this.agentIds = agentIds;
        return this;
    }

    public String getAgentNames () {
        return agentNames;
    }

    public AssigneeVO setAgentNames (String agentNames) {
        this.agentNames = agentNames;
        return this;
    }

    public String getAgentOrgIds() {
        return agentOrgIds;
    }

    public void setAgentOrgIds(String agentOrgIds) {
        this.agentOrgIds = agentOrgIds;
    }
}
