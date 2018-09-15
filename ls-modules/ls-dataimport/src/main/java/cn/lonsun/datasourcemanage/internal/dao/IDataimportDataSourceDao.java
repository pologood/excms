package cn.lonsun.datasourcemanage.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.entity.DataimportDataSourceEO;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;

import java.util.List;

/**
 * Created by lonsun on 2018-2-5.
 *
 */
public interface IDataimportDataSourceDao extends IMockDao<DataimportDataSourceEO> {
    Pagination getPage(DataSourceQueryVo dataSourceQueryVo);

    List<DataimportDataSourceEO> getList();
}
