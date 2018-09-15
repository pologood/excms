package cn.lonsun.system.member.vo;

import cn.lonsun.system.member.internal.entity.MemberEO.Sex;


public class MemberSessionVO implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum Status {
		Enabled(1), // 可用
		Unable(0);// 禁用
		private Integer statusNum;
		private Status(Integer statusNum){
			this.statusNum=statusNum;
		}
		public Integer getStatus(){
			return statusNum;
		}
	}

	private Long id;
	
	private Long siteId;

	//登录名称
	private String uid;

	//昵称
	private String name;

	private String idCard;

	//性别
	private Integer sex = Sex.Man.getSex();

	//邮箱
	private String email;

	//手机号
	private String phone;

	//手机号
	private String address;

	// 登录次数
	private Integer loginTimes = 0;

	//积分
	private Long memberPoints = 0L;
	
	private Integer status = Status.Enabled.getStatus();
	
	private String question;

	private String answer;

	private String img;

	private Integer smsCheck;

	/**
	 * QQ登录对应每个qq的昵称
	 */
	private String qqName;

	/**
	 * QQ登录对应每个qq的头像
	 */
	private String qqImg;


	/**
	 * 微信登录对应每个微信的昵称
	 */
	private String weChatName;

	/**
	 * 微信登录对应每个微信的头像
	 */
	private String weChatImg;

	/**
	 * 微博登录对应每个微博的昵称
	 */
	private String weiBoName;

	/**
	 * 微博登录对应每个微博的头像
	 */
	private String weiBoImg;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(Integer loginTimes) {
		this.loginTimes = loginTimes;
	}

	public Long getMemberPoints() {
		return memberPoints;
	}

	public void setMemberPoints(Long memberPoints) {
		this.memberPoints = memberPoints;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}


	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Integer getSmsCheck() {
		return smsCheck;
	}

	public void setSmsCheck(Integer smsCheck) {
		this.smsCheck = smsCheck;
	}

	public String getQqName() {
		return qqName;
	}

	public void setQqName(String qqName) {
		this.qqName = qqName;
	}

	public String getQqImg() {
		return qqImg;
	}

	public void setQqImg(String qqImg) {
		this.qqImg = qqImg;
	}

	public String getWeChatName() {
		return weChatName;
	}

	public void setWeChatName(String weChatName) {
		this.weChatName = weChatName;
	}

	public String getWeChatImg() {
		return weChatImg;
	}

	public void setWeChatImg(String weChatImg) {
		this.weChatImg = weChatImg;
	}

	public String getWeiBoName() {
		return weiBoName;
	}

	public void setWeiBoName(String weiBoName) {
		this.weiBoName = weiBoName;
	}

	public String getWeiBoImg() {
		return weiBoImg;
	}

	public void setWeiBoImg(String weiBoImg) {
		this.weiBoImg = weiBoImg;
	}
}
