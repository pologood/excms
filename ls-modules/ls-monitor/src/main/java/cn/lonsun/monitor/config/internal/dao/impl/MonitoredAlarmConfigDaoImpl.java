package cn.lonsun.monitor.config.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.monitor.config.internal.dao.IMonitoredAlarmConfigDao;
import cn.lonsun.monitor.config.internal.entity.MonitoredAlarmConfigEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**监测报警配置
 * Created by lonsun on 2017-10-24.
 */
@Repository
public class MonitoredAlarmConfigDaoImpl extends MockDao<MonitoredAlarmConfigEO> implements IMonitoredAlarmConfigDao {
    @Override
    public MonitoredAlarmConfigEO getConfigBySiteId(Long siteId) {
        StringBuffer stringBuffer =new StringBuffer();
        stringBuffer.append("from  MonitoredAlarmConfigEO m where m.recordStatus =? and m.siteId =?");
        List<Object> parm =new ArrayList<Object>();
        parm.add(AMockEntity.RecordStatus.Normal.toString());
        parm.add(siteId);
       List<MonitoredAlarmConfigEO> monitoredAlarmConfigEOs = getEntitiesByHql(stringBuffer.toString(),parm.toArray());

        if(monitoredAlarmConfigEOs.size()>0){
            return  monitoredAlarmConfigEOs.get(0);
        }
        return new MonitoredAlarmConfigEO();
    }
}
