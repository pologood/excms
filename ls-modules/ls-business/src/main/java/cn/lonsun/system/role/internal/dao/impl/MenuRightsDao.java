package cn.lonsun.system.role.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.role.internal.dao.IMenuRightsDao;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;

/**
 * @author gu.fei
 * @version 2015-10-30 10:47
 */
@Repository
public class MenuRightsDao extends BaseDao<RbacMenuRightsEO> implements IMenuRightsDao {

    @Override
    public List<RbacMenuRightsEO> getEOsByRoleIds(Long[] roleIds) {
        /*StringBuffer sb = new StringBuffer("SELECT MENU_ID,OPT_CODE FROM RBAC_MENU_RIGHTS WHERE ROLE_ID IN (");
        int count = 0;
        for(Long roleId : roleIds) {
            if(count == 0) {
                sb.append(roleId);
            } else {
                sb.append("," + roleId);
            }
            count++;
        }
        sb.append(")");
        sb.append(" GROUP BY MENU_ID,OPT_CODE");

        return (List<RbacMenuRightsEO>) this.getBeansBySql(sb.toString(),new Object[]{},RbacMenuRightsEO.class);*/
        return null;
    }

    @Override
    public void delByRoleId(Long roleId) {
        this.executeUpdateBySql("DELETE FROM RBAC_MENU_RIGHTS WHERE ROLE_ID = ? AND SITE_ID = -1" ,new Object[]{roleId});
    }

    @Override
    public void delByRoleIdAndSiteId(Long roleId, Long siteId) {
        this.executeUpdateBySql("DELETE FROM RBAC_MENU_RIGHTS WHERE ROLE_ID = ? AND SITE_ID = ?" ,new Object[]{roleId,siteId});
    }
}
