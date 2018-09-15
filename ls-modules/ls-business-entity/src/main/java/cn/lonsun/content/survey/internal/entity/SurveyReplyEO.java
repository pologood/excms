package cn.lonsun.content.survey.internal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;



import cn.lonsun.core.base.entity.ABaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Table(name="CMS_SURVEY_REPLY")
public class SurveyReplyEO extends ABaseEntity{
	
	
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
	@Column(name="REPLY_ID")
	private Long replyId;
	
	//(问题id)
	@Column(name="QUESTION_ID")
	private Long questionId;
	
	//(主题id)
	@Column(name="THEME_ID")
	private Long themeId;
	

	@Column(name="IP_")
	private String ip;
	
	@Column(name="IS_ISSUED")
	private Integer isIssued = Status.No.getStatus();

	//(发布时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "ISSUED_TIME")
	private Date issuedTime;
	
	@Column(name="CONTENT")
	private String content;

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getThemeId() {
		return themeId;
	}

	public void setThemeId(Long themeId) {
		this.themeId = themeId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getIsIssued() {
		return isIssued;
	}

	public void setIsIssued(Integer isIssued) {
		this.isIssued = isIssued;
	}

	public Date getIssuedTime() {
		return issuedTime;
	}

	public void setIssuedTime(Date issuedTime) {
		this.issuedTime = issuedTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}
