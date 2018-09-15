package cn.lonsun.monitor.task.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;

/**
 * @author gu.fei
 * @version 2017-09-28 9:22
 */
public interface IMonitorTaskManageDao extends IMockDao<MonitorTaskManageEO> {


    /**
     * 分页获取结果
     * @param siteId
     * @param vo
     * @return
     */
    Pagination getTaskPage(Long siteId, PageQueryVO vo);

    /**
     * 更新状态
     * @param taskId
     * @param field
     * @param status
     */
    void updateStatus(Long taskId, String field, Integer status);

    /**
     * 根据任务状态获取检测任务
     * @param taskId
     * @param status
     * @return
     */
    MonitorTaskManageEO getTask(Long taskId,Integer status);

    /**
     * 获取最新任务
     * @param siteId
     * @param taskId 如果taskId不为空 则获取除了taskId 之外的最新任务
     * @return
     */
    MonitorTaskManageEO getLatestTask(Long siteId,Long taskId);
}
