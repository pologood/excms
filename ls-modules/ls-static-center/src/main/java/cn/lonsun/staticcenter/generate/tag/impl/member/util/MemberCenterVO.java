package cn.lonsun.staticcenter.generate.tag.impl.member.util;

import cn.lonsun.system.member.vo.MemberSessionVO;


public class MemberCenterVO implements java.io.Serializable{

	
	public enum Type {
		member, //个人资料
		liuyan,// 留言
		post,// 帖子
		safe,//安全验证
		uppw;//修改密码
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer isLogin = 0;

	private Long  siteId;

	private String linkUrl;

	private String action = Type.member.toString();;
	
	private String step;
	
	private String type;

	private MemberSessionVO member;


	public Integer getIsLogin() {
		return isLogin;
	}


	public void setIsLogin(Integer isLogin) {
		this.isLogin = isLogin;
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


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getStep() {
		return step;
	}


	public void setStep(String step) {
		this.step = step;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public MemberSessionVO getMember() {
		return member;
	}


	public void setMember(MemberSessionVO member) {
		this.member = member;
	}
	
    

}
