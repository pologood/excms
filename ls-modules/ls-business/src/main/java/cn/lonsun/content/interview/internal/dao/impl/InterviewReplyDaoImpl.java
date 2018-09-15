package cn.lonsun.content.interview.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.content.interview.internal.dao.IInterviewReplyDao;
import cn.lonsun.content.interview.internal.entity.InterviewReplyEO;
import cn.lonsun.core.base.dao.impl.BaseDao;

@Repository
public class InterviewReplyDaoImpl extends BaseDao<InterviewReplyEO> implements IInterviewReplyDao{

	@Override
	public void deleteByInterviewId(Long[] ids) {
		String hql = "delete from InterviewReplyEO where interviewId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@Override
	public List<InterviewReplyEO> getListByInterviewId(Long interviewId) {
		String hql = "from InterviewReplyEO where interviewId = ? order by replyId desc";
		return getEntitiesByHql(hql, new Object[]{interviewId});
	}

	@Override
	public void deleteByQuestionId(Long[] ids) {
		String hql = "delete from InterviewReplyEO where questionId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

}
