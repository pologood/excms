package cn.lonsun.content.survey.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.content.survey.internal.dao.ISurveyIpDao;
import cn.lonsun.content.survey.internal.entity.SurveyIpEO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;

@Repository
public class SurveyIpDaoImpl extends BaseDao<SurveyIpEO> implements ISurveyIpDao{

	@Override
	public void deleteByThemeId(Long[] ids) {
		String hql = "delete from SurveyIpEO where themeId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@Override
	public void deleteByQuestionId(Long[] ids) {
		String hql = "delete from SurveyIpEO where questionId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@Override
	public List<SurveyIpEO> getSurveyIp(Long themeId, String ip, String time) {
		List<Object> values = new ArrayList<Object>();
		String hql = "from SurveyIpEO where themeId = ? and ipAddr = ?";
		values.add(themeId);
		values.add(ip);
		if(!StringUtils.isEmpty(time)){
			hql +=" and to_char(voteTime,'yyyy-MM-dd') = ?";
			values.add(time);
		}
		return getEntitiesByHql(hql, values.toArray());
	}

}
