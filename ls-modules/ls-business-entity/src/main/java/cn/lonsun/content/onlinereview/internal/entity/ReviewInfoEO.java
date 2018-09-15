package cn.lonsun.content.onlinereview.internal.entity;

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
@Table(name="CMS_REVIEW_INFO")
public class ReviewInfoEO extends ABaseEntity{


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
	@Column(name = "INFO_ID")
	private Long infoId;

	@Column(name = "TITLE")
	private String title;

	// 栏目Id
	@Column(name = "COLUMN_ID")
	private Long columnId;
	// 站点Id
	@Column(name = "SITE_ID")
	private Long siteId;

	@Column(name="SORT_NUM")
	private Long sortNum;

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

	//是否发布
	@Column(name="ISSUED_")
	private Integer issued = Status.No.getStatus();

	//(发布时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "ISSUED_TIME")
	private Date issuedTime;

	//(内容)
	@Column(name="CONTENT_")
	private String content;
	
	//(类型ids)
	@Column(name="TYPE_IDS")
	private String typeIds;
	
	//(对象ids)
	@Column(name="OBJECT_IDS")
	private String objectIds;

	// 1 未开始   2 进行中  3 已过期
	@Transient
	private Integer isTimeOut;

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
	
	
	

}
