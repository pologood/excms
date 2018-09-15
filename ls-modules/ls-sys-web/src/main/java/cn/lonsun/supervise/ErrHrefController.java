package cn.lonsun.supervise;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.site.words.util.ExportExcel;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.errhref.internal.entity.ErrHrefEO;
import cn.lonsun.supervise.errhref.internal.entity.HrefResultEO;
import cn.lonsun.supervise.errhref.internal.entity.HrefResultVO;
import cn.lonsun.supervise.errhref.internal.service.IErrHrefService;
import cn.lonsun.supervise.errhref.internal.service.IHrefResultService;
import cn.lonsun.supervise.errhref.internal.util.URLHelper;
import cn.lonsun.supervise.vo.SupervisePageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2016-4-5 9:40
 */
@Controller
@RequestMapping("/err/href")
public class ErrHrefController extends BaseController {

    private static final String FILE_BASE = "/supervise/errhref/";

    @Autowired
    private IErrHrefService errHrefService;

    @Autowired
    private ICronConfService cronConfService;

    @Autowired
    private IHrefResultService hrefResultService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/index";
    }

    @RequestMapping("/addOrEdit")
    public String addOrEdit() {
        return FILE_BASE + "/edit_task";
    }

    /**
     * 查看栏目检查结果
     * @return
     */
    @RequestMapping("/checkResult")
    public String checkResult(ModelMap map,Long taskId,String taskName) {
        map.put("taskId",taskId);
        map.put("taskName",taskName);
        return FILE_BASE + "check_result";
    }

    /**
     * 查看栏目检查结果
     * @return
     */
    @RequestMapping("/errLocation")
    public String errLocation(ModelMap map,Long resultId,HttpServletRequest request) {
        String curUrl = URLHelper.getBasePathUrl(request.getRequestURL().toString());
        HrefResultEO resultEO = hrefResultService.getEntity(HrefResultEO.class, resultId);
        ErrHrefEO errHrefEO = errHrefService.getEntity(ErrHrefEO.class, resultEO.getTaskId());
        String html = URLHelper.getHtml(resultEO.getParentUrl(), errHrefEO.getCharset());
        String baseUrl = "<base href=\"" + URLHelper.getBasePathUrl(resultEO.getParentUrl()) + "\">";
        String jquery = "<script src=\"" + curUrl + "/assets/js/plugins/jquery-min.js\"></script>\n";
        String dialogcss = "<link rel=\"stylesheet\" href=\"" + curUrl +"/assets/css/artdialog-min.css\">\n";
        String dialogjs = " <script src=\"" + curUrl + "/assets/js/plugins/dialog/dialog-min.js\"></script>\n";
        String autojs = " <script  defer=\"defer\" type=\"text/javascript\" charset=\"utf-8\" src=\"" + curUrl + "/assets/js/pages/checkHref.js\"></script>\n";
        String js = "";
        try {
            js = "<script>var hrefId='" + resultEO.getId() + "';" +
                    "var errCode = '" + resultEO.getRepCode() + "';" +
                    "var errDesc = '" + resultEO.getRepDesc() + "';</script>\n";
        } catch (Exception e) {

        }
        html = html.substring(0,html.indexOf("<head>") + "<head>".length()) + baseUrl +
                html.substring(html.indexOf("<head>") + "<head>".length() + 1,html.length());

        html = html.substring(0,html.indexOf("</head>")) + jquery + dialogcss + dialogjs +autojs + js +
                html.substring(html.indexOf("</head>"), html.length());
        html = URLHelper.setTips(html,resultEO.getUrl(),errHrefEO.getWebDomain(),resultEO.getId().toString());
        html = URLHelper.setDiv(html, resultEO.getUrl(), errHrefEO.getWebDomain(),curUrl,"font", "", "color:red");
        map.put("html",html);
        return FILE_BASE + "err_location";
    }

    /**
     * 查看源码
     * @return
     */
    @RequestMapping("/htmlLocation")
    public String htmlLocation(ModelMap map,Long resultId) {
        HrefResultEO resultEO = hrefResultService.getEntity(HrefResultEO.class, resultId);
        ErrHrefEO errHrefEO = errHrefService.getEntity(ErrHrefEO.class, resultEO.getTaskId());
        String html = URLHelper.getHtml(resultEO.getParentUrl(), errHrefEO.getCharset());
        String href = resultEO.getUrl();
        if(!html.contains(href)) {
            int x = href.indexOf(errHrefEO.getWebDomain());
            href = href.substring(x + errHrefEO.getWebDomain().length(), href.length());
        }
        String htmlBegin = html.substring(0,html.indexOf(href));
        String htmlEnd = html.substring(html.indexOf(href) + href.length(),html.length());
        map.put("htmlBegin",htmlBegin);
        map.put("realHref",href);
        map.put("href",resultEO.getUrl());
        map.put("htmlEnd",htmlEnd);
        map.put("hrefId",resultEO.getId());
        map.put("errCode",resultEO.getRepCode());
        map.put("errDesc",resultEO.getRepDesc());
        return FILE_BASE + "html_location";
    }


    /**
     * 分页获取采集任务
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageTaskEOs")
    public Object getPageTaskEOs(SupervisePageVO vo) {
        Pagination page = errHrefService.getPageEOs(vo);
        return page;
    }

    @ResponseBody
    @RequestMapping("/getPageResultEOs")
    public Object getPageResultEOs(SupervisePageVO vo) {
        return hrefResultService.getPageEOs(vo);
    }

    @ResponseBody
    @RequestMapping("/saveTask")
    public Object saveTask(ErrHrefEO hrefEO,CronConfEO cronEO) {
        errHrefService.saveTask(hrefEO,cronEO);
        return ajaxOk();
    }

    @ResponseBody
    @RequestMapping("/updateTask")
    public Object updateTask(ErrHrefEO hrefEO,CronConfEO cronEO) {
        errHrefService.updateTask(hrefEO, cronEO);
        return ajaxOk();
    }

    /**
     * 暂停任务
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping("/pauseTask")
    public Object pauseTask(Long taskId) {
        errHrefService.pauseTask(taskId);
        return ajaxOk();
    }

    /**
     * 恢复任务
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping("/resumeTask")
    public Object resumeTask(Long taskId) {
        errHrefService.resumeTask(taskId);
        return ajaxOk();
    }

    /**
     * 删除任务
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteTasks")
    public Object deleteTasks(@RequestParam(value="ids[]",required=false) Long[] ids) {
        errHrefService.physDelEOs(ids);
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/getCronConf")
    public Object getCronConf(Long cronId) {
        return getObject(cronConfService.getEntity(CronConfEO.class, cronId));
    }

    @ResponseBody
    @RequestMapping("/recheck")
    public Object recheck(Long resultId) {
        int code = errHrefService.recheck(resultId);
        return getObject(code);
    }

    @ResponseBody
    @RequestMapping("/remove")
    public Object remove(Long resultId) {
        hrefResultService.physDelEO(resultId);
        return ajaxOk();
    }

    @RequestMapping("/exportErrhref")
    public void exportErrhref(HttpServletResponse response,Long taskId,String taskName) {
        String[] titles = new String[]{"链接名称","链接地址","链接来源地址","链接访问返回编码","返回编码说明"};
        List<HrefResultEO> list = hrefResultService.getByTaskId(taskId);

        List<Object> datas = new ArrayList<Object>();
        if(!AppUtil.isEmpty(list)){
            for (HrefResultEO eo : list) {
                HrefResultVO vo = new HrefResultVO();
                vo.setUrlName(eo.getUrlName());
                vo.setUrl(eo.getUrl());
                vo.setParentUrl(eo.getParentUrl());
                vo.setRepCode(eo.getRepCode());
                vo.setRepDesc(eo.getRepDesc());
                datas.add(vo);
            }
        }

        try {
            String name = taskName + "-错链(" + dateFormat(new Date()) + ")";
            String suffic = "xls";
            ExportExcel.exportExcel(name, suffic, titles, datas, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String dateFormat(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy.MM.dd");
        return time.format(date);
    }
}
