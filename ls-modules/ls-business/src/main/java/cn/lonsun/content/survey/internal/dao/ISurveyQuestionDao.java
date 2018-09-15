package cn.lonsun.content.survey.internal.dao;

import java.util.List;

import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface ISurveyQuestionDao extends IBaseDao<SurveyQuestionEO>{

	void deleteByThemeId(Long[] ids);

	Pagination getPage(SurveyQueryVO query);

	List<SurveyQuestionEO> getListByThemeId(Long themeId);

}
