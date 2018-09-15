package cn.lonsun.monitor.job.timingjob.jobimpl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.internal.entity.ScheduleJobEO;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredColumnConfigService;
import cn.lonsun.monitor.internal.dao.IMonitorColumnDetailDao;
import cn.lonsun.monitor.internal.entity.MonitorColumnDetailEO;
import cn.lonsun.monitor.internal.service.IMonitorColumnDetailService;
import cn.lonsun.monitor.internal.vo.ColumnPublishVO;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomIndexManageEO;
import cn.lonsun.monitor.task.internal.entity.MonitorIndexManageEO;
import cn.lonsun.monitor.task.internal.entity.vo.JobParamVO;
import cn.lonsun.monitor.task.internal.service.IMonitorCustomIndexManageService;
import cn.lonsun.monitor.task.internal.service.IMonitorIndexManageService;
import cn.lonsun.monitor.task.internal.service.IMonitorTaskManageService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.supervise.util.CronUtil;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.PublicCatalogUtil;
import cn.lonsun.webservice.monitor.client.IMonitorColumnDetailWebClient;
import cn.lonsun.webservice.monitor.client.vo.ColumnDetailVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 首页栏目监测
 * <p>
 * Created by liuk on 2017-9-27.
 */
public class SylmUpdateTaskImpl extends ISchedulerService {
    private IMonitorColumnDetailService monitorColumnDetailService = SpringContextHolder.getBean(IMonitorColumnDetailService.class);
    private IMonitoredColumnConfigService monitoredColumnConfigService = SpringContextHolder.getBean(IMonitoredColumnConfigService.class);
    private IMonitorTaskManageService taskManageService = SpringContextHolder.getBean(IMonitorTaskManageService.class);
    private IMonitorColumnDetailWebClient monitorColumnDetailWebClient = SpringContextHolder.getBean(IMonitorColumnDetailWebClient.class);
    private IMonitorCustomIndexManageService monitorCustomIndexManageService = SpringContextHolder.getBean(IMonitorCustomIndexManageService.class);
    private IMonitorIndexManageService monitorIndexManageService = SpringContextHolder.getBean(IMonitorIndexManageService.class);
    private IScheduleJobService scheduleJobService = SpringContextHolder.getBean(IScheduleJobService.class);

