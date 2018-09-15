package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.internal.service.IMonitorSiteRegisterService;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;
import cn.lonsun.monitor.task.internal.entity.MonitorIndexManageEO;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteStatisEO;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;
import cn.lonsun.monitor.task.internal.service.IMonitorIndexManageService;
import cn.lonsun.monitor.task.internal.service.IMonitorSiteStatisService;
import cn.lonsun.monitor.task.internal.service.IMonitorTaskManageService;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.webservice.monitor.client.IMonitorReportRecordClient;
import cn.lonsun.webservice.monitor.client.vo.ReportResponseVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorIndexManageServiceImpl extends MockService<MonitorIndexManageEO> implements IMonitorIndexManageService {

    @Resource
    private IMonitorTaskManageService monitorTaskManageService;

    @Resource
    private IScheduleJobService scheduleJobService;

    @Resource
    private IMonitorSiteStatisService monitorSiteStatisService;

    @Resource
    private IMonitorSiteRegisterService monitorSiteRegisterService;

    @Resource
    private IMonitorReportRecordClient monitorReportRecordClient;

    @Resource
    private ISiteConfigService siteConfigService;
    /**
     * 保存指标
     * @param siteId
     */
    @Override
    public void saveIndex(Long siteId) {

        List<MonitorIndexManageEO> indexs = new ArrayList<MonitorIndexManageEO>();
        String aIndexVote = "单项否决项";
        String aIndexScop = "综合评分项";
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.siteDeny.toString(),"站点无法访问",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.siteUpdate.toString(),"网站不更新",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),"栏目不更新",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.error.toString(),"严重错误",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.reply.toString(),"互动回应差",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.scop.toString(),aIndexScop,MonitoredVetoConfigEO.CodeType.siteUse.toString(),"首页及链接可用性",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.scop.toString(),aIndexScop,MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),"信息更新情况",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.scop.toString(),aIndexScop,MonitoredVetoConfigEO.CodeType.replyScope.toString(),"互动回应情况",siteId));
        this.saveEntities(indexs);
    }

    @Override
    public MonitorTaskManageEO startTask() {
        Long siteId = LoginPersonUtil.getSiteId();
        MonitorIndexManageEO index = this.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),siteId);
        MonitorTaskManageEO task = new MonitorTaskManageEO();
        MonitorSiteRegisterVO siteRegisterVO = monitorSiteRegisterService.getSiteRegisterInfo(siteId);
        if(null == siteRegisterVO || null == siteRegisterVO.getRegisteredCode() || "".equals(siteRegisterVO.getRegisteredCode())) {
            throw new BaseRunTimeException("当前站点未注册,请先注册");
        }
        task.setStartDate(new Date());
        task.setSiteId(siteId);
        try {
            ReportResponseVO report = monitorReportRecordClient.pushReportRecord(siteRegisterVO.getRegisteredCode(),siteId,siteRegisterVO.getSiteName());
            if(null != report) {
                task.setReportId(report.getReportId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long taskId = monitorTaskManageService.saveEntity(task);
        index.setTaskId(taskId);
        this.updateEntity(index);
        MonitorSiteStatisEO statis = monitorSiteStatisService.getSiteStatis(siteId);
        if(null == statis) {
            statis = new MonitorSiteStatisEO();
        }
        statis.setSiteId(siteId);
        statis.setTaskId(taskId);
        if(null != siteRegisterVO) {
            statis.setSiteName(siteRegisterVO.getSiteName());
        }
        monitorSiteStatisService.saveOrUpdateEntity(statis);
        return task;
    }

    @Override
    public void stopTask() {
        Long siteId = LoginPersonUtil.getSiteId();
        MonitorIndexManageEO siteDeny = this.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),siteId);
//        MonitorIndexManageEO columnUpdate = this.getIndex(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),siteId);

//        if(null != columnUpdate.getScheduleId()) {
//            Long schedId = columnUpdate.getScheduleId();
//            if(null != schedId) {
//                try {
//                    scheduleJobService.delete(ScheduleJobEO.class,schedId);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    throw new BaseRunTimeException("栏目更新定时任务删除失败");
//                }
//            }
//        }

        if(null != siteDeny.getTaskId()) {
            //获取正在运行的任务
            MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,siteDeny.getTaskId());
            if(null == task) {
                throw new BaseRunTimeException("没有采集任务");
            }

//            Long schedId = siteDeny.getScheduleId();
//            if(null != schedId) {
//                try {
//                    scheduleJobService.delete(ScheduleJobEO.class,schedId);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    throw new BaseRunTimeException("首页可用性定时任务删除失败");
//                }
//            }
            task.setSiteDenyStatus(0);
            task.setSiteUpdateStatus(0);
            task.setColumnUpdateStatus(0);
            task.setErrorStatus(0);
            task.setReplyStatus(0);
            task.setSiteUseStatus(0);
            task.setInfoUpdateStatus(0);
            task.setReplyScopeStatus(0);
            task.setServiceStatus(0);
            monitorTaskManageService.updateEntity(task);
        }
    }

    @Override
    public MonitorIndexManageEO getIndex(String codeType,Long siteId) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",siteId);
        map.put("bIndex",codeType);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return this.getEntity(MonitorIndexManageEO.class,map);
    }

    /**
     * 获取指标对象
     * @param aIndex
     * @param aIndexName
     * @param bIndex
     * @param bIndexName
     * @param siteId
     * @return
     */
    private MonitorIndexManageEO getIndex(String aIndex,String aIndexName,String bIndex,String bIndexName,Long siteId) {
        MonitorIndexManageEO index = new MonitorIndexManageEO();
        index.setaIndex(aIndex);
        index.setaIndexName(aIndexName);
        index.setbIndex(bIndex);
        index.setbIndexName(bIndexName);
        index.setSiteId(siteId);
        return index;
    }
}
