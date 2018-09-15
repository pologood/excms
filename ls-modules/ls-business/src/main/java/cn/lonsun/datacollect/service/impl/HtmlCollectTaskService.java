package cn.lonsun.datacollect.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IHtmlCollectContentDao;
import cn.lonsun.datacollect.dao.IHtmlCollectTaskDao;
import cn.lonsun.datacollect.entity.HtmlCollectContentEO;
import cn.lonsun.datacollect.entity.HtmlCollectTaskEO;
import cn.lonsun.datacollect.job.HtmlCollectTaskImpl;
import cn.lonsun.datacollect.service.IHtmlCollectTaskService;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2016-1-21 14:36
 */
@Service("htmlCollectTaskService")
public class HtmlCollectTaskService extends MockService<HtmlCollectTaskEO> implements IHtmlCollectTaskService {

    @Autowired
    private IHtmlCollectTaskDao htmlCollectTaskDao;

    @Autowired
    private IHtmlCollectContentDao htmlCollectContentDao;

    @Autowired
    private ICronConfService cronConfService;

    @Autowired
    private IScheduleJobService scheduleJobService;

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        return htmlCollectTaskDao.getPageEOs(vo);
    }

    @Override
    public void saveEO(HtmlCollectTaskEO eo) {
        Long siteId = LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        this.saveEntity(eo);
    }

    @Override
    public void updateEO(HtmlCollectTaskEO eo) {
        HtmlCollectTaskEO oeo = this.getEntity(HtmlCollectTaskEO.class,eo.getId());
        oeo.setTaskName(eo.getTaskName());
        oeo.setColumnId(eo.getColumnId());
        oeo.setcSiteId(eo.getcSiteId());
        oeo.setWebDomain(eo.getWebDomain());
        oeo.setDefaultUrl(eo.getDefaultUrl());
        oeo.setRegexUrl(eo.getRegexUrl());
        oeo.setTargetBeginDom(eo.getTargetBeginDom());
        oeo.setTargetEndDom(eo.getTargetEndDom());
        oeo.setRegexHref(eo.getRegexHref());
        oeo.setPageType(eo.getPageType());
        oeo.setPageBeginNumber(eo.getPageBeginNumber());
        oeo.setPageEndNumber(eo.getPageEndNumber());
        oeo.setZeroFill(eo.getZeroFill());
        oeo.setPageBeginChar(eo.getPageBeginChar());
        oeo.setPageEndChar(eo.getPageEndChar());
        oeo.setPageList(eo.getPageList());
        oeo.setPageSort(eo.getPageSort());
        oeo.setFilterTag(eo.getFilterTag());
        oeo.setFilterRegexUrl(eo.getFilterRegexUrl());
        oeo.setCron(eo.getCron());
        oeo.setEmployType(eo.getEmployType());
        oeo.setThreads(eo.getThreads());
        oeo.setIsPush(eo.getIsPush());
        oeo.setPushUrlFlag(eo.getPushUrlFlag());
        this.updateEntity(oeo);
    }

    @Override
    public void copyEO(HtmlCollectTaskEO eo) {
        Long taskId = eo.getId();
        Long siteId = LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        this.saveEntity(eo);
        List<HtmlCollectContentEO> eos = htmlCollectContentDao.getByTaskId(taskId);
        if(null != eos) {
            for(HtmlCollectContentEO ceo : eos) {
                HtmlCollectContentEO neo = new HtmlCollectContentEO();
                neo.setTaskId(eo.getId());
                neo.setName(ceo.getName());
                neo.setColumnName(ceo.getColumnName());
                neo.setRegexBegin(ceo.getRegexBegin());
                neo.setRegexEnd(ceo.getRegexEnd());
                neo.setRegexFilter(ceo.getRegexFilter());
                neo.setDefaultValue(ceo.getDefaultValue());
                neo.setTagId(ceo.getTagId());
                neo.setType(ceo.getType());
                htmlCollectContentDao.save(neo);
            }
        }
    }

    @Override
    public void deleteEOs(Long[] ids) {
        if(null != ids) {
            for(Long id : ids) {
                HtmlCollectTaskEO eo = htmlCollectTaskDao.getEntity(HtmlCollectTaskEO.class,id);
                scheduleJobService.delete(ScheduleJobEO.class,eo.getSchedId());
            }
        }
        htmlCollectTaskDao.deleteEOs(ids);
    }

    @Override
    public void saveTaskCron(CronConfEO cronEO, Long taskId) {
        Long cronId = cronConfService.saveEntity(cronEO);
        HtmlCollectTaskEO task = this.getEntity(HtmlCollectTaskEO.class,taskId);
        task.setCronId(cronId);
        task.setCronDesc(cronEO.getCronDesc());
        ScheduleJobEO eo = new ScheduleJobEO();
        eo.setSiteId(task.getSiteId());
        eo.setName(task.getTaskName());
        eo.setType("html_collect_cron");
        eo.setJson(String.valueOf(taskId));
        eo.setClazz(HtmlCollectTaskImpl.class.getName());
        eo.setStatus("NORMAL");
        //定时模式是隔天模式
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
            //设置下次执行时间
            task.setNextRunDate(next);
            //设置应该执行时间
            task.setPrevRunDate(cronEO.getStartDate());
            eo.setCronExpression(CronUtil.getCron(next));
        } else {
            //自定义cron表达式
            eo.setCronExpression(cronEO.getCronExpress());
            Date next = CronUtil.getNextCronTime(cronEO.getCronExpress());
            task.setNextRunDate(next);
            task.setPrevRunDate(new Date());
        }

        Long schedId = scheduleJobService.saveEntity(eo);
        task.setSchedId(schedId);
        this.updateEntity(task);

    }

    @Override
    public void updateTaskCron(CronConfEO cronEO, Long taskId) {
        HtmlCollectTaskEO task = this.getEntity(HtmlCollectTaskEO.class,taskId);
        task.setCronDesc(cronEO.getCronDesc());
        CronConfEO oceo = cronConfService.getEntity(CronConfEO.class, cronEO.getId());
        //如果定时改变，则需要重新配置定时任务
        oceo.setSpaceOfDay(cronEO.getSpaceOfDay());
        oceo.setTimeMode(cronEO.getTimeMode());
        oceo.setStartDate(cronEO.getStartDate());
        oceo.setCronExpress(cronEO.getCronExpress());
        cronConfService.updateEntity(oceo);

        //删除老的定时任务
        scheduleJobService.delete(ScheduleJobEO.class, task.getSchedId());
        ScheduleJobEO eo = new ScheduleJobEO();
        eo.setSiteId(task.getSiteId());
        eo.setName(task.getTaskName());
        eo.setType("html_collect_cron");
        eo.setJson(String.valueOf(task.getId()));
        eo.setClazz(HtmlCollectTaskImpl.class.getName());
        eo.setStatus("NORMAL");
        //定时方式为隔天方式
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
            //循环获取下次应该执行的时间
            while(next.getTime() < new Date().getTime()) {
                next = DateUtil.getDateBeforeOrAfter(next, cronEO.getSpaceOfDay());
            }
            task.setNextRunDate(next);
            task.setPrevRunDate(DateUtil.getDateBeforeOrAfter(next,-cronEO.getSpaceOfDay()));
            eo.setCronExpression(CronUtil.getCron(next));
        } else {
            //自定义cron模式
            Date next = CronUtil.getNextCronTime(cronEO.getCronExpress());
            task.setNextRunDate(next);
            task.setPrevRunDate(new Date());

            eo.setCronExpression(cronEO.getCronExpress());
        }

        Long schedId = scheduleJobService.saveEntity(eo);
        task.setSchedId(schedId);
        this.updateEntity(task);
    }

    @Override
    public void pauseTask(Long taskId) {
        HtmlCollectTaskEO task = this.getEntity(HtmlCollectTaskEO.class, taskId);
        task.setRunStatus(0);
        this.updateEntity(task);
        scheduleJobService.delete(ScheduleJobEO.class, task.getSchedId());
    }

    @Override
    public void resumeTask(Long taskId) {
        HtmlCollectTaskEO task = this.getEntity(HtmlCollectTaskEO.class, taskId);
        task.setRunStatus(1);
        CronConfEO cronEO = cronConfService.getEntity(CronConfEO.class,task.getCronId());
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            if(task.getNextRunDate().getTime() < new Date().getTime()) {
                scheduleJobService.delete(ScheduleJobEO.class, task.getSchedId());
                ScheduleJobEO eo = new ScheduleJobEO();
                eo.setSiteId(task.getSiteId());
                eo.setName(task.getTaskName());
                eo.setType("html_collect_cron");
                eo.setJson(String.valueOf(task.getId()));
                eo.setClazz(HtmlCollectTaskImpl.class.getName());
                Date next = DateUtil.getDateBeforeOrAfter(cronEO.getStartDate(), cronEO.getSpaceOfDay());
                //循环获取下次应该执行的时间
                while(next.getTime() < new Date().getTime()) {
                    next = DateUtil.getDateBeforeOrAfter(next, cronEO.getSpaceOfDay());
                }
                eo.setCronExpression(CronUtil.getCron(next));
                eo.setStatus("NORMAL");
                scheduleJobService.saveEntity(eo);

                task.setNextRunDate(next);
                task.setPrevRunDate(DateUtil.getDateBeforeOrAfter(next, -cronEO.getSpaceOfDay()));
            }
        }

        this.updateEntity(task);
    }

}
