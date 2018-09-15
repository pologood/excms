package cn.lonsun.monitor.task.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomTaskManageEO;

/**
 * @author gu.fei
 * @version 2017-09-28 9:22
 */
public interface IMonitorCustomTaskManageDao extends IMockDao<MonitorCustomTaskManageEO> {


    /**
     * 分页获取结果
     * @param siteId
     * @param typeCode
     * @param vo
     * @return
     */
    Pagination getTaskPage(Long siteId,String typeCode, PageQueryVO vo);

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
    MonitorCustomTaskManageEO getTask(Long taskId, Integer status);
}
