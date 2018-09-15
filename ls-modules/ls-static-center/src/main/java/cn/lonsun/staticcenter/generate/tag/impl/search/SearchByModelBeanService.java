package cn.lonsun.staticcenter.generate.tag.impl.search;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.solr.SolrQueryHolder;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.search.util.SearchUtil;
import cn.lonsun.staticcenter.generate.tag.impl.search.vo.ModelVO;
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
public class SearchByModelBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        //栏目ids，多个栏目
        String str = paramObj.getString("models");
        //关键词
        List<String> models = StringUtils.getListWithString(str, ",");

        if (null == models) {
            return "";
        }

        List<ModelVO> modelVOs = new ArrayList<ModelVO>();
        IndicatorEO othereo = new IndicatorEO();
        othereo.setName("其他");
        //来自url参数
        Map<String, String> map = context.getParamMap();
        SolrPageQueryVO vo = SearchUtil.setPageVal(map);

        String typeCode = map.get("typeCode");
        String scope = map.get("scope");

        //查询类型
        if (!SearchUtil.isNull(map.get("keywords"))) {
            vo.setKeywords(map.get("keywords"));
        }

        vo.setSiteId(siteId);
        vo.setExcColumns(null);
        Long tempcolumnId = vo.getColumnId();
        vo.setColumnId(null);
        for (String model : models) {
            ModelVO modelVO = new ModelVO();

            /*if(model.equals(BaseContentEO.TypeCode.workGuide.toString())) {
                modelVO.setName("网上办事");
            } else if(model.equals(BaseContentEO.TypeCode.public_content.toString())) {
                modelVO.setName("信息公开");
            } else if(model.equals(BaseContentEO.TypeCode.guestBook.toString())) {
                modelVO.setName("政务论坛");
            } else if("news".equals(model)) {
                modelVO.setName("资讯中心");
            }*/


            if ("news".equals(model)) {
                modelVO.setTypeCode("articleNews,pictureNews,videoNews");
                vo.setTypeCode("articleNews,pictureNews,videoNews");
            } else {
                modelVO.setTypeCode(model);
                vo.setTypeCode(model);
            }

            if (null != typeCode) {
                if ("news".equals(model)) {
                    if (typeCode.contains("articleNews")
                        || typeCode.contains("pictureNews")
                        || typeCode.equals("videoNews")) {
                        modelVO.setActive("active");
                    }
                } else if (typeCode.equals(model)) {
                    modelVO.setActive("active");
                }
            }

            if (model.equals("workGuide")) {
                vo.setColumnId(tempcolumnId);
            }

            //设置关键字
            try {
                Long total = SolrQueryHolder.queryCount(vo);
                if (null != total) {
                    modelVO.setTotal(total);
                }
                //设置栏目数量
            } catch (Exception e) {
                e.printStackTrace();
            }


            modelVOs.add(modelVO);
        }

        return modelVOs;
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

        return map;
    }
}
