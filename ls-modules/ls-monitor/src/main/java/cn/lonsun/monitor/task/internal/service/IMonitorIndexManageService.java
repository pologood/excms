package cn.lonsun.monitor.task.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.monitor.task.internal.entity.MonitorIndexManageEO;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;

/**
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
public interface IMonitorIndexManageService extends IMockService<MonitorIndexManageEO> {


    /**
     * 保存指标
     * @param siteId
     */
    void saveIndex(Long siteId);

    /**
     * 停止任务
     */
    void stopTask();

    /**
     * 开始任务
     */
    MonitorTaskManageEO startTask();

    /**
     * 根据站点ID获取指标
     * @param siteId
     * @return
     */
    MonitorIndexManageEO getIndex(String codeType,Long siteId);
}
