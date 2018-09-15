package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.rbac.internal.service.IIndicatorPermissionService;
import cn.lonsun.rbac.internal.service.IPermissionService;
import cn.lonsun.system.role.internal.cache.MenuRightsCache;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;
import cn.lonsun.system.role.internal.service.IMenuAsgService;
import cn.lonsun.system.role.internal.service.IMenuRightsService;
import cn.lonsun.util.LoginPersonUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-10-30 10:37
 */
@Service
public class MenuAsgService extends MockService<IndicatorPermissionEO> implements IMenuAsgService {

    @Resource
    private IIndicatorPermissionService indicatorPermissionService;
    @Resource
    private IPermissionService permissionService;

    @Autowired
    private IMenuRightsService menuRightsService;

    @Override
    public Long save(Long roleId, String menuRights, String optRights) {
        indicatorPermissionService.updateRoleAndIndicator(roleId, menuRights);
        permissionService.updatePermission(roleId, menuRights);

        JSONArray json = JSONArray.fromObject(optRights);
        List<RbacMenuRightsEO> menuRightsEOs = (List<RbacMenuRightsEO>) JSONArray.toCollection(json, RbacMenuRightsEO.class);
        if (LoginPersonUtil.isSuperAdmin()) {
            for (RbacMenuRightsEO eo : menuRightsEOs) {
                eo.setSiteId(-1L);
            }
            menuRightsService.delByRoleId(roleId);
        } else if (LoginPersonUtil.isSiteAdmin()) {
            menuRightsService.delByRoleIdAndSiteId(roleId, LoginPersonUtil.getSiteId());
        }
        menuRightsService.saveEntities(menuRightsEOs);
        CacheHandler.reload(RbacMenuRightsEO.class.getName());
        MenuRightsCache.refresh();
        return roleId;
    }
}
