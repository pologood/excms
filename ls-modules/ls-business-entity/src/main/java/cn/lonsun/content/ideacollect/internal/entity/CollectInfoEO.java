package cn.lonsun.content.ideacollect.internal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="CMS_COLLECT_INFO")
public class CollectInfoEO extends ABaseEntity{

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
	@Column(name="COLLECT_INFO_ID")
	private Long collectInfoId;
	
	@Column(name="CONTENT_ID")
	private Long contentId;
	
	//(开始时间)
	@Column(name = "START_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JSONField(format = "yyyy-MM-dd HH:mm")
	private Date startTime;

	//(结束时间)
	@Column(name = "END_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JSONField(format = "yyyy-MM-dd HH:mm")
	private Date endTime;

	//详细内容
	@Column(name="CONTENT_")
	private String content;

	//小结分析
	@Column(name="DESC_")
	private String desc;
	
	//留言总数
	@Column(name="IDEA_COUNT")
	private Long ideaCount = 0L;

	//转链
	@Column(name="LINKURL")
	private String linkUrl;

	@Transient
	private Integer isTimeOut;

	//征集结果
	@Column(name="RESULT")
	private String result;


	public Long getCollectInfoId() {
		return collectInfoId;
	}


	public void setCollectInfoId(Long collectInfoId) {
		this.collectInfoId = collectInfoId;
	}


	public Long getContentId() {
		return contentId;
	}


	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public Long getIdeaCount() {
		return ideaCount;
	}


	public void setIdeaCount(Long ideaCount) {
		this.ideaCount = ideaCount;
	}


	public Integer getIsTimeOut() {
		return isTimeOut;
	}


	public void setIsTimeOut(Integer isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
