package cn.lonsun.govbbs.internal.vo;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BbsReplyVO implements java.io.Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Long replyId;

	private Long postId;

	private Long plateId;

	private String plateName;
	
	private String title;
	
	private String postMemberName;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date postCreateTime;
	
	private String postIp;
	

	private String replyContent;
	
	private String replyMemberName;
	
	private String replyIp;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date replyCreateTime;
	
	private Integer isPublish;

	private Long auditUserId;

	//(审核人)
	private String auditUserName;

	//(审核时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date auditTime;

	//是否关闭
	private Integer isColse;

	private String colseDesc;


	public Long getPostId() {
		return postId;
	}

	public String getTitle() {
		return title;
	}

	public String getPostMemberName() {
		return postMemberName;
	}

	public Date getPostCreateTime() {
		return postCreateTime;
	}

	public String getPostIp() {
		return postIp;
	}

	public Long getReplyId() {
		return replyId;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public String getReplyMemberName() {
		return replyMemberName;
	}

	public String getReplyIp() {
		return replyIp;
	}

	public Date getReplyCreateTime() {
		return replyCreateTime;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPostMemberName(String postMemberName) {
		this.postMemberName = postMemberName;
	}

	public void setPostCreateTime(Date postCreateTime) {
		this.postCreateTime = postCreateTime;
	}

	public void setPostIp(String postIp) {
		this.postIp = postIp;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public void setReplyMemberName(String replyMemberName) {
		this.replyMemberName = replyMemberName;
	}

	public void setReplyIp(String replyIp) {
		this.replyIp = replyIp;
	}

	public void setReplyCreateTime(Date replyCreateTime) {
		this.replyCreateTime = replyCreateTime;
	}

	public Integer getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}


	public String getColseDesc() {
		return colseDesc;
	}

	public void setColseDesc(String colseDesc) {
		this.colseDesc = colseDesc;
	}

	public Integer getIsColse() {
		return isColse;
	}

	public void setIsColse(Integer isColse) {
		this.isColse = isColse;
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
}
