package cn.lonsun.supervise;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.supervise.column.internal.job.ColumnUpdateWarnTaskImpl;
import cn.lonsun.supervise.column.internal.service.IColumnUpdateWarnService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateWarnEO;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.supervise.vo.SupervisePageVO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author gu.fei
 * @version 2017-04-05 9:39
 */
@Controller
@RequestMapping(value = "/column/update/warn")
public class ColumnUpdateWarnController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ColumnUpdateWarnController.class);

    private static final String FILE_BASE = "/supervise/columnupdate/warn";

    @Autowired
    private IColumnUpdateWarnService columnUpdateWarnService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IScheduleJobService scheduleJobService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/index";
    }

    @ResponseBody
    @RequestMapping("getPageTask")
    public Object getPageTask(SupervisePageVO vo) {
        Pagination page = columnUpdateWarnService.getPageTasks(vo);
        List<ColumnUpdateWarnEO> data = (List<ColumnUpdateWarnEO>)page.getData();
        for(ColumnUpdateWarnEO eo : data) {
            ScheduleJobEO schedule = scheduleJobService.getEntity(ScheduleJobEO.class,eo.getSchedId());
            if(null != schedule) {
                eo.setRunStatus(schedule.getStatus());
            } else {
                eo.setRunStatus(Trigger.TriggerState.NONE.toString());
            }
            eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(),eo.getSiteId()));
        }
        page.setData(data);
        return page;
    }

    /**
     * 获取栏目树
     * @return
     */
    @ResponseBody
    @RequestMapping("getColumnTree")
    public Object getColumnTree() {

        String[] codes = {
            BaseContentEO.TypeCode.articleNews.toString(), //文字新闻类
            BaseContentEO.TypeCode.guestBook.toString(), //留言模块
            BaseContentEO.TypeCode.messageBoard.toString()  //留言模块
        };

        return columnConfigService.getColumns(Arrays.asList(codes), LoginPersonUtil.getSiteId());
    }

    /**
     * 保存配置信息
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("saveOrUpdateColumnUpdateWarnEntity")
    public Object saveOrUpdateColumnUpdateWarnEntity(ColumnUpdateWarnEO eo) {
        columnUpdateWarnService.saveOrUpdateColumnUpdateWarnEntity(eo);
        return ajaxOk();
    }

    /**
     * 获取配置信息
     * @return
     */
    @ResponseBody
    @RequestMapping("getColumnUpdateWarnEntity")
    public Object getColumnUpdateWarnEntity(Long columnId) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("columnId",columnId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        ColumnUpdateWarnEO eo = columnUpdateWarnService.getEntity(ColumnUpdateWarnEO.class,params);
        if(null != eo && null != eo.getSchedId()) {
            ScheduleJobEO schedule = scheduleJobService.getEntity(ScheduleJobEO.class,eo.getSchedId());
            if(null != schedule) {
                eo.setRunStatus(schedule.getStatus());
            } else {
                eo.setRunStatus(Trigger.TriggerState.NONE.toString());
            }
        }
        return getObject(eo);
    }

    /**
     * 暂停任务
     * @param columnId
     * @return
     */
    @ResponseBody
    @RequestMapping("pauseTask")
    public Object pauseTask(Long columnId) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("columnId",columnId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        ColumnUpdateWarnEO eo = columnUpdateWarnService.getEntity(ColumnUpdateWarnEO.class,params);
        if(null != eo && null != eo.getSchedId()) {
            if(eo.getTimeMode().equals(CronConfEO.TimeMode.day)) {
                //时间间隔任务则删除任务
                scheduleJobService.delete(ScheduleJobEO.class,eo.getSchedId());
            } else {
                //普通cron循环表达式则暂停任务
                ScheduleJobEO schedule = scheduleJobService.getEntity(ScheduleJobEO.class,eo.getSchedId());
                if(null != schedule) {
                    scheduleJobService.pauseJob(schedule);
                }
            }
        }

        return ajaxOk();
    }

    /**
     * 启动任务
     * @param columnId
     * @return
     */
    @ResponseBody
    @RequestMapping("runTask")
    public Object runTask(Long columnId) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("columnId",columnId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        ColumnUpdateWarnEO eo = columnUpdateWarnService.getEntity(ColumnUpdateWarnEO.class,params);

        ScheduleJobEO schedule = new ScheduleJobEO();
        schedule.setSiteId(eo.getSiteId());
        schedule.setName(eo.getColumnName());
        schedule.setType("column_update");
        schedule.setJson(String.valueOf(eo.getId()));
        schedule.setClazz(ColumnUpdateWarnTaskImpl.class.getName());
        schedule.setStatus("NORMAL");

        //防止改变数据导致id变化，这里增加引用
        Long deleteschedid = eo.getSchedId();
        if(eo.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            if(null != eo.getSchedId()) {
                scheduleJobService.delete(ScheduleJobEO.class,deleteschedid);
            }
            Date next = DateUtil.getDateBeforeOrAfter(eo.getStartDate(), eo.getSpaceOfDay());
            Date now = new Date();
            while(next.getTime() < now.getTime()) {
                next = DateUtil.getDateBeforeOrAfter(next, eo.getSpaceOfDay());
                logger.info("当前时间:" + now.getTime());
                logger.info("下次执行时间:" + next.getTime());
            }
            //设置下次执行时间
            eo.setNextRunDate(next);
            //设置应该执行时间
            eo.setPrevShouldRunDate(eo.getStartDate());
            schedule.setCronExpression(CronUtil.getCron(next));
            Long schedId = scheduleJobService.saveEntity(schedule);
            eo.setSchedId(schedId);
        } else {
            //普通cron循环表达式则暂停任务
            ScheduleJobEO uschedule = scheduleJobService.getEntity(ScheduleJobEO.class,eo.getSchedId());
            if(null != uschedule) {
                scheduleJobService.resumeJob(uschedule);
            } else {
                //自定义cron表达式
                schedule.setCronExpression(eo.getCronExpress());
                Date next = CronUtil.getNextCronTime(eo.getCronExpress());
                eo.setNextRunDate(next);
                eo.setPrevShouldRunDate(new Date());
                Long schedId = scheduleJobService.saveEntity(schedule);
                eo.setSchedId(schedId);
            }
        }
        columnUpdateWarnService.updateEntity(eo);
        return ajaxOk();
    }
}
