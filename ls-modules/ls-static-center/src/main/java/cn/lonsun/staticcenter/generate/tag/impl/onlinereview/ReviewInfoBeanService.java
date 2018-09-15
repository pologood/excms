package cn.lonsun.staticcenter.generate.tag.impl.onlinereview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.survey.internal.dao.ISurveyOptionsDao;
import cn.lonsun.content.survey.internal.dao.ISurveyQuestionDao;
import cn.lonsun.content.survey.internal.dao.ISurveyReplyDao;
import cn.lonsun.content.survey.internal.dao.ISurveyThemeDao;
import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;
import cn.lonsun.content.survey.util.MapUtil;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.util.TimeOutUtil;

import com.alibaba.fastjson.JSONObject;

@Component
public class ReviewInfoBeanService extends AbstractBeanService {
	@Autowired
	private ISurveyThemeDao surveyThemeDao;

	@Autowired
	private ISurveyQuestionDao surveyQuestionDao;

	@Autowired
	private ISurveyOptionsDao surveyOptionsDao;

	@Autowired
	private ISurveyReplyDao surveyReplyDao;

	@Override
	public Object getObject(JSONObject paramObj) {
		Context context = ContextHolder.getContext();
		Long columnId = context.getColumnId();
		Long contentId = context.getContentId();// 根据文章id查询文章
		// 此写法是为了使得在页面这样调用也能解析
		if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
			columnId = paramObj.getLong(GenerateConstant.ID);
		}
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,"
				+ "s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,"
				+ "s.endTime as endTime,s.isLink as isLink,s.linkUrl as linkUrl,s.content as content"
				+ " from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1 and b.id = ?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(BaseContentEO.TypeCode.reviewInfo.toString());
		values.add(contentId);
		hql.append(" order by b.num desc");
		SurveyThemeVO stVO = (SurveyThemeVO) surveyThemeDao.getBean(hql.toString(), values.toArray(), SurveyThemeVO.class);
		if(stVO != null){
			List<SurveyQuestionEO> questions = surveyQuestionDao.getListByThemeId(stVO.getThemeId());
			if(questions != null && questions.size() > 0){
				Map<Long,List<SurveyOptionsEO>> optionsMap = MapUtil.getOptionsMap(surveyOptionsDao.getListByThemeId(stVO.getThemeId()));
				Map<Long,List<SurveyReplyEO>> replysMap =  MapUtil.getReplysMap(surveyReplyDao.getListByThemeId(stVO.getThemeId(),SurveyReplyEO.Status.Yes.getStatus()));
				for(SurveyQuestionEO sq:questions){
					List<SurveyReplyEO> replysList = (replysMap == null?null:replysMap.get(sq.getQuestionId()));
					List<SurveyOptionsEO> options = (optionsMap == null?null:optionsMap.get(sq.getQuestionId()));
					sq.setReplys(replysList);
					sq.setOptionsList(options);
				}
				stVO.setQuestions(questions);
			}
		}
		stVO.setIsTimeOut(TimeOutUtil.getTimeOut(stVO.getStartTime(), stVO.getEndTime()));
		return stVO;
	}
}
