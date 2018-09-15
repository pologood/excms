package cn.lonsun.content.survey.internal.entity;

import java.util.Date;


import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="CMS_SURVEY_OPTIONS")
public class SurveyOptionsEO extends ABaseEntity{
	
	
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
	@Column(name="OPTIONS_ID")
	private Long optionId;
	
	//(问题id)
	@Column(name="QUESTION_ID")
	private Long questionId;
	
	//(主题id)
	@Column(name="THEME_ID")
	private Long themeId;
	
	//(选项标题)
	@Column(name="TITLE")
	private String title;
	
	//(票数统计)
	@Column(name="VOTES_COUNT")
	private Long votesCount = 0L;
	
	//(图片地址)
	@Column(name="PIC_URL")
	private String picUrl;


	@Transient
	private Integer progress;

	
	public Long getOptionId() {
		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getVotesCount() {
		return votesCount;
	}

	public void setVotesCount(Long votesCount) {
		this.votesCount = votesCount;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}
}
