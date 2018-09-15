package cn.lonsun.content.survey.internal.service;


import cn.lonsun.content.survey.internal.entity.SurveyIpEO;
import cn.lonsun.core.base.service.IBaseService;

public interface ISurveyIpService extends IBaseService<SurveyIpEO>{

	void deleteByThemeId(Long[] ids);

	void deleteByQuestionId(Long[] ids);

	Long getSurveyIpCount(Long themeId,String ip,String time);
}
