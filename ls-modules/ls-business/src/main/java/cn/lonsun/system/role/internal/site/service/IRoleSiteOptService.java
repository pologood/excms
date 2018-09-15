package cn.lonsun.system.role.internal.site.service;

import java.util.List;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.system.role.internal.site.entity.CmsRoleSiteOptEO;
import cn.lonsun.system.role.internal.site.entity.vo.ColumnOpt;
import cn.lonsun.system.role.internal.site.entity.vo.SiteOpt;

/**
 * @author gu.fei
 * @version 2015-10-31 13:58
 */
public interface IRoleSiteOptService extends IBaseService<CmsRoleSiteOptEO> {

    /**
     * 根据角色查询站点权限
     * @param roleId
     * @return
     */
    public Object getSiteOpt(Long roleId);

    /**
     * 根据角色删除站点权限
     * @param roleId
     */
    public  void delByRoleId(Long roleId);

    /**
     * 获取当前用户拥有的站点
     * @return
     */
    public Long[] getCurUserSiteIds();

    /**
     * 获取当前用户拥有的站点操作权限
     * @return
     */
    public List<SiteOpt> getCurUserSiteOpt();

    /**
     * 根据站点获取栏目权限
     * @param siteId
     * @return
     */
    public List<ColumnOpt> getColumnOptBySite(Long siteId);

    /**
     * 保存权限
     * @param strJson
     * @param roleId
     */
    public void saveOpt(String strJson,Long roleId);


    /**
     * 获取当前单位下面拥有指定栏目权限的用户
     * @param unitId
     * @param columnId
     * @return
     */
    public List<TreeNodeVO> getUserAuthForColumn(Long unitId,Long columnId);

}
