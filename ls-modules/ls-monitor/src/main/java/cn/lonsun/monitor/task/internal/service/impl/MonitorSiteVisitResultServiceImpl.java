package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredVetoConfigService;
import cn.lonsun.monitor.task.internal.dao.IMonitorSiteVisitResultDao;
import cn.lonsun.monitor.task.internal.dao.impl.MonitorCustomTaskManageDaoImpl;
import cn.lonsun.monitor.task.internal.entity.*;
import cn.lonsun.monitor.task.internal.entity.vo.JobParamVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitDateStatisVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitStatisVO;
import cn.lonsun.monitor.task.internal.job.SiteVisitJob;
import cn.lonsun.monitor.task.internal.service.*;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.supervise.util.CronUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorSiteVisitResultServiceImpl extends MockService<MonitorSiteVisitResultEO> implements IMonitorSiteVisitResultService {

    @Resource
    private IScheduleJobService scheduleJobService;

    @Resource
    private IMonitorIndexManageService monitorIndexManageService;

    @Resource
    private IMonitorCustomIndexManageService monitorCustomIndexManageService;

    @DbInject("monitorSiteVisitResult")
    private IMonitorSiteVisitResultDao monitorSiteVisitResultDao;

    @Resource
    private IMonitoredVetoConfigService monitoredVetoConfigService;

    @Resource
    private IMonitorTaskManageService monitorTaskManageService;

    @Resource
    private IMonitorCustomTaskManageService monitorCustomTaskManageService;

    @Resource
    private IMonitorSiteStatisService monitorSiteStatisService;

    @Override
    public Pagination getSiteVisitPage(SiteVisitQueryVO vo) {
        MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,vo.getTaskId());
        Long siteId = task.getSiteId();
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteDeny.toString(), siteId);
        String end = getWeekBeforeDate(task.getStartDate(),0);
        Long weeks = Long.valueOf(String.valueOf(bc.get("monitoredNum")));
        int day = weeks.intValue()*7;
        String begin = getWeekBeforeDate(task.getStartDate(),-day);
        vo.setSiteId(siteId);
        vo.setBegin(begin);
        vo.setEnd(end);
        return monitorSiteVisitResultDao.getSiteVisitPage(vo);
    }

    @Override
    public List<MonitorSiteVisitResultEO> getSiteVisitList(SiteVisitQueryVO vo) {
        MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,vo.getTaskId());
        Long siteId = task.getSiteId();
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteDeny.toString(), siteId);
        String end = getWeekBeforeDate(task.getStartDate(),0);
        Long weeks = Long.valueOf(String.valueOf(bc.get("monitoredNum")));
        int day = weeks.intValue()*7;
        String begin = getWeekBeforeDate(task.getStartDate(),-day);
        vo.setSiteId(siteId);
        vo.setBegin(begin);
        vo.setEnd(end);
        return monitorSiteVisitResultDao.getSiteVisitList(vo);
    }

    @Override
    public void runMonitor(Long siteId, Long taskId,Long reportId) {
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteDeny.toString(), siteId);
//        SiteMgrEO siteVO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        JobParamVO vo = new JobParamVO();
        String cron = getCron(bc,vo);
        vo.setReportId(reportId);
        vo.setSiteId(siteId);
        vo.setTaskId(taskId);
