package cn.lonsun.monitor.config.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.monitor.config.internal.entity.MonitoredAlarmConfigEO;

/**监测报警配置
 * Created by lonsun on 2017-10-24.
 */
public interface IMonitoredAlarmConfigService extends IMockService<MonitoredAlarmConfigEO> {
    MonitoredAlarmConfigEO getConfigBySiteId(Long siteId);
}
