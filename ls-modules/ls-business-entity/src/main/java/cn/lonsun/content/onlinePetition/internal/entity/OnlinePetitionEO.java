package cn.lonsun.content.onlinePetition.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 网上信访<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */
@Entity
@Table(name="cms_online_petition")
public class OnlinePetitionEO extends AMockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="content_id")
    private Long contentId;

    @Column(name="category_code")
    private String categoryCode;

    @Column(name="purpose_code")
    private String purposeCode;

    @Column(name="is_public")
    private Integer isPublic=0;

    @Column(name="attach_id")
    private String attachId;

    @Column(name="attach_name")
    private String attachName;

    @Column(name="content")
    private String content;

    @Column(name="rec_unit_id")
    private Long recUnitId;

    @Column(name="rec_unit_name")
    private String recUnitName;

    @Column(name="occupation")
    private String occupation;

    @Column(name="phone_num")
    private Long phoneNum;

    @Column(name="address")
    private String address;

    @Column(name="column_id")
    private Long columnId;

    @Column(name="site_id")
    private Long siteId;

    @Column(name="deal_status")
    private String dealStatus;

    @Column(name="ip")
    private String ip;

    @Column(name="checkCode")
    private String checkCode;

    @Column(name="rec_type")
    private Integer recType=0;

    @Column(name="rec_user_id")
    private Long recUserId;

    @Column(name="rec_user_name")
    private String recUserName;

    @Column(name="create_unit_id",updatable = false)
    private Long createUnitId;

    @Transient
    private String statusName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Long getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(Long phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public Integer getRecType() {
        return recType;
    }

    public void setRecType(Integer recType) {
        this.recType = recType;
    }

    public Long getRecUserId() {
        return recUserId;
    }

    public void setRecUserId(Long recUserId) {
        this.recUserId = recUserId;
    }

    public String getRecUserName() {
        return recUserName;
    }

    public void setRecUserName(String recUserName) {
        this.recUserName = recUserName;
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
}

