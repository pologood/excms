package cn.lonsun.monitor.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.dao.IMonitorSiteRegisterDao;
import cn.lonsun.monitor.internal.entity.MonitorSiteRegisterEO;
import cn.lonsun.monitor.internal.service.IMonitorSiteRegisterService;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日常监测站点注册service层<br/>
 *
 */
@Service("monitorSiteRegisterService")
public class MonitorSiteRegisterServiceImpl extends BaseService<MonitorSiteRegisterEO> implements IMonitorSiteRegisterService {


    @Autowired
    private IMonitorSiteRegisterDao monitorSiteRegisterDao;


    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    @Override
    public List<MonitorSiteRegisterVO> getSiteRegisterInfos(){
        return monitorSiteRegisterDao.getSiteRegisterInfos(null);
    }

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    @Override
    public MonitorSiteRegisterVO getSiteRegisterInfo(Long siteId){
        List<MonitorSiteRegisterVO> vos = monitorSiteRegisterDao.getSiteRegisterInfos(siteId);
        if(vos!=null&&vos.size()>0){
            return vos.get(0);
        }
        return null;
    }


    @Override
    public MonitorSiteRegisterEO getBySiteId(Long siteId){
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("siteId",siteId);
        return getEntity(MonitorSiteRegisterEO.class,param);
    }

    /**
     * 查询站点以及其日常监测开通分页信息
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public Pagination getSiteRegisterInfos(Long pageIndex, Integer pageSize){
        return monitorSiteRegisterDao.getSiteRegisterInfos(pageIndex,pageSize);
    }

}

