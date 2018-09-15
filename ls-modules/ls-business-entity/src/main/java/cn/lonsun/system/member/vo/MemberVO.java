package cn.lonsun.system.member.vo;


public class MemberVO implements java.io.Serializable{

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

	//密码
	private String password;

	//性别
	private Integer sex;;

	//邮箱
	private String email;

	//手机号
	private String phone;

	//手机号
	private String address;

	//问题
	private String question;

	//答案
	private String answer;

	//状态
	private Integer status = Status.Unable.getStatus();

	private String openType;

	private String idCard;

	//第三方登录key
	private String openId;
	
	private String bduserid;

	private String img;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getBduserid() {
		return bduserid;
	}

	public void setBduserid(String bduserid) {
		this.bduserid = bduserid;
	}


	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
}
