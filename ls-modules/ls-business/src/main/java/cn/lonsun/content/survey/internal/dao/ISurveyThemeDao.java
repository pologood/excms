package cn.lonsun.content.survey.internal.dao;

import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface ISurveyThemeDao extends IBaseDao<SurveyThemeEO>{

	Pagination getPage(QueryVO query);

	SurveyThemeEO getSurveyThemeByContentId(Long contentId);

	List<SurveyThemeVO> getSurveyThemeVOS(String code);
}