//        vo.setUrl(siteVO.getUri());
        int monitoredOvertime = Integer.valueOf(String.valueOf(bc.get("monitoredOvertime")));
        vo.setTimeout(monitoredOvertime * 1000);
        ScheduleJobEO eo = new ScheduleJobEO();
        eo.setSiteId(siteId);
        if(null != reportId) {
            eo.setName("站点[" + siteId + "]无法访问检测");
        } else {
            eo.setName("站点[" + siteId + "]无法访问自定义检测");
        }
        eo.setType("site_visit");
        eo.setJson(JSONObject.fromObject(vo).toString());
        eo.setClazz(SiteVisitJob.class.getName());
        eo.setStatus("NORMAL");
        //定时模式是隔天模式
        eo.setCronExpression(cron);
        MonitorIndexManageEO index = monitorIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),siteId);
        if(null != reportId) {
            if(null != index.getScheduleId()) {
                ScheduleJobEO jobEO = scheduleJobService.getEntity(ScheduleJobEO.class,index.getScheduleId());
                if(null == jobEO) {
                    Long schedId = scheduleJobService.saveEntity(eo);
                    index.setScheduleId(schedId);
                    monitorIndexManageService.updateEntity(index);
                }
            } else {
                Long schedId = scheduleJobService.saveEntity(eo);
                index.setScheduleId(schedId);
                monitorIndexManageService.updateEntity(index);
            }
        } else {
            MonitorCustomIndexManageEO csindex = monitorCustomIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),siteId);
            if(null != csindex) {
                Long schedId = scheduleJobService.saveEntity(eo);
                csindex.setScheduleId(schedId);
            }
            monitorCustomIndexManageService.updateEntity(csindex);
        }
    }

    @Override
    public SiteVisitStatisVO getSiteVisitStatis(Long taskId) {
        MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,taskId);
        Long siteId = 0l;
        Date startDate ;
        if(task==null){//查询自定义任务
            MonitorCustomTaskManageEO customTask = monitorCustomTaskManageService.getEntity(MonitorCustomTaskManageEO.class,taskId);
            if(customTask==null){
                return new SiteVisitStatisVO();
            }
            siteId = customTask.getSiteId();
            startDate = customTask.getStartDate();
        }else{
            siteId = task.getSiteId();
            startDate = task.getStartDate();

        }
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteDeny.toString(), siteId);
        String end = getWeekBeforeDate(startDate,0);
        Long weeks = Long.valueOf(String.valueOf(bc.get("monitoredNum")));
        int day = weeks.intValue()*7;
        String begin = getWeekBeforeDate(startDate,-day);
        return monitorSiteVisitResultDao.getSiteVisitStatis(siteId,begin,end);
    }

    @Override
    public SiteVisitDateStatisVO getSiteVisitStatis(Long taskId, String date) {
        return monitorSiteVisitResultDao.getSiteVisitStatis(taskId,date);
    }

    @Override
    public SiteVisitStatisVO loadSiteVisitStatis(Long taskId, Long siteId) {
        if(null == taskId) {
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteId,null);
            taskId = task.getId();
        }
        SiteVisitStatisVO lstatis = this.getSiteVisitStatis(taskId);
        //注意：这里后期从指标库获取数据
        double rateLimit = 5;
        if(null != lstatis && lstatis.getTotal() > 0) {
            Map<String,Object> vetoConfig =  monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),siteId);
            if(null != vetoConfig && null != vetoConfig.get("notOpenRate")) {
                rateLimit = Double.valueOf(vetoConfig.get("notOpenRate").toString());
            }
            boolean flag = true;
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);
            String successrate = "0";
            if(null != lstatis.getTotal() && lstatis.getTotal() != 0) {
                successrate = numberFormat.format((float) lstatis.getSuccess() / (float) lstatis.getTotal() * 100);
            }
            lstatis.setSuccessRate(Double.valueOf(successrate));
            String failrate = "0";
            if(null != lstatis.getTotal() && lstatis.getTotal() != 0) {
                failrate = numberFormat.format((float) lstatis.getFail() / (float) lstatis.getTotal() * 100);
            }
            lstatis.setFailRate(Double.valueOf(failrate));
            BigDecimal a = new BigDecimal(rateLimit);
            BigDecimal b = new BigDecimal(failrate);
            if(b.compareTo(a) < 0) {
                flag = false;
                lstatis.setIsOk(1);
                monitorSiteStatisService.updateSiteDeny(siteId,taskId,1);
            } else {
                monitorSiteStatisService.updateSiteDeny(siteId,taskId,0);
            }

            MonitorTaskManageEO secondLatestTask = monitorTaskManageService.getLatestTask(siteId,taskId);
            if(null == secondLatestTask) {
                if(lstatis.getFail() > 0) {
                    lstatis.setLinkRelationStatus(2); //向上箭头 本期单项否决且上期未单项否决
                } else {
                    lstatis.setLinkRelationStatus(0); // 横杠
                }

            } else {
                MonitorSiteStatisEO siteStatis = monitorSiteStatisService.getSiteStatis(siteId,secondLatestTask.getId());
                if(null == siteStatis) {
                    lstatis.setLinkRelationStatus(0); // 横杠
                } else {
                    if (flag) {
                        if (null == siteStatis.getSiteDeny() || siteStatis.getSiteDeny() == 0) {
                            lstatis.setLinkRelationStatus(1); // 斜杠 本期单项否决且上期单项否决
                        } else {
                            lstatis.setLinkRelationStatus(2); //向上箭头 本期单项否决且上期未单项否决
                        }
                    } else {
                        if (null == siteStatis.getSiteDeny()) {
                            lstatis.setLinkRelationStatus(0); // 向下箭头 本期未单项否决且上期单项否决
                        } else if (siteStatis.getSiteDeny() == 0) {
                            lstatis.setLinkRelationStatus(3); // 向下箭头 本期未单项否决且上期单项否决
                        } else {
                            lstatis.setLinkRelationStatus(4); // 本期且上期均未单项否决，计算y
                            SiteVisitStatisVO sstatis = this.getSiteVisitStatis(siteStatis.getId());
                            if (null == sstatis || sstatis.getFail() == 0) {
                                if (lstatis.getFail() > 0) {
                                    lstatis.setLinkRelationRate(new Double(100));
                                } else {
                                    lstatis.setLinkRelationRate(new Double(0));
                                }
                            } else {
                                double hb = (double) (lstatis.getFail() - sstatis.getFail()) * 100 / sstatis.getFail();
                                lstatis.setLinkRelationRate(getDoubleValue(hb, 2));
                            }
                        }
                    }
                }
            }
        } else {
            lstatis.setIsOk(1);
        }

        return lstatis;
    }

    /**
     * 获取cron表达式
     * @param config
     * @return
     */
    private String getCron(Map<String, Object> config, JobParamVO param) {
        StringBuilder cron = new StringBuilder();
        Long weeks = Long.valueOf(String.valueOf(config.get("monitoredNum")));
        int day = weeks.intValue()*7;
        Long count = Long.valueOf(String.valueOf(config.get("dayMonitoredCount")));
        Set<Integer> hours = CronUtil.randomArray(0,24,count.intValue());
        cron.append("0 0 ");
        int c = 0;
        for (Integer h : hours) {
            if(c++ > 0) {
                cron.append(",");
            }
            cron.append(h);
        }
        cron.append(" * * ? ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MINUTE,59);
        Date date = calendar.getTime();
        param.setOverTime(date.getTime());
        param.setCron(cron.toString());
        return cron.toString();
    }

    private String getWeekBeforeDate(Date date,int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);
        Date d = c.getTime();
        return format.format(d);
    }

    /**
     * 获取保留小数位数的数据
     * @param v
     * @param i
     * @return
     */
    private Double getDoubleValue(double v,int i) {
        BigDecimal b = new BigDecimal(v);
        return b.setScale(i, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
