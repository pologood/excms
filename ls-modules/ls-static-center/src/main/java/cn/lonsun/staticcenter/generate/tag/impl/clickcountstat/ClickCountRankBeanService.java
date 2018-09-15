package cn.lonsun.staticcenter.generate.tag.impl.clickcountstat;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.statistics.internal.service.IClickCountStatService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 点击排行标签
 * Created by zhushouyong on 2016-8-15.
 */
@Component
public class ClickCountRankBeanService extends AbstractBeanService {


    @Autowired
    private IClickCountStatService clickCountStatService;

    @Autowired
    private IBaseContentService baseContentService;


    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Map<String,Object> result = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        if(AppUtil.isEmpty(siteId)){
            siteId = Long.parseLong(context.getParamMap().get("siteId"));
        }
        String type = AppUtil.isEmpty(paramObj.get("type")) ? "0" : paramObj.get("type").toString();//排行统计类型(0:按天 1:按周 2:按月 4:全部)，默认按天
        Integer topCount = null == AppUtil.getInteger(paramObj.get("topCount")) ? 10 : AppUtil.getInteger(paramObj.get("topCount"));//显示条数，默认10条
        Integer sort = null == AppUtil.getInteger(paramObj.get("sort")) ? 0 : AppUtil.getInteger(paramObj.get("sort"));//排序(0:降序 1:升序),默认降序
        Long contentIds[] = null;
        Date date = new Date();
        if("0".equals(type)){
            contentIds = clickCountStatService.getContentIdsByDay(siteId,topCount,date,sort);
        }else if("1".equals(type)){
            contentIds = clickCountStatService.getContentIdsByWeek(siteId,topCount,date,sort);
        }else if("2".equals(type)){
            contentIds = clickCountStatService.getContentIdsByMonth(siteId,topCount,date,sort);
        }else if("4".equals(type)){
            contentIds = clickCountStatService.getContentIdsByDate(siteId,topCount,null,sort);
        }
        if(null != contentIds){
            Map<String,Object> params = new HashMap<String, Object>();
            params.put("id",contentIds);
            params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<BaseContentEO> list =  baseContentService.getEntities(BaseContentEO.class,params);
            if(null != list && list.size() > 0){
                List<BaseContentEO> contents = new ArrayList<BaseContentEO>(list.size());
                Map<Long,BaseContentEO> contentMap = (Map<Long,BaseContentEO> )AppUtil.parseListToMap(list,"id");
                BaseContentEO eo = null;
                for(Long contentId : contentIds){
                    if(null != (eo = contentMap.get(contentId))){
                        try{
                            eo.setLink(PathUtil.getLinkPath(eo.getColumnId(),eo.getId()));
                            contents.add(eo);
                        }catch (GenerateException e){}
                    }
                }
                result.put("contents",contents);
            }
        }
        return result;
    }
}
