package cn.lonsun.system.role.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.role.internal.dao.IUserMenuRightsDao;
import cn.lonsun.system.role.internal.entity.RbacUserMenuRightsEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2015-10-30 10:47
 */
@Repository
public class UserMenuRightsDao extends BaseDao<RbacUserMenuRightsEO> implements IUserMenuRightsDao {

    @Override
    public void deleteByUserId(Long userId) {
        this.executeUpdateByHql("delete from RbacUserMenuRightsEO where userId=?  and siteId=?",new Object[]{userId, LoginPersonUtil.getSiteId()});
    }
}
