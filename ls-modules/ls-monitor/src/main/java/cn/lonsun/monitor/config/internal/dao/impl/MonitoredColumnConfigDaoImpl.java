package cn.lonsun.monitor.config.internal.dao.impl;


import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.config.internal.dao.IMonitoredColumnConfigDao;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**栏目类别配置查询
 * Created by lonsun on 2017-9-22.
 */
@Repository
public class MonitoredColumnConfigDaoImpl extends MockDao<MonitoredColumnConfigEO> implements IMonitoredColumnConfigDao {
    /**
     * 分页查询列表
     * @param pageQueryVO
     * @return
     */
    @Override
    public Pagination getPage(PageQueryVO pageQueryVO) {
       StringBuffer hql =new StringBuffer();
       hql.append("from MonitoredColumnConfigEO m where m.recordStatus =? and m.siteId =?");
        return getPagination(pageQueryVO.getPageIndex(), pageQueryVO.getPageSize(), hql.toString(), new Object[]{BaseContentEO.RecordStatus.Normal.toString(), LoginPersonUtil.getSiteId()});
    }
    /**
     * 获取未删除项
     * @param ids
     * @return
     */
    @Override
    public List<MonitoredColumnConfigEO> getNoramlConfig(Long[] ids) {

        return null;
    }
    /**
     * 获取栏目更新栏目
     * @return
     */
    @Override
    public List<MonitoredColumnConfigEO> getUpateColumn() {
        StringBuffer hql =new StringBuffer();
        hql.append("from MonitoredColumnConfigEO m where m.recordStatus =? and m.siteId =?  and m.typeCode in(?,?,?,?) ");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(LoginPersonUtil.getSiteId());
        param.add(MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString());
        param.add(MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString());
        param.add(MonitoredColumnConfigEO.TypeCode.columnType_update.toString());
        param.add(MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString());
        return getEntitiesByHql(hql.toString(),param.toArray());
    }

    @Override
    public List<MonitoredColumnConfigEO> getColumnByType(List<String> infoUpdateTypeList) {
        StringBuffer hql =new StringBuffer();
        List<Object> param =new ArrayList<Object>();

        hql.append("from MonitoredColumnConfigEO m where m.recordStatus =? and m.siteId =? and m.typeCode in(");
        for(int i=0;i<infoUpdateTypeList.size();i++){
            if(i<infoUpdateTypeList.size()-1){
                hql.append("?,");
            }else {
                hql.append("?");
            }
        }
        hql.append(")");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(LoginPersonUtil.getSiteId());
        for(String type :  infoUpdateTypeList){
            param.add(type);
        }
        return getEntitiesByHql(hql.toString(),param.toArray());
    }

    @Override
    public List<MonitoredColumnConfigEO> getSiteColumnType(Long siteId) {
        StringBuffer hql =new StringBuffer();
        hql.append("from MonitoredColumnConfigEO m where m.recordStatus =? and m.siteId =?");
        return getEntitiesByHql(hql.toString(),new Object[]{BaseContentEO.RecordStatus.Normal.toString(), siteId});

    }

    /**
     *
     * @param columnConfigEO
     * @return
     */
    @Override
    public MonitoredColumnConfigEO queryConfigByCode(MonitoredColumnConfigEO columnConfigEO) {
        StringBuffer hql =new StringBuffer();
        hql.append("from MonitoredColumnConfigEO m where m.recordStatus =? and m.siteId =? and m.typeCode=?");
        List<MonitoredColumnConfigEO> monitoredColumnConfigEOs = getEntitiesByHql(hql.toString(),new Object[]{BaseContentEO.RecordStatus.Normal.toString(),columnConfigEO.getSiteId(),columnConfigEO.getTypeCode()});
        if(null==monitoredColumnConfigEOs||monitoredColumnConfigEOs.size()<1){
            return new MonitoredColumnConfigEO();
        }
        return monitoredColumnConfigEOs.get(0);
    }

    @Override
    public MonitoredColumnConfigEO queryConfigByTypeId(Long typeId) {
        StringBuffer hql =new StringBuffer();
        hql.append("from MonitoredColumnConfigEO m where m.recordStatus =? and m.siteId =? and m.typeId=?");
        List<MonitoredColumnConfigEO> monitoredColumnConfigEOs = getEntitiesByHql(hql.toString(),new Object[]{BaseContentEO.RecordStatus.Normal.toString(),LoginPersonUtil.getSiteId(),typeId});
        if(null==monitoredColumnConfigEOs||monitoredColumnConfigEOs.size()<1){
            return new MonitoredColumnConfigEO();
        }
        return monitoredColumnConfigEOs.get(0);    }
}
