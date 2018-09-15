package cn.lonsun.staticcenter.generate.tag.impl.interview;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.interview.internal.dao.IInterviewInfoDao;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class InterviewInfoListBeanService extends AbstractBeanService {

    @Autowired
    private IInterviewInfoDao interviewInfoDao;

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        List<Long> ids = new ArrayList<Long>();
        String id = paramObj.getString(GenerateConstant.ID);
        Integer itype = paramObj.getInteger(GenerateConstant.ITYPE);
        String itypeParams = context.getParamMap().get(GenerateConstant.ITYPE);
        //是否状态排序
        Boolean isTypeSort = true;
        try {
            isTypeSort = paramObj.getBoolean("isTypeSort");
        }catch (Exception e){}

        if (!StringUtils.isEmpty(itypeParams)) {
            try {
                itype = Integer.parseInt(itypeParams);
            } catch (NumberFormatException e) {
            }
        }

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
        StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                + "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
                + "s.interviewId as interviewId,s.presenter as presenter,s.userNames as userNames,s.time as time,s.liveLink as liveLink,"
                + "s.outLink as outLink,s.handleOrgan as handleOrgan,s.content as content,s.desc as desc,s.isOpen as isOpen,s.openTime as openTime,s.startTime as startTime,s.endTime as endTime,s.address as address,s.organizer as organizer"
                + " from BaseContentEO b,InterviewInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.siteId= ? and b.isPublish = 1 and b.columnId in ("+idsStr+")");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.interviewInfo.toString());
        values.add(siteId);
        if (itype != null && itype != 0) {
            hql.append(" and s.type = ?");
            values.add(itype);
        }
        hql.append(" order by");
        if(isTypeSort){hql.append(" s.type asc,");}
        hql.append(" b.num desc");
        List<InterviewInfoVO> list = (List<InterviewInfoVO>) interviewInfoDao.getBeansByHql(hql.toString(), values.toArray(), InterviewInfoVO.class, num);
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (InterviewInfoVO iiVo : list) {
                if(!StringUtils.isEmpty(iiVo.getOutLink())){
                    //转链
                    iiVo.setLinkUrl(iiVo.getOutLink());
                    iiVo.setIsLink(1);
                }else {
                    iiVo.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), iiVo.getColumnId(), iiVo.getContentId()));
                }
                iiVo.setUri(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), iiVo.getColumnId(),null));
            }
        }
        return list;
    }
}