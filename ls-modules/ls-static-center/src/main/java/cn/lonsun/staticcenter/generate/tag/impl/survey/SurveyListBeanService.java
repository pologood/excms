package cn.lonsun.staticcenter.generate.tag.impl.survey;

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
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.HtmlUtil;
import cn.lonsun.util.TimeOutUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class SurveyListBeanService extends AbstractBeanService {

    @Autowired
    private ISurveyThemeDao surveyThemeDao;

    @Autowired
    private ISurveyQuestionDao surveyQuestionDao;

    @Autowired
    private ISurveyOptionsDao surveyOptionsDao;

    @Autowired
    private ISurveyReplyDao surveyReplyDao;

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        List<Long> ids = new ArrayList<Long>();
        String id = paramObj.getString(GenerateConstant.ID);
        String timeStr = paramObj.getString(GenerateConstant.TIME_STR);
        String dateFormat = paramObj.getString(GenerateConstant.DATE_FORMAT);
        Boolean isLoadOptions = false;
        try {
            isLoadOptions = paramObj.getBoolean("isLoadOptions");
            if(isLoadOptions == null){isLoadOptions = false;}
        } catch (Exception e) {
        }
        SimpleDateFormat format = StringUtils.isEmpty(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd") : new SimpleDateFormat(dateFormat);
        if (!StringUtils.isEmpty(id)) {
            // 默认查询本栏目
            ids.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(id.split(","), Long.class))));

        } else {
            Long columnId = context.getColumnId();
            // 默认查询本栏目
            ids.add(columnId);
        }
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        String idsStr = StringUtils.join(ids.toArray(), ",");
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql =
                new StringBuffer(
                        "select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                                + "b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,b.createDate as createDate,"
                                + "s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,"
                                + "s.endTime as endTime,s.isLink as isLink,s.linkUrl as linkUrl,s.content as content"
                                + " from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.columnId in ("
                                + idsStr + ") and b.siteId= ? and b.isPublish = 1 ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.survey.toString());
        values.add(siteId);
        hql.append(" order by b.num desc");
        List<SurveyThemeVO> list = (List<SurveyThemeVO>) surveyThemeDao.getBeansByHql(hql.toString(), values.toArray(), SurveyThemeVO.class, num);
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (SurveyThemeVO stVOs : list) {
                if(stVOs.getStartTime() != null && stVOs.getEndTime() != null){
                    String startEndTime =
                            timeStr.replace(GenerateConstant.START_TIME, format.format(stVOs.getStartTime())).replace(GenerateConstant.END_TIME,
                                    format.format(stVOs.getEndTime()));
                    stVOs.setTimeStr(startEndTime);
                    stVOs.setIsTimeOut(TimeOutUtil.getTimeOut(stVOs.getStartTime(), stVOs.getEndTime()));
                }
                if (stVOs.getIsLink() == 0) {
                    stVOs.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), stVOs.getColumnId(), stVOs.getContentId()) + "?type=1");
                    stVOs.setViewUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), stVOs.getColumnId(), stVOs.getContentId()) + "?type=0");
                    if (!StringUtils.isEmpty(stVOs.getContent())) {
                        try {
                            stVOs.setContent(HtmlUtil.getTextFromTHML(stVOs.getContent()));
                        } catch (Exception e) {
                        }
                    }
                    //设置选项
                    if (isLoadOptions) {
                        setOptions(stVOs);
                    }
                } else {
                    stVOs.setViewUrl(stVOs.getLinkUrl());
                }

            }
        }
        return list;
    }

    private void setOptions(SurveyThemeVO stVO) {
        List<SurveyQuestionEO> questions = surveyQuestionDao.getListByThemeId(stVO.getThemeId());
        if (questions != null && questions.size() > 0) {
            Map<Long, List<SurveyOptionsEO>> optionsMap = MapUtil.getOptionsMap(surveyOptionsDao.getListByThemeId(stVO.getThemeId()));
            Map<Long, List<SurveyReplyEO>> replysMap = MapUtil.getReplysMap(surveyReplyDao.getListByThemeId(stVO.getThemeId(), SurveyReplyEO.Status.Yes.getStatus()));
            for (SurveyQuestionEO sq : questions) {
                if (sq.getOptions() == 3) {
                    List<SurveyReplyEO> replysList = (replysMap == null ? null : replysMap.get(sq.getQuestionId()));
                    sq.setReplys(replysList);
                } else {
                    List<SurveyOptionsEO> options = (optionsMap == null ? null : optionsMap.get(sq.getQuestionId()));
                    Long count = 0L;
                    if (options != null && options.size() > 0) {
                        for (int i = 0; i < options.size(); i++) {
                            Long votesCount = options.get(i).getVotesCount();
                            count += (votesCount == null ? 0 : votesCount);
                        }
                        for (int i = 0; i < options.size(); i++) {
                            Double progress = 0.00;
                            try {
                                progress = (Double) options.get(i).getVotesCount().doubleValue() / count.doubleValue() * 100;
                            } catch (Exception e) {
                            }
                            options.get(i).setProgress(progress.intValue());
                        }
                    }
                    sq.setOptionsList(options);
                }
            }
            stVO.setQuestions(questions);
        }
    }
}