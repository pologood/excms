package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.monitor.task.internal.dao.IMonitorIndexManageDao;
import cn.lonsun.monitor.task.internal.entity.MonitorIndexManageEO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository
public class MonitorIndexManageDaoImpl extends MockDao<MonitorIndexManageEO> implements IMonitorIndexManageDao {

    @Override
    public MonitorIndexManageEO getIndex(Long siteId) {
        return this.getEntityByHql("from MonitorIndexManageEO where siteId=? and recordStatus=?",new Object[]{siteId, AMockEntity.RecordStatus.Normal.toString()});
    }
}
