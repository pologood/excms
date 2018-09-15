package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
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
public class OnlineNavIndexBeanService extends AbstractBeanService {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, String> pmap = context.getParamMap();
        Long organColumnId = paramObj.getLong("organColumnId");
        Long parentColumnId = null;
        boolean organShow = false;
        if (pmap != null && pmap.size() > 0) {
            if (!AppUtil.isEmpty(pmap.get("organShow"))) {
                organShow = Boolean.parseBoolean(pmap.get("organShow"));
            }

            if (!AppUtil.isEmpty(pmap.get("parentColumnId"))) {
                parentColumnId = Long.valueOf(pmap.get("parentColumnId"));
                paramObj.put("parentColumnId", parentColumnId);
                IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, parentColumnId);
                paramObj.put("parentColumnName", eo.getName());
            }

        }

        // 显示条数
        int num = paramObj.getInteger("num");
        PageQueryVO vo = null;
        if (!AppUtil.isEmpty(num)) {
            vo = new PageQueryVO();
            vo.setPageIndex(0L);
            vo.setPageSize(num);
        }

        // 来自标签的ID
        Long columnId = paramObj.getLong(GenerateConstant.ID);

        if (columnId == null||columnId==0) {
            columnId = parentColumnId;
        }

        Long ccolumnId = context.getColumnId();

        // 查询条件
        String where = paramObj.getString("where");

        //排除不显示的栏目
        String exclude = paramObj.getString("excludeColumnIds");
        List<Long> idlist = new ArrayList<Long>();
        if (!AppUtil.isEmpty(exclude)) {
            idlist = StringUtils.getListWithLong(exclude, ",");
        }
        List<ColumnMgrEO> rstlist = new ArrayList<ColumnMgrEO>();

        List<ColumnMgrEO> list = columnConfigService.getLevelColumnTree(columnId, new int[]{2}, where, vo);

        if (null == list || list.size() <= 0) {
            IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, columnId);
            if(eo!=null){
                list = columnConfigService.getLevelColumnTree(eo.getParentId(), new int[]{2}, where, vo);
            }
        }

        if (null != list) {
            for (ColumnMgrEO mgrEO : list) {
                if (organShow) {
                    if (null != organColumnId) {
                        if (mgrEO.getIndicatorId().intValue() == organColumnId.intValue()) {
                            mgrEO.setActive("active");
                        }
                    }
                } else {
                    if (null != ccolumnId) {
                        if (mgrEO.getIndicatorId().intValue() == ccolumnId.intValue()) {
                            mgrEO.setActive("active");
                        } else {
                            IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, ccolumnId);
                            IndicatorEO parent = CacheHandler.getEntity(IndicatorEO.class, eo.getParentId());
                            if (mgrEO.getIndicatorId().intValue() == parent.getIndicatorId().intValue()) {
                                mgrEO.setActive("active");
                            }
                        }
                    }
                }

                if (!AppUtil.isEmpty(exclude)) {
                    if (!idlist.contains(mgrEO.getIndicatorId())) {
                        rstlist.add(mgrEO);
                    }
                } else {
                    rstlist.add(mgrEO);
                }
            }
        }

        for (ColumnMgrEO columnMgrEO : rstlist) {
            List<ColumnMgrEO> listMgr = columnConfigService.getLevelColumnTree(columnMgrEO.getIndicatorId(), new int[]{2}, null, vo);
            if (listMgr != null && !listMgr.isEmpty()) {
                columnMgrEO.setFirstColumnId(listMgr.get(0).getIndicatorId());
            }
        }

        return rstlist;
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
        Map<String, Object> map = new HashMap<String, Object>();

        List<ColumnMgrEO> rstlist = (List<ColumnMgrEO>) resultObj;
        if (null != rstlist) {
            for (ColumnMgrEO columnMgrEO : rstlist) {
                String path = PathUtil.getLinkPath(columnMgrEO.getIndicatorId(), null);
                columnMgrEO.setUri(path);
            }
        }

        return map;
    }
}