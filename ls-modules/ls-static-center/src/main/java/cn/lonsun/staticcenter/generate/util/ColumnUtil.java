package cn.lonsun.staticcenter.generate.util;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.db.DBConfig;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.IColumnConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.util.ColumnRelUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-21<br/>
 */

public class ColumnUtil {

    private static IColumnConfigDao configDao = SpringContextHolder.getBean(DBConfig.getDaoNameByCurDBType("columnConfig"));

    public static List<ColumnMgrEO> getColumnByPId(Long columnId, Long siteId) {
        List<ColumnMgrEO> list = null;
        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
        if (indicatorEO == null) {
            return null;
        }
        if (IndicatorEO.Type.CMS_Site.toString().equals(indicatorEO.getType())) {
            return CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, columnId);
        }
        //获取子栏目
        list = configDao.getColumnByParentId(columnId, true, siteId);
        List<ColumnConfigRelEO> relList = ColumnRelUtil.getBySiteId(siteId);
        Map<Long, ColumnConfigRelEO> relMap = new HashMap<Long, ColumnConfigRelEO>();
        if (null != relList && !relList.isEmpty()) {
            for (ColumnConfigRelEO organRel : relList) {
                if (organRel.getIndicatorId() != null) {
                    relMap.put(organRel.getIndicatorId(), organRel);
                }
            }
            if (null != list && !list.isEmpty()) {
                for (Iterator<ColumnMgrEO> it = list.iterator(); it.hasNext(); ) {
                    ColumnMgrEO eo = it.next();
                    // 删除不显示的目录
                    if (relMap.containsKey(eo.getIndicatorId())) {
                        if (relMap.get(eo.getIndicatorId()).getIsHide()) {
                            it.remove();
                        } else {
                            ColumnConfigRelEO relEO = relMap.get(eo.getIndicatorId());
                            eo.setIndicatorId(relEO.getIndicatorId());
                            eo.setName(relEO.getName());
                            eo.setSortNum(relEO.getSortNum());
                            eo.setIsParent(relEO.getIsParent());
                            eo.setColumnTypeCode(relEO.getColumnTypeCode());
                            eo.setContentModelCode(relEO.getContentModelCode());
                            eo.setIsShow(relEO.getIsShow());
                            eo.setTransUrl(relEO.getTransUrl());
                            eo.setTransWindow(relEO.getTransWindow());
                            eo.setSiteId(siteId);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static ColumnMgrEO getByIndicatorId(Long columnId, Long siteId) {
        SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        if (siteMgrEO == null) {
            return null;
        }
        ColumnMgrEO eo = getEntity(ColumnMgrEO.class, columnId);
        if (IndicatorEO.Type.SUB_Site.toString().equals(siteMgrEO.getType())) {
            ColumnConfigRelEO relEO = ColumnRelUtil.getByIndicatorId(columnId, siteId);
            if (relEO != null) {
                eo.setIndicatorId(relEO.getIndicatorId());
                eo.setIsParent(relEO.getIsParent());
                eo.setName(relEO.getName());
                eo.setSortNum(relEO.getSortNum());
                eo.setColumnTypeCode(relEO.getColumnTypeCode());
                eo.setContentModelCode(relEO.getContentModelCode());
                eo.setIsShow(relEO.getIsShow());
                eo.setTransUrl(relEO.getTransUrl());
                eo.setTransWindow(relEO.getTransWindow());
                eo.setSiteId(siteId);
                if (BaseContentEO.TypeCode.articleNews.toString().equals(eo.getColumnTypeCode())) {
                    eo.setGenePageIds(relEO.getGenePageIds());
                    eo.setGenePageNames(relEO.getGenePageNames());
                    eo.setSynColumnIds(relEO.getSynColumnIds());
                    eo.setSynColumnNames(relEO.getSynColumnNames());
                } else if (BaseContentEO.TypeCode.linksMgr.toString().equals(eo.getColumnTypeCode())) {
                    eo.setIsLogo(relEO.getIsLogo());
                    eo.setHeight(relEO.getHeight());
                    eo.setWidth(relEO.getWidth());
                    eo.setNum(relEO.getNum());
                }
            }
            if (columnId.equals(siteMgrEO.getComColumnId())) {
                eo.setParentId(siteId);
            }

        }
        return eo;
    }

}
