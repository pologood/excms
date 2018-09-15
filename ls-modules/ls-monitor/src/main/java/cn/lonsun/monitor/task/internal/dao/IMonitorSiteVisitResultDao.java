package cn.lonsun.monitor.task.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteVisitResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitDateStatisVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitStatisVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:22
 */
public interface IMonitorSiteVisitResultDao extends IMockDao<MonitorSiteVisitResultEO> {


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
     * 获取统计信息
     * @param siteId+
     * @param begin
     * @param end
     * @return
     */
    SiteVisitStatisVO getSiteVisitStatis(Long siteId, String begin, String end);

    /**
     * 根据监测日期统计结果
     * @param siteId
     * @param date
     * @return
     */
    SiteVisitDateStatisVO getSiteVisitStatis(Long siteId, String date);
}
