package cn.lonsun.resourceMonitor.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.resourceMonitor.internal.entity.ResourceMonitorEO;
import cn.lonsun.resourceMonitor.vo.ResourceMonitorQueryVO;

/**
 * Created by Administrator on 2017/4/11.
 */
public interface IResourceMonitorService extends IMockService<ResourceMonitorEO>{

    public Pagination getPageEntities(ResourceMonitorQueryVO queryVO);

    Boolean isExist(ResourceMonitorEO resourceEO);

    void deleteEO(Long[] ids);
}
