package cn.lonsun.monitor.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredColumnConfigService;
import cn.lonsun.monitor.config.internal.service.IMonitoredVetoConfigService;
import cn.lonsun.monitor.internal.dao.IMonitorInteractDetailDao;
import cn.lonsun.monitor.internal.entity.MonitorInteractDetailEO;
import cn.lonsun.monitor.internal.service.IMonitorInteractDetailService;
import cn.lonsun.monitor.internal.vo.BadInteractStatisVO;
import cn.lonsun.monitor.internal.vo.InteractInfoStatisVO;
import cn.lonsun.monitor.internal.vo.MonitorDetailQueryVO;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;
import cn.lonsun.monitor.task.internal.service.IMonitorCustomIndexManageService;
import cn.lonsun.monitor.task.internal.service.IMonitorSiteStatisService;
import cn.lonsun.monitor.task.internal.service.IMonitorTaskManageService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.webservice.monitor.client.IMonitorInteractDetailWebClient;
import cn.lonsun.webservice.monitor.client.vo.InteractDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日常监测互动更新详细service实现<br/>
 *
 */
@Service("monitorInteractDetailService")
public class MonitorInteractDetailServiceImpl extends BaseService<MonitorInteractDetailEO> implements IMonitorInteractDetailService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @DbInject("monitorInteractDetail")
    private IMonitorInteractDetailDao monitorInteractDetailDao;

    @Autowired
    private IMonitoredColumnConfigService monitoredColumnConfigService;

    @Resource
    private IMonitorTaskManageService monitorTaskManageService;

    @Autowired
    private IMonitorInteractDetailWebClient monitorInteractDetailWebClient;

    @Resource
    private IMonitorCustomIndexManageService monitorCustomIndexManageService;
    @Autowired
    private IMonitoredVetoConfigService monitoredVetoConfigService;

    @Resource
    private IMonitorSiteStatisService monitorSiteStatisService;

    @Autowired
    private IMessageBoardForwardService forwardService;

    @Autowired
    private IOrganService organService;



    @Override
    public Pagination getPage(MonitorDetailQueryVO queryVO) {
        Pagination page = monitorInteractDetailDao.getPage(queryVO);
        List<MonitorInteractDetailEO> dataList = (List<MonitorInteractDetailEO>)page.getData();
        for(MonitorInteractDetailEO eo:dataList){
            Long monitorId = eo.getMonitorId()==null?0:eo.getMonitorId();
            MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,monitorId);
            Long siteId = task==null?LoginPersonUtil.getSiteId():task.getSiteId();
            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            eo.setColumnUrl(siteMgrEO.getUri()+"/content/column/"+eo.getColumnId());
            eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(), siteId));
        }
        return page;
    }

    @Override
    public Pagination getUnReplyPage(Long pageIndex, Integer pageSize, String contentIds) {
        Pagination page = monitorInteractDetailDao.getUnReplyPage(pageIndex,pageSize,contentIds);
        List<?> data = page.getData();
        if(data!=null&&data.size()>0){
            for(Object obj : data){
                MessageBoardEditVO vo = (MessageBoardEditVO)obj;
                try{
                    Long messageBoardId = vo.getId();
                    List<MessageBoardForwardVO> forwardVOList = forwardService.getAllForwardByMessageBoardId(messageBoardId);
                    String recUnitNames="";
                    if (forwardVOList != null && forwardVOList.size() > 0) {
                        for(MessageBoardForwardVO forwardVO:forwardVOList){
                            if (!StringUtils.isEmpty(forwardVO.getReceiveUnitName())) {
                                recUnitNames+=forwardVO.getReceiveUnitName()+",";
                            } else {
                                OrganEO organEO = organService.getEntity(OrganEO.class, forwardVO.getReceiveOrganId());
                                if (organEO != null) {
                                    recUnitNames+=organEO.getName()+",";
                                }
                            }
                        }
                    }
                    if(!StringUtils.isEmpty(recUnitNames)){
                        recUnitNames=recUnitNames.substring(0,recUnitNames.length()-1);
                    }
                    vo.setReceiveUnitName(recUnitNames);
                    vo.setColumnName(ColumnUtil.getColumnName(vo.getColumnId(),vo.getSiteId()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return page;
    }

    @Override
    public List<MonitorInteractDetailEO> getList(MonitorDetailQueryVO queryVO) {
        List<MonitorInteractDetailEO> dataList = monitorInteractDetailDao.getList(queryVO);
        for(MonitorInteractDetailEO eo:dataList){
            Long monitorId = eo.getMonitorId()==null?0:eo.getMonitorId();
            MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,monitorId);
            Long siteId = task==null?LoginPersonUtil.getSiteId():task.getSiteId();
            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            eo.setColumnUrl(siteMgrEO.getUri()+"/content/column/"+eo.getColumnId());
            eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(), siteId));
        }
        return dataList;
    }

    /**
     * 处理政务咨询类(留言)栏目数据
     */
    @Override
    public void handleZWZXDatas(Long siteId,Long monitorId,Long reportId, String columnType) {
        String columnIds = getColumnByCode(MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString(),siteId);
        String[] columnId = StringUtils.split(columnIds, ",");
        Integer updateCycle = 365;//监测周期默认配置
        Integer unreplyCount = 90;//回复周期默认配置

        //查询配置
        Map<String, Object> bc = monitoredVetoConfigService.getDataByCode(MonitoredVetoConfigEO.CodeType.reply.toString(),siteId);
        if(bc!=null){

            Integer num = !bc.containsKey("num") || bc.get("num")==null?3:Integer.valueOf(String.valueOf(bc.get("num")));
            unreplyCount = num * 30; //更新周期
            if(!AppUtil.isEmpty(String.valueOf(bc.get("monitoredNum")))){
                Long monitoredNum = Long.valueOf(String.valueOf(bc.get("monitoredNum")));
                updateCycle = getUpdateCycle(monitoredNum, String.valueOf(bc.get("monitoredTimeType"))).intValue();//回复周期
            }
        }
        List<MonitorInteractDetailEO> monitorInteractDetailEOList = monitorInteractDetailDao.getZWZXInfo(columnIds,updateCycle,unreplyCount);
        List<String> columnList = new ArrayList<String>();
        List<InteractDetailVO> interactDetailVOS = new ArrayList<InteractDetailVO>();
        for(MonitorInteractDetailEO eo:monitorInteractDetailEOList){//设置栏目类型
            eo.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString());
            eo.setMonitorId(monitorId);
            columnList.add(eo.getColumnId().toString());

            if(reportId!=null&&reportId!=0){
                SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
                String columnUrl = siteMgrEO.getUri()+"/content/column/"+eo.getColumnId();//栏目页访问地址
                //将信息推到到云平台
                InteractDetailVO interactDetailVO = new InteractDetailVO();
                interactDetailVO.setColumnId(eo.getColumnId());
                interactDetailVO.setColumnUrl(columnUrl);
                interactDetailVO.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(),siteId));
                interactDetailVO.setColumnType(columnType);
                interactDetailVO.setUpdateCount(eo.getUpdateCount());
                interactDetailVO.setUnreplyCount(eo.getUnreplyCount());
                interactDetailVO.setReportId(reportId);//设置监测id
                interactDetailVOS.add(interactDetailVO);
            }
        }


        //处理一年内未更新数据的栏目
        for(int i=0;i<columnId.length;i++){
            if(!columnList.contains(columnId[i])){
                MonitorInteractDetailEO eo = new MonitorInteractDetailEO();
                eo.setColumnId(Long.parseLong(columnId[i]));
                eo.setUpdateCount(0L);
                eo.setUnreplyCount(0L);
                eo.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString());
                eo.setMonitorId(monitorId);
                monitorInteractDetailEOList.add(eo);//加入到需要保存的列表中
                columnList.add(columnId[i]);

                if(reportId!=null&&reportId!=0){
                    SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
                    String columnUrl = siteMgrEO.getUri()+"/content/column/"+eo.getColumnId();//栏目页访问地址
                    //将信息推到到云平台
                    InteractDetailVO interactDetailVO = new InteractDetailVO();
                    interactDetailVO.setColumnId(Long.parseLong(columnId[i]));
                    interactDetailVO.setColumnUrl(columnUrl);
                    interactDetailVO.setColumnName(ColumnUtil.getColumnName(Long.parseLong(columnId[i]),siteId));
                    interactDetailVO.setColumnType(MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString());
                    interactDetailVO.setUpdateCount(0L);
                    interactDetailVO.setUnreplyCount(0L);
                    interactDetailVO.setReportId(reportId);//设置监测id
                    interactDetailVOS.add(interactDetailVO);
                }
            }
        }
        monitorInteractDetailDao.save(monitorInteractDetailEOList);
        if(reportId!=null&&reportId!=0){
            //将信息推到到云平台
            monitorInteractDetailWebClient.saveInteractDetails(interactDetailVOS);
        }
    }

    private Long getUpdateCycle(Long num, String unit){

        Long dayOffset = 365l;
        //从配置中获取首页栏目监测周期
        if(!AppUtil.isEmpty(unit)&&!AppUtil.isEmpty(num)){
            if("day".equals(unit)){
                dayOffset = num;
            }else if("week".equals(unit)){
                dayOffset = num*7;
            }else if("month".equals(unit)){
                dayOffset = num*30;
            }else if("year".equals(unit)){
                dayOffset = num*365;
            }
        }
        return dayOffset;
    }

    /**
     * 处理栏目更新数据
     */
    @Override
    public void handleColumnDatasByType(Long siteId,Long monitorId,Long reportId,String columnType) {
        String columnIds = getColumnByCode(columnType,siteId);  //根据columnType获取绑定的栏目
        if(AppUtil.isEmpty(columnIds)){
            return;
        }
        String[] columnId = StringUtils.split(columnIds, ",");
        //查询配置
        Map<String,Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.replyScope.toString(),
                columnType,MonitoredVetoConfigEO.BaseCode.scop.toString(),siteId);

        Long noActivityAonitoredNum = getLong(bc,"noActivityAonitoredNum");
        String noActivityTimeType = getString(bc,"noActivityTimeType");
        Integer updateCycle = getUpdateCycle(noActivityAonitoredNum,noActivityTimeType).intValue();//从配置中获取监测周期

        List<MonitorInteractDetailEO> monitorInteractDetailEOs = monitorInteractDetailDao.getUpdatedColumns(columnIds,updateCycle);//查询出规定周期内一更新数据的栏目
        List<Long> columnlist = new ArrayList<Long>();
        List<InteractDetailVO> interactDetailVOS = new ArrayList<InteractDetailVO>();
        for(MonitorInteractDetailEO eo:monitorInteractDetailEOs){
            eo.setColumnType(columnType);
            eo.setMonitorId(monitorId);
            columnlist.add(eo.getColumnId());

            if(reportId!=null&&reportId!=0){
                SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
                String columnUrl = siteMgrEO.getUri()+"/content/column/"+eo.getColumnId();//栏目页访问地址
                //将信息推到到云平台
                InteractDetailVO interactDetailVO = new InteractDetailVO();
                interactDetailVO.setColumnId(eo.getColumnId());
                interactDetailVO.setColumnUrl(columnUrl);
                interactDetailVO.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(),siteId));
                interactDetailVO.setColumnType(columnType);
                interactDetailVO.setUpdateCount(eo.getUpdateCount());
                interactDetailVO.setUnreplyCount(0L);
                interactDetailVO.setReportId(reportId);//设置监测id
                interactDetailVOS.add(interactDetailVO);
            }
        }

        //保存未更新数据的栏目导监测字表中
        for(int i=0;i<columnId.length;i++){
            Long curColumnId = Long.parseLong(columnId[i]);
            if(!columnlist.contains(curColumnId)){
                MonitorInteractDetailEO eo = new MonitorInteractDetailEO();
                eo.setColumnId(curColumnId);
                eo.setColumnType(columnType);
                eo.setUpdateCount(0L);
                eo.setUnreplyCount(0L);
                eo.setMonitorId(monitorId);//设置监测id
                columnlist.add(curColumnId);
                monitorInteractDetailEOs.add(eo);

                if(reportId!=null&&reportId!=0){
                    SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
                    String columnUrl = siteMgrEO.getUri()+"/content/column/"+eo.getColumnId();//栏目页访问地址
                    //将信息推到到云平台
                    InteractDetailVO interactDetailVO = new InteractDetailVO();
                    interactDetailVO.setColumnId(curColumnId);
                    interactDetailVO.setColumnUrl(columnUrl);
                    interactDetailVO.setColumnName(ColumnUtil.getColumnName(curColumnId,siteId));
                    interactDetailVO.setColumnType(columnType);
                    interactDetailVO.setUpdateCount(0L);
                    interactDetailVO.setUnreplyCount(0L);
                    interactDetailVO.setReportId(reportId);//设置监测id
                    interactDetailVOS.add(interactDetailVO);
                }
            }

        }
        monitorInteractDetailDao.save(monitorInteractDetailEOs);
        if(reportId!=null&&reportId!=0){
            //将信息推到到云平台
            monitorInteractDetailWebClient.saveInteractDetails(interactDetailVOS);
        }

    }

    /**
     * 启动互动交流监测任务
     * @param monitorId
     */
    @Override
    public void monitorInteract(Long siteId,Long monitorId,Long reportId) {
        handleZWZXDatas(siteId,monitorId,reportId,MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString());//政务咨询
        handleColumnDatasByType(siteId,monitorId,reportId,MonitoredColumnConfigEO.TypeCode.columnType_DCZJ.toString());//调查征集
        handleColumnDatasByType(siteId,monitorId,reportId,MonitoredColumnConfigEO.TypeCode.columnType_HDFT.toString());//互动访谈类

//        MonitorTaskManageEO task = monitorTaskManageService.getEntity(MonitorTaskManageEO.class,monitorId);
//        task.setReplyScopeStatus(2);
//        logger.info("互动交流检测完成，任务[{}]状态更新完成",monitorId);
//        monitorTaskManageService.updateEntity(task);
        if(null != reportId) {
            monitorTaskManageService.updateStatus(monitorId,"replyStatus",2);
            monitorTaskManageService.updateStatus(monitorId,"replyScopeStatus",2);
        } else {
            monitorCustomIndexManageService.updateStatus(monitorId,2);
        }

    }




    @Override
    public Map<String,Object> calculateScoreByColumnType(Long monitorId, Long siteId, String columnType) {
        //查询上次监测报告
        MonitorTaskManageEO secondLatestReport = monitorTaskManageService.getLatestTask(siteId,monitorId);
        List<MonitorInteractDetailEO> interactList = monitorInteractDetailDao.getList(monitorId,new String[]{columnType},null);
        List<MonitorInteractDetailEO> lastInteractList = monitorInteractDetailDao.getList(secondLatestReport==null?0:secondLatestReport.getId(),new String[]{columnType},null);
        Map<String,Object> result = new HashMap<String,Object>();
        Integer score = 0;
        Integer status = 3;//0-当前栏目不存在，1-一年内没数据,2-开展活动少于规定次数 3-正常。不扣分
        Integer updateCounts = 0;
        //获取监测配置信息
        Map<String, Object> bc = monitoredVetoConfigService.getConfigByTypes(MonitoredVetoConfigEO.CodeType.replyScope.toString(),
                columnType,MonitoredVetoConfigEO.BaseCode.scop.toString(),LoginPersonUtil.getSiteId());

        if(interactList==null||interactList.size()==0){//为开设栏目扣5分
            if(!MonitoredColumnConfigEO.TypeCode.columnType_HDFT.toString().equals(columnType)){//互动访谈未开始栏目不扣分
                score = 5;
                status = 0;
            }
        }else{
            for(MonitorInteractDetailEO eo:interactList){
                if(eo.getUpdateCount()!=null){
                    updateCounts += eo.getUpdateCount().intValue();
                }
            }

            if(MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString().equals(columnType)){
                Integer num = 5;//默认配置
                if(bc!=null){
                    Long num1 = getLong(bc,"num");
                    num = num1==null?5:num1.intValue();
                }
                if(updateCounts<=0){
                    score = num;
                    status = 1;
                }
            }else{
                Integer noActivityNum = 5;//一年未展开活动扣分 默认配置
                Integer activityLimitNum = 6;//展开活动次数最低限度 默认配置
                Integer activityNum = 3;//低于最新限度扣分 默认配置

                if(bc!=null){
                    Long noActivityNum1 = getLong(bc,"noActivityNum");
                    Long activityLimitNum1 = getLong(bc,"activityLimitNum");
                    Long activityNum1 = getLong(bc,"activityNum");
                    noActivityNum = noActivityNum1==null?5:noActivityNum1.intValue();
                    activityLimitNum = activityLimitNum1==null?6:activityLimitNum1.intValue();
                    activityNum = activityNum1==null?3:activityNum1.intValue();
                }
                if(updateCounts<=0){
                    score = noActivityNum;
                    status = 1;
                }else if(updateCounts<activityLimitNum){
                    score = activityNum;
                    status = 2;
                }
            }
        }

        Integer lastScore = 0;
        Integer lastUpdateCounts = 0;
        if(lastInteractList==null||lastInteractList.size()==0){//为开设栏目扣5分
            lastScore = 5;
        }else{
            for(MonitorInteractDetailEO eo:lastInteractList){
                if(eo.getUpdateCount()!=null){
                    lastUpdateCounts += eo.getUpdateCount().intValue();
                }
            }

            if(MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString().equals(columnType)){
                Integer num = 5;//默认配置
                if(bc!=null){
                    Long num1 = getLong(bc,"num");
                    num = num1==null?5:num1.intValue();
                }
                if(lastUpdateCounts<=0){
                    lastScore = num;
                }
            }else{
                Integer noActivityNum = 5;//一年未展开活动扣分 默认配置
                Integer activityLimitNum = 6;//展开活动次数最低限度 默认配置
                Integer activityNum = 3;//低于最新限度扣分 默认配置

                if(bc!=null){
                    Long noActivityNum1 = getLong(bc,"noActivityNum");
                    Long activityLimitNum1 = getLong(bc,"activityLimitNum");
                    Long activityNum1 = getLong(bc,"activityNum");
                    noActivityNum = noActivityNum1==null?5:noActivityNum1.intValue();
                    activityLimitNum = activityLimitNum1==null?6:activityLimitNum1.intValue();
                    activityNum = activityNum1==null?3:activityNum1.intValue();
                }
                if(lastUpdateCounts<=0){
                    lastScore = noActivityNum;
                }else if(lastUpdateCounts<activityLimitNum){
                    lastScore = activityNum;
                }
            }
        }

        result.put("score",score);
        result.put("lastScore",lastScore);
        result.put("status",status);


        if(null == secondLatestReport) {
            result.put("linkRelationStatus",0);
            result.put("linkRelationRate",new Double(0));
        }else{
            if(score==0&&lastScore==0){
                result.put("linkRelationStatus",0);
                result.put("linkRelationRate",new Double(0));
            }else if(score!=0&&lastScore==0){
                result.put("linkRelationStatus",2);
                result.put("linkRelationRate",new Double(0));
            }else if(score!=0&&lastScore!=0){
                result.put("linkRelationStatus",4);
                Double hb = (double)(score - lastScore)*100/lastScore;
                if(hb.intValue()==100){//持平
                    result.put("linkRelationStatus",0);
                }
                result.put("linkRelationRate",getDoubleValue(hb,2));
            }

        }

        return result;
    }

    /**
     * 综合评分项-互动回应情况
     * @return
     */
    @Override
    public InteractInfoStatisVO loadInteractInfoStatis(Long monitorId, Long siteId) {
        InteractInfoStatisVO statisVO = new InteractInfoStatisVO();
        //计算政务咨询分数
        Map<String,Object> zwzxMap = calculateScoreByColumnType(monitorId,siteId,MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString());
        Long lastZwzxScore = Long.valueOf(zwzxMap.get("lastScore")+"");
        statisVO.setZwzxScore(Long.valueOf(zwzxMap.get("score")+""));
        statisVO.setZwzxStatus(Integer.valueOf(zwzxMap.get("status")+""));
//        statisVO.setZwzxLinkRelationStatus(Integer.valueOf(zwzxMap.get("linkRelationStatus")+""));
//        statisVO.setZwzxLinkRelationRate(Double.valueOf(zwzxMap.get("linkRelationRate")+""));

        //计算调查征集分数
        Map<String,Object> dczjMap = calculateScoreByColumnType(monitorId,siteId,MonitoredColumnConfigEO.TypeCode.columnType_DCZJ.toString());
        Long lastDczjScore = Long.valueOf(dczjMap.get("lastScore")+"");
        statisVO.setDczjScore(Long.valueOf(dczjMap.get("score")+""));
        statisVO.setDczjStatus(Integer.valueOf(dczjMap.get("status")+""));
//        statisVO.setDczjLinkRelationStatus(Integer.valueOf(dczjMap.get("linkRelationStatus")+""));
//        statisVO.setDczjLinkRelationRate(Double.valueOf(dczjMap.get("linkRelationRate")+""));

        //计算互动访谈分数
        Map<String,Object> hdftMap = calculateScoreByColumnType(monitorId,siteId,MonitoredColumnConfigEO.TypeCode.columnType_HDFT.toString());
        Long lastHdftScore = Long.valueOf(hdftMap.get("lastScore")+"");
        statisVO.setHdftScore(Long.valueOf(hdftMap.get("score")+""));
        statisVO.setHdftStatus(Integer.valueOf(hdftMap.get("status")+""));
//        statisVO.setHdftLinkRelationStatus(Integer.valueOf(hdftMap.get("linkRelationStatus")+""));
//        statisVO.setHdftLinkRelationRate(Double.valueOf(hdftMap.get("linkRelationRate")+""));

        Long totalScore = statisVO.getZwzxScore() + statisVO.getDczjScore() + statisVO.getHdftScore();
        Long lastToalScore = lastZwzxScore + lastDczjScore + lastHdftScore;
        if(totalScore==0&&lastToalScore==0){
            statisVO.setCpiStatus(0);
        }else if(totalScore!=0&&lastToalScore==0){
            statisVO.setCpiStatus(2);
        }else if(totalScore==0&&lastToalScore!=0){
            statisVO.setCpiStatus(3);
        }else{
            Double cpi = (double)totalScore*100/lastToalScore;
            statisVO.setCpi(getDoubleValue(cpi,2));
            if(cpi.intValue()==100){
                statisVO.setCpiStatus(0);
            }else{
                statisVO.setCpiStatus(4);
            }
        }
        statisVO.setTotalScore(totalScore);

        //更新监测报告子项结果
        monitorSiteStatisService.updateReplyScope(siteId,monitorId,statisVO.getTotalScore()+"");

        return statisVO;
    }

    /**
     * 单项否决-互动回应差
     * @return
     */
    @Override
    public BadInteractStatisVO loadBadInteractStatis(Long monitorId, Long siteId) {

        List<MonitorInteractDetailEO> zwzxColumns = monitorInteractDetailDao.getList(monitorId,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString()},null);
        Integer isOk = 1;
        Integer unreplayCounts = 0;
        if(zwzxColumns!=null){
            for(MonitorInteractDetailEO eo:zwzxColumns){
                if(eo.getUnreplyCount()!=null){
                    unreplayCounts += eo.getUnreplyCount().intValue();
                }
            }
        }

        if(unreplayCounts>0){
            isOk = 0;
        }
        BadInteractStatisVO statisVO = new BadInteractStatisVO();
        statisVO.setIsOk(isOk);
        statisVO.setCount(unreplayCounts+0l);

        //更新监测报告子项结果
        monitorSiteStatisService.updateReply(siteId,monitorId,isOk);

        //查询上次监测报告
        MonitorTaskManageEO secondLatestReport = monitorTaskManageService.getLatestTask(siteId,monitorId);
        if(null == secondLatestReport) {
            statisVO.setLinkRelationStatus(1);
        }else{
            //上次监测政务咨询信息
            List<MonitorInteractDetailEO> lastTimeColumns = monitorInteractDetailDao.getList(secondLatestReport.getId()
                    ,new String[]{MonitoredColumnConfigEO.TypeCode.columnType_ZWZX.toString()},null);
            Long lastCounts = 0l;
            if(null == lastTimeColumns || lastTimeColumns.size() == 0) {

            } else {
                for(MonitorInteractDetailEO eo:lastTimeColumns){
                    if(eo==null||eo.getUnreplyCount()==null||eo.getUnreplyCount().intValue()==0){

                    }else{
                        lastCounts = lastCounts + eo.getUnreplyCount().intValue();
                    }
                }
            }
            if(unreplayCounts==0&&lastCounts==0){
                statisVO.setLinkRelationStatus(0);
            }else if(unreplayCounts!=0&&lastCounts==0){
                statisVO.setLinkRelationStatus(2);
            }else if(unreplayCounts==0&&lastCounts!=0){
                statisVO.setLinkRelationStatus(3);
            }else{//都>0，单项否决
                statisVO.setLinkRelationStatus(1);
            }
        }

        return statisVO;
    }


    @Override
    public Long getCount(Long monitorId, String[] columnType, Long updateCount) {
        return monitorInteractDetailDao.getCount(monitorId,columnType,updateCount);
    }

    /**
     * 根据栏目类型获取绑定的栏目id
     * @param code
     * @return
     */
    private String getColumnByCode(String code,Long siteId){
        String columnIds = "";
        List<ColumnMgrEO> columnMgrEOS = monitoredColumnConfigService.getColumnByCode(code,siteId);  //根据columnType获取绑定的栏目
        for(ColumnMgrEO columnMgrEO:columnMgrEOS){//将columnid拼成字符串
            if(!AppUtil.isEmpty(columnIds)){
                columnIds += ","+columnMgrEO.getIndicatorId();
            }else{
                columnIds += columnMgrEO.getIndicatorId();
            }
        }
        return columnIds;
    }

    private Integer getInt(Map<String, Object> bc, String key){
        if(!bc.containsKey(key) || bc.get(key) == null){
            return null;
        }
        return Integer.valueOf(String.valueOf(bc.get(key)));
    }
    private Long getLong(Map<String, Object> bc, String key){
        if(!bc.containsKey(key) || bc.get(key) == null){
            return null;
        }
        return Long.valueOf(String.valueOf(bc.get(key)));
    }
    private String getString(Map<String, Object> bc, String key){
        if(!bc.containsKey(key) || bc.get(key) == null){
            return "";
        }
        return String.valueOf(bc.get(key));
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

