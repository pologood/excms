package cn.lonsun.staticcenter.generate.tag.impl.search;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.solr.SolrQueryHolder;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.search.util.SearchUtil;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-3-14 16:19
 */
@Component
public class SearchByTypeBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        //栏目ids，多个栏目
        String columnIds = paramObj.getString("columnIds");
        //关键词
        Long[] ids = StringUtils.getArrayWithLong(columnIds, ",");

        if (null == ids || ids.length <= 0) {
            return "";
        }

        List<IndicatorEO> indicatorEOs = new ArrayList<IndicatorEO>();
        Long otherCount = 0L;
        IndicatorEO othereo = new IndicatorEO();
        othereo.setName("其他");
        othereo.setExcColumns(columnIds);
        //来自url参数
        Map<String, String> map = context.getParamMap();
        SolrPageQueryVO vo = SearchUtil.setPageVal(map);

        //查询类型
        if (!SearchUtil.isNull(map.get("keywords"))) {
            vo.setKeywords(map.get("keywords"));
        }

        vo.setSiteId(siteId);
        vo.setColumnId(null);
        vo.setExcColumns(null);
        try {
            otherCount = SolrQueryHolder.queryAllCount(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Long columnId : ids) {
            IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, columnId);
            //设置关键字
            try {
                vo.setColumnId(columnId);
                Long total = SolrQueryHolder.queryCount(vo);
                otherCount = otherCount - total;
                //设置栏目数量
                eo.setTotal(total);
            } catch (Exception e) {
                e.printStackTrace();
            }
            indicatorEOs.add(eo);
        }

        othereo.setTotal(otherCount);
        indicatorEOs.add(othereo);
        return indicatorEOs;
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
            map.put("excColumns", cmap.get("excColumns"));
            map.put("typeCode", cmap.get("typeCode"));
            map.put("fromCode", cmap.get("fromCode"));
            map.put("beginDate", cmap.get("beginDate"));
            map.put("endDate", cmap.get("endDate"));
            //搜索类型，区分是专题（子站）搜索
            map.put("searchType", cmap.get("searchType"));
            //专题（子站）搜索模板ID
            map.put("searchTplId", cmap.get("searchTplId"));
        }

        // 数据预处理
        Long siteId = context.getSiteId();
        map.put("siteId", siteId);

        String typeCode = cmap.get("typeCode");
        boolean show = true;
        if (!AppUtil.isEmpty(typeCode) && (typeCode.contains(SolrPageQueryVO.TypeCode.workGuide.toString()) ||
            typeCode.contains(SolrPageQueryVO.TypeCode.guestBook.toString()) ||
            typeCode.contains(SolrPageQueryVO.TypeCode.public_content.toString()))) {
            show = false;
        }
        if (!AppUtil.isEmpty(cmap.get("columnIds"))) {
            show = false;
        }
        map.put("show", show);
        return map;
    }
}
