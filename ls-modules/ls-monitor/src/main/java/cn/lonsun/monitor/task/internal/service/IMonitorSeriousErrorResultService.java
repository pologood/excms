package cn.lonsun.monitor.task.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorSeriousErrorResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.SeriousErrorQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.SeriousErrorStatisVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
public interface IMonitorSeriousErrorResultService extends IMockService<MonitorSeriousErrorResultEO> {


    /**
     * 分页查询严重错误
     * @param vo
     * @return
     */
    Pagination getSeriousErrorPage(SeriousErrorQueryVO vo);

    /**
     * 不分页查询严重错误
     * @param vo
     * @return
     */
    List<MonitorSeriousErrorResultEO> getSeriousErrorList(SeriousErrorQueryVO vo);

    /**
     * 执行检测
     * @param siteId
     * @param taskId
     */
    void runMonitor(Long siteId, Long taskId,Long reportId);

    /**
     * 查询数量
     * @param taskId
     * @return
     */
    Long getCount(Long taskId);

    /**
     * 查询数量
     * @param taskId
     * @param checkType
     * @return
     */
    Long getCount(Long taskId,String checkType);

    /**
     * 获取统计结果
     * @param taskId
     * @param siteId
     * @return
     */
    SeriousErrorStatisVO loadSeriousErrorStatis(Long taskId,Long siteId);

    /**
     * 获取严重错误信息
     * @return
     */
    List<MonitorSeriousErrorResultEO> getMonitorSiteGroupErrors();
}
