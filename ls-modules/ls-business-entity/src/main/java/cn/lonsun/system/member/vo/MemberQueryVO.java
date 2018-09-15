package cn.lonsun.system.member.vo;

import cn.lonsun.common.vo.PageQueryVO;

public class MemberQueryVO extends PageQueryVO {
	
	private String searchText;
	
	private Long siteId;

	private Integer memberType;

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Integer getMemberType() {
		return memberType;
	}

	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}
}
