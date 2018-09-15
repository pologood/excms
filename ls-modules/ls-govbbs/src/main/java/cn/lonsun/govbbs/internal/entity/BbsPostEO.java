package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;

import java.util.Date;

/**
 * 主题信息
 */
@Entity
@Table(name="CMS_BBS_POST")
public class BbsPostEO extends ABaseEntity{


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public enum Status {
		Yes(1), // 是
		No(0);// 否
		private Integer status;
		private Status(Integer status){
			this.status=status;
		}
		public Integer getStatus(){
			return status;
		}
	}

	public enum AddType {
		TOURIST(2),
		Member(1),
		User(0);
		private Integer addType;
		private AddType(Integer addType){
			this.addType=addType;
		}
		public Integer getAddType(){
			return addType;
		}
	}

	public enum IsAccept {
		ToReply(0),
		Replyed(1);
		private Integer isAccept;
		private IsAccept(Integer isAccept){
			this.isAccept=isAccept;
		}
		public Integer getIsAccept(){
			return isAccept;
		}
	}



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


	/**
	 * 正常状态：Normal,已删除:Removed
	 *
	 * @author xujh
	 *
	 */
	public enum RecordStatus {
		Normal, Removed;
	}


	//(主键id)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="POST_ID")
	private Long postId;

	//(站点id)
	@Column(name="SITE_ID")
	private Long siteId;

	//(版块父节点串)
	@Column(name="PARENT_IDS")
	private String parentIds;

	//(版块id)
	@Column(name="PLATE_ID")
	private Long plateId;

	//(版块名称)
	@Column(name="PLATE_NAME")
	private String plateName;

	//(信息类型id)
	@Column(name="INFO_KEY")
	private String infoKey;

	//(信息类型)
	@Column(name="INFO_NAME")
	private String infoName;

	//(标题)
	@Column(name="TITLE")
	private String title;

	//(内容)
	@Column(name="CONTENT")
	private String content;

	//(浏览次数)
	@Column(name="VIEW_COUNT")
	private Integer viewCount = 0;

	//(回复次数)
	@Column(name="REPLY_COUNT")
	private Integer replyCount = 0;

	//(是否总置顶)
	@Column(name="IS_HEAD_TOP")
	private Integer isHeadTop = Status.No.getStatus();

	//(是否置顶)
	@Column(name="IS_TOP")
	private Integer isTop = Status.No.getStatus();

	//(是否精华、推荐)
	@Column(name="IS_ESSENCE")
	private Integer isEssence  = Status.No.getStatus();

	//(是否锁定)			
	@Column(name="IS_LOCK")
	private Integer isLock = Status.No.getStatus();

	//(是否受理)
	@Column(name="IS_ACCEPT")
	private Integer isAccept;

	//单位时间戳
	@Column(name="HANDLE_TIMES")
	private Long handleTimes;

	//(受理单位id)	
	@Column(name="ACCEPT_UNIT_ID")
	private Long acceptUnitId;

	//(受理单位名称)	
	@Column(name="ACCEPT_UNIT_NAME")
	private String acceptUnitName;

