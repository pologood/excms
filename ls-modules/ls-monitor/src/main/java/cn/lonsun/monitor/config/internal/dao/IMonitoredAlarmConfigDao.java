package cn.lonsun.monitor.config.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.monitor.config.internal.entity.MonitoredAlarmConfigEO;

/**监测报警配置
 * Created by lonsun on 2017-10-24.
 */
public interface IMonitoredAlarmConfigDao extends IMockDao<MonitoredAlarmConfigEO> {
    MonitoredAlarmConfigEO getConfigBySiteId(Long siteId);
}
