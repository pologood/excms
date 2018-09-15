package cn.lonsun.monitor.config.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.monitor.config.internal.dao.IMonitoredAlarmConfigDao;
import cn.lonsun.monitor.config.internal.entity.MonitoredAlarmConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredAlarmConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**监测报警配置
 * Created by lonsun on 2017-10-24.
 */
@Service
public class MonitoredAlarmConfigServiceImpl extends MockService<MonitoredAlarmConfigEO> implements IMonitoredAlarmConfigService {
    @Resource
    private IMonitoredAlarmConfigDao monitoredAlarmConfigDao;

    /**
     * 当前站点配置
     * @param siteId
     * @return
     */
    @Override
    public MonitoredAlarmConfigEO getConfigBySiteId(Long siteId) {
        return monitoredAlarmConfigDao.getConfigBySiteId(siteId);
    }
}
