package cn.lonsun.monitor.task.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableResultEO;
import cn.lonsun.monitor.task.internal.service.IMonitorHrefUseableResultService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.supervise.errhref.internal.util.URLHelper;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author gu.fei
 * @version 2017-10-25 8:20
 */
@Controller
@RequestMapping(value = "/href/useable")
public class HrefUseableController extends BaseController {


    private static final String FILE_BASE = "/monitor/task/";

    @Resource
    private IMonitorHrefUseableResultService monitorHrefUseableResultService;

    /**
     * 查看栏目检查结果
     * @return
     */
    @RequestMapping("/errLocation")
    public String errLocation(ModelMap map, Long resultId, HttpServletRequest request) {

        Long siteId = LoginPersonUtil.getSiteId();
        SiteMgrEO siteVO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        String curUrl = URLHelper.getBasePathUrl(request.getRequestURL().toString());
        MonitorHrefUseableResultEO href = monitorHrefUseableResultService.getEntity(MonitorHrefUseableResultEO.class, resultId);
        String html = URLHelper.getHtml(href.getParentUrl(), "UTF-8");
        String baseUrl = "<base href=\"" + URLHelper.getBasePathUrl(href.getParentUrl()) + "\">";
        String jquery = "<script src=\"" + curUrl + "/assets/js/jquery.min.js\"></script>\n";
        String dialogcss = "<link rel=\"stylesheet\" href=\"" + curUrl +"/assets/css/artdialog-min.css\">\n";
        String dialogjs = " <script src=\"" + curUrl + "/assets/js/plugins/dialog/dialog-min.js\"></script>\n";
        String autojs = " <script  defer=\"defer\" type=\"text/javascript\" charset=\"utf-8\" src=\"" + curUrl + "/assets/js/pages/checkHref.js\"></script>\n";
        String js = "";
        try {
            js = "<script>var hrefId='" + href.getId() + "';" +
                    "var errCode = '" + href.getRespCode() + "';" +
                    "var errDesc = '" + href.getReason() + "';</script>\n";
        } catch (Exception e) {

        }
        html = html.substring(0,html.indexOf("<head>") + "<head>".length()) + baseUrl +
                html.substring(html.indexOf("<head>") + "<head>".length() + 1,html.length());

        html = html.substring(0,html.indexOf("</head>")) + jquery + dialogcss + dialogjs +autojs + js +
                html.substring(html.indexOf("</head>"), html.length());
        html = URLHelper.setTips(html,href.getVisitUrl(),siteVO.getUri(),href.getId().toString());
        html = URLHelper.setDiv(html, href.getVisitUrl(), siteVO.getUri(),curUrl,"font", "", "color:red");
        map.put("html",html);
        return FILE_BASE + "err_location";
    }

    /**
     * 查看源码
     * @return
     */
    @RequestMapping("/htmlLocation")
    public String htmlLocation(ModelMap map,Long resultId) {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteMgrEO siteVO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        MonitorHrefUseableResultEO hrefeo = monitorHrefUseableResultService.getEntity(MonitorHrefUseableResultEO.class, resultId);
        String html = URLHelper.getHtml(hrefeo.getParentUrl(), "UTF-8");
        String href = hrefeo.getVisitUrl();
        if(!html.contains(href)) {
            int x = href.indexOf(siteVO.getUri());
            href = href.substring(x + siteVO.getUri().length(), href.length());
        }
        if(html.contains(href)) {
            String htmlBegin = html.substring(0,html.indexOf(href));
            String htmlEnd = html.substring(html.indexOf(href) + href.length(),html.length());
            map.put("htmlBegin",htmlBegin);
            map.put("htmlEnd",htmlEnd);
            map.put("errCode",hrefeo.getRespCode());
            map.put("errDesc",hrefeo.getReason());
        } else {
            map.put("htmlBegin",html);
            map.put("errCode",hrefeo.getRespCode());
            map.put("errDesc","链接地址已经移除，请重新检测");
        }
        map.put("realHref",href);
        map.put("href",hrefeo.getVisitUrl());
        map.put("hrefId",hrefeo.getId());
        return FILE_BASE + "html_location";
    }
}
