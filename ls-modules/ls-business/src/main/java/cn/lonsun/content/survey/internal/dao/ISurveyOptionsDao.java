package cn.lonsun.content.survey.internal.dao;

import java.util.List;

import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.core.base.dao.IBaseDao;

public interface ISurveyOptionsDao extends IBaseDao<SurveyOptionsEO>{

	void deleteByThemeId(Long[] ids);

	void deleteByQuestionId(Long[] ids);

	List<SurveyOptionsEO> getListByThemeId(Long themeId);

	List<SurveyOptionsEO> getSurveyOptionsByQuestionId(Long[] ids);

	List<SurveyOptionsEO> getSurveyOptionsByThemeId(Long[] ids);

	Long getVotesCount(Long id);

}
