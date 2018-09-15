package cn.lonsun.supervise.column.internal.job;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.supervise.column.internal.service.IColumnResultService;
import cn.lonsun.supervise.column.internal.service.IColumnUpdateService;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.column.internal.service.IUnreplyGuestService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnResultEO;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateEO;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.columnupdate.internal.entity.UnreplyGuestEO;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import cn.lonsun.util.ColumnUtil;

import java.util.*;

/**
 * @author gu.fei
 * @version 2016-4-8 8:06
 */
public class ColumnUpdateTaskImpl extends ISchedulerService {

    private static final IColumnUpdateService columnUpdateService = SpringContextHolder.getBean("columnUpdateService");
    private static final ICronConfService cronConfService = SpringContextHolder.getBean("cronConfService");
    private static final IBaseContentService baseContentService = SpringContextHolder.getBean("baseContentService");
    private static final IGuestBookService guestBookService = SpringContextHolder.getBean("guestBookService");
    private static final IUserSiteOptService userSiteOptService = SpringContextHolder.getBean("userSiteOptService");
    private static final IColumnConfigService columnConfigService = SpringContextHolder.getBean("columnConfigService");
    private static final IRoleService roleServer = SpringContextHolder.getBean(IRoleService.class);
    private static final IRoleAssignmentService roleAssignmentService = SpringContextHolder.getBean(IRoleAssignmentService.class);
    private static final IScheduleJobService scheduleJobService = SpringContextHolder.getBean(IScheduleJobService.class);
    private static final IUnreplyGuestService unreplyGuestService = SpringContextHolder.getBean("unreplyGuestService");

