package cn.lonsun.content.ideacollect.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.core.base.entity.ABaseEntity;
import cn.lonsun.core.util.Pagination;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CollectInfoWebVO extends ABaseEntity{
	
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
	
	private Long collectInfoId;
	
	private Long contentId;
	
	// 栏目Id
	private Long columnId;
	// 站点Id
	private Long siteId;

	//标题
	private String title;

	//图片地址
	private String picUrl;

	//(开始时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
	private Date startTime;

	//(结束时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
	private Date endTime;

	//详细内容
	private String content;

	//小结分析
	private String desc;
	
	//留言总数
	private Long ideaCount = 0L;


	//是否发布
	private Integer isIssued = Status.No.getStatus();

	//(发布时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date issuedTime;
	
	private Integer isTimeOut;

	private Long sortNum;
	
	private String linkUrl;
	
	private String timeStr;
	
	private Pagination ideaPage;

	//征集结果
	private String result;

	public Long getCollectInfoId() {
		return collectInfoId;
	}

	public Long getContentId() {
		return contentId;
	}

	public Long getColumnId() {
		return columnId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public String getTitle() {
		return title;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getContent() {
		return content;
	}

	public String getDesc() {
		return desc;
	}

	public Long getIdeaCount() {
		return ideaCount;
	}

	public Integer getIsIssued() {
		return isIssued;
	}

	public Date getIssuedTime() {
		return issuedTime;
	}

	public Integer getIsTimeOut() {
		return isTimeOut;
	}

	public Long getSortNum() {
		return sortNum;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public Pagination getIdeaPage() {
		return ideaPage;
	}

	public void setCollectInfoId(Long collectInfoId) {
		this.collectInfoId = collectInfoId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setIdeaCount(Long ideaCount) {
		this.ideaCount = ideaCount;
	}

	public void setIsIssued(Integer isIssued) {
		this.isIssued = isIssued;
	}

	public void setIssuedTime(Date issuedTime) {
		this.issuedTime = issuedTime;
	}

	public void setIsTimeOut(Integer isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public void setIdeaPage(Pagination ideaPage) {
		this.ideaPage = ideaPage;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
