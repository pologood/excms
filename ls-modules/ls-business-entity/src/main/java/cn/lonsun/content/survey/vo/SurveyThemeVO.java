package cn.lonsun.content.survey.vo;

import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.core.base.entity.ABaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


public class SurveyThemeVO extends ABaseEntity{

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

	private Long themeId;

	private Long contentId;

	private Long columnId;

	private Long siteId;

	private String title;


	private Long sortNum;

	//(选项)
	private Integer options = Option.IS_TEXT.getOption();

	//(是否ip限制) 1 总共  2 每天
	private Integer ipLimit = Status.Yes.getStatus();

	//(ip 每天限制多少票)
	private Integer ipDayCount = 1;

	//(投票是否可见)
	private Integer isVisible = Status.Yes.getStatus();

	//(开始时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JSONField(format = "yyyy-MM-dd HH:mm")
	private Date startTime;

	//(结束时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JSONField(format = "yyyy-MM-dd HH:mm")
	private Date endTime;

	//(内容)
	private String content;

	//是否发布
	private Integer isPublish = Status.No.getStatus();

	//(发布时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date issuedTime;

	//(是否链接)
	private Integer isLink = Status.No.getStatus();

	//(链接地址)
	private String linkUrl;

	//(类型ids)
	private String typeIds;

	//(对象ids)
	private String objectIds;

	// 1 未开始   2 进行中  3 已过期
	private Integer isTimeOut;

	private Integer hasClild;

	private String typeNames;

	private String objectNames;

	private List<SurveyQuestionEO> questions;

	private String viewUrl;

	private String timeStr;

	private Long counts = 0L;

	private Integer videoStatus;

	public Integer getVideoStatus() {
		return videoStatus;
	}

	public void setVideoStatus(Integer videoStatus) {
		this.videoStatus = videoStatus;
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


	public Long getSortNum() {
		return sortNum;
	}


	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
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


	public Integer getIsPublish() {
		return isPublish;
	}


	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}


	public Date getIssuedTime() {
		return issuedTime;
	}


	public void setIssuedTime(Date issuedTime) {
		this.issuedTime = issuedTime;
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


	public Integer getIsTimeOut() {
		return isTimeOut;
	}


	public void setIsTimeOut(Integer isTimeOut) {
		this.isTimeOut = isTimeOut;
	}


	public Long getColumnId() {
		return columnId;
	}


	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}


	public Long getSiteId() {
		return siteId;
	}


	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}


	public Integer getHasClild() {
		return hasClild;
	}


	public void setHasClild(Integer hasClild) {
		this.hasClild = hasClild;
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


	public Long getContentId() {
		return contentId;
	}


	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}


	public List<SurveyQuestionEO> getQuestions() {
		return questions;
	}


	public void setQuestions(List<SurveyQuestionEO> questions) {
		this.questions = questions;
	}


	public String getViewUrl() {
		return viewUrl;
	}


	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}


	public String getTimeStr() {
		return timeStr;
	}


	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}


	public Long getCounts() {
		return counts;
	}

	public void setCounts(Long counts) {
		this.counts = counts;
	}
}
