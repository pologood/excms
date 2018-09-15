package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.monitor.internal.service.IMonitorColumnDetailService;
import cn.lonsun.monitor.internal.service.IMonitorInteractDetailService;
import cn.lonsun.monitor.task.internal.dao.IMonitorCustomTaskManageDao;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomIndexManageEO;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomTaskManageEO;
import cn.lonsun.monitor.task.internal.service.*;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorCustomTaskManageServiceImpl extends MockService<MonitorCustomTaskManageEO> implements IMonitorCustomTaskManageService {

    @Resource
    private IMonitorCustomTaskManageDao monitorCustomTaskManageDao;

    @Resource
    private IMonitorCustomIndexManageService monitorCustomIndexManageService;

    @Resource
    private IMonitorSiteVisitResultService monitorSiteVisitResultService;

    @Resource
    private IMonitorHrefUseableResultService monitorHrefUseableResultService;

    @Resource
    private IMonitorSeriousErrorResultService monitorSeriousErrorResultService;

    @Resource
    private IMonitorColumnDetailService monitorColumnDetailService;

    @Resource
    private IMonitorInteractDetailService monitorInteractDetailService;

    @Resource
    private IScheduleJobService scheduleJobService;

    @Resource
    private TaskExecutor taskExecutor;

    @Override
    public void startTask(String typeCode) {
        final Long siteId = LoginPersonUtil.getSiteId();
        MonitorCustomTaskManageEO task = new MonitorCustomTaskManageEO();
        task.setStartDate(new Date());
        task.setTypeCode(typeCode);
        task.setSiteId(siteId);
        final Long taskId = this.saveEntity(task);
        MonitorCustomIndexManageEO index = monitorCustomIndexManageService.getIndex(typeCode,siteId);
        index.setStartDate(new Date());
        index.setTaskId(taskId);
        index.setTaskStatus(1);
        monitorCustomIndexManageService.updateEntity(index);
        try {
            if(typeCode.equals("siteDeny")) {
                //站点不可访问检测
                monitorSiteVisitResultService.runMonitor(siteId,taskId,null);
            } else if(typeCode.equals("siteUpdate")) {
                //网站不更新
                monitorColumnDetailService.monitorIndexColumn(siteId,taskId,null);
            } else if(typeCode.equals("columnUpdate")) {
                //栏目更新
                monitorColumnDetailService.monitorBaseColumn(siteId,taskId,null);
            } else if(typeCode.equals("error")) {
                //严重错误检测
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        monitorSeriousErrorResultService.runMonitor(siteId,taskId,null);
                    }
                });
            } else if(typeCode.equals("reply")) {
                //互动回应
                monitorInteractDetailService.monitorInteract(siteId,taskId,null);
            } else if(typeCode.equals("siteUse")) {
                //错链检测
                monitorHrefUseableResultService.runMonitor(siteId,taskId,null);
            } else if(typeCode.equals("infoUpdate")) {
                //栏目更新
                monitorColumnDetailService.monitorCustomBaseColumn(siteId,taskId,null);
            } else if(typeCode.equals("replyScope")) {
                //互动回应差
                monitorInteractDetailService.monitorInteract(siteId,taskId,null);
            }
        } catch (Exception e) {
            //任务启动失败
            monitorCustomIndexManageService.updateStatus(taskId,3);
            e.printStackTrace();
        }
    }

    @Override
    public void stopTask(Long id) {
        MonitorCustomIndexManageEO index = monitorCustomIndexManageService.getEntity(MonitorCustomIndexManageEO.class,id);
        index.setTaskStatus(2);
        if(null != index.getScheduleId()) {
            scheduleJobService.delete(ScheduleJobEO.class,index.getScheduleId());
        }
        monitorCustomIndexManageService.updateEntity(index);
    }

    @Override
    public Pagination getTaskPage(Long siteId,String typeCode, PageQueryVO vo) {
        return monitorCustomTaskManageDao.getTaskPage(siteId,typeCode,vo);
    }

    @Override
    public void updateStatus(Long taskId, String field, Integer status) {
        monitorCustomTaskManageDao.updateStatus(taskId,field,status);
    }

    @Override
    public MonitorCustomTaskManageEO getTask(Long siteId, Integer status) {
        return monitorCustomTaskManageDao.getTask(siteId,status);
    }
}
