package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorSiteVisitResultDao;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteVisitResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitDateStatisVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitStatisVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository("monitorSiteVisitResultDao")
public class MonitorSiteVisitResultDaoImpl extends MockDao<MonitorSiteVisitResultEO> implements IMonitorSiteVisitResultDao {


    @Override
    public Pagination getSiteVisitPage(SiteVisitQueryVO vo) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder hql = new StringBuilder("from MonitorSiteVisitResultEO where");
        hql.append(" siteId = ?");
        values.add(vo.getSiteId());
        hql.append(" and monitorDate >= to_date(?,'yyyy-MM-dd')");
        values.add(vo.getBegin());
        hql.append(" and monitorDate <= to_date(?,'yyyy-MM-dd')");
        values.add(vo.getEnd());
        hql.append(" order by monitorDate desc");
        Pagination page =  this.getPagination(vo.getPageIndex(),vo.getPageSize(), hql.toString(),values.toArray());
        return page;
    }

    @Override
    public List<MonitorSiteVisitResultEO> getSiteVisitList(SiteVisitQueryVO vo) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder hql = new StringBuilder("from MonitorSiteVisitResultEO where");
        if (!AppUtil.isEmpty(vo.getSiteId())) {
            hql.append(" siteId = ?");
            values.add(vo.getSiteId());
        }

        if (!AppUtil.isEmpty(vo.getBegin())) {
            hql.append(" and monitorDate >= to_date(?,'yyyy-MM-dd')");
            values.add(vo.getBegin());
        }

        if (!AppUtil.isEmpty(vo.getEnd())) {
            hql.append(" and monitorDate <= to_date(?,'yyyy-MM-dd')");
            values.add(vo.getEnd());
        }

        hql.append(" order by monitorDate desc");
        return getEntitiesByHql(hql.toString(), values.toArray());
    }

//    @Override
//    public SiteVisitStatisVO getSiteVisitStatis(Long taskId) {
//        StringBuilder sql = new StringBuilder("select");
//        sql.append(" count(*) as total,");
//        sql.append(" sum(case is_visitable when 0 then 1 else 0 end) as fail,");
//        sql.append(" sum(case is_visitable when 1 then 1 else 0 end) as success");
//        sql.append(" from MONITOR_SITE_VISIT_RESULT where task_id = ?");
//        return (SiteVisitStatisVO)this.getBeanBySql(sql.toString(),new Object[]{taskId},SiteVisitStatisVO.class,new String[]{"total","success","fail"});
//    }

    @Override
    public SiteVisitStatisVO getSiteVisitStatis(Long siteId, String begin, String end) {
        StringBuilder sql = new StringBuilder("select");
        sql.append(" count(*) as total,");
        sql.append(" sum(case is_visitable when 0 then 1 else 0 end) as fail,");
        sql.append(" sum(case is_visitable when 1 then 1 else 0 end) as success");
        sql.append(" from MONITOR_SITE_VISIT_RESULT where site_id = ?");
        sql.append(" and MONITOR_DATE BETWEEN TO_DATE(?, 'yyyy-mm-dd') and TO_DATE(?, 'yyyy-mm-dd')");
        return (SiteVisitStatisVO)this.getBeanBySql(sql.toString(),new Object[]{siteId,begin,end},SiteVisitStatisVO.class,new String[]{"total","success","fail"});
    }

    @Override
    public SiteVisitDateStatisVO getSiteVisitStatis(Long siteId, String date) {
        StringBuilder sql = new StringBuilder("select");
        sql.append(" count(*) as total,");
        sql.append(" NVL(sum(case when is_visitable = 1 then 1 else 0 end),0) as success,");
        sql.append("'" + date + "'");
        sql.append(" as mdate");
        sql.append(" from MONITOR_SITE_VISIT_RESULT");
        sql.append(" where SITE_ID = ?");
        sql.append(" AND TO_CHAR (monitor_date, 'yyyy/MM/dd') = ?");
        return (SiteVisitDateStatisVO)this.getBeanBySql(sql.toString(),new Object[]{siteId,date},SiteVisitDateStatisVO.class,new String[]{"total","success","mdate"});
    }
}
