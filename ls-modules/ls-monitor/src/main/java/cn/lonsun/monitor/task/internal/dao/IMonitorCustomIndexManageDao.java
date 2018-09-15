package cn.lonsun.monitor.task.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomIndexManageEO;

/**
 * @author gu.fei
 * @version 2017-09-28 9:22
 */
public interface IMonitorCustomIndexManageDao extends IMockDao<MonitorCustomIndexManageEO> {


    /**
     * 根据站点ID获取指标
     * @param siteId
     * @return
     */
    MonitorCustomIndexManageEO getIndex(Long siteId);

    /**
     * 更新指定任务
     * @param taskId
     * @param status
     */
    void updateStatus(Long taskId,Integer status);
}
