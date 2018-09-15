package cn.lonsun.content.commentMgr.internal.dao;

import java.util.List;

import cn.lonsun.content.commentMgr.internal.entity.CommentEO;
import cn.lonsun.content.commentMgr.vo.CommentPageVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

public interface ICommentDao extends IMockDao<CommentEO> {

	public Pagination getPage(CommentPageVO commentVO);
	
	public void updateStatus(String column,Integer status,Long id);
	
	public void updatePublish(Long[] ids,Integer status);
	
	public List<CommentEO> getContentComments(Long contentId);
	
	public Long getNumByColumn(Integer isPublish,Long columnId);
	
	public void deleteByContent(Long[] contentIds);
}
