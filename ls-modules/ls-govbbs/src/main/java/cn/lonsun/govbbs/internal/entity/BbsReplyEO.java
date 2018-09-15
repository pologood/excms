package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="CMS_BBS_REPLY")
public class BbsReplyEO extends ABaseEntity{


	/**
	 * TO_AUDIT, // 待审核  PASS,// 已审核  NO_PASS;// 已屏蔽
	 */
	public enum IsPublish{
		TO_AUDIT(0),
		PASS(1),
		NO_PASS(2);
		private Integer isPublish;
		private IsPublish(Integer isPublish){
			this.isPublish=isPublish;
		}
		public Integer getIsPublish(){
			return isPublish;
		}
	}

	public enum AddType {
		Member(1),
		TOURIST(2);
		private Integer addType;
		private AddType(Integer addType){
			this.addType=addType;
		}
		public Integer getAddType(){
			return addType;
		}
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 正常状态：Normal,已删除:Removed
	 *
	 * @author xujh
	 *
	 */
	public enum RecordStatus {
		Normal, Removed;
	}

	//(主键)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="REPLY_ID")
	private Long replyId;

	//(站点id)
	@Column(name="SITE_ID")
	private Long siteId;

	//(帖子id)
	@Column(name="POST_ID")
	private Long postId;

	//(帖子id)
	@Column(name="POST_TITLE")
	private String postTile;

	//(版块id)
	@Column(name="PLATE_ID")
	private Long plateId;	

	//(父节点串)	
	@Column(name="PARENT_IDS")
	private String parentIds;	
	
	//(内容)	
	@Column(name="CONTENT_")
	private String content;		
	
	//(会员id)		
	@Column(name="MEMBER_ID")
	private Long memberId;
	
	//(发帖人)	
	@Column(name="MEMBER_NAME")
	private String memberName;
	
	//(手机号)		
	@Column(name="MEMBER_PHONE")
	private String memberPhone;
	
	//(邮箱)	
	@Column(name="MEMBER_EMAIL")
	private String memberEmail;	
	
	//(地址)	
	@Column(name="MEMBER_ADDRESS")
	private String memberAddress;	
	
	//(回帖ip)		
	@Column(name="IP_")
	private String ip;
	
	//(发布状态)	
	@Column(name="IS_PUBLISH")
	private Integer isPublish = IsPublish.TO_AUDIT.getIsPublish();
	
	//(发布时间)
	@Column(name="PUBLISH_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date publishDate;

	//是否是办理信息
	@Column(name="IS_HANDLE")
	private Integer isHandle = 0;
	//办理单位id
	@Column(name="HANDLE_USET_ID")
	private Long handleUserId;

	//办理单位id
	@Column(name="HANDLE_UNIT_ID")
	private Long handleUnitId;

	//办理单位名称
	@Column(name="HANDLE_UNIT_NAME")
	private String handleUnitName;

	//办理时间
	@Column(name="HANDLE_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date handleTime;

	//(审核人)
	@Column(name="AUDIT_USER_ID")
	private Long auditUserId;

	//(审核人)
	@Column(name="AUDIT_USER_NAME")
	private String auditUserName;

	//(审核时间)
	@Column(name="AUDIT_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date auditTime;

	//是否关闭
	@Column(name="IS_COLSE")
	private Integer isColse = 0;

	@Column(name="COLSE_DESC")
	private String colseDesc;

	//添加人类型 0 系统用户  1 会员
	@Column(name="ADD_TYPE")
	private Integer addType = AddType.Member.getAddType();

	@Column(name = "RECORD_STATUS")
	private String recordStatus = RecordStatus.Normal.toString();

	@Column(name = "OLD_ID")
	private String oldId;

	@Column(name = "type")
	private String type;

	@Transient
	private String plateName;

	@Transient
	private String time;


	public Long getReplyId() {
		return replyId;
	}

	public Long getPostId() {
		return postId;
	}

	public Long getPlateId() {
		return plateId;
	}

	public String getParentIds() {
		return parentIds;
	}

	public String getContent() {
		return content;
	}

	public Long getMemberId() {
		return memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public String getMemberPhone() {
		return memberPhone;
	}

	public String getMemberEmail() {
		return memberEmail;
	}

	public String getMemberAddress() {
		return memberAddress;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}

	public String getIp() {
		return ip;
	}

	public Integer getIsPublish() {
		return isPublish;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public void setPlateId(Long plateId) {
		this.plateId = plateId;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Integer getIsHandle() {
		return isHandle;
	}

	public void setIsHandle(Integer isHandle) {
		this.isHandle = isHandle;
	}

	public String getColseDesc() {
		return colseDesc;
	}

	public void setColseDesc(String colseDesc) {
		this.colseDesc = colseDesc;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Integer getIsColse() {
		return isColse;
	}

	public void setIsColse(Integer isColse) {
		this.isColse = isColse;
	}

	public String getAuditUserName() {
		return auditUserName;
	}

	public void setAuditUserName(String auditUserName) {
		this.auditUserName = auditUserName;
	}

	public Long getAuditUserId() {
		return auditUserId;
	}

	public void setAuditUserId(Long auditUserId) {
		this.auditUserId = auditUserId;
	}

	public String getPostTile() {
		return postTile;
	}

	public void setPostTile(String postTile) {
		this.postTile = postTile;
	}

	public Long getHandleUnitId() {
		return handleUnitId;
	}

	public void setHandleUnitId(Long handleUnitId) {
		this.handleUnitId = handleUnitId;
	}

	public String getHandleUnitName() {
		return handleUnitName;
	}

	public void setHandleUnitName(String handleUnitName) {
		this.handleUnitName = handleUnitName;
	}

	public Date getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(Date handleTime) {
		this.handleTime = handleTime;
	}

	public Long getHandleUserId() {
		return handleUserId;
	}

	public void setHandleUserId(Long handleUserId) {
		this.handleUserId = handleUserId;
	}

	public String getPlateName() {
		return plateName;
	}

	public void setPlateName(String plateName) {
		this.plateName = plateName;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Integer getAddType() {
		return addType;
	}

	public void setAddType(Integer addType) {
		this.addType = addType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
