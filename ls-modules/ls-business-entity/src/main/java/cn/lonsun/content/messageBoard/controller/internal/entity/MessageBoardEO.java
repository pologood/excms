package cn.lonsun.content.messageBoard.controller.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 留言管理
 */
@Entity
@Table(name = "CMS_MESSAGE_BOARD")
public class MessageBoardEO extends  AMockEntity{

    private static final long serialVersionUID = 2051195255124278812L;

    /**
     * 办理状态：已回复 replyed；未回复 unreply；已办结 handled；未办结 unhandle；办理中 handling；已退回 returned
     */
    public enum DealStatus {
        replyed, unreply,handled,unhandle,handling,returned;
    }

    /**
     * 留言类型：我要咨询 do_consult；我要投诉 do_complain；我要建议 do_suggest；我要举报 do_report；其他 others
     */
    public enum ClassCode {
        do_consult, do_complain,do_suggest,do_report,others;
    }

    /**
     * 留言评价：满意 satisfactory；不满意 unsatisfactory
     */
    public enum CommentCode {
        satisfactory, unsatisfactory
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    //留言内容
    @Column(name = "MESSAGE_BOARD_CONTENT")
    private String messageBoardContent;
    //留言人ID
    @Column(name = "PERSON_ID")
    private Long personId;
    //留言人IP
    @Column(name = "PERSON_IP")
    private String personIp;

    //留言类型,0表示咨询，1表示投诉，2表示建议，3表示其他
    @Column(name = "MESSAGE_BOARD_TYPE")
    private Integer type;//不使用
    //留言人姓名
    @Column(name = "PERSON_NAME")
    private String personName;
    //留言人电话
    @Column(name = "PERSON_PHONE")
    private String personPhone;
    //基础表主键
    @Column(name = "BASE_CONTENT_ID")
    private Long baseContentId;
    //关联问答知识库id
    @Column(name="KNOWLEDGE_BASE_ID")
    private Long knowledgeBaseId;
    @Column(name = "ADD_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;
    //留言类型
    @Column(name="class_code")
    private String classCode;
    @Column(name="class_name")
    private String className;

    @Column(name="is_public")
    private Integer isPublic=0;

    @Column(name="is_public_info")
    private Integer isPublicInfo=0;

    @Column(name="resource_type")
    private Integer resourceType=0;//0:网页，1：微信，2：微博,3:手机

    @Column(name="open_id")
    private String openId;//账号ID

    @Column(name="random_code")
    private String randomCode;

    @Column(name="doc_num")
    private String docNum;

    @Column(name="RECEIVE_UNIT_ID")
    private Long receiveUnitId;

    @Column(name = "RECEIVE_UNIT_NAME")
    private String receiveUnitName;

    @Column(name="RECEIVE_USER_CODE")
    private String receiveUserCode;

    @Column(name="create_unit_id",updatable = false)
    private Long createUnitId;

    @Column(name="DEAL_STATUS")
    private String dealStatus;

    @Column(name="comment_Code")
    private String commentCode;

    @Column(name="attach_Name")
    private String attachName;

    @Column(name="attach_Id")
    private String attachId;

    @Column(name="is_Read")
    private Integer isRead;

    //默认天数
    @Column(name = "default_Days")
    private Integer defaultDays;

    //到期时间
    @Column(name="due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date dueDate;

    //到期时间
    @Column(name="reply_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date replyDate;

    //地址
    @Column(name="address")
    private String address;

    //邮箱
    @Column(name="email")
    private String email;

    @Column(name="FORWARD_COUNT")
    private Integer forwardCount=0;

    @Column(name="OLD_ID")
    private String oldId;

    @Column(name="OLD_PERSON_ID")
    private String oldPersonId;

    public String getOldPersonId() {
        return oldPersonId;
    }
    public void setOldPersonId(String oldPersonId) {
        this.oldPersonId = oldPersonId;
    }

    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public String getAttachId() {
        return attachId;
    }

    public void setAttachId(String attachId) {
        this.attachId = attachId;
    }

    public String getCommentCode() {
        return commentCode;
    }

    public void setCommentCode(String commentCode) {
        this.commentCode = commentCode;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageBoardContent() {
        return messageBoardContent;
    }

    public void setMessageBoardContent(String messageBoardContent) {
        this.messageBoardContent = messageBoardContent;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getPersonIp() {
        return personIp;
    }

    public void setPersonIp(String personIp) {
        this.personIp = personIp;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public void setPersonPhone(String personPhone) {
        this.personPhone = personPhone;
    }

    public Long getBaseContentId() {
        return baseContentId;
    }

    public void setBaseContentId(Long baseContentId) {
        this.baseContentId = baseContentId;
    }

    public Long getKnowledgeBaseId() {
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(Long knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getIsPublicInfo() {
        return isPublicInfo;
    }

    public void setIsPublicInfo(Integer isPublicInfo) {
        this.isPublicInfo = isPublicInfo;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public Long getReceiveUnitId() {
        return receiveUnitId;
    }

    public void setReceiveUnitId(Long receiveUnitId) {
        this.receiveUnitId = receiveUnitId;
    }

    public String getReceiveUnitName() {
        return receiveUnitName;
    }

    public void setReceiveUnitName(String receiveUnitName) {
        this.receiveUnitName = receiveUnitName;
    }

    public String getReceiveUserCode() {
        return receiveUserCode;
    }

    public void setReceiveUserCode(String receiveUserCode) {
        this.receiveUserCode = receiveUserCode;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getDefaultDays() {
        return defaultDays;
    }

    public void setDefaultDays(Integer defaultDays) {
        this.defaultDays = defaultDays;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(Integer forwardCount) {
        this.forwardCount = forwardCount;
    }
}
