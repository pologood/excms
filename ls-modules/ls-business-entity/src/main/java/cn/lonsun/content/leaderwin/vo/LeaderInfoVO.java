package cn.lonsun.content.leaderwin.vo;

import java.util.Date;

import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;


public class LeaderInfoVO implements java.io.Serializable{

	
	public enum Issued {
		Yes(1), // 是
		No(0);// 否
		private Integer issued;
		private Issued(Integer issued){
			this.issued=issued;
		}
		public Integer getIssued(){
			return issued;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long leaderInfoId;
	
	private Long contentId;
	
	// 栏目Id
	private Long columnId;
	// 站点Id
	private Long siteId;
	
	//分类id
	private Long leaderTypeId;
	
	//职务
	private String positions;
	
	//领导姓名
	private String name;
	
	//领导图片
	private String picUrl;
	
	//工作分工
	private String work;
	
	//工作简历
	private String jobResume;
	
	//是否发布
	private Integer issued = Issued.No.getIssued();
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date issuedTime;
	
	private Long sortNum;
	
	private String typeName;
	
	private String linkUrl;
	
	private Integer isClick = 0;

	public Long getLeaderInfoId() {
		return leaderInfoId;
	}

	public void setLeaderInfoId(Long leaderInfoId) {
		this.leaderInfoId = leaderInfoId;
	}

	public Long getLeaderTypeId() {
		return leaderTypeId;
	}

	public void setLeaderTypeId(Long leaderTypeId) {
		this.leaderTypeId = leaderTypeId;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getJobResume() {
		return jobResume;
	}

	public void setJobResume(String jobResume) {
		this.jobResume = jobResume;
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

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
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

	public Long getSortNum() {
		return sortNum;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public Integer getIsClick() {
		return isClick;
	}

	public void setIsClick(Integer isClick) {
		this.isClick = isClick;
	}

	
	
}
