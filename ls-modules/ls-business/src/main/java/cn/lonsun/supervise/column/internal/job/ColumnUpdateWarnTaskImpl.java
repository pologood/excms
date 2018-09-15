package cn.lonsun.supervise.column.internal.job;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.supervise.column.internal.service.IColumnUpdateWarnService;
import cn.lonsun.supervise.column.internal.service.IUnreplyGuestService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnResultEO;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateWarnEO;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.columnupdate.internal.entity.UnreplyGuestEO;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.util.ColumnUtil;
import org.quartz.Trigger;

import java.util.*;

/**
 * @author gu.fei
 * @version 2016-4-8 8:06
 */
public class ColumnUpdateWarnTaskImpl extends ISchedulerService {

    private static final IColumnUpdateWarnService columnUpdateWarnService = SpringContextHolder.getBean("columnUpdateWarnService");
    private static final IBaseContentService baseContentService = SpringContextHolder.getBean("baseContentService");
    private static final IGuestBookService guestBookService = SpringContextHolder.getBean("guestBookService");
    private static final IMessageBoardService messageBoardService = SpringContextHolder.getBean("messageBoardService");
    private static final IColumnConfigService columnConfigService = SpringContextHolder.getBean("columnConfigService");
    private static final IRoleService roleServer = SpringContextHolder.getBean(IRoleService.class);
    private static final IRoleAssignmentService roleAssignmentService = SpringContextHolder.getBean(IRoleAssignmentService.class);
    private static final IScheduleJobService scheduleJobService = SpringContextHolder.getBean(IScheduleJobService.class);
    private static final IUnreplyGuestService unreplyGuestService = SpringContextHolder.getBean("unreplyGuestService");

    @Override
    public void execute(String json) {
        Long id = Long.parseLong(json);
        ColumnUpdateWarnEO warnEO = columnUpdateWarnService.getEntity(ColumnUpdateWarnEO.class, id);
        List<ColumnResultEO> results = new ArrayList<ColumnResultEO>();
        ScheduleJobEO schedule = scheduleJobService.getEntity(ScheduleJobEO.class,warnEO.getSchedId());
        if(schedule.getStatus().equals(Trigger.TriggerState.NORMAL.toString())) { //如果任务是运行状态，处理业务
            //上次应该运行时间
            Date shouldPrev = warnEO.getPrevShouldRunDate();
            //这次执行时间
            Date next = warnEO.getNextRunDate();
            Long plancount = warnEO.getPlanUpdateNum();
            if(warnEO.getTaskType().equals(BaseContentEO.TypeCode.articleNews.toString())) {
                Long updatecount = baseContentService.getCountByCondition(warnEO.getColumnId(),1,shouldPrev,next, AMockEntity.RecordStatus.Normal.toString());
                if( (updatecount != null) && (updatecount.intValue() < plancount.intValue())) {
                    warnEO.setRealUpdateNum(updatecount);
                    warnEO.setCheckResult("栏目计划更新:" + plancount + " ,实际更新:" + updatecount);
                    warnEO.setIsQualified(1);
                }
            } else if(warnEO.getTaskType().equals(BaseContentEO.TypeCode.guestBook.toString()) ||
                            warnEO.getTaskType().equals(BaseContentEO.TypeCode.messageBoard.toString())) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("indicatorId",warnEO.getColumnId());
                map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
                List<ColumnConfigEO> columnConfigEOs = columnConfigService.getEntities(ColumnConfigEO.class,map);
                List<GuestBookEditVO> gbs = null;
                List<MessageBoardEditVO> msgs = null;
                String typeCode = null;
                if(null != columnConfigEOs && !columnConfigEOs.isEmpty()) {
                    typeCode = columnConfigEOs.get(0).getColumnTypeCode();
                    if(null != typeCode && typeCode.equals(BaseContentEO.TypeCode.guestBook.toString())) {
                        gbs = guestBookService.getUnReply(warnEO.getColumnId(),warnEO.getUnreplyDateNum().intValue());
                    } else if(null != typeCode && typeCode.equals(BaseContentEO.TypeCode.messageBoard.toString())){
                        msgs = messageBoardService.getUnReply(warnEO.getColumnId(),warnEO.getUnreplyDateNum().intValue());
                    }
                }

                String columnName = null;
                try {
                    columnName = ColumnUtil.getColumnName(warnEO.getColumnId(),warnEO.getSiteId());
                } catch (Exception e) {
                    columnName = "";
                }
                List<UnreplyGuestEO> urges = new ArrayList<UnreplyGuestEO>();
                if(null != gbs && gbs.size() > 0) {
                    for(GuestBookEditVO eo : gbs) {
                        UnreplyGuestEO guest = new UnreplyGuestEO();
                        guest.setColumnId(warnEO.getColumnId());
                        guest.setColumnName(columnName);
                        guest.setContentId(eo.getBaseContentId());
                        guest.setGuestId(eo.getId());
                        guest.setGuestType(typeCode);
                        guest.setTitle(eo.getGuestBookContent());
                        guest.setType(eo.getType());
                        guest.setPersonName(eo.getPersonName());
                        guest.setSiteId(eo.getSiteId());
                        guest.setAddDate(eo.getAddDate());
                        guest.setReceiveId(eo.getReceiveId());
                        guest.setReceiveName(eo.getReceiveName());
                        urges.add(guest);
                    }
                }

                if(null != msgs && msgs.size() > 0) {
                    for(MessageBoardEditVO eo : msgs) {
                        UnreplyGuestEO guest = new UnreplyGuestEO();
                        guest.setColumnId(warnEO.getColumnId());
                        guest.setColumnName(columnName);
                        guest.setContentId(eo.getBaseContentId());
                        guest.setGuestId(eo.getId());
                        guest.setGuestType(typeCode);
                        guest.setTitle(eo.getMessageBoardContent());
                        guest.setType(eo.getType());
                        guest.setPersonName(eo.getPersonName());
                        guest.setSiteId(eo.getSiteId());
                        guest.setAddDate(eo.getAddDate());
                        guest.setReceiveId(eo.getReceiveUnitId());
                        guest.setReceiveName(eo.getReceiveUnitName());
                        urges.add(guest);
                    }
                }

                //物理删除对应栏目上次检查结果
                unreplyGuestService.delete(warnEO.getColumnId());
                unreplyGuestService.saveEntities(urges);
                if(urges.size() > 0) {
                    warnEO.setIsQualified(1);
                }
                warnEO.setUnreplyGuestNum(Long.valueOf(urges.size()));
                warnEO.setCheckResult(warnEO.getColumnName() + "中有" + urges.size() + "个超过" + warnEO.getUnreplyDateNum() + "天未回复留言!");
            }
            //设置任务上次执行时间
            warnEO.setPrevRunDate(warnEO.getNextRunDate());
        }

        //设置任务上次应该执行的时间
        warnEO.setPrevShouldRunDate(warnEO.getNextRunDate());
        if(warnEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Long schedId = warnEO.getSchedId();
            if(null != schedId) {
                scheduleJobService.delete(ScheduleJobEO.class,schedId);
            }
            //设置模式为隔几天更新一次
            warnEO.setNextRunDate(DateUtil.getDateBeforeOrAfter(warnEO.getNextRunDate(), warnEO.getSpaceOfDay()));
            ScheduleJobEO eo = new ScheduleJobEO();
            eo.setSiteId(warnEO.getSiteId());
            eo.setName(warnEO.getColumnName());
            eo.setType("column_update");
            eo.setJson(json);
            eo.setClazz(ColumnUpdateWarnTaskImpl.class.getName());
            eo.setStatus("NORMAL");
            eo.setCronExpression(CronUtil.getCron(warnEO.getNextRunDate()));
            Long scheduId = scheduleJobService.saveEntity(eo);
            warnEO.setSchedId(scheduId);
        } else {
            String cronExpress = warnEO.getCronExpress();
            warnEO.setNextRunDate(CronUtil.getNextCronTime(cronExpress,warnEO.getPrevRunDate()));
        }

        columnUpdateWarnService.updateEntity(warnEO);

        if(!results.isEmpty()) {
            RoleEO roleEO = roleServer.getRoleByCode(RoleEO.RoleCode.super_admin.toString());
            List<RoleAssignmentEO> assignmentEOs = roleAssignmentService.getRoleAssignments(roleEO.getRoleId());

            for(RoleAssignmentEO assignmentEO : assignmentEOs) {
                MessageSystemEO eo = new MessageSystemEO();
                eo.setTitle("栏目更新预警!");
                eo.setModeCode("columnUpdate");
                eo.setRecUserIds(assignmentEO.getUserId() + "");
                eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                if(warnEO.getTaskType().equals(BaseContentEO.TypeCode.articleNews.toString())) {
                    eo.setContent("有未按时更新的栏目!");
                } else if(warnEO.getTaskType().equals(BaseContentEO.TypeCode.guestBook.toString()) ||
                        warnEO.getTaskType().equals(BaseContentEO.TypeCode.messageBoard.toString())) {
                    eo.setContent("有未按时回复的留言!");
                }
                MessageSender.sendMessage(eo);
            }
        }
    }
}
