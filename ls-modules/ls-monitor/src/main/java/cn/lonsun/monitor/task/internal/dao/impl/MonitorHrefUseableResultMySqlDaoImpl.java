package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableStatisVO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository("monitorHrefUseableResultMySqlDao")
public class MonitorHrefUseableResultMySqlDaoImpl extends MonitorHrefUseableResultDaoImpl {

    @Override
    public HrefUseableStatisVO getHrefUseaStatis(Long taskId) {
        StringBuilder sql = new StringBuilder("select");
        sql.append(" count(*) as total,");
        sql.append(" ifnull(sum(case is_index when 1 then 1 else 0 end),0) as indexCount,");
        sql.append(" ifnull(sum(case is_index when 0 then 1 else 0 end),0) as otherCount");
        sql.append(" from MONITOR_HREF_USEABLE_RESULT where task_id = ?");
        return (HrefUseableStatisVO)this.getBeanBySql(sql.toString(),new Object[]{taskId},HrefUseableStatisVO.class,new String[]{"total","indexCount","otherCount"});
    }
}
