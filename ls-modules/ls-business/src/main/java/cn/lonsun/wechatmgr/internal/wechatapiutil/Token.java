package cn.lonsun.wechatmgr.internal.wechatapiutil;

import java.util.Date;

public class Token {
	
	private AccessToken token;
	//当前时间戳
	private Long getTime=(new Date()).getTime();
	
	public AccessToken getToken() {
		return token;
	}
	public void setToken(AccessToken token) {
		this.token = token;
	}
	public Long getGetTime() {
		return getTime;
	}
	public void setGetTime(Long getTime) {
		this.getTime = getTime;
	}
	
	public static void main(String[] args) {
		//ApiUtil.getMaterial(null);
	}
}
