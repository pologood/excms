package cn.lonsun.wechatmgr.vo;

import cn.lonsun.common.vo.PageQueryVO;

public class WeChatArticleVO extends PageQueryVO {

	private String title;
	
	private Integer type;

	private Long siteId;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	
	
}
