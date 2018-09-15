package cn.lonsun.content.leaderwin.vo;

import cn.lonsun.common.vo.PageQueryVO;

public class LeaderQueryVO extends PageQueryVO{

	private String searchText;

	private Long siteId;

	private Long columnId;
	
	private Integer issued;

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

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public Integer getIssued() {
		return issued;
	}

	public void setIssued(Integer issued) {
		this.issued = issued;
	}
	

}
