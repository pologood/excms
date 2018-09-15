package cn.lonsun.system.role.internal.site.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.role.internal.site.entity.CmsRoleSiteOptEO;

/**
 * @author gu.fei
 * @version 2015-10-31 13:56
 */
public interface IRoleSiteOptDao extends IBaseDao<CmsRoleSiteOptEO> {

    public List<CmsRoleSiteOptEO> getRoleOpt(Long roleId);

    public List<CmsRoleSiteOptEO> getRoleSiteOpt(Long roleId);

    public List<CmsRoleSiteOptEO> getRoleOpt(Long roleId,Long siteId);

    public  void delByRoleId(Long roleId);

    public Long[] getSiteIdsByRoleIds(Long[] roleIds);

}
