package cn.lonsun.content.survey.internal.entity;

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
@Table(name="CMS_SURVEY_THEME")
public class SurveyThemeEO extends ABaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public enum Status {
		Yes(1), // 
		No(0);// 
		private Integer status;
		private Status(Integer status){
			this.status=status;
		}
		public Integer getStatus(){
			return status;
		}
	}

	public enum Option {
		IS_TEXT(1), // 文字列表
		IS_PCI(0);// 图片列表
		private Integer op;
		private Option(Integer op){
			this.op=op;
		}
		public Integer getOption(){
			return op;
		}
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="THEME_ID")
	private Long themeId;
	
	@Column(name="CONTENT_ID")
	private Long contentId;

	//(选项)
	@Column(name="OPTIONS_")
	private Integer options = Option.IS_TEXT.getOption();

	//(是否ip限制) 1 总共  0每天
	@Column(name="IP_LIMIT")
	private Integer ipLimit = Status.Yes.getStatus();

	//(ip 每天限制多少票)
	@Column(name="IP_DAY_COUNT")
	private Integer ipDayCount = 1;

	//(投票是否可见)
	@Column(name="IS_VISIBLE")
	private Integer isVisible = Status.Yes.getStatus();

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

	//(内容)
	@Column(name="CONTENT_")
	private String content;


	//(是否链接)
	@Column(name="IS_LINK")
	private Integer isLink = Status.No.getStatus();

	//(链接地址)
	@Column(name="LINK_URL")
	private String linkUrl;
	
	//(类型ids)
	@Column(name="TYPE_IDS")
	private String typeIds;
	
	//(对象ids)
	@Column(name="OBJECT_IDS")
	private String objectIds;


	// 1 未开始   2 进行中  3 已过期
	@Transient
	private Integer isTimeOut;
	
	@Transient
	private Integer hasClild;
	
	@Transient
	private String typeNames;
	
	@Transient
	private String objectNames;

	public Long getThemeId() {
		return themeId;
	}

	public void setThemeId(Long themeId) {
		this.themeId = themeId;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(Integer options) {
		this.options = options;
	}

	public Integer getIpLimit() {
		return ipLimit;
	}

	public void setIpLimit(Integer ipLimit) {
		this.ipLimit = ipLimit;
	}

	public Integer getIpDayCount() {
		return ipDayCount;
	}

	public void setIpDayCount(Integer ipDayCount) {
		this.ipDayCount = ipDayCount;
	}

	public Integer getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Integer isVisible) {
		this.isVisible = isVisible;
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

	public Integer getIsLink() {
		return isLink;
	}

	public void setIsLink(Integer isLink) {
		this.isLink = isLink;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getTypeIds() {
		return typeIds;
	}

	public void setTypeIds(String typeIds) {
		this.typeIds = typeIds;
	}

	public String getObjectIds() {
		return objectIds;
	}

	public void setObjectIds(String objectIds) {
		this.objectIds = objectIds;
	}

	public Integer getIsTimeOut() {
		return isTimeOut;
	}

	public void setIsTimeOut(Integer isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public Integer getHasClild() {
		return hasClild;
	}

	public void setHasClild(Integer hasClild) {
		this.hasClild = hasClild;
	}

	public String getTypeNames() {
		return typeNames;
	}

	public void setTypeNames(String typeNames) {
		this.typeNames = typeNames;
	}

	public String getObjectNames() {
		return objectNames;
	}

	public void setObjectNames(String objectNames) {
		this.objectNames = objectNames;
	}

}
