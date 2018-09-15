package cn.lonsun.monitor.task.internal.crawler;

import cn.edu.hfut.dmic.webcollector.crawler.BasicCrawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDB;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDBManager;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamGenerator;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableResultEO;
import cn.lonsun.monitor.task.internal.service.IMonitorHrefUseableResultService;
import cn.lonsun.supervise.errhref.internal.util.URLHelper;
import cn.lonsun.webservice.monitor.client.vo.HrefUseableVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author gu.fei
 * @version 2017-10-11 9:12
 */
public class HrefUseableCheckCrawler extends BasicCrawler {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IMonitorHrefUseableResultService monitorHrefUseableResultService = SpringContextHolder.getBean(IMonitorHrefUseableResultService.class);

    private List<HrefUseableVO> hrefs = Collections.synchronizedList(new ArrayList<HrefUseableVO>());

    //任务ID
    private Long taskId;

    //云端报告ID
    private Long reportId;

    //检测地址
    private String href;

    /**
     * 构造方法
     * @param autoParse
     * @param taskId
     * @param href
     */
    public HrefUseableCheckCrawler(boolean autoParse,Long taskId,String href,Long reportId) {
        super(autoParse);
        this.taskId = taskId;
        this.reportId = reportId;
        this.href = href;
        RamDB ramDB = new RamDB();
        this.dbManager = new RamDBManager(ramDB);
        this.generator = new RamGenerator(ramDB);
        this.addSeed(href);
        this.addRegex(".*");
        /*不要爬取包含 # 的URL*/
        this.addRegex("-.*#.*");
    }

    /**
     * 可以访问
     * @param page
     * @param next
     */
    @Override
    public void visit(Page page, CrawlDatums next) {
        if(page.getUrl().contains(href)) {
            this.setAutoParse(true);
        } else {
            this.setAutoParse(false);
        }
    }

    @Override
    public void afterVisit(Page page, CrawlDatums next) {
        super.afterVisit(page, next);
        next.putMetaData("parentUrl", page.getUrl());
    }

    @Override
    public void stop() {
        super.stop();
    }

    /**
     * 访问不到
     * @param page
     * @param next
     */
    @Override
    public void notFound(Page page, CrawlDatums next) {
        if(!AppUtil.isEmpty(page.getUrl())) { //不为空
            if(!URLHelper.isEmail(page.getUrl())) { //非邮件格式
                int code = URLHelper.isConnect(page.getUrl());
                if(code != 200) {
                    String parentUrl = page.getCrawlDatum().getMetaData("parentUrl");
                    logger.info(page.getUrl() + "打不开，父地址：" + parentUrl);
                    Map<String,Object> param = new HashMap<String,Object>();
                    param.put("taskId",taskId);
                    param.put("visitUrl",page.getUrl());
                    param.put("parentUrl",parentUrl);
                    MonitorHrefUseableResultEO hrefUseable = monitorHrefUseableResultService.getEntity(MonitorHrefUseableResultEO.class,param);
                    if(hrefUseable!=null){//重试或者重复的错链，不继续往数据库保存
                        return;
                    }
                    hrefUseable = new MonitorHrefUseableResultEO();
                    hrefUseable.setTaskId(taskId);
                    hrefUseable.setMonitorDate(new Date());
                    hrefUseable.setIsVisitable(0);
                    hrefUseable.setRespCode(code);
                    hrefUseable.setVisitUrl(page.getUrl());
                    hrefUseable.setParentUrl(parentUrl);
                    hrefUseable.setReason("网页404");
                    if(parentUrl.equalsIgnoreCase(href)) {
                        hrefUseable.setIsIndex(1);
                    }
                    monitorHrefUseableResultService.saveEntity(hrefUseable);
                    logger.info("{}：保存本地成功",page.getUrl());

                    if(null != reportId) {
                        //保存数据到云端
                        HrefUseableVO vo = new HrefUseableVO();
                        vo.setReportId(reportId);
                        vo.setMonitorDate(new Date());
                        vo.setIsVisitable(0);
                        vo.setRespCode(code);
                        vo.setVisitUrl(page.getUrl());
                        vo.setParentUrl(parentUrl);
                        vo.setReason("网页404");
                        if(parentUrl.equalsIgnoreCase(href)) {
                            vo.setIsIndex(1);
                        }
                        vo.setDomain(href);
                        hrefs.add(vo);
                    }
                }
            }
        }
    }

    /**
     * 访问失败
     * @param page
     * @param next
     */
    @Override
    public void fail(Page page, CrawlDatums next) {
        if(!AppUtil.isEmpty(page.getUrl())) { //不为空
            if(!URLHelper.isEmail(page.getUrl())) { //非邮件格式
                int code = URLHelper.isConnect(page.getUrl());
                if(code != 200) {
                    String parentUrl = page.getCrawlDatum().getMetaData("parentUrl");
                    logger.info(page.getUrl() + "打不开，父地址：" + parentUrl);
                    Map<String,Object> param = new HashMap<String,Object>();
                    param.put("taskId",taskId);
                    param.put("visitUrl",page.getUrl());
                    param.put("parentUrl",parentUrl);
                    MonitorHrefUseableResultEO hrefUseable = monitorHrefUseableResultService.getEntity(MonitorHrefUseableResultEO.class,param);
                    if(hrefUseable!=null){//重试或者重复的错链，不继续往数据库保存
                        return;
                    }
                    hrefUseable = new MonitorHrefUseableResultEO();
                    hrefUseable.setTaskId(taskId);
                    hrefUseable.setMonitorDate(new Date());
                    hrefUseable.setIsVisitable(0);
                    hrefUseable.setRespCode(code);
                    hrefUseable.setVisitUrl(page.getUrl());
                    hrefUseable.setParentUrl(parentUrl);
                    hrefUseable.setReason("访问失败");
                    if(parentUrl.equalsIgnoreCase(href)) {
                        hrefUseable.setIsIndex(1);
                    }
                    monitorHrefUseableResultService.saveEntity(hrefUseable);
                    if(null != reportId) {
                        //保存数据到云端
                        HrefUseableVO vo = new HrefUseableVO();
                        vo.setReportId(reportId);
                        vo.setMonitorDate(new Date());
                        vo.setIsVisitable(0);
                        vo.setRespCode(code);
                        vo.setVisitUrl(page.getUrl());
                        vo.setParentUrl(parentUrl);
                        vo.setReason("访问失败");
                        if (parentUrl.equalsIgnoreCase(href)) {
                            vo.setIsIndex(1);
                        }
                        vo.setDomain(href);
                        hrefs.add(vo);
                    }
                }
            }
        }
    }

    public List<HrefUseableVO> getHrefs() {
        return hrefs;
    }

    public void setHrefs(List<HrefUseableVO> hrefs) {
        this.hrefs = hrefs;
    }
}