	//(单位发送时间)
	@Column(name="ACCEPT_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date acceptTime;

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

	//(会员id)		
	@Column(name="MEMBER_ID")
	private Long memberId;

	//(联系人)
	@Column(name="LINK_MAN")
	private String linkman;

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

	//(发帖ip)		
	@Column(name="IP_")
	private String ip;

	//(发布状态)
	@Column(name="IS_PUBLISH")
	private Integer isPublish = IsPublish.TO_AUDIT.getIsPublish();

	//(是否封贴)
	@Column(name="IS_COLSE")
	private Integer isColse = Status.No.getStatus();

	@Column(name="COLSE_DESC")
	private String colseDesc;

	//(发布时间)		
	@Column(name="PUBLISH_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date publishDate;

	//(审核回复id)		
	@Column(name="REPLY_ID")
	private Long replyId;

	@Column(name="RECORD_STATUS")
	private String recordStatus = RecordStatus.Normal.toString();

	//逾期时间戳
	@Column(name="OVERDUE_TIMES")
	private Long overdueTimes;

	//红牌时间戳
	@Column(name="YELLOW_TIMES")
	private Long yellowTimes;

	//红牌时间戳
	@Column(name="RED_TIMES")
	private Long redTimes;

	//添加人类型 0 系统用户  1 会员
	@Column(name="ADD_TYPE")
	private Integer addType = AddType.Member.getAddType();


	//最后回复人
	@Column(name="LAST_MEMBERID")
	private Long lastMemberId;

	//(审核人)
	@Column(name="LAST_MEMBER_NAME")
	private String lastMemberName;

	//(审核时间)
	@Column(name="LAST_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date lastTime;




	//1 附件  2图片 3 都有
	@Column(name="HAS_FILE")
	private Integer hasFile = 0;

	//参观游客
	@Column(name="VISIT_LIST")
	private String visitList;

	//支持
	@Column(name="SUPPORT")
	private Integer support = 0;

	//老平台Id
	@Column(name="OLD_ID")
	private String oldId;


	//火
	@Transient
	private Integer hot = 0;

	@Transient
	private Integer isTimeOut;

	@Transient
	private Integer redCard;

	@Transient
	private Integer yellowCard;

	@Transient
	private String changeFiled;

	@Transient//(0:已回复,1:未回复,2:逾期,3:黄牌，4：红牌)
	private Integer order;

	@Transient
	private Integer isNew;

	@Transient
	private String time;

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public Long getPlateId() {
		return plateId;
	}

	public void setPlateId(Long plateId) {
		this.plateId = plateId;
	}

	public String getPlateName() {
		return plateName;
	}

	public void setPlateName(String plateName) {
		this.plateName = plateName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	public Integer getIsHeadTop() {
		return isHeadTop;
	}

	public void setIsHeadTop(Integer isHeadTop) {
		this.isHeadTop = isHeadTop;
	}

	public Integer getIsTop() {
		return isTop;
	}

	public void setIsTop(Integer isTop) {
		this.isTop = isTop;
	}

	public Integer getIsEssence() {
		return isEssence;
	}

	public void setIsEssence(Integer isEssence) {
		this.isEssence = isEssence;
	}

	public Integer getIsLock() {
		return isLock;
	}

	public void setIsLock(Integer isLock) {
		this.isLock = isLock;
	}

	public Integer getIsAccept() {
		return isAccept;
	}

	public void setIsAccept(Integer isAccept) {
		this.isAccept = isAccept;
	}

	public Long getAcceptUnitId() {
		return acceptUnitId;
	}

	public void setAcceptUnitId(Long acceptUnitId) {
		this.acceptUnitId = acceptUnitId;
	}

	public String getAcceptUnitName() {
		return acceptUnitName;
	}

	public void setAcceptUnitName(String acceptUnitName) {
		this.acceptUnitName = acceptUnitName;
	}

	public Date getAcceptTime() {
		return acceptTime;
	}

	public void setAcceptTime(Date acceptTime) {
		this.acceptTime = acceptTime;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
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

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberPhone() {
		return memberPhone;
	}

	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}

	public String getMemberEmail() {
		return memberEmail;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public String getMemberAddress() {
		return memberAddress;
	}

	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public Integer getIsTimeOut() {
		return isTimeOut;
	}

	public void setIsTimeOut(Integer isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public Integer getRedCard() {
		return redCard;
	}

	public void setRedCard(Integer redCard) {
		this.redCard = redCard;
	}

	public Integer getYellowCard() {
		return yellowCard;
	}

	public void setYellowCard(Integer yellowCard) {
		this.yellowCard = yellowCard;
	}

	public Long getOverdueTimes() {
		return overdueTimes;
	}

	public void setOverdueTimes(Long overdueTimes) {
		this.overdueTimes = overdueTimes;
	}

	public Long getYellowTimes() {
		return yellowTimes;
	}

	public void setYellowTimes(Long yellowTimes) {
		this.yellowTimes = yellowTimes;
	}

	public Long getRedTimes() {
		return redTimes;
	}

	public void setRedTimes(Long redTimes) {
		this.redTimes = redTimes;
	}


	public Integer getAddType() {
		return addType;
	}

	public void setAddType(Integer addType) {
		this.addType = addType;
	}

	public String getChangeFiled() {
		return changeFiled;
	}

	public void setChangeFiled(String changeFiled) {
		this.changeFiled = changeFiled;
	}

	public String getInfoName() {
		return infoName;
	}

	public void setInfoName(String infoName) {
		this.infoName = infoName;
	}

	public String getInfoKey() {
		return infoKey;
	}

	public void setInfoKey(String infoKey) {
		this.infoKey = infoKey;
	}

	public Integer getIsColse() {
		return isColse;
	}

	public void setIsColse(Integer isColse) {
		this.isColse = isColse;
	}

	public String getColseDesc() {
		return colseDesc;
	}

	public void setColseDesc(String colseDesc) {
		this.colseDesc = colseDesc;
	}

	public Integer getHot() {
		return hot;
	}

	public void setHot(Integer hot) {
		this.hot = hot;
	}

	public Integer getHasFile() {
		return hasFile;
	}

	public void setHasFile(Integer hasFile) {
		this.hasFile = hasFile;
	}

	public Integer getSupport() {
		return support;
	}

	public void setSupport(Integer support) {
		this.support = support;
	}

	public Long getHandleTimes() {
		return handleTimes;
	}

	public void setHandleTimes(Long handleTimes) {
		this.handleTimes = handleTimes;
	}

	public Long getLastMemberId() {
		return lastMemberId;
	}

	public void setLastMemberId(Long lastMemberId) {
		this.lastMemberId = lastMemberId;
	}

	public String getLastMemberName() {
		return lastMemberName;
	}

	public void setLastMemberName(String lastMemberName) {
		this.lastMemberName = lastMemberName;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public Integer getIsNew() {
		return isNew;
	}

	public void setIsNew(Integer isNew) {
		this.isNew = isNew;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getVisitList() {
		return visitList;
	}

	public void setVisitList(String visitList) {
		this.visitList = visitList;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}
}
