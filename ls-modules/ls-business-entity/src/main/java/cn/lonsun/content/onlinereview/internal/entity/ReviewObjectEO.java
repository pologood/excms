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

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="CMS_REVIEW_OBJECT")
public class ReviewObjectEO extends ABaseEntity{

	
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
	@Column(name="OBJECT_ID")
	private Long objectId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="PIC_URL")
	private String picUrl;

	@Column(name="CONTENT")
	private String content;
	
	// 栏目Id
	@Column(name = "COLUMN_ID")
	private Long columnId;
	// 站点Id
	@Column(name = "SITE_ID")
	private Long siteId;

	@Column(name="IS_SHOW")
	private Integer isShow = Status.Yes.getStatus();
	
	@Column(name = "SHOW_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
	private Date showTime;
	
	@Transient
	private String organNames;
	
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public Integer getIsShow() {
		return isShow;
	}

	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}

	public Date getShowTime() {
		return showTime;
	}

	public void setShowTime(Date showTime) {
		this.showTime = showTime;
	}

	public String getOrganNames() {
		return organNames;
	}

	public void setOrganNames(String organNames) {
		this.organNames = organNames;
	}
	
	
	
}
