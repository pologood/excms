package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorCustomTaskManageDao;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomTaskManageEO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository
public class MonitorCustomTaskManageDaoImpl extends MockDao<MonitorCustomTaskManageEO> implements IMonitorCustomTaskManageDao {


    @Override
    public Pagination getTaskPage(Long siteId,String typeCode, PageQueryVO vo) {
        StringBuilder hql = new StringBuilder("from MonitorCustomTaskManageEO where siteId = ? and typeCode= ? order by startDate desc");
        return this.getPagination(vo.getPageIndex(), vo.getPageSize(),hql.toString(), new Object[]{siteId,typeCode});
    }

    @Override
    public synchronized void updateStatus(Long taskId,String field, Integer status) {
        StringBuilder hql = new StringBuilder("update MonitorCustomTaskManageEO set  ");
        hql.append(field).append(" = ?");
        hql.append(" where id = ?");
        this.executeUpdateByHql(hql.toString(),new Object[]{status,taskId});
    }

    @Override
    public MonitorCustomTaskManageEO getTask(Long taskId, Integer status) {
        StringBuilder hql = new StringBuilder("from MonitorCustomTaskManageEO where id = ? ");
        hql.append(" and status = ?");
        hql.append(" and recordStatus = 'Normal'");
        return getEntityByHql(hql.toString(),new Object[]{taskId,status});
    }



}
