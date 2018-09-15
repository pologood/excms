package cn.lonsun.wechatmgr.vo;

import java.util.List;

import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatPushMsgEO;

public class PushArticleVO extends WeChatPushMsgEO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6562787643530472312L;
	private List<WeChatArticleEO> newsList;

	public List<WeChatArticleEO> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<WeChatArticleEO> newsList) {
		this.newsList = newsList;
	}

}
