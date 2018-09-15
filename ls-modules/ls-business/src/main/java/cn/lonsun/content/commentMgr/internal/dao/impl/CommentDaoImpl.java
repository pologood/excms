package cn.lonsun.content.commentMgr.internal.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.commentMgr.internal.dao.ICommentDao;
import cn.lonsun.content.commentMgr.internal.entity.CommentEO;
import cn.lonsun.content.commentMgr.vo.CommentPageVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;

@Repository("commentDao")
public class CommentDaoImpl extends MockDao<CommentEO> implements ICommentDao {

	@Override
	public Pagination getPage(CommentPageVO commentVO) {
		StringBuffer hql=new StringBuffer("from CommentEO where recordStatus='Normal'");
		Map<String,Object> map=new HashMap<String, Object>();
		if(!AppUtil.isEmpty(commentVO.getSiteId())){
			hql.append(" and siteId=:siteId");
			map.put("siteId", commentVO.getSiteId());
		}
		if(!AppUtil.isEmpty(commentVO.getColumnId())){
			hql.append(" and columnId=:columnId");
			map.put("columnId", commentVO.getColumnId());
		}
		if(!AppUtil.isEmpty(commentVO.getContentId())){
			hql.append(" and contentId=:contentId");
			map.put("contentId", commentVO.getContentId());
		}
		if(!AppUtil.isEmpty(commentVO.getTitle())){
			hql.append(" and ( content like :content escape'\\'");
			map.put("content","%".concat(commentVO.getTitle()).concat("%"));
			hql.append(" or contentTitle like :contentTitle escape'\\')");
			map.put("contentTitle","%".concat(commentVO.getTitle()).concat("%"));
		}
		if(!AppUtil.isEmpty(commentVO.getIsPublish())){
			hql.append(" and isPublish=:isPublish");
			map.put("isPublish", commentVO.getIsPublish());
		}
		if(!AppUtil.isEmpty(commentVO.getIsRead())){
			hql.append(" and isRead=:isRead");
			map.put("isRead", commentVO.getIsRead());
		}
		hql.append(" order by contentId desc, createDate desc");
		return getPagination(commentVO.getPageIndex(), commentVO.getPageSize(), hql.toString(), map);
	}

	@Override
	public void updateStatus(String column, Integer status, Long id) {
		String hql="update CommentEO set "+column+"=?,publishDate=? where id=?";
		executeUpdateByHql(hql, new Object[]{status,new Date(),id});
	}

	@Override
	public void updatePublish(Long[] ids, Integer status) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("isPublish", status);
		map.put("publishDate", new Date());
		map.put("ids", ids);
		String hql="update CommentEO set isPublish=:isPublish , publishDate=:publishDate where id in (:ids)";
		executeUpdateByJpql(hql, map);
	}

	@Override
	public List<CommentEO> getContentComments(Long contentId) {
		String hql="from CommentEO where recordStatus='Normal' and contentId=?";
		return getEntitiesByHql(hql, new Object[]{contentId});
	}

	@Override
	public Long getNumByColumn(Integer isPublish, Long columnId) {
		String hql="from CommentEO where recordStatus='Normal' and isPublish=? and columnId=?";
		
		return getCount(hql, new Object[]{isPublish,columnId});
	}

	@Override
	public void deleteByContent(Long[] contentIds) {
		Map<String,Object> map=new HashMap<String,Object>();
		String hql="update CommentEO set recordStatus='Removed' where contentId in(:contentIds)";
		map.put("contentIds", contentIds);
		executeUpdateByJpql(hql, map);
	}

}
