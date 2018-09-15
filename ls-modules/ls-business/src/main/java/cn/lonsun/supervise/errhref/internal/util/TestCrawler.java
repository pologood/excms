package cn.lonsun.supervise.errhref.internal.util;

import cn.edu.hfut.dmic.webcollector.crawler.BasicCrawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDB;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamDBManager;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamGenerator;

/**
 * @author gu.fei
 * @version 2016-4-12 11:10
 */
public class TestCrawler extends BasicCrawler {

    public TestCrawler( boolean autoParse) {
        super(autoParse);
        RamDB ramDB = new RamDB();
        this.dbManager = new RamDBManager(ramDB);
        this.generator = new RamGenerator(ramDB);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        if(page.getUrl().contains("www.ahjs.gov.cn")) {
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
        int code = URLHelper.isConnect(page.getUrl());
        if(code != 200) {
            System.out.println("父亲节点:" + page.getCrawlDatum().getMetaData("parentUrl"));
            System.out.println("错误的:" + page.getUrl() + " code:" + code);
        }
    }

    @Override
    public void notFound(Page page, CrawlDatums next) {
        System.out.println(page.getResponse().getCode());
        System.out.println(page.getUrl());
    }

    public static void main(String[] args){
        TestCrawler crawler = new TestCrawler(true);
        crawler.addSeed("http://www.ahjs.gov.cn");
        crawler.addRegex(".*");
        /*不要爬取包含 # 的URL*/
        crawler.addRegex("-.*#.*");
        /*线程数*/
        crawler.setThreads(50);
        /*设置每次迭代中爬取数量的上限*/
        crawler.setTopN(50000000);
        crawler.setRetry(1);
        try {
            crawler.start(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
