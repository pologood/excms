package cn.lonsun.content.messageBoard.controller.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 留言管理操作记录表
 */
@Entity
@Table(name = "CMS_MESSAGE_BOARD_OPERATION")
public class MessageBoardOperationEO extends AMockEntity{

    public enum OperationStatus {
        Normal, Recover,Back;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //留言表ID
    @Column(name = "FORWARD_ID")
    private Long forwardId;

    //转办人ip
    @Column(name = "IP")
    private String ip;

    //操作人名称
    @Column(name = "USERNAME")
    private String username;

    //备注
    @Column(name = "REMARKS")
    private String remarks;

    //修改人名称
    @Column(name = "UPDATE_USER_NAME")
    private String updateUserName;

    //操作单位名称
    @Column(name = "CREATE_ORGAN_NAME")
    private String createOrganName;

    //操作状态，记录退回，收回，正常的状态
    @Column(name = "operation_status")
    private  String operationStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getForwardId() {
        return forwardId;
    }

    public void setForwardId(Long forwardId) {
        this.forwardId = forwardId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public String getCreateOrganName() {
        return createOrganName;
    }

    public void setCreateOrganName(String createOrganName) {
        this.createOrganName = createOrganName;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }
}
