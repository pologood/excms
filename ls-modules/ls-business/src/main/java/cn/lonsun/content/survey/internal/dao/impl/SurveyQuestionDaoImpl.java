package cn.lonsun.content.survey.internal.dao.impl;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.content.survey.internal.dao.ISurveyQuestionDao;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;

@Repository
public class SurveyQuestionDaoImpl extends BaseDao<SurveyQuestionEO> implements ISurveyQuestionDao{

	@Override
	public void deleteByThemeId(Long[] ids) {
		String hql = "delete from SurveyQuestionEO where themeId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@Override
	public Pagination getPage(SurveyQueryVO query) {
		StringBuffer hql = new StringBuffer("from SurveyQuestionEO where 1 = 1");
		List<Object> values = new ArrayList<Object>();
		if(query != null){
			if(!StringUtils.isEmpty(query.getSearchText())){
				hql.append(" and title like ?");
				values.add("%".concat(query.getSearchText()).concat("%"));
			}
		}
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray());
	}

	@Override
	public List<SurveyQuestionEO> getListByThemeId(Long themeId) {
		String hql = "from SurveyQuestionEO where themeId = ? order by questionId asc";
		return getEntitiesByHql(hql, new Object[]{themeId});
	}

}
