package cn.lonsun.system.role.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;

/**
 * @author gu.fei
 * @version 2015-10-30 10:37
 */
public interface IMenuAsgService extends IMockService<IndicatorPermissionEO> {

    /**
     * 保存菜单权限及操作权限
     * @param roleId
     * @param menuRights
     * @param optRights
     * @return
     */
    public Long save(Long roleId, String menuRights,String optRights);

}
