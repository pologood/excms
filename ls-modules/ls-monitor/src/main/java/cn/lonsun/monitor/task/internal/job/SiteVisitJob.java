package cn.lonsun.monitor.task.internal.job;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.internal.service.IMonitorSiteRegisterService;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;
import cn.lonsun.monitor.job.service.ISchedulerService;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteVisitResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.JobParamVO;
import cn.lonsun.monitor.task.internal.service.IMonitorSiteVisitResultService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.webservice.monitor.client.IMonitorSiteVisitClient;
import cn.lonsun.webservice.monitor.client.vo.SiteVisitResponseVO;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-09-29 10:41
 */
public class SiteVisitJob extends ISchedulerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private IMonitorSiteVisitResultService monitorSiteVisitResultService = SpringContextHolder.getBean(IMonitorSiteVisitResultService.class);
    private IMonitorSiteVisitClient monitorSiteVisitClient = SpringContextHolder.getBean(IMonitorSiteVisitClient.class);
    private IMonitorSiteRegisterService monitorSiteRegisterService = SpringContextHolder.getBean(IMonitorSiteRegisterService.class);

    @Override
    public void execute(String json) {
        JSONObject jsonObject= JSONObject.fromObject(json);
        JobParamVO param = (JobParamVO)JSONObject.toBean(jsonObject, JobParamVO.class);
        MonitorSiteVisitResultEO result = new MonitorSiteVisitResultEO();
        MonitorSiteRegisterVO siteRegisterVO = monitorSiteRegisterService.getSiteRegisterInfo(param.getSiteId());
        result.setTaskId(param.getTaskId());
        result.setSiteId(param.getSiteId());
        SiteMgrEO siteVO = CacheHandler.getEntity(SiteMgrEO.class, param.getSiteId());
        if(siteVO!=null&&!AppUtil.isEmpty(siteVO.getUri())){
            result.setVisitUrl(siteVO.getUri());
        }else{
            result.setVisitUrl(param.getUrl());
        }
        result.setMonitorDate(new Date());
        SiteVisitResponseVO visitResponseVO = monitorSiteVisitClient.checkUrlConnect(param.getReportId(),param.getSiteId(),siteRegisterVO.getSiteName(),siteRegisterVO.getRegisteredCode(),param.getUrl(),param.getTimeout());
        result.setRespCode(visitResponseVO.getRespCode());
        result.setReason(visitResponseVO.getDesc());
        if(visitResponseVO.getRespCode() == 200) {
            result.setIsVisitable(1);
        }
        monitorSiteVisitResultService.saveEntity(result);
        logger.info("完成一次站点访问检测{}",new Date());
    }
}
