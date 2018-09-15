package cn.lonsun.system.role.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.system.role.internal.dao.IInfoOpenRightsDao;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;

/**
 * @author gu.fei
 * @version 2015-10-30 10:47
 */
@Repository
public class InfoOpenRightsDao extends MockDao<RbacInfoOpenRightsEO> implements IInfoOpenRightsDao {

    @Override
    public List<RbacInfoOpenRightsEO> getEOsByRoleIds(Long[] roleIds) {
        String ids = null;
        for(Long id : roleIds) {
            if(ids == null) {
                ids = id + "";
            } else {
                ids += "," + id;
            }
        }
        return this.getEntitiesByHql("from RbacInfoOpenRightsEO where roleId in(" + ids + ")",new Object[]{});
    }

    @Override
    public void delByRoleIdAndOrganId(Long roleId, String organIds) {
        this.executeUpdateBySql("DELETE FROM RBAC_INFO_OPEN_RIGHTS WHERE ROLE_ID = ? AND ORGAN_ID in (" + organIds + ")" ,new Object[]{roleId});
    }
}
