package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.XSSFilterUtil;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.generate.util.UrlUtil;
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
public class OnlineNavItemBeanService extends AbstractBeanService {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IWorkGuideService workGuideService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        //标签参数
        int num = paramObj.getInteger("num");
        //标签参数
        String where = paramObj.getString("where");

        Long LBcolumnId = paramObj.getLong("id");

        //来自传参
        Long columnId = context.getColumnId();
        //来自传参
        Long pageIndex = context.getPageIndex();
        //来自url参数
        Map<String, String> map = context.getParamMap();

        Long organId = null;
        if (map != null && map.size() > 0) {
            if (!AppUtil.isEmpty(map.get("organId"))) {
                organId = Long.valueOf(map.get("organId"));
            }
        }

        boolean organShow = false;
        if (map != null && map.size() > 0) {
            if (!AppUtil.isEmpty(map.get("organShow"))) {
                organShow = Boolean.parseBoolean(map.get("organShow"));
            }
        }

        String name = null;
        if (map != null && map.size() > 0) {
            if (!AppUtil.isEmpty(map.get("SearchWords"))) {
                name = map.get("SearchWords");
                name = XSSFilterUtil.filterSqlInject(name);
            }
        }

        Pagination page = null;
        ParamDto dto = new ParamDto();
        dto.setPageIndex(pageIndex - 1);
        dto.setPageSize(num);
        dto.setSiteId(context.getSiteId());
        List<Long> cIds = new ArrayList<Long>();
        if (LBcolumnId != null) {
            List<ColumnMgrEO> list = columnConfigService.getAllColumnBySite(LBcolumnId);
            for (ColumnMgrEO eo : list) {
                cIds.add(eo.getIndicatorId());
                dto.setSiteId(eo.getSiteId());
            }
            page = workGuideService.getPageEOs(dto, cIds, where);
        } else if (AppUtil.isEmpty(organId) && !organShow) {
            List<ColumnMgrEO> list = columnConfigService.getChildColumn(columnId, new int[]{0});

            for (ColumnMgrEO eo : list) {
                cIds.add(eo.getIndicatorId());
                dto.setSiteId(eo.getSiteId());
            }

            dto.setKeys("name");
            dto.setKeyValue(name);

            page = workGuideService.getPageEOs(dto, cIds, where);
        } else {
            page = workGuideService.getPageEOs(dto, organId, name, where, null);
        }

        List<CmsWorkGuideEO> list = (List<CmsWorkGuideEO>) page.getData();
        if (null != list) {
            for (CmsWorkGuideEO eo : list) {
                if (null != eo.getSbLink()) {
                    String sblink = UrlUtil.addParam(eo.getSbLink(), "tableIds=" + (AppUtil.isEmpty(eo.getTableIds()) ? "" : eo.getTableIds()));
                    sblink = UrlUtil.addParam(sblink, "organId=" + (AppUtil.isEmpty(eo.getOrganId()) ? "" : eo.getOrganId()));
                    eo.setSbLink(sblink);
                }
                String zxlink = UrlUtil.addParam(eo.getZxLink(), "organId=" + (AppUtil.isEmpty(eo.getOrganId()) ? "" : eo.getOrganId()));
                eo.setZxLink(zxlink);

                String tslink = UrlUtil.addParam(eo.getTsLink(), "organId=" + (AppUtil.isEmpty(eo.getOrganId()) ? "" : eo.getOrganId()));
                eo.setTsLink(tslink);
            }
        }

        return page;
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

        String name = null;
        if (cmap != null && cmap.size() > 0) {
            if (!AppUtil.isEmpty(cmap.get("SearchWords"))) {
                name = cmap.get("SearchWords");
            }
        }

        Long organId = null;
        if (cmap != null && cmap.size() > 0) {
            if (!AppUtil.isEmpty(cmap.get("organId"))) {
                organId = Long.valueOf(cmap.get("organId"));
            }
        }

        boolean organShow = false;
        if (cmap != null && cmap.size() > 0) {
            if (!AppUtil.isEmpty(cmap.get("organShow"))) {
                organShow = Boolean.parseBoolean(cmap.get("organShow"));
            }
        }

        // 数据预处理
        Map<String, Object> map = new HashMap<String, Object>();
        String path = PathUtil.getLinkPath(context.getColumnId(), null);
        map.put("linkPrefix", "");
        String target = paramObj.getString("target");
        map.put("target", target);
        //来自传参的ID
        Long columnId = context.getColumnId();
        map.put("columnId", columnId);
        map.put("tableColumnId", paramObj.getLong("tableColumnId"));
        map.put("relateColumnId", paramObj.getLong("relateColumnId"));
        map.put("SearchWords", XSSFilterUtil.stripXSS(name));
        map.put("organId", organId);
        map.put("organShow", organShow);

        return map;
    }
}
