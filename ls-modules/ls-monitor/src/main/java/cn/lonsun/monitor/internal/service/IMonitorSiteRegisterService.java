package cn.lonsun.monitor.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.entity.MonitorSiteRegisterEO;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;

import java.util.List;

/**
 * 日常监测站点注册service层<br/>
 *
 */
public interface IMonitorSiteRegisterService extends IBaseService<MonitorSiteRegisterEO> {

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    List<MonitorSiteRegisterVO> getSiteRegisterInfos();

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    MonitorSiteRegisterVO getSiteRegisterInfo(Long siteId);

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    MonitorSiteRegisterEO getBySiteId(Long siteId);

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    Pagination getSiteRegisterInfos(Long pageIndex, Integer pageSize);


}
