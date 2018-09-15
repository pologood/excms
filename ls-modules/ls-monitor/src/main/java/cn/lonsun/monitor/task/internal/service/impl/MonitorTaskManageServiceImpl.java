package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorTaskManageDao;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;
import cn.lonsun.monitor.task.internal.service.IMonitorTaskManageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorTaskManageServiceImpl extends MockService<MonitorTaskManageEO> implements IMonitorTaskManageService {

    @Resource
    private IMonitorTaskManageDao monitorTaskManageDao;

    @Override
    public MonitorTaskManageEO getTaskInfo(Long siteId, Integer taskStatus) {

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",siteId);
        map.put("siteDenyStatus",taskStatus);
        map.put("siteUpdateStatus",taskStatus);
        map.put("columnUpdateStatus",taskStatus);
        map.put("errorStatus",taskStatus);
        map.put("replyStatus",taskStatus);
        map.put("siteUseStatus",taskStatus);
        map.put("infoUpdateStatus",taskStatus);
        map.put("serviceStatus",taskStatus);
        map.put("replyScopeStatus",taskStatus);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return this.getEntity(MonitorTaskManageEO.class,map);
    }

    @Override
    public void startTask(Long siteId,Long taskId) {
    }

    @Override
    public Pagination getTaskPage(Long siteId, PageQueryVO vo) {
        return monitorTaskManageDao.getTaskPage(siteId,vo);
    }

    @Override
    public void updateStatus(Long taskId, String field, Integer status) {
        monitorTaskManageDao.updateStatus(taskId,field,status);
    }

    @Override
    public MonitorTaskManageEO getTask(Long siteId, Integer status) {
        return monitorTaskManageDao.getTask(siteId,status);
    }

    @Override
    public MonitorTaskManageEO getLatestTask(Long siteId, Long taskId) {
        return monitorTaskManageDao.getLatestTask(siteId,taskId);
    }
}
