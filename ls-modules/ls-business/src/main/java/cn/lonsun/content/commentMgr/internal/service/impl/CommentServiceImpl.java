package cn.lonsun.content.commentMgr.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.commentMgr.internal.dao.ICommentDao;
import cn.lonsun.content.commentMgr.internal.entity.CommentEO;
import cn.lonsun.content.commentMgr.internal.service.ICommentService;
import cn.lonsun.content.commentMgr.vo.CommentPageVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;

@Service("commentService")
public class CommentServiceImpl extends MockService<CommentEO> implements
		ICommentService {

	@Autowired
	private ICommentDao commentDao;
	@Override
	public Pagination getPage(CommentPageVO commentVO) {
		return commentDao.getPage(commentVO);
	}
	@Override
	public void changStatus(Long id, Integer mark) {
		CommentEO _eo=getEntity(CommentEO.class, id);
		Integer status=0;
		if(mark==1){
			if(_eo.getIsPublish()!=1) status=1;
			commentDao.updateStatus("isPublish", status, id);
		}else if(mark==2){
			if(_eo.getIsRead()!=1) status=1;
			commentDao.updateStatus("isRead", status, id);
		}else{
			throw new BaseRunTimeException(TipsMode.Message.toString(), "非法参数");
		}
	}
	@Override
	public void updatePublish(Long[] ids, Integer status) {
		commentDao.updatePublish(ids, status);
	}
	@Override
	public List<CommentEO> getContentComments(Long contentId) {
		return commentDao.getContentComments(contentId);
	}
	@Override
	public Long getNumByColumn(Integer isPublish,Long columnId) {
		return commentDao.getNumByColumn(isPublish,columnId);
	}
	@Override
	public void deleteByContent(Long[] contentIds) {
		commentDao.deleteByContent(contentIds);
	}
	
}
