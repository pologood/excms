package cn.lonsun.site.site.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface ISiteConfigSpecialService extends IMockService<SiteConfigEO> {

    //保存站点
    public SiteConfigEO saveEO(SiteMgrEO siteVO);

    //更新站点
    public SiteConfigEO updateEO(SiteMgrEO siteVO);

}
