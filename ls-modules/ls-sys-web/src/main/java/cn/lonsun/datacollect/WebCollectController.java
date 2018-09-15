package cn.lonsun.datacollect;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.JSONHelper;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.HtmlCollectContentEO;
import cn.lonsun.datacollect.entity.HtmlCollectDataEO;
import cn.lonsun.datacollect.entity.HtmlCollectTaskEO;
import cn.lonsun.datacollect.service.IHtmlCollectContentService;
import cn.lonsun.datacollect.service.IHtmlCollectDataService;
import cn.lonsun.datacollect.service.IHtmlCollectTaskService;
import cn.lonsun.datacollect.util.CollectCrawler;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.util.ColumnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-1-21 10:00
 */
@Controller
@RequestMapping("/data/collect")
public class WebCollectController extends BaseController {

    private static final String FILE_BASE = "/datacollect/web/";

    @Autowired
    private IHtmlCollectTaskService htmlCollectTaskService;

    @Autowired
    private IHtmlCollectContentService htmlCollectContentService;

    @Autowired
    private IHtmlCollectDataService htmlCollectDataService;

    @Autowired
    private ICronConfService cronConfService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "index";
    }

    @RequestMapping("/addOrEditTask")
    public String addOrEditTask() {
        return FILE_BASE + "edit_task";
    }

    @RequestMapping("/contentRule")
    public String contentRule() {
        return FILE_BASE + "content_rule";
    }

    @RequestMapping("/addOrEditContentRule")
    public String addOrEditContentRule() {
        return FILE_BASE + "content_rule_edit";
    }

    @RequestMapping("/regexBeginDetail")
    public String regexStrDetail(Long id,ModelMap map) {
        HtmlCollectContentEO eo = htmlCollectContentService.getEntity(HtmlCollectContentEO.class,id);
        map.put("regexStr",eo == null?"":eo.getRegexBegin());
        return FILE_BASE + "regex_str_detail";
    }

    @RequestMapping("/regexEndDetail")
    public String regexEndDetail(Long id,ModelMap map) {
        HtmlCollectContentEO eo = htmlCollectContentService.getEntity(
                HtmlCollectContentEO.class,id);
        map.put("regexStr",eo == null?"":eo.getRegexEnd());
        return FILE_BASE + "regex_str_detail";
    }

    @RequestMapping("/regexFilterDetail")
    public String regexFilterDetail(Long id,ModelMap map) {
        HtmlCollectContentEO eo = htmlCollectContentService.getEntity(HtmlCollectContentEO.class,id);
        map.put("regexStr",eo == null?"":eo.getRegexFilter());
        return FILE_BASE + "regex_str_detail";
    }

    @RequestMapping("/dataDetail")
    public String dataDetail(Long taskId,ModelMap map) {
        map.put("taskId",taskId);
        return FILE_BASE + "content_data";
    }

    @RequestMapping("/showDetail")
     public String showDetail() {
        return FILE_BASE + "show_detail";
    }

    @RequestMapping("/columnSelect")
    public String columnSelect() {
        return FILE_BASE + "column_select";
    }

    @RequestMapping("/quoteColumn")
    public String quoteColumn(Long columnId,Long cSiteId,ModelMap map) {
        map.put("columnId",columnId);
        map.put("cSiteId",cSiteId);
        map.put("columnName", ColumnUtil.getColumnName(columnId,cSiteId));
        return FILE_BASE + "quote_column";
    }

    @RequestMapping("/showLog")
    public String showLog() {
        return FILE_BASE + "show_log";
    }

    @RequestMapping("/taskCron")
    public String taskCron(Long cronId,ModelMap map) {
        CronConfEO cron = null;
        if(null != cronId) {
            cron = cronConfService.getEntity(CronConfEO.class,cronId);
        } else {
            cron = new CronConfEO();
        }
        map.put("cron", JSONHelper.toJSON(cron));
        return FILE_BASE + "task_cron";
    }

    /**
     * 数据预览
     * @return
     */
    @RequestMapping("/scanDetail")
    public String scanDetail(Long id,ModelMap map) {
        HtmlCollectDataEO eo = htmlCollectDataService.getEntity(HtmlCollectDataEO.class,id);
        if(null != eo) {
            map.put("eo",eo);
        }
        return FILE_BASE + "scan_detail";
    }

    /**
     * 分页获取采集任务
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageTaskEOs")
    public Object getPageTaskEOs(CollectPageVO vo) {
        Pagination page = htmlCollectTaskService.getPageEOs(vo);
        List<HtmlCollectTaskEO> list = (List<HtmlCollectTaskEO>) page.getData();

        for(HtmlCollectTaskEO eo : list) {
            if(null != eo.getColumnId()) {
                eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(), eo.getcSiteId()));
            }
        }

        return page;
    }

    /**
     * 分页获取内容采集
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageContentEOs")
    public Object getPageContentEOs(CollectPageVO vo) {
        return htmlCollectContentService.getPageEOs(vo);
    }

    /**
     * 分页获取采集数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageDataEOs")
    public Object getPageDataEOs(CollectPageVO vo) {
        return htmlCollectDataService.getPageEOs(vo);
    }


    /**
     * 保存采集任务
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveTask")
    public Object saveTask(HtmlCollectTaskEO eo) {
        htmlCollectTaskService.saveEO(eo);
        return ResponseData.success("保存成功!");
    }

    /**
     * 更新采集任务
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateTask")
    public Object updateTask(HtmlCollectTaskEO eo) {
        htmlCollectTaskService.updateEO(eo);
        return ResponseData.success("更新成功!");
    }

    /**
     * 复制采集任务
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/copyTask")
    public Object copyTask(HtmlCollectTaskEO eo) {
        htmlCollectTaskService.copyEO(eo);
        return ResponseData.success("更新成功!");
    }

    /**
     * 删除任务
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteTasks")
    public Object deleteTasks(@RequestParam(value="ids[]",required=false) Long[] ids) {
        htmlCollectTaskService.deleteEOs(ids);
        return ResponseData.success("删除成功!");
    }

    /**
     * 保存内容规则
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveContentRule")
    public Object saveContentRule(HtmlCollectContentEO eo) {
        htmlCollectContentService.saveEO(eo);
        return ResponseData.success("保存成功!");
    }

    /**
     * 更新内容规则
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateContentRule")
    public Object updateContentRule(HtmlCollectContentEO eo) {
        htmlCollectContentService.updateEO(eo);
        return ResponseData.success("更新成功!");
    }

    /**
     * 删除内容规则
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteContentRules")
    public Object deleteContentRules(@RequestParam(value="ids[]",required=false) Long[] ids) {
        htmlCollectContentService.deleteEOs(ids);
        return ResponseData.success("删除成功!");
    }

    /**
     * 执行采集任务
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/execTask")
    public Object execTask(Long id) {

        CollectCrawler collectCrawler = new CollectCrawler(true,id);
        /*尝试三次*/
        collectCrawler.setRetry(3);
        /*设置每次迭代中爬取数量的上限*/
        collectCrawler.setTopN(5000000);

