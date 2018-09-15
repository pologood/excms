package cn.lonsun.supervise.errhref.internal.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.errhref.internal.dao.IErrHrefDao;
import cn.lonsun.supervise.errhref.internal.entity.ErrHrefEO;
import cn.lonsun.supervise.errhref.internal.entity.HrefResultEO;
import cn.lonsun.supervise.errhref.internal.job.ErrHrefTaskImpl;
import cn.lonsun.supervise.errhref.internal.service.IErrHrefService;
import cn.lonsun.supervise.errhref.internal.service.IHrefResultService;
import cn.lonsun.supervise.errhref.internal.util.URLHelper;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.supervise.vo.SupervisePageVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2016-4-5 10:48
 */
@Service
public class ErrHrefService extends MockService<ErrHrefEO> implements IErrHrefService {

    @Autowired
    private IErrHrefDao errHrefDao;

    @Autowired
    private ICronConfService cronConfService;

    @Autowired
    private IHrefResultService hrefResultService;

    @Autowired
    private IScheduleJobService scheduleJobService;

    @Override
    public Pagination getPageEOs(SupervisePageVO vo) {
        return errHrefDao.getPageEOs(vo);
    }

    @Override
     public void saveTask(ErrHrefEO hrefEO, CronConfEO cronEO) {
        Long siteId = LoginPersonUtil.getSiteId();
        Long cronId = cronConfService.saveEntity(cronEO);
        hrefEO.setCronId(cronId);
        hrefEO.setSiteId(siteId);
        hrefEO.setRunStatus(1); //设置执行
        Long taskId = this.saveEntity(hrefEO);

        ScheduleJobEO eo = new ScheduleJobEO();
        eo.setSiteId(hrefEO.getSiteId());
        eo.setName(hrefEO.getTaskName());
        eo.setType("err_href");
        eo.setJson(String.valueOf(taskId));
        eo.setClazz(ErrHrefTaskImpl.class.getName());
        eo.setStatus("NORMAL");
        //定时模式是隔天模式
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
            //设置下次执行时间
            hrefEO.setNextRunDate(next);
            eo.setCronExpression(CronUtil.getCron(next));
        } else {
            //自定义cron表达式
            eo.setCronExpression(cronEO.getCronExpress());
            Date next = CronUtil.getNextCronTime(cronEO.getCronExpress());
            hrefEO.setNextRunDate(next);
        }

        Long schedId = scheduleJobService.saveEntity(eo);
        hrefEO.setSchedId(schedId);
        this.updateEntity(hrefEO);
    }

    @Override
    public void updateTask(ErrHrefEO hrefEO, CronConfEO cronEO) {
        ErrHrefEO oeo = this.getEntity(ErrHrefEO.class, hrefEO.getId());
        scheduleJobService.delete(ScheduleJobEO.class, oeo.getSchedId());
        oeo.setTaskName(hrefEO.getTaskName());
        oeo.setWebDomain(hrefEO.getWebDomain());
        oeo.setWebSite(hrefEO.getWebSite());
        oeo.setCharset(hrefEO.getCharset());
        oeo.setDepth(hrefEO.getDepth());
        oeo.setFilterHref(hrefEO.getFilterHref());
        oeo.setMsgAlert(hrefEO.getMsgAlert());
        oeo.setAlertFreq(hrefEO.getAlertFreq());
        oeo.setCronDesc(hrefEO.getCronDesc());

        CronConfEO oceo = cronConfService.getEntity(CronConfEO.class, hrefEO.getCronId());
        oceo.setSpaceOfDay(cronEO.getSpaceOfDay());
        oceo.setTimeMode(cronEO.getTimeMode());
        oceo.setStartDate(cronEO.getStartDate());
        oceo.setCronExpress(cronEO.getCronExpress());
        cronConfService.updateEntity(oceo);

        //删除老的定时任务
        scheduleJobService.delete(ScheduleJobEO.class, oeo.getSchedId());
        ScheduleJobEO eo = new ScheduleJobEO();
        eo.setSiteId(oeo.getSiteId());
        eo.setName(hrefEO.getTaskName());
        eo.setType("err_href");
        eo.setJson(String.valueOf(oeo.getId()));
        eo.setClazz(ErrHrefTaskImpl.class.getName());
        eo.setStatus("NORMAL");
        //定时方式为隔天方式
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
            //循环获取下次应该执行的时间
            while(next.getTime() < new Date().getTime()) {
                next = DateUtil.getDateBeforeOrAfter(next, cronEO.getSpaceOfDay());
            }
            oeo.setNextRunDate(next);
            eo.setCronExpression(CronUtil.getCron(next));
        } else {
            //自定义cron模式
            Date next = CronUtil.getNextCronTime(cronEO.getCronExpress());
            oeo.setNextRunDate(next);
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
        ErrHrefEO hrefEO = this.getEntity(ErrHrefEO.class, taskId);
        hrefEO.setRunStatus(0);
        this.updateEntity(hrefEO);
    }

    @Override
    public void resumeTask(Long taskId) {
        ErrHrefEO hrefEO = this.getEntity(ErrHrefEO.class, taskId);
        hrefEO.setRunStatus(1);

        CronConfEO cronEO = cronConfService.getEntity(CronConfEO.class,hrefEO.getCronId());
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            if(hrefEO.getNextRunDate().getTime() < new Date().getTime()) {
                scheduleJobService.delete(ScheduleJobEO.class, hrefEO.getSchedId());
                ScheduleJobEO eo = new ScheduleJobEO();
                eo.setSiteId(hrefEO.getSiteId());
                eo.setName(hrefEO.getTaskName());
                eo.setType("column_update");
                eo.setJson(String.valueOf(hrefEO.getId()));
                eo.setClazz(ErrHrefTaskImpl.class.getName());
                Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
                //循环获取下次应该执行的时间
                while(next.getTime() < new Date().getTime()) {
                    next = DateUtil.getDateBeforeOrAfter(next, cronEO.getSpaceOfDay());
                }
                eo.setCronExpression(CronUtil.getCron(next));
                eo.setStatus("NORMAL");
                scheduleJobService.saveEntity(eo);
                hrefEO.setNextRunDate(next);
            }
        }
        this.updateEntity(hrefEO);
    }

    @Override
    public void physDelEOs(Long[] ids) {
        for(Long id : ids) {
            ErrHrefEO hrefEO = this.getEntity(ErrHrefEO.class, id);
            pauseTask(id);
            scheduleJobService.delete(ScheduleJobEO.class,hrefEO.getSchedId());
        }
        errHrefDao.physDelEOs(ids);
    }

    @Override
    public int recheck(Long resultId) {
        HrefResultEO eo = hrefResultService.getEntity(HrefResultEO.class,resultId);
        int code = URLHelper.isConnect(eo.getUrl());
        eo.setRepCode(code);
        hrefResultService.updateEntity(eo);
        return code;
    }

}
