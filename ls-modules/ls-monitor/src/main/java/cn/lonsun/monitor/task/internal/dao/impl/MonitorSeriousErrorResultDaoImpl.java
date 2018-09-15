package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorSeriousErrorResultDao;
import cn.lonsun.monitor.task.internal.entity.MonitorSeriousErrorResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.SeriousErrorQueryVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository
public class MonitorSeriousErrorResultDaoImpl extends MockDao<MonitorSeriousErrorResultEO> implements IMonitorSeriousErrorResultDao {


    @Override
    public Pagination getSeriousErrorPage(SeriousErrorQueryVO vo) {
        StringBuilder hql = new StringBuilder("from MonitorSeriousErrorResultEO where taskId = ? order by monitorDate desc");
        return this.getPagination(vo.getPageIndex(),vo.getPageSize(), hql.toString(),new Object[]{vo.getTaskId()});
    }

    @Override
    public List<MonitorSeriousErrorResultEO> getSeriousErrorList(SeriousErrorQueryVO vo) {
        StringBuilder hql = new StringBuilder("from MonitorSeriousErrorResultEO where taskId = ? order by monitorDate desc");
        return getEntitiesByHql(hql.toString(), new Object[]{vo.getTaskId()});
    }

    @Override
    public Long getCount(Long taskId) {
        return this.getCount("from MonitorSeriousErrorResultEO where taskId = ?",new Object[]{taskId});
    }

    @Override
    public Long getCount(Long taskId,String checkType) {
        return this.getCount("from MonitorSeriousErrorResultEO where taskId = ? and checkType = ? ",
                new Object[]{taskId,checkType});
    }

    @Override
    public List<MonitorSeriousErrorResultEO> getMonitorSiteGroupErrors() {
        StringBuilder sql = new StringBuilder("select");
        sql.append(" S.SITE_NAME as siteName,");
        sql.append(" E.CONTENT_ID as contentId,");
        sql.append(" E.TITLE as title,");
        sql.append(" E.WORD as word,");
        sql.append(" E.TYPE_CODE as typeCode,");
        sql.append(" E.FROM_CODE as fromCode,");
        sql.append(" E.CHECK_TYPE as checkType,");
        sql.append(" E.RESULT as result,");
        sql.append(" E.DOMAIN as domain,");
        sql.append(" E.COLUMN_ID as columnId");
        sql.append(" FROM");
        sql.append(" MONITOR_SERIOUS_ERROR_RESULT E,MONITOR_SITE_STATIS S");
        sql.append(" WHERE E .TASK_ID = S.TASK_ID");
        //返回字段
        String[] fields = new String[]{
                "siteName","contentId","title","word",
                "typeCode","fromCode","checkType","result","domain",
                "columnId"
        };
        return (List<MonitorSeriousErrorResultEO>)this.getBeansBySql(sql.toString(),new Object[]{},MonitorSeriousErrorResultEO.class,fields);
    }
}
