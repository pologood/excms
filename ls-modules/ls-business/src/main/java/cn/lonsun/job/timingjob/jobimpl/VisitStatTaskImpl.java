package cn.lonsun.job.timingjob.jobimpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.system.sitechart.internal.entity.SiteChartTrendEO;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.service.ISiteChartTrendService;
import cn.lonsun.system.sitechart.vo.CountStatVO;

public class VisitStatTaskImpl extends ISchedulerService {

    private static final Logger logger = LoggerFactory
        .getLogger(VisitStatTaskImpl.class);

    private ISiteChartMainService siteChartMainService = SpringContextHolder
        .getBean("siteChartMainService");

    private ISiteChartTrendService siteChartTrendService = SpringContextHolder
        .getBean("siteChartTrendService");

    // @Scheduled(cron = "0 0 0-23 * * ?")
    // @Scheduled(cron = "0/10 * * * * ?")
    @Override
    public void execute(String json) {
        System.out.println("统计开始---");
        logger.warn("站点统计开始---");
        List<IndicatorEO> siteList = CacheHandler.getList(IndicatorEO.class,
            CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());

        if (null != siteList && siteList.size() > 0) {
            Date st = null;
            Date ed = null;
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat df3 = new SimpleDateFormat("HH");
            String nowStr = df1.format(new Date());
            try {
                ed = df1.parse(nowStr);
                long t = ed.getTime() - 3600000L;
                st = new Date(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String hourAgo = df1.format(st);

            List<CountStatVO> pv = siteChartMainService.getPv(st, ed);
            List<CountStatVO> uv = siteChartMainService.getUv(df2.format(st),
                hourAgo, nowStr);
            List<CountStatVO> nuv = siteChartMainService
                .getNuv(hourAgo, nowStr);
            List<CountStatVO> ip = siteChartMainService.getIp(df2.format(st),
                hourAgo, nowStr);
            List<CountStatVO> sv = siteChartMainService.getSv(st, ed);
            for (IndicatorEO li : siteList) {
                Long siteId = li.getIndicatorId();
                SiteChartTrendEO trendEO = new SiteChartTrendEO();
                trendEO.setCreateDate(new Date());
                String day = df2.format(st);
                String hour = df3.format(st);
                trendEO.setDay(day);
                trendEO.setHour(hour + ":00-" + hour + ":59");
                trendEO.setTime(st.getTime());
                trendEO.setSiteId(siteId);
                trendEO.setPv(0L);
                trendEO.setUv(0L);
                trendEO.setNuv(0L);
                trendEO.setIp(0L);
                trendEO.setSv(0L);
                for (CountStatVO p : pv) {
                    if (siteId.equals(p.getSiteId())) {
                        trendEO.setPv(p.getNum());
                    }
                }
                for (CountStatVO u : uv) {
                    if (siteId.equals(u.getSiteId())) {
                        trendEO.setUv(u.getNum());
                    }
                }
                for (CountStatVO n : nuv) {
                    if (siteId.equals(n.getSiteId())) {
                        trendEO.setNuv(n.getNum());
                    }
                }
                for (CountStatVO i : ip) {
                    if (siteId.equals(i.getSiteId())) {
                        trendEO.setIp(i.getNum());
                    }
                }
                for (CountStatVO s : sv) {
                    if (siteId.equals(s.getSiteId())) {
                        trendEO.setSv(s.getNum());
                    }
                }
                siteChartTrendService.saveEntity(trendEO);
            }
            System.out.println("---统计结束");
            logger.warn("---站点统计结束");
        }
    }
}
