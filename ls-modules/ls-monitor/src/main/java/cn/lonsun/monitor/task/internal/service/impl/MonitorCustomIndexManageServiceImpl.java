package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.task.internal.dao.IMonitorCustomIndexManageDao;
import cn.lonsun.monitor.task.internal.entity.MonitorCustomIndexManageEO;
import cn.lonsun.monitor.task.internal.service.IMonitorCustomIndexManageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorCustomIndexManageServiceImpl extends MockService<MonitorCustomIndexManageEO> implements IMonitorCustomIndexManageService {

    @Resource
    private IMonitorCustomIndexManageDao monitorCustomIndexManageDao;

    /**
     * 保存指标
     * @param siteId
     */
    @Override
    public void saveIndex(Long siteId) {
        List<MonitorCustomIndexManageEO> indexs = new ArrayList<MonitorCustomIndexManageEO>();
        String aIndexVote = "单项否决项";
        String aIndexScop = "综合评分项";
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.siteDeny.toString(),"站点无法访问",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.siteUpdate.toString(),"网站不更新",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.columnUpdate.toString(),"栏目不更新",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.error.toString(),"严重错误",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.vote.toString(),aIndexVote,MonitoredVetoConfigEO.CodeType.reply.toString(),"互动回应差",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.scop.toString(),aIndexScop,MonitoredVetoConfigEO.CodeType.siteUse.toString(),"首页及链接可用性",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.scop.toString(),aIndexScop,MonitoredVetoConfigEO.CodeType.infoUpdate.toString(),"基本信息更新情况",siteId));
        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.scop.toString(),aIndexScop,MonitoredVetoConfigEO.CodeType.replyScope.toString(),"互动回应情况",siteId));
//        indexs.add(getIndex(MonitoredVetoConfigEO.BaseCode.scop.toString(),aIndexScop,MonitoredVetoConfigEO.CodeType.service.toString(),"服务实用情况",siteId));
        this.saveEntities(indexs);
    }

    @Override
    public MonitorCustomIndexManageEO getIndex(String codeType,Long siteId) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",siteId);
        map.put("bIndex",codeType);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return this.getEntity(MonitorCustomIndexManageEO.class,map);
    }

    /**
     * 获取指标对象
     * @param aIndex
     * @param aIndexName
     * @param bIndex
     * @param bIndexName
     * @param siteId
     * @return
     */
    private MonitorCustomIndexManageEO getIndex(String aIndex,String aIndexName,String bIndex,String bIndexName,Long siteId) {
        MonitorCustomIndexManageEO index = new MonitorCustomIndexManageEO();
        index.setaIndex(aIndex);
        index.setaIndexName(aIndexName);
        index.setbIndex(bIndex);
        index.setbIndexName(bIndexName);
        index.setSiteId(siteId);
        return index;
    }

    @Override
    public void updateStatus(Long taskId,Integer status) {
        monitorCustomIndexManageDao.updateStatus(taskId,status);
    }
}
