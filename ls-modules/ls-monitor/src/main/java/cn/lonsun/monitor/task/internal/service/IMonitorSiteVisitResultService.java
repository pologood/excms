package cn.lonsun.monitor.task.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteVisitResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitDateStatisVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitStatisVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
public interface IMonitorSiteVisitResultService extends IMockService<MonitorSiteVisitResultEO> {


    /**
     * 分页查询站点访问
     * @param vo
     * @return
     */
    Pagination getSiteVisitPage(SiteVisitQueryVO vo);

    /**
     * 不分页查询站点访问
     * @param vo
     * @return
     */
    List<MonitorSiteVisitResultEO> getSiteVisitList(SiteVisitQueryVO vo);

    /**
     * 执行站点不可访问检测
     * @param siteId
     * @param taskId
     */
    void runMonitor(Long siteId,Long taskId,Long reportId);

    /**
     * 获取统计信息
     * @param taskId
     * @return
     */
    SiteVisitStatisVO getSiteVisitStatis(Long taskId);

    /**
     * 根据监测日期统计结果
     * @param siteId
     * @param date
     * @return
     */
    SiteVisitDateStatisVO getSiteVisitStatis(Long siteId, String date);

    /**
     * 获取站点统计
     * @param taskId
     * @param siteId
     * @return
     */
    SiteVisitStatisVO loadSiteVisitStatis(Long taskId,Long siteId);
}
