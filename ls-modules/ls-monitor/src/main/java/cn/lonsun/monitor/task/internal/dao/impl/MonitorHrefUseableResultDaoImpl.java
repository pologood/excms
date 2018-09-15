package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorHrefUseableResultDao;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableStatisVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository("monitorHrefUseableResultDao")
public class MonitorHrefUseableResultDaoImpl extends MockDao<MonitorHrefUseableResultEO> implements IMonitorHrefUseableResultDao {


    @Override
    public Pagination getHrefUseablePage(HrefUseableQueryVO vo) {
        StringBuilder hql = new StringBuilder("from MonitorHrefUseableResultEO where taskId = ? order by monitorDate desc");
        return this.getPagination(vo.getPageIndex(),vo.getPageSize(), hql.toString(),new Object[]{vo.getTaskId()});
    }

    @Override
    public List<MonitorHrefUseableResultEO> getHrefUseableList(HrefUseableQueryVO vo) {
        StringBuilder hql = new StringBuilder("from MonitorHrefUseableResultEO where taskId = ? order by monitorDate desc");
        return getEntitiesByHql(hql.toString(), new Object[]{vo.getTaskId()});
    }

    @Override
    public HrefUseableStatisVO getHrefUseaStatis(Long taskId) {
        StringBuilder sql = new StringBuilder("select");
        sql.append(" count(*) as total,");
        sql.append(" nvl(sum(case is_index when 1 then 1 else 0 end),0) as indexCount,");
        sql.append(" nvl(sum(case is_index when 0 then 1 else 0 end),0) as otherCount");
        sql.append(" from MONITOR_HREF_USEABLE_RESULT where task_id = ?");
        return (HrefUseableStatisVO)this.getBeanBySql(sql.toString(),new Object[]{taskId},HrefUseableStatisVO.class,new String[]{"total","indexCount","otherCount"});
    }
}
