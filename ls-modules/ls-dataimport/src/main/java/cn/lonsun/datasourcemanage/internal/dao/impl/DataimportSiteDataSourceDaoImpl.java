package cn.lonsun.datasourcemanage.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.dao.IDataimportSiteDataSourceDao;
import cn.lonsun.datasourcemanage.internal.entity.DataimportSiteDataSourceEO;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;
import cn.lonsun.datasourcemanage.internal.vo.SiteDataSourceVo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2018-2-5.
 */
@Repository
public class DataimportSiteDataSourceDaoImpl extends MockDao<DataimportSiteDataSourceEO> implements IDataimportSiteDataSourceDao {
    /**
     * 站点栏目类别查询
     * @param queryVo
     *
     * @return
     */
    @Override
    public Pagination getClomunTypePage(DataSourceQueryVo queryVo) {
        StringBuffer stringBuffer =new StringBuffer();
        List<Object> param =new ArrayList<Object>();

        stringBuffer.append("select d.ID as id,d.TYPE_CODE as typeCode,d.TYPE_CODE_NAME as typeCodeName,s.DATABASE_NAME as databaseName, s.DATABASE_URI as databaseUri,s.USERNAME as username,s.PASSWD as passwd,s.DATABASE_TYPE as databaseType  from DATAIMPORT_SITE_DATA_SOURCE d left join DATAIMPORT_DATA_SOURCE s on d.DATA_SOURCE_ID=s.ID where d.RECORD_STATUS =? ");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(!AppUtil.isEmpty(queryVo.getSiteId())){
            stringBuffer.append("d.SITE_ID =?");
            param.add(queryVo.getSiteId());
        }
        return getPaginationBySql(queryVo.getPageIndex(),queryVo.getPageSize(),stringBuffer.toString(),param.toArray(),SiteDataSourceVo.class);
    }

    @Override
    public List<DataimportSiteDataSourceEO> getSiteDataSource(Long siteId, String type, Long dataSourceId) {
        StringBuffer stringBuffer =new StringBuffer();
        List<Object> param =new ArrayList<Object>();
        stringBuffer.append("from DataimportSiteDataSourceEO d where d.recordStatus=?");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(!AppUtil.isEmpty(siteId)){
            stringBuffer.append(" and d.siteId=?");
            param.add(siteId);
        }

        if(!AppUtil.isEmpty(type)){
            stringBuffer.append(" and d.typeCode=?");
            param.add(type);
        }
        if(!AppUtil.isEmpty(dataSourceId)){
            stringBuffer.append(" and d.dataSourceId=?");
            param.add(dataSourceId);
        }
        return getEntitiesByHql(stringBuffer.toString(),param.toArray());
    }
}
