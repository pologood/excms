package cn.lonsun.content.interview.vo;

import java.util.Date;
import java.util.List;

import cn.lonsun.content.internal.entity.ContentPicEO;
import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.core.base.entity.ABaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;


public class InterviewInfoVO extends ABaseEntity{

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

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Long interviewId;

	private Long contentId;
	// 栏目Id
	private Long columnId;
	// 站点Id
	private Long siteId;

	private Long sortNum;

	//访谈标题
	private String title;

	//访谈图片
	private String picUrl;

	//主持人
	private String presenter;

	//访谈 嘉宾
	private String userNames;

	//访谈时间
	private String time;

	//直播地址
	private String liveLink;

	//上次视频mongoDbid
	private String contentPath;

	//上次状态
	private Integer quoteStatus=0;

	//外链地址
	private String outLink;

	//访谈摘要
	private String content;

	//详情
	private String desc;

	//小结
	private String summary;

	private String organDesc;
	private Integer  videoStatus=100;
	//是否发布
	private Integer isOpen = Status.No.getStatus();

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date openTime;

	//是否发布
	private Integer issued = Status.No.getStatus();

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date issuedTime;

	//(开始时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
	private Date startTime;

	//(结束时间)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
	private Date endTime;

	// 1 未开始   2 进行中  3 已过期
	private Integer isTimeOut;

	private Integer type = InterviewInfoEO.Type.wait.getType();

	private String linkUrl;

	private String address;

	private String uri;

	private List<ContentPicEO> pics;

	//主办单位
	private String handleOrgan;

	//承办单位
	private String organizer;

	private Long questionCount = 0L;

	private Long replyCount = 0L;

	private Long memberCount = 0L;

	private Integer isLink = 0;

	private List<String> names;

	public Integer getVideoStatus() {
		return videoStatus;
	}

	public void setVideoStatus(Integer videoStatus) {
		this.videoStatus = videoStatus;
	}

	public Long getInterviewId() {
		return interviewId;
	}

	public void setInterviewId(Long interviewId) {
		this.interviewId = interviewId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
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

	public Integer getIsTimeOut() {
		return isTimeOut;
	}

	public void setIsTimeOut(Integer isTimeOut) {
		this.isTimeOut = isTimeOut;
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

	public Long getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(Long questionCount) {
		this.questionCount = questionCount;
	}

	public Long getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Long replyCount) {
		this.replyCount = replyCount;
	}

	public Long getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Long memberCount) {
		this.memberCount = memberCount;
	}

	public List<ContentPicEO> getPics() {
		return pics;
	}

	public void setPics(List<ContentPicEO> pics) {
		this.pics = pics;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
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


	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	public Integer getQuoteStatus() {
		return quoteStatus;
	}

	public void setQuoteStatus(Integer quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public Integer getIsLink() {
		return isLink;
	}

	public void setIsLink(Integer isLink) {
		this.isLink = isLink;
	}
}
