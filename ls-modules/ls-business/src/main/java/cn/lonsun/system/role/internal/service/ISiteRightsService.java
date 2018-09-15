package cn.lonsun.system.role.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.system.role.internal.entity.RbacSiteRightsEO;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
public interface ISiteRightsService extends IBaseService<RbacSiteRightsEO> {

    /**
     * @param roleId
     */
    public void delByRoleId(Long roleId);

    /**
     * 获取角色站点权限-新的
     * @param roleId
     */
    public Object getRoleRights(Long roleId);

    /**
     * 获取角色站点权限-老的方法
     * @param roleId
     * @return
     */
    public Object getSiteRights(Long roleId);

    /**
     * 保存角色权限
     * @param rights
     * @param roleId
     */
    public void saveRights(String rights,Long roleId);

    /**
     * 获取当前用户拥有的站点权限
     * @param siteVOs
     * @return
     */
    public List<SiteMgrEO> getCurUserSiteOpt(List<SiteMgrEO> siteVOs);

    /**
     * 获取当前用户拥有的站点权限
     * @return
     */
    public Long[] getCurUserSiteIds();

    /**
     * 获取当前用户拥有的站点权限
     * @return
     */
    public Long[] getCurUserSiteIds(boolean subshow);

    /**
     * 根据站点ID查询当前用户在此站点下拥有的栏目权限
     * @param siteId
     * @return
     */
    public List<Long> getCurUserHasColumnIds(Long siteId);

    /**
     * 获取栏目操作权限
     * @param columnVOs
     * @param columnVOs
     * @return
     */
    public List<ColumnMgrEO> getCurUserColumnOpt(List<ColumnMgrEO> columnVOs);

    /**
     * 根据栏目ID获取当前栏目操作权限
     * @param columnId
     * @return
     */
    public List<FunctionEO> getOptByColumnId(Long columnId);

}
