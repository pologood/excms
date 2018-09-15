package cn.lonsun.monitor.config.internal.dao.impl;


import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.config.internal.dao.IMonitoredVetoConfigDao;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2017-9-25.
 */
@Repository("monitoredVetoConfigDao")
public class MonitoredVetoConfigDaoImpl extends MockDao<MonitoredVetoConfigEO> implements IMonitoredVetoConfigDao {
    @Override
    /**
     * 根据类别查询
     * @param typeCode
     * @return
     */
    public List<MonitoredVetoConfigEO>  getDataByCode(String typeCode,Long siteId) {
        StringBuffer hql =new StringBuffer();
        hql.append("from MonitoredVetoConfigEO m where m.recordStatus =? and m.codeType=? and siteId=? ");

        return getEntitiesByHql(hql.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),typeCode,siteId});
    }



    /**
     * 获取单项否决-栏目不更新
     * @param pageQueryVO
     * @param typeCode
     * @return
     */
    @Override
    public Pagination getColumnUpdatePage(PageQueryVO pageQueryVO, String typeCode,Long siteId) {
        StringBuffer hql =new StringBuffer();
        hql.append("from MonitoredVetoConfigEO m where m.recordStatus =? and m.codeType=? and siteId=? ");
        return getPagination(pageQueryVO.getPageIndex(),pageQueryVO.getPageSize(),hql.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),typeCode,siteId});
    }
    /**
     * 根据栏目类别和否决类别编码查询栏目不更新配置
     * @param columnTypeCode
     * @param typeCode
     * @return
     */
    @Override
    public List<MonitoredVetoConfigEO> getColumnUpdateDataByCode(String columnTypeCode, String typeCode,Long siteId) {
        StringBuffer hql =new StringBuffer();
        hql.append("from MonitoredVetoConfigEO m where m.recordStatus =? and m.codeType=? and m.columnTypeCode=? and siteId=? ");

        return getEntitiesByHql(hql.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),typeCode,columnTypeCode,siteId});
    }

    /**
     * @param typeCode(类别编码)
     * @param columnTypeCode(栏目类别编码)
     * @param baseCode( vote:单项否决,scop:综合评分)
     * @param siteId
     * @return
     */
    @Override
    public List<MonitoredVetoConfigEO> getMonitorConfig(String typeCode, String columnTypeCode, String baseCode,Long siteId) {
        StringBuffer hql =new StringBuffer();
        hql.append("from MonitoredVetoConfigEO m where m.recordStatus =? and m.codeType=? and siteId=?");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(typeCode);
        param.add(siteId);
        if(!AppUtil.isEmpty(columnTypeCode)){
            hql.append(" and m.columnTypeCode=?");
            param.add(columnTypeCode);
        }
        if(!AppUtil.isEmpty(baseCode)){
            hql.append(" and m.baseCode=?");
            param.add(baseCode);
        }

        return getEntitiesByHql(hql.toString(),param.toArray());
    }
}
