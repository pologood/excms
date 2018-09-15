package cn.lonsun.content.vo;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-1-5<br/>
 */

public class GuestBookEditVO {
    private Long id;
    // 管理员ID
    private Long userId;
    private String userName;
    // 留言内容
    private String guestBookContent;
    // 回复内容
    private String responseContent;

    private Integer isPublic = 0;

    private Integer isPublicInfo=0;
    // 留言人ID
    private Long personId;
    // 留言人IP
    private String personIp;
    // 留言类型,0表示咨询，1表示投诉，2表示建议，3表示其他
    private Integer type;
    // 留言人姓名
    private String personName;
    // 留言人电话
    private Long personPhone;
    // 基础表主键
    private Long baseContentId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date forwardDate;
    private String remarks;
    private Integer isResponse;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date replyDate;
    private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;
    private String receiveUserCode;
    private String receiveUserName;
    // 接受单位ID
    private Long receiveId;
    private String receiveName;
    private Integer recType;
    private String classCode;
    private String className;

    private Integer isPublish = 0;
    private String title;
    private Long siteId;
    private Long columnId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;
    private String typeCode = BaseContentEO.TypeCode.guestBook.toString();
    private String link;
    private Long hit = 0L;

    private String recordStatus = AMockEntity.RecordStatus.Normal.toString();
    private Long createUserId;
    private Long createOrganId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    private Long updateUserId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    private Integer resourceType = 0;// 0:网页，1：微信，2：微博,3:手机客户端

    private String openId;// 账号ID

    private String randomCode;

    private String replyUserName;

    private String replyUserId;

    private String replyUnitName;

    private Long replyUnitId;

    private String docNum;

    private Integer isTimeOut;

    private String commentCode;

    private String commentName;

    private String uri;

    private String dealStatus;

    private String statusName;
    private String columnName;// 所属栏目名称

    private Long createUnitId;

    private String localUnitId;

    private String localUnitName;

    private Integer isRead=0;

    //(超时天数)
    private Integer limitDay;

    //(黄牌天数)
    private Integer yellowCardDay;

    //(红牌天数)
    private Integer redCardDay;

    private Integer workFlowStatus;

    private Integer isTurn=0;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGuestBookContent() {
        return guestBookContent;
    }

    public void setGuestBookContent(String guestBookContent) {
        this.guestBookContent = guestBookContent;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
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

    public Long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
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

    public Long getPersonPhone() {
        return personPhone;
    }

    public void setPersonPhone(Long personPhone) {
        this.personPhone = personPhone;
    }

    public Long getBaseContentId() {
        return baseContentId;
    }

    public void setBaseContentId(Long baseContentId) {
        this.baseContentId = baseContentId;
    }

    public Date getForwardDate() {
        return forwardDate;
    }

    public void setForwardDate(Date forwardDate) {
        this.forwardDate = forwardDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getIsResponse() {
        return isResponse;
    }

    public void setIsResponse(Integer isResponse) {
        this.isResponse = isResponse;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getCreateOrganId() {
        return createOrganId;
    }

    public void setCreateOrganId(Long createOrganId) {
        this.createOrganId = createOrganId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getReceiveUserCode() {
        return receiveUserCode;
    }

    public void setReceiveUserCode(String receiveUserCode) {
        this.receiveUserCode = receiveUserCode;
    }

    public Integer getRecType() {
        return recType;
    }

    public void setRecType(Integer recType) {
        this.recType = recType;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
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

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public String getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(String replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public Integer getIsTimeOut() {
        return isTimeOut;
    }

    public void setIsTimeOut(Integer isTimeOut) {
        this.isTimeOut = isTimeOut;
    }

    public Long getHit() {
        return hit;
    }

    public void setHit(Long hit) {
        this.hit = hit;
    }

    public String getCommentCode() {
        return commentCode;
    }

    public void setCommentCode(String commentCode) {
        this.commentCode = commentCode;
    }

    public String getCommentName() {
        return commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Long getId() {
        return id;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getReplyUnitName() {
        return replyUnitName;
    }

    public void setReplyUnitName(String replyUnitName) {
        this.replyUnitName = replyUnitName;
    }

    public Long getReplyUnitId() {
        return replyUnitId;
    }

    public void setReplyUnitId(Long replyUnitId) {
        this.replyUnitId = replyUnitId;
    }

    public Integer getIsPublicInfo() {
        return isPublicInfo;
    }

    public void setIsPublicInfo(Integer isPublicInfo) {
        this.isPublicInfo = isPublicInfo;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

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


    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(Integer limitDay) {
        this.limitDay = limitDay;
    }

    public Integer getYellowCardDay() {
        return yellowCardDay;
    }

    public void setYellowCardDay(Integer yellowCardDay) {
        this.yellowCardDay = yellowCardDay;
    }

    public Integer getRedCardDay() {
        return redCardDay;
    }

    public void setRedCardDay(Integer redCardDay) {
        this.redCardDay = redCardDay;
    }

    public Integer getWorkFlowStatus() {
        return workFlowStatus;
    }

    public void setWorkFlowStatus(Integer workFlowStatus) {
        this.workFlowStatus = workFlowStatus;
    }

    public Integer getIsTurn() {
        return isTurn;
    }

    public void setIsTurn(Integer isTurn) {
        this.isTurn = isTurn;
    }
}