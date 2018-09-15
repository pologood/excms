package cn.lonsun.system.role.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.system.role.internal.entity.RbacUserSiteRightsEO;

import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
public interface IUserSiteRightsService extends IBaseService<RbacUserSiteRightsEO> {

    /**
     * 获取栏目权限
     * @param userId
     * @return
     */
    List<ColumnMgrEO> getSiteRights(Long userId);

    /**
     * 获取拥有的栏目权限
     * @param userId
     * @return
     */
    List<ColumnMgrEO> getCheckSiteRights(Long userId);

    /**
     * 获取指定站点下拥有的栏目权限
     * @param userId
     * @param siteId
     * @return
     */
    List<ColumnMgrEO> getCheckSiteRights(Long userId,Long siteId);

    /**
     * 保存栏目权限
     * @param userId
     * @param rights
     */
    void saveSiteRights(Long userId,String rights);

    /**
     * 获取
     * @param userId
     * @param siteId
     * @return
     */
    Map<String, Boolean> getCheckedRightsMap(Long userId, Long siteId);
}
