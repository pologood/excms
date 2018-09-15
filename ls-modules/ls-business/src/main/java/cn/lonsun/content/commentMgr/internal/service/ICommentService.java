package cn.lonsun.content.commentMgr.internal.service;

import java.util.List;

import cn.lonsun.content.commentMgr.internal.entity.CommentEO;
import cn.lonsun.content.commentMgr.vo.CommentPageVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

public interface ICommentService extends IMockService<CommentEO> {
	public Pagination getPage(CommentPageVO commentVO);
	
	public void changStatus(Long id,Integer mark);
	
	public void updatePublish(Long[] ids,Integer status);
	
	public List<CommentEO> getContentComments(Long contentId);
	public Long getNumByColumn(Integer isPublish,Long columnId);
	
	public void deleteByContent(Long[] contentIds);
}
