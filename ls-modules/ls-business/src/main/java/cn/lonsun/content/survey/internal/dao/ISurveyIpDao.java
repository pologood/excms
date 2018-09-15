package cn.lonsun.content.survey.internal.dao;

import java.util.List;

import cn.lonsun.content.survey.internal.entity.SurveyIpEO;
import cn.lonsun.core.base.dao.IBaseDao;

public interface ISurveyIpDao extends IBaseDao<SurveyIpEO>{

	void deleteByThemeId(Long[] ids);

	void deleteByQuestionId(Long[] ids);

	List<SurveyIpEO> getSurveyIp(Long questionId, String ip, String time);
}
