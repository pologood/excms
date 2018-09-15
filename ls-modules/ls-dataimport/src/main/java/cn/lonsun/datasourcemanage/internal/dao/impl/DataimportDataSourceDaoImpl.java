package cn.lonsun.datasourcemanage.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.dao.IDataimportDataSourceDao;
import cn.lonsun.datasourcemanage.internal.entity.DataimportDataSourceEO;
import cn.lonsun.datasourcemanage.internal.vo.DataSourcePageVo;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2018-2-5.
 */
@Repository
public class DataimportDataSourceDaoImpl extends MockDao<DataimportDataSourceEO> implements IDataimportDataSourceDao {
    /**
     * 数据源列表
     * @param dataSourceQueryVo
     *
     * @return
     */
    @Override
    public Pagination getPage(DataSourceQueryVo dataSourceQueryVo) {
        StringBuffer stringBuffer =new StringBuffer();
        List<Object> param =new ArrayList<Object>();
        stringBuffer.append("select d.ID as dataSourceId,d.name as name,d.DATABASE_TYPE as databaseType,d.DATABASE_NAME as databaseName,d.DATABASE_URI as databaseUri,d.USERNAME as username,d.PASSWD as passwd,s.name as manufacturerName from DATAIMPORT_DATA_SOURCE d left join DATAIMPORT_MANUFACTURER s on d.MANUFACTURERID=s.id  where  d.RECORD_STATUS =? ");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(!AppUtil.isEmpty(dataSourceQueryVo.getManufacturerid())){
            stringBuffer.append(" and d.MANUFACTURERID=?");
            param.add(dataSourceQueryVo.getManufacturerid());
        }
        return getPaginationBySql(dataSourceQueryVo.getPageIndex(),dataSourceQueryVo.getPageSize(),stringBuffer.toString(),param.toArray(),DataSourcePageVo.class);
    }

    /**
     * 数据源列表
     * @return
     */
    @Override
    public List<DataimportDataSourceEO> getList() {
        StringBuffer stringBuffer =new StringBuffer();
        List<Object> param =new ArrayList<Object>();

        stringBuffer.append("from DataimportDataSourceEO d where d.recordStatus=? ");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(stringBuffer.toString(),param.toArray());
    }
}
