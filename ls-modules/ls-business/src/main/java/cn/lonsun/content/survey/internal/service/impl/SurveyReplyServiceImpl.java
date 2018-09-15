package cn.lonsun.content.survey.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.survey.internal.dao.ISurveyReplyDao;
import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;
import cn.lonsun.content.survey.internal.service.ISurveyReplyService;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;

@Service
public class SurveyReplyServiceImpl extends BaseService<SurveyReplyEO> implements ISurveyReplyService{
	
	@Autowired
	private ISurveyReplyDao surveyReplyDao;

	@Override
	public void deleteByQuestionIds(Long[] ids) {
		
		surveyReplyDao.deleteByQuestionIds(ids);
	}

	@Override
	public Pagination getPage(SurveyQueryVO query) {
		return surveyReplyDao.getPage(query);
	}

	@Override
	public List<SurveyReplyEO> getListByThemeId(Long themeId,Integer isIssued) {
		return surveyReplyDao.getListByThemeId(themeId,isIssued);
	}

}
