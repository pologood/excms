package cn.lonsun.system.role.internal.site.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.role.internal.site.dao.IUserSiteOptDao;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;

/**
 * @author gu.fei
 * @version 2015-10-31 13:56
 */
@Repository
public class UserSiteOptDao extends BaseDao<CmsUserSiteOptEO> implements IUserSiteOptDao {

    @Override
    public List<CmsUserSiteOptEO> getOpts(Long organId, Long userId) {
        String hql = "from CmsUserSiteOptEO t where  t.organId=? and t.userId=?";
        return this.getEntitiesByHql(hql, new Object[]{organId,userId});
    }

    @Override
    public CmsUserSiteOptEO getOpts(Long organId, Long userId, Long siteId) {
        String hql = "from CmsUserSiteOptEO t where  t.organId=? and t.userId=? and t.siteId=?";
        return this.getEntityByHql(hql, new Object[]{organId,userId,siteId});
    }

    @Override
    public void deleteByUser(Long organId, Long userId) {
        this.executeUpdateBySql("DELETE FROM CMS_USER_SITE_OPT WHERE ORGAN_ID = ? AND USER_ID = ?", new Object[]{organId,userId});
    }

    @Override
    public void deleteById(Long siteId, Long columnId) {
        this.executeUpdateBySql("DELETE FROM CMS_USER_SITE_OPT WHERE SITE_ID = ? AND COLUMN_ID = ?", new Object[]{siteId,columnId});
    }

    @Override
    public List<CmsUserSiteOptEO> getRightsBySiteId(Long siteId) {
        String hql = "from CmsUserSiteOptEO t where t.siteId=?";
        return this.getEntitiesByHql(hql, new Object[]{siteId});
    }
}
