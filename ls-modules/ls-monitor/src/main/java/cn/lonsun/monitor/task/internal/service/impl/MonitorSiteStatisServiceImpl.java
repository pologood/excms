package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorSiteStatisDao;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteStatisEO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteStatisQueryVO;
import cn.lonsun.monitor.task.internal.service.IMonitorSiteStatisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorSiteStatisServiceImpl extends MockService<MonitorSiteStatisEO> implements IMonitorSiteStatisService {

    @Resource
    private IMonitorSiteStatisDao monitorSiteStatisDao;

    @Override
    public Pagination getSiteStatisPage(SiteStatisQueryVO vo) {
        return monitorSiteStatisDao.getSiteStatisPage(vo);
    }

    @Override
    public void updateSiteDeny(Long siteId, Long taskId, Integer status) {
        monitorSiteStatisDao.updateSiteDeny(siteId,taskId,status);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void updateSiteUpdate(Long siteId, Long taskId, Integer status) {
        monitorSiteStatisDao.updateSiteUpdate(siteId,taskId,status);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void updateColumnUpdate(Long siteId, Long taskId, Integer status) {
        monitorSiteStatisDao.updateColumnUpdate(siteId,taskId,status);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void updateError(Long siteId, Long taskId, Integer status) {
        monitorSiteStatisDao.updateError(siteId,taskId,status);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void updateReply(Long siteId, Long taskId, Integer status) {
        monitorSiteStatisDao.updateReply(siteId,taskId,status);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void updateSiteUse(Long siteId, Long taskId, String score) {
        monitorSiteStatisDao.updateSiteUse(siteId,taskId,score);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void updateIndexInfoUpdate(Long siteId, Long taskId, String score) {
        monitorSiteStatisDao.updateIndexInfoUpdate(siteId,taskId,score);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void updateInfoUpdate(Long siteId, Long taskId, String score) {
        monitorSiteStatisDao.updateInfoUpdate(siteId,taskId,score);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void updateReplyScope(Long siteId, Long taskId, String score) {
        monitorSiteStatisDao.updateReplyScope(siteId,taskId,score);
        summaryStatisData(siteId,taskId);
    }

    @Override
    public void summaryStatisData(Long siteId, Long taskId) {
        MonitorSiteStatisEO statis = getSiteStatis(siteId,taskId);
        if(null != statis) {
            if(null != statis.getSiteDeny() && statis.getSiteDeny() == 0) {
                statis.setIsQualified(0);
            } else if(null != statis.getSiteUpdate() && statis.getSiteUpdate() == 0) {
                statis.setIsQualified(0);
            } else if(null != statis.getColumnUpdate() && statis.getColumnUpdate() == 0) {
                statis.setIsQualified(0);
            } else if(null != statis.getError() && statis.getError() == 0) {
                statis.setIsQualified(0);
            } else if(null != statis.getReply() && statis.getReply() == 0) {
                statis.setIsQualified(0);
            }
            //综合评分项比较
            BigDecimal score = new BigDecimal(0);
            if(null != statis.getSiteUse()) {
                score = score.add(new BigDecimal(statis.getSiteUse()));
            }
            if(null != statis.getIndexInfoUpdate()) {
                score = score.add(new BigDecimal(statis.getIndexInfoUpdate()));
            }

            if(null != statis.getInfoUpdate()) {
                score = score.add(new BigDecimal(statis.getInfoUpdate()));
            }
            if(null != statis.getReplyScope()) {
                score = score.add(new BigDecimal(statis.getReplyScope()));
            }
            if(score.intValue() > 40) {
                statis.setIsQualified(0);
            }
            statis.setMonitorScore(String.valueOf(getDoubleValue(score.doubleValue(),1)));
        }
    }

    public synchronized MonitorSiteStatisEO getSiteStatis(Long siteId) {
        return monitorSiteStatisDao.getSiteStatis(siteId);
    }

    @Override
    public MonitorSiteStatisEO getSiteStatis(Long siteId, Long taskId) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",siteId);
        map.put("taskId",taskId);
        return this.getEntity(MonitorSiteStatisEO.class,map);
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
