package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavItemByUnitBeanService extends AbstractBeanService {

    @Autowired
    private IWorkGuideService workGuideService;

    @Override
    public Object getObject(JSONObject paramObj) {
        //标签参数
        int pageSize = paramObj.getInteger("pageSize");
        //标签参数
        String where = paramObj.getString("where");
        Long unitId = paramObj.getLong("unitId");
        Long siteId = paramObj.getLong("siteId");

        ParamDto dto = new ParamDto();
        IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
        if(null != siteEO) {
            paramObj.put("url",siteEO.getUri());
        }
        dto.setPageIndex(0L);
        dto.setPageSize(pageSize);
        dto.setSiteId(siteId);
        Pagination page = workGuideService.getPageEOs(dto,unitId,null,where,null);
        return page;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        return new HashMap<String, Object>();
    }
}
