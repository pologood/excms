package cn.lonsun.content.onlineDeclaration.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 在线申报<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-12<br/>
 */
@Entity
@Table(name="cms_online_declaration")
public class OnlineDeclarationEO extends AMockEntity {
    private static final long serialVersionUID = -1300742296285581640L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name="base_content_id")
    private Long baseContentId;

    @Column(name = "person_name")
    private String personName;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "down_url")
    private String downUrl;

    @Column(name = "address")
    private String address;

    @Column(name="phone_num")
    private Long phoneNum;

    @Column(name="attach_id")
    private String attachId;

    @Column(name="attach_name")
    private String attachName;

    @Column(name="fact_reason")
    private String factReason;

    @Column(name="rec_unit_id")
    private Long recUnitId;

    @Column(name="rec_unit_name")
    private String recUnitName;

    @Column(name="deal_status")
    private String dealStatus;

    @Column(name="reply_content")
    private String replyContent;

    @Column(name="reply_unit_Id")
    private String replyUnitId;

    @Transient
    private String replyUnitName;

    @Column(name = "reply_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date replyDate;

    @Column(name="random_code")
    private String randomCode;

    @Column(name="create_unit_id",updatable = false)
    private Long createUnitId;

    @Column(name="doc_num")
    private String docNum;

    @Transient
    private String statusName;

    @Column(name = "rec_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date recDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaseContentId() {
        return baseContentId;
    }

    public void setBaseContentId(Long baseContentId) {
        this.baseContentId = baseContentId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(Long phoneNum) {
        this.phoneNum = phoneNum;
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

    public String getFactReason() {
        return factReason;
    }

    public void setFactReason(String factReason) {
        this.factReason = factReason;
    }

    public Long getRecUnitId() {
        return recUnitId;
    }

    public void setRecUnitId(Long recUnitId) {
        this.recUnitId = recUnitId;
    }

    public String getRecUnitName() {
        return recUnitName;
    }

    public void setRecUnitName(String recUnitName) {
        this.recUnitName = recUnitName;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReplyUnitId() {
        return replyUnitId;
    }

    public void setReplyUnitId(String replyUnitId) {
        this.replyUnitId = replyUnitId;
    }

    public String getReplyUnitName() {
        return replyUnitName;
    }

    public void setReplyUnitName(String replyUnitName) {
        this.replyUnitName = replyUnitName;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
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

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

    public Date getRecDate() {
        return recDate;
    }

    public void setRecDate(Date recDate) {
        this.recDate = recDate;
    }
}
