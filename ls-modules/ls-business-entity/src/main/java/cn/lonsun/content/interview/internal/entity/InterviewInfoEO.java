package cn.lonsun.content.interview.internal.entity;

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
@Table(name="CMS_INTERVIEW_INFO")
public class InterviewInfoEO extends ABaseEntity{

	public enum Status {
		Yes(1), // 是
		No(0);// 否
		private Integer sta;
		private Status(Integer sta){
			this.sta=sta;
		}
		public Integer getStatus(){
			return sta;
		}
	}
	
	public enum Type {
		view(1),// 访谈中
		history(2),// 访谈历史
		wait(3); // 访谈预告
		private Integer t;
		private Type(Integer t){
			this.t=t;
		}
		public Integer getType(){
			return t;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="INTERVIEW_ID")
	private Long interviewId;

	@Column(name="CONTENT_ID")
	private Long contentId;

	//主持人
	@Column(name="PRESENTER")
	private String presenter;

	//访谈 嘉宾
	@Column(name="USER_NAMES")
	private String userNames;

	//访谈时间
	@Column(name="TIME_")
	private String time;

	//直播地址
	@Column(name="LIVE_LINK")
	private String liveLink;

	//承办单位
	@Column(name="HANDLE_ORGAN")
	private String handleOrgan;

	//主办单位
	@Column(name="ORGANIZER_")
	private String organizer;

	//外链地址
	@Column(name="OUT_LINK")
	private String outLink;

	//访谈摘要
	@Column(name="CONTENT")
	private String content;

	//详情
	@Column(name="DESC_")
	private String desc;

	//单位介绍
	@Column(name="ORGAN_DESC")
	private String organDesc;


	//小结
	@Column(name = "SUMMARY_")
	private String summary;

	//是否发布
	@Column(name="IS_OPEN")
	private Integer isOpen = Status.No.getStatus();
	
	
	//是否发布
	@Column(name="TYPE")
    private Integer type = Type.wait.getType();
	
	@Column(name="ADDRESS_")
    private String address;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "OPEN_TIME")
	private Date openTime;

	//(开始时间)
	@Column(name = "START_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
	private Date startTime;

	//(结束时间)
	@Column(name = "END_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
	private Date endTime;

	// 1 未开始   2 进行中  3 已过期
	@Transient
	private Integer isTimeOut;

	public Long getInterviewId() {
		return interviewId;
	}

	public void setInterviewId(Long interviewId) {
		this.interviewId = interviewId;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public String getPresenter() {
		return presenter;
	}

	public void setPresenter(String presenter) {
		this.presenter = presenter;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLiveLink() {
		return liveLink;
	}

	public void setLiveLink(String liveLink) {
		this.liveLink = liveLink;
	}

	public String getOutLink() {
		return outLink;
	}

	public void setOutLink(String outLink) {
		this.outLink = outLink;
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

	public Integer getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
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

	public Integer getIsTimeOut() {
		return isTimeOut;
	}

	public void setIsTimeOut(Integer isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHandleOrgan() {
		return handleOrgan;
	}

	public void setHandleOrgan(String handleOrgan) {
		this.handleOrgan = handleOrgan;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getOrganDesc() {
		return organDesc;
	}

	public void setOrganDesc(String organDesc) {
		this.organDesc = organDesc;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}
}
