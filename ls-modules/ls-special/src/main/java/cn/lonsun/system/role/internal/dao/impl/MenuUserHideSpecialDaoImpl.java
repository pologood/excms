package cn.lonsun.system.role.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.system.role.internal.dao.IMenuUserHideSpecialDao;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/9/26.
 */
@Repository
public class MenuUserHideSpecialDaoImpl extends MockDao<RbacMenuUserHideEO> implements IMenuUserHideSpecialDao  {
    @Override
    public void phyDelete(Long menuId) {
        this.executeUpdateByHql("delete from RbacMenuUserHideEO where menuId = ?",new Object[]{menuId});
    }
}
