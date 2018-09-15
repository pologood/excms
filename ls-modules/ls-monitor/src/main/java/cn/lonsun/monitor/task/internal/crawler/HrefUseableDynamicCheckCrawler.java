package cn.lonsun.monitor.task.internal.crawler;

import cn.edu.hfut.dmic.webcollector.crawler.BasicCrawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDB;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDBManager;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamGenerator;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableDynamicEO;
import cn.lonsun.monitor.task.internal.service.IMonitorHrefUseableDynamicService;
import cn.lonsun.supervise.errhref.internal.util.URLHelper;
import cn.lonsun.webservice.monitor.client.vo.HrefUseableDynamicVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2017-10-11 9:12
 */
public class HrefUseableDynamicCheckCrawler extends BasicCrawler {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IMonitorHrefUseableDynamicService monitorHrefUseableDynamicService = SpringContextHolder.getBean(IMonitorHrefUseableDynamicService.class);

    private List<HrefUseableDynamicVO> hrefs = Collections.synchronizedList(new ArrayList<HrefUseableDynamicVO>());

    //注册编码
    private String registerCode;

    //站点ID
    private Long siteId;

    //任务ID
    private Long taskId;

    //报告ID
    private Long reportId;

    //检测地址
    private String href;

    /**
     * 构造方法
     * @param autoParse
     * @param registerCode
     * @param href
     * @param siteId
     */
    public HrefUseableDynamicCheckCrawler(boolean autoParse, String registerCode, String href, Long siteId,Long reportId) {
        super(autoParse);
        this.registerCode = registerCode;
        this.siteId = siteId;
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
     * 构造方法
     * @param autoParse
     * @param registerCode
     * @param href
     * @param siteId
     */
    public HrefUseableDynamicCheckCrawler(boolean autoParse, String registerCode, String href, Long siteId,Long taskId,Long reportId) {
        super(autoParse);
        this.registerCode = registerCode;
        this.siteId = siteId;
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
                    if(null != registerCode && null != siteId) {

                        MonitorHrefUseableDynamicEO eo = new MonitorHrefUseableDynamicEO();
                        eo.setTaskId(taskId);
                        eo.setSiteId(siteId);
                        eo.setMonitorDate(new Date());
                        eo.setIsVisitable(0);
                        eo.setRespCode(code);
                        eo.setVisitUrl(page.getUrl());
                        eo.setParentUrl(parentUrl);
                        eo.setReason("网页404");
                        if(parentUrl.equalsIgnoreCase(href)) {
                            eo.setIsIndex(1);
                        }
                        eo.setDomain(href);
                        monitorHrefUseableDynamicService.saveEntity(eo);
                        logger.info("{}：动态监测保存本地成功",page.getUrl());

                        //保存数据到云端
                        HrefUseableDynamicVO vo = new HrefUseableDynamicVO();
                        vo.setRegisterCode(registerCode);
                        vo.setSiteId(siteId);
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
                    if(null != registerCode && null != siteId) {
                        String parentUrl = page.getCrawlDatum().getMetaData("parentUrl");
                        logger.info(page.getUrl() + "打不开，父地址：" + parentUrl);

                        MonitorHrefUseableDynamicEO eo = new MonitorHrefUseableDynamicEO();
                        eo.setTaskId(taskId);
                        eo.setSiteId(siteId);
                        eo.setMonitorDate(new Date());
                        eo.setIsVisitable(0);
                        eo.setRespCode(code);
                        eo.setVisitUrl(page.getUrl());
                        eo.setParentUrl(parentUrl);
                        eo.setReason("网页404");
                        if(parentUrl.equalsIgnoreCase(href)) {
                            eo.setIsIndex(1);
                        }
                        eo.setDomain(href);
                        monitorHrefUseableDynamicService.saveEntity(eo);

                        //保存数据到云端
                        HrefUseableDynamicVO vo = new HrefUseableDynamicVO();
                        vo.setRegisterCode(registerCode);
                        vo.setReportId(reportId);
                        vo.setSiteId(siteId);
                        vo.setMonitorDate(new Date());
                        vo.setIsVisitable(0);
                        vo.setRespCode(code);
                        vo.setVisitUrl(page.getUrl());
                        vo.setParentUrl(parentUrl);
                        vo.setReason("访问失败");
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

    public List<HrefUseableDynamicVO> getHrefs() {
        return hrefs;
    }

    public void setHrefs(List<HrefUseableDynamicVO> hrefs) {
        this.hrefs = hrefs;
    }
}
