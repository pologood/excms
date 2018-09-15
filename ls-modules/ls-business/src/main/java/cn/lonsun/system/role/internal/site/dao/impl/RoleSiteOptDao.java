package cn.lonsun.system.role.internal.site.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.role.internal.site.dao.IRoleSiteOptDao;
import cn.lonsun.system.role.internal.site.entity.CmsRoleSiteOptEO;

/**
 * @author gu.fei
 * @version 2015-10-31 13:56
 */
@Repository
public class RoleSiteOptDao extends BaseDao<CmsRoleSiteOptEO> implements IRoleSiteOptDao {

    private final static String DEL_SQL_BY_ROLE_ID = "DELETE FROM CMS_ROLE_SITE_OPT WHERE ROLE_ID = ?";

    @Override
    public List<CmsRoleSiteOptEO> getRoleOpt(Long roleId) {
        String hql = "from CmsRoleSiteOptEO t where  t.roleId=?";
        return this.getEntitiesByHql(hql, new Object[]{roleId});
    }

    @Override
    public List<CmsRoleSiteOptEO> getRoleSiteOpt(Long roleId) {
        String hql = "from CmsRoleSiteOptEO t where  t.roleId=? and t.columnId is null";
        return this.getEntitiesByHql(hql, new Object[]{roleId});
    }

    @Override
    public List<CmsRoleSiteOptEO> getRoleOpt(Long roleId, Long siteId) {
        String hql = "from CmsRoleSiteOptEO t where  t.roleId=? and t.siteId = ?";
        return this.getEntitiesByHql(hql, new Object[]{roleId,siteId});
    }

    @Override
    public void delByRoleId(Long roleId) {
        this.executeUpdateBySql(DEL_SQL_BY_ROLE_ID,new Object[]{roleId});
    }

    @Override
    public Long[] getSiteIdsByRoleIds(Long[] roleIds) {
        String ids = null;
        for(Long roleId : roleIds) {
            if(ids == null) {
                ids = String.valueOf(roleId);
            } else {
                ids += "," + String.valueOf(roleId);
            }
        }

        String sql = "SELECT DISTINCT SITE_ID FROM CMS_ROLE_SITE_OPT WHERE SITE_ID IS NOT NULL AND ROLE_ID in(" + ids + ")";
        List list = this.getCurrentSession().createSQLQuery(sql).list();

        Long[] siteIds = new Long[list.size()];
        int count = 0;
        for(Object siteId : list) {
            siteIds[count++] = Long.parseLong(siteId.toString());
        }

        return siteIds;
    }
}
