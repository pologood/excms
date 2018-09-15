package cn.lonsun.datasourcemanage.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.entity.DataimportSiteDataSourceEO;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;

import java.util.List;

/**
 * Created by lonsun on 2018-2-5.
 *
 */
public interface IDataimportSiteDataSourceService extends IMockService<DataimportSiteDataSourceEO> {
    Pagination getSitePage(DataSourceQueryVo queryVo);

    Pagination getClomunTypePage(DataSourceQueryVo queryVo);
    void bindDataSource(Long siteId, Long id, Long dataSourceId,String typeCode);

    List<DataimportSiteDataSourceEO> getSiteDataSource(Long siteId,String type,Long dataSourceId);
}
