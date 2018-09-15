package cn.lonsun.wechatmgr.vo;

import java.util.List;

import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatPushMsgEO;

public class PushMsg extends WeChatPushMsgEO {

	private List<WeChatArticleEO> newsList;
	
	private String groupName;

	public List<WeChatArticleEO> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<WeChatArticleEO> newsList) {
		this.newsList = newsList;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
