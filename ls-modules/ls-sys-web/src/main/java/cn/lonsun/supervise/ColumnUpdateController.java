package cn.lonsun.supervise;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.vo.OrganCatalogQueryVO;
import cn.lonsun.publicInfo.vo.OrganCatalogVO;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.supervise.column.internal.service.IColumnResultService;
import cn.lonsun.supervise.column.internal.service.IColumnUpdateService;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnResultEO;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateEO;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.vo.SupervisePageVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2016-4-5 9:40
 */
@Controller
@RequestMapping("/column/update")
public class ColumnUpdateController extends BaseController {

    private static final String FILE_BASE = "/supervise/columnupdate/";

    @Autowired
    private IColumnUpdateService columnUpdateService;

    @Autowired
    private IColumnResultService columnResultService;

    @Autowired
    private ICronConfService cronConfService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IPublicCatalogService publicCatalogService;

    @Autowired
    private IIndicatorService indicatorService;

    @Resource
    private IOrganService organService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/index";
    }

    @RequestMapping("/addOrEdit")
    public String addOrEdit() {
        return FILE_BASE + "/edit_task";
    }

    @RequestMapping("/emptyIndex")
    public String emptyIndex(ModelMap modelMap) {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        List<OrganVO> organVOList = new ArrayList<OrganVO>();
        PublicCatalogUtil.filterOrgan(organList, organVOList);// 过滤单位列表
        PublicCatalogUtil.sortOrgan(organVOList);// 排序
        modelMap.put("organList", organVOList);
        return FILE_BASE + "/empty_index";
    }

    /**
     * 选择栏目
     * @return
     */
    @RequestMapping("/columnSelect")
    public String columnSelect() {
        return FILE_BASE + "column_select";
    }

    /**
     * 选择栏目
     * @return
     */
    @RequestMapping("/publicInfoSelect")
    public String publicInfoSelect() {
        return FILE_BASE + "publicinfo_select";
    }

    /**
     * 查看栏目检查结果
     * @return
     */
    @RequestMapping("/checkResult")
    public String checkResult(ModelMap map,Long taskId,String taskType,String siteId) {
        map.put("taskId",taskId);
        map.put("taskType",taskType);
        map.put("siteId",siteId);
        return FILE_BASE + "check_result";
    }

    @ResponseBody
    @RequestMapping("/getPublicInfo")
    public Object getPublicInfo(OrganCatalogQueryVO qvo) {
        List<OrganCatalogVO> vos = new ArrayList<OrganCatalogVO>();
        qvo.setAll(false);
        qvo.setSiteId(LoginPersonUtil.getSiteId());
        qvo.setCatalog(true);
        List<OrganCatalogVO> catalogVOs = publicCatalogService.getOrganCatalogTree(qvo);
        vos.addAll(catalogVOs);
        if (AppUtil.isEmpty(qvo.getOrganId())) {
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, LoginPersonUtil.getSiteId());
            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, LoginPersonUtil.getSiteId());
            OrganCatalogVO vo = new OrganCatalogVO();
            vo.setId(Long.valueOf(siteConfigEO.getUnitIds()));
            vo.setName(indicatorEO.getName());
            vo.setType(IndicatorEO.Type.CMS_Site.toString());
            if (null != catalogVOs && !catalogVOs.isEmpty()) {
                vo.setIsParent(true);
            }
            vos.add(vo);
        }

        return vos;
    }

    /**
     * 分页获取采集任务
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageTaskEOs")
    public Object getPageTaskEOs(SupervisePageVO vo) {
        Pagination page = columnUpdateService.getPageEOs(vo);
        List<ColumnUpdateEO> list = (List<ColumnUpdateEO>) page.getData();

        for(ColumnUpdateEO eo : list) {
            String columnNames = "";
            if(null != eo.getColumnIds() && !eo.getTaskType().equals(ColumnUpdateEO.TastkType.publicinfo.toString())) {
                if(eo.getColumnIds().contains("null") || eo.getcSiteIds().contains("null")) {
                    continue;
                }
                List<Long> columnIds = StringUtils.getListWithLong(eo.getColumnIds(),",");
                List<Long> cSiteIds = StringUtils.getListWithLong(eo.getcSiteIds(),",");
                int count = 0;
                for(Long columnId : columnIds){
                    if("".equals(columnNames)) {
                        columnNames = ColumnUtil.getColumnName(columnId,cSiteIds.get(count++));
                    } else {
                        columnNames += "\r\n" + ColumnUtil.getColumnName(columnId,cSiteIds.get(count++));
                    }
                }
            } else {
                List<String> pnames = StringUtils.getListWithString(eo.getcSiteIds(),",");
                if(null != pnames) {
                    for(String s : pnames) {
                        if("".equals(columnNames)) {
                            columnNames = s;
                        } else {
                            columnNames += "\r\n" + s;
                        }
                    }
                 }
            }
            eo.setColumnNames(columnNames);
        }
        return page;
    }

    @ResponseBody
    @RequestMapping("/getPageResultEOs")
    public Object getPageResultEOs(SupervisePageVO vo) {
        Pagination page = columnResultService.getPageEOs(vo);

        List<ColumnResultEO> list = (List<ColumnResultEO>)page.getData();

        if(null != list) {
            for(ColumnResultEO eo : list) {
                if(null != eo.getcSiteId()) {
                    IndicatorEO indicatorEO = indicatorService.getEntity(IndicatorEO.class,eo.getcSiteId());
                    if(null != indicatorEO && indicatorEO.getType().equals(IndicatorEO.Type.CMS_Site.toString())) {
                        eo.setSiteName(indicatorEO.getName());
                    }
                    if(null != eo.getColumnId()) {
                        ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, eo.getColumnId());
                        if(null != column ) {
                            eo.setColumnName("<a href='"+indicatorEO.getUri()+"/"+ column.getUrlPath() +"/index.html' target='_blank'>"+ eo.getColumnName() +"</a>");
                        }
                    }
                }
            }
        }

        return page;
    }

    @RequestMapping("getColumnTreeBySite")
    @ResponseBody
    public Object getColumnTreeBySite(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId,String taskType) {
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        if(indicatorId != null) {
            return list;
        }
        if(taskType.equals(ColumnUpdateEO.TastkType.article.toString())) {
            list.addAll(columnConfigService.getAllTree(BaseContentEO.TypeCode.articleNews.toString()));
        } else {
            String[] strings = new String[2];
            strings[0] = BaseContentEO.TypeCode.guestBook.toString();
            strings[1] = BaseContentEO.TypeCode.messageBoard.toString();
            list.addAll(columnConfigService.getByColumnTypeCodes(strings, LoginPersonUtil.getSiteId(),false));
        }
        return list;
    }

    @ResponseBody
    @RequestMapping("/saveTask")
    public Object saveTask(ColumnUpdateEO updateEO,CronConfEO cronEO) {
        columnUpdateService.saveTask(updateEO,cronEO);
        return ajaxOk();
    }

    @ResponseBody
    @RequestMapping("/updateTask")
    public Object updateTask(ColumnUpdateEO updateEO,CronConfEO cronEO) {
        columnUpdateService.updateTask(updateEO, cronEO);
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
        columnUpdateService.pauseTask(taskId);
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
        columnUpdateService.resumeTask(taskId);
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
        columnUpdateService.physDelEOs(ids);
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/getCronConf")
    public Object getCronConf(Long cronId) {
        return getObject(cronConfService.getEntity(CronConfEO.class, cronId));
    }


    @ResponseBody
    @RequestMapping("/getAutoCron")
    public Object getAutoCron() {
        List<DataDictVO> itemEOs = DataDictionaryUtil.getDDList("autoCron");
        return itemEOs;
    }

    @ResponseBody
    @RequestMapping("/getTaskType")
    public Object getTaskType() {
        List<DataDictVO> itemEOs = DataDictionaryUtil.getDDList("column_update_task_type");
        return itemEOs;
    }
}
