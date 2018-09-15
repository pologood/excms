package cn.lonsun.staticcenter.generate.tag.impl.collectInfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.content.ideacollect.internal.dao.ICollectInfoDao;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.ideacollect.vo.CollectInfoWebVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import cn.lonsun.util.TimeOutUtil;

import com.alibaba.fastjson.JSONObject;

@Component
public class CollectInfoDetailBeanService extends AbstractBeanService {

    @Autowired
    private ICollectInfoDao collectInfoDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long contentId = context.getContentId();// 根据文章id查询文章
        // 需要显示条数.
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer();
        hql.append("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,");
        hql.append("b.num as sortNum,b.isPublish as isIssued,b.publishDate as issuedTime,b.imageLink as picUrl,");
        hql.append("s.collectInfoId as collectInfoId,s.startTime as startTime,s.endTime as endTime,s.content as content,s.desc as desc,s.ideaCount as ideaCount,s.result as result");
        hql.append(" from BaseContentEO b,CollectInfoEO s where b.id = s.contentId and b.isPublish = 1 and b.recordStatus= ? and b.typeCode = ? and b.id = ?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.collectInfo.toString());
        values.add(contentId);
        Object obj = collectInfoDao.getBean(hql.toString(), values.toArray(), CollectInfoVO.class);
        CollectInfoWebVO infoWeb = null;
        if (obj != null) {
            infoWeb = new CollectInfoWebVO();
            BeanUtils.copyProperties(obj, infoWeb);
            // isTimeOut = 1 //未开始 3;//已过期 2;//进行中
            if (infoWeb.getStartTime() != null && infoWeb.getEndTime() != null) {
                infoWeb.setIsTimeOut(TimeOutUtil.getTimeOut(infoWeb.getStartTime(), infoWeb.getEndTime()));
            }
        }
        return infoWeb;
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        return RegexUtil.parseProperty(content, resultObj);
    }
}