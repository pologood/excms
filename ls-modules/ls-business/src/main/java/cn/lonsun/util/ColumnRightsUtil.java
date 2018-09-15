package cn.lonsun.util;

import java.util.ArrayList;
import java.util.List;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.internal.service.impl.ColumnConfigServiceImpl;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.role.internal.service.impl.SiteRightsService;

/**
 * @author gu.fei
 * @version 2016-5-18 13:56
 */
public class ColumnRightsUtil {

    private static final IColumnConfigService columnConfigService = SpringContextHolder.getBean(ColumnConfigServiceImpl.class);

    private static final ISiteRightsService siteRightsService = SpringContextHolder.getBean(SiteRightsService.class);

    public static List<Long> getRCurHasColumns() {
        if(LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot()) {
            return null;
        }
        List<Long> ids = new ArrayList<Long>();
        Long siteId = LoginPersonUtil.getSiteId();
        if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
        } else {
            List<ColumnMgrEO> columns = columnConfigService.getTree(siteId);
            List<ColumnMgrEO> myColumns = siteRightsService.getCurUserColumnOpt(columns);
            for (ColumnMgrEO myColumn : myColumns) {
                List<FunctionEO> functions = myColumn.getFunctions();
                for (FunctionEO function : functions) {
                    if ("publish".equals(function.getAction())) {
                        ids.add(myColumn.getIndicatorId());
                    }
                }
            }
        }

        return ids;
    }
}
