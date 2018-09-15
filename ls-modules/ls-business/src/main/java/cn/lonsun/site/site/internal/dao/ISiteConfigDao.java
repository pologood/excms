package cn.lonsun.site.site.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.vo.SiteVO;

import java.util.List;


/**
 * 站点配置DAO层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
public interface ISiteConfigDao extends IMockDao<SiteConfigEO> {
    public Integer getNewSortNum(Long parentId, boolean isSub);

    public SiteConfigEO getSiteConfigByIndicatorId(Long indicatorId);

    public List<SiteVO> getSiteTree(Long indicatorId);

    public List<SiteMgrEO> getByComColumnId(Long comColumnId);

    public SiteMgrEO getById(Long indicatorId);

    Pagination getSiteInfos(Long pageIndex, Integer pageSize);
}
