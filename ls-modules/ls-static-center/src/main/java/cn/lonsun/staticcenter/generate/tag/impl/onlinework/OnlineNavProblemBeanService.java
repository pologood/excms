package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.net.service.service.ICommonProblemService;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.util.ModelConfigUtil;
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
public class OnlineNavProblemBeanService extends AbstractBeanService {

    @Autowired
    private ICommonProblemService commonProblemService;

    @Override
    public Object getObject(JSONObject paramObj) {
        int num = paramObj.getInteger("num");
        Map<String,Object> map = new HashMap<String, Object>();
        Long columnId = paramObj.getLong("columnId");
        ParamDto dto = new ParamDto();
        dto.setPageSize(num);
        dto.setColumnId(columnId);
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class,columnId);
        if(null != eo) {
            ModelTemplateEO template = ModelConfigUtil.getTemplateByColumnId(columnId, eo.getSiteId());
            if(null != template) {
                map.put("tplId",template.getArticalTempId());
            }
        }
        Pagination page = commonProblemService.getPageEntities(dto);
        map.put("page",page);
        map.put("columnId",columnId);
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
