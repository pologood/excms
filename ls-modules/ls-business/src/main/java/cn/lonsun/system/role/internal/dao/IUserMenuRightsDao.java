package cn.lonsun.system.role.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.role.internal.entity.RbacUserMenuRightsEO;

/**
 * @author gu.fei
 * @version 2015-9-18 16:25
 */
public interface IUserMenuRightsDao extends IBaseDao<RbacUserMenuRightsEO> {

    /**
     * 根据用户ID删除
     * @param userId
     */
    void deleteByUserId(Long userId);
}
