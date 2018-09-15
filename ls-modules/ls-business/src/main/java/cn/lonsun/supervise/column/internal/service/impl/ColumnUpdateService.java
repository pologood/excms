package cn.lonsun.supervise.column.internal.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.supervise.column.internal.dao.IColumnUpdateDao;
import cn.lonsun.supervise.column.internal.job.ColumnUpdateTaskImpl;
import cn.lonsun.supervise.column.internal.service.IColumnUpdateService;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateEO;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.supervise.vo.SupervisePageVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2016-4-5 10:48
 */
@Service
public class ColumnUpdateService extends MockService<ColumnUpdateEO> implements IColumnUpdateService {

    @Autowired
    private IColumnUpdateDao columnUpdateDao;

    @Autowired
    private ICronConfService cronConfService;

    @Autowired
    private IScheduleJobService scheduleJobService;

    @Override
    public Pagination getPageEOs(SupervisePageVO vo) {
        return columnUpdateDao.getPageEOs(vo);
    }

    @Override
     public void saveTask(ColumnUpdateEO updateEO, CronConfEO cronEO) {
        Long siteId = LoginPersonUtil.getSiteId();
        Long cronId = cronConfService.saveEntity(cronEO);
        updateEO.setCronId(cronId);
        updateEO.setSiteId(siteId);
        updateEO.setRunStatus(1); //设置执行
        Long taskId = this.saveEntity(updateEO);

        ScheduleJobEO eo = new ScheduleJobEO();
        eo.setSiteId(updateEO.getSiteId());
        eo.setName(updateEO.getTaskName());
        eo.setType("column_update");
        eo.setJson(String.valueOf(taskId));
        eo.setClazz(ColumnUpdateTaskImpl.class.getName());
        eo.setStatus("NORMAL");
        //定时模式是隔天模式
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
            //设置下次执行时间
            updateEO.setNextRunDate(next);
            //设置应该执行时间
            updateEO.setPrevShouldRunDate(cronEO.getStartDate());
            eo.setCronExpression(CronUtil.getCron(next));
        } else {
            //自定义cron表达式
            eo.setCronExpression(cronEO.getCronExpress());
            Date next = CronUtil.getNextCronTime(cronEO.getCronExpress());
            updateEO.setNextRunDate(next);
            updateEO.setPrevShouldRunDate(new Date());
        }

        Long schedId = scheduleJobService.saveEntity(eo);
        updateEO.setSchedId(schedId);
        this.updateEntity(updateEO);
    }

    @Override
    public void updateTask(ColumnUpdateEO updateEO, CronConfEO cronEO) {
        ColumnUpdateEO oeo = this.getEntity(ColumnUpdateEO.class, updateEO.getId());
        oeo.setTaskName(updateEO.getTaskName());
        oeo.setTaskType(updateEO.getTaskType());
        oeo.setMsgAlert(updateEO.getMsgAlert());
        oeo.setAlertFreq(updateEO.getAlertFreq());
        oeo.setColumnUpdateFreq(updateEO.getColumnUpdateFreq());
        oeo.setColumnUpdateNum(updateEO.getColumnUpdateNum());
        oeo.setColumnIds(updateEO.getColumnIds());
        oeo.setcSiteIds(updateEO.getcSiteIds());
        oeo.setCronDesc(updateEO.getCronDesc());

        CronConfEO oceo = cronConfService.getEntity(CronConfEO.class, updateEO.getCronId());
        //如果定时改变，则需要重新配置定时任务
        oceo.setSpaceOfDay(cronEO.getSpaceOfDay());
        oceo.setTimeMode(cronEO.getTimeMode());
        oceo.setStartDate(cronEO.getStartDate());
        oceo.setCronExpress(cronEO.getCronExpress());
        this.updateEntity(oeo);
        cronConfService.updateEntity(oceo);

        //删除老的定时任务
        scheduleJobService.delete(ScheduleJobEO.class, oeo.getSchedId());
        ScheduleJobEO eo = new ScheduleJobEO();
        eo.setSiteId(oeo.getSiteId());
        eo.setName(updateEO.getTaskName());
        eo.setType("column_update");
        eo.setJson(String.valueOf(updateEO.getId()));
        eo.setClazz(ColumnUpdateTaskImpl.class.getName());
        eo.setStatus("NORMAL");
        //定时方式为隔天方式
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
            //循环获取下次应该执行的时间
            while(next.getTime() < new Date().getTime()) {
                next = DateUtil.getDateBeforeOrAfter(next, cronEO.getSpaceOfDay());
            }
            oeo.setNextRunDate(next);
            oeo.setPrevShouldRunDate(DateUtil.getDateBeforeOrAfter(next,-cronEO.getSpaceOfDay()));

            eo.setCronExpression(CronUtil.getCron(next));
        } else {
            //自定义cron模式
            Date next = CronUtil.getNextCronTime(cronEO.getCronExpress());
            oeo.setNextRunDate(next);
            oeo.setPrevShouldRunDate(new Date());

            eo.setCronExpression(cronEO.getCronExpress());
        }

        Long schedId = scheduleJobService.saveEntity(eo);
        oeo.setSchedId(schedId);
        this.updateEntity(oeo);
    }

    @Override
    public void execTask(Long schedId) {
        ScheduleJobEO eo = scheduleJobService.getEntity(ScheduleJobEO.class,schedId);
        scheduleJobService.triggerJob(eo);
    }

    @Override
    public void pauseTask(Long taskId) {
        ColumnUpdateEO updateEO = this.getEntity(ColumnUpdateEO.class, taskId);
        updateEO.setRunStatus(0);
        this.updateEntity(updateEO);
    }

    @Override
    public void resumeTask(Long taskId) {
        ColumnUpdateEO updateEO = this.getEntity(ColumnUpdateEO.class, taskId);
        updateEO.setRunStatus(1);
        CronConfEO cronEO = cronConfService.getEntity(CronConfEO.class,updateEO.getCronId());
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            if(updateEO.getNextRunDate().getTime() < new Date().getTime()) {
                scheduleJobService.delete(ScheduleJobEO.class, updateEO.getSchedId());
                ScheduleJobEO eo = new ScheduleJobEO();
                eo.setSiteId(updateEO.getSiteId());
                eo.setName(updateEO.getTaskName());
                eo.setType("column_update");
                eo.setJson(String.valueOf(updateEO.getId()));
                eo.setClazz(ColumnUpdateTaskImpl.class.getName());
                Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
                //循环获取下次应该执行的时间
                while(next.getTime() < new Date().getTime()) {
                    next = DateUtil.getDateBeforeOrAfter(next, cronEO.getSpaceOfDay());
                }
                eo.setCronExpression(CronUtil.getCron(next));
                eo.setStatus("NORMAL");
                scheduleJobService.saveEntity(eo);

                updateEO.setNextRunDate(next);
                updateEO.setPrevShouldRunDate(DateUtil.getDateBeforeOrAfter(next, -cronEO.getSpaceOfDay()));
            }
        }

        this.updateEntity(updateEO);
    }

    @Override
    public void physDelEOs(Long[] ids) {
        for(Long id : ids) {
            ColumnUpdateEO updateEO = this.getEntity(ColumnUpdateEO.class, id);
            pauseTask(id);
            scheduleJobService.delete(ScheduleJobEO.class,updateEO.getSchedId());
        }
        columnUpdateDao.physDelEOs(ids);
    }
}