    @Override
    public void execute(String json) {
        //获取监测id
        JSONObject jsonObject= JSONObject.fromObject(json);
        JobParamVO param = (JobParamVO)JSONObject.toBean(jsonObject, JobParamVO.class);
        Long monitorId = param.getTaskId();
        Long reportId = param.getReportId();//数据推送到云平台用
        Long siteId = param.getSiteId();
        String cron = param.getCron();
        Long overTime = param.getOverTime();
        Integer dayOffset = param.getTimeout()==0?14:param.getTimeout();//此处获取的是更新周期
        String updateCycleStr = "";
        if(dayOffset!=null&&dayOffset==14){
            updateCycleStr = "2周";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String publishDate = sdf.format(calendar.getTime());

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
        if(AppUtil.isEmpty(columnIds)){
            return;
        }

        List<MonitorColumnDetailEO> monitorColumnDetailEOS = null;//查询前一天的内容
        monitorColumnDetailEOS = monitorColumnDetailService.getIndexUpdatedColumns(columnIds,DateUtil.getDayDate(new Date(),-1),
                DateUtil.getDayDate(new Date()));
        Map<String,MonitorColumnDetailEO> monitorColumnDetailEOMap = new HashMap<String, MonitorColumnDetailEO>();
        for(MonitorColumnDetailEO eo:monitorColumnDetailEOS){
            monitorColumnDetailEOMap.put(eo.getColumnId(),eo);
        }

        List<ColumnDetailVO> columnDetailVOS = new ArrayList<ColumnDetailVO>();
        //保存未更新数据的栏目导监测字表中
        for(int i=0;i<columnId.length;i++){
            String curColumnId = columnId[i];
            Map<String,Object> param1 = new HashMap<String,Object>();
            param1.put("monitorId",monitorId);
            param1.put("columnType",MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
            param1.put("columnId",curColumnId);
            MonitorColumnDetailEO oldEO = monitorColumnDetailService.getEntity(MonitorColumnDetailEO.class,param1);
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
                columnPublishVO.setUpdateCount(eo.getUpdateCount());
                columnPublishVO.setPublishDate(publishDate);
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
            String columnUrl = siteMgrEO.getUri()+"/"+curColumnId+".html";//栏目页访问地址

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
                monitorColumnDetailService.saveEntity(eo);
            }else{//更新原有栏目信息
                oldEO.setUpdateCount(updateCount);
                oldEO.setLastPublishDate(lastPublishDate);
                oldEO.setUnPublishDays(unPublishDays);
                oldEO.setUpdateCycleStr(updateCycleStr);
                oldEO.setColumnUrl(columnUrl);
                oldEO.setPublishDetail(publishDetail);
                monitorColumnDetailService.updateEntity(oldEO);
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
                columnDetailVOS.add(columnDetailVO);
            }
        }

        String organCatIds = monitoredColumnConfigService.getPublicCatsByCode(MonitoredColumnConfigEO.TypeCode.columnType_index.toString(),siteId);

        String[] organCatId = new String[]{};
        if(!AppUtil.isEmpty(organCatIds)){
            organCatId = StringUtils.split(organCatIds, ",");
        }

        List<MonitorColumnDetailEO> publicDetailEOS = monitorColumnDetailService.getIndexUpdatedPublics(organCatId, DateUtil.getDayDate(new Date(),-1),
                DateUtil.getDayDate(new Date()));//查询出前一天更新数据的栏目
        Map<String, MonitorColumnDetailEO> publicDetailEOMap = new HashMap<String, MonitorColumnDetailEO>();
        for (MonitorColumnDetailEO eo : publicDetailEOS) {
            publicDetailEOMap.put(eo.getColumnId(), eo);
        }
        //保存未更新数据的栏目导监测字表中
        for (int i = 0; i < organCatId.length; i++) {
            String curOrganCatId = organCatId[i];
            Map<String,Object> param1 = new HashMap<String,Object>();
            param1.put("monitorId",monitorId);
            param1.put("columnType",MonitoredColumnConfigEO.TypeCode.columnType_index.toString());
            param1.put("columnId",curOrganCatId);
            MonitorColumnDetailEO oldEO = monitorColumnDetailService.getEntity(MonitorColumnDetailEO.class,param1);
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
                monitorColumnDetailService.saveEntity(eo);
            }else{//更新原有栏目信息
                oldEO.setUpdateCount(updateCount);
                oldEO.setUnPublishDays(unPublishDays);
                oldEO.setUpdateCycleStr(updateCycleStr);
                oldEO.setLastPublishDate(lastPublishDate);
                oldEO.setPublishDetail(publishDetail);
                oldEO.setColumnUrl(columnUrl);
                monitorColumnDetailService.updateEntity(oldEO);
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
                columnDetailVOS.add(columnDetailVO);
            }

        }


        try{
            if(reportId!=null&&reportId!=0){
                //将信息推到到云平台
                monitorColumnDetailWebClient.saveColumnDetails(columnDetailVOS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(reportId!=null&&reportId!=0) {
            //需要调用修改子任务进行状态的方法,在此调用
            Date next = CronUtil.getNextCronTime(cron);
            //完成检测任务指标则停止检测任务，删除定时任务
            if(null == next || (null != next && next.getTime() > overTime)) {
                taskManageService.updateStatus(monitorId,"siteUpdateStatus",2);
                taskManageService.updateStatus(monitorId, "infoUpdateStatus", 2);
                MonitorIndexManageEO index = monitorIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteUpdate.toString(),param.getSiteId());
                if(null != index) {
                    scheduleJobService.delete(ScheduleJobEO.class,index.getScheduleId());
                }
            }
        } else {
            monitorCustomIndexManageService.updateStatus(param.getTaskId(),2);
            MonitorCustomIndexManageEO index = monitorCustomIndexManageService.getIndex(MonitoredVetoConfigEO.CodeType.siteUpdate.toString(),param.getSiteId());
            if(null != index) {
                scheduleJobService.delete(ScheduleJobEO.class,index.getScheduleId());
            }
        }

    }
}
