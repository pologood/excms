package cn.lonsun.content.survey.internal.dao;

import java.util.List;

import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface ISurveyReplyDao extends IBaseDao<SurveyReplyEO>{

	void deleteByQuestionIds(Long[] ids);

	Pagination getPage(SurveyQueryVO query);

	List<SurveyReplyEO> getListByThemeId(Long themeId,Integer isIssued);

}
