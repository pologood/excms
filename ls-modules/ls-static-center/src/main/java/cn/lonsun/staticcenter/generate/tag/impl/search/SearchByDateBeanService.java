package cn.lonsun.staticcenter.generate.tag.impl.search;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-3-14 16:19
 */
@Component
public class SearchByDateBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        IndicatorEO eo = new IndicatorEO();
        return eo;
    }

    /**
     * 预处理结果
     *
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        //来自url参数
        Map<String, String> cmap = context.getParamMap();
        Map<String, Object> map = new HashMap<String, Object>();
        //关键词
        if (!AppUtil.isEmpty(cmap) && !cmap.isEmpty()) {
            map.put("keywords", cmap.get("keywords"));
            map.put("sort", cmap.get("sort"));
            map.put("datecode", cmap.get("datecode"));
            map.put("columnId", cmap.get("columnId"));
            map.put("columnIds", cmap.get("columnIds"));
            map.put("typeCode", cmap.get("typeCode"));
            map.put("fromCode", cmap.get("fromCode"));
            map.put("beginDate", cmap.get("beginDate"));
            map.put("endDate", cmap.get("endDate"));
            //政务论坛类型
            map.put("type", cmap.get("type"));
            map.put("tableColumnId", cmap.get("tableColumnId"));
            map.put("excColumns", cmap.get("excColumns"));
            //信息公开公文号
            map.put("fileNum", cmap.get("fileNum"));
            //信息公开索引号
            map.put("indexNum", cmap.get("indexNum"));
            //搜索类型，区分是专题（子站）搜索
            map.put("searchType", cmap.get("searchType"));
            //专题（子站）搜索模板ID
            map.put("searchTplId", cmap.get("searchTplId"));
        }

        Long siteId = context.getSiteId();
        map.put("siteId", siteId);
        return map;
    }
}
