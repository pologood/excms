package cn.lonsun.staticcenter.generate.tag.impl.clickcountstat;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.statistics.internal.service.IClickCountStatService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.statistics.WordListVO;
import cn.lonsun.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 栏目点击排行标签
 * Created by liuk on 2017-01-09.
 */
@Component
public class ColumnClickCountRankBeanService extends AbstractBeanService {


    @Autowired
    private IClickCountStatService clickCountStatService;

    @Autowired
    private IIndicatorService indicatorService;


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
        List<Object> columnIds = new ArrayList<Object>();
        Date date = new Date();
        if("0".equals(type)){
            date = DateUtil.getDayDate(date);
            columnIds = clickCountStatService.getColumnIdsByDate(siteId,topCount,date,sort);
        }else if("1".equals(type)){
            date = DateUtil.getDayDate(date,-7);
            columnIds = clickCountStatService.getColumnIdsByDate(siteId,topCount,date,sort);
        }else if("2".equals(type)){
            date = DateUtil.getDayDate(date,-30);
            columnIds = clickCountStatService.getColumnIdsByDate(siteId,topCount,date,sort);
        }else if("4".equals(type)){
            columnIds = clickCountStatService.getColumnIdsByDate(siteId,topCount,null,sort);
        }
        if(null != columnIds){

//            for(int i=0;i<columnIds.size();i++){
//                IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class,columnIds.get(i).getColumnId());
//                if(eo!=null){
//                    columnIds.get(i).setColumnName(eo.getName());
//                }
//            }
            result.put("columns",columnIds);
        }
        return result;
    }
}
