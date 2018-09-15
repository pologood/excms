package cn.lonsun.system.role.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;
import cn.lonsun.system.role.internal.entity.RbacUserInfoOpenRightsEO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-9-18 16:25
 */
public interface IUserInfoOpenRightsDao extends IMockDao<RbacUserInfoOpenRightsEO> {

    /**
     * 删除
     * @param userId
     * @param organIds
     */
    void delete(Long userId, String organIds);

    /**
     * 获取用户信息公开权限
     * @param userId
     * @param siteId
     * @return
     */
    List<RbacInfoOpenRightsEO> getInfoOpenRights(Long userId,Long siteId);
}
