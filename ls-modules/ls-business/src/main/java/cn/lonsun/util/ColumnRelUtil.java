package cn.lonsun.util;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.service.IColumnConfigRelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 栏目中间表信息<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-20<br/>
 */

public class ColumnRelUtil {
    private static IColumnConfigRelService relService = SpringContextHolder.getBean(IColumnConfigRelService.class);

    public static List<ColumnConfigRelEO> getBySiteId(Long siteId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", siteId);
        List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
        return relList;
    }

    public static ColumnConfigRelEO getByIndicatorId(Long indicatorId, Long siteId) {
        ColumnConfigRelEO relEO = null;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", siteId);
        map.put("isHide", Boolean.FALSE.booleanValue());
        map.put("indicatorId", indicatorId);
        List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
        if (relList != null && relList.size() > 0) {
            relEO = relList.get(0);
        }
        return relEO;
    }
}
