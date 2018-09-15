package cn.lonsun.monitor.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.entity.MonitorSiteRegisterEO;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;

import java.util.List;

/**
 * 日常监测站点注册<br/>
 *
 */

public interface IMonitorSiteRegisterDao extends IBaseDao<MonitorSiteRegisterEO> {

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    List<MonitorSiteRegisterVO> getSiteRegisterInfos(Long siteId);

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    Pagination getSiteRegisterInfos(Long pageIndex, Integer pageSize);
}