//        htmlCollectDataService.deleteByTaskId(id);

        try {
            collectCrawler.start(2);
        } catch (Exception e) {
            return ResponseData.fail("启动采集任务失败...");
        }

        HtmlCollectTaskEO eo = htmlCollectTaskService.getEntity(HtmlCollectTaskEO.class,id);
        eo.setCollectDate(new Date());
        htmlCollectTaskService.updateEntity(eo);
        return ResponseData.success("采集中...");
    }

    /**
     * 获取字段
     * @param tableName
     * @return
     */
    @ResponseBody
    @RequestMapping("/getColumns")
    public Object getColumns(String tableName) {
        return getObject(htmlCollectContentService.getColumns(tableName));
    }

    /**
     * 引用全部
     * @param columnId
     * @return
     */
    @ResponseBody
    @RequestMapping("/quoteAll")
    public Object quoteAll(Long columnId,Long cSiteId,Long taskId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("taskId",taskId);

        List<HtmlCollectDataEO> list = htmlCollectDataService.getEntities(HtmlCollectDataEO.class,param);
        Long[] ids = new Long[list.size()];

        int i = 0;
        for(HtmlCollectDataEO eo : list) {
            ids[i++] = eo.getId();
        }

        String returnStr = htmlCollectDataService.quoteData(columnId, cSiteId, ids);
        if(!StringUtils.isEmpty(returnStr)) {
            MessageSenderUtil.publishCopyNews(returnStr);
        }
        return ResponseData.success("引用成功!");
    }

    /**
     * 引用选中
     * @param columnId
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("/quoteCheck")
    public Object quoteCheck(Long columnId,Long cSiteId,@RequestParam(value="ids[]",required=false) Long[] ids) {
        String returnStr = htmlCollectDataService.quoteData(columnId, cSiteId, ids);
        if(!StringUtils.isEmpty(returnStr)) {
            MessageSenderUtil.publishCopyNews(returnStr);
        }
        return ResponseData.success("引用成功!");
    }

    /**
     * 保存定时任务
     * @param cronEO
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveTaskCron")
    public Object saveTaskCron(CronConfEO cronEO,Long taskId) {
        htmlCollectTaskService.saveTaskCron(cronEO,taskId);
        return ajaxOk();
    }

    /**
     * 修改定时任务
     * @param cronEO
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateTaskCron")
    public Object updateTaskCron(CronConfEO cronEO,Long taskId) {
        htmlCollectTaskService.updateTaskCron(cronEO,taskId);
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
        htmlCollectTaskService.pauseTask(taskId);
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
        htmlCollectTaskService.resumeTask(taskId);
        return ajaxOk();
    }
}
