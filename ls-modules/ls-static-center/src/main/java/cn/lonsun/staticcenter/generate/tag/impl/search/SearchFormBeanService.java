package cn.lonsun.staticcenter.generate.tag.impl.search;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-3-14 16:19
 */
@Component
public class SearchFormBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, String> map = context.getParamMap();
        if(null != map) {
            if(!AppUtil.isEmpty(map.get("activeId"))) {
                paramObj.put("activeId",map.get("activeId"));
            }
        }
        return paramObj;
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {

        Context context = ContextHolder.getContext();// 上下文
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        map.put("global_siteId", context.getSiteId());
        map.put("global_columnId", context.getColumnId());
        map.put("global_contentId", context.getContentId());

        return map;
    }

}
