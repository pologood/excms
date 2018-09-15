package cn.lonsun.supervise.column.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.supervise.column.internal.dao.IColumnUpdateWarnDao;
import cn.lonsun.supervise.column.internal.job.ColumnUpdateWarnTaskImpl;
import cn.lonsun.supervise.column.internal.service.IColumnUpdateWarnService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateWarnEO;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.supervise.vo.SupervisePageVO;
import cn.lonsun.util.LoginPersonUtil;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2016-4-5 10:48
 */
@Service
public class ColumnUpdateWarnService extends MockService<ColumnUpdateWarnEO> implements IColumnUpdateWarnService {

    private static Logger logger = LoggerFactory.getLogger(ColumnUpdateWarnService.class);

    @Autowired
    private IScheduleJobService scheduleJobService;

    @Autowired
    private IColumnUpdateWarnDao columnUpdateWarnDao;

    @Override
    public Pagination getPageTasks(SupervisePageVO vo) {
        return columnUpdateWarnDao.getPageTasks(vo);
    }

    @Override
    public void saveOrUpdateColumnUpdateWarnEntity(ColumnUpdateWarnEO eo) {
        Long id;
        if(null == eo.getId()) {
            eo.setSiteId(LoginPersonUtil.getSiteId());
            try {
                int c = 20;
                //获取父级栏目ID结构
                String str = null;
                ColumnMgrEO columnMgr = CacheHandler.getEntity(ColumnMgrEO.class,eo.getColumnId());
                while (null != columnMgr && !columnMgr.getType().equals(IndicatorEO.Type.CMS_Site.toString())) {
                    //防止死循环
                    if(c-- <= 0) {
                        break;
                    }
                    if(null != columnMgr) {
                        if(null == str) {
                            str = String.valueOf(columnMgr.getParentId());
                        } else {
                            str = String.valueOf(columnMgr.getParentId()) + ">" + str;
                        }
                    }
                    columnMgr = CacheHandler.getEntity(ColumnMgrEO.class,columnMgr.getParentId());
                }
                eo.setParentColumnId(str);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("获取父级栏目结构失败!");
            }
            id = this.saveEntity(eo);
        } else {
            ColumnUpdateWarnEO update = this.getEntity(ColumnUpdateWarnEO.class,eo.getId());
            update.setTimeMode(eo.getTimeMode());
            update.setCronExpress(eo.getCronExpress());
            update.setStartDate(eo.getStartDate());
            update.setSpaceOfDay(eo.getSpaceOfDay());
            update.setPlanUpdateNum(eo.getPlanUpdateNum());
            update.setUnreplyDateNum(eo.getUnreplyDateNum());
            update.setRunStatus(eo.getRunStatus());
            update.setCronDesc(eo.getCronDesc());
            update.setCheckResult("");
            this.updateEntity(update);
            id = eo.getId();
        }

        //删除老的执行任务
        ColumnUpdateWarnEO ueo = this.getEntity(ColumnUpdateWarnEO.class,id);
        Long deleteschedId = ueo.getSchedId();
        if(null != ueo.getSchedId()) {
            scheduleJobService.delete(ScheduleJobEO.class,deleteschedId);
        }
        if(eo.getRunStatus().equals(Trigger.TriggerState.NORMAL.toString())) {
            ScheduleJobEO schedule = new ScheduleJobEO();
            schedule.setSiteId(ueo.getSiteId());
            schedule.setName(ueo.getColumnName());
            schedule.setType("column_update");
            schedule.setJson(String.valueOf(id));
            schedule.setClazz(ColumnUpdateWarnTaskImpl.class.getName());
            schedule.setStatus("NORMAL");
            //定时模式是隔天模式
            if(ueo.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
                Date next = DateUtil.getDateBeforeOrAfter(ueo.getStartDate(), ueo.getSpaceOfDay());
                Date now = new Date();
                while(next.getTime() < now.getTime()) {
                    next = DateUtil.getDateBeforeOrAfter(next, ueo.getSpaceOfDay());
                    logger.info("当前时间:" + now.getTime());
                    logger.info("下次执行时间:" + next.getTime());
                }
                //设置下次执行时间
                ueo.setNextRunDate(next);
                //设置应该执行时间
                ueo.setPrevShouldRunDate(ueo.getStartDate());
                schedule.setCronExpression(CronUtil.getCron(next));
            } else {
                //自定义cron表达式
                schedule.setCronExpression(ueo.getCronExpress());
                Date next = CronUtil.getNextCronTime(ueo.getCronExpress());
                ueo.setNextRunDate(next);
                ueo.setPrevShouldRunDate(new Date());
            }

            Long schedId = scheduleJobService.saveEntity(schedule);
            ueo.setSchedId(schedId);
            this.updateEntity(ueo);
        }
    }
}
