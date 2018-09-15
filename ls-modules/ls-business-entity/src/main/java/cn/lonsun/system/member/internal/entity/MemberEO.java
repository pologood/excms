package cn.lonsun.system.member.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="CMS_MEMBER")
public class MemberEO extends AMockEntity implements Serializable {

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

	public enum MemberType {
		WEB(0), // 网站会员
		ORGAN(1);// 部门会员
		private Integer mt;
		private MemberType(Integer mt){
			this.mt=mt;
		}
		public Integer getMemberType(){
			return mt;
		}
	}

	public enum Sex {
		Man(1), // 男
		Woman(0);// 女
		private Integer sexNum;
		private Sex(Integer sexNum){
			this.sexNum=sexNum;
		}
		public Integer getSex(){
			return sexNum;
		}
	}

	public enum BDTYPE {
		ANDROID, // 可用
		IOS;// 禁用
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SITE_ID")
	private Long siteId;

	//登录名称
	@Column(name = "UID_")
	private String uid;

	//登录名称
	@Column(name = "IMG_")
	private String img;

	//昵称
	@Column(name = "NAME")
	private String name;

	//密码
	@Column(name = "PASSWORD")
	private String password;

	//明文密码
	@Column(name = "PLAIN_PW")
	private String plainpw;

	//性别
	@Column(name = "SEX_")
	private Integer sex = Sex.Man.getSex();

	//邮箱
	@Column(name = "EMAIL")
	private String email;

	//手机号
	@Column(name = "PHONE")
	private String phone;

	//身份证
	@Column(name = " ID_CARD")
	private String idCard;

	//手机号
	@Column(name = "ADDRESS")
	private String address;

	//问题
	@Column(name = "QUESTION")
	private String question;

	//答案
	@Column(name = "ANSWER")
	private String answer;

	//最后登录ip
	@Column(name = "IP_")
	private String ip;

	// 登录次数
	@Column(name = "LOGIN_TIMES")
	private Integer loginTimes = 0;

	@Column(name = "LAST_LOGIN_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date lastLoginDate;

	//积分
	@Column(name = "MEMBER_POINTS")
	private Long memberPoints = 0L;

	//角色Id
	@Column(name = "MEMBER_ROLE_ID")
	private Long memberRoleId;

	//会员类型
	@Column(name = "MEMBER_TYPE")
	private Integer memberType = MemberType.WEB.getMemberType();

	//单位Id
	@Column(name = "UNIT_ID")
	private Long unitId;

	//单位名称
	@Column(name = "UNIT_NAME")
	private String unitName;

	//状态
	@Column(name = "STATUS")
	private Integer status = Status.Enabled.getStatus();

	//是否短信验证 0 否 1 是
	@Column(name = "SMS_CHECK")
	private Integer smsCheck=0;

	//第三方登录类型
	@Column(name = "OPEN_TYPE")
	private String openType;

	//第三方登录key
	@Column(name = "OPEN_ID")
	private String openId;

	@Column(name = "BDUSER_ID")
	private String bduserid;

	@Column(name = "BD_TYPE")
	private String bdtype;


	//所属平台  （导入数据所属平台）HN 淮南
	@Column(name = "PLAT_CODE")
	private String platCode;

	//淮南密码自定义加密方式串
	@Column(name = "RAND_KEY")
	private String randKey;

	/**
	 * 发帖数
	 */
	@Column(name = "POST_COUNT")
	private Long postCount = 0L;

	/**
	 * 回帖数
	 */
	@Column(name = "REPLY_COUNT")
	private Long replyCount = 0L;

	/**
	 * 老的Id
	 */
	@Column(name = "old_Id")
	private String oldId;

	/**
	 * QQ登录对应每个qq的openid
	 */
	@Column(name = "QQ_OPEN_ID")
	private String qqOpenId;

	/**
	 * QQ登录对应每个qq的昵称
	 */
	@Column(name = "QQ_NAME")
	private String qqName;

	/**
	 * QQ登录对应每个qq的头像
	 */
	@Column(name = "QQ_IMG")
	private String qqImg;

	/**
	 * 微信登录对应每个微信的openid
	 */
	@Column(name = "WECHAT_OPEN_ID")
	private String weChatOpenId;

	/**
	 * 微信登录对应每个微信的昵称
	 */
	@Column(name = "WECHAT_NAME")
	private String weChatName;

	/**
	 * 微信登录对应每个微信的头像
	 */
	@Column(name = "WECHAT_IMG")
	private String weChatImg;

	/**
	 * 微博登录对应每个微博的openid
	 */
	@Column(name = "WEIBO_OPEN_ID")
	private String weiBoOpenId;

	/**
	 * 微博登录对应每个微博的昵称
	 */
	@Column(name = "WEIBO_NAME")
	private String weiBoName;

	/**
	 * 微博登录对应每个微博的头像
	 */
	@Column(name = "WEIBO_IMG")
	private String weiBoImg;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getPlainpw() {
		return plainpw;
	}

	public void setPlainpw(String plainpw) {
		this.plainpw = plainpw;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Long getSiteId() {
		return siteId;
	}

	public String getAddress() {
		return address;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getMemberPoints() {
		return memberPoints;
	}

	public void setMemberPoints(Long memberPoints) {
		this.memberPoints = memberPoints;
	}

	public Integer getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(Integer loginTimes) {
		this.loginTimes = loginTimes;
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

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public String getBduserid() {
		return bduserid;
	}

	public void setBduserid(String bduserid) {
		this.bduserid = bduserid;
	}

	public String getBdtype() {
		return bdtype;
	}

	public void setBdtype(String bdtype) {
		this.bdtype = bdtype;
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

	public Integer getSmsCheck() {
		return smsCheck;
	}

	public void setSmsCheck(Integer smsCheck) {
		this.smsCheck = smsCheck;
	}

	public String getRandKey() {
		return randKey;
	}

	public void setRandKey(String randKey) {
		this.randKey = randKey;
	}

	public String getPlatCode() {
		return platCode;
	}

	public void setPlatCode(String platCode) {
		this.platCode = platCode;
	}

	public Long getMemberRoleId() {
		return memberRoleId;
	}

	public void setMemberRoleId(Long memberRoleId) {
		this.memberRoleId = memberRoleId;
	}

	public Integer getMemberType() {
		return memberType;
	}

	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}

	public Long getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Long replyCount) {
		this.replyCount = replyCount;
	}

	public Long getPostCount() {
		return postCount;
	}

	public void setPostCount(Long postCount) {
		this.postCount = postCount;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}

	public String getQqOpenId() {
		return qqOpenId;
	}

	public void setQqOpenId(String qqOpenId) {
		this.qqOpenId = qqOpenId;
	}

	public String getWeChatOpenId() {
		return weChatOpenId;
	}

	public void setWeChatOpenId(String weChatOpenId) {
		this.weChatOpenId = weChatOpenId;
	}

	public String getWeiBoOpenId() {
		return weiBoOpenId;
	}

	public void setWeiBoOpenId(String weiBoOpenId) {
		this.weiBoOpenId = weiBoOpenId;
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
