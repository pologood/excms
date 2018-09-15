package cn.lonsun.monitor.task.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomIndexManageEO;

/**
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
public interface IMonitorCustomIndexManageService extends IMockService<MonitorCustomIndexManageEO> {


    /**
     * 保存指标
     * @param siteId
     */
    void saveIndex(Long siteId);

    /**
     * 根据站点ID获取指标
     * @param siteId
     * @return
     */
    MonitorCustomIndexManageEO getIndex(String codeType, Long siteId);

    /**
     * 更新指定任务
     * @param taskId
     * @param status
     */
    void updateStatus(Long taskId,Integer status);
}
