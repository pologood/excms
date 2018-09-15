package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.XSSFilterUtil;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavQjSceneItemsBeanService extends AbstractBeanService {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IWorkGuideService workGuideService;

    @Override
    public Object getObject(JSONObject paramObj) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("unempty", true);
        Context context = ContextHolder.getContext();
        //来自传参的ID
        Long ckcolumnId = context.getColumnId();

        //来自url参数
        Map<String, String> pmap = context.getParamMap();

        Long columnId = null;
        if (pmap != null && pmap.size() > 0) {
            if (!AppUtil.isEmpty(pmap.get("columnId"))) {
                columnId = Long.valueOf(pmap.get("columnId"));
            }
        }

        map.put("columnId", null == columnId ? "" : columnId);
        map.put("ckcolumnId", ckcolumnId);

        String name = null;
        if (pmap != null && pmap.size() > 0) {
            if (!AppUtil.isEmpty(pmap.get("keywords"))) {
                name = pmap.get("keywords");
                name = XSSFilterUtil.filterSqlInject(name);
            }
        }

        //分页条数
        Integer pageSize = paramObj.getInteger("pageSize");
        //页码
        Long pageIndex = context.getPageIndex();

        String columnIds = paramObj.getString("columnIds");
        List<ColumnMgrEO> eos = columnConfigService.getLevelColumnTree(null == columnId ? ckcolumnId : columnId, new int[]{2}, null, null);

        if (null == eos || eos.isEmpty() || null != columnIds) {
            if(null != columnIds && !"".equals(columnIds)) {
                List<Long> list = StringUtils.getListWithLong(columnIds,",");
                for(Long id:list) {
                    List<ColumnMgrEO> tempeos = columnConfigService.getLevelColumnTree(id, new int[]{1}, null, null);
                    if(null == eos) {
                        eos = tempeos;
                    } else {
                        if(null != tempeos) {
                            eos.addAll(tempeos);
                        }
                    }
                }
            } else {
                eos = columnConfigService.getLevelColumnTree(null == columnId ? ckcolumnId : columnId, new int[]{1}, null, null);
            }

            ParamDto dto = new ParamDto();
            dto.setPageSize(pageSize);
            dto.setPageIndex(pageIndex - 1);

            dto.setKeys("name");
            dto.setKeyValue(name);

            List<Long> cIds = new ArrayList<Long>();
            for (ColumnMgrEO eo : eos) {
                cIds.add(eo.getIndicatorId());
                dto.setSiteId(eo.getSiteId());
            }
            if (!cIds.isEmpty()) {
                Pagination guides = workGuideService.getPageEOsByCIds(dto, cIds);
                guides.getPageCount();
                map.put("page", guides);
                map.put("leaf", true);
            }
        } else {
            map.put("eos", eos);
        }
        if (null == eos || eos.isEmpty()) {
            map.put("unempty", false);
        }
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

        Context context = ContextHolder.getContext();
        //来自url参数
        Map<String, String> cmap = context.getParamMap();
        String name = null;
        if (cmap != null && cmap.size() > 0) {
            if (!AppUtil.isEmpty(cmap.get("keywords"))) {
                name = cmap.get("keywords");
                name = XSSFilterUtil.filterSqlInject(name);
            }
        }
        // 数据预处理
        String path = PathUtil.getLinkPath(context.getColumnId(), null);
        map.put("linkPrefix",path);
        String target = paramObj.getString("target");
        map.put("target",target);
        map.put("tableColumnId",paramObj.getLong("tableColumnId"));
        map.put("relateColumnId",paramObj.getLong("relateColumnId"));
        map.put("keywords", XSSFilterUtil.stripXSS(name));

        return map;
    }
}
