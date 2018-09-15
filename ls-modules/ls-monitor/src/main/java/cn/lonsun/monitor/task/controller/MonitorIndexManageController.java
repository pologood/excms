package cn.lonsun.monitor.task.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredVetoConfigService;
import cn.lonsun.monitor.internal.entity.MonitorColumnDetailEO;
import cn.lonsun.monitor.internal.entity.MonitorInteractDetailEO;
import cn.lonsun.monitor.internal.entity.MonitorSiteRegisterEO;
import cn.lonsun.monitor.internal.service.IMonitorColumnDetailService;
import cn.lonsun.monitor.internal.service.IMonitorInteractDetailService;
import cn.lonsun.monitor.internal.service.IMonitorSiteRegisterService;
import cn.lonsun.monitor.internal.vo.*;
import cn.lonsun.monitor.task.internal.entity.*;
import cn.lonsun.monitor.task.internal.entity.vo.*;
import cn.lonsun.monitor.task.internal.service.*;
import cn.lonsun.monitor.util.*;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.util.*;
import cn.lonsun.monitor.util.PoiExcelUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO.TypeCode.columnType_ZWZX;
import static cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO.CodeType.*;

/**
 * @author gu.fei
 * @version 2017-09-28 9:25
 */
@Controller
@RequestMapping(value = "/monitor/index/manage")
public class MonitorIndexManageController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String FILE_BASE = "/monitor/task/";

    @Resource
    private IMonitorIndexManageService monitorIndexManageService;

    @Resource
    private IMonitorCustomIndexManageService monitorCustomIndexManageService;

    @Resource
    private IMonitorTaskManageService monitorTaskManageService;

    @Resource
    private IMonitorSiteVisitResultService monitorSiteVisitResultService;

    @Resource
    private IMonitorHrefUseableResultService monitorHrefUseableResultService;

    @Resource
    private IMonitorSeriousErrorResultService monitorSeriousErrorResultService;

    @Resource
    private IMonitoredVetoConfigService monitoredVetoConfigService;

    @Resource
    private IMonitorSiteStatisService monitorSiteStatisService;

    @Resource
    private IMonitorColumnDetailService monitorColumnDetailService;

    @Resource
    private IMonitorInteractDetailService monitorInteractDetailService;

    @Resource
    private IMonitorCustomTaskManageService monitorCustomTaskManageService;

    @Resource
    private IMonitorHrefUseableDynamicService monitorHrefUseableDynamicService;

    @Resource
    private ISiteConfigService siteConfigService;

    @Resource
    private IMonitorSiteRegisterService monitorSiteRegisterService;

    @Resource
    private TaskExecutor taskExecutor;

    @Autowired
    private IOrganService organService;

    /**
     * 任务主页面
     * @return
     */
    @RequestMapping(value = "index")
    public String index(ModelMap map) {
        MonitorIndexManageEO index = monitorIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),LoginPersonUtil.getSiteId());
        if(null != index) {
            map.put("taskId",index.getTaskId());
        }
        MonitorCustomIndexManageEO customIndex = monitorCustomIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),LoginPersonUtil.getSiteId());
        if(null != customIndex) {
            map.put("customTaskId",customIndex.getTaskId());
        }
        return FILE_BASE + "index";
    }

    /**
     * 任务历史纪录
     * @return
     */
    @RequestMapping(value = "taskList")
    public String taskList() {
        return FILE_BASE + "task_list";
    }

    /**
     * 检测报告
     * @return
     */
    @RequestMapping(value = "monitorReport")
    public String monitorReport() {
        return FILE_BASE + "monitor_report";
    }

    /**
     * 检测报告
     * @return
     */
    @RequestMapping(value = "monitorReportView")
    public String monitorReportView(ModelMap map) {
        MonitorIndexManageEO index = monitorIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),LoginPersonUtil.getSiteId());
        map.put("taskId",index.getTaskId());

        Map<String,Object> param = new HashMap<String,Object>();
        param.put("isRegistered",1);
        List<MonitorSiteRegisterEO> list  = monitorSiteRegisterService.getEntities(MonitorSiteRegisterEO.class,param);
        if(list!=null){
            map.put("monitoredSiteCount",list.size());
        }else{
            map.put("monitoredSiteCount",0);
        }

        return FILE_BASE + "monitor_report_view";
    }

    /**
     * 站点访问详情
     * @return
     */
    @RequestMapping(value = "siteVisitDetail")
    public String siteVisitDetail(Long monitorId,ModelMap map) {
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteDeny.toString(), LoginPersonUtil.getSiteId());
        map.put("monitoredNum",bc.get("monitoredNum"));
        map.put("dayMonitoredCount",bc.get("dayMonitoredCount"));
        map.put("monitoredOvertime",bc.get("monitoredOvertime"));
        map.put("notOpenRate",bc.get("notOpenRate"));

        if(monitorId!=null){
            SiteVisitStatisVO lstatis = monitorSiteVisitResultService.getSiteVisitStatis(monitorId);
            map.put("total",lstatis.getTotal());
            map.put("failure",lstatis.getFail());
            if(lstatis.getFail()!=null&&lstatis.getTotal()!=null&&lstatis.getTotal().intValue()>0){
                // 创建一个数值格式化对象
                NumberFormat numberFormat = NumberFormat.getInstance();
                // 设置精确到小数点后2位
                numberFormat.setMaximumFractionDigits(2);
                map.put("failRate",numberFormat.format((float) lstatis.getFail() / (float) lstatis.getTotal() * 100));
            }
        }

        return FILE_BASE + "site_visit_detail";
    }

    /**
     * 自定义检测任务列表
     * @return
     */
    @RequestMapping(value = "customTaskList")
    public String customTaskList() {
        return FILE_BASE + "custom_task_list";
    }

    /**
     * 严重错误详情
     * @return
     */
    @RequestMapping(value = "seriousErrorDetail")
    public String seriousErrorDetail(Long monitorId,ModelMap map) {
        if(monitorId!=null){
            Long sensitive = monitorSeriousErrorResultService.getCount(monitorId,"SENSITIVE");
            Long easyerr = monitorSeriousErrorResultService.getCount(monitorId,"EASYERR");
            map.put("sensitive",sensitive);
            map.put("easyerr",easyerr);
        }

        return FILE_BASE + "serious_error_detail";
    }

    /**
     * 错误链接详情
     * @return
     */
    @RequestMapping(value = "hrefUseableDetail")
    public String hrefUseableDetail(Long monitorId,ModelMap map) {
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteUse.toString(), LoginPersonUtil.getSiteId());
        map.put("monitoredNum",bc.get("monitoredNum"));
        map.put("dayMonitoredCount",bc.get("dayMonitoredCount"));
        map.put("monitoredOvertime",bc.get("monitoredOvertime"));
        map.put("notOpenRate",bc.get("notOpenRate"));
        map.put("indexErrorNum",bc.get("indexErrorNum"));
        map.put("otherErrorNum",bc.get("otherErrorNum"));

        //小计
        if(monitorId!=null){

            SiteVisitStatisVO siteVisitStatis = monitorSiteVisitResultService.getSiteVisitStatis(monitorId);
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);

            String failrate = "0";
            //首页不可访问占比
            if(siteVisitStatis.getTotal()!=null&&siteVisitStatis.getTotal().intValue()!=0){
                failrate = numberFormat.format((float) siteVisitStatis.getFail() / (float) siteVisitStatis.getTotal() * 100);
            }
            map.put("failrate",failrate);
            map.put("failScore",Float.parseFloat(failrate)*5);

            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(1);

            HrefUseableStatisVO lstatis = monitorHrefUseableResultService.getHrefUseaStatis(monitorId);
            //首页错链数
            Long indexErrLinkCount = lstatis.getIndexCount();
            map.put("indexErrLinkCount",indexErrLinkCount);
            map.put("indexErrLinkScore",numberFormat.format(indexErrLinkCount*Double.parseDouble((String)bc.get("indexErrorNum"))));

            //其他页面错链数
            Long otherErrLinkCount = lstatis.getOtherCount();
            map.put("otherErrLinkCount",otherErrLinkCount);
            map.put("otherErrLinkScore",numberFormat.format(otherErrLinkCount*Double.parseDouble((String)bc.get("otherErrorNum"))));

        }

        return FILE_BASE + "href_useable_detail";
    }

    /**
     * 今日错误链接详情
     * @return
     */
    @RequestMapping(value = "hrefUseableDynamicDetail")
    public String hrefUseableDynamicDetail() {
        return FILE_BASE + "href_useable_dynamic_detail";
    }

    /**
     * 网站不更新详情
     * @return
     */
    @RequestMapping(value = "indexNotUpdateDetail")
    public String indexNotUpdateDetail(Long monitorId,ModelMap map) {
        //查询配置
        Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.siteUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_index.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                ,LoginPersonUtil.getSiteId());
        int num = 1;
        int monitoredNum = 2;
        String monitoredTimeType = "周";
        if(bc!=null){
            Long num1 = getLong(bc, "num");
            Long monitoredNum1 = getLong(bc, "monitoredNum");
            String monitoredTimeType1 = getString(bc, "monitoredTimeType");
            num = num1==null?1:num1.intValue();
            monitoredNum = monitoredNum1==null?2:monitoredNum1.intValue();
            monitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("num",num);
        map.put("monitoredNum",monitoredNum);
        map.put("monitoredTimeType",monitoredTimeType);

        if(monitorId!=null){
            Long total = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);
            Long notUpdate = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,0l);
            map.put("total",total);
            map.put("notUpdate",notUpdate);
        }

        return FILE_BASE + "index_not_update_detail";
    }

    /**
     * 栏目不更新详情
     * @return
     */
    @RequestMapping(value = "columnNotUpdateDetail")
    public String columnNotUpdateDetail(Long monitorId,ModelMap map) {
        Map<String, Object> dtywbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                ,LoginPersonUtil.getSiteId());
        int dtywNum = 1;
        int dtywMonitoredNum = 2;
        String dtywMonitoredTimeType = "周";
        if(dtywbc!=null){
            Long num1 = getLong(dtywbc, "num");
            Long monitoredNum1 = getLong(dtywbc, "monitoredNum");
            String monitoredTimeType1 = getString(dtywbc, "monitoredTimeType");
            dtywNum = num1==null?1:num1.intValue();
            dtywMonitoredNum = monitoredNum1==null?2:monitoredNum1.intValue();
            dtywMonitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("dtywNum",dtywNum);
        map.put("dtywMonitoredNum",dtywMonitoredNum);
        map.put("dtywMonitoredTimeType",dtywMonitoredTimeType);

        Map<String, Object> tzzcbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                    ,LoginPersonUtil.getSiteId());
        int tzzcMonitoredNum = 6;
        String tzzcMonitoredTimeType = "个月";
        if(tzzcbc!=null){
            Long monitoredNum1 = getLong(tzzcbc, "monitoredNum");
            String monitoredTimeType1 = getString(tzzcbc, "monitoredTimeType");
            tzzcMonitoredNum = monitoredNum1==null?2:monitoredNum1.intValue();
            tzzcMonitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("tzzcMonitoredNum",tzzcMonitoredNum);
        map.put("tzzcMonitoredTimeType",tzzcMonitoredTimeType);

        Map<String, Object> updatebc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                ,LoginPersonUtil.getSiteId());
        int updateNum = 10;
        if(updatebc!=null){
            Long num1 = getLong(updatebc, "num");
            updateNum = num1==null?10:num1.intValue();
        }
        map.put("updateNum",updateNum);

        //查询配置
        Map<String, Object> blankbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                ,LoginPersonUtil.getSiteId());
        int balnkNum = 5;
        if(updatebc!=null){
            Long num1 = getLong(blankbc, "num");
            balnkNum = num1==null?10:num1.intValue();
        }
        map.put("balnkNum",balnkNum);

        if(monitorId!=null){
            Long blankCount = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString()},null,0l);
            Long notUpdateCount = monitorColumnDetailService.getCount(monitorId,
                    new String[]{MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0l);
            Long dtywCount = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString()},null,0l);
            Long tzzcCount = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0l);
            map.put("blankCount",blankCount);
            map.put("notUpdateCount",notUpdateCount);
            map.put("dtywCount",dtywCount);
            map.put("tzzcCount",tzzcCount);
        }

        return FILE_BASE + "column_not_update_detail";
    }

    /**
     * 互动回应差详情
     * @return
     */
    @RequestMapping(value = "badInteractDetail")
    public String badInteractDetail(Long monitorId,ModelMap map) {
        //查询配置
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.reply.toString(),
                LoginPersonUtil.getSiteId());
        int num = 3;
        int monitoredNum = 1;
        String monitoredTimeType = "年";
        if(bc!=null){
            Long num1 = getLong(bc, "num");
            Long monitoredNum1 = getLong(bc, "monitoredNum");
            String monitoredTimeType1 = getString(bc, "monitoredTimeType");
            num = num1==null?3:num1.intValue();
            monitoredNum = monitoredNum1==null?1:monitoredNum1.intValue();
            monitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("num",num);
        map.put("monitoredNum",monitoredNum);
        map.put("monitoredTimeType",monitoredTimeType);
        if(monitorId!=null){
            MonitorDetailQueryVO queryVO = new MonitorDetailQueryVO();
            queryVO.setMonitorId(monitorId);
            queryVO.setColumnType(Arrays.asList(new String[]{MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString()}));
            queryVO.setUnreplyCount(0l);
            List<MonitorInteractDetailEO> zwzxColumns = monitorInteractDetailService.getList(queryVO);
            Integer unreplayCounts = 0;
            if(zwzxColumns!=null){
                for(MonitorInteractDetailEO eo:zwzxColumns){
                    if(eo.getUnreplyCount()!=null){
                        unreplayCounts += eo.getUnreplyCount().intValue();
                    }
                }
            }
            map.put("unreplayCounts",unreplayCounts);
        }

        return FILE_BASE + "bad_interact_detail";
    }

    /**
     * 首页栏目更新情况详情
     * @return
     */
    @RequestMapping(value = "indexColumnDetail")
    public String indexColumnDetail(ModelMap map) {
        return FILE_BASE + "index_column_detail";
    }

    /**
     * 基本信息栏目更新情况详情
     * @return
     */
    @RequestMapping(value = "columnBaseInfoDetail")
    public String columnBaseInfoDetail(Long monitorId,ModelMap map) {
        //获取监测配置信息
        Map<String, Object> indexbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_index.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString()
                ,LoginPersonUtil.getSiteId());
        int indexMonitoredNum = 2;
        String indexMonitoredTimeType = "周";
        int indexLimitNum = 10;
        int indexNum = 5;
        if(indexbc!=null){
            Long num1 = getLong(indexbc, "num");
            Long limitNum1 = getLong(indexbc, "limitNum");
            Long monitoredNum1 = getLong(indexbc, "monitoredNum");
            String monitoredTimeType1 = getString(indexbc, "monitoredTimeType");
            indexNum = num1==null?5:num1.intValue();
            indexMonitoredNum = monitoredNum1==null?2:monitoredNum1.intValue();
            indexLimitNum = limitNum1==null?10:limitNum1.intValue();
            indexMonitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("indexMonitoredNum",indexMonitoredNum);
        map.put("indexMonitoredTimeType",indexMonitoredTimeType);
        map.put("indexLimitNum",indexLimitNum);
        map.put("indexNum",indexNum);


        //获取动态要闻监测配置信息
        Map<String, Object> dtywbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString()
                ,LoginPersonUtil.getSiteId());
        int dtywMonitoredNum = 2;
        String dtywMonitoredTimeType = "周";
        int dtywNum = 3;
        if(dtywbc!=null){
            Long num1 = getLong(dtywbc, "num");
            Long monitoredNum1 = getLong(dtywbc, "monitoredNum");
            String monitoredTimeType1 = getString(dtywbc, "monitoredTimeType");
            dtywNum = num1==null?3:num1.intValue();
            dtywMonitoredNum = monitoredNum1==null?2:monitoredNum1.intValue();
            dtywMonitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("dtywMonitoredNum",dtywMonitoredNum);
        map.put("dtywMonitoredTimeType",dtywMonitoredTimeType);
        map.put("dtywNum",dtywNum);

        //获取通知政策监测配置信息
        Map<String, Object> tzzcbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString()
                ,LoginPersonUtil.getSiteId());
        int tzzcMonitoredNum = 6;
        String tzzcMonitoredTimeType = "个月";
        int tzzcNum = 4;
        if(tzzcbc!=null){
            Long num1 = getLong(tzzcbc, "num");
            Long monitoredNum1 = getLong(tzzcbc, "monitoredNum");
            String monitoredTimeType1 = getString(tzzcbc, "monitoredTimeType");
            tzzcNum = num1==null?4:num1.intValue();
            tzzcMonitoredNum = monitoredNum1==null?2:monitoredNum1.intValue();
            tzzcMonitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("tzzcMonitoredNum",tzzcMonitoredNum);
        map.put("tzzcMonitoredTimeType",tzzcMonitoredTimeType);
        map.put("tzzcNum",tzzcNum);

        //获取人事规划监测配置信息
        Map<String, Object> rsghbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString()
                ,LoginPersonUtil.getSiteId());
        int rsghMonitoredNum = 1;
        String rsghMonitoredTimeType = "年";
        int rsghNum = 5;
        if(rsghbc!=null){
            Long num1 = getLong(rsghbc, "num");
            Long monitoredNum1 = getLong(rsghbc, "monitoredNum");
            String monitoredTimeType1 = getString(rsghbc, "monitoredTimeType");
            rsghNum = num1==null?5:num1.intValue();
            rsghMonitoredNum = monitoredNum1==null?2:monitoredNum1.intValue();
            rsghMonitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("rsghMonitoredNum",rsghMonitoredNum);
        map.put("rsghMonitoredTimeType",rsghMonitoredTimeType);
        map.put("rsghNum",rsghNum);

        //小计
        if(monitorId!=null){
            //首页扣分
            List<MonitorColumnDetailEO> indexColumns = monitorColumnDetailService.getList(monitorId
                    ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);
            Long indexUpdateCount = 0l;
            if(indexColumns!=null&&indexColumns.size()>0){
                for(MonitorColumnDetailEO eo:indexColumns){
                    if(eo==null||eo.getUpdateCount()==null||eo.getUpdateCount().intValue()==0){

                    }else{
                        indexUpdateCount = indexUpdateCount + eo.getUpdateCount();
                    }
                }
            }
            map.put("indexUpdateCount",indexUpdateCount);

            //动态要闻类扣分
            Long dtywCounts = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString()},null,0l);
            map.put("dtywCounts",dtywCounts);
            map.put("dtywScore",dtywCounts*dtywNum);

            //通知政策扣分
            Long tzzcCounts = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0l);
            map.put("tzzcCounts",tzzcCounts);
            map.put("tzzcScore",tzzcCounts*tzzcNum);

            //人事规划扣分
            Long rsghCounts = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString()},null,0l);
            map.put("rsghCounts",rsghCounts);
            map.put("rsghScore",rsghCounts*rsghNum);
        }


        return FILE_BASE + "column_base_info_detail";
    }

    /**
     * 互动回应情况详情
     * @return
     */
    @RequestMapping(value = "interactInfoDetail")
    public String interactInfoDetail(Long monitorId,ModelMap map) {
        //获取监测配置信息
        Map<String, Object> zwzxbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.replyScope.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString(),LoginPersonUtil.getSiteId());
        int zwzxMonitoredNum = 1;
        String zwzxMonitoredTimeType = "年";
        int zwzxNum = 5;
        if(zwzxbc!=null){
            Long num1 = getLong(zwzxbc, "num");
            Long monitoredNum1 = getLong(zwzxbc, "monitoredNum");
            String monitoredTimeType1 = getString(zwzxbc, "monitoredTimeType");
            zwzxNum = num1==null?5:num1.intValue();
            zwzxMonitoredNum = monitoredNum1==null?1:monitoredNum1.intValue();
            zwzxMonitoredTimeType = getUpdateTimeType(monitoredTimeType1);
        }
        map.put("zwzxMonitoredNum",zwzxMonitoredNum);
        map.put("zwzxMonitoredTimeType",zwzxMonitoredTimeType);
        map.put("zwzxNum",zwzxNum);

        //互动访谈
        Map<String, Object> hdftbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.replyScope.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_HDFT.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString(),LoginPersonUtil.getSiteId());
        int hdftNoActivityMonitoredNum = 1;
        String hdftNoActivityMonitoredTimeType = "年";
        int hdftNoActivityNum = 5;
        int hdftActivityMonitoredNum = 1;
        String hdftActivityMonitoredTimeType = "年";
        int hdftActivityLimitNum = 6;
        int hdftActivityNum = 3;
        if(hdftbc!=null){
            Long noActivityAonitoredNum = getLong(hdftbc, "noActivityAonitoredNum");
            String noActivityTimeType = getString(hdftbc, "noActivityTimeType");
            Long noActivityNum = getLong(hdftbc, "noActivityNum");
            Long activityAonitoredNum = getLong(hdftbc, "activityAonitoredNum");
            String activityTimeType = getString(hdftbc, "activityTimeType");
            Long activityLimitNum = getLong(hdftbc, "activityLimitNum");
            Long activityNum = getLong(hdftbc, "activityNum");
            hdftNoActivityMonitoredNum = noActivityAonitoredNum==null?1:noActivityAonitoredNum.intValue();
            hdftNoActivityMonitoredTimeType = getUpdateTimeType(noActivityTimeType);
            hdftNoActivityNum = noActivityNum==null?5:noActivityNum.intValue();

            hdftActivityMonitoredNum = activityAonitoredNum==null?1:activityAonitoredNum.intValue();
            hdftActivityMonitoredTimeType = getUpdateTimeType(activityTimeType);
            hdftActivityLimitNum = activityLimitNum==null?5:activityLimitNum.intValue();
            hdftActivityNum = activityNum==null?5:activityNum.intValue();
        }
        map.put("hdftNoActivityMonitoredNum",hdftNoActivityMonitoredNum);
        map.put("hdftNoActivityMonitoredTimeType",hdftNoActivityMonitoredTimeType);
        map.put("hdftNoActivityNum",hdftNoActivityNum);
        map.put("hdftActivityMonitoredNum",hdftActivityMonitoredNum);
        map.put("hdftActivityMonitoredTimeType",hdftActivityMonitoredTimeType);
        map.put("hdftActivityLimitNum",hdftActivityLimitNum);
        map.put("hdftActivityNum",hdftActivityNum);

        //调查征集
        Map<String, Object> dczjbc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.replyScope.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_DCZJ.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString(),LoginPersonUtil.getSiteId());
        int dczjNoActivityMonitoredNum = 1;
        String dczjNoActivityMonitoredTimeType = "年";
        int dczjNoActivityNum = 5;
        int dczjActivityMonitoredNum = 1;
        String dczjActivityMonitoredTimeType = "年";
        int dczjActivityLimitNum = 6;
        int dczjActivityNum = 3;
        if(dczjbc!=null){
            Long noActivityAonitoredNum = getLong(dczjbc, "noActivityAonitoredNum");
            String noActivityTimeType = getString(dczjbc, "noActivityTimeType");
            Long noActivityNum = getLong(dczjbc, "noActivityNum");
            Long activityAonitoredNum = getLong(dczjbc, "activityAonitoredNum");
            String activityTimeType = getString(dczjbc, "activityTimeType");
            Long activityLimitNum = getLong(dczjbc, "activityLimitNum");
            Long activityNum = getLong(dczjbc, "activityNum");
            dczjNoActivityMonitoredNum = noActivityAonitoredNum==null?1:noActivityAonitoredNum.intValue();
            dczjNoActivityMonitoredTimeType = getUpdateTimeType(noActivityTimeType);
            dczjNoActivityNum = noActivityNum==null?5:noActivityNum.intValue();

            dczjActivityMonitoredNum = activityAonitoredNum==null?1:activityAonitoredNum.intValue();
            dczjActivityMonitoredTimeType = getUpdateTimeType(activityTimeType);
            dczjActivityLimitNum = activityLimitNum==null?5:activityLimitNum.intValue();
            dczjActivityNum = activityNum==null?5:activityNum.intValue();
        }
        map.put("dczjNoActivityMonitoredNum",dczjNoActivityMonitoredNum);
        map.put("dczjNoActivityMonitoredTimeType",dczjNoActivityMonitoredTimeType);
        map.put("dczjNoActivityNum",dczjNoActivityNum);
        map.put("dczjActivityMonitoredNum",dczjActivityMonitoredNum);
        map.put("dczjActivityMonitoredTimeType",dczjActivityMonitoredTimeType);
        map.put("dczjActivityLimitNum",dczjActivityLimitNum);
        map.put("dczjActivityNum",dczjActivityNum);

        //小计
        if(monitorId!=null){
            MonitorDetailQueryVO queryVO = new MonitorDetailQueryVO();
            queryVO.setMonitorId(monitorId);

            //政务咨询
            queryVO.setColumnType(Arrays.asList(new String[]{MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString()}));
            List<MonitorInteractDetailEO> zwzxList = monitorInteractDetailService.getList(queryVO);
            Integer zwzxColumnCount = 0;//开设栏目数
            Integer zwzxUpdateCount = 0;//更新数
            if(zwzxList!=null&&zwzxList.size()>0){
                zwzxColumnCount = zwzxList.size();
                for(MonitorInteractDetailEO eo:zwzxList){
                    if(eo.getUpdateCount()!=null){
                        zwzxUpdateCount += eo.getUpdateCount().intValue();
                    }
                }
            }
            map.put("zwzxColumnCount",zwzxColumnCount);
            map.put("zwzxUpdateCount",zwzxUpdateCount);

            //调查征集
            queryVO.setColumnType(Arrays.asList(new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DCZJ.toString()}));
            List<MonitorInteractDetailEO> dczjList = monitorInteractDetailService.getList(queryVO);
            Integer dczjColumnCount = 0;//开设栏目数
            Integer dczjUpdateCount = 0;//更新数
            if(dczjList!=null&&dczjList.size()>0){
                dczjColumnCount = dczjList.size();
                for(MonitorInteractDetailEO eo:dczjList){
                    if(eo.getUpdateCount()!=null){
                        dczjUpdateCount += eo.getUpdateCount().intValue();
                    }
                }
            }
            map.put("dczjColumnCount",dczjColumnCount);
            map.put("dczjUpdateCount",dczjUpdateCount);

            //互动访谈
            queryVO.setColumnType(Arrays.asList(new String[]{MonitoredColumnConfigEO.TypeCode.columnType_HDFT.toString()}));
            List<MonitorInteractDetailEO> hdftList = monitorInteractDetailService.getList(queryVO);
            Integer hdftColumnCount = 0;//开设栏目数
            Integer hdftUpdateCount = 0;//更新数
            if(hdftList!=null&&hdftList.size()>0){
                hdftColumnCount = hdftList.size();
                for(MonitorInteractDetailEO eo:hdftList){
                    if(eo.getUpdateCount()!=null){
                        hdftUpdateCount += eo.getUpdateCount().intValue();
                    }
                }
            }
            map.put("hdftColumnCount",hdftColumnCount);
            map.put("hdftUpdateCount",hdftUpdateCount);

        }


        return FILE_BASE + "interact_info_detail";
    }

    /**
     * 获取指标
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getIndexList")
    public Object getIndexList() {
        Long siteId = LoginPersonUtil.getSiteId();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<MonitorIndexManageEO> list = monitorIndexManageService.getEntities(MonitorIndexManageEO.class,map);
        if(null == list || list.isEmpty()) {
            monitorIndexManageService.saveIndex(siteId);
            list = monitorIndexManageService.getEntities(MonitorIndexManageEO.class,map);
        }
        MonitorIndexManageEO index = monitorIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),siteId);
        Long taskId = index.getTaskId();
        if(null != taskId) {
            MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,taskId);
            if(null != task) {
                for(MonitorIndexManageEO dex : list) {
                    String bIndex = dex.getbIndex();
                    if(bIndex.equals(MonitoredVetoConfigEO.CodeType.siteDeny.toString())) {
                        dex.setTaskStatus(task.getSiteDenyStatus());
                    } else if(bIndex.equals(MonitoredVetoConfigEO.CodeType.siteUpdate.toString())) {
                        dex.setTaskStatus(task.getSiteUpdateStatus());
                    } else if(bIndex.equals(MonitoredVetoConfigEO.CodeType.columnUpdate.toString())) {
                        dex.setTaskStatus(task.getColumnUpdateStatus());
                    } else if(bIndex.equals(MonitoredVetoConfigEO.CodeType.error.toString())) {
                        dex.setTaskStatus(task.getErrorStatus());
                    } else if(bIndex.equals(MonitoredVetoConfigEO.CodeType.reply.toString())) {
                        dex.setTaskStatus(task.getReplyStatus());
                    } else if(bIndex.equals(siteUse.toString())) {
                        dex.setTaskStatus(task.getSiteUseStatus());
                    } else if(bIndex.equals(replyScope.toString())) {
                        dex.setTaskStatus(task.getReplyScopeStatus());
                    } else if(bIndex.equals(infoUpdate.toString())) {
                        dex.setTaskStatus(task.getInfoUpdateStatus());
                    } else if(bIndex.equals(service.toString())) {
                        dex.setTaskStatus(task.getServiceStatus());
                    }
                    dex.setStartDate(task.getStartDate());
                }
            }
        }
        return getObject(list);
    }

    /**
     * 获取自定义指标
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getCustomIndexList")
    public Object getCustomIndexList() {
        Long siteId = LoginPersonUtil.getSiteId();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<MonitorCustomIndexManageEO> list = monitorCustomIndexManageService.getEntities(MonitorCustomIndexManageEO.class,map);
        if(null == list || list.isEmpty()) {
            monitorCustomIndexManageService.saveIndex(siteId);
            list = monitorCustomIndexManageService.getEntities(MonitorCustomIndexManageEO.class,map);
        }
        return getObject(list);
    }

    /**
     * 分页获取任务列表
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getTaskPage")
    public Object getTaskPage(PageQueryVO vo) {
        Long siteId = LoginPersonUtil.getSiteId();
        Pagination page = monitorTaskManageService.getTaskPage(siteId,vo);
        List<MonitorTaskManageEO> tasks = (List<MonitorTaskManageEO>)page.getData();
        if(null != tasks) {
            for(MonitorTaskManageEO task : tasks) {
                if(task.getSiteDenyStatus() == 1 || task.getSiteUpdateStatus() == 1 || task.getColumnUpdateStatus() == 1
                        || task.getErrorStatus() == 1 || task.getReplyStatus() == 1 || task.getInfoUpdateStatus() == 1
                        || task.getReplyScopeStatus() == 1) {
                    task.setTaskStatus("正在检测");
                } else if(task.getSiteDenyStatus() == 2 && task.getSiteUpdateStatus() == 2 && task.getColumnUpdateStatus() == 2
                        && task.getErrorStatus() == 2 && task.getReplyStatus() == 2 && task.getInfoUpdateStatus() == 2
                        && task.getReplyScopeStatus() == 2) {
                    task.setTaskStatus("检测完成");
                } else {
                    task.setTaskStatus("检测异常");
                }
            }
        }
        return getObject(page);
    }

    /**
     * 分页获取自定义任务列表
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getCustomTaskPage")
    public Object getCustomTaskPage(PageQueryVO vo,String typeCode) {
        Long siteId = LoginPersonUtil.getSiteId();
        return getObject(monitorCustomTaskManageService.getTaskPage(siteId,typeCode,vo));
    }

    /**
     * 分页获取站点访问数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteVisitPage")
    public Object getSiteVisitPage(SiteVisitQueryVO vo) {
        return getObject(monitorSiteVisitResultService.getSiteVisitPage(vo));
    }

    /**
     * 获取站群严重错误
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getMonitorSiteGroupErrors")
    public Object getMonitorSiteGroupErrors() {
        return getObject(monitorSeriousErrorResultService.getMonitorSiteGroupErrors());
    }

    /**
     * 分页获取网站不更新数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getIndexNotUpdatePage")
    public Object getIndexNotUpdatePage(MonitorDetailQueryVO vo) {
        if(vo!=null&&vo.getMonitorId()==null){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }
        List<String> columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
        vo.setColumnType(columnType);
        return getObject(monitorColumnDetailService.getPage(vo));
    }

    /**
     * 分页获取栏目不更新数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getColumnNotUpdatePage")
    public Object getColumnNotUpdatePage(MonitorDetailQueryVO vo) {
        if(vo!=null&&vo.getMonitorId()==null){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }
        List<String> columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_update.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString());
        vo.setColumnType(columnType);
        vo.setUpdateCount(0l);
        return getObject(monitorColumnDetailService.getPage(vo));
    }

    /**
     * 分页获取互动回应差数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getBadInteractPage")
    public Object getBadInteractPage(MonitorDetailQueryVO vo) {
        if(vo!=null&&vo.getMonitorId()==null){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }
        List<String> columnType = new ArrayList<String>();
        columnType.add(columnType_ZWZX.toString());
        vo.setColumnType(columnType);
        vo.setUnreplyCount(0l);//未回复数大于0
        return getObject(monitorInteractDetailService.getPage(vo));
    }


    /**
     * 分页获取首页栏目更新情况数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getIndexColumnPage")
    public Object getIndexColumnPage(MonitorDetailQueryVO vo) {
        if(vo!=null&&vo.getMonitorId()==null){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }
        List<String> columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
        vo.setColumnType(columnType);
        return getObject(monitorColumnDetailService.getPage(vo));
    }

    /**
     * 分页获取基本信息栏目更新情况详情数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getColumnBaseInfoPage")
    public Object getColumnBaseInfoPage(MonitorDetailQueryVO vo) {
        if(vo!=null&&vo.getMonitorId()==null){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }
        List<String> columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString());
        vo.setColumnType(columnType);
        vo.setUpdateCount(0l);
        return getObject(monitorColumnDetailService.getPage(vo));
    }

    /**
     * 分页获取互动回应情况数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getInteractInfoPage")
    public Object getInteractInfoPage(MonitorDetailQueryVO vo) {
        if(vo!=null&&vo.getMonitorId()==null){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }
        List<String> columnType = new ArrayList<String>();
        columnType.add(columnType_ZWZX.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_HDFT.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_DCZJ.toString());
        vo.setColumnType(columnType);
        return getObject(monitorInteractDetailService.getPage(vo));
    }


    /**
     * 查看未回复留言详情
     * @param contentIds
     * @param map
     * @return
     */
    @RequestMapping(value = "viewUnReplyDetail")
    public Object viewUnReplyDetail(String contentIds,ModelMap map) {
        map.put("contentIds",contentIds);
        return FILE_BASE + "view_unreply_detail";
    }

    /**
     * 分页获取超过三个月未回复留言
     * @param pageIndex
     * @param pageSize
     * @param contentIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getUnReplyPage")
    public Object getUnReplyPage(Long pageIndex,Integer pageSize,String contentIds) {
        if(pageIndex==null){
            pageIndex = 0l;
        }
        if(pageSize==null){
            pageSize = 10;
        }
        return getObject(monitorInteractDetailService.getUnReplyPage(pageIndex,pageSize,contentIds));
    }

    /**
     * 分页获取严重错误数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSeriousErrorPage")
    public Object getSeriousErrorPage(SeriousErrorQueryVO vo) {
        return getObject(monitorSeriousErrorResultService.getSeriousErrorPage(vo));
    }

    /**
     * 分页获取错误链接数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHrefUseablePage")
    public Object getHrefUseablePage(HrefUseableQueryVO vo) {
        return getObject(monitorHrefUseableResultService.getHrefUseablePage(vo));
    }

    /**
     * 分页获取错误链接数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteStatisPage")
    public Object getSiteStatisPage(SiteStatisQueryVO vo) {
        Pagination page = monitorSiteStatisService.getSiteStatisPage(vo);
        if(null != page.getData()) {
            List<MonitorSiteStatisEO> data = (List<MonitorSiteStatisEO>)page.getData();
            for(MonitorSiteStatisEO statis : data) {
                String siteUse = null != statis.getSiteUse()?statis.getSiteUse() : "0";
                String infoUpdate = null != statis.getInfoUpdate()?statis.getInfoUpdate():"0";
                String replyScope = null != statis.getReplyScope()?statis.getReplyScope():"0";
                String service = null != statis.getService()?statis.getInfoUpdate():"0";
                BigDecimal score = new BigDecimal(siteUse)
                        .add(new BigDecimal(infoUpdate))
                        .add(new BigDecimal(replyScope))
                        .add(new BigDecimal(service)).add(new BigDecimal(100));
                statis.setScore(score.doubleValue());
            }
        }
        return getObject(page);
    }

    /**
     * 启动所有任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "startTask")
    public Object startTask() {
        final Long siteId = LoginPersonUtil.getSiteId();
        SiteMgrEO mgr = getEntity(SiteMgrEO.class,siteId);
        MonitorSiteRegisterVO siteRegisterVO = monitorSiteRegisterService.getSiteRegisterInfo(siteId);
        if(null == siteRegisterVO || null == siteRegisterVO.getRegisteredCode() || "".equals(siteRegisterVO.getRegisteredCode())) {
            mgr = siteConfigService.getById(siteId);
            if(null == siteRegisterVO || null == siteRegisterVO.getRegisteredCode() || "".equals(siteRegisterVO.getRegisteredCode())) {
                return ajaxErr("当前站点未注册,请先注册");
            }
        }

        if(null != siteRegisterVO && siteRegisterVO.getIsRegistered() == 0) {
            return ajaxErr("当前站点已注册,但未开通");
        }
        //启动动态监测定时任务
        MonitorIndexManageEO index = monitorIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteDeny.toString(),siteId);
        index.setStartDate(new Date());
        monitorIndexManageService.updateEntity(index);
        MonitorTaskManageEO task = null;
        if(null != index.getTaskId()) {
            task = monitorTaskManageService.getTask(index.getTaskId(),1);
        }
        //获取正在运行的任务
        if(null != task) {
            return ajaxErr("任务正在运行，请先结束任务");
        }
        task = monitorIndexManageService.startTask();
        monitorHrefUseableResultService.runMontorDynamic(task.getId(),siteId,task.getReportId(),siteRegisterVO.getRegisteredCode(),mgr.getUri());
        final Long taskId = task.getId();
        final Long reportId = task.getReportId();

        try {
            //将任务都设置成正在检测状态
            monitorTaskManageService.updateStatus(taskId,"siteDenyStatus",1);
            monitorTaskManageService.updateStatus(taskId,"siteUseStatus",1);
            monitorTaskManageService.updateStatus(taskId,"siteUpdateStatus",1);
            monitorTaskManageService.updateStatus(taskId,"columnUpdateStatus",1);
            monitorTaskManageService.updateStatus(taskId,"infoUpdateStatus",1);
            monitorTaskManageService.updateStatus(taskId,"replyStatus",1);
            monitorTaskManageService.updateStatus(taskId,"replyScopeStatus",1);
            monitorTaskManageService.updateStatus(taskId,"errorStatus",1);

        }catch (Exception e){
            e.printStackTrace();
        }

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);

                try {

                    //站点不可访问检测
                    monitorSiteVisitResultService.runMonitor(siteId,taskId,reportId);
                    monitorTaskManageService.updateStatus(taskId,"siteDenyStatus",2);
                } catch (Exception e) {
                    //任务启动失败
                    monitorTaskManageService.updateStatus(taskId,"siteDenyStatus",3);
                    e.printStackTrace();
                }



                try {

                    //错链检测
                    monitorHrefUseableResultService.runMonitor(siteId,taskId,reportId);
                } catch (Exception e) {
                    //任务启动失败
                    monitorTaskManageService.updateStatus(taskId,"siteUseStatus",3);
                    e.printStackTrace();
                }

                try {

                    //栏目情况监测
                    monitorColumnDetailService.monitorColumn(siteId,taskId,reportId);
                } catch (Exception e) {
                    //任务启动失败
                    monitorTaskManageService.updateStatus(taskId,"siteUpdateStatus",3);
                    monitorTaskManageService.updateStatus(taskId,"columnUpdateStatus",3);
                    monitorTaskManageService.updateStatus(taskId,"infoUpdateStatus",3);
                    e.printStackTrace();
                }

                try {

                    //互动回应情况监测
                    monitorInteractDetailService.monitorInteract(siteId,taskId,reportId);
                } catch (Exception e) {
                    //任务启动失败
                    monitorTaskManageService.updateStatus(taskId,"replyStatus",3);
                    monitorTaskManageService.updateStatus(taskId,"replyScopeStatus",3);
                    e.printStackTrace();
                }


                try {
                    //严重错误检测
                    monitorSeriousErrorResultService.runMonitor(siteId,taskId,reportId);
                } catch (Exception e) {
                    //任务启动失败
                    monitorTaskManageService.updateStatus(taskId,"errorStatus",3);
                    e.printStackTrace();
                }
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ajaxOk();
    }

    /**
     * 获取单项否决项结果
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getVetoTotalStatis")
    public Object getVetoTotalStatis(Long taskId) {
        MonitorTaskManageEO latestTask = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if(null == taskId) {
            latestTask = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            taskId = latestTask.getId();
        } else {
            latestTask = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,taskId);
        }
        if(null == latestTask) {
            return ajaxErr("当前任务不存在");
        }
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("monitorDate",latestTask.getStartDate());
        //站点访问
        map.put("siteVisit",monitorSiteVisitResultService.loadSiteVisitStatis(taskId,siteId));
        //网站不更新
        map.put("siteUpdate",monitorColumnDetailService.loadIndexNotUpdateStatis(taskId,siteId));
        //栏目不更新
        map.put("columnUpdate",monitorColumnDetailService.loadColumnNotUpdateStatis(taskId,siteId));
        //严重错误
        map.put("seriousError",monitorSeriousErrorResultService.loadSeriousErrorStatis(taskId,siteId));
        //互动回应差
        map.put("reply",monitorInteractDetailService.loadBadInteractStatis(taskId,siteId));
        return getObject(map);
    }

    /**
     * 获取综合评分项统计
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getComprehScoreStatis")
    public Object getComprehScoreStatis(Long taskId) {
        MonitorTaskManageEO latestTask = null;
        Long siteId = LoginPersonUtil.getSiteId();
        if(null == taskId) {
            latestTask = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            taskId = latestTask.getId();
        } else {
            latestTask = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,taskId);
        }
        if(null == latestTask) {
            return ajaxErr("当前任务不存在");
        }
        Map<String,Object> map = new HashMap<String, Object>();
        //错误链接检测
        map.put("hrefUseable",monitorHrefUseableResultService.loadHrefUseableStatis(taskId,siteId));
        //首页栏目
        map.put("indexColumn",monitorColumnDetailService.loadIndexColumnStatis(taskId,siteId));
        //基本信息
        map.put("baseInfo",monitorColumnDetailService.loadColumnBaseInfoStatis(taskId,siteId));
        //互动回应情况
        map.put("replyScope",monitorInteractDetailService.loadInteractInfoStatis(taskId,siteId));
        return getObject(map);
    }

    /**
     * 停止所有任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "stopTask")
    public Object stopTask() {
        monitorIndexManageService.stopTask();
        return ajaxOk();
    }

    /**
     * 启动自定义任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "startCustomTask")
    public Object startCustomTask(String typeCode) {
        Long siteId = LoginPersonUtil.getSiteId();
//        SiteMgrEO mgr = getEntity(SiteMgrEO.class,siteId);
        MonitorSiteRegisterVO siteRegisterVO = monitorSiteRegisterService.getSiteRegisterInfo(siteId);
        if(null == siteRegisterVO || null == siteRegisterVO.getRegisteredCode() || "".equals(siteRegisterVO.getRegisteredCode())) {
//            mgr = siteConfigService.getById(siteId);
            if(null == siteRegisterVO || null == siteRegisterVO.getRegisteredCode() || "".equals(siteRegisterVO.getRegisteredCode())) {
                return ajaxErr("当前站点未注册,请先注册");
            }
        }

        if(null != siteRegisterVO && siteRegisterVO.getIsRegistered() == 0) {
            return ajaxErr("当前站点已注册,但未开通");
        }

        MonitorCustomIndexManageEO index = monitorCustomIndexManageService.getIndex(typeCode,siteId);
        //获取正在运行的任务
        if(null != index && index.getTaskStatus() == 1) {
            return ajaxErr("任务正在运行，请先结束任务");
        }
        monitorCustomTaskManageService.startTask(typeCode);
        return ajaxOk();
    }

    /**
     * 停止自定义任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "stopCustomTask")
    public Object stopCustomTask(Long id) {
        monitorCustomTaskManageService.stopTask(id);
        return ajaxOk();
    }

    /**
     * 获取统计结果分值
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getTotalScore")
    public Object getTotalScore(Long taskId) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("taskId",taskId);
        MonitorSiteStatisEO statis = monitorSiteStatisService.getEntity(MonitorSiteStatisEO.class,map);
        if(null != statis) {
            String siteUse = null != statis.getSiteUse()?statis.getSiteUse() : "0";
            String infoUpdate = null != statis.getInfoUpdate()?statis.getInfoUpdate():"0";
            String replyScope = null != statis.getReplyScope()?statis.getReplyScope():"0";
            String service = null != statis.getService()?statis.getInfoUpdate():"0";
            BigDecimal score = new BigDecimal(siteUse)
                    .add(new BigDecimal(infoUpdate))
                    .add(new BigDecimal(replyScope))
                    .add(new BigDecimal(service)).add(new BigDecimal(100));
            statis.setScore(score.doubleValue());
            MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,taskId);
            if(null != task) {
                if(null == statis.getSiteDeny() || null == statis.getSiteUpdate()|| null == statis.getColumnUpdate()
                        || null == statis.getError() || null == statis.getReply()) {
                    return ajaxErr("任务正在执行");
                }
                if(statis.getSiteDeny() != 1 || statis.getSiteUpdate() != 1 || statis.getColumnUpdate() != 1
                        || statis.getError() != 1 || statis.getReply() != 1) {
                    task.setResult("不合格");
                } else {
                    task.setResult(String.valueOf(score.doubleValue()));
                }
                monitorTaskManageService.updateEntity(task);
            }
        }
        return ajaxOk(statis);
    }

    /**
     * 获取站点访问统计信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSeriousErrorStatis")
    public Object getSeriousErrorStatis(Long taskId) {
        Long count = monitorSeriousErrorResultService.getCount(taskId);
        monitorSiteStatisService.updateError(LoginPersonUtil.getSiteId(),taskId,count > 0 ?0:1);
        return getObject(count);
    }

    /**
     * 获取错误链接统计信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHrefUseableStatis")
    public Object getHrefUseableStatis(Long taskId) {
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteUse.toString(), LoginPersonUtil.getSiteId());
        HrefUseableStatisVO vo = monitorHrefUseableResultService.getHrefUseaStatis(taskId);
        if(null != vo) {
            double n = 5;
            double i = 1;
            double o = 0.1;
            if(null != bc) {
                if(null != bc.get("notOpenNum")) {
                    n = Double.valueOf(bc.get("notOpenNum").toString());
                }
                if(null != bc.get("indexErrorNum")) {
                    i = Double.valueOf(bc.get("indexErrorNum").toString());
                }
                if(null != bc.get("otherErrorNum")) {
                    o = Double.valueOf(bc.get("otherErrorNum").toString());
                }
            }
            BigDecimal indexScore = new BigDecimal(null != vo.getIndexCount()?vo.getIndexCount()*i:0);
            BigDecimal otherScore = new BigDecimal(null != vo.getOtherCount()?vo.getOtherCount()*o:0);
            BigDecimal score = indexScore.add(otherScore);
            SiteVisitStatisVO statis = monitorSiteVisitResultService.getSiteVisitStatis(taskId);
            if(null != statis && statis.getTotal() > 0) {
                // 创建一个数值格式化对象
                NumberFormat numberFormat = NumberFormat.getInstance();
                // 设置精确到小数点后2位
                numberFormat.setMaximumFractionDigits(2);
                String successrate = numberFormat.format((float) statis.getSuccess() / (float) statis.getTotal() * 100);
                statis.setSuccessRate(Double.valueOf(successrate));
                String failrate = numberFormat.format((float) statis.getFail() / (float) statis.getTotal() * 100);
                score = score.add(new BigDecimal(Double.valueOf(failrate)*n));
            }
            vo.setTotalScore(score.doubleValue());
            monitorSiteStatisService.updateSiteUse(LoginPersonUtil.getSiteId(),taskId,String.valueOf(-score.doubleValue()));
        }
        return getObject(vo);
    }

    /**
     * 获取今日首页错链结果
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHrefUseableDynamicPage")
    public Object getHrefUseableDynamicPage(HrefUseableQueryVO vo) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateUtil.getStrAnyday(1));
            vo.setDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ajaxErr("时间格式化错误");
        }
        return monitorHrefUseableDynamicService.getPage(vo);
    }

    /**
     * 检测问题统计分析
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteVisitStatis")
    public Object getSiteVisitStatis(Long taskId) {
        Map<String,Object> map = new HashMap<String, Object>();
        if(null == taskId) {
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            taskId = task.getId();
        }
        if(null != taskId) {
            SiteVisitStatisVO siteVisit = monitorSiteVisitResultService.getSiteVisitStatis(taskId);
            if(null != siteVisit) {
                map.put("siteVisitTotal",siteVisit.getTotal());
                if(siteVisit.getTotal() == 0) {
                    map.put("successVisitRate",0);
                } else {
                    map.put("successVisitRate",getDoubleValue(siteVisit.getSuccess()*100/siteVisit.getTotal(),2));
                }
            }

            HrefUseableStatisVO hrefUseable = monitorHrefUseableResultService.getHrefUseaStatis(taskId);
            if(null != hrefUseable) {
                map.put("hrefTotal",hrefUseable.getTotal());
                map.put("indexHref",hrefUseable.getIndexCount());
                map.put("otherHref",hrefUseable.getOtherCount());
            }
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateUtil.getStrAnyday(1));
                map.put("dynamicHrefCount",monitorHrefUseableDynamicService.getHrefUseableDynamicCout(taskId,date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<SiteVisitDateStatisVO> list = new ArrayList<SiteVisitDateStatisVO>();
            list.add(monitorSiteVisitResultService.getSiteVisitStatis(taskId,DateFormatUtils.format(DateUtil.getAnyday(-6),"yyyy/MM/dd")));
            list.add(monitorSiteVisitResultService.getSiteVisitStatis(taskId,DateFormatUtils.format(DateUtil.getAnyday(-5),"yyyy/MM/dd")));
            list.add(monitorSiteVisitResultService.getSiteVisitStatis(taskId,DateFormatUtils.format(DateUtil.getAnyday(-4),"yyyy/MM/dd")));
            list.add(monitorSiteVisitResultService.getSiteVisitStatis(taskId,DateFormatUtils.format(DateUtil.getAnyday(-3),"yyyy/MM/dd")));
            list.add(monitorSiteVisitResultService.getSiteVisitStatis(taskId,DateFormatUtils.format(DateUtil.getAnyday(-2),"yyyy/MM/dd")));
            list.add(monitorSiteVisitResultService.getSiteVisitStatis(taskId,DateFormatUtils.format(DateUtil.getAnyday(-1),"yyyy/MM/dd")));
            list.add(monitorSiteVisitResultService.getSiteVisitStatis(taskId,DateFormatUtils.format(DateUtil.getAnyday(0),"yyyy/MM/dd")));
            map.put("siteVisitDateStatis",list);
        }
        return getObject(map);
    }


    /**
     * 获取监测栏目总数
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getColumnCounts")
    public Object getColumnCounts(Long taskId) {
        if(taskId==null||taskId==0){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            taskId = task.getId();
        }
        if(taskId==null||taskId==0){
            return ajaxErr("监测报告不存在");
        }
        Long columnCounts = monitorColumnDetailService.getCount(taskId,
                new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);
//        Long publicCounts = monitorColumnDetailService.getCount(taskId,
//                null, MonitorColumnDetailEO.INFO_TYPE.publics.toString(),null);
        return getObject(columnCounts);
    }


    /**
     * 获取不达标栏目总数
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getBdbColumnCounts")
    public Object getBdbColumnCounts(Long taskId) {
        if(taskId==null||taskId==0){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            taskId = task.getId();
        }
        if(taskId==null||taskId==0){
            return ajaxErr("监测报告不存在");
        }
        return getObject(monitorColumnDetailService.getCount(taskId,
                new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,0l));
    }

    /**
     * 获取栏目更新情况
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getColumnDetaiList")
    public Object getColumnDetaiList(Long taskId,Integer isStatard) {
        if(taskId==null||taskId==0){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            taskId = task.getId();
        }
        if(taskId==null||taskId==0){
            return ajaxErr("监测报告不存在");
        }
        Long updateCount = null;
        if(isStatard!=null&&isStatard==1){//查询未达标栏目
            updateCount = 0l;
        }

        List<MonitorColumnDetailEO> columnDetailEOList = monitorColumnDetailService.getList(taskId,
                new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,updateCount);

        for(MonitorColumnDetailEO eo:columnDetailEOList){
            if(MonitorColumnDetailEO.INFO_TYPE.publics.toString().equals(eo.getInfoType())){//信息公开
                try {
                    String[] ids =  eo.getColumnId().split("_");
                    OrganEO organ = CacheHandler.getEntity(OrganEO.class,Long.parseLong(ids[0]));
                    eo.setColumnName(organ.getName()+" > "+ PublicCatalogUtil.getCatalogPath(Long.parseLong(ids[0]), Long.parseLong(ids[1])));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{//新闻
                eo.setColumnName(ColumnUtil.getColumnName(Long.parseLong(eo.getColumnId()), LoginPersonUtil.getSiteId()));
            }
        }


        return getObject(columnDetailEOList);
    }


    /**
     * 跳转栏目更新分页情况
     * @param monitorId
     * @param map
     * @return
     */
    @RequestMapping(value = "viewColumnDetai")
    public Object viewColumnDetai(Long monitorId,ModelMap map) {
        map.put("monitorId",monitorId);
        return FILE_BASE + "view_column_detai";
    }

    /**
     * 获取栏目更新分页情况
     * @param vo
     * @param isStatard
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getColumnDetaiPage")
    public Object getColumnDetaiPage(MonitorDetailQueryVO vo,Integer isStatard) {
        if(vo==null||vo.getMonitorId()==null||vo.getMonitorId()==0){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }
        if(vo==null||vo.getMonitorId()==null){
            return ajaxErr("监测报告不存在");
        }
        if(isStatard!=null&&isStatard==1){//查询未达标栏目
            vo.setUpdateCount(0l);
        }
        vo.setColumnType(Arrays.asList(new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_index.toString()}));

        return getObject(monitorColumnDetailService.getPage(vo));
    }

    /**
     * 导出栏目更新情况
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("downColumnDetaiPage")
    public void downColumnDetaiPage(HttpServletRequest request, HttpServletResponse response,MonitorDetailQueryVO vo,Integer isStatard) throws Exception{
        if(vo==null||vo.getMonitorId()==null||vo.getMonitorId()==0){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }
        if(vo==null||vo.getMonitorId()==null){
            return ;
        }
        if(isStatard!=null&&isStatard==1){//查询未达标栏目
            vo.setUpdateCount(0l);
        }

        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        List<String> columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_update.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
        vo.setColumnType(columnType);
        List<MonitorColumnDetailEO> mcList = monitorColumnDetailService.getList(vo);
        //设置columnName
        setColumnName(mcList);

        String[] headers = new String[]{"栏目类型","栏目名称","最后更新日期","应更新周期","未更新天数","是否达标"};
        List<Object[]> values = new ArrayList<Object[]>(mcList.size());
        for(MonitorColumnDetailEO mc:mcList){
            Object[] objects = new Object[6];
            objects[0] = columnType(mc.getColumnType());
            objects[1] = null == mc.getColumnName() ? "--" : mc.getColumnName();
            objects[2] = mc.getLastPublishDate() == null ? "--":dateFormater.format(mc.getLastPublishDate());
            objects[3] = updateCycleStr(mc.getUpdateCycleStr(),mc.getUpdateCycle());
            objects[4] = mc.getUnPublishDays();
            Long updateCount = mc.getUpdateCount();
            objects[5] = (updateCount!=null && updateCount>0) ? "是":"否";
            values.add(objects);
        }
        PoiExcelUtil.exportExcel("栏目更新情况", "栏目更新情况", "xls", headers, values, response);
    }

    private void setColumnName(List<MonitorColumnDetailEO> mcList) {
        for(MonitorColumnDetailEO eo:mcList){
            if(MonitorColumnDetailEO.INFO_TYPE.publics.toString().equals(eo.getInfoType())){//信息公开
                try {
                    String[] ids =  eo.getColumnId().split("_");
                    OrganEO organ = CacheHandler.getEntity(OrganEO.class,Long.parseLong(ids[0]));
                    eo.setColumnName(organ.getName()+" > "+ PublicCatalogUtil.getCatalogPath(Long.parseLong(ids[0]), Long.parseLong(ids[1])));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{//新闻
                eo.setColumnName(ColumnUtil.getColumnName(Long.parseLong(eo.getColumnId()), LoginPersonUtil.getSiteId()));
            }
        }
    }

    /**
     * 导出单项否决项
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("downDenyPage")
    public void downDenyPage(HttpServletRequest request, HttpServletResponse response,MonitorDetailQueryVO vo) throws Exception{

        //创建多sheet数据对象
        String[] titles = new String[5];
        List<String[]> multiHeaders = new ArrayList<String[]>();
        List<List<Object[]>> multiValues = new ArrayList<List<Object[]>>();
        String[] headers = null;
        List<Object[]> values = null;

        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(vo!=null&&vo.getMonitorId()==null){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }

        //查询站点无法访问数据======================start===========================
        SiteVisitQueryVO svqVo = new SiteVisitQueryVO();
        svqVo.setTaskId(vo.getMonitorId());
        List<MonitorSiteVisitResultEO> msvList = monitorSiteVisitResultService.getSiteVisitList(svqVo);
        headers = new String[]{"地址","检测时间","返回编码","是否可访问","原因"};
        values = new ArrayList<Object[]>(msvList.size());
        for(MonitorSiteVisitResultEO msv:msvList){
            Object[] objects = new Object[5];
            objects[0] = null == msv.getVisitUrl() ? "--" : msv.getVisitUrl();
            objects[1] = null == msv.getMonitorDate() ? "--" : dateFormater.format(msv.getMonitorDate());
            objects[2] = null == msv.getRespCode() ? "--" : msv.getRespCode();
            objects[3] = (null != msv.getRespCode() && msv.getRespCode() == 200) ? "是":"否";
            objects[4] = null == msv.getReason() ? "--" : msv.getReason();
            values.add(objects);
        }

        //加入整体对象中
        titles[0] = "站点无法访问详情";
        multiHeaders.add(headers);
        multiValues.add(values);
        //查询站点无法访问数据======================end===========================

        //清空数据
        headers = null;values = null;

        //查询网站不更新数据======================start===========================
        List<String> columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
        vo.setColumnType(columnType);
        List<MonitorColumnDetailEO> mcList = monitorColumnDetailService.getList(vo);
        //设置columnName
        setColumnName(mcList);
        headers = new String[]{"栏目类型","栏目名称","更新数量"};
        values = new ArrayList<Object[]>(mcList.size());
        for(MonitorColumnDetailEO mc:mcList){
            Object[] objects = new Object[3];
            objects[0] = columnType(mc.getColumnType());
            objects[1] = null == mc.getColumnName() ? "--" : mc.getColumnName();
            objects[2] = mc.getUpdateCount()!=null ? mc.getUpdateCount():"--";
            values.add(objects);
        }

        //加入整体对象中
        titles[1] = "网站不更新详情";
        multiHeaders.add(headers);
        multiValues.add(values);

        //查询网站不更新数据======================end===========================

        headers = null;values = null;

        //查询栏目不更新数据======================start===========================
        vo.setUpdateCount(0l);
        columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_update.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString());
        vo.setColumnType(columnType);
        List<MonitorColumnDetailEO> mcList1 = monitorColumnDetailService.getList(vo);
        //设置columnName
        setColumnName(mcList1);

        headers = new String[]{"栏目类型","栏目名称","更新数量"};
        values = new ArrayList<Object[]>(mcList1.size());
        for(MonitorColumnDetailEO mc:mcList1){
            Object[] objects = new Object[3];
            objects[0] = columnType(mc.getColumnType());
            objects[1] = null == mc.getColumnName() ? "--" : mc.getColumnName();
            objects[2] = mc.getUpdateCount()!=null ? mc.getUpdateCount():"--";
            values.add(objects);
        }

        //加入整体对象中
        titles[2] = "栏目不更新详情";
        multiHeaders.add(headers);
        multiValues.add(values);
        //查询栏目不更新数据======================end===========================

        headers = null;values = null;

        //查询严重错误数据======================start===========================
        SeriousErrorQueryVO seqVo = new SeriousErrorQueryVO();
        seqVo.setTaskId(vo.getMonitorId());
        List<MonitorSeriousErrorResultEO> mseList = monitorSeriousErrorResultService.getSeriousErrorList(seqVo);
        headers = new String[]{"标题","检测时间","疑似错误","类型","来源","检测类型"};
        values = new ArrayList<Object[]>(mseList.size());
        for(MonitorSeriousErrorResultEO mse:mseList){
            Object[] objects = new Object[6];
            objects[0] = link(mse);
            objects[1] = null == mse.getMonitorDate() ? "--" : dateFormater.format(mse.getMonitorDate());
            objects[2] = null == mse.getWord() ? "--" : mse.getWord();
            objects[3] = null == mse.getTypeCode() ? "--" : mse.getTypeCode();
            objects[4] = null == mse.getFromCode() ? "--" : mse.getFromCode();
            objects[5] = (null != mse.getCheckType() && mse.getCheckType() == "SENSITIVE") ? "铭感词":"易错词";
            values.add(objects);
        }

        //加入整体对象中
        titles[3] = "严重错误详情";
        multiHeaders.add(headers);
        multiValues.add(values);
        //查询严重错误数据======================end===========================

        headers = null;values = null;

        //查询互动回应差数据======================start===========================
        vo.setUnreplyCount(0l);//未回复数大于0
        columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString());
        vo.setColumnType(columnType);
        List<MonitorInteractDetailEO> mcList2 = monitorInteractDetailService.getList(vo);

        headers = new String[]{"栏目类型","栏目名称","更新数量","未回复数量"};
        values = new ArrayList<Object[]>(mcList2.size());
        for(MonitorInteractDetailEO mc:mcList2){
            Object[] objects = new Object[4];
            objects[0] = columnType(mc.getColumnType());
            objects[1] = null == mc.getColumnName() ? "--" : mc.getColumnName();
            objects[2] = mc.getUpdateCount()!=null ? mc.getUpdateCount():"--";
            objects[3] = null == mc.getUnreplyCount() ? "--" : mc.getUnreplyCount();
            values.add(objects);
        }

        //加入整体对象中
        titles[4] = "互动回应差详情";
        multiHeaders.add(headers);
        multiValues.add(values);
        //查询互动回应差数据======================end===========================

        //最后调用到处方法
        PoiExcelUtil.exportMultiSheetExcel(titles, "单项否决项", "xls", multiHeaders, multiValues, response);
    }

    /**
     * 导出综合评分
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("downScorePage")
    public void downScorePage(HttpServletRequest request, HttpServletResponse response,MonitorDetailQueryVO vo) throws Exception{

        //创建多sheet数据对象
        String[] titles = new String[3];
        List<String[]> multiHeaders = new ArrayList<String[]>();
        List<List<Object[]>> multiValues = new ArrayList<List<Object[]>>();
        String[] headers = null;
        List<Object[]> values = null;

        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(vo!=null&&vo.getMonitorId()==null){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(LoginPersonUtil.getSiteId(),null);
            vo.setMonitorId(task.getId());
        }

        //查询错误链接详情数据======================start===========================
        HrefUseableQueryVO huqVo = new HrefUseableQueryVO();
        huqVo.setTaskId(vo.getMonitorId());
        List<MonitorHrefUseableResultEO> huqList = monitorHrefUseableResultService.getHrefUseableList(huqVo);
        headers = new String[]{"链接","父链接","检测时间","返回编码","结果","是否首页","定位"};
        values = new ArrayList<Object[]>(huqList.size());
        for(MonitorHrefUseableResultEO huq:huqList){
            Object[] objects = new Object[7];
            objects[0] = null == huq.getVisitUrl() ? "--" : huq.getVisitUrl();
            objects[1] = null == huq.getParentUrl() ? "--" : huq.getParentUrl();
            objects[2] = null == huq.getMonitorDate() ? "--" : dateFormater.format(huq.getMonitorDate());
            objects[3] = null == huq.getRespCode() ? "--" : huq.getRespCode();
            objects[4] = errDesc(huq);
            objects[5] = (null != huq.getIsIndex() && huq.getIsIndex() != 0) ? "是":"否";
            objects[6] = "--";
            values.add(objects);
        }

        //加入整体对象中
        titles[0] = "错误链接详情";
        multiHeaders.add(headers);
        multiValues.add(values);
        //查询错误链接详情数据======================end===========================

        //清空数据
        headers = null;values = null;

        //查询信息更新详情======================start===========================
        List<String> columnType = new ArrayList<String>();
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString());
        vo.setColumnType(columnType);
        vo.setUpdateCount(0l);
        List<MonitorColumnDetailEO> mcList = monitorColumnDetailService.getList(vo);
        //设置columnName
        setColumnName(mcList);
        headers = new String[]{"栏目类型","栏目名称","更新数量"};
        values = new ArrayList<Object[]>(mcList.size());
        for(MonitorColumnDetailEO mc:mcList){
            Object[] objects = new Object[3];
            objects[0] = columnType(mc.getColumnType());
            objects[1] = null == mc.getColumnName() ? "--" : mc.getColumnName();
            objects[2] = mc.getUpdateCount()!=null ? mc.getUpdateCount():"--";
            values.add(objects);
        }

        //加入整体对象中
        titles[1] = "信息更新详情";
        multiHeaders.add(headers);
        multiValues.add(values);

        //查询信息更新详情======================end===========================

        headers = null;values = null;

        //查询互动回应情况详情======================start===========================
        columnType = new ArrayList<String>();
        columnType.add(columnType_ZWZX.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_HDFT.toString());
        columnType.add(MonitoredColumnConfigEO.TypeCode.columnType_DCZJ.toString());
        vo.setColumnType(columnType);
        vo.setUpdateCount(null);
        List<MonitorInteractDetailEO> mcList1 = monitorInteractDetailService.getList(vo);

        headers = new String[]{"栏目类型","栏目名称","更新数量","未回复数量"};
        values = new ArrayList<Object[]>(mcList1.size());
        for(MonitorInteractDetailEO mc:mcList1){
            Object[] objects = new Object[4];
            objects[0] = columnType(mc.getColumnType());
            objects[1] = null == mc.getColumnName() ? "--" : mc.getColumnName();
            objects[2] = mc.getUpdateCount()!=null ? mc.getUpdateCount():"--";
            objects[3] = null == mc.getUnreplyCount() ? "--" : mc.getUnreplyCount();
            values.add(objects);
        }

        //加入整体对象中
        titles[2] = "互动回应情况详情";
        multiHeaders.add(headers);
        multiValues.add(values);
        //查询互动回应情况详情======================end===========================

        //最后调用到处方法
        PoiExcelUtil.exportMultiSheetExcel(titles, "综合评分", "xls", multiHeaders, multiValues, response);
    }

    private String unreplyCount(MonitorInteractDetailEO mc){
        String columnType = mc.getColumnType();
        Long unreplyCount = mc.getUnreplyCount();
        if(MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString().equals(columnType)){
            return unreplyCount.toString();
        }else{
            return "--";
        }
    }

    //错误结果
    private String errDesc(MonitorHrefUseableResultEO huq) {
        int errCode = huq.getRespCode();
        String desc = "错误链接";
        if (errCode == 400) {
            desc = "错误请求";
        } else if (errCode == 403) {
            desc = "服务器拒绝请求";
        } else if (errCode == 404) {
            desc = "服务器找不到请求的网页";
        } else if (errCode == 405) {
            desc = "禁用请求中指定的方法";
        } else if (errCode == 406) {
            desc = "无法使用请求的内容特性响应请求的网页";
        } else if (errCode == 407) {
            desc = "指定请求者应当授权使用代理";
        } else if (errCode == 408) {
            desc = "服务器等候请求时发生超时";
        } else if (errCode == 409) {
            desc = "服务器在完成请求时发生冲突";
        } else if (errCode == 410) {
            desc = "资源已删除";
        } else if (errCode == 500) {
            desc = "服务器遇到错误，无法完成请求";
        } else if (errCode == 501) {
            desc = "服务器不具备完成请求的功能";
        } else if (errCode == 502) {
            desc = "错误网关";
        } else if (errCode == 503) {
            desc = "服务不可用";
        } else if (errCode == 504) {
            desc = "网关超时";
        } else if (errCode == 505) {
            desc = "HTTP 版本不受支持";
        }
        return desc;
    }

    //分析链接
    private String link(MonitorSeriousErrorResultEO mse) {
        StringBuffer domain = new StringBuffer();
        if (AppUtil.isEmpty(mse.getDomain())) {
            return "--";
        }
        domain.append(mse.getDomain());
        if(!mse.getDomain().endsWith("/")) {
            domain.append("/");
        }
        domain.append(mse.getColumnId()).append("/").append(mse.getContentId()).append(".html");
        return domain.toString();
    }

    //栏目类型
    private String columnType(String columnType) {
        String columnTypeName;
        if ("columnType_index".equals(columnType)) {
            columnTypeName = "首页栏目";
        }else if ("columnType_update".equals(columnType)) {
            columnTypeName = "应更新栏目";
        }else if ("columnType_DTYW".equals(columnType)) {
            columnTypeName = "动态、要闻类栏目";
        }else if ("columnType_TZZC".equals(columnType)) {
            columnTypeName = "通知公告、政策文件类栏目";
        }else if ("columnType_RSGH".equals(columnType)) {
            columnTypeName = "人事、规划计划类栏目";
        }else if ("columnType_BLANK".equals(columnType)) {
            columnTypeName = "空白栏目";
        }else if ("'columnType_ZWZX'".equals(columnType)) {
            columnTypeName = "'政务咨询类栏目'";
        }else if ("'columnType_DCZJ'".equals(columnType)) {
            columnTypeName = "'调查征集类栏目'";
        }else if ("'columnType_HDFT'".equals(columnType)) {
            columnTypeName = "'互动访谈类栏目'";
        }else {
            columnTypeName="其他栏目";
        }
        return columnTypeName;
    }
    //应更新周期
    private String updateCycleStr(String updateCycleStr,Integer updateCycle) {
        if(!AppUtil.isEmpty(updateCycleStr)){
            return updateCycleStr+"("+updateCycle+"天)";
        }else{
            return updateCycle+"天";
        }
    }


    /**
     * 导出监测报告
     * @param request
     * @param response
     * @param taskId
     */
    @RequestMapping("/exportReport")
    public void exportReport(HttpServletRequest request, HttpServletResponse response, Long taskId) {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteMgrEO mgr = getEntity(SiteMgrEO.class,siteId);
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("siteName",mgr.getName());
        Map<String,Object> map = new HashMap<String, Object>();
        params.put("veto","合格");
        map.put("taskId",taskId);

        MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,taskId);
        Calendar cal = Calendar.getInstance();
        if(null != task.getStartDate()) {
            cal = Calendar.getInstance();
            cal.setTime(new Date());
        } else {
            cal.setTime(new Date());
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        params.put("monitorDate","（" + year + "年" + month + "月" + day + "日监测结果）");
        //设置站点名称
        MonitorSiteStatisEO statis = monitorSiteStatisService.getEntity(MonitorSiteStatisEO.class,map);
        if(null != statis) {
            String siteUse = null != statis.getSiteUse()?statis.getSiteUse() : "0";
            String infoUpdate = null != statis.getInfoUpdate()?statis.getInfoUpdate():"0";
            String replyScope = null != statis.getReplyScope()?statis.getReplyScope():"0";
            String service = null != statis.getService()?statis.getInfoUpdate():"0";
            BigDecimal score = new BigDecimal(siteUse)
                    .add(new BigDecimal(infoUpdate))
                    .add(new BigDecimal(replyScope))
                    .add(new BigDecimal(service)).add(new BigDecimal(100));
            params.put("monitorResult",score.doubleValue());
            if(statis.getSiteDeny() != 1 || statis.getSiteUpdate() != 1 || statis.getColumnUpdate() != 1
                    || statis.getError() != 1 || statis.getReply() != 1 || score.intValue() > 40) {
                params.put("monitorResult","不合格");
                params.put("veto","不合格");
            }
        }

        SiteVisitStatisVO statiVisit = monitorSiteVisitResultService.getSiteVisitStatis(taskId);
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteDeny.toString(), LoginPersonUtil.getSiteId());
        int rateLimit = Integer.valueOf(String.valueOf(bc.get("notOpenRate")));
        String failrate = null;
        if(statiVisit.getTotal() > 0) {
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);
            failrate = numberFormat.format((float) statiVisit.getFail() / (float) statiVisit.getTotal() * 100);
            BigDecimal a = new BigDecimal(rateLimit);
            BigDecimal b = new BigDecimal(failrate);
            if(b.compareTo(a) < 0) {
                params.put("siteVisitDesc","合格");
                params.put("siteVisitVeto","");
            } else {
                params.put("veto","不合格");
                params.put("siteVisitDesc","不合格,访问失败率为" + failrate + "%,超过了" + a + "%");
                params.put("siteVisitVeto","单项否决");
            }
        } else {
            params.put("siteVisitDesc","合格");
            params.put("siteVisitVeto","");
        }
        Long seriousCount = monitorSeriousErrorResultService.getCount(taskId);
        if(seriousCount > 0) {
            params.put("veto","不合格");
            params.put("seriousErrorDesc","有" + seriousCount + "个严重错误");
            params.put("seriousErrorVeto","单项否决");
        } else {
            params.put("seriousErrorDesc","合格");
            params.put("seriousErrorVeto","");
        }

        IndexNotUpdateStatisVO indexUpdate = monitorColumnDetailService.loadIndexNotUpdateStatis(taskId,siteId);
        if(indexUpdate.getIsOk() == 1) {
            params.put("siteUpdateDesc","合格");
            params.put("siteUpdateVeto","");
        } else {
            params.put("veto","不合格");
            params.put("siteUpdateDesc","不合格,首页更新量：" + indexUpdate.getCount());
            params.put("siteUpdateVeto","单项否决");
        }

        ColumnNotUpdateStatisVO columnUpdate = monitorColumnDetailService.loadColumnNotUpdateStatis(taskId,siteId);
        if(columnUpdate.getIsOk() == 1) {
            params.put("columnUpdateDesc","合格");
            params.put("columnUpdateVeto","");
        } else {
            params.put("veto","不合格");
            StringBuilder s = new StringBuilder();
            s.append(getWordWrap("【1】动态要闻类、通知政策类栏目，" + columnUpdate.getDtywCounts() + "个未按时更新"));
//            s.append(getWordWrap("【2】通知公告、政策文件类栏目，" + columnUpdate.getTzzcCounts() + "个未按时更新"));
            s.append(getWordWrap("【2】" + columnUpdate.getUpdateCounts() + "个应更新栏目未按时更新"));
            s.append(getWordWrap("【3】" + columnUpdate.getBlankCounts() + "个空白栏目"));
            params.put("columnUpdateDesc",s.toString());
            params.put("columnUpdateVeto","单项否决");
        }

        BadInteractStatisVO reply = monitorInteractDetailService.loadBadInteractStatis(taskId,siteId);
        if(reply.getIsOk() == 1) {
            params.put("replyDesc","合格");
            params.put("replyVeto","");
        } else {
            params.put("veto","不合格");
            StringBuilder s = new StringBuilder("不合格,");
            s.append(reply.getCount() + "件超过三个月未回复的信件");
            params.put("replyDesc",s.toString());
            params.put("replyVeto","单项否决");
        }
        BigDecimal score = new BigDecimal(0);
        HrefUseableStatisVO hrefUseable = monitorHrefUseableResultService.getHrefUseaStatis(taskId);
        if(null != hrefUseable) {
            Map<String,Object> vetoConfig =  monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.siteUse.toString(),siteId);
            double n = 5;
            double i = 1;
            double o = 0.1;
            if(null != vetoConfig) {
                if(null != vetoConfig.get("notOpenNum")) {
                    n = Double.valueOf(vetoConfig.get("notOpenNum").toString());
                }
                if(null != vetoConfig.get("indexErrorNum")) {
                    i = Double.valueOf(vetoConfig.get("indexErrorNum").toString());
                }
                if(null != vetoConfig.get("otherErrorNum")) {
                    o = Double.valueOf(vetoConfig.get("otherErrorNum").toString());
                }
            }
            BigDecimal indexScore = new BigDecimal(null != hrefUseable.getIndexCount()?hrefUseable.getIndexCount()*i:0);
            BigDecimal otherScore = new BigDecimal(null != hrefUseable.getOtherCount()?hrefUseable.getOtherCount()*o:0);

            StringBuilder indexUseDesc = new StringBuilder(getWordWrap("【1】首页有" + hrefUseable.getIndexCount() + "个链接不可访问"));
            if(null != failrate) {
                BigDecimal svRate = new BigDecimal(failrate);
                if(svRate.intValue() > 0) {
                    indexScore = indexScore.add(new BigDecimal(svRate.doubleValue() * n));
                    indexUseDesc.append("【2】站点不可访问率：" + svRate.doubleValue());
                }
            }
            params.put("indexUseDesc",indexUseDesc.toString());
            params.put("indexUseScore",indexScore.doubleValue() == 0?0:-indexScore.doubleValue());
            params.put("linkUseableDesc","其它页面有" + hrefUseable.getOtherCount() + "个链接不可访问");
            params.put("linkUseableScore",otherScore.doubleValue() == 0?0:-otherScore.doubleValue());
            score = score.subtract(indexScore);
            score = score.subtract(otherScore);
        }

        IndexColumnStatisVO indexColumnStatis = monitorColumnDetailService.loadIndexColumnStatis(taskId,siteId);
        params.put("indexColumnDesc","首页更新量："+indexColumnStatis.getCount());
        params.put("indexColumnScore",-indexColumnStatis.getScore());
        score = score.add(new BigDecimal(-indexColumnStatis.getScore()));

        ColumnBaseInfoStatisVO baseInfoStatis = monitorColumnDetailService.loadColumnBaseInfoStatis(taskId,siteId);
        StringBuilder sb = new StringBuilder();
        sb.append(getWordWrap("【1】动态、要闻类栏目，" + baseInfoStatis.getDtywCount() + "个未按时更新"));
        sb.append(getWordWrap("【2】通知公告、政策文件类栏目，" + baseInfoStatis.getTzzcCount() + "个未按时更新"));
        sb.append(getWordWrap("【3】人事、规划计划类栏目，" + baseInfoStatis.getRsghCount() + "个应更新栏目未按时更新"));
        params.put("baseInfoDesc",sb.toString());
        params.put("baseInfoScore",-baseInfoStatis.getTotalScore());
        score = score.add(new BigDecimal(-baseInfoStatis.getTotalScore()));

        InteractInfoStatisVO replyStatis = monitorInteractDetailService.loadInteractInfoStatis(taskId,siteId);
        Integer zwzx = replyStatis.getZwzxStatus();
        Integer dczj = replyStatis.getDczjStatus();
        Integer hdft = replyStatis.getHdftStatus();
        if(zwzx == 0) {
            params.put("zwzxDesc","未开设政务咨询类栏目");
        } else if(zwzx == 1) {
            params.put("zwzxDesc","政务咨询类栏目:无有效信件、留言");
        } else if(zwzx == 2) {
            params.put("zwzxDesc","政务咨询类栏目:开展次数少于规定次数");
        }else{
            params.put("zwzxDesc","政务咨询类栏目:合格");
        }
        params.put("zwzxScore",-replyStatis.getZwzxScore());

        if(dczj == 0) {
            params.put("dczjDesc","未开设调查征集类栏目");
        } else if(dczj == 1) {
            params.put("dczjDesc","调查征集类栏目:未开展调查征集活动");
        } else if(dczj == 2) {
            params.put("dczjDesc","调查征集类栏目:开展次数少于规定次数");
        }else{
            params.put("dczjDesc","调查征集类栏目:合格");
        }
        params.put("dczjScore",-replyStatis.getDczjScore());

        if(hdft == 0) {
            params.put("hdftDesc","未开设互动访谈类栏目");
        } else if(hdft == 1) {
            params.put("hdftDesc","互动访谈类栏目:未开展互动访谈活动");
        } else if(hdft == 2) {
            params.put("hdftDesc","互动访谈类栏目:开展次数少于规定次数");
        }else{
            params.put("hdftDesc","互动访谈类栏目:合格");
        }
        params.put("hdftScore",-replyStatis.getHdftScore());
        score = score.add(new BigDecimal(-replyStatis.getTotalScore()));
        params.put("score",score.doubleValue());
        FreeMakerUtil.createDocument(response,params,"monitor_report.ftl",mgr.getName() + "(" + DateFormatUtils.format(new Date(),"yyyy.MM.dd") + ")",".doc");
    }



    /**
     * 获取各站点栏目更新情况
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteColumnDetaiList")
    public Object getSiteColumnDetaiList() {

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("isRegistered",1);
        List<MonitorSiteRegisterEO> siteInfos = monitorSiteRegisterService.getEntities(MonitorSiteRegisterEO.class,map);

        if(siteInfos==null||siteInfos.size()==0){
            return ajaxErr("暂无已启用监测的站点");
        }
        List<MonitorColumnDetailEO> SiteColumnDetaiList = new ArrayList<MonitorColumnDetailEO>();
        if(siteInfos!=null||siteInfos.size()>0){
            for(MonitorSiteRegisterEO siteRegisterEO:siteInfos){
                MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteRegisterEO.getSiteId(),null);
                if(task==null){
                    continue;
                }
                Long monitorId = task.getId();
                SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteRegisterEO.getSiteId());

                List<MonitorColumnDetailEO> columnDetailEOList = monitorColumnDetailService.getList(monitorId,
                        new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                                MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                                MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                                MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                                MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,0l);
                if(columnDetailEOList!=null&&columnDetailEOList.size()>0){
                    for(MonitorColumnDetailEO eo : columnDetailEOList){
                        eo.setSiteName(siteMgrEO.getName());
                        if(MonitorColumnDetailEO.INFO_TYPE.publics.toString().equals(eo.getInfoType())){//信息公开
                            try {
                                String[] ids =  eo.getColumnId().split("_");
                                OrganEO organ = CacheHandler.getEntity(OrganEO.class,Long.parseLong(ids[0]));
                                eo.setColumnName(organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(Long.parseLong(ids[0]), Long.parseLong(ids[1])));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{//新闻
                            eo.setColumnName(ColumnUtil.getColumnName(Long.parseLong(eo.getColumnId()), siteRegisterEO.getSiteId()));
                        }
                        SiteColumnDetaiList.add(eo);
                    }
                }
            }
        }

        return getObject(SiteColumnDetaiList);
    }


    /**
     * 跳转栏目更新分页情况
     * @param monitorId
     * @param map
     * @return
     */
    @RequestMapping(value = "viewSiteColumnDetai")
    public Object viewSiteColumnDetai(Long monitorId,ModelMap map) {
        map.put("monitorId",monitorId);
        return FILE_BASE + "view_site_column_detai";
    }

    /**
     * 获取栏目更新分页情况
     * @param vo
     * @param isStatard
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteColumnDetaiPage")
    public Object getSiteColumnDetaiPage(MonitorDetailQueryVO vo,Integer isStatard) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("isRegistered",1);
        List<MonitorSiteRegisterEO> siteInfos = monitorSiteRegisterService.getEntities(MonitorSiteRegisterEO.class,map);

        if(siteInfos==null||siteInfos.size()==0){
            return ajaxErr("暂无已启用监测的站点");
        }

        List<Long> monitorIds = new ArrayList<Long>();

        for(MonitorSiteRegisterEO siteRegisterEO:siteInfos){
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteRegisterEO.getSiteId(),null);
            if(task==null){
                continue;
            }
            Long monitorId = task.getId();
            monitorIds.add(monitorId);
        }
        vo.setMonitorIds(monitorIds);

        vo.setColumnType(Arrays.asList(new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_index.toString()}));
        return getObject(monitorColumnDetailService.getPage(vo));
    }


    /**
     * 获取各个站点栏目未更新情况TOP5
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteColumnInfos")
    public Object getColumnDetaiList() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("isRegistered",1);
        List<MonitorSiteRegisterEO> siteInfos = monitorSiteRegisterService.getEntities(MonitorSiteRegisterEO.class,map);

        if(siteInfos==null||siteInfos.size()==0){
            return ajaxErr("暂无已启用监测的站点");
        }

        Map<Long,Long> failMap = new HashMap<Long, Long>();//站点未达标数map
        Map<Long,Long> totalMap = new HashMap<Long, Long>();

        if(siteInfos!=null||siteInfos.size()>0){
            for(MonitorSiteRegisterEO siteRegisterEO:siteInfos){
                MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteRegisterEO.getSiteId(),null);
                if(task==null){
                    continue;
                }
                Long monitorId = task.getId();
                Long counts = 0l;//不达标栏目数
                Long columnCounts = 0l;//监测新闻栏目数
//                Long publicCounts = 0l;//监测信息公开栏目数
                if(monitorId!=null){
                    counts = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                            MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                            MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                            MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                            MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,0l);
                    columnCounts = monitorColumnDetailService.getCount(monitorId,
                            new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                                    MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                                    MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                                    MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                                    MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);
//                    publicCounts = monitorColumnDetailService.getCount(monitorId,
//                            null,MonitorColumnDetailEO.INFO_TYPE.publics.toString(),null);
                }
                failMap.put(siteRegisterEO.getSiteId(),counts);
                totalMap.put(siteRegisterEO.getSiteId(),columnCounts);
            }

        }
        ArrayList<Map.Entry<Long, Long>> entries = sortMap(failMap);
        List<SiteInfoStatisVO> statisVOS = new ArrayList<SiteInfoStatisVO>();
        int limit = 5;
        if(limit >= entries.size()){
            limit = entries.size();
        }

        for(int i=0;i<limit;i++){
            SiteInfoStatisVO statisVO = new SiteInfoStatisVO();
            Long siteId = entries.get(i).getKey();
            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            Long failCounts = entries.get(i).getValue();
            statisVO.setSiteId(siteId);
            statisVO.setTotalCounts(totalMap.get(siteId));
            statisVO.setFailCount(failCounts);
            statisVO.setSiteName(siteMgrEO.getName());
            statisVOS.add(statisVO);
        }

        return getObject(statisVOS);
    }

    /**
     * 获取各个站点栏目按时更新情况TOP10
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteColumnStatis")
    public Object getSiteColumnStatis() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("isRegistered",1);
        List<MonitorSiteRegisterEO> siteInfos = monitorSiteRegisterService.getEntities(MonitorSiteRegisterEO.class,map);

        if(siteInfos==null||siteInfos.size()==0){
            return ajaxErr("暂无已启用监测的站点");
        }

        Map<Long,Long> passMap = new HashMap<Long, Long>();//站点达标数map
        Map<Long,Long> totalMap = new HashMap<Long, Long>();

        if(siteInfos!=null||siteInfos.size()>0){
            for(MonitorSiteRegisterEO siteRegisterEO:siteInfos){
                MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteRegisterEO.getSiteId(),null);
                if(task==null){
                    continue;
                }
                Long monitorId = task.getId();
                Long counts = 0l;//不达标栏目数
                Long columnCounts = 0l;//监测新闻栏目数
//                Long publicCounts = 0l;//监测信息公开栏目数
                if(monitorId!=null){
                    counts = monitorColumnDetailService.getCount(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                            MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                            MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                            MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                            MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,0l);
                    columnCounts = monitorColumnDetailService.getCount(monitorId,
                            new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                                    MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
                                    MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
                                    MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),
                                    MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);
//                    publicCounts = monitorColumnDetailService.getCount(monitorId,
//                            null,MonitorColumnDetailEO.INFO_TYPE.publics.toString(),null);
                }
                passMap.put(siteRegisterEO.getSiteId(),columnCounts - counts);
                totalMap.put(siteRegisterEO.getSiteId(),columnCounts);
            }

        }
        ArrayList<Map.Entry<Long, Long>> entries = sortMap(passMap);
        List<String> xDatas = new ArrayList<String>();
        List<Long> passDatas = new ArrayList<Long>();
        List<Long> totalDatas = new ArrayList<Long>();
        int limit = 5;
        if(limit >= entries.size()){
            limit = entries.size();
        }
        for(int i=0;i<limit;i++){
            Long siteId = entries.get(i).getKey();
            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            Long passCounts = entries.get(i).getValue();
            xDatas.add(siteMgrEO.getName());
            passDatas.add(passCounts);
            totalDatas.add(totalMap.get(siteId));
        }

        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("xDatas", JSON.toJSON(xDatas));
        resultMap.put("passDatas",JSON.toJSON(passDatas));
        resultMap.put("totalDatas",JSON.toJSON(totalDatas));
        return resultMap;
    }


    /**
     * 获取各站点栏目更新情况
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteInteractDetaiList")
    public Object getSiteInteractDetaiList() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("isRegistered",1);
        List<MonitorSiteRegisterEO> siteInfos = monitorSiteRegisterService.getEntities(MonitorSiteRegisterEO.class,map);

        if(siteInfos==null||siteInfos.size()==0){
            return ajaxErr("暂无已启用监测的站点");
        }
        List<SiteInfoStatisVO> SiteInteractDetaiList = new ArrayList<SiteInfoStatisVO>();
        if(siteInfos!=null||siteInfos.size()>0){
            for(MonitorSiteRegisterEO siteRegisterEO:siteInfos){
                MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteRegisterEO.getSiteId(),null);
                if(task==null){
                    continue;
                }
                Long monitorId = task.getId();
                InteractInfoStatisVO statisVO = monitorInteractDetailService.loadInteractInfoStatis(monitorId,siteRegisterEO.getSiteId());
                Long counts = 0l;
                if(statisVO!=null){
                    if(statisVO.getZwzxScore()!=null&&statisVO.getZwzxScore().intValue() > 0){
                        counts += 1;
                    }
                    if(statisVO.getHdftScore()!=null&&statisVO.getHdftScore().intValue() > 0){
                        counts += 1;
                    }
                    if(statisVO.getDczjScore()!=null&&statisVO.getDczjScore().intValue() > 0){
                        counts += 1;
                    }
                }
                SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteRegisterEO.getSiteId());
                SiteInfoStatisVO siteInfoStatisVO = new SiteInfoStatisVO();
                siteInfoStatisVO.setSiteName(siteMgrEO.getName());
                siteInfoStatisVO.setSiteId(siteRegisterEO.getSiteId());
                siteInfoStatisVO.setTotalCounts(counts);
                siteInfoStatisVO.setUrl(siteMgrEO.getUri());
                SiteInteractDetaiList.add(siteInfoStatisVO);
            }
        }

        return getObject(SiteInteractDetaiList);
    }

    private ArrayList<Map.Entry<Long,Long>> sortMap(Map map){
        List<Map.Entry<Long, Long>> entries = new ArrayList<Map.Entry<Long, Long>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Long, Long>>() {
            public int compare(Map.Entry<Long, Long> obj1 , Map.Entry<Long, Long> obj2) {
                return (int)(obj2.getValue() - obj1.getValue());
            }
        });
        return (ArrayList<Map.Entry<Long, Long>>) entries;
    }

    private String getWordWrap(String value) {
        StringBuilder s = new StringBuilder();
        s.append(value);
        s.append("<w:br/>");
        return s.toString();
    }

    /**
     * 获取保留小数位数的数据
     * @param v
     * @param i
     * @return
     */
    private Double getDoubleValue(double v,int i) {
        BigDecimal b = new BigDecimal(v);
        return b.setScale(i, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private Long getLong(Map<String, Object> bc, String key) {
        if (!bc.containsKey(key) || bc.get(key) == null) {
            return null;
        }
        return Long.valueOf(String.valueOf(bc.get(key)));
    }

    private String getString(Map<String, Object> bc, String key) {
        if (!bc.containsKey(key) || bc.get(key) == null) {
            return "";
        }
        return String.valueOf(bc.get(key));
    }

    private String getUpdateTimeType(String monitoredTimeType){
        if ("day".equals(monitoredTimeType)) {
            return "天";
        } else if ("week".equals(monitoredTimeType)) {
            return "周";
        } else if ("month".equals(monitoredTimeType)) {
            return "个月";
        } else if ("year".equals(monitoredTimeType)) {
            return "年";
        }else{
            return "周";
        }
    }
}
