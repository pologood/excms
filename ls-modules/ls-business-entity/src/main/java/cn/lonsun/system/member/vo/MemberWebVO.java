package cn.lonsun.system.member.vo;

import cn.lonsun.system.member.internal.entity.MemberEO;

public class MemberWebVO implements java.io.Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		member, //个人资料
		liuyan,// 留言
		post,// 帖子
	}

	private Integer isLogin = 0;
	
	private Long  siteId;
	
	private String linkUrl;
	
	private String type = Type.member.toString();

	private MemberEO member;

	public Integer getIsLogin() {
		return isLogin;
	}

	public MemberEO getMember() {
		return member;
	}

	public void setIsLogin(Integer isLogin) {
		this.isLogin = isLogin;
	}

	public void setMember(MemberEO member) {
		this.member = member;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
