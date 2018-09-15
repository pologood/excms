package cn.lonsun.supervise.errhref.internal.util;

import java.util.List;

import cn.edu.hfut.dmic.webcollector.crawler.BasicCrawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDB;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDBManager;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamGenerator;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.supervise.errhref.internal.entity.ErrHrefEO;
import cn.lonsun.supervise.errhref.internal.entity.HrefResultEO;
import cn.lonsun.supervise.errhref.internal.service.IErrHrefService;
import cn.lonsun.supervise.errhref.internal.service.IHrefResultService;

/**
 * 网页数据采集
 * @author gu.fei
 * @version 2016-1-25 13:52
 */
public class ErrHrefCheckCrawler extends BasicCrawler {

    private static IErrHrefService errHrefService = SpringContextHolder.getBean("errHrefService");

    private static IHrefResultService hrefResultService = SpringContextHolder.getBean("hrefResultService");;

    private static ErrHrefEO eo;

    /**
     * 构造函数初始化数据
     * @param autoParse
     * @param taskId
     */
    public ErrHrefCheckCrawler(boolean autoParse, Long taskId) {
        super(autoParse);
        RamDB ramDB = new RamDB();
        this.dbManager = new RamDBManager(ramDB);
        this.generator = new RamGenerator(ramDB);

        eo = errHrefService.getEntity(ErrHrefEO.class,taskId);
        this.addSeed(eo.getWebSite());

        this.addRegex(".*");

        if(null != eo.getFilterHref()) {
            List<String> hrefs = StringUtils.getListWithString(eo.getFilterHref(),",");
            for(String href : hrefs) {
                this.addSeed("-.*" + href);
            }
        }

        /*不要爬取包含 # 的URL*/
        this.addRegex("-.*#.*");
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        if(page.getUrl().contains(eo.getWebDomain())) {
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
    public void fail(Page page, CrawlDatums next) {
        if(!AppUtil.isEmpty(page.getUrl())) { //不为空
            if(!URLHelper.isEmail(page.getUrl())) { //非邮件格式
                int code = URLHelper.isConnect(page.getUrl());
                if(code != 200) {
                    String parentUrl = page.getCrawlDatum().getMetaData("parentUrl");
                    String urlName = URLHelper.getATagName(parentUrl, page.getUrl(), eo.getWebDomain(),eo.getCharset());
                    HrefResultEO resultEO = new HrefResultEO();
                    resultEO.setTaskId(eo.getId());
                    resultEO.setUrl(page.getUrl());
                    resultEO.setUrlName(urlName);
                    resultEO.setParentUrl(parentUrl);
                    resultEO.setRepCode(code);
                    resultEO.setRepDesc(URLHelper.getRepcodeDesc(code));
                    hrefResultService.saveEntity(resultEO);
                }
            }
        }
    }

    @Override
    public void notFound(Page page, CrawlDatums next) {
        if(!AppUtil.isEmpty(page.getUrl())) { //不为空
            if(!URLHelper.isEmail(page.getUrl())) { //非邮件格式
                int code = URLHelper.isConnect(page.getUrl());
                if(code != 200) {
                    String parentUrl = page.getCrawlDatum().getMetaData("parentUrl");
                    String urlName = URLHelper.getATagName(parentUrl, page.getUrl(), eo.getWebDomain(),eo.getCharset());
                    HrefResultEO resultEO = new HrefResultEO();
                    resultEO.setTaskId(eo.getId());
                    resultEO.setUrl(page.getUrl());
                    resultEO.setUrlName(urlName);
                    resultEO.setParentUrl(parentUrl);
                    resultEO.setRepCode(code);
                    resultEO.setRepDesc(URLHelper.getRepcodeDesc(code));
                    hrefResultService.saveEntity(resultEO);
                }
            }
        }
    }
}
