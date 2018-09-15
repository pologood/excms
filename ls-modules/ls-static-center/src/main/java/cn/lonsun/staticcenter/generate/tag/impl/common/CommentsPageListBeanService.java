package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.content.commentMgr.internal.dao.ICommentDao;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;

import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentsPageListBeanService extends AbstractBeanService {

	@Autowired
	private ICommentDao commentDao;
	@Override
	public Object getObject(JSONObject paramObj) {
		Context context = ContextHolder.getContext();
		Long contentId=context.getContentId();
		String hql="from CommentEO where recordStatus='Normal' and contentId=? and isPublish=1";
		return commentDao.getEntitiesByHql(hql, new Object[]{contentId});
	}

}
