package cn.lonsun.system.role.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IMenuUserHideSpecialService extends IMockService<RbacMenuUserHideEO> {

    /**
     * 物理删除
     * @param menuId
     */
    public void phyDelete(Long menuId);
}
