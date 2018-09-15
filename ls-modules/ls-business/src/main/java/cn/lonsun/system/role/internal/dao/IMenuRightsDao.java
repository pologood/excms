package cn.lonsun.system.role.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;

/**
 * @author gu.fei
 * @version 2015-9-18 16:25
 */
public interface IMenuRightsDao extends IBaseDao<RbacMenuRightsEO> {

    public List<RbacMenuRightsEO> getEOsByRoleIds(Long[] roleIds);

    public void delByRoleId(Long roleId);

    public void delByRoleIdAndSiteId(Long roleId, Long siteId);
}
