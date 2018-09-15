package cn.lonsun.content.survey.internal.service;

import java.util.List;

import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.vo.SurveyWebVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.system.member.vo.MemberSessionVO;

public interface ISurveyOptionsService extends IBaseService<SurveyOptionsEO>{

	void deleteByThemeId(Long[] ids);

	void deleteByQuestionId(Long[] ids);

	List<SurveyOptionsEO> getListByThemeId(Long themeId);

	Long getVotesCount(Long id);

	void saveWeb(SurveyWebVO[] resultLists,Long themeId,String ip,MemberSessionVO sessionMember);

	void saveReviewWeb(SurveyWebVO[] resultLists, Long themeId, String ip);

}
