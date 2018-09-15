package cn.lonsun.content.survey.internal.service;

import java.util.List;

import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

public interface ISurveyReplyService extends IBaseService<SurveyReplyEO>{

	void deleteByQuestionIds(Long[] ids);

	Pagination getPage(SurveyQueryVO query);

	List<SurveyReplyEO> getListByThemeId(Long themeId,Integer isIssued);

}
