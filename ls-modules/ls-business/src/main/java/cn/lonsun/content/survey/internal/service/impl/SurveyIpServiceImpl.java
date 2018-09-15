package cn.lonsun.content.survey.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.content.survey.internal.dao.ISurveyIpDao;
import cn.lonsun.content.survey.internal.entity.SurveyIpEO;
import cn.lonsun.content.survey.internal.service.ISurveyIpService;
import cn.lonsun.core.base.service.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyIpServiceImpl extends BaseService<SurveyIpEO> implements ISurveyIpService{

	@Autowired
	private ISurveyIpDao surveyIpDao;

	@Override
	public void deleteByThemeId(Long[] ids) {
		surveyIpDao.deleteByThemeId(ids);
		
	}

	@Override
	public void deleteByQuestionId(Long[] ids) {
		surveyIpDao.deleteByQuestionId(ids);		
	}

	@Override
	public Long getSurveyIpCount(Long themeId, String ip, String time) {
		List<SurveyIpEO> list = surveyIpDao.getSurveyIp(themeId,ip,time);
		Long count = 0L;
		if(list !=null && list.size()>0){
			Integer c  = list.size();
			count = c.longValue();
		}
		return count;
	}
	
}
