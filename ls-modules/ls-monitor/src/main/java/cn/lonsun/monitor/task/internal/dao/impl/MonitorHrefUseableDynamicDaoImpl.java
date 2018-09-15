package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorHrefUseableDynamicDao;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableDynamicEO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableQueryVO;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository
public class MonitorHrefUseableDynamicDaoImpl extends MockDao<MonitorHrefUseableDynamicEO> implements IMonitorHrefUseableDynamicDao {

    @Override
    public Pagination getPage(HrefUseableQueryVO vo) {
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(new Date());//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        Date frontDate = calendar.getTime();
        Map<String,Object> map = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from MonitorHrefUseableDynamicEO");
        hql.append(" where taskId=:taskId");
        hql.append(" and monitorDate < :monitorDate");
        hql.append(" and monitorDate >= :frontDate");
        hql.append(" order by monitorDate desc");
        map.put("taskId",vo.getTaskId());
        map.put("monitorDate",vo.getDate());
        map.put("frontDate",frontDate);
        return this.getPagination(vo.getPageIndex(),vo.getPageSize(),hql.toString(),map);
    }

    @Override
    public Long getHrefUseableDynamicCout(Long taskId, Date date) {
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(new Date());//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        Date frontDate = calendar.getTime();
        System.out.println(DateFormatUtils.format(frontDate,"yyyy-MM-dd"));
        Map<String,Object> map = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from MonitorHrefUseableDynamicEO");
        hql.append(" where taskId=:taskId");
        hql.append(" and monitorDate < :monitorDate");
        hql.append(" and monitorDate >= :frontDate");
        map.put("taskId",taskId);
        map.put("monitorDate",date);
        map.put("frontDate",frontDate);
        return this.getCount(hql.toString(),map);
    }
}
