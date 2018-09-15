package cn.lonsun.monitor.task.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableStatisVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
public interface IMonitorHrefUseableResultService extends IMockService<MonitorHrefUseableResultEO> {


    /**
     * 分页查询错误链接
     * @param vo
     * @return
     */
    Pagination getHrefUseablePage(HrefUseableQueryVO vo);

    /**
     * 不分页查询错误链接
     * @param vo
     * @return
     */
    List<MonitorHrefUseableResultEO> getHrefUseableList(HrefUseableQueryVO vo);

    /**
     * 执行检测
     * @param siteId
     * @param taskId
     */
    void runMonitor(Long siteId, Long taskId,Long reportId);

    /**
     * 错链数量统计
     * @param taskId
     * @return
     */
    HrefUseableStatisVO getHrefUseaStatis(Long taskId);

    /**
     * 启动动态监测任务
     * @param taskId
     * @param siteId
     * @param reportId
     * @param registerCode
     */
    void runMontorDynamic(Long taskId,Long siteId,Long reportId,String registerCode,String uri);

    /**
     * 获取错误链接统计
     * @param taskId
     * @param siteId
     * @return
     */
    HrefUseableStatisVO loadHrefUseableStatis(Long taskId,Long siteId);
}
