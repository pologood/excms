package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.monitor.task.internal.dao.IMonitorCustomIndexManageDao;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomIndexManageEO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository
public class MonitorCustomIndexManageDaoImpl extends MockDao<MonitorCustomIndexManageEO> implements IMonitorCustomIndexManageDao {

    @Override
    public MonitorCustomIndexManageEO getIndex(Long siteId) {
        return this.getEntityByHql("from MonitorCustomIndexManageEO where siteId=? and recordStatus=?",new Object[]{siteId, AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public void updateStatus(Long taskId, Integer status) {
        StringBuilder hql = new StringBuilder("update MonitorCustomIndexManageEO set taskStatus = ?");
        hql.append(" where taskId=?");
        this.executeUpdateByHql(hql.toString(),new Object[]{status,taskId});
    }
}
