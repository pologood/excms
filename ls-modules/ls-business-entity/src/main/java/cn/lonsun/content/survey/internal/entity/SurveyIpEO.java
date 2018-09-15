package cn.lonsun.content.survey.internal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.ABaseEntity;


@Entity
@Table(name="CMS_SURVEY_QUESTION_IP")
public class SurveyIpEO extends ABaseEntity{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="IP_ID")
	private Long ipId;

	//会员id
	@Column(name="NAME")
	private  String name;

	//会员id
	@Column(name="MEMBER_ID")
	private Long memberId;

	//(问题id)
	@Column(name="QUESTION_ID")
	private Long questionId;
	
	//(主题id)
	@Column(name="THEME_ID")
	private Long themeId;
	
	//ip地址
	@Column(name="IP_")
	private String ipAddr;
	
	//投票时间
	@Column(name="VOTE_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date voteTime;
	
	//投票次数
	@Column(name="VOTE_COUNT")
	private Long voteCount;
	
	public Long getIpId() {
		return ipId;
	}

	public void setIpId(Long ipId) {
		this.ipId = ipId;
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

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}


	public Date getVoteTime() {
		return voteTime;
	}

	public void setVoteTime(Date voteTime) {
		this.voteTime = voteTime;
	}

	public Long getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(Long voteCount) {
		this.voteCount = voteCount;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
