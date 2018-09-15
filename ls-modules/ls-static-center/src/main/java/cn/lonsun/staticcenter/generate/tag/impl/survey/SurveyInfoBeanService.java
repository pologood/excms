package cn.lonsun.staticcenter.generate.tag.impl.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.lonsun.content.survey.vo.QuestionOptionsVO;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
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
public class SurveyInfoBeanService extends AbstractBeanService {

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
		String type = context.getParamMap().get("type");
		paramObj.put("type", type);
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
		values.add(BaseContentEO.TypeCode.survey.toString());
		values.add(contentId);
		hql.append(" order by b.num desc");
		SurveyThemeVO stVO = (SurveyThemeVO) surveyThemeDao.getBean(hql.toString(), values.toArray(), SurveyThemeVO.class);
		if(stVO != null){
			stVO.setViewUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), stVO.getColumnId(), stVO.getContentId())+ "?type=0");
			if(stVO.getIsLink()==0) {//非转链
				stVO.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), stVO.getColumnId(), stVO.getContentId())+ "?type=1");
			}
			List<SurveyQuestionEO> questions = surveyQuestionDao.getListByThemeId(stVO.getThemeId());
			if(questions != null && questions.size() > 0){
				Map<Long,List<SurveyOptionsEO>> optionsMap = MapUtil.getOptionsMap(surveyOptionsDao.getListByThemeId(stVO.getThemeId()));
				Map<Long,List<SurveyReplyEO>> replysMap =  MapUtil.getReplysMap(surveyReplyDao.getListByThemeId(stVO.getThemeId(),SurveyReplyEO.Status.Yes.getStatus()));
				for(SurveyQuestionEO sq:questions){
					if(sq.getOptions() == 3){
						List<SurveyReplyEO> replysList = (replysMap == null?null:replysMap.get(sq.getQuestionId()));
						sq.setReplys(replysList);
					}else{
						List<SurveyOptionsEO> options = (optionsMap == null?null:optionsMap.get(sq.getQuestionId()));
						Long count = 0L;
						if(options !=null && options.size()>0){
							for(int i=0;i<options.size();i++){
								Long votesCount = options.get(i).getVotesCount();
								count += (votesCount == null?0:votesCount);
							}
							for(int i=0;i<options.size();i++){
								Double progress = 0.00;
								try {
									progress = (Double) options.get(i).getVotesCount().doubleValue()/ count.doubleValue()*100;
								}catch (Exception e){}
								options.get(i).setProgress(progress.intValue());
							}
						}
						sq.setOptionsList(options);
					}
				}
				stVO.setQuestions(questions);
			}
		}
		stVO.setIsTimeOut(TimeOutUtil.getTimeOut(stVO.getStartTime(), stVO.getEndTime()));
		return stVO;
	}
}
