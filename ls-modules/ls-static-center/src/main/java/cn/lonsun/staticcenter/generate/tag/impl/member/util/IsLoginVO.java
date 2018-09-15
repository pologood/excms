package cn.lonsun.staticcenter.generate.tag.impl.member.util;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class IsLoginVO {
	
	public enum IsLogin {
		Yes(1), // 是
		No(0);// 否
		private Integer isl;
		private IsLogin(Integer isl){
			this.isl=isl;
		}
		public Integer getIsLogin(){
			return isl;
		}
	}
	private Integer isLogin = IsLogin.No.getIsLogin();
	
	
	private Long userId;

	//(注册账号)
	private String  uid;		

	//(名称)
	private String  userName;

	private String phone;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date loginDate;

	//是否短信验证
	private Integer smsCheck=0;

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public Integer getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(Integer isLogin) {
		this.isLogin = isLogin;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getSmsCheck() {
		return smsCheck;
	}

	public void setSmsCheck(Integer smsCheck) {
		this.smsCheck = smsCheck;
	}
}
