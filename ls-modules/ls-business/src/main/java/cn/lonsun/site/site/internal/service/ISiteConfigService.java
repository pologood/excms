package cn.lonsun.site.site.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.vo.SiteVO;

import java.util.List;

/**
 * 站点配置业务接口<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
public interface ISiteConfigService extends IMockService<SiteConfigEO> {
    //检查站点名称是否存在
    public boolean checkSiteNameExist(String siteName, Long parentId, Long indicatorId);


    //删除站点
    public void deleteEO(Long indicatorId);

    //保存站点
    public SiteConfigEO saveEO(SiteMgrEO siteVO);

    //更新站点
    public SiteConfigEO updateEO(SiteMgrEO siteVO);

    //获取排序号
    public Integer getNewSortNum(Long parentId, boolean isSub);

    //获取站点树（异步）
    public List<SiteVO> getSiteTree(Long indicatorId);

    //获取所有站点树
    public List<IndicatorEO> getAllSites();

    //保存虚拟子站
    public SiteConfigEO saveSubEO(SiteMgrEO siteVO);

    //根据公共栏目获取绑定的站点
    public List<SiteMgrEO> getByComColumnId(Long comColumnId);

    //根据站点ID 获取实体类
    public SiteMgrEO getById(Long indicatorId);

    Pagination getSiteInfos(Long pageIndex, Integer pageSize);
}
