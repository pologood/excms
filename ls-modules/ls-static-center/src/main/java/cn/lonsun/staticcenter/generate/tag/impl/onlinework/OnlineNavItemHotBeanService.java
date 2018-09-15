package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangxx on 2016/8/16.
 */
@Component
public class OnlineNavItemHotBeanService extends AbstractBeanService {

    @Autowired
    private IWorkGuideService workGuideService;

    @Override
    public Object getObject(JSONObject paramObj) {

        Context context = ContextHolder.getContext();
        int num = paramObj.getInteger("num");
        Map<String, Object> map = new HashMap<String, Object>();
        ParamDto dto = new ParamDto();
        dto.setSiteId(context.getSiteId());
        dto.setSortField("b.hit");
        dto.setSortOrder("DESC");
        dto.setPageIndex(0L);
        dto.setPageSize(num);
        map.put("page",workGuideService.getPageEOs(dto,null,null,null, BaseContentEO.TypeCode.workGuide.toString()));
        return map;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Map<String, Object> map = (Map<String, Object>) resultObj;
        return map;
    }
}
