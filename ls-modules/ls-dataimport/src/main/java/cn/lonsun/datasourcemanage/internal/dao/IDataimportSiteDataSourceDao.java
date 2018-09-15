package cn.lonsun.datasourcemanage.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.entity.DataimportSiteDataSourceEO;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;

import java.util.List;

/**
 * Created by lonsun on 2018-2-5.
 *
 */
public interface IDataimportSiteDataSourceDao extends IMockDao<DataimportSiteDataSourceEO> {
    Pagination getClomunTypePage(DataSourceQueryVo queryVo);

    List<DataimportSiteDataSourceEO> getSiteDataSource(Long siteId, String type, Long dataSourceId);
}
