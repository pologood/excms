
package cn.lonsun.content.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;


/**
 * 
 * @ClassName: GuestBookEO
 * @Description: 留言类
 * @author hujun
 * @date 2015年10月26日 下午2:17:01
 * 
 */
@Entity
@Table(name = "GUESTBOOK_TABLE")
public class GuestBookEO extends AMockEntity{

	private static final long serialVersionUID = 2051195255124278812L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	//管理员ID
	@Column(name = "USER_ID")
	private Long userId;//不使用
	@Column(name = "USER_NAME")
	private String userName;//回复人使用
	//留言内容
	@Column(name = "GUESTBOOK_CONTENT")
	private String guestBookContent;
	//回复内容
	@Column(name = "RESPONSE_CONTENT")
	private String responseContent;

	//留言人ID
	@Column(name = "PERSON_ID")
	private Long personId;
	//留言人IP
	@Column(name = "PERSON_IP")
	private String personIp;
	//接受单位ID
	@Column(name = "RECEIVE_ID")
	private Long receiveId;

	@Column(name = "RECEIVE_NAME")
	private String receiveName;

	//留言类型,0表示咨询，1表示投诉，2表示建议，3表示其他
	@Column(name = "GUESTBOOK_TYPE")
	private Integer type;//不使用
	//留言人姓名
	@Column(name = "PERSON_NAME")
	private String personName;
	//留言人电话
	@Column(name = "PERSON_PHONE")
	private Long personPhone;
	//基础表主键
	@Column(name = "BASE_CONTENT_ID")
	private Long baseContentId;
	@Column(name = "FORWARD_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date forwardDate;
	@Column(name = "REMARKS")
	private String remarks;
	@Column(name = "IS_RESPONSE")
	private Integer isResponse ;//不使用
	@Column(name = "REPLY_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date replyDate;
	@Column(name = "PASSWORD")
	private String password;
	@Column(name = "ADD_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date addDate;
    @Column(name="receive_user_code")
	private String receiveUserCode;
	@Column(name="rec_type")
	private Integer recType;//不使用
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

	@Column(name="reply_user_name")
	private String replyUserName;

	@Column(name="reply_user_id")
	private String replyUserId;

	@Column(name="reply_unit_name")
	private String replyUnitName;

	@Column(name="reply_unit_id")
	private Long replyUnitId;

	@Column(name="doc_num")
	private String docNum;

    @Column(name="comment_code")
	private String commentCode;

	@Column(name="deal_status")
	private String dealStatus;

	@Column(name="create_unit_id",updatable = false)
	private Long createUnitId;

	@Column(name="local_unit_id")
	private String localUnitId;

	@Column(name="is_read")
    private Integer isRead=0;

	@Transient
	private String localUnitName;

	@Transient
	private String commentName;

	public Date getReplyDate() {
		return replyDate;
	}
	public void setReplyDate(Date replyDate) {
		this.replyDate = replyDate;
	}
	public Integer getIsResponse() {
		return isResponse;
	}
	public void setIsResponse(Integer isResponse) {
		this.isResponse = isResponse;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Date getForwardDate() {
		return forwardDate;
	}
	public void setForwardDate(Date forwardDate) {
		this.forwardDate = forwardDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	/*public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}*/
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getIsPublicInfo() {
		return isPublicInfo;
	}

	public void setIsPublicInfo(Integer isPublicInfo) {
		this.isPublicInfo = isPublicInfo;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public String getReplyUserId() {
		return replyUserId;
	}

	public void setReplyUserId(String replyUserId) {
		this.replyUserId = replyUserId;
	}

	public String getReplyUserName() {
		return replyUserName;
	}

	public void setReplyUserName(String replyUserName) {
		this.replyUserName = replyUserName;
	}

	public String getDocNum() {
		return docNum;
	}

	public void setDocNum(String docNum) {
		this.docNum = docNum;
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

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
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

	public String getCommentName() {
		return commentName;
	}

	public void setCommentName(String commentName) {
		this.commentName = commentName;
	}
}
