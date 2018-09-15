package cn.lonsun.staticcenter.generate.tag.impl.collectInfo;

import cn.lonsun.content.ideacollect.internal.dao.ICollectInfoDao;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
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

@Component
public class CollectInfoPageListBeanService extends AbstractBeanService {

    @Autowired
    private ICollectInfoDao collectInfoDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        String timeStr = paramObj.getString(GenerateConstant.TIME_STR);
        String dateFormat = paramObj.getString(GenerateConstant.DATE_FORMAT);
        SimpleDateFormat format = StringUtils.isEmpty(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd") : new SimpleDateFormat(dateFormat);
        // 此写法是为了使得在页面这样调用也能解析
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        // 需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql =
                new StringBuffer(
                        "select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                                + "b.num as sortNum,b.isPublish as isIssued,b.publishDate as issuedTime,b.imageLink as picUrl,"
                                + "s.collectInfoId as collectInfoId,s.startTime as startTime,s.endTime as endTime,s.content as content,s.desc as desc,s.ideaCount as ideaCount,s.linkUrl as linkUrl"
                                + " from BaseContentEO b,CollectInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.columnId = ? and b.siteId= ? and b.isPublish = 1");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.collectInfo.toString());
        values.add(columnId);
        values.add(context.getSiteId());
        hql.append(" order by b.num desc,s.createDate desc");
        Pagination pagination = (Pagination) collectInfoDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), CollectInfoVO.class);
        if (null != pagination) {
            List<?> resultList = pagination.getData();
            // 处理查询结果
            if (null != resultList && !resultList.isEmpty()) {
                for (Object o : resultList) {
                    CollectInfoVO vo = (CollectInfoVO) o;
                    if (vo.getStartTime() != null && vo.getEndTime() != null) {
                        vo.setIsTimeOut(TimeOutUtil.getTimeOut(vo.getStartTime(), vo.getEndTime()));
                    }
                    String startEndTime =
                            timeStr.replace(GenerateConstant.START_TIME, format.format(vo.getStartTime())).replace(GenerateConstant.END_TIME,
                                    format.format(vo.getEndTime()));
                    vo.setTimeStr(startEndTime);
                    if(StringUtils.isEmpty(vo.getLinkUrl())) {
                        vo.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getContentId()));
                    }else{
                        //转链
                        vo.setIsLink(1);
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