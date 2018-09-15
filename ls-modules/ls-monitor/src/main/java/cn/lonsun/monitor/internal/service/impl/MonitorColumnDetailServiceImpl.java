package cn.lonsun.monitor.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredColumnConfigService;
import cn.lonsun.monitor.config.internal.service.IMonitoredVetoConfigService;
import cn.lonsun.monitor.internal.dao.IMonitorColumnDetailDao;
import cn.lonsun.monitor.internal.entity.MonitorColumnDetailEO;
import cn.lonsun.monitor.internal.service.IMonitorColumnDetailService;
import cn.lonsun.monitor.internal.vo.*;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteStatisEO;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;
import cn.lonsun.monitor.task.internal.service.IMonitorCustomIndexManageService;
import cn.lonsun.monitor.task.internal.service.IMonitorIndexManageService;
import cn.lonsun.monitor.task.internal.service.IMonitorSiteStatisService;
import cn.lonsun.monitor.task.internal.service.IMonitorTaskManageService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import cn.lonsun.webservice.monitor.client.IMonitorColumnDetailWebClient;
import cn.lonsun.webservice.monitor.client.vo.ColumnDetailVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日常监测栏目更新详细service实现<br/>
 */
@Service("monitorColumnDetailService")
public class MonitorColumnDetailServiceImpl extends BaseService<MonitorColumnDetailEO> implements IMonitorColumnDetailService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @DbInject("monitorColumnDetail")
    private IMonitorColumnDetailDao monitorColumnDetailDao;

    @Autowired
    private IMonitoredColumnConfigService monitoredColumnConfigService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Resource
    private IMonitorTaskManageService monitorTaskManageService;

    @Resource
    private IMonitorIndexManageService monitorIndexManageService;

    @Autowired
    private IMonitorColumnDetailWebClient monitorColumnDetailWebClient;

    @Autowired
    private IMonitoredVetoConfigService monitoredVetoConfigService;

    @Resource
    private IMonitorCustomIndexManageService monitorCustomIndexManageService;

    @Resource
    private IScheduleJobService scheduleJobService;

    @Resource
    private IMonitorSiteStatisService monitorSiteStatisService;

    @Resource
    private TaskExecutor taskExecutor;

    @Override
    public Pagination getPage(MonitorDetailQueryVO queryVO) {
        Pagination page = monitorColumnDetailDao.getPage(queryVO);
        List<MonitorColumnDetailEO> dataList = (List<MonitorColumnDetailEO>) page.getData();
        for (MonitorColumnDetailEO eo : dataList) {
            Long monitorId = eo.getMonitorId()==null?0:eo.getMonitorId();
            MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,monitorId);
            Long siteId = task==null?LoginPersonUtil.getSiteId():task.getSiteId();
            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            if(siteMgrEO!=null){
                eo.setSiteName(siteMgrEO.getName());
            }

            if(MonitorColumnDetailEO.INFO_TYPE.publics.toString().equals(eo.getInfoType())){//信息公开
                try {
                    String[] ids =  eo.getColumnId().split("_");
                    OrganEO organ = CacheHandler.getEntity(OrganEO.class,Long.parseLong(ids[0]));
                    eo.setColumnName(organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(Long.parseLong(ids[0]), Long.parseLong(ids[1])));
                    eo.setColumnUrl(siteMgrEO.getUri()+"/public/column/"+ids[0]+"?type=4&catId="+ids[1]+"&action=list");//栏目页访问地址
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{//新闻

                String columnUrl = siteMgrEO.getUri()+"/"+eo.getColumnId()+".html";
                try{
                    ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class,eo.getColumnId());
                    if(existsField(ColumnMgrEO.class,"urlPath")&&
                            columnMgrEO!=null&&!AppUtil.isEmpty(columnMgrEO.getUrlPath())){
                        columnUrl = siteMgrEO.getUri()+"/"+columnMgrEO.getUrlPath()+"/index.html";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                eo.setColumnUrl(columnUrl);
                eo.setColumnName(ColumnUtil.getColumnName(Long.parseLong(eo.getColumnId()), siteId));
            }
        }
        return page;
    }

    @Override
    public void handleColumnDatasByType(Long siteId, Long monitorId, Long reportId, String columnType) {
        String columnIds = getColumnByCode(columnType, siteId);  //根据columnType获取绑定的栏目
        String organCatIds = getPublicCatsByCode(columnType, siteId);//根据columnType获取绑定的信息公开目录
        if (AppUtil.isEmpty(columnIds) && AppUtil.isEmpty(organCatIds)) {
            return;
        }

        //从配置中获取栏目监测周期
        Map<String,Object> map = getUpdateCycle(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(), columnType,
                MonitoredVetoConfigEO.BaseCode.scop.toString(), siteId);
        Integer updateCycle = (Integer) map.get("updateCycle");
        String updateCycleStr = (String)map.get("updateCycleStr");

        List<ColumnDetailVO> columnDetailVOS = new ArrayList<ColumnDetailVO>();

        //处理新闻数据
        if(!AppUtil.isEmpty(columnIds)){
            columnDetailVOS = handleNewsDatas(columnDetailVOS,columnIds,updateCycle,updateCycleStr,columnType,reportId,siteId,monitorId);
        }
        //处理信息公开数据
        if(!AppUtil.isEmpty(organCatIds)){
            columnDetailVOS = handlePublicsDatas(columnDetailVOS,organCatIds,updateCycle,updateCycleStr,columnType,reportId,siteId,monitorId);
        }

        final List<ColumnDetailVO> columnDetailVOS_ = new ArrayList<ColumnDetailVO>();
        columnDetailVOS_.addAll(columnDetailVOS);
        if (reportId != null && reportId != 0) {
            //将信息推到到云平台
            if(columnDetailVOS!=null&&columnDetailVOS.size()>0){
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //将信息推到到云平台
                            monitorColumnDetailWebClient.saveColumnDetails(columnDetailVOS_);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });


            }
        }
    }


    /**
     * 处理新闻数据
     * @param columnDetailVOS
     * @param columnIds
     * @param updateCycle
     * @param columnType
     * @param reportId
     * @param siteId
     * @param monitorId
     * @return
     */
    private List<ColumnDetailVO> handleNewsDatas(List<ColumnDetailVO> columnDetailVOS, String columnIds, Integer updateCycle,
                                                 String updateCycleStr,String columnType, Long reportId, Long siteId, Long monitorId) {

        if(AppUtil.isEmpty(columnIds)){
            return new ArrayList<ColumnDetailVO>();
        }

        String[] columnId = StringUtils.split(columnIds, ",");
        List<MonitorColumnDetailEO> monitorColumnDetailEOS = monitorColumnDetailDao.getUpdatedColumns(columnIds, updateCycle);//查询出规定周期内一更新数据的栏目
        Map<String, MonitorColumnDetailEO> monitorColumnDetailEOMap = new HashMap<String, MonitorColumnDetailEO>();
        for (MonitorColumnDetailEO eo : monitorColumnDetailEOS) {
            monitorColumnDetailEOMap.put(eo.getColumnId(), eo);
        }
        //保存未更新数据的栏目导监测字表中
        for (int i = 0; i < columnId.length; i++) {
            String curColumnId = columnId[i];
            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            String columnUrl = siteMgrEO.getUri()+"/"+curColumnId+".html";
            try{
                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class,curColumnId);
                if(existsField(ColumnMgrEO.class,"urlPath")&&
                        columnMgrEO!=null&&!AppUtil.isEmpty(columnMgrEO.getUrlPath())){
                    columnUrl = siteMgrEO.getUri()+"/"+columnMgrEO.getUrlPath()+"/index.html";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if (monitorColumnDetailEOMap.containsKey(curColumnId)) {
                MonitorColumnDetailEO eo = monitorColumnDetailEOMap.get(curColumnId);
                eo.setColumnType(columnType);
                eo.setMonitorId(monitorId);//设置监测id
                eo.setUpdateCycle(updateCycle);
                eo.setUpdateCycleStr(updateCycleStr);
                eo.setUnPublishDays((new Double(eo.getUnPublishDays_())).intValue());
                eo.setInfoType(MonitorColumnDetailEO.INFO_TYPE.news.toString());
                eo.setColumnUrl(columnUrl);
                saveEntity(eo);

                if (reportId != null && reportId != 0) {
                    //将信息推到到云平台
                    ColumnDetailVO columnDetailVO = new ColumnDetailVO();
                    columnDetailVO.setColumnId(curColumnId);
                    columnDetailVO.setColumnName(ColumnUtil.getColumnName(Long.parseLong(curColumnId), siteId));
                    columnDetailVO.setColumnType(columnType);
                    columnDetailVO.setUpdateCount(eo.getUpdateCount());
                    columnDetailVO.setLastPublishDate(eo.getLastPublishDate());
                    columnDetailVO.setUpdateCycle(updateCycle);
                    columnDetailVO.setUpdateCycleStr(updateCycleStr);
                    columnDetailVO.setUnPublishDays((new Double(eo.getUnPublishDays_())).intValue());
                    columnDetailVO.setInfoType(MonitorColumnDetailEO.INFO_TYPE.news.toString());
                    columnDetailVO.setReportId(reportId);//设置监测id
                    columnDetailVO.setColumnUrl(columnUrl);
                    columnDetailVOS.add(columnDetailVO);
                }
            } else {//空白栏目
                MonitorColumnDetailEO eo = new MonitorColumnDetailEO();
                eo.setColumnId(curColumnId);
                eo.setColumnType(columnType);
                eo.setUpdateCount(0L);
                eo.setUpdateCycle(updateCycle);
                eo.setUpdateCycleStr(updateCycleStr);
                eo.setUnPublishDays(0);
                eo.setInfoType(MonitorColumnDetailEO.INFO_TYPE.news.toString());
                eo.setMonitorId(monitorId);//设置监测id
                eo.setColumnUrl(columnUrl);
                saveEntity(eo);

                if (reportId != null && reportId != 0) {
                    //将信息推到到云平台
                    ColumnDetailVO columnDetailVO = new ColumnDetailVO();
                    columnDetailVO.setColumnId(curColumnId);
                    columnDetailVO.setColumnName(ColumnUtil.getColumnName(Long.parseLong(curColumnId), siteId));
                    columnDetailVO.setColumnType(columnType);
                    columnDetailVO.setUpdateCount(0L);
                    columnDetailVO.setUpdateCycle(updateCycle);
                    columnDetailVO.setUpdateCycleStr(updateCycleStr);
                    columnDetailVO.setUnPublishDays(0);
                    columnDetailVO.setInfoType(MonitorColumnDetailEO.INFO_TYPE.news.toString());
                    columnDetailVO.setReportId(reportId);//设置监测id
                    columnDetailVO.setColumnUrl(columnUrl);
                    columnDetailVOS.add(columnDetailVO);
                }
            }
        }
        return columnDetailVOS;
    }


    /**
     * 处理信息公开数据
     * @param columnDetailVOS
     * @param organCatIds
     * @param updateCycle
     * @param columnType
     * @param reportId
     * @param siteId
     * @param monitorId
     * @return
     */
    private List<ColumnDetailVO> handlePublicsDatas(List<ColumnDetailVO> columnDetailVOS, String organCatIds, Integer updateCycle,
                                                 String updateCycleStr,String columnType, Long reportId, Long siteId, Long monitorId) {
        if(AppUtil.isEmpty(organCatIds)){
            return new ArrayList<ColumnDetailVO>();
        }

        String[] organCatId = StringUtils.split(organCatIds, ",");
        List<MonitorColumnDetailEO> monitorColumnDetailEOS = monitorColumnDetailDao.getUpdatedPublics(organCatId, updateCycle);//查询出规定周期内一更新数据的栏目
        Map<String, MonitorColumnDetailEO> monitorColumnDetailEOMap = new HashMap<String, MonitorColumnDetailEO>();
        for (MonitorColumnDetailEO eo : monitorColumnDetailEOS) {
            monitorColumnDetailEOMap.put(eo.getColumnId(), eo);
        }
        //保存未更新数据的栏目导监测字表中
        for (int i = 0; i < organCatId.length; i++) {
            String curOrganCatId = organCatId[i];
            String columnName = "";
            String columnUrl = "";
            try {
                String[] ids =  curOrganCatId.split("_");
                OrganEO organ = CacheHandler.getEntity(OrganEO.class,Long.parseLong(ids[0]));
                columnName = organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(Long.parseLong(ids[0]), Long.parseLong(ids[1]));
                SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
                columnUrl = siteMgrEO.getUri()+"/public/column/"+ids[0]+"?type=4&catId="+ids[1]+"&action=list";//栏目页访问地址
            }catch (Exception e){
                e.printStackTrace();
            }

            if (monitorColumnDetailEOMap.containsKey(curOrganCatId)) {
                MonitorColumnDetailEO eo = monitorColumnDetailEOMap.get(curOrganCatId);
                eo.setColumnType(columnType);
                eo.setMonitorId(monitorId);//设置监测id
                eo.setUpdateCycle(updateCycle);
                eo.setUpdateCycleStr(updateCycleStr);
                eo.setUnPublishDays((new Double(eo.getUnPublishDays_())).intValue());
                eo.setInfoType(MonitorColumnDetailEO.INFO_TYPE.publics.toString());
                eo.setColumnUrl(columnUrl);
                saveEntity(eo);

                if (reportId != null && reportId != 0) {
                    //将信息推到到云平台
                    ColumnDetailVO columnDetailVO = new ColumnDetailVO();
                    columnDetailVO.setColumnId(curOrganCatId);
                    columnDetailVO.setColumnName(columnName);
                    columnDetailVO.setColumnType(columnType);
                    columnDetailVO.setUpdateCount(eo.getUpdateCount());
                    columnDetailVO.setLastPublishDate(eo.getLastPublishDate());
                    columnDetailVO.setUpdateCycle(updateCycle);
                    columnDetailVO.setUpdateCycleStr(updateCycleStr);
                    columnDetailVO.setUnPublishDays((new Double(eo.getUnPublishDays_())).intValue());
                    columnDetailVO.setInfoType(MonitorColumnDetailEO.INFO_TYPE.publics.toString());
                    columnDetailVO.setReportId(reportId);//设置监测id
                    columnDetailVO.setColumnUrl(columnUrl);
                    columnDetailVOS.add(columnDetailVO);
                }
            } else {//空白栏目
                MonitorColumnDetailEO eo = new MonitorColumnDetailEO();
                eo.setColumnId(curOrganCatId);
                eo.setColumnType(columnType);
                eo.setUpdateCount(0L);
                eo.setUnPublishDays(0);
                eo.setUpdateCycle(updateCycle);
                eo.setUpdateCycleStr(updateCycleStr);
                eo.setInfoType(MonitorColumnDetailEO.INFO_TYPE.publics.toString());
                eo.setMonitorId(monitorId);//设置监测id
                eo.setColumnUrl(columnUrl);
                saveEntity(eo);

                if (reportId != null && reportId != 0) {
                    //将信息推到到云平台
                    ColumnDetailVO columnDetailVO = new ColumnDetailVO();
                    columnDetailVO.setColumnId(curOrganCatId);
                    columnDetailVO.setColumnName(columnName);
                    columnDetailVO.setColumnType(columnType);
                    columnDetailVO.setUpdateCount(0L);
                    columnDetailVO.setUpdateCycle(updateCycle);
                    columnDetailVO.setUpdateCycleStr(updateCycleStr);
                    columnDetailVO.setUnPublishDays(0);
                    columnDetailVO.setInfoType(MonitorColumnDetailEO.INFO_TYPE.publics.toString());
                    columnDetailVO.setReportId(reportId);//设置监测id
                    columnDetailVO.setColumnUrl(columnUrl);
                    columnDetailVOS.add(columnDetailVO);
                }
            }
        }
        return columnDetailVOS;
    }

    /**
     * 监测空白栏目
     *
     * @param monitorId
     */
    @Override
    public void handleEmptyColumns(Long siteId, Long monitorId, Long reportId) {
        List<Long> emptyColumnList = getEmptyColumnList(siteId);
        final List<ColumnDetailVO> columnDetailVOS = new ArrayList<ColumnDetailVO>();
        //非空白栏目也要统计出来
        List<MonitorColumnDetailEO> unEmptyColumnInfoList = monitorColumnDetailDao.getUnEmptyColumnInfoList(siteId);

        //查出配置的空白栏目，这些栏目不在监测范围之内
        List<String> columnIdList = new ArrayList<String>();
        String columnIds = getColumnByCode(MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString(), siteId);  //根据columnType获取绑定的栏目
        columnIdList.addAll(Arrays.asList(StringUtils.split(columnIds, ",")));

        for (MonitorColumnDetailEO eo : unEmptyColumnInfoList) {
            if(columnIdList.contains(eo.getColumnId()+"")){//不需要监测的栏目
                continue;
            }

            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class,eo.getColumnId());
            String columnUrl = siteMgrEO.getUri()+"/"+eo.getColumnId()+".html";//栏目页访问地址
            try{
                if(existsField(ColumnMgrEO.class,"urlPath")&&
                        columnMgrEO!=null&&!AppUtil.isEmpty(columnMgrEO.getUrlPath())){
                    columnUrl = siteMgrEO.getUri()+"/"+columnMgrEO.getUrlPath()+"/index.html";
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            eo.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString());
            eo.setMonitorId(monitorId);//设置监测id
            eo.setColumnUrl(columnUrl);
            eo.setUnPublishDays((new Double(eo.getUnPublishDays_())).intValue());
            saveEntity(eo);

            if (reportId != null && reportId != 0) {
                //将信息推到到云平台
                String curColumnId = eo.getColumnId();
                ColumnDetailVO columnDetailVO = new ColumnDetailVO();
                columnDetailVO.setColumnId(curColumnId);
                columnDetailVO.setColumnName(ColumnUtil.getColumnName(Long.parseLong(curColumnId), siteId));
                columnDetailVO.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString());
                columnDetailVO.setUpdateCount(eo.getUpdateCount());
                columnDetailVO.setColumnUrl(columnUrl);
                columnDetailVO.setLastPublishDate(eo.getLastPublishDate());
                columnDetailVO.setUnPublishDays((new Double(eo.getUnPublishDays_())).intValue());
                columnDetailVO.setReportId(reportId);//设置监测id
                columnDetailVOS.add(columnDetailVO);
            }
        }

        for (Long columnId : emptyColumnList) {
            if(columnIdList.contains(columnId+"")){//不需要监测的栏目
                continue;
            }

            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            String columnUrl = siteMgrEO.getUri()+"/"+columnId+".html";//栏目页访问地址
            try{
                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class,columnId);
                if(existsField(ColumnMgrEO.class,"urlPath")&&
                        columnMgrEO!=null&&!AppUtil.isEmpty(columnMgrEO.getUrlPath())){
                    columnUrl = siteMgrEO.getUri()+"/"+columnMgrEO.getUrlPath()+"/index.html";
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            MonitorColumnDetailEO eo = new MonitorColumnDetailEO();
            eo.setColumnId(columnId.toString());
            eo.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString());
            eo.setUpdateCount(0L);
            eo.setUnPublishDays(0);
            eo.setColumnUrl(columnUrl);
            eo.setMonitorId(monitorId);//设置监测id
            saveEntity(eo);


            if (reportId != null && reportId != 0) {
                //将信息推到到云平台
                ColumnDetailVO columnDetailVO = new ColumnDetailVO();
                columnDetailVO.setColumnId(columnId.toString());
                columnDetailVO.setColumnName(ColumnUtil.getColumnName(columnId, siteId));
                columnDetailVO.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString());
                columnDetailVO.setUpdateCount(0L);
                columnDetailVO.setUnPublishDays(0);
                columnDetailVO.setColumnUrl(columnUrl);
                columnDetailVO.setReportId(reportId);//设置监测id
                columnDetailVOS.add(columnDetailVO);
            }
        }
        if (reportId != null && reportId != 0) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        //将信息推到到云平台
                        monitorColumnDetailWebClient.saveColumnDetails(columnDetailVOS);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private Integer getInt(Map<String, Object> bc, String key) {
        if (!bc.containsKey(key) || bc.get(key) == null) {
            return null;
        }
        return Integer.valueOf(String.valueOf(bc.get(key)));
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



    private Map<String,Object> getUpdateCycle(String typeCode, String columnTypeCode, String baseCode, Long siteId) {
        Integer dayOffset = 0;
        String updateCycleStr = "";
        Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(typeCode, columnTypeCode, baseCode, siteId);
        if (bc != null) {
            Long monitoredNum = getLong(bc, "monitoredNum");
            String monitoredTimeType = getString(bc, "monitoredTimeType");

            //从配置中获取首页栏目监测周期
            if (!AppUtil.isEmpty(monitoredNum) && !AppUtil.isEmpty(monitoredTimeType)) {
                if ("day".equals(monitoredTimeType)) {
                    dayOffset = monitoredNum.intValue();
                    updateCycleStr = monitoredNum.intValue()+"天";
                } else if ("week".equals(monitoredTimeType)) {
                    dayOffset = monitoredNum.intValue() * 7;
                    updateCycleStr = monitoredNum.intValue()+"周";
                } else if ("month".equals(monitoredTimeType)) {
                    dayOffset = monitoredNum.intValue() * 30;
                    updateCycleStr = monitoredNum.intValue()+"个月";
                } else if ("year".equals(monitoredTimeType)) {
                    dayOffset = monitoredNum.intValue() * 365;
                    updateCycleStr = monitoredNum.intValue()+"年";
                }
            }
        }

        if (dayOffset == 0) {//没查到配置信息
            if (MonitoredColumnConfigEO.TypeCode.columnType_index.toString().equals(columnTypeCode)) {
                dayOffset = 14;
                updateCycleStr = "2周";
            } else if (MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString().equals(columnTypeCode)) {
                dayOffset = 14;
                updateCycleStr = "2周";
            } else if (MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString().equals(columnTypeCode)) {
                dayOffset = 180;
                updateCycleStr = "6个月";
            } else if (MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString().equals(columnTypeCode)) {
                dayOffset = 365;
                updateCycleStr = "1年";
            } else if (MonitoredColumnConfigEO.TypeCode.columnType_update.toString().equals(columnTypeCode)) {
                dayOffset = 365;
                updateCycleStr = "1年";
            }
        }

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("updateCycle",dayOffset);
        map.put("updateCycleStr",updateCycleStr);
        return map;
    }


    /**
     * 启动新闻监测任务
     *
     * @param monitorId
     */
    @Override
    public void monitorColumn(Long siteId, Long monitorId, Long reportId) {
        monitorIndexColumn(siteId, monitorId, reportId);//监测首页栏目
        monitorBaseColumn(siteId, monitorId, reportId);//监测栏目更新

        if(reportId!=null&&reportId!=0) {
            monitorTaskManageService.updateStatus(monitorId, "infoUpdateStatus", 2);
        } else {
            monitorCustomIndexManageService.updateStatus(monitorId,2);
        }

    }

//    /**
//     * 监测首页栏目
//     *
//     * @param siteId
//     * @param monitorId
//     * @param reportId
//     */
//    @Override
//    public void monitorIndexColumn(Long siteId, Long monitorId, Long reportId) {
//        Integer dayOffset = (Integer) getUpdateCycle(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
//                MonitoredColumnConfigEO.TypeCode.columnType_index.toString(), MonitoredVetoConfigEO.BaseCode.scop.toString()
//                , siteId).get("updateCycle");
//
//
//        //首页栏目
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dayOffset);
//
//        JobParamVO vo = new JobParamVO();
//        vo.setReportId(reportId);
//        vo.setSiteId(siteId);
//        vo.setTaskId(monitorId);
//        vo.setTimeout(dayOffset);//此处存的是更新周期
//        vo.setOverTime(calendar.getTime().getTime());
//        vo.setCron("0 0 0 * * ?");
//        ScheduleJobEO eo = new ScheduleJobEO();
//        eo.setSiteId(siteId);
//        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        String text;
//        if (null != reportId) {
//            text = "站点["+siteId+"]["+dateStr+"]首页栏目监测";
//        } else {
//            text = "站点["+siteId+"]["+dateStr+"]自定义首页栏目监测";
//        }
//        eo.setName(text);
//        eo.setType("site_update");
//        eo.setJson(JSONObject.fromObject(vo).toString());
//        eo.setClazz(SylmUpdateTaskImpl.class.getName());
//        eo.setStatus("NORMAL");
//        //定时模式是每天零点
//        eo.setCronExpression("0 0 0 * * ?");
//        Long schedId = scheduleJobService.saveEntity(eo);
//        if(null != reportId) {
//            MonitorIndexManageEO index = monitorIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteUpdate.toString(),siteId);
//            if(null != index) {
//                index.setScheduleId(schedId);
//            }
//            monitorIndexManageService.updateEntity(index);
//        } else {
//            MonitorCustomIndexManageEO index = monitorCustomIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteUpdate.toString(), siteId);
//            if (null != index) {
//                index.setScheduleId(schedId);
//            }
//            monitorCustomIndexManageService.updateEntity(index);
//        }
//    }


    /**
     * 监测首页栏目
     *
     * @param siteId
     * @param monitorId
     * @param reportId
     */
    @Override
    public void monitorIndexColumn(Long siteId, Long monitorId, Long reportId) {

        Map<String,Object> map = getUpdateCycle(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_index.toString(), MonitoredVetoConfigEO.BaseCode.scop.toString()
                , siteId);

        Integer dayOffset = (Integer) map.get("updateCycle");
        String updateCycleStr = (String)map.get("updateCycleStr");

        //首页栏目
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - dayOffset);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date nextSt = DateUtil.getDayDate(calendar.getTime());


        String columnIds = "";//根据columnType获取绑定的栏目
        List<ColumnMgrEO> columnMgrEOS = monitoredColumnConfigService.getColumnByCode(
                MonitoredColumnConfigEO.TypeCode.columnType_index.toString(),siteId);  //根据columnType获取绑定的栏目
        for(ColumnMgrEO columnMgrEO:columnMgrEOS){//将columnid拼成字符串
            if(!AppUtil.isEmpty(columnIds)){
                columnIds += ","+columnMgrEO.getIndicatorId();
            }else{
                columnIds += columnMgrEO.getIndicatorId();
            }
        }
        String[] columnId = StringUtils.split(columnIds, ",");

        String organCatIds = monitoredColumnConfigService.getPublicCatsByCode(MonitoredColumnConfigEO.TypeCode.columnType_index.toString(),siteId);

        String[] organCatId = new String[]{};
        if(!AppUtil.isEmpty(organCatIds)){
            organCatId = StringUtils.split(organCatIds, ",");
        }

        if(AppUtil.isEmpty(columnIds)&&AppUtil.isEmpty(organCatIds)){
            return;
        }

        Date st,ed;
        String publishDate;
        Map<String,MonitorColumnDetailEO> columnDetailEOSMap = new HashMap<String, MonitorColumnDetailEO>();
        Map<String,ColumnDetailVO> columnDetailVOSMap = new HashMap<String, ColumnDetailVO>();
        for(int j = 0;j<dayOffset;j++){
            st = nextSt;//单天开始时间
            ed = DateUtil.getDayDate(st,1);//单天结束时间
            publishDate = sdf.format(st);
            nextSt = ed;//下次监测开始时间

            List<MonitorColumnDetailEO> monitorColumnDetailEOS = null;//查询前一天的内容
            monitorColumnDetailEOS = getIndexUpdatedColumns(columnIds, st, ed);
            Map<String,MonitorColumnDetailEO> monitorColumnDetailEOMap = new HashMap<String, MonitorColumnDetailEO>();
            for(MonitorColumnDetailEO eo:monitorColumnDetailEOS){
                monitorColumnDetailEOMap.put(eo.getColumnId(),eo);
            }

            //保存未更新数据的栏目导监测字表中
            for(int i=0;i<columnId.length;i++){
                String curColumnId = columnId[i];
                MonitorColumnDetailEO oldEO = columnDetailEOSMap.get(curColumnId);
                String publishDetail = "";
                if(oldEO!=null){
                    publishDetail = oldEO.getPublishDetail();
                }

                //该栏目该天的监测记录已存在，跳过
                if(!AppUtil.isEmpty(publishDetail)&&publishDetail.contains(publishDate)){
                    continue;
                }

                Long updateCount = 0l;
                Date lastPublishDate = null;
                Integer unPublishDays = 0;
                if(monitorColumnDetailEOMap.containsKey(curColumnId)){
                    MonitorColumnDetailEO eo = monitorColumnDetailEOMap.get(curColumnId);
                    ColumnPublishVO columnPublishVO = new ColumnPublishVO();
                    columnPublishVO.setPublishDate(publishDate);
                    columnPublishVO.setUpdateCount(eo.getUpdateCount());
                    lastPublishDate = eo.getLastPublishDate();
                    unPublishDays = (new Double(eo.getUnPublishDays_())).intValue();
                    if(oldEO!=null){//已存在，更新记录
                        updateCount = oldEO.getUpdateCount()+eo.getUpdateCount();
                    }else{
                        updateCount = eo.getUpdateCount();
                    }
                    String jsonStr_= JSONObject.fromObject(columnPublishVO).toString();
                    if(!AppUtil.isEmpty(publishDetail)){
                        publishDetail += ","+jsonStr_;
                    }else{
                        publishDetail = jsonStr_;
                    }
                }else{
                    ColumnPublishVO columnPublishVO = new ColumnPublishVO();
                    columnPublishVO.setPublishDate(publishDate);
                    columnPublishVO.setUpdateCount(0l);
                    String jsonStr_= JSONObject.fromObject(columnPublishVO).toString();
                    if(!AppUtil.isEmpty(publishDetail)){
                        publishDetail += ","+jsonStr_;
                    }else{
                        publishDetail = jsonStr_;
                    }
                }

                SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
                String columnUrl = siteMgrEO.getUri()+"/"+curColumnId+".html";
                try{
                    ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class,curColumnId);
                    //新网站跟老网站栏目页访问地址不一样，需要通过是否含有urlPath字段区分下
                    if(existsField(ColumnMgrEO.class,"urlPath")&&columnMgrEO!=null
                            &&!AppUtil.isEmpty(columnMgrEO.getUrlPath())){
                        columnUrl = siteMgrEO.getUri()+"/"+columnMgrEO.getUrlPath()+"/index.html";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                if(oldEO==null){
                    MonitorColumnDetailEO eo = new MonitorColumnDetailEO();
                    eo.setColumnId(curColumnId);
                    eo.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
                    eo.setUpdateCount(updateCount);
                    eo.setLastPublishDate(lastPublishDate);
                    eo.setUnPublishDays(unPublishDays);
                    eo.setUpdateCycle(dayOffset);
                    eo.setUpdateCycleStr(updateCycleStr);
                    eo.setMonitorId(monitorId);//设置监测id
                    eo.setPublishDetail(publishDetail);//设置发布时间
                    eo.setColumnUrl(columnUrl);
                    eo.setInfoType(MonitorColumnDetailEO.INFO_TYPE.news.toString());
                    columnDetailEOSMap.put(curColumnId,eo);
                }else{//更新原有栏目信息
                    oldEO.setUpdateCount(updateCount);
                    oldEO.setLastPublishDate(lastPublishDate);
                    oldEO.setUnPublishDays(unPublishDays);
                    oldEO.setUpdateCycleStr(updateCycleStr);
                    oldEO.setColumnUrl(columnUrl);
                    oldEO.setPublishDetail(publishDetail);
                    columnDetailEOSMap.put(curColumnId,oldEO);
                }

                if(reportId!=null&&reportId!=0){
                    //将信息推到到云平台
                    ColumnDetailVO columnDetailVO = new ColumnDetailVO();
                    columnDetailVO.setColumnId(curColumnId);
                    columnDetailVO.setColumnName(ColumnUtil.getColumnName(Long.parseLong(curColumnId),siteId));
                    columnDetailVO.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
                    columnDetailVO.setUpdateCount(updateCount);
                    columnDetailVO.setLastPublishDate(lastPublishDate);
                    columnDetailVO.setUnPublishDays(unPublishDays);
                    columnDetailVO.setUpdateCycle(dayOffset);
                    columnDetailVO.setUpdateCycleStr(updateCycleStr);
                    columnDetailVO.setPublishDetail(publishDetail);//设置发布时间
                    columnDetailVO.setInfoType(MonitorColumnDetailEO.INFO_TYPE.news.toString());
                    columnDetailVO.setReportId(reportId);//设置监测id
                    columnDetailVO.setColumnUrl(columnUrl);
//                    columnDetailVOS.add(columnDetailVO);
                    columnDetailVOSMap.put(curColumnId,columnDetailVO);
                }
            }



            List<MonitorColumnDetailEO> publicDetailEOS = getIndexUpdatedPublics(organCatId, st, ed);//查询出前一天更新数据的栏目
            Map<String, MonitorColumnDetailEO> publicDetailEOMap = new HashMap<String, MonitorColumnDetailEO>();
            for (MonitorColumnDetailEO eo : publicDetailEOS) {
                publicDetailEOMap.put(eo.getColumnId(), eo);
            }
            //保存未更新数据的栏目导监测字表中
            for (int i = 0; i < organCatId.length; i++) {
                String curOrganCatId = organCatId[i];
                MonitorColumnDetailEO oldEO = columnDetailEOSMap.get(curOrganCatId);
                String publishDetail = "";
                if(oldEO!=null){
                    publishDetail = oldEO.getPublishDetail();
                }

                //该栏目该天的监测记录已存在，跳过
                if(!AppUtil.isEmpty(publishDetail)&&publishDetail.contains(publishDate)){
                    continue;
                }

                Long updateCount = 0l;
                Date lastPublishDate = null;
                Integer unPublishDays = 0;
                if (publicDetailEOMap.containsKey(curOrganCatId)) {
                    MonitorColumnDetailEO eo = publicDetailEOMap.get(curOrganCatId);

                    ColumnPublishVO columnPublishVO = new ColumnPublishVO();
                    columnPublishVO.setPublishDate(publishDate);
                    columnPublishVO.setUpdateCount(eo.getUpdateCount());
                    lastPublishDate = eo.getLastPublishDate();
                    unPublishDays = (new Double(eo.getUnPublishDays_())).intValue();
                    if(oldEO!=null){//已存在，更新记录
                        updateCount = oldEO.getUpdateCount()+eo.getUpdateCount();
                    }else{
                        updateCount = eo.getUpdateCount();
                    }
                    String jsonStr_= JSONObject.fromObject(columnPublishVO).toString();
                    if(!AppUtil.isEmpty(publishDetail)){
                        publishDetail += ","+jsonStr_;
                    }else{
                        publishDetail = jsonStr_;
                    }
                }else{
                    ColumnPublishVO columnPublishVO = new ColumnPublishVO();
                    columnPublishVO.setUpdateCount(0l);
                    columnPublishVO.setPublishDate(publishDate);
                    String jsonStr_= JSONObject.fromObject(columnPublishVO).toString();
                    if(!AppUtil.isEmpty(publishDetail)){
                        publishDetail += ","+jsonStr_;
                    }else{
                        publishDetail = jsonStr_;
                    }
                }

                String columnName = "";
                String columnUrl = "";
                try {
                    String[] ids =  curOrganCatId.split("_");
                    OrganEO organ = CacheHandler.getEntity(OrganEO.class,Long.parseLong(ids[0]));
                    SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
                    columnName = organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(Long.parseLong(ids[0]), Long.parseLong(ids[1]));
                    columnUrl = siteMgrEO.getUri()+"/public/column/"+ids[0]+"?type=4&catId="+ids[1]+"&action=list";//栏目页访问地址
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(oldEO==null){
                    MonitorColumnDetailEO eo = new MonitorColumnDetailEO();
                    eo.setColumnId(curOrganCatId);
                    eo.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
                    eo.setUpdateCount(updateCount);
                    eo.setUnPublishDays(unPublishDays);
                    eo.setLastPublishDate(lastPublishDate);
                    eo.setUpdateCycle(dayOffset);
                    eo.setUpdateCycleStr(updateCycleStr);
                    eo.setMonitorId(monitorId);//设置监测id
                    eo.setPublishDetail(publishDetail);//设置发布时间
                    eo.setInfoType(MonitorColumnDetailEO.INFO_TYPE.publics.toString());
                    eo.setColumnUrl(columnUrl);
                    columnDetailEOSMap.put(curOrganCatId,eo);

                }else{//更新原有栏目信息
                    oldEO.setUpdateCount(updateCount);
                    oldEO.setUnPublishDays(unPublishDays);
                    oldEO.setUpdateCycleStr(updateCycleStr);
                    oldEO.setLastPublishDate(lastPublishDate);
                    oldEO.setPublishDetail(publishDetail);
                    oldEO.setColumnUrl(columnUrl);
                    columnDetailEOSMap.put(curOrganCatId,oldEO);
                }

                if(reportId!=null&&reportId!=0){
                    //将信息推到到云平台
                    ColumnDetailVO columnDetailVO = new ColumnDetailVO();
                    columnDetailVO.setColumnId(curOrganCatId);
                    columnDetailVO.setColumnName(columnName);
                    columnDetailVO.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
                    columnDetailVO.setUpdateCount(updateCount);
                    columnDetailVO.setLastPublishDate(lastPublishDate);
                    columnDetailVO.setUnPublishDays(unPublishDays);
                    columnDetailVO.setUpdateCycle(dayOffset);
                    columnDetailVO.setUpdateCycleStr(updateCycleStr);
                    columnDetailVO.setPublishDetail(publishDetail);//设置发布时间
                    columnDetailVO.setInfoType(MonitorColumnDetailEO.INFO_TYPE.publics.toString());
                    columnDetailVO.setReportId(reportId);//设置监测id
                    columnDetailVO.setColumnUrl(columnUrl);
                    columnDetailVOSMap.put(curOrganCatId,columnDetailVO);
                }

            }

        }

        Collection<MonitorColumnDetailEO> eoMapCollection = columnDetailEOSMap.values();
        List<MonitorColumnDetailEO> columnDetailEOS = new ArrayList<MonitorColumnDetailEO>(eoMapCollection);
        saveEntities(columnDetailEOS);

        Collection<ColumnDetailVO> voMapCollection = columnDetailVOSMap.values();
        final List<ColumnDetailVO> columnDetailVOS = new ArrayList<ColumnDetailVO>(voMapCollection);

        if(reportId!=null&&reportId!=0){
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        //将信息推到到云平台
                        monitorColumnDetailWebClient.saveColumnDetails(columnDetailVOS);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }


        if(reportId!=null&&reportId!=0) {
            monitorTaskManageService.updateStatus(monitorId,"siteUpdateStatus",2);
        } else {
            monitorCustomIndexManageService.updateStatus(monitorId,2);
        }
    }

    /**
     * 判断实体是否含有某个字段
     * @param clz
     * @param fieldName
     * @return
     */
    public static boolean existsField(Class clz,String fieldName){
        try{
            return clz.getDeclaredField(fieldName)!=null;
        }
        catch(Exception e){
        }
        if(clz!=Object.class){
            return existsField(clz.getSuperclass(),fieldName);
        }
        return false;
    }

    /**
     * 监测基本栏目信息
     *
     * @param siteId
     * @param monitorId
     * @param reportId
     */
    @Override
    public void monitorBaseColumn(Long siteId, Long monitorId, Long reportId) {
        handleColumnDatasByType(siteId, monitorId, reportId, MonitoredColumnConfigEO.TypeCode.columnType_update.toString());//应更新栏目
        handleColumnDatasByType(siteId, monitorId, reportId, MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString());//动态、要闻类栏目
        handleColumnDatasByType(siteId, monitorId, reportId, MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString());////通知公告、政策文件类栏目
        handleColumnDatasByType(siteId, monitorId, reportId, MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString());//人事、规划计划类栏目
        handleEmptyColumns(siteId, monitorId, reportId);//空白栏目

        if (null != reportId) {
            monitorTaskManageService.updateStatus(monitorId, "columnUpdateStatus", 2);
        } else {
            monitorCustomIndexManageService.updateStatus(monitorId, 2);
        }
    }


    /**
     * 综合评分项 基本信息更新情况 自定义监测
     *
     * @param siteId
     * @param monitorId
     * @param reportId
     */
    @Override
    public void monitorCustomBaseColumn(Long siteId, Long monitorId, Long reportId) {
        monitorIndexColumn(siteId, monitorId, reportId);//监测首页栏目

        handleColumnDatasByType(siteId, monitorId, reportId, MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString());//动态、要闻类栏目
        handleColumnDatasByType(siteId, monitorId, reportId, MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString());////通知公告、政策文件类栏目
        handleColumnDatasByType(siteId, monitorId, reportId, MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString());//人事、规划计划类栏目

    }


    /**
     * 根据栏目类型获取绑定的栏目id
     *
     * @param code
     * @return
     */
    private String getColumnByCode(String code, Long siteId) {
        String columnIds = "";
        List<ColumnMgrEO> columnMgrEOS = monitoredColumnConfigService.getColumnByCode(code, siteId);  //根据columnType获取绑定的栏目
        if(columnMgrEOS!=null&&columnMgrEOS.size()>0){
            for (ColumnMgrEO columnMgrEO : columnMgrEOS) {//将columnid拼成字符串
                if (!AppUtil.isEmpty(columnIds)) {
                    columnIds += "," + columnMgrEO.getIndicatorId();
                } else {
                    columnIds += columnMgrEO.getIndicatorId();
                }
            }
        }
        return columnIds;
    }

    /**
     * 根据typeCode查询绑定的信息公开目录
     *
     * @param code
     * @return
     */
    private String getPublicCatsByCode(String code, Long siteId) {
        String organCatIds = monitoredColumnConfigService.getPublicCatsByCode(code, siteId);  //根据columnType获取绑定的栏目
        return organCatIds;
    }


    /**
     * 获取空白栏目列表
     *
     * @return
     */
    public List<Long> getEmptyColumnList(Long siteId) {
        List<ColumnMgrEO> columnMgrEOs = columnConfigService.getAllColumnTree(siteId, null);

        //获取非空白新闻栏目列表
        List<Long> columnIds = monitorColumnDetailDao.getUnEmptyColumnList(siteId);

        List<Long> emptyColumnList = new ArrayList<Long>();

        for (int i = 0; i < columnMgrEOs.size(); i++) {
            if ((BaseContentEO.TypeCode.articleNews.toString().equals(columnMgrEOs.get(i).getColumnTypeCode())
                    || BaseContentEO.TypeCode.pictureNews.toString().equals(columnMgrEOs.get(i).getColumnTypeCode()))
                    && columnMgrEOs.get(i).getIsParent() == 0) {

                //空白栏目
                if (!columnIds.contains(columnMgrEOs.get(i).getIndicatorId())) {
                    emptyColumnList.add(columnMgrEOs.get(i).getIndicatorId());
                }
            }
        }
        return emptyColumnList;
    }




    /**
     * 综合评分项目-首页栏目
     * @return
     */
    @Override
    public IndexColumnStatisVO loadIndexColumnStatis(Long monitorId, Long siteId) {
        if(null == monitorId) {
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteId,null);
            monitorId = task.getId();
        }
        List<MonitorColumnDetailEO> indexColumns = monitorColumnDetailDao.getList(monitorId
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);

        //获取监测配置信息
        Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_index.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString()
                ,LoginPersonUtil.getSiteId());

        Integer updateCounts = 10;//默认设置
        Integer num = 5;//默认设置
        if(bc!=null){
            Long limitNum = getLong(bc, "limitNum");
            Long num1 = getLong(bc, "num");
            updateCounts = limitNum==null?10:limitNum.intValue();
            num = num1==null?5:num1.intValue();
        }

        Integer score = 0;
        Integer counts = 0;
        if(indexColumns!=null&&indexColumns.size()>0){
            for(MonitorColumnDetailEO eo:indexColumns){
                if(eo==null||eo.getUpdateCount()==null||eo.getUpdateCount().intValue()==0){

                }else{
                    counts = counts + eo.getUpdateCount().intValue();
                }
            }
            if(counts<updateCounts){
                score = num;
            }
        }else{
            score = num;
        }

        IndexColumnStatisVO statisVO = new IndexColumnStatisVO();
        statisVO.setScore(score+0l);
        statisVO.setCount(counts+0l);

        //更新监测报告子项结果
        monitorSiteStatisService.updateIndexInfoUpdate(siteId,monitorId,score+"");

        //查询上次监测报告
        MonitorTaskManageEO secondLatestReport = monitorTaskManageService.getLatestTask(siteId,monitorId);
        if(null == secondLatestReport) {
            statisVO.setLinkRelationStatus(0);
        }else{
            //上次监测首页未更新数据栏目
            List<MonitorColumnDetailEO> lastTimeColumns = monitorColumnDetailDao.getList(secondLatestReport.getId()
                    ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);
            Long lastCounts = 0l;
            if(null != lastTimeColumns && lastTimeColumns.size() > 0) {
                for(MonitorColumnDetailEO eo:indexColumns){
                    if(eo==null||eo.getUpdateCount()==null||eo.getUpdateCount().intValue()==0){

                    }else{
                        lastCounts = lastCounts + eo.getUpdateCount().intValue();
                    }
                }
            }
            if(counts==0&&lastCounts==0){
                statisVO.setLinkRelationStatus(0);
            }else if(counts!=0&&lastCounts==0){
                statisVO.setLinkRelationStatus(2);
            }else if(counts==0&&lastCounts!=0){
                statisVO.setLinkRelationStatus(3);
            }else{
                statisVO.setLinkRelationStatus(4);
                double hb = (double)(counts - lastCounts)*100/lastCounts;
                statisVO.setLinkRelationRate(getDoubleValue(hb,2));
            }
        }

        return statisVO;
    }

    /**
     * 综合评分项目-基本信息
     * @return
     */
    @Override
    public ColumnBaseInfoStatisVO loadColumnBaseInfoStatis(Long monitorId, Long siteId) {
        if(null == monitorId) {
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteId,null);
            monitorId = task.getId();
        }
        //查询上次监测报告
        MonitorTaskManageEO secondLatestReport = monitorTaskManageService.getLatestTask(siteId,monitorId);
        Long dtywCounts = monitorColumnDetailDao.getCount(monitorId
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString()},null,0L);
        Long dtywLastCounts = monitorColumnDetailDao.getCount(secondLatestReport==null?0:secondLatestReport.getId()
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString()},null,0L);

        Long tzzcCounts = monitorColumnDetailDao.getCount(monitorId
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0L);
        Long tzzcLastCounts = monitorColumnDetailDao.getCount(secondLatestReport==null?0:secondLatestReport.getId()
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0L);

        Long rsghCounts = monitorColumnDetailDao.getCount(monitorId
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString()},null,0L);
        Long rsghLastCounts = monitorColumnDetailDao.getCount(secondLatestReport==null?0:secondLatestReport.getId()
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString()},null,0L);

        Long dtywScore = 0l;
        Long tzzcScore = 0l;
        Long rsghScore = 0l;
        Long dtywLastScore = 0l;
        Long tzzcLastScore = 0l;
        Long rsghLastScore = 0l;
        if(dtywCounts!=null){
            //获取动态要闻监测配置信息
            Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString()
                    ,LoginPersonUtil.getSiteId());

            Integer num = 3;//默认设置
            if(bc!=null){
                Long num1 = getLong(bc, "num");
                num = num1==null?3:num1.intValue();
            }
            dtywScore = dtywCounts*num;
            dtywLastScore = dtywLastCounts*num;
        }
        if(tzzcCounts!=null){
            //获取通知政策监测配置信息
            Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString()
                    ,LoginPersonUtil.getSiteId());

            Integer num = 4;//默认设置
            if(bc!=null){
                Long num1 = getLong(bc, "num");
                num = num1==null?4:num1.intValue();
            }
            tzzcScore = tzzcCounts*num;
            tzzcLastScore = tzzcLastCounts*num;
        }
        if(rsghCounts!=null){
            //获取人事规划监测配置信息
            Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_RSGH.toString(),MonitoredVetoConfigEO.BaseCode.scop.toString()
                    ,LoginPersonUtil.getSiteId());

            Integer num = 5;//默认设置
            if(bc!=null){
                Long num1 = getLong(bc, "num");
                num = num1==null?5:num1.intValue();
            }
            rsghScore = rsghCounts*num;
            rsghLastScore = rsghLastCounts*num;
        }


        ColumnBaseInfoStatisVO statisVO = new ColumnBaseInfoStatisVO();
        statisVO.setDtywCount(dtywCounts);
        statisVO.setDtywScore(dtywScore);
        statisVO.setTzzcCount(tzzcCounts);
        statisVO.setTzzcScore(tzzcScore);
        statisVO.setRsghCount(rsghCounts);
        statisVO.setRsghScore(rsghScore);
        statisVO.setTotalCount(dtywCounts + tzzcCounts + rsghCounts);
        statisVO.setTotalScore(dtywScore + tzzcScore + rsghScore);

        //更新监测报告子项结果
        monitorSiteStatisService.updateInfoUpdate(siteId,monitorId,statisVO.getTotalScore()+"");

        if(dtywCounts==0&&dtywLastCounts==0){
            statisVO.setDtywLinkRelationStatus(0);
        }else if(dtywCounts!=0&&dtywLastCounts==0){
            statisVO.setDtywLinkRelationStatus(2);
        }else{
            statisVO.setDtywLinkRelationStatus(4);
            Double hb = (double)(dtywCounts - dtywLastCounts)*100/dtywLastCounts;
            if(hb.intValue()==0){
                statisVO.setDtywLinkRelationStatus(0);
            }
            statisVO.setDtywLinkRelationRate(getDoubleValue(hb,2));
        }

        if(tzzcCounts==0&&tzzcLastCounts==0){
            statisVO.setTzzcLinkRelationStatus(0);
        }else if(tzzcCounts!=0&&tzzcLastCounts==0){
            statisVO.setTzzcLinkRelationStatus(2);
        }else{
            statisVO.setTzzcLinkRelationStatus(4);
            Double hb = (double)(tzzcCounts - tzzcLastCounts)*100/tzzcLastCounts;
            if(hb.intValue()==0){
                statisVO.setTzzcLinkRelationStatus(0);
            }
            statisVO.setTzzcLinkRelationRate(getDoubleValue(hb,2));
        }

        if(rsghCounts==0&&rsghLastCounts==0){
            statisVO.setRsghLinkRelationStatus(0);
        }else if(rsghCounts!=0&&rsghLastCounts==0){
            statisVO.setRsghLinkRelationStatus(2);
        }else{
            statisVO.setRsghLinkRelationStatus(4);
            Double hb = (double)(rsghCounts - rsghLastCounts)*100/rsghLastCounts;
            if(hb.intValue()==0){
                statisVO.setRsghLinkRelationStatus(0);
            }
            statisVO.setRsghLinkRelationRate(getDoubleValue(hb,2));
        }


//        MonitorSiteStatisEO lasteStatisEO = null;
//        if(secondLatestReport!=null){
//            lasteStatisEO =  monitorSiteStatisService.getSiteStatis(siteId,secondLatestReport.getId());
//        }

        //查询上次监测报告
        if(null == secondLatestReport) {
            statisVO.setCpiStatus(0);
        }else{
            Long totalScore = statisVO.getTotalScore();
            Long lastTotalScore = dtywLastScore + tzzcLastScore + rsghLastScore;
            if(totalScore==0&&lastTotalScore==0){
                statisVO.setCpiStatus(0);
            }else if(totalScore!=0&&lastTotalScore==0){
                statisVO.setCpiStatus(2);
            }else if(totalScore==0&&lastTotalScore!=0){
                statisVO.setCpiStatus(3);
            }else{
                statisVO.setCpiStatus(4);
                Double cpi = (double) totalScore * 100/lastTotalScore;
                if(cpi.intValue()==100){
                    statisVO.setCpiStatus(0);
                }
                statisVO.setCpi(getDoubleValue(cpi,2));
            }
        }
        return statisVO;
    }


    /**
     * 单项否决-首页栏目不更新
     * @return
     */
    @Override
    public IndexNotUpdateStatisVO loadIndexNotUpdateStatis(Long monitorId, Long siteId) {
        if(null == monitorId) {
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteId,null);
            monitorId = task.getId();
        }
        MonitorTaskManageEO secondLatestReport = monitorTaskManageService.getLatestTask(siteId,monitorId);
        List<MonitorColumnDetailEO> indexColumns = monitorColumnDetailDao.getList(monitorId
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);
        Integer isOk = 1;
        Integer updateCounts = 0;
        Integer num = 1;//默认配置
        //查询配置
        Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.siteUpdate.toString(),
                MonitoredColumnConfigEO.TypeCode.columnType_index.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                ,LoginPersonUtil.getSiteId());
        if(bc!=null){
            Long num1 = getLong(bc, "num");
            num = num1==null?1:num1.intValue();
        }
        Integer indexNotUpdateColumn = 0;
        if(indexColumns!=null&&indexColumns.size()>0){
            for(MonitorColumnDetailEO eo:indexColumns){
                if(eo==null||eo.getUpdateCount()==null||eo.getUpdateCount().intValue()==0){
                    indexNotUpdateColumn = indexNotUpdateColumn + 1;
                }else{
                    updateCounts = updateCounts + eo.getUpdateCount().intValue();
                }
            }
            if(updateCounts<num){
                isOk = 0;
            }
        }else{
            isOk = 0;
        }

        IndexNotUpdateStatisVO statisVO = new IndexNotUpdateStatisVO();
        statisVO.setIsOk(isOk);
        statisVO.setCount(updateCounts+0L);

        //更新监测报告子项结果
        monitorSiteStatisService.updateSiteUpdate(siteId,monitorId,statisVO.getIsOk());
        MonitorSiteStatisEO lasteStatisEO = null;
        if(secondLatestReport!=null){
            lasteStatisEO =  monitorSiteStatisService.getSiteStatis(siteId,secondLatestReport.getId());
        }

        //查询上次监测报告
        if(null == lasteStatisEO) {
            statisVO.setLinkRelationStatus(0);//持平
            statisVO.setLinkRelationRate(new Double(0));
        }else{
            Integer isOk_ = lasteStatisEO.getSiteUpdate()==null?0:lasteStatisEO.getSiteUpdate();
            if(isOk==0&&isOk_==0){//本期单项否决且上期单项否决
                statisVO.setLinkRelationStatus(1);
            }else if(isOk==1&&isOk_==0){//本期未单项否决且上期单项否决
                statisVO.setLinkRelationStatus(3);
            }else if(isOk==0&&isOk_==1){//本期单项否决且上期未单项否决
                statisVO.setLinkRelationStatus(2);
            }else if(isOk==1&&isOk_==1){
                statisVO.setLinkRelationStatus(4);
//                if(indexNotUpdateColumn==0){
//                    statisVO.setLinkRelationRate(new Double(0));
//                }else{
                    //上次监测首页未更新数据栏目数
                    List<MonitorColumnDetailEO> lastTimeList = monitorColumnDetailDao.getList(secondLatestReport.getId()
                            ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_index.toString()},null,null);
                    Integer lastUpdateCounts = 0;
                    if(lastTimeList!=null&&lastTimeList.size()>0){
                        for(MonitorColumnDetailEO eo:lastTimeList){
                            if(eo==null||eo.getUpdateCount()==null||eo.getUpdateCount().intValue()==0){

                            }else{
                                lastUpdateCounts = lastUpdateCounts + eo.getUpdateCount().intValue();
                            }
                        }
                    }

                    if(null == lastUpdateCounts || lastUpdateCounts == 0) {
                        if(updateCounts.intValue()>0){//本次不为0
                            statisVO.setLinkRelationStatus(2);
                        }else{//本次跟上次未更新栏目数都是0
                            statisVO.setLinkRelationStatus(0);
                        }
                    } else {
                        Double hb = (double)(updateCounts - lastUpdateCounts)*100/lastUpdateCounts;
                        if(hb.intValue()==0){
                            statisVO.setLinkRelationStatus(0);//持平
                        }
                        statisVO.setLinkRelationRate(getDoubleValue(hb,2));
                    }
//                }
            }

        }
        return statisVO;
    }

    /**
     * 单项否决-栏目不更新
     * @return
     */
    @Override
    public ColumnNotUpdateStatisVO loadColumnNotUpdateStatis(Long monitorId, Long siteId) {
        MonitorTaskManageEO secondLatestReport = monitorTaskManageService.getLatestTask(siteId,monitorId);
        Long dtywCounts = monitorColumnDetailDao.getCount(monitorId
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0L);
        Long dtywLastCounts = monitorColumnDetailDao.getCount(secondLatestReport==null?0:secondLatestReport.getId()
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
                        MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0L);
        Integer isOk_dtyw = 1;
        Integer isOk_dtyw_last = 1;

//        Long tzzcCounts = monitorColumnDetailDao.getCount(monitorId
//                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0L);
//        Long tzzcLastCounts = monitorColumnDetailDao.getCount(secondLatestReport==null?0:secondLatestReport.getId()
//                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString()},null,0L);
//        Integer isOk_tzzc = 1;
//        Integer isOk_tzzc_last = 1;

        Long updateCounts = monitorColumnDetailDao.getCount(monitorId
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_update.toString()},null,0L);
        Long updateLastCounts = monitorColumnDetailDao.getCount(secondLatestReport==null?0:secondLatestReport.getId()
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_update.toString()},null,0L);
        Integer isOk_update = 1;
        Integer isOk_update_last = 1;

        Long blankCounts = monitorColumnDetailDao.getCount(monitorId
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString()},null,0L);
        Long blankLastCounts = monitorColumnDetailDao.getCount(secondLatestReport==null?0:secondLatestReport.getId()
                ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString()},null,0L);
        Integer isOk_blank = 1;
        Integer isOk_blank_last = 1;

        Integer isOk = 1;
        if(dtywCounts!=null&&dtywCounts>0){
            Integer num = 5;//默认配置
            //查询配置
            Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                    ,LoginPersonUtil.getSiteId());
            if(bc!=null){
                Long num1 = getLong(bc, "num");
                num = num1==null?5:num1.intValue();
            }
            if(dtywCounts>=num){
                isOk = 0;
                isOk_dtyw = 0;
            }

            if(dtywLastCounts>=num){
                isOk_dtyw_last = 0;
            }
        }

        //单项否决 通知政策动态要闻跟类的合并
//        if(tzzcCounts!=null&&tzzcCounts>0){
//            Integer num = 5;//默认配置
//            //查询配置
//            Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),
//                    MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
//                    ,LoginPersonUtil.getSiteId());
//            if(bc!=null){
//                Long num1 = getLong(bc, "num");
//                num = num1==null?5:num1.intValue();
//            }
//            if(tzzcCounts>=num){
//                isOk = 0;
//                isOk_tzzc = 0;
//            }
//
//            if(tzzcLastCounts>=num){
//                isOk_tzzc_last = 0;
//            }
//        }


        if(updateCounts!=null&&updateCounts>0){
            Integer num = 10;//默认配置
            //查询配置
            Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                    ,LoginPersonUtil.getSiteId());
            if(bc!=null){
                Long num1 = getLong(bc, "num");
                num = num1==null?10:num1.intValue();
            }
            if(updateCounts>=num){
                isOk = 0;
                isOk_update = 0;
            }

            if(updateLastCounts>=num){
                isOk_update_last = 0;
            }
        }

        if(blankCounts!=null&&blankCounts>0){
            Integer num = 5;//默认配置
            //查询配置
            Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),
                    MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString(),MonitoredVetoConfigEO.BaseCode.vote.toString()
                    ,LoginPersonUtil.getSiteId());
            if(bc!=null){
                Long num1 = getLong(bc, "num");
                num = num1==null?5:num1.intValue();
            }
            if(blankCounts>=num){
                isOk = 0;
                isOk_blank = 0;
            }

            if(blankLastCounts>=num){
                isOk_blank_last = 0;
            }
        }
        ColumnNotUpdateStatisVO statisVO = new ColumnNotUpdateStatisVO();
        statisVO.setIsOk(isOk);
        statisVO.setDtywCounts(dtywCounts==null?0:dtywCounts);
//        statisVO.setTzzcCounts(tzzcCounts==null?0:tzzcCounts);
        statisVO.setUpdateCounts(updateCounts==null?0:updateCounts);
        statisVO.setBlankCounts(blankCounts==null?0:blankCounts);
        statisVO.setTotalCount(statisVO.getBlankCounts() + statisVO.getUpdateCounts()
                /*+ statisVO.getTzzcCounts()*/ + statisVO.getDtywCounts());

        //更新监测报告子项结果
        monitorSiteStatisService.updateColumnUpdate(siteId,monitorId,statisVO.getIsOk());
//        MonitorSiteStatisEO lasteStatisEO =  monitorSiteStatisService.getSiteStatis(siteId,secondLatestReport.getId());

        //查询上次监测报告

        if(null == secondLatestReport) {
            statisVO.setLinkRelationStatus(1);
            statisVO.setDtywLinkRelationStatus(1);
            statisVO.setTzzcLinkRelationStatus(1);
            statisVO.setUpdateLinkRelationStatus(1);
            statisVO.setBlankLinkRelationStatus(1);
        }else{
//            if(statisVO.getTotalCount()==0){
//                statisVO.setLinkRelationRate(new Double(0));
//            }else{
                if(isOk_dtyw==0&&isOk_dtyw_last==0){//本期单项否决且上期单项否决
                    statisVO.setDtywLinkRelationStatus(1);
                }else if(isOk_dtyw==1&&isOk_dtyw_last==0){//本期未单项否决且上期单项否决
                    statisVO.setDtywLinkRelationStatus(3);
                }else if(isOk_dtyw==0&&isOk_dtyw_last==1){//本期单项否决且上期未单项否决
                    statisVO.setDtywLinkRelationStatus(2);
                }else if(isOk_dtyw==1&&isOk_dtyw_last==1){
                    if(dtywLastCounts.intValue()==0){//上次未更新数量为0
                        if(dtywCounts.intValue()>0){//本次不为0
                            statisVO.setDtywLinkRelationStatus(2);
                        }else{//本次跟上次未更新栏目数都是0
                            statisVO.setDtywLinkRelationStatus(0);
                        }
                    }else{
                        Double hb = (double)(dtywCounts - dtywLastCounts)*100/dtywLastCounts;
                        statisVO.setDtywLinkRelationStatus(4);
                        if(hb.intValue()==0){
                            statisVO.setDtywLinkRelationStatus(0);//持平
                        }
                        statisVO.setDtywLinkRelationRate(getDoubleValue(hb,2));
                    }

                }

//                if(isOk_tzzc==0&&isOk_tzzc_last==0){//本期单项否决且上期单项否决
//                    statisVO.setTzzcLinkRelationStatus(1);
//                }else if(isOk_tzzc==1&&isOk_tzzc_last==0){//本期未单项否决且上期单项否决
//                    statisVO.setTzzcLinkRelationStatus(2);
//                }else if(isOk_tzzc==0&&isOk_tzzc_last==1){//本期单项否决且上期未单项否决
//                    statisVO.setTzzcLinkRelationStatus(3);
//                }else if(isOk_tzzc==1&&isOk_tzzc_last==1){
//                    if(tzzcLastCounts.intValue()==0){//上次未更新数量为0
//                        if(tzzcCounts.intValue()>0){//本次不为0
//                            statisVO.setTzzcLinkRelationStatus(2);
//                        }else{//本次跟上次未更新栏目数都是0
//                            statisVO.setTzzcLinkRelationStatus(0);
//                        }
//                    }else{
//                        Double hb = (double)(tzzcCounts - tzzcLastCounts)*100/tzzcLastCounts;
//                        statisVO.setTzzcLinkRelationStatus(4);
//                        if(hb.intValue()==0){
//                            statisVO.setTzzcLinkRelationStatus(0);//持平
//                        }
//                        statisVO.setTzzcLinkRelationRate(getDoubleValue(hb,2));
//                    }
//                }


                if(isOk_update==0&&isOk_update_last==0){//本期单项否决且上期单项否决
                    statisVO.setUpdateLinkRelationStatus(1);
                }else if(isOk_update==1&&isOk_update_last==0){//本期未单项否决且上期单项否决
                    statisVO.setUpdateLinkRelationStatus(3);
                }else if(isOk_update==0&&isOk_update_last==1){//本期单项否决且上期未单项否决
                    statisVO.setUpdateLinkRelationStatus(2);
                }else if(isOk_update==1&&isOk_update_last==1){
                    if(updateLastCounts.intValue()==0){//上次未更新数量为0
                        if(updateCounts.intValue()>0){//本次不为0
                            statisVO.setUpdateLinkRelationStatus(2);
                        }else{//本次跟上次未更新栏目数都是0
                            statisVO.setUpdateLinkRelationStatus(0);
                        }
                    }else{
                        Double hb = (double)(updateCounts - updateLastCounts)*100/updateLastCounts;
                        statisVO.setUpdateLinkRelationStatus(4);
                        if(hb.intValue()==0){
                            statisVO.setUpdateLinkRelationStatus(0);//持平
                        }
                        statisVO.setUpdateLinkRelationRate(getDoubleValue(hb,2));
                    }

                }

                if(isOk_blank==0&&isOk_blank_last==0){//本期单项否决且上期单项否决
                    statisVO.setBlankLinkRelationStatus(1);
                }else if(isOk_blank==1&&isOk_blank_last==0){//本期未单项否决且上期单项否决
                    statisVO.setBlankLinkRelationStatus(3);
                }else if(isOk_blank==0&&isOk_blank_last==1){//本期单项否决且上期未单项否决
                    statisVO.setBlankLinkRelationStatus(2);
                }else if(isOk_blank==1&&isOk_blank_last==1){

                    if(blankLastCounts.intValue()==0){//上次未更新数量为0
                        if(blankCounts.intValue()>0){//本次不为0
                            statisVO.setBlankLinkRelationStatus(2);
                        }else{//本次跟上次未更新栏目数都是0
                            statisVO.setBlankLinkRelationStatus(0);
                        }
                    }else{
                        Double hb = (double)(blankCounts - blankLastCounts)*100/blankLastCounts;
                        statisVO.setBlankLinkRelationStatus(4);
                        if(hb.intValue()==0){
                            statisVO.setBlankLinkRelationStatus(0);//持平
                        }
                        statisVO.setBlankLinkRelationRate(getDoubleValue(hb,2));
                    }

                }


//                //上次监测首页未更新数据栏目数
//                Long lastTime = monitorColumnDetailDao.getCount(secondLatestReport.getId()
//                        ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_DTYW.toString(),
//                                MonitoredColumnConfigEO.TypeCode.columnType_TZZC.toString(),
//                                MonitoredColumnConfigEO.TypeCode.columnType_update.toString(),
//                                MonitoredColumnConfigEO.TypeCode.columnType_BLANK.toString()},null,0l);
//                if(null == lastTime || lastTime == 0) {
//                    statisVO.setLinkRelationRate(new Double(100));
//                } else {
//                    double hb = (double)(statisVO.getTotalCount() - lastTime)*100/statisVO.getTotalCount();
//                    statisVO.setLinkRelationRate(getDoubleValue(hb,2));
//                }
//            }
        }
        return statisVO;
    }

    @Override
    public List<MonitorColumnDetailEO> getList(Long monitorId, String[] columnType,String infoType, Long updateCount) {
        return monitorColumnDetailDao.getList(monitorId,columnType,infoType,updateCount);
    }

    @Override
    public List<MonitorColumnDetailEO> getList(MonitorDetailQueryVO queryVO) {
        return monitorColumnDetailDao.getList(queryVO);
    }

    @Override
    public Long getCount(Long monitorId, String[] columnType,String infoType, Long updateCount) {
        return monitorColumnDetailDao.getCount(monitorId,columnType,infoType,updateCount);
    }


    /**
     * 获取规定时间内已经更新数据的首页栏目
     * @param columnIds
     * @param st
     * @param ed
     * @return
     */
    @Override
    public List<MonitorColumnDetailEO> getIndexUpdatedColumns(String columnIds,Date st,Date ed){
        return monitorColumnDetailDao.getIndexUpdatedColumns(columnIds,st,ed);
    }

    /**
     * 获取规定时间内已经更新数据的首页信息公开栏目
     * @param organCatId
     * @param st
     * @param ed
     * @return
     */
    @Override
    public List<MonitorColumnDetailEO> getIndexUpdatedPublics(String[] organCatId,Date st,Date ed){
        return monitorColumnDetailDao.getIndexUpdatedPublics(organCatId,st,ed);
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
    
}

