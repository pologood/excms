package cn.lonsun.content.vo;

import java.util.List;

public class SynColumnVO {
	private List<Long> picList;
	
	private Long contentId;

	public List<Long> getPicList() {
		return picList;
	}

	public void setPicList(List<Long> picList) {
		this.picList = picList;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}
	
}
