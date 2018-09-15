package cn.lonsun.content.survey.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.survey.internal.dao.ISurveyQuestionDao;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.internal.service.ISurveyIpService;
import cn.lonsun.content.survey.internal.service.ISurveyOptionsService;
import cn.lonsun.content.survey.internal.service.ISurveyQuestionService;
import cn.lonsun.content.survey.internal.service.ISurveyReplyService;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;

@Service
public class SurveyQuestionServiceImpl extends BaseService<SurveyQuestionEO> implements ISurveyQuestionService{
	
	@Autowired
	private ISurveyQuestionDao surveyQuestionDao;

	@Autowired
	private ISurveyIpService surveyIpService;
	
	@Autowired
	private ISurveyOptionsService surveyOptionsService;
	
	@Autowired
	private ISurveyReplyService surveyReplyService;

	@Override
	public void deleteByThemeId(Long[] ids) {
		surveyQuestionDao.deleteByThemeId(ids);
	}

	@Override
	public Pagination getPage(SurveyQueryVO query) {
		return surveyQuestionDao.getPage(query);
	}
	@Override
	public List<SurveyQuestionEO> getListByThemeId(Long themeId) {
		return surveyQuestionDao.getListByThemeId(themeId);
	}

	@Override
	public void delete(Long[] ids) {
		if(ids != null && ids.length >0){
			surveyQuestionDao.delete(SurveyQuestionEO.class,ids);
			surveyOptionsService.deleteByQuestionId(ids);
			surveyIpService.deleteByQuestionId(ids);
			surveyReplyService.deleteByQuestionIds(ids);
		}
	}



}
