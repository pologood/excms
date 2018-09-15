package cn.lonsun.content.interview.internal.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.util.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.interview.internal.dao.IInterviewQuestionDao;
import cn.lonsun.content.interview.internal.entity.InterviewQuestionEO;
import cn.lonsun.content.interview.vo.InterviewQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;

@Repository
public class InterviewQuestionDaoImpl extends BaseDao<InterviewQuestionEO> implements IInterviewQuestionDao{

	@Override
	public void deleteByInterviewId(Long[] ids) {
		String hql = "delete from InterviewQuestionEO where interviewId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@Override
	public List<InterviewQuestionEO> getListByInterviewId(Long interviewId) {
		String hql = "from InterviewQuestionEO where interviewId = ? order by questionId desc";
		return getEntitiesByHql(hql, new Object[]{interviewId});
	}

	@Override
	public Pagination getPage(InterviewQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from InterviewQuestionEO where interviewId = ? ");
		values.add(query.getInterviewId());
		if(query != null){
			if(query.getIssued()!= null){
				hql.append(" and issued = ?");
				values.add(query.getIssued());
			}
		}
		hql.append(" order by questionId ").append(query.getSortOrder());
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray());
	}

	@Override
	public Long getParticipationNum(Long id) {
		Map<String,Object> map=new HashMap<String,Object>();
		String hql=" from InterviewQuestionEO where interviewId =:interviewId";
		map.put("interviewId",id);
		Long count =this.getCount(hql,map);
		return count;
	}

	@Override
	public Long getAnswerNum(Long id) {
		Map<String,Object> map=new HashMap<String,Object>();
		String hql=" from InterviewQuestionEO t where t.interviewId =:interviewId and t.isReply =:isReply";
		map.put("interviewId",id);
		map.put("isReply",InterviewQuestionEO.Status.Yes.getStatus());
		Long count =this.getCount(hql,map);
		return count;
	}

	@Override
	public Long getQtNetFriendNum(Long id) {
		String sql=" from(select distinct name from CMS_INTERVIEW_QUESTION t where t.interview_id = ?) q ";
		Long count=this.getCountBySql(sql,new Object[]{id});
		return count;
	}

	@Override
	public List<InterviewQuestionEO> getListByInterviewId(Long interviewId,String sortOrder) {
		StringBuffer hql =new StringBuffer("from InterviewQuestionEO where interviewId = ? and issued = 1") ;
		hql.append(" order by questionId ").append(sortOrder);
		return getEntitiesByHql(hql.toString(), new Object[]{interviewId});
	}
}
