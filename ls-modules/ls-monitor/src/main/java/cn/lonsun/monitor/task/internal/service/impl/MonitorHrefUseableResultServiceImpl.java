package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredVetoConfigService;
import cn.lonsun.monitor.job.service.IScheduleMonitorJobService;
import cn.lonsun.monitor.job.util.ScheduleMonitorJobUtil;
import cn.lonsun.monitor.task.internal.crawler.HrefUseableCheckCrawler;
import cn.lonsun.monitor.task.internal.dao.IMonitorHrefUseableResultDao;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableResultEO;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableStatisVO;
import cn.lonsun.monitor.task.internal.entity.vo.JobParamVO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteVisitStatisVO;
import cn.lonsun.monitor.task.internal.job.HrefUseableDynamicJob;
import cn.lonsun.monitor.task.internal.service.*;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.webservice.monitor.client.IMonitorHrefUseableClient;
import cn.lonsun.webservice.monitor.client.vo.HrefUseableVO;
import cn.lonsun.webservice.to.WebServiceTO;
import net.sf.json.JSONObject;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorHrefUseableResultServiceImpl extends MockService<MonitorHrefUseableResultEO> implements IMonitorHrefUseableResultService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private TaskExecutor taskExecutor;

    @DbInject("monitorHrefUseableResult")
    private IMonitorHrefUseableResultDao monitorHrefUseableResultDao;

    @Resource
    private IMonitoredVetoConfigService monitoredVetoConfigService;

    @Resource
    private IMonitorTaskManageService monitorTaskManageService;

    @Resource
    private IMonitorSiteStatisService monitorSiteStatisService;

    @Resource
    private IMonitorSiteVisitResultService monitorSiteVisitResultService;

    @Resource
    private IScheduleMonitorJobService scheduleMonitorJobService;

    @Override
    public Pagination getHrefUseablePage(HrefUseableQueryVO vo) {
        return monitorHrefUseableResultDao.getHrefUseablePage(vo);
    }

    @Override
    public List<MonitorHrefUseableResultEO> getHrefUseableList(HrefUseableQueryVO vo) {
        return monitorHrefUseableResultDao.getHrefUseableList(vo);
    }

    @Override
    public void runMonitor(final Long siteId, final Long taskId,final Long reportId) {
        final SiteMgrEO siteVO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 绑定session至当前线程中
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                logger.info("开始错链检测，taskId[{}]>>>>>>>>>>>>>>>>>>>>>>>>",taskId);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                HrefUseableCheckCrawler crawler = new HrefUseableCheckCrawler(true,taskId,siteVO.getUri(),reportId);
                /*线程数*/
                crawler.setThreads(50);
                /*设置每次迭代中爬取数量的上限*/
                crawler.setTopN(500000000);
                crawler.setRetry(1);
                try {
                    crawler.start(3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info("错链检测完成,taskId[{}]>>>>>>>>>>>>>>>>>>>>>>>>",taskId);
                java.util.List<HrefUseableVO> hrefs = crawler.getHrefs();
                IMonitorHrefUseableClient monitorHrefUseableClient = SpringContextHolder.getBean(IMonitorHrefUseableClient.class);
                try {
                    WebServiceTO to = monitorHrefUseableClient.saveHrefUseable(hrefs);
                    if(null == to || to.getStatus() == 0) {
                        monitorHrefUseableClient.saveHrefUseable(hrefs);
                    }
                } catch (Exception e) {
                    monitorHrefUseableClient.saveHrefUseable(hrefs);
                }
                if(null != reportId) {
                    IMonitorTaskManageService monitorTaskManageService = SpringContextHolder.getBean(IMonitorTaskManageService.class);
                    monitorTaskManageService.updateStatus(taskId,"siteUseStatus",2);
                } else {
                    IMonitorCustomIndexManageService monitorCustomIndexManageService = SpringContextHolder.getBean(IMonitorCustomIndexManageService.class);
                    monitorCustomIndexManageService.updateStatus(taskId,2);
                }
                logger.info("错误链接检测完成，任务[{}]状态更新完成",taskId);
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
            }
        });
    }

    @Override
    public HrefUseableStatisVO getHrefUseaStatis(Long taskId) {
        return monitorHrefUseableResultDao.getHrefUseaStatis(taskId);
    }

    @Override
    public void runMontorDynamic(Long taskId,Long siteId,Long reportId,String registerCode,String uri) {
        if(null != siteId && null != registerCode && null != uri) {
            ScheduleJobEO existEO = scheduleMonitorJobService.getScheduleJobByClazzAndJson(HrefUseableDynamicJob.class.getName(),siteId, null);
            if(null != existEO) {
                scheduleMonitorJobService.delete(ScheduleJobEO.class,existEO.getId());
            }
            try {
                JobParamVO vo = new JobParamVO();
                vo.setRegisterCode(registerCode);
                vo.setSiteId(siteId);
                vo.setReportId(reportId);
                vo.setUrl(uri);
                vo.setTaskId(taskId);
                String cron = "0 0 1 * * ?"; //每天凌晨一点执行
                ScheduleMonitorJobUtil.updateScheduleJob("动态监测首页可访问性", HrefUseableDynamicJob.class.getName(),siteId,cron,JSONObject.fromObject(vo).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public HrefUseableStatisVO loadHrefUseableStatis(Long taskId, Long siteId) {
        Map<String,Object> vetoConfig =  monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteUse.toString(),siteId);
        if(null == taskId) {
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteId,null);
            taskId = task.getId();
        }
        HrefUseableStatisVO lstatis = this.getHrefUseaStatis(taskId);
        SiteVisitStatisVO siteVisitStatis = monitorSiteVisitResultService.getSiteVisitStatis(taskId);
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        //注意：这里后期从指标库获取数据
        if(null != lstatis) {
            BigDecimal sscore = new BigDecimal(0);
            MonitorTaskManageEO secondLatestTask = monitorTaskManageService.getLatestTask(siteId,taskId);
            double n = 5;
            double i = 1;
            double o = 0.1;
            if(null != vetoConfig) {
                if(null != vetoConfig.get("notOpenNum")) {
                    n = Double.valueOf(vetoConfig.get("notOpenNum").toString());
                }
                if(null != vetoConfig.get("indexErrorNum")) {
                    i = Double.valueOf(vetoConfig.get("indexErrorNum").toString());
                }
                if(null != vetoConfig.get("otherErrorNum")) {
                    o = Double.valueOf(vetoConfig.get("otherErrorNum").toString());
                }
            }
            if(null == secondLatestTask) {
                lstatis.setIndexLinkRelationStatus(0); //横杠
                lstatis.setOtherLinkRelationStatus(0); //横杠
                lstatis.setCpiStatus(0);
            } else {
                HrefUseableStatisVO sstatis = this.getHrefUseaStatis(secondLatestTask.getId());
                SiteVisitStatisVO ssiteVisitStatis = monitorSiteVisitResultService.getSiteVisitStatis(secondLatestTask.getId());
                if(null != ssiteVisitStatis && null != ssiteVisitStatis.getFailRate()) {
                    sscore = sscore.add(new BigDecimal(ssiteVisitStatis.getFailRate()*n));
                }
                sscore = sscore.add(new BigDecimal(null != sstatis.getIndexCount()?sstatis.getIndexCount()*i:0));
                sscore = sscore.add(new BigDecimal(null != sstatis.getOtherCount()?sstatis.getOtherCount()*o:0));
                String failrate_ = "0";
                if(null != ssiteVisitStatis.getTotal() && ssiteVisitStatis.getTotal() != 0) {
                    failrate_ = numberFormat.format((float) ssiteVisitStatis.getFail() / (float) ssiteVisitStatis.getTotal() * 100);
                }
                BigDecimal b_ = new BigDecimal(failrate_);
                sscore = sscore.add(new BigDecimal(b_.doubleValue()*n));

                //计算首页环比
                if(lstatis.getIndexCount() == 0) {
                    if(sstatis.getIndexCount() == 0) {
                        lstatis.setIndexLinkRelationStatus(0); //横杠
                    } else {
                        lstatis.setIndexLinkRelationStatus(3); //向下箭头
                    }
                } else {
                    if(sstatis.getIndexCount() == 0) {
                        lstatis.setIndexLinkRelationStatus(2); //向上箭头
                    } else {
                        lstatis.setIndexLinkRelationStatus(4); // 本期且上期均不等于0，计算y
                        double hb = (double)(lstatis.getIndexCount() - sstatis.getIndexCount())*100/sstatis.getIndexCount();
                        lstatis.setIndexLinkRelationRate(getDoubleValue(hb,2));
                    }
                }
                //计算其他页面环比
                if(lstatis.getOtherCount() == 0) {
                    if(sstatis.getOtherCount() == 0) {
                        lstatis.setOtherLinkRelationStatus(0); //横杠
                    } else {
                        lstatis.setOtherLinkRelationStatus(3); //向下箭头
                    }
                } else {
                    if(sstatis.getOtherCount() == 0) {
                        lstatis.setOtherLinkRelationStatus(2); //向上箭头
                    } else {
                        lstatis.setOtherLinkRelationStatus(4); // 本期且上期均不等于0，计算y
                        double hb = (double)(lstatis.getOtherCount() - sstatis.getOtherCount())*100/sstatis.getOtherCount();
                        lstatis.setOtherLinkRelationRate(getDoubleValue(hb,2));
                    }
                }
            }

            BigDecimal indexScore = new BigDecimal(null != lstatis.getIndexCount()?lstatis.getIndexCount()*i:0);
            BigDecimal otherScore = new BigDecimal(null != lstatis.getOtherCount()?lstatis.getOtherCount()*o:0);
            BigDecimal score = indexScore.add(otherScore);
            lstatis.setLinkScore(score.doubleValue());
            double latestScore = score.doubleValue();

            if(null != siteVisitStatis) {
                lstatis.setSiteVisitSuccess(siteVisitStatis.getSuccess());
                lstatis.setSiteVisitFail(siteVisitStatis.getFail());

                String failrate = "0";
                if(null != siteVisitStatis.getTotal() && siteVisitStatis.getTotal() != 0) {
                    failrate = numberFormat.format((float) siteVisitStatis.getFail() / (float) siteVisitStatis.getTotal() * 100);
                }
                BigDecimal b = new BigDecimal(failrate);
                lstatis.setSiteVisitFailRate(b.doubleValue());
                lstatis.setSiteVisitScore(getDoubleValue(b.doubleValue()*n,2));
                score = score.add(new BigDecimal(b.doubleValue()*n));
                lstatis.setTotalScore(getDoubleValue(score.doubleValue(),2));
            }

            if(null != secondLatestTask) {
                HrefUseableStatisVO secondLstatis = this.getHrefUseaStatis(secondLatestTask.getId());
                SiteVisitStatisVO secondSiteVisitStatis = monitorSiteVisitResultService.getSiteVisitStatis(taskId);
                BigDecimal indexScore_ = new BigDecimal(null != secondLstatis.getIndexCount()?secondLstatis.getIndexCount()*i:0);
                BigDecimal otherScore_ = new BigDecimal(null != secondLstatis.getOtherCount()?secondLstatis.getOtherCount()*o:0);
                BigDecimal score_ = indexScore_.add(otherScore_);
                if(latestScore == 0) {
                    if(sscore.doubleValue() == 0) {
                        lstatis.setCpiStatus(0);
                    } else {
                        lstatis.setCpiStatus(3);
                    }
                } else {
                    if(sscore.doubleValue() == 0) {
                        lstatis.setCpiStatus(2);
                    } else {
                        lstatis.setCpiStatus(4);
                        lstatis.setCpi(getDoubleValue(score.doubleValue()*100/sscore.doubleValue(),2));
                    }
                }
            }
            monitorSiteStatisService.updateSiteUse(siteId,taskId,String.valueOf(getDoubleValue(score.doubleValue(),2)));
        }
        return lstatis;
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
