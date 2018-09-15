package cn.lonsun.staticcenter.generate.util;

public class CommentsVO {

	private Long siteId;
	
	private Long columnId;
	
	private Long contentId;
	
	private String contentTitle;
	
	private Integer isAllowComment;

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

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}
	
	public String getContentTitle() {
		return contentTitle;
	}

	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}

	public Integer getIsAllowComment() {
		return isAllowComment;
	}

	public void setIsAllowComment(Integer isAllowComment) {
		this.isAllowComment = isAllowComment;
	}
	
	
}
