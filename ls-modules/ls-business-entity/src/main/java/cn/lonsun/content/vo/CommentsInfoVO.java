package cn.lonsun.content.vo;

import cn.lonsun.content.internal.entity.BaseContentEO;

public class CommentsInfoVO extends BaseContentEO{
	
	//评论条数
	private Integer commentsNum=0;
	//未发布（审核）条数
	private Integer unPublishNum=0;
	//今日评论条数
	private Integer todayCommentsNum=0;
	public Integer getCommentsNum() {
		return commentsNum;
	}
	public void setCommentsNum(Integer commentsNum) {
		this.commentsNum = commentsNum;
	}
	public Integer getUnPublishNum() {
		return unPublishNum;
	}
	public void setUnPublishNum(Integer unPublishNum) {
		this.unPublishNum = unPublishNum;
	}
	public Integer getTodayCommentsNum() {
		return todayCommentsNum;
	}
	public void setTodayCommentsNum(Integer todayCommentsNum) {
		this.todayCommentsNum = todayCommentsNum;
	}
	
	
	
}
