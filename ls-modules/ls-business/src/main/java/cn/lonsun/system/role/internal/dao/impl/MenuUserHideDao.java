package cn.lonsun.system.role.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.system.role.internal.dao.IMenuUserHideDao;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2015-10-30 10:47
 */
@Repository
public class MenuUserHideDao extends MockDao<RbacMenuUserHideEO> implements IMenuUserHideDao {

    @Override
    public void phyDelete(Long menuId) {
        this.executeUpdateByHql("delete from RbacMenuUserHideEO where menuId = ?",new Object[]{menuId});
    }
}
