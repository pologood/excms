package cn.lonsun.system.role.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;

/**
 * @author gu.fei
 * @version 2015-10-30 10:58
 */
public interface IMenuUserHideService extends IMockService<RbacMenuUserHideEO>  {

    /**
     * 物理删除
     * @param menuId
     */
    public void phyDelete(Long menuId);
}
