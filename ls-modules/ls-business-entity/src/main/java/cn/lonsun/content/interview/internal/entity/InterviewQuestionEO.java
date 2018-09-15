package cn.lonsun.content.interview.internal.entity;

import java.util.Date;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.ABaseEntity;


@Entity
@Table(name="CMS_INTERVIEW_QUESTION")
public class InterviewQuestionEO extends ABaseEntity{



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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="QUESTION_ID")
	private Long questionId;

	@Column(name="INTERVIEW_ID")
	private Long interviewId;

	@Column(name="NAME")
	private String name;

	//会员id
	@Column(name="MEMBER_ID")
	private Long memberId;

	@Column(name="IP_")
	private String ip;

	@Column(name="CONTENT")
	private String content;

	//是否发布
	@Column(name="ISSUED_")
	private Integer issued = Status.No.getStatus();

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "ISSUED_TIME")
	private Date issuedTime;
	
	@Column(name="IS_REPLY")
	private Integer isReply =  Status.No.getStatus();
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "REPLY_TIME")
	private Date replyTime;

	@Column(name="REPLY_NAME")
	private String replyName;

	@Column(name="REPLY_CONTENT")
	private String replyContent;

	//添加图片
	@Column(name = "QUESTION_PIC")
	private String questionPic;

	//回复图片
	@Column(name = "REPLY_PIC")
	private String replyPic;
	
	@Transient
	private String names;

	@Transient
	private String createWebTime;
	
	@Transient
	private String replyWebTime;

	public String getReplyPic() {
		return replyPic;
	}

	public void setReplyPic(String replyPic) {
		this.replyPic = replyPic;
	}

	public String getQuestionPic() {
		return questionPic;
	}

	public void setQuestionPic(String questionPic) {
		this.questionPic = questionPic;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getInterviewId() {
		return interviewId;
	}

	public void setInterviewId(Long interviewId) {
		this.interviewId = interviewId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getIssued() {
		return issued;
	}

	public void setIssued(Integer issued) {
		this.issued = issued;
	}

	public Date getIssuedTime() {
		return issuedTime;
	}

	public void setIssuedTime(Date issuedTime) {
		this.issuedTime = issuedTime;
	}

	public Integer getIsReply() {
		return isReply;
	}

	public void setIsReply(Integer isReply) {
		this.isReply = isReply;
	}

	public Date getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(Date replyTime) {
		this.replyTime = replyTime;
	}

	public String getReplyName() {
		return replyName;
	}

	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getCreateWebTime() {
		return createWebTime;
	}

	public String getReplyWebTime() {
		return replyWebTime;
	}

	public void setCreateWebTime(String createWebTime) {
		this.createWebTime = createWebTime;
	}

	public void setReplyWebTime(String replyWebTime) {
		this.replyWebTime = replyWebTime;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
}
