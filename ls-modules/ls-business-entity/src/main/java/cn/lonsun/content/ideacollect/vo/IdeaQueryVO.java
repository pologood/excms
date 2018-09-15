package cn.lonsun.content.ideacollect.vo;

import cn.lonsun.common.vo.PageQueryVO;

public class IdeaQueryVO extends PageQueryVO{
	
	private String searchText;
	
	private Long siteId;

	private Long columnId;

	private Long collectInfoId;
	
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

	public Long getCollectInfoId() {
		return collectInfoId;
	}

	public void setCollectInfoId(Long collectInfoId) {
		this.collectInfoId = collectInfoId;
	}

	public Integer getIssued() {
		return issued;
	}

	public void setIssued(Integer issued) {
		this.issued = issued;
	}

}
