package cn.lonsun.staticcenter.generate.tag.impl.keywordsheat;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.heatAnalysis.entity.KeyWordsHeatEO;
import cn.lonsun.heatAnalysis.service.IKeyWordsHeatService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索词排行标签
 * Created by zhushouyong on 2016-8-15.
 */
@Component
public class KeyWordsHeatRankBeanService extends AbstractBeanService {


    @Autowired
    private IKeyWordsHeatService keyWordsHeatService;


    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Map<String,Object> result = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        if(AppUtil.isEmpty(siteId)){
            siteId = Long.parseLong(context.getParamMap().get("siteId"));
        }
        Integer topCount = null == AppUtil.getInteger(paramObj.get("topCount")) ? 10 : AppUtil.getInteger(paramObj.get("topCount"));//显示条数，默认10条
        Integer sort = null == AppUtil.getInteger(paramObj.get("sort")) ? 0 : AppUtil.getInteger(paramObj.get("sort"));//排序(0:降序 1:升序),默认降序
        List<KeyWordsHeatEO> list = keyWordsHeatService.getKeyWordsHeatList(siteId,topCount,sort);
        if(null != list && list.size() > 0){
            result.put("list",list);
        }
        return result;
    }
}
