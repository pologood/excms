package cn.lonsun.datasourcemanage.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.entity.DataimportDataSourceEO;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;
import cn.lonsun.internal.metadata.DataModule;

import java.util.List;

/**
 * Created by lonsun on 2018-2-5.
 *
 */
public interface IDataimportDataSourceService extends IMockService<DataimportDataSourceEO> {
    Pagination getPage(DataSourceQueryVo dataSourceQueryVo);

    void saveOrUpdateDataSource(DataimportDataSourceEO dataimportDataSourceEO);

    /**
     * 根据站点和栏目类型获取数据源
     * @param siteId
     * @param typeCode
     * @return
     */
    public DataimportDataSourceEO getDataSourceBySite(Long siteId, DataModule typeCode);

    void delete(Long[] ids);
    List<DataimportDataSourceEO> getList();

}
