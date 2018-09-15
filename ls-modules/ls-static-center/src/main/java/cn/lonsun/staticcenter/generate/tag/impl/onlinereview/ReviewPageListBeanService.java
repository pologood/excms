package cn.lonsun.staticcenter.generate.tag.impl.onlinereview;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.survey.internal.dao.ISurveyOptionsDao;
import cn.lonsun.content.survey.internal.dao.ISurveyQuestionDao;
import cn.lonsun.content.survey.internal.dao.ISurveyReplyDao;
import cn.lonsun.content.survey.internal.dao.ISurveyThemeDao;
import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.util.MapUtil;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.TimeOutUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ReviewPageListBeanService extends AbstractBeanService {

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
        String isShow = context.getParamMap().get("cid");
        paramObj.put("action", "");
        if (!StringUtils.isEmpty(isShow)) {
            paramObj.put("action", "isShow");
        }
        // 设置action参数
        return !StringUtils.isEmpty(isShow) ? getReviewInfo(paramObj) : getPageList(paramObj);
    }

    private Object getReviewInfo(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        String cid = context.getParamMap().get("cid");
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql =
            new StringBuffer(
                "select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                    + "b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,"
                    + "s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,"
                    + "s.endTime as endTime,s.isLink as isLink,s.linkUrl as linkUrl,s.content as content"
                    + " from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1 and b.id = ?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.reviewInfo.toString());
        values.add(Long.parseLong(cid));
        hql.append(" order by b.num desc");
        SurveyThemeVO stVO = (SurveyThemeVO) surveyThemeDao.getBean(hql.toString(), values.toArray(), SurveyThemeVO.class);
        if (stVO != null) {
            if (stVO.getIsLink() == 0) {
                stVO.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), stVO.getColumnId(), null) + "?cid="
                    + stVO.getContentId());
            }
            stVO.setViewUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), stVO.getColumnId(), stVO.getContentId()));
            List<SurveyQuestionEO> questions = surveyQuestionDao.getListByThemeId(stVO.getThemeId());
            if (questions != null && questions.size() > 0) {
                Map<Long, List<SurveyOptionsEO>> optionsMap = MapUtil.getOptionsMap(surveyOptionsDao.getListByThemeId(stVO.getThemeId()));
                // Map<Long,List<SurveyReplyEO>> replysMap =
                // MapUtil.getReplysMap(surveyReplyDao.getListByThemeId(stVO.getThemeId()));
                for (SurveyQuestionEO sq : questions) {
                    if (sq.getOptions() == 3) {
                        // //不是文本时
                        // List<SurveyReplyEO> replysList = (replysMap ==
                        // null?null:replysMap.get(sq.getQuestionId()));
                        // sq.setReplys(replysList);
                    } else {
                        List<SurveyOptionsEO> options = (optionsMap == null ? null : optionsMap.get(sq.getQuestionId()));
                        sq.setOptionsList(options);
                    }
                }
                stVO.setQuestions(questions);
            }
            stVO.setIsTimeOut(TimeOutUtil.getTimeOut(stVO.getStartTime(), stVO.getEndTime()));
        }
        return stVO;
    }

    // 获取分页列表
    private Object getPageList(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        // 此写法是为了使得在页面这样调用也能解析
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        String timeStr = paramObj.getString(GenerateConstant.TIME_STR);
        String dateFormat = paramObj.getString(GenerateConstant.DATE_FORMAT);
        SimpleDateFormat format = StringUtils.isEmpty(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd") : new SimpleDateFormat(dateFormat);
        // 需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql =
            new StringBuffer(
                "select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                    + "b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,"
                    + "s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,"
                    + "s.endTime as endTime,s.isLink as isLink,s.linkUrl as linkUrl"
                    + " from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.columnId = ? and b.siteId= ? and b.isPublish = 1");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.reviewInfo.toString());
        values.add(columnId);
        values.add(context.getSiteId());
        hql.append(" order by b.num desc");
        Pagination pagination = surveyThemeDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), SurveyThemeVO.class);
        if (null != pagination) {
            List<?> resultList = pagination.getData();
            // 处理查询结果
            if (null != resultList && !resultList.isEmpty()) {
                for (Object o : resultList) {
                    SurveyThemeVO vo = (SurveyThemeVO) o;
                    if (vo.getIsLink() == 0) {
                        String startEndTime =
                            timeStr.replace(GenerateConstant.START_TIME, format.format(vo.getStartTime())).replace(GenerateConstant.END_TIME,
                                format.format(vo.getEndTime()));
                        vo.setTimeStr(startEndTime);
                        vo.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), vo.getColumnId(), null) + "?cid="
                            + vo.getContentId());
                    }
                }
            }
            // 设置连接地址
            String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), columnId, null);
            pagination.setLinkPrefix(path);
        }
        return pagination;
    }
}