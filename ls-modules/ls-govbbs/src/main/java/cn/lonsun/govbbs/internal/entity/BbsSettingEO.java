package cn.lonsun.govbbs.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import javax.persistence.*;

@Entity
@Table(name="CMS_BBS_SETTING")
public class BbsSettingEO extends ABaseEntity{


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

	public enum RegisterType {
		NoCheck(1), // 注册成功后，即可使用论坛功能
		Check(2);// 注册成功后，需手动审核通过才可使用论坛功能
		private Integer ct;
		private RegisterType(Integer ct){
			this.ct=ct;
		}
		public Integer getRegisterType(){
			return ct;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="SETTING_ID")
	private Long settingId;	

	//(站点id)	
	@Column(name="CASE_ID")
	private Long caseId;

	//(站点id)	
	@Column(name="SITE_ID")
	private Long siteId;

	//(论坛名称)	
	@Column(name="NAME_")
	private String name;

	//(论坛URL)
	@Column(name="SOFT_URL")
	private String softUrl;

	//(注册默认角色)
	@Column(name="default_Group_Id")
	private Long defaultGroupId;

	//(楼层名称)
	@Column(name="thread_Floor")
	private String threadFloor;

	//(信息类型)
	@Column(name="thread_Type")
	private String threadType;

	//(论坛声明)
	@Column(name="statement")
	private String statement;

	//(回复被删除)
	@Column(name="del_Reply")
	private Integer delReply;

	//(主题被屏蔽)
	@Column(name="screen_Title")
	private Integer screenTitle;

	//(回复被屏蔽)
	@Column(name="screen_Reply")
	private Integer screenReply;

	//(热门贴)
	@Column(name="hot_title")
	private Integer hotTitle;

	//(新帖)
	@Column(name="new_Title")
	private Integer newTitle;

	//(自动办结天数)
	@Column(name="auto_Close")
	private Integer autoClose;

	//(回复后警告贴)
	@Column(name="hide_Card_Replied")
	private Integer hideCardReplied;

	//(快速发主题帖)
	@Column(name="fast_Thread_On")
	private Integer fastThreadOn;

	//(快速发回复帖)
	@Column(name="fast_Post_On")
	private Integer fastPostOn;

	//(会员注册)
	@Column(name="register_On")
	private Integer registerOn;

	//(注册验证)
	@Column(name="verify_Method")
	private Integer verifyMethod;

	//(发帖限制)
	@Column(name="visitor_Can_Thread")
	private Integer visitorCanThread;

	//(回复限制)
	@Column(name="visitor_Can_post")
	private Integer visitorCanPost;

	//游客查看/下载
	@Column(name="visitor_Can_Download")
	private Integer visitorCanDownload;

	//游客命名前缀
	@Column(name="visitor_Prefix")
	private String visitorPrefix;

	//(关键字)
	@Column(name="KEY_")
	private String key;	

	//(是否允许发布帖子)
	@Column(name="IS_ISSUE")
	private Integer isIssue = Status.Yes.getStatus();	

	//(是否允许你回复帖子)
	@Column(name="IS_REPLY")
	private Integer isReply = Status.Yes.getStatus();	

	//(注册会员积分增加)	
	@Column(name="REGISTER_NUM")
	private Integer registerNum = 0; 

	//(登录积分增加)
	@Column(name="LOGIN_NUM")
	private Integer loginNum = 0;	

	//(发帖积分增加)
	@Column(name="POSTED_NUM")
	private Integer postedNum = 0;		

	//(回帖积分增加)
	@Column(name="REPLY_NUM")
	private Integer replyNum = 0;		

	//(帖子审核通过后积分增加)
	@Column(name="CHECK_NUM")
	private Integer checkNum = 0;		

	//(删除积分减少)
	@Column(name="DEL_NUM")
	private Integer delNum = 0;		

	//(置顶积分增加)
	@Column(name="TOP_NUM")
	private Integer topNum = 0;		

	//(推荐积分增加)
	@Column(name="ESSENCE_NUM")
	private Integer essenceNum = 0;	

	//(回复年限定)
	@Column(name="YEAR_")
	private Integer year;		

	//(禁止发帖ip)	
	@Column(name="IPS")
	private String ips;

	//(限制回帖说明)
	@Column(name="CONTENT")
	private String content;		

	//(日期池)
	@Column(name="TIMES_")
	private String times;		

	//(激活方式)	
	@Column(name="REGISTER_TYPE")
	private Integer registerType = RegisterType.Check.getRegisterType();	

	//(超时回复天数)
	@Column(name="REPLY_DAY")
	private Integer replyDay;		

	//(黄牌天数)
	@Column(name="YELLOW_DAY")
	private Integer yellowDay;		

	//(红牌天数)
	@Column(name="RED_DAY")
	private Integer redDay;


	public Long getSettingId() {
		return settingId;
	}

	public void setSettingId(Long settingId) {
		this.settingId = settingId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getIsIssue() {
		return isIssue;
	}

	public void setIsIssue(Integer isIssue) {
		this.isIssue = isIssue;
	}

	public Integer getIsReply() {
		return isReply;
	}

	public void setIsReply(Integer isReply) {
		this.isReply = isReply;
	}

	public Integer getRegisterNum() {
		return registerNum;
	}

	public void setRegisterNum(Integer registerNum) {
		this.registerNum = registerNum;
	}

	public Integer getLoginNum() {
		return loginNum;
	}

	public void setLoginNum(Integer loginNum) {
		this.loginNum = loginNum;
	}

	public Integer getPostedNum() {
		return postedNum;
	}

	public void setPostedNum(Integer postedNum) {
		this.postedNum = postedNum;
	}

	public Integer getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(Integer replyNum) {
		this.replyNum = replyNum;
	}

	public Integer getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(Integer checkNum) {
		this.checkNum = checkNum;
	}

	public Integer getDelNum() {
		return delNum;
	}

	public void setDelNum(Integer delNum) {
		this.delNum = delNum;
	}

	public Integer getTopNum() {
		return topNum;
	}

	public void setTopNum(Integer topNum) {
		this.topNum = topNum;
	}

	public Integer getEssenceNum() {
		return essenceNum;
	}

	public void setEssenceNum(Integer essenceNum) {
		this.essenceNum = essenceNum;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public Integer getRegisterType() {
		return registerType;
	}

	public void setRegisterType(Integer registerType) {
		this.registerType = registerType;
	}

	public Integer getReplyDay() {
		return replyDay;
	}

	public void setReplyDay(Integer replyDay) {
		this.replyDay = replyDay;
	}

	public Integer getYellowDay() {
		return yellowDay;
	}

	public void setYellowDay(Integer yellowDay) {
		this.yellowDay = yellowDay;
	}

	public Integer getRedDay() {
		return redDay;
	}

	public void setRedDay(Integer redDay) {
		this.redDay = redDay;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getSoftUrl() {
		return softUrl;
	}

	public void setSoftUrl(String softUrl) {
		this.softUrl = softUrl;
	}

	public Long getDefaultGroupId() {
		return defaultGroupId;
	}

	public void setDefaultGroupId(Long defaultGroupId) {
		this.defaultGroupId = defaultGroupId;
	}

	public String getThreadFloor() {
		return threadFloor;
	}

	public void setThreadFloor(String threadFloor) {
		this.threadFloor = threadFloor;
	}

	public String getThreadType() {
		return threadType;
	}

	public void setThreadType(String threadType) {
		this.threadType = threadType;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public Integer getDelReply() {
		return delReply;
	}

	public void setDelReply(Integer delReply) {
		this.delReply = delReply;
	}

	public Integer getScreenTitle() {
		return screenTitle;
	}

	public void setScreenTitle(Integer screenTitle) {
		this.screenTitle = screenTitle;
	}

	public Integer getScreenReply() {
		return screenReply;
	}

	public void setScreenReply(Integer screenReply) {
		this.screenReply = screenReply;
	}

	public Integer getHotTitle() {
		return hotTitle;
	}

	public void setHotTitle(Integer hotTitle) {
		this.hotTitle = hotTitle;
	}

	public Integer getNewTitle() {
		return newTitle;
	}

	public void setNewTitle(Integer newTitle) {
		this.newTitle = newTitle;
	}

	public Integer getAutoClose() {
		return autoClose;
	}

	public void setAutoClose(Integer autoClose) {
		this.autoClose = autoClose;
	}

	public Integer getHideCardReplied() {
		return hideCardReplied;
	}

	public void setHideCardReplied(Integer hideCardReplied) {
		this.hideCardReplied = hideCardReplied;
	}

	public Integer getFastThreadOn() {
		return fastThreadOn;
	}

	public void setFastThreadOn(Integer fastThreadOn) {
		this.fastThreadOn = fastThreadOn;
	}

	public Integer getFastPostOn() {
		return fastPostOn;
	}

	public void setFastPostOn(Integer fastPostOn) {
		this.fastPostOn = fastPostOn;
	}

	public Integer getRegisterOn() {
		return registerOn;
	}

	public void setRegisterOn(Integer registerOn) {
		this.registerOn = registerOn;
	}

	public Integer getVerifyMethod() {
		return verifyMethod;
	}

	public void setVerifyMethod(Integer verifyMethod) {
		this.verifyMethod = verifyMethod;
	}

	public Integer getVisitorCanThread() {
		return visitorCanThread;
	}

	public void setVisitorCanThread(Integer visitorCanThread) {
		this.visitorCanThread = visitorCanThread;
	}

	public Integer getVisitorCanDownload() {
		return visitorCanDownload;
	}

	public void setVisitorCanDownload(Integer visitorCanDownload) {
		this.visitorCanDownload = visitorCanDownload;
	}

	public String getVisitorPrefix() {
		return visitorPrefix;
	}

	public void setVisitorPrefix(String visitorPrefix) {
		this.visitorPrefix = visitorPrefix;
	}

	public Integer getVisitorCanPost() {
		return visitorCanPost;
	}

	public void setVisitorCanPost(Integer visitorCanPost) {
		this.visitorCanPost = visitorCanPost;
	}
}
