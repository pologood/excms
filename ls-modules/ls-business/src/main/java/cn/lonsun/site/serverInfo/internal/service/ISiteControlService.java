package cn.lonsun.site.serverInfo.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.site.serverInfo.internal.entity.SiteControlEO;

/**
 * @author gu.fei
 * @version 2017-08-01 9:14
 */
public interface ISiteControlService extends IMockService<SiteControlEO> {

    /**
     * 更新配置信息
     * @param siteIds
     * @param status
     * @return
     */
    void updateSiteControl(Long[] siteIds,Integer status);
}
