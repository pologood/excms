package cn.lonsun.monitor.task.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.monitor.task.internal.entity.MonitorIndexManageEO;

/**
 * @author gu.fei
 * @version 2017-09-28 9:22
 */
public interface IMonitorIndexManageDao extends IMockDao<MonitorIndexManageEO> {


    /**
     * 根据站点ID获取指标
     * @param siteId
     * @return
     */
    MonitorIndexManageEO getIndex(Long siteId);
}