    @Override
    public void execute(String json) {
        Long id = Long.parseLong(json);
        ColumnUpdateEO updateEO = columnUpdateService.getEntity(ColumnUpdateEO.class, id);
        List<ColumnResultEO> results = new ArrayList<ColumnResultEO>();
        CronConfEO cronEO = cronConfService.getEntity(CronConfEO.class, updateEO.getCronId());

        if(updateEO.getRunStatus() == 1) { //如果任务是运行状态，处理业务
            //删除上次检测结果
            columnResultService.physDelEOs(id);
            //检测的栏目
            String columnIds = updateEO.getColumnIds();
            String cSiteIds = updateEO.getcSiteIds();
            //上次应该运行时间
            Date shouldPrev = updateEO.getPrevShouldRunDate();
            //这次执行时间
            Date next = updateEO.getNextRunDate();
            Long plancount = updateEO.getColumnUpdateNum();
            List<Long> list = StringUtils.getListWithLong(columnIds, ",");

            if(updateEO.getTaskType().equals(ColumnUpdateEO.TastkType.article.toString())) {
                List<Long> lists = StringUtils.getListWithLong(cSiteIds,",");
                int c = 0;
                for(Long columnId : list) {
                    Long updatecount = baseContentService.getCountByCondition(columnId,1,shouldPrev,next, AMockEntity.RecordStatus.Normal.toString());
                    if( (updatecount != null) && (updatecount.intValue() < plancount.intValue())) {
                        String columnName = null;
                        try {
                            columnName = ColumnUtil.getColumnName(columnId,lists.get(c));
                        } catch (Exception e) {
                            columnName = "";
                        }
                        ColumnResultEO resultEO = new ColumnResultEO();
                        resultEO.setTaskId(id);
                        resultEO.setColumnId(columnId);
                        resultEO.setcSiteId(lists.get(c));
                        resultEO.setColumnName(columnName);
                        resultEO.setTaskType(updateEO.getTaskType());
                        resultEO.setPlanNum(plancount);
                        resultEO.setUpdateNum(updatecount);
                        columnResultService.saveEntity(resultEO);
                        results.add(resultEO);
                        c++;
                    }
                }
                updateEO.setCheckResultNum(Long.valueOf(c));
                updateEO.setCheckResult("<font color=\"red\">(" + c + ")</font>个栏目未及时更新");
            } else if(updateEO.getTaskType().equals(ColumnUpdateEO.TastkType.guest.toString())) {
                List<Long> lists = StringUtils.getListWithLong(cSiteIds,",");
                int c = 0;
                for(Long columnId : list) {
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put("indicatorId",columnId);
                    map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
                    List<ColumnConfigEO> columnConfigEOs = columnConfigService.getEntities(ColumnConfigEO.class,map);
                    List<GuestBookEditVO> gbs = null;
                    String typeCode = null;
                    if(null != columnConfigEOs && !columnConfigEOs.isEmpty()) {
                        typeCode = columnConfigEOs.get(0).getColumnTypeCode();
                        if(null != typeCode && typeCode.equals(BaseContentEO.TypeCode.guestBook.toString())) {
                            gbs = guestBookService.getUnReply(columnId,updateEO.getColumnUpdateNum().intValue());
                        } else if(null != typeCode && typeCode.equals(BaseContentEO.TypeCode.messageBoard.toString())){
                            //这里增加代办留言中心模块代码
                        }
                    }
                    if(null != gbs && gbs.size() > 0) {
                        String columnName = null;
                        try {
                            columnName = ColumnUtil.getColumnName(columnId,lists.get(c));
                        } catch (Exception e) {
                            columnName = "";
                        }

                        ColumnResultEO resultEO = new ColumnResultEO();
                        resultEO.setTaskId(id);
                        resultEO.setColumnId(columnId);
                        resultEO.setcSiteId(lists.get(c));
                        resultEO.setColumnName(columnName);
                        resultEO.setTaskType(updateEO.getTaskType());
                        resultEO.setGuestNum(Long.valueOf(gbs.size()));
                        columnResultService.saveEntity(resultEO);
                        results.add(resultEO);
                        c++;

                        List<UnreplyGuestEO> urges = new ArrayList<UnreplyGuestEO>();
                        for(GuestBookEditVO eo : gbs) {
                            UnreplyGuestEO guest = new UnreplyGuestEO();
                            guest.setColumnId(columnId);
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
                        //物理删除对应栏目上次检查结果
                        unreplyGuestService.delete(columnId);
                        unreplyGuestService.saveEntities(urges);
                    }
                }
                updateEO.setCheckResult("<font color=\"red\">(" + c + ")</font>个栏目有超过" + updateEO.getColumnUpdateNum() + "天未回复留言!");
            } else if(updateEO.getTaskType().equals(ColumnUpdateEO.TastkType.publicinfo.toString())) {
                List<String> publicNames = StringUtils.getListWithString(updateEO.getPublicNames(),",");

                //信息公开单位ID
                List<Long> organIds = StringUtils.getListWithLong(updateEO.getOrganIds(),",");

                //信息公开类型
                List<String> publicTypes = StringUtils.getListWithString(updateEO.getPublicTypes(),",");
                //信息公开检测
                int c = 0;
                for(Long columnId : list) {
                    /*
                    * 这里增加查询信息公开更新数量接口
                    * */
                    Long updatecount = 0L;

                    Map<Long,String> map = new HashMap<Long, String>();
                    DataDictEO dict = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, PublicContentEO.PUBLIC_ITEM_INSTITUTION_CODE);
                    if (null == dict) {
                        List<DataDictItemEO> dictList = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dict.getDictId());
                        if(null != dictList) {
                            for(DataDictItemEO eo : dictList) {
                                map.put(eo.getItemId(),eo.getName());
                            }
                        }
                    }

                    if( (updatecount != null) && (updatecount.intValue() < plancount.intValue())) {
                        ColumnResultEO resultEO = new ColumnResultEO();
                        resultEO.setTaskId(id);
                        resultEO.setColumnId(columnId);
                        resultEO.setColumnName(null != publicNames?publicNames.get(c):"");
                        resultEO.setTaskType(updateEO.getTaskType());
                        resultEO.setPlanNum(plancount);
                        resultEO.setUpdateNum(updatecount);
                        columnResultService.saveEntity(resultEO);
                        results.add(resultEO);
                        c++;
                    }
                }
                updateEO.setCheckResult("<font color=\"red\">(" + c + ")</font>个信息公开未及时更新");
            }
            //设置任务上次执行时间
            updateEO.setPrevRunDate(updateEO.getNextRunDate());
        }

