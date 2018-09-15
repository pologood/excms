package cn.lonsun.content.survey.internal.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.survey.internal.dao.ISurveyOptionsDao;
import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.core.base.dao.impl.BaseDao;

@Repository
public class SurveyOptionsDaoImpl extends BaseDao<SurveyOptionsEO> implements ISurveyOptionsDao{

	@Override
	public void deleteByThemeId(Long[] ids) {
		String hql = "delete from SurveyOptionsEO where themeId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@Override
	public void deleteByQuestionId(Long[] ids) {
		String hql = "delete from SurveyOptionsEO where questionId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SurveyOptionsEO> getSurveyOptionsByQuestionId(Long[] ids) {
		String hql = " from SurveyOptionsEO where questionId in (:ids)";
		return getCurrentSession().createQuery(hql).setParameterList("ids", ids).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SurveyOptionsEO> getSurveyOptionsByThemeId(Long[] ids) {
		String hql = " from SurveyOptionsEO where themeId in (:ids)";
		return getCurrentSession().createQuery(hql).setParameterList("ids", ids).list();
	}
	
	@Override
	public List<SurveyOptionsEO> getListByThemeId(Long themeId) {
		String hql = "from SurveyOptionsEO where themeId = ?  order by optionId asc";
		return getEntitiesByHql(hql, new Object[]{themeId});
	}

	@Override
	public Long getVotesCount(Long id) {
		Long votesCount = null;
		StringBuffer sb = new StringBuffer(
				"select sum(o.votesCount) from SurveyOptionsEO o where o.themeId = ?");
		Query query = getCurrentSession().createQuery(sb.toString()).setParameter(0, id);
		@SuppressWarnings("rawtypes")
		List list = query.list();
		if (list != null && list.size() > 0) {
			votesCount = Long.valueOf(list.get(0) == null ? "0" : list.get(0)
					.toString());
		}
		return votesCount;
	}

	

	

}
