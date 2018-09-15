package cn.lonsun.content.survey.internal.service;



import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface ISurveyThemeService extends IBaseService<SurveyThemeEO>{

	Pagination getPage(QueryVO query);

	void delete(Long[] ids,Long[] contentIds);

	Long saveOrupdate(SurveyThemeVO surveyThemeVO);

	SurveyThemeEO getSurveyThemeByContentId(Long id);

	SurveyThemeVO getSurveyThemeVO(BaseContentVO content);

	SurveyThemeVO getSurveyThemeVO(Long id);

	/**
	 * 彻底删除接口
	 * @param contentId
	 */
	void deleteByContentIds(Long contentId);

	List<SurveyThemeVO> getSurveyThemeVOS(String code);

	BaseContentEO save(SurveyThemeVO surveyThemeVO);
}
