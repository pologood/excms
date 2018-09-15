package cn.lonsun.content.messageBoard.controller.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 留言管理转办记录
 */
@Entity
@Table(name = "CMS_MESSAGE_BOARD_FORWARD")
public class MessageBoardForwardEO extends AMockEntity{

    public enum OperationStatus {
        Normal, Recover,Back,Forward;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //留言表ID
    @Column(name = "MESSAGE_BOARD_ID")
    private Long messageBoardId;

    //接受组织单位ID
    @Column(name = "RECEIVE_ORGAN_ID")
    private Long receiveOrganId;

    //转办人ip
    @Column(name = "IP")
    private String ip;

    //转办人名称
    @Column(name = "USERNAME")
    private String username;

    //备注
    @Column(name = "REMARKS")
    private String remarks;

    //接受单位名称
    @Column(name = "RECEIVE_UNIT_NAME")
    private String receiveUnitName;

    //接受单位名称
    @Column(name = "RECEIVE_USER_NAME")
    private String receiveUserName;

    //接受人编码
    @Column(name = "RECEIVE_USER_CODE")
    private String receiveUserCode;

    //操作状态，记录退回，收回，正常的状态
    @Column(name = "operation_status")
    private  String operationStatus;

    //附件id
    @Column(name = "ATTACH_ID")
    private String attachId;

    //附件名
    @Column(name = "ATTACH_NAME")
    private String attachName;

    //默认天数
    @Column(name = "default_Days")
    private Integer defaultDays;

    //到期时间
    @Column(name="due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date dueDate;

    @Column(name="local_unit_id")
    private String localUnitId;

    //办理状态（为方便统计使用，导入数据时要赋值）
    @Column(name="deal_status")
    private String dealStatus;

    //办理日期（为方便统计使用，导入数据时要赋值）
    @Column(name="reply_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date replyDate;

    @Transient
    private String localUnitName;

    public String getLocalUnitId() {
        return localUnitId;
    }

    public void setLocalUnitId(String localUnitId) {
        this.localUnitId = localUnitId;
    }

    public String getLocalUnitName() {
        return localUnitName;
    }

    public void setLocalUnitName(String localUnitName) {
        this.localUnitName = localUnitName;
    }

    public Integer getDefaultDays() {
        return defaultDays;
    }

    public void setDefaultDays(Integer defaultDays) {
        this.defaultDays = defaultDays;
    }

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

    public Long getReceiveOrganId() {
        return receiveOrganId;
    }

    public void setReceiveOrganId(Long receiveOrganId) {
        this.receiveOrganId = receiveOrganId;
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

    public String getReceiveUnitName() {
        return receiveUnitName;
    }

    public void setReceiveUnitName(String receiveUnitName) {
        this.receiveUnitName = receiveUnitName;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getReceiveUserCode() {
        return receiveUserCode;
    }

    public void setReceiveUserCode(String receiveUserCode) {
        this.receiveUserCode = receiveUserCode;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getAttachId() {
        return attachId;
    }

    public void setAttachId(String attachId) {
        this.attachId = attachId;
    }

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }
}
