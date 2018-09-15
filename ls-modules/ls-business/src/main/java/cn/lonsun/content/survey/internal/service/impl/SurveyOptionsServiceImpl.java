package cn.lonsun.content.survey.internal.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.lonsun.system.member.vo.MemberSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.survey.internal.dao.ISurveyOptionsDao;
import cn.lonsun.content.survey.internal.entity.SurveyIpEO;
import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;
import cn.lonsun.content.survey.internal.service.ISurveyIpService;
import cn.lonsun.content.survey.internal.service.ISurveyOptionsService;
import cn.lonsun.content.survey.internal.service.ISurveyReplyService;
import cn.lonsun.content.survey.vo.SurveyWebVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.util.FileUploadUtil;

@Service
public class SurveyOptionsServiceImpl extends BaseService<SurveyOptionsEO> implements ISurveyOptionsService{

	@Autowired
	private ISurveyOptionsDao surveyOptionsDao;

	@Autowired
	private ISurveyReplyService surveyReplyService;

	@Autowired
	private ISurveyIpService surveyIpService;

	@Override
	public void deleteByThemeId(Long[] ids) {
		List<SurveyOptionsEO> sos = surveyOptionsDao.getSurveyOptionsByThemeId(ids);
		if(sos != null && sos.size() > 0){
			List<String> strs = new ArrayList<String>();
			for(SurveyOptionsEO so:sos){
				if(!StringUtils.isEmpty(so.getPicUrl())){
					strs.add(so.getPicUrl());
				}
			}
			if(strs !=null && strs.size()>0){
				FileUploadUtil.deleteFileCenterEO(strs.toArray(new String[]{}));
			}
			surveyOptionsDao.deleteByThemeId(ids);
		}
	}

	@Override
	public void deleteByQuestionId(Long[] ids) {
		List<SurveyOptionsEO> sos = surveyOptionsDao.getSurveyOptionsByQuestionId(ids);
		if(sos != null && sos.size() > 0){
			List<String> strs = new ArrayList<String>();
			for(SurveyOptionsEO so:sos){
				if(!StringUtils.isEmpty(so.getPicUrl())){
					strs.add(so.getPicUrl());
				}
			}
			if(strs !=null && strs.size()>0){
				FileUploadUtil.deleteFileCenterEO(strs.toArray(new String[]{}));
			}
			surveyOptionsDao.deleteByQuestionId(ids);
		}
	}

	@Override
	public List<SurveyOptionsEO> getListByThemeId(Long themeId) {
		return surveyOptionsDao.getListByThemeId(themeId);
	}

	@Override
	public Long getVotesCount(Long id) {
		return surveyOptionsDao.getVotesCount(id);
	}

	@Override
	public void saveWeb(SurveyWebVO[] resultLists,Long themeId,String ip,MemberSessionVO sessionMember) {
		SurveyOptionsEO option = null;
		for(SurveyWebVO webvo:resultLists){
			String optionIds = webvo.getOptionIds();
			switch (webvo.getOptionType()) {
				case 1://单选
					if(!StringUtils.isEmpty(optionIds)){
						option = getEntity(SurveyOptionsEO.class, Long.parseLong(optionIds));
						if(option != null){
							option.setVotesCount(option.getVotesCount()+1);
							updateEntity(option);
						}
					}
					break;
				case 2://多选
					if(!StringUtils.isEmpty(optionIds)){
						String[] optionIdArray = optionIds.split(",");
						if(optionIdArray != null && optionIdArray.length>0){
							for(String optionId:optionIdArray){
								if(!StringUtils.isEmpty(optionId)){
									option = getEntity(SurveyOptionsEO.class, Long.parseLong(optionId));
									if(option != null){
										option.setVotesCount(option.getVotesCount()+1);
										updateEntity(option);
									}
								}
							}
						}
					}
					break;
				case 3://文本
					String content = webvo.getContent();
					if(!StringUtils.isEmpty(content)){
						SurveyReplyEO reply = new SurveyReplyEO();
						reply.setIp(ip);
						reply.setQuestionId(webvo.getQuestionId());
						reply.setThemeId(webvo.getThemeId());
						reply.setContent(content);
						surveyReplyService.saveEntity(reply);
					}
					break;
				default:
					break;
			}
		}
		//保存投票id记录
		saveSurveyIp(themeId,ip,sessionMember);
	}

	@Override
	public void saveReviewWeb(SurveyWebVO[] resultLists, Long themeId, String ip) {
		SurveyOptionsEO option = null;
		for(SurveyWebVO webvo:resultLists){
			String optionIds = webvo.getOptionIds();
			if(!StringUtils.isEmpty(optionIds)){
				option = getEntity(SurveyOptionsEO.class, Long.parseLong(optionIds));
				if(option != null){
					option.setVotesCount(option.getVotesCount()+1);
					updateEntity(option);
				}
			}
			String content = webvo.getContent();
			if(!StringUtils.isEmpty(content)){
				SurveyReplyEO reply = new SurveyReplyEO();
				reply.setIp(ip);
				reply.setQuestionId(webvo.getQuestionId());
				reply.setThemeId(webvo.getThemeId());
				reply.setContent(content);
				surveyReplyService.saveEntity(reply);
			}
		}
		//保存投票id记录
		saveSurveyIp(themeId,ip,null);
	}

	private void saveSurveyIp(Long themeId, String ip,MemberSessionVO sessionMember) {
		SurveyIpEO surveyIp = new SurveyIpEO();
		surveyIp.setQuestionId(null);
		if(sessionMember != null){
			surveyIp.setMemberId(sessionMember.getId());
			surveyIp.setName(sessionMember.getName());
		}
		surveyIp.setThemeId(themeId);
		surveyIp.setIpAddr(ip);
		surveyIp.setVoteTime(new Date());
		surveyIp.setVoteCount(1L);
		surveyIpService.saveEntity(surveyIp);
	}
}
