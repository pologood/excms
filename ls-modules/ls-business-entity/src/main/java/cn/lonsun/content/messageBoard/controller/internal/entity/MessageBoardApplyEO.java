package cn.lonsun.content.messageBoard.controller.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 申请延期记录
 */
@Entity
@Table(name = "CMS_MESSAGE_BOARD_APPLY")
public class MessageBoardApplyEO extends AMockEntity{

    /**
     * 审核状态：disposeWait,待审核:disposeNotPass,审核不通过，disposePass审核通过
     *
     */
    public enum DisposeStatus {
        disposeWait, disposeNotPass,disposePass;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //留言表ID
    @Column(name = "MESSAGE_BOARD_ID")
    private Long messageBoardId;

    //留言表ID
    @Column(name = "FORWARD_ID")
    private Long forwardId;

    //申请人ip
    @Column(name = "APPLY_IP")
    private String applyIp;

    //申请天数
    @Column(name = "APPLY_DAYS")
    private Integer applyDays;

    //申请人名称
    @Column(name = "APPLY_NAME")
    private String applyName;

    //申请原因
    @Column(name = "APPLY_REASON")
    private String applyReason;

    //申请单位名称
    @Column(name = "APPLY_ORGAN_NAME")
    private String applyOrganName;

    //审核状态：disposeWait,待审核:disposeNotPass,审核不通过，disposePass审核通过
    @Column(name = "DISPOSE_STATUS")
    private String disposeStatus;

    //处理人IP
    @Column(name = "DISPOSE_IP")
    private String disposeIp;

    //处理结果原因
    @Column(name = "DISPOSE_REASON")
    private String disposeReason;

    //处理人
    @Column(name = "UPDATE_USER_NAME")
    private String updateUserName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageBoardId() {
        return messageBoardId;
    }

    public void setMessageBoardId(Long messageBoardId) {
        this.messageBoardId = messageBoardId;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public Long getForwardId() {
        return forwardId;
    }

    public void setForwardId(Long forwardId) {
        this.forwardId = forwardId;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public String getApplyOrganName() {
        return applyOrganName;
    }

    public void setApplyOrganName(String applyOrganName) {
        this.applyOrganName = applyOrganName;
    }

    public String getApplyIp() {
        return applyIp;
    }

    public void setApplyIp(String applyIp) {
        this.applyIp = applyIp;
    }

    public String getDisposeStatus() {
        return disposeStatus;
    }

    public void setDisposeStatus(String disposeStatus) {
        this.disposeStatus = disposeStatus;
    }

    public Integer getApplyDays() {
        return applyDays;
    }

    public void setApplyDays(Integer applyDays) {
        this.applyDays = applyDays;
    }

    public String getDisposeIp() {
        return disposeIp;
    }

    public void setDisposeIp(String disposeIp) {
        this.disposeIp = disposeIp;
    }

    public String getDisposeReason() {
        return disposeReason;
    }

    public void setDisposeReason(String disposeReason) {
        this.disposeReason = disposeReason;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }
}
