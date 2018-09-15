package cn.lonsun.system.role.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;

/**
 * @author gu.fei
 * @version 2015-10-30 10:46
 */
public interface IMenuUserHideDao extends IMockDao<RbacMenuUserHideEO> {

    /**
     * 物理删除
     * @param menuId
     */
    public void phyDelete(Long menuId);
}
