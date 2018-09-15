package cn.lonsun.system.role.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;

/**
 * @author gu.fei
 * @version 2015-9-18 16:25
 */
public interface IInfoOpenRightsDao extends IMockDao<RbacInfoOpenRightsEO> {

    public List<RbacInfoOpenRightsEO> getEOsByRoleIds(Long[] roleIds);

    public void delByRoleIdAndOrganId(Long roleId, String organIds);
}
