package cn.lonsun.staticcenter.generate.tag.impl.collectInfo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.ideacollect.internal.dao.ICollectInfoDao;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
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

@Component
public class CollectInfoListBeanService extends AbstractBeanService {

    @Autowired
    private ICollectInfoDao collectInfoDao;

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        List<Long> ids = new ArrayList<Long>();
        String id = paramObj.getString(GenerateConstant.ID);
        if (!StringUtils.isEmpty(id)) {
            ids.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(id.split(","), Long.class))));
        } else {
            Long columnId = context.getColumnId();
            ids.add(columnId);
        }
        String siteIdStr = paramObj.getString("siteId");
        System.out.print("站点字符串===="+siteIdStr);
        Long siteId=0L;
        if(!AppUtil.isEmpty(siteIdStr)){
            siteId=Long.valueOf(siteIdStr);
        }

        System.out.print("站点ID===="+siteId);
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        System.out.print("站点的ID===="+siteId);
        String timeStr = paramObj.getString(GenerateConstant.TIME_STR);
        String dateFormat = paramObj.getString(GenerateConstant.DATE_FORMAT);
        SimpleDateFormat format = StringUtils.isEmpty(dateFormat) ? new SimpleDateFormat("yyyy-MM-dd") : new SimpleDateFormat(dateFormat);


        String idsStr = StringUtils.join(ids.toArray(), ",");
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql =
                new StringBuffer(
                        "select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                                + "b.num as sortNum,b.createDate as createDate,b.isPublish as isIssued,b.publishDate as issuedTime,b.imageLink as picUrl,"
                                + "s.collectInfoId as collectInfoId,s.startTime as startTime,s.endTime as endTime,s.content as content,s.desc as desc,s.ideaCount as ideaCount,s.linkUrl as linkUrl"
                                + " from BaseContentEO b,CollectInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.siteId= ? and b.isPublish = 1 and b.columnId in ("
                                + idsStr + ")");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.collectInfo.toString());
        values.add(siteId);
        hql.append(" order by b.num desc,s.createDate desc");
        List<CollectInfoVO> list = (List<CollectInfoVO>) collectInfoDao.getBeansByHql(hql.toString(), values.toArray(), CollectInfoVO.class, num);
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (CollectInfoVO ciVO : list) {
                if (ciVO.getStartTime() != null && ciVO.getEndTime() != null) {
                    ciVO.setIsTimeOut(TimeOutUtil.getTimeOut(ciVO.getStartTime(), ciVO.getEndTime()));
                }
                String startEndTime =
                        timeStr.replace(GenerateConstant.START_TIME, format.format(ciVO.getStartTime())).replace(GenerateConstant.END_TIME,
                                format.format(ciVO.getEndTime()));
                ciVO.setTimeStr(startEndTime);
                if(StringUtils.isEmpty(ciVO.getLinkUrl())){
                    ciVO.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), ciVO.getColumnId(), ciVO.getContentId()));
                }else{
                    //转链
                    ciVO.setIsLink(1);
                }
                if(!StringUtils.isEmpty(ciVO.getContent())){
                    try{
                        ciVO.setContent(HtmlUtil.getTextFromTHML(ciVO.getContent()));
                    }catch (Exception e){}
                }
            }
        }
        return list;
    }
}