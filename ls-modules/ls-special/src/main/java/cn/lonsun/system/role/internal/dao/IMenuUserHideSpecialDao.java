package cn.lonsun.system.role.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IMenuUserHideSpecialDao extends IMockDao<RbacMenuUserHideEO> {
    /**
     * 物理删除
     * @param menuId
     */
    public void phyDelete(Long menuId);
}
