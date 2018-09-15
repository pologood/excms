package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.ip2region.DataBlock;
import cn.lonsun.ip2region.Ip2regionUtil;
import cn.lonsun.nlp.utils.MemberLabelUtil;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.system.sitechart.internal.entity.SiteChartMainEO;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.service.ISiteChartTrendService;
import cn.lonsun.system.sitechart.vo.SourceType;
import cn.lonsun.system.sitechart.webutil.ChartDataSaveRunnale;
import cn.lonsun.system.sitechart.webutil.SearchEnginesUtil;
import cn.lonsun.system.sitechart.webutil.StatDateUtil;
import cn.lonsun.system.sitechart.webutil.UrlHostUtil;
import cn.lonsun.util.RandomDigitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(value = "site/chart", produces = {"application/json;charset=UTF-8"})
public class SiteChartController extends BaseController {
    @Autowired
    private ISiteChartTrendService siteChartTrendService;

    @Autowired
    private ISiteChartMainService siteChartMainService;

    private static final Logger logger = LoggerFactory.getLogger(SiteChartController.class);


    @RequestMapping("/{siteId:[\\d\\.]+}")
    public void js(HttpServletRequest req, HttpServletResponse resp, @PathVariable Long siteId) {
        IndicatorEO indicat = CacheHandler.getEntity(IndicatorEO.class, siteId);
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            /*out.print(";(function(){var params = {};if(document) {params.url = document.URL || \'\';params.title = document.title || \'\';params.referer = document.referrer || \'\';}"
                    + "if(window && window.screen) {var sh = window.screen.height || 0;var sw = window.screen.width || 0;params.resolution=sw+\' x \'+sh;params.colorDepth = window.screen.colorDepth || 0;}"
					+ "if(navigator) {params.language = navigator.language || \'\';params.siteId=_maq;"
					+ "var ua =navigator.userAgent;var b=\"Other\";if (ua.indexOf(\"Firefox\") > -1) {b=\"Firefox\"}else if(ua.indexOf(\"Chrome\") > -1){b=\"Chrome\"}else if(ua.indexOf(\"Opera\") > -1){b=\"Opera\"}"
					+ "else if(ua.indexOf(\"Netscape\") > -1){b=\"Netscape\"}else if(ua.indexOf(\"MSIE\") > -1||ua.indexOf(\"Trident\")>-1){b=\"IE\"} params.client=b;"
					+ "var Agents = new Array(\"Android\", \"iPhone\", \"SymbianOS\", \"Windows Phone\", \"iPad\", \"iPod\",\"Windows\",\"Mac\",\"Unix\",\"Linux\",\"X11\");var f=\"Other\";"
					+ "for (var v = 0; v < ua.length; v++) {if (ua.indexOf(Agents[v]) > -1) { f = Agents[v];} }var isPc=\"true\";if(f!=\"Windows\"&&f!=\"Mac\"&&f!=\"Unix\"&&f!=\"Linux\"){isPc=\"false\"} params.isPc=isPc;"
					+ "if(f==\"Windows\"){if(ua.indexOf(\"Windows NT 5.0\") > -1 || ua.indexOf(\"Windows 2000\") > -1){f=\"Windows 2000\"}"
					+ "else if(ua.indexOf(\"Windows NT 5.1\") > -1 || ua.indexOf(\"Windows XP\") > -1){f=\"Windows XP\"}else if(ua.indexOf(\"Windows NT 5.2\") > -1 || ua.indexOf(\"Windows 2003\") > -1){f=\"Windows 2003\"}"
					+ "else if(ua.indexOf(\"Windows NT 6.0\") > -1 || ua.indexOf(\"Windows Vista\") > -1){f=\"Windows Vista\"}else if(ua.indexOf(\"Windows NT 6.1\") > -1 || ua.indexOf(\"Windows 7\") > -1){f=\"Windows 7\"}"
					+ "else if(ua.indexOf(\"Windows NT 6.2\") > -1 || ua.indexOf(\"Windows 8\") > -1){f=\"Windows 8\"}else if(ua.indexOf(\"Windows NT 10\") > -1 || ua.indexOf(\"Windows 10\") > -1){f=\"Windows 10\"}} params.os=f;"
					+ "} var args = \'\';try{params.country=remote_ip_info.country;params.province=remote_ip_info.province;params.city=remote_ip_info.city;}catch(e){console.log(e.message);}"
					+ "for(var i in params) {if(args != \'\') { args += \'&\';}args += i + \'=\' + params[i];}"
					+ "var lonsun_sta = ((\"https:\" == document.location.protocol) ? \" https://\" : \" http://\");document.write(unescape(\"%3Cspan id=\'lonsun_web_master_\"+_maq+\"\'%3E%3C/span%3E%3Cscript "
					+ "src=\'"+indicat.getUri()+"/site/chart/webMst?\"+args+\"\' type=\'text/javascript\'%3E%3C/script%3E\"));"
					+ "})();");*/
//            "        var b = \"Other\";\n" +
//                    "        if (ua.indexOf(\"Firefox\") > -1) {\n" +
//                    "            b = \"Firefox\"\n" +
//                    "        } else if (ua.indexOf(\"Chrome\") > -1) {\n" +
//                    "            b = \"Chrome\"\n" +
//                    "        } else if (ua.indexOf(\"Opera\") > -1) {\n" +
//                    "            b = \"Opera\"\n" +
//                    "        } else if (ua.indexOf(\"Netscape\") > -1) {\n" +
//                    "            b = \"Netscape\"\n" +
//                    "        } else if (ua.indexOf(\"MSIE\") > -1 || ua.indexOf(\"Trident\") > -1) {\n" +
//                    "            b = \"IE\"\n" +
//                    "        }\n" +
//                    "        params.client = b;\n" +
            out.print(";(function () {\n" +
                "    var params = {};\n" +
                "    if (document) {\n" +
                "        params.url = document.URL || '';\n" +
                "        params.title = document.title || '';\n" +
                "        params.referer = document.referrer || '';\n" +
                "    }\n" +
                "    if (window && window.screen) {\n" +
                "        var sh = window.screen.height || 0;\n" +
                "        var sw = window.screen.width || 0;\n" +
                "        params.resolution = sw + ' x ' + sh;\n" +
                "        params.colorDepth = window.screen.colorDepth || 0;\n" +
                "    }\n" +
                "    if (navigator) {\n" +
                "        params.language = navigator.language || '';\n" +
                "        params.siteId = global_siteId;\n" +
                "        try{params.isTimes = isTimes;}catch(e){}\n" +
                "        var ua = navigator.userAgent;\n" +
                "        var Agents = new Array(\"Android\", \"iPhone\", \"SymbianOS\", \"Windows Phone\", \"iPad\", \"iPod\", \"Windows\", \"Mac\", \"Unix\", \"Linux\", \"X11\");\n" +
                "        var f = \"Other\";\n" +
                "        for (var v = 0; v < ua.length; v++) {\n" +
                "            if (ua.indexOf(Agents[v]) > -1) {\n" +
                "                f = Agents[v];\n" +
                "            }\n" +
                "        }\n" +
                "        var isPc = \"true\";\n" +
                "        if (f != \"Windows\" && f != \"Mac\" && f != \"Unix\" && f != \"Linux\") {\n" +
                "            isPc = \"false\"\n" +
                "        }\n" +
                "        params.isPc = isPc;\n" +
                "        if (f == \"Windows\") {\n" +
                "            if (ua.indexOf(\"Windows NT 5.0\") > -1 || ua.indexOf(\"Windows 2000\") > -1) {\n" +
                "                f = \"Windows 2000\"\n" +
                "            } else if (ua.indexOf(\"Windows NT 5.1\") > -1 || ua.indexOf(\"Windows XP\") > -1) {\n" +
                "                f = \"Windows XP\"\n" +
                "            } else if (ua.indexOf(\"Windows NT 5.2\") > -1 || ua.indexOf(\"Windows 2003\") > -1) {\n" +
                "                f = \"Windows 2003\"\n" +
                "            } else if (ua.indexOf(\"Windows NT 6.0\") > -1 || ua.indexOf(\"Windows Vista\") > -1) {\n" +
                "                f = \"Windows Vista\"\n" +
                "            } else if (ua.indexOf(\"Windows NT 6.1\") > -1 || ua.indexOf(\"Windows 7\") > -1) {\n" +
                "                f = \"Windows 7\"\n" +
                "            } else if (ua.indexOf(\"Windows NT 6.2\") > -1 || ua.indexOf(\"Windows 8\") > -1) {\n" +
                "                f = \"Windows 8\"\n" +
                "            } else if (ua.indexOf(\"Windows NT 10\") > -1 || ua.indexOf(\"Windows 10\") > -1) {\n" +
                "                f = \"Windows 10\"\n" +
                "            }\n" +
                "        }\n" +
                "        params.os = f;\n" +
                "    }\n" +
                "    var args = '';\n" +
                "    for (var i in params) {\n" +
                "        if (args != '') {\n" +
                "            args += '&';\n" +
                "        }\n" +
                "        args += i + '=' + encodeURIComponent(params[i]);\n" +
                "    }\n" +
                "    document.write(unescape(\"%3Cspan id='website_statistics_\" + global_siteId + \"'%3E%3C/span%3E%3Cscript src='" + "" + "/site/chart/webMst?\" + args + \"' type='text/javascript'%3E%3C/script%3E\"));\n" +
                "})();");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("webMst")
    public void webMst(HttpServletRequest req, HttpServletResponse resp,
                       SiteChartMainEO mainEO) {
        PrintWriter out = null;
        Long siteId = null;
        siteId = Long.parseLong(req.getParameter("siteId") == null ? "-1" : req.getParameter("siteId"));
        Boolean isImg = req.getParameter("isImg") == null ? false : true;
        Boolean isTimes = req.getParameter("isTimes") == null ? false : true;
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        logger.debug("来源页面：" + mainEO.getReferer());
        String sCookie = null;
        if (siteEO != null) {
            Long lastTime = null;
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("time".equals(c.getName())) {
                        mainEO.setCookie(c.getValue());
                        lastTime = Long.parseLong(c.getValue());
                        logger.debug(c.getName() + "=" + c.getValue());
                    }
                    if ("sCookie".equals(c.getName())) {
                        mainEO.setsCookie(c.getValue());
                        sCookie = c.getValue();
                    }
                }
            }
            Cookie timeCookie = new Cookie("time", String.valueOf(new Date().getTime()));
            timeCookie.setMaxAge(-1);
            resp.addCookie(timeCookie);

            if (sCookie == null) {
                sCookie = RandomDigitUtil.getRandomDigit();
                Cookie session = new Cookie("sCookie", sCookie);
                session.setMaxAge(StatDateUtil.getSurplusTimeOfToday());
                resp.addCookie(session);
                mainEO.setsCookie(sCookie);
            }

            if (!AppUtil.isEmpty(mainEO.getReferer())) {
                mainEO.setSourceHost(UrlHostUtil.getHost(mainEO.getReferer()));
            }
            String host = siteEO.getUri();
            if (AppUtil.isEmpty(mainEO.getReferer())) {
                mainEO.setSourceType(SourceType.DE);
                logger.debug(SourceType.DE);
            } else if (mainEO.getReferer().contains(host)) {
                if (lastTime != null && (lastTime + (30 * 60 * 1000)) < new Date().getTime()) {
                    mainEO.setSourceType(SourceType.SS);
                    mainEO.setCookie(null);
                } else {
                    mainEO.setCookie(String.valueOf(new Date().getTime()));
                    mainEO.setSourceType(SourceType.SL);
                }
            } else {
                String searchEngine = SearchEnginesUtil.getSearchEngine(mainEO.getReferer());
                String searchKey = SearchEnginesUtil.getKeyword(mainEO.getReferer());
                if (null == searchEngine) {
                    mainEO.setSourceType(SourceType.OL);
                    logger.debug(SourceType.OL);
                } else {
                    mainEO.setSourceType(SourceType.SE);
                    mainEO.setSearchEngine(searchEngine);
                    mainEO.setSearchKey(searchKey);
                    mainEO.setCookie(null);
                    logger.debug("搜索引擎： " + searchEngine + " 关键词： " + searchKey);
                }
            }
            //如果已经登录，保存会员id
            try {
                MemberSessionVO sessionMember = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
                if (sessionMember != null) {
                    mainEO.setMemberId(sessionMember.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String ip = IpUtil.getIpAddr(req);
            mainEO.setIp(ip);
            //保存Ip所在区域
            if (!StringUtils.isEmpty(ip)) {
                DataBlock dataBlock = Ip2regionUtil.binarySearch(ip);
                if (dataBlock != null) {
                    mainEO.setCountry(dataBlock.getCountry());
                    mainEO.setProvince(dataBlock.getProvince());
                    mainEO.setCity(dataBlock.getCity());
                }
            }
            logger.debug("IP：" + IpUtil.getIpAddr(req));
            logger.debug("页面标题：" + mainEO.getTitle());
            logger.debug(mainEO.getCountry() + "-" + mainEO.getProvince() + "-" + mainEO.getCity());
            logger.debug("屏幕分辨率：" + mainEO.getResolution());
            logger.debug("颜色深度：" + mainEO.getColorDepth());
            logger.debug("当前URL：" + mainEO.getUrl());
//			String UserAgent=req.getHeader("USER-AGENT");
//			String browser = UserAgentParser.getBrowser(UserAgent);
//			String os = UserAgentParser.getOS(UserAgent);
//			mainEO.setClient(browser);
//			mainEO.setOs(os);
//			logger.debug("浏览器版本："+browser+" 操作系统："+os);	

            ChartDataSaveRunnale saveChart = new ChartDataSaveRunnale();
            saveChart.setMainEO(mainEO);
            Thread thread = new Thread(saveChart);
            thread.start();
        }
        //逻辑处理

        try {
            out = resp.getWriter();
            if (isImg) {
                out.print("" +
                    "var website_statistics = document.getElementById(\"website_statistics_" + siteId + "\");" +
                    "if(website_statistics) {" +
                    "   website_statistics.innerHTML=\'<img title=\"站点统计\" alt=\"站点统计\" src=\"/assets/images/chart_bar.png\"/>\';" +
                    "}" +
                    "");
            }
            if (isTimes) {
                //统计人次
                Long count = siteChartMainService.getCount(siteId);
                out.print("" +
                    "var website_times = document.getElementById(\"website_times_" + siteId + "\");" +
                    "if(website_times) {" +
                    "   website_times.innerHTML=\'" + count + "\';" +
                    "}" +
                    "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }

        /**
         * 会员访问行为记录
         */
//        try {
//            MemberLabelUtil.handleMemberKeywordRel(mainEO.getUrl(),IpUtil.getIpAddr(req),siteId);
//        }catch (Exception e){
//            e.printStackTrace();
//        }


    }



}
