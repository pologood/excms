package cn.lonsun.content.messageBoard.controller.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 留言管理回复记录
 */
@Entity
@Table(name = "CMS_MESSAGE_BOARD_REPLY")
public class MessageBoardReplyEO extends AMockEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //留言表ID
    @Column(name = "MESSAGE_BOARD_ID")
    private Long messageBoardId;

    //回复人ip
    @Column(name = "IP")
    private String ip;

    //回复人名称
    @Column(name = "USERNAME")
    private String username;

    //回复内容
    @Column(name = "REPLY_CONTENT")
    private String replyContent;

    //回复单位名称
    @Column(name = "RECEIVE_NAME")
    private String receiveName;

    //接受人编码
    @Column(name = "RECEIVE_USER_CODE")
    private String receiveUserCode;

    //办理状态
    @Column(name = "DEAL_STATUS")
    private String dealStatus;

    //附件id
    @Column(name = "ATTACH_ID")
    private String attachId;

    //附件名
    @Column(name = "ATTACH_NAME")
    private String attachName;

    @Column(name="comment_code")
    private String commentCode;

    @Column(name="forward_Id")
    private Long forwardId;

    @Column(name="is_super")
    private Integer isSuper;

    @Column(name="reply_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date replyDate;

    public Long getForwardId() {
        return forwardId;
    }

    public void setForwardId(Long forwardId) {
        this.forwardId = forwardId;
    }

    public String getCommentCode() {
        return commentCode;
    }

    public void setCommentCode(String commentCode) {
        this.commentCode = commentCode;
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

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceiveUserCode() {
        return receiveUserCode;
    }

    public void setReceiveUserCode(String receiveUserCode) {
        this.receiveUserCode = receiveUserCode;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
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

    public Integer getIsSuper() {
        return isSuper;
    }

    public void setIsSuper(Integer isSuper) {
        this.isSuper = isSuper;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }
}
