package cn.lonsun.wechatmgr.vo;

import cn.lonsun.common.vo.PageQueryVO;

public class KeyWordsVO extends PageQueryVO {

	private Long siteId;
	
	private String keyWords;
	
	private String msgType;

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	
}
