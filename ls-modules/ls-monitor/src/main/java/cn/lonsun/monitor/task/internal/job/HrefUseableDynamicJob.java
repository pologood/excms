package cn.lonsun.monitor.task.internal.job;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.job.service.ISchedulerService;
import cn.lonsun.monitor.task.internal.crawler.HrefUseableDynamicCheckCrawler;
import cn.lonsun.monitor.task.internal.entity.vo.JobParamVO;
import cn.lonsun.webservice.monitor.client.IMonitorHrefUseableClient;
import cn.lonsun.webservice.monitor.client.vo.HrefUseableDynamicVO;
import cn.lonsun.webservice.to.WebServiceTO;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-09-29 10:41
 */
public class HrefUseableDynamicJob extends ISchedulerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(String json) {
        JSONObject jsonObject= JSONObject.fromObject(json);
        JobParamVO param = (JobParamVO)JSONObject.toBean(jsonObject, JobParamVO.class);
        HrefUseableDynamicCheckCrawler crawler = new HrefUseableDynamicCheckCrawler(true,param.getRegisterCode(),param.getUrl(),param.getSiteId(),param.getTaskId(),param.getReportId());
        /*线程数*/
        crawler.setThreads(50);
        /*设置每次迭代中爬取数量的上限*/
        crawler.setTopN(500000000);
        crawler.setRetry(1);
        try {
            crawler.start(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.util.List<HrefUseableDynamicVO> hrefs = crawler.getHrefs();
        IMonitorHrefUseableClient monitorHrefUseableClient = SpringContextHolder.getBean(IMonitorHrefUseableClient.class);
        try {
            WebServiceTO to = monitorHrefUseableClient.saveHrefUseableDynamic(hrefs);
            if(null == to || to.getStatus() == 0) {
                monitorHrefUseableClient.saveHrefUseableDynamic(hrefs);
            }
        } catch (Exception e) {
            monitorHrefUseableClient.saveHrefUseableDynamic(hrefs);
        }

        logger.info("完成一次动态监测首页链接可用性{}",new Date());
    }
}
