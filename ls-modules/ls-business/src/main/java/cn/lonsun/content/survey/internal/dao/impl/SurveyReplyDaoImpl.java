package cn.lonsun.content.survey.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.content.survey.internal.dao.ISurveyReplyDao;
import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;

@Repository
public class SurveyReplyDaoImpl extends BaseDao<SurveyReplyEO> implements ISurveyReplyDao{

	@Override
	public void deleteByQuestionIds(Long[] ids) {
		String hql = "delete from SurveyReplyEO where questionId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();		
	}

	@Override
	public Pagination getPage(SurveyQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from SurveyReplyEO where questionId = ? ");
		values.add(query.getQuestionId());
		hql.append(" order by replyId desc");
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray());
	}

	@Override
	public List<SurveyReplyEO> getListByThemeId(Long themeId,Integer isIssued) {
		List<Object> values = new ArrayList<Object>();
		String hql = "from SurveyReplyEO where themeId = ? ";
		values.add(themeId);
		if(isIssued != null){
			hql +="and isIssued = ?";
			values.add(isIssued);
		}
		return getEntitiesByHql(hql, values.toArray());
	}

}