        //设置任务上次应该执行的时间
        updateEO.setPrevShouldRunDate(updateEO.getNextRunDate());
        if(cronEO.getTimeMode().equals(CronConfEO.TimeMode.day.toString())) {
            Long schedId = updateEO.getSchedId();
            if(null != schedId) {
                scheduleJobService.delete(ScheduleJobEO.class,schedId);
            }
            //设置模式为隔几天更新一次
            updateEO.setNextRunDate(DateUtil.getDateBeforeOrAfter(updateEO.getNextRunDate(), cronEO.getSpaceOfDay()));
            ScheduleJobEO eo = new ScheduleJobEO();
            eo.setSiteId(updateEO.getSiteId());
            eo.setName(updateEO.getTaskName());
            eo.setType("column_update");
            eo.setJson(json);
            eo.setClazz(ColumnUpdateTaskImpl.class.getName());
            eo.setStatus("NORMAL");
            eo.setCronExpression(CronUtil.getCron(updateEO.getNextRunDate()));
            Long scheduId = scheduleJobService.saveEntity(eo);
            updateEO.setSchedId(scheduId);
        } else {
            String cronExpress = cronEO.getCronExpress();
            updateEO.setNextRunDate(CronUtil.getNextCronTime(cronExpress,updateEO.getPrevRunDate()));
        }

        columnUpdateService.updateEntity(updateEO);

        if(!updateEO.getTaskType().equals(ColumnUpdateEO.TastkType.publicinfo.toString())) {
            Map<Long,List<CmsUserSiteOptEO>> map = new HashMap<Long, List<CmsUserSiteOptEO>>();
            for(ColumnResultEO eo : results) {
                List<CmsUserSiteOptEO> users = userSiteOptService.getRightsBySiteId(eo.getcSiteId());
                map.put(eo.getcSiteId(),users);
            }

            for (Map.Entry<Long, List<CmsUserSiteOptEO>> entry : map.entrySet()) {
                Long siteId = entry.getKey();
                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
                List<CmsUserSiteOptEO> users = entry.getValue();
                for(CmsUserSiteOptEO user : users) {
                    MessageSystemEO eo = new MessageSystemEO();
                    eo.setTitle("栏目更新预警!");
                    eo.setLink("/column/update/checkResult?taskId=" + updateEO.getId() + "&taskType=" + updateEO.getTaskType() + "&siteId=" + siteId);
                    eo.setModeCode("columnUpdate");
                    eo.setRecUserIds(user.getUserId() + "");
                    if(updateEO.getTaskType().equals(ColumnUpdateEO.TastkType.article.toString())) {
                        eo.setContent("【" + indicatorEO.getName() + "】站点下面有未按时更新的栏目!");
                    } else {
                        eo.setContent("【" + indicatorEO.getName() + "】站点下面有未按时回复的留言!");
                    }
                    eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                    MessageSender.sendMessage(eo);
                }
            }
        }

        if(!results.isEmpty()) {
            RoleEO roleEO = roleServer.getRoleByCode(RoleEO.RoleCode.super_admin.toString());
            List<RoleAssignmentEO> assignmentEOs = roleAssignmentService.getRoleAssignments(roleEO.getRoleId());

            for(RoleAssignmentEO assignmentEO : assignmentEOs) {
                MessageSystemEO eo = new MessageSystemEO();
                eo.setTitle("栏目更新预警!");
                eo.setLink("/column/update/checkResult?taskId=" + updateEO.getId() + "&taskType=" + updateEO.getTaskType());
                eo.setModeCode("columnUpdate");
                eo.setRecUserIds(assignmentEO.getUserId() + "");
                eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                if(updateEO.getTaskType().equals(ColumnUpdateEO.TastkType.article.toString())) {
                    eo.setContent("有未按时更新的栏目!");
                } else if(updateEO.getTaskType().equals(ColumnUpdateEO.TastkType.guest.toString())) {
                    eo.setContent("有未按时回复的留言!");
                } else if(updateEO.getTaskType().equals(ColumnUpdateEO.TastkType.publicinfo.toString())) {
                    eo.setContent("有未按时更新的信息公开!");
                }
                MessageSender.sendMessage(eo);
            }
        }
    }

    private static IColumnResultService columnResultService = SpringContextHolder.getBean("columnResultService");
}
