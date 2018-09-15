package cn.lonsun.system.role.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.role.internal.entity.RbacSiteRightsEO;
import cn.lonsun.system.role.internal.entity.vo.IdsVO;
import cn.lonsun.system.role.internal.entity.vo.SiteRightsVO;

/**
 * @author gu.fei
 * @version 2015-9-18 16:25
 */
public interface ISiteRightsDao extends IBaseDao<RbacSiteRightsEO> {

    public List<SiteRightsVO> getEOsByRoleIds(Long[] roleIds);

    public List<SiteRightsVO> getEOsByRoleIds(Long[] roleIds,String type);

    public List<IdsVO> getSiteIdsByRoleIds(Long[] roleIds,String type);

    public void delByRoleId(Long roleId);

}
