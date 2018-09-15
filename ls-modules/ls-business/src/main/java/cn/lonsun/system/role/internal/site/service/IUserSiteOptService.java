package cn.lonsun.system.role.internal.site.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;

/**
 * @author gu.fei
 * @version 2015-10-31 13:58
 */
public interface IUserSiteOptService extends IBaseService<CmsUserSiteOptEO> {

    /*
    * 获取当前用户拥有的站点权限
    * */
    public List<CmsUserSiteOptEO> getOpts(Long organId,Long userId);

    /*
    * 查询站点权限
    * */
    public CmsUserSiteOptEO getOpts(Long organId,Long userId,Long siteId);

    /*
    * 获取当前用户拥有站点权限
    * */
    public List<IndicatorEO> getUserOpts(Long organId,Long userId);

    /*
    * 根据用户删除
    * */
    public void deleteByUser(Long organId,Long userId);

    /*
    * 根据站点/栏目删除
    * */
    public void deleteById(Long siteId,Long columnId);

    /**
     * 保存用户站点权限
     * @param strJson
     * @param organId
     * @param userId
     */
    public void saveUserOpt(String strJson,Long organId,Long userId);

    /**
     * 查询指定站点下绑定的用户
     * @param siteId
     * @return
     */
    public List<CmsUserSiteOptEO> getRightsBySiteId(Long siteId);

}
