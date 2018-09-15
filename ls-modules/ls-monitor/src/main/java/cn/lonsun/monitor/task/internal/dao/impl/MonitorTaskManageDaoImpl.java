package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorTaskManageDao;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository
public class MonitorTaskManageDaoImpl extends MockDao<MonitorTaskManageEO> implements IMonitorTaskManageDao {


    @Override
    public Pagination getTaskPage(Long siteId, PageQueryVO vo) {
        StringBuilder hql = new StringBuilder("from MonitorTaskManageEO where siteId = ? order by startDate desc");
        return this.getPagination(vo.getPageIndex(), vo.getPageSize(),hql.toString(), new Object[]{siteId});
    }

    @Override
    public synchronized void updateStatus(Long taskId,String field, Integer status) {
        StringBuilder hql = new StringBuilder("update MonitorTaskManageEO set  ");
        hql.append(field).append(" = ?");
        hql.append(" where id = ?");
        this.executeUpdateByHql(hql.toString(),new Object[]{status,taskId});
    }

    @Override
    public MonitorTaskManageEO getTask(Long taskId, Integer status) {
        StringBuilder hql = new StringBuilder("from MonitorTaskManageEO where id = ? and (");
        hql.append("siteDenyStatus = ?");
        hql.append(" or siteUpdateStatus = ?");
        hql.append(" or columnUpdateStatus = ?");
        hql.append(" or errorStatus = ?");
        hql.append(" or replyStatus = ?");
        hql.append(" or siteUseStatus = ?");
        hql.append(" or infoUpdateStatus = ?");
        hql.append(" or replyScopeStatus = ?");
        hql.append(") and recordStatus = 'Normal'");
        return getEntityByHql(hql.toString(),new Object[]{taskId,status,status,status,status,status,status,status,status});
    }

    @Override
    public MonitorTaskManageEO getLatestTask(Long siteId, Long taskId) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder hql = new StringBuilder("from MonitorTaskManageEO n where n.id=(");
        hql.append(" select max(id) from MonitorTaskManageEO o");
        hql.append(" where o.siteId = ?");
        values.add(siteId);
        if (null != taskId) {
            hql.append(" and o.id < ?");
            values.add(taskId);
        }
        hql.append(" )");
        return getEntityByHql(hql.toString(), values.toArray());
    }
}
