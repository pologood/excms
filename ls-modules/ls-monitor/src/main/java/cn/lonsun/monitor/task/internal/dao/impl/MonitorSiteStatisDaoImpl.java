package cn.lonsun.monitor.task.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorSiteStatisDao;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteStatisEO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteStatisQueryVO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2017-09-28 9:23
 */
@Repository
public class MonitorSiteStatisDaoImpl extends MockDao<MonitorSiteStatisEO> implements IMonitorSiteStatisDao {

    @Override
    public Pagination getSiteStatisPage(SiteStatisQueryVO vo) {
        StringBuilder hql = new StringBuilder("from MonitorSiteStatisEO  order by isQualified desc,monitorScore ASC");
        return this.getPagination(vo.getPageIndex(), vo.getPageSize(),hql.toString(), new Object[]{});
    }

    @Override
    public MonitorSiteStatisEO getSiteStatis(Long siteId) {
        StringBuilder hql = new StringBuilder("from MonitorSiteStatisEO where siteId=?");
        return this.getEntityByHql(hql.toString(), new Object[]{siteId});
    }

    @Override
    public void updateSiteDeny(Long siteId, Long taskId, Integer status) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set SITE_DENY = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{status,siteId,taskId});
    }

    @Override
    public void updateSiteUpdate(Long siteId, Long taskId, Integer status) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set SITE_UPDATE = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{status,siteId,taskId});
    }

    @Override
    public void updateColumnUpdate(Long siteId, Long taskId, Integer status) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set COLUMN_UPDATE = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{status,siteId,taskId});
    }

    @Override
    public void updateError(Long siteId, Long taskId, Integer status) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set ERROR = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{status,siteId,taskId});
    }

    @Override
    public void updateReply(Long siteId, Long taskId, Integer status) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set REPLY = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{status,siteId,taskId});
    }

    @Override
    public void updateSiteUse(Long siteId, Long taskId, String score) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set SITE_USE = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{score,siteId,taskId});
    }

    @Override
    public void updateIndexInfoUpdate(Long siteId, Long taskId, String score) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set INDEX_INFO_UPDATE = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{score,siteId,taskId});
    }

    @Override
    public void updateInfoUpdate(Long siteId, Long taskId, String score) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set INFO_UPDATE = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{score,siteId,taskId});
    }

    @Override
    public void updateReplyScope(Long siteId, Long taskId, String score) {
        this.executeUpdateBySql("update MONITOR_SITE_STATIS set REPLY_SCOPE = ? where SITE_ID = ? AND TASK_ID = ?", new Object[]{score,siteId,taskId});
    }
}
