package cn.lonsun.content.interview.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="CMS_INTERVIEW_REPLY")
public class InterviewReplyEO extends ABaseEntity{

	public enum IsReply {
		Yes(1), // 是
		No(0);// 否
		private Integer re;
		private IsReply(Integer re){
			this.re=re;
		}
		public Integer getIsReply(){
			return re;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="REPLY_ID")
	private Long replyId;

	@Column(name="QUESTION_ID")
	private Long questionId;

	@Column(name="INTERVIEW_ID")
	private Long interviewId;

	@Column(name="NAME_")
	private String name;

	@Column(name="CONTENT")
	private String content;

	@Column(name="IS_REPLY")
	private Integer isReply = IsReply.No.getIsReply();

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getIsReply() {
		return isReply;
	}

	public void setIsReply(Integer isReply) {
		this.isReply = isReply;
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



}
