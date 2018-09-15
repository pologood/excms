package cn.lonsun.system.role.internal.site.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;

/**
 * @author gu.fei
 * @version 2015-10-31 13:56
 */
@Repository
public interface IUserSiteOptDao extends IBaseDao<CmsUserSiteOptEO> {

    public List<CmsUserSiteOptEO> getOpts(Long organId, Long userId);

    public CmsUserSiteOptEO getOpts(Long organId, Long userId, Long siteId);

    public void deleteByUser(Long organId,Long userId);

    public void deleteById(Long siteId, Long columnId);

    public List<CmsUserSiteOptEO> getRightsBySiteId(Long siteId);
}
