package cn.lonsun.content.survey.internal.service;

import java.util.List;

import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

public interface ISurveyQuestionService extends IBaseService<SurveyQuestionEO>{

	void deleteByThemeId(Long[] ids);

	Pagination getPage(SurveyQueryVO query);

	void delete(Long[] ids);

	List<SurveyQuestionEO> getListByThemeId(Long themeId);

}
