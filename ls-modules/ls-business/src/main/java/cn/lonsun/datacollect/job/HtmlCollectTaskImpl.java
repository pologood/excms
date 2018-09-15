package cn.lonsun.datacollect.job;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.datacollect.entity.HtmlCollectTaskEO;
import cn.lonsun.datacollect.service.IHtmlCollectDataService;
import cn.lonsun.datacollect.service.impl.HtmlCollectTaskService;
import cn.lonsun.datacollect.util.CollectCrawler;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2016-10-12 11:24
 */
public class HtmlCollectTaskImpl extends ISchedulerService {

    private static final HtmlCollectTaskService htmlCollectTaskService = SpringContextHolder.getBean("htmlCollectTaskService");
    private static final ICronConfService cronConfService = SpringContextHolder.getBean("cronConfService");
    private static final IScheduleJobService scheduleJobService = SpringContextHolder.getBean(IScheduleJobService.class);
    private static final IHtmlCollectDataService htmlCollectDataService = SpringContextHolder.getBean("htmlCollectDataService");

    @Override
    public void execute(String json) {
        Long id = Long.parseLong(json);
        HtmlCollectTaskEO task = htmlCollectTaskService.getEntity(HtmlCollectTaskEO.class,id);
        CronConfEO cronEO = cronConfService.getEntity(CronConfEO.class, task.getCronId());
        if(task.getRunStatus() == 1) { //如果任务是运行状态，处理业务
            CollectCrawler collectCrawler = new CollectCrawler(true,id);
            /*尝试三次*/
            collectCrawler.setRetry(3);
            /*设置每次迭代中爬取数量的上限*/
            collectCrawler.setTopN(5000000);
            htmlCollectDataService.deleteByTaskId(id);

            try {
                collectCrawler.start(2);
            } catch (Exception e) {
                MessageSystemEO eo = new MessageSystemEO();
                eo.setTitle("网页采集任务!");
                eo.setLink("");
                eo.setModeCode("htmlCollect");
                eo.setRecUserIds(LoginPersonUtil.getUserId() + "");
                eo.setContent("采集任务[" + task.getTaskName() + "]采集异常,异常:" + e.getMessage());
                eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                MessageSender.sendMessage(eo);
            }
        }


        //设置任务上次应该执行的时间
        task.setPrevRunDate(task.getNextRunDate());
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Long schedId = task.getSchedId();
            if(null != schedId) {
                scheduleJobService.delete(ScheduleJobEO.class,schedId);
            }

            //设置模式为隔几天更新一次
            task.setNextRunDate(DateUtil.getDateBeforeOrAfter(task.getNextRunDate(), cronEO.getSpaceOfDay()));
            ScheduleJobEO eo = new ScheduleJobEO();
            eo.setSiteId(task.getSiteId());
            eo.setName(task.getTaskName());
            eo.setType("html_collect_cron");
            eo.setJson(json);
            eo.setClazz(HtmlCollectTaskImpl.class.getName());
            eo.setStatus("NORMAL");
            eo.setCronExpression(CronUtil.getCron(task.getNextRunDate()));
            Long scheduId = scheduleJobService.saveEntity(eo);
            task.setSchedId(scheduId);
        } else {
            String cronExpress = cronEO.getCronExpress();
            task.setNextRunDate(CronUtil.getNextCronTime(cronExpress,task.getPrevRunDate()));
        }

        htmlCollectTaskService.updateEntity(task);
    }
}
