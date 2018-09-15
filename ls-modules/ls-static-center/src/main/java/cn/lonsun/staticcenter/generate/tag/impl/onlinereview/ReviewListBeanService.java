package cn.lonsun.staticcenter.generate.tag.impl.onlinereview;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.survey.internal.dao.ISurveyThemeDao;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ReviewListBeanService extends AbstractBeanService {

    @Autowired
    private ISurveyThemeDao surveyThemeDao;

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        List<Long> ids = new ArrayList<Long>();
        String id = paramObj.getString(GenerateConstant.ID);
        String timeStr = paramObj.getString(GenerateConstant.TIME_STR);
        String dateFormat = paramObj.getString(GenerateConstant.DATE_FORMAT);
        SimpleDateFormat format = StringUtils.isEmpty(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd") : new SimpleDateFormat(dateFormat);
        if (!StringUtils.isEmpty(id)) {
            // 默认查询本栏目
            ids.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(id.split(","), Long.class))));

        } else {
            Long columnId = context.getColumnId();
            // 默认查询本栏目
            ids.add(columnId);
        }
        String idsStr = StringUtils.join(ids.toArray(), ",");
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql =
                new StringBuffer(
                        "select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                                + "b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,"
                                + "s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,"
                                + "s.endTime as endTime,s.isLink as isLink,s.linkUrl as linkUrl"
                                + " from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.columnId in ("
                                + idsStr + ") and b.siteId= ? and b.isPublish = 1");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.reviewInfo.toString());
        values.add(context.getSiteId());
        hql.append(" order by b.num desc");
        List<SurveyThemeVO> list = (List<SurveyThemeVO>) surveyThemeDao.getBeansByHql(hql.toString(), values.toArray(), SurveyThemeVO.class, num);
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (SurveyThemeVO stVOs : list) {
                String startEndTime =
                        timeStr.replace(GenerateConstant.START_TIME, format.format(stVOs.getStartTime())).replace(GenerateConstant.END_TIME,
                                format.format(stVOs.getEndTime()));
                stVOs.setTimeStr(startEndTime);
                stVOs.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), stVOs.getColumnId(), null) + "?cid="
                        + stVOs.getContentId());
            }
        }
        return list;
    }
}