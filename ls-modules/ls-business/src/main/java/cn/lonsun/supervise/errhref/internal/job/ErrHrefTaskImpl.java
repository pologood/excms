package cn.lonsun.supervise.errhref.internal.job;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.supervise.column.internal.job.ColumnUpdateTaskImpl;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.errhref.internal.entity.ErrHrefEO;
import cn.lonsun.supervise.errhref.internal.service.IErrHrefService;
import cn.lonsun.supervise.errhref.internal.service.IHrefResultService;
import cn.lonsun.supervise.errhref.internal.util.ErrHrefCheckCrawler;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-4-8 8:06
 */
public class ErrHrefTaskImpl extends ISchedulerService {

    private static final IErrHrefService errHrefService = SpringContextHolder.getBean("errHrefService");
    private static final ICronConfService cronConfService = SpringContextHolder.getBean("cronConfService");
    private static final IHrefResultService hrefResultService = SpringContextHolder.getBean("hrefResultService");
    private static final IUserSiteOptService userSiteOptService = SpringContextHolder.getBean("userSiteOptService");
    private static final IRoleService roleServer = SpringContextHolder.getBean(IRoleService.class);
    private static final IRoleAssignmentService roleAssignmentService = SpringContextHolder.getBean(IRoleAssignmentService.class);
    private static final IScheduleJobService scheduleJobService = SpringContextHolder.getBean(IScheduleJobService.class);

    @Override
    public void execute(String json) {
        Long id = Long.parseLong(json);
        ErrHrefEO hrefEO = errHrefService.getEntity(ErrHrefEO.class, id);
        CronConfEO cronEO = cronConfService.getEntity(CronConfEO.class, hrefEO.getCronId());

        if(hrefEO.getRunStatus() == 1) { //如果任务是运行状态，处理业务
            hrefResultService.physDelEOs(id);//删除错链记录
            ErrHrefCheckCrawler crawl = new ErrHrefCheckCrawler(true, id);
            /*线程数*/
            crawl.setThreads(50);
            /*设置每次迭代中爬取数量的上限*/
            crawl.setTopN(500000000);
            crawl.setRetry(1);
            try {
                crawl.start(hrefEO.getDepth());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //设置任务上次应该执行的时间
        hrefEO.setPrevRunDate(hrefEO.getNextRunDate());
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Long schedId = hrefEO.getSchedId();
            if(null != schedId) {
            scheduleJobService.delete(ScheduleJobEO.class,schedId);
        }
        //设置模式为隔几天更新一次
            hrefEO.setNextRunDate(DateUtil.getDateBeforeOrAfter(hrefEO.getNextRunDate(), cronEO.getSpaceOfDay()));
            ScheduleJobEO eo = new ScheduleJobEO();
            eo.setSiteId(hrefEO.getSiteId());
            eo.setName(hrefEO.getTaskName());
            eo.setType("err_href");
            eo.setJson(json);
            eo.setClazz(ErrHrefTaskImpl.class.getName());
            eo.setStatus("NORMAL");
            eo.setCronExpression(CronUtil.getCron(hrefEO.getNextRunDate()));
            scheduleJobService.saveEntity(eo);
            Long scheduId = scheduleJobService.saveEntity(eo);
            hrefEO.setSchedId(scheduId);
        } else {
            String cronExpress = cronEO.getCronExpress();
            hrefEO.setNextRunDate(CronUtil.getNextCronTime(cronExpress,hrefEO.getPrevRunDate()));
        }
        Long num = hrefResultService.getCountByTaskId(hrefEO.getId());
        if(null != num) {
            hrefEO.setCheckResultNum(num);
        }
        errHrefService.updateEntity(hrefEO);


        List<CmsUserSiteOptEO> users = userSiteOptService.getRightsBySiteId(hrefEO.getSiteId());

        for(CmsUserSiteOptEO user : users) {
            MessageSystemEO eo = new MessageSystemEO();
            eo.setTitle("错链检测!");
            eo.setLink("/err/href/checkResult?taskId=" + hrefEO.getId() + "taskName=" + hrefEO.getTaskName());
            eo.setModeCode("hrefCheck");
            eo.setRecUserIds(user.getUserId() + "");
            eo.setContent("错链检测完成!");
            eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
            MessageSender.sendMessage(eo);
        }

        RoleEO roleEO = roleServer.getRoleByCode("super_admin");
        List<RoleAssignmentEO> assignmentEOs = roleAssignmentService.getRoleAssignments(roleEO.getRoleId());

        for(RoleAssignmentEO assignmentEO : assignmentEOs) {
            MessageSystemEO eo = new MessageSystemEO();
            eo.setTitle("错链检测!");
            eo.setLink("/err/href/checkResult?taskId=" + hrefEO.getId() + "taskName=" + hrefEO.getTaskName());
            eo.setModeCode("hrefCheck");
            eo.setRecUserIds(assignmentEO.getUserId() + "");
            eo.setContent("错链检测完成!");
            eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
            MessageSender.sendMessage(eo);
        }
    }
}
