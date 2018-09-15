package cn.lonsun.resourceMonitor.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.resourceMonitor.internal.entity.ResourceMonitorEO;
import cn.lonsun.resourceMonitor.vo.ResourceMonitorQueryVO;

/**
 * Created by Administrator on 2017/4/11.
 */
public interface IResourceMonitorDao extends IMockDao<ResourceMonitorEO>{

    public Pagination getPageEntities(ResourceMonitorQueryVO queryVO);

    ResourceMonitorEO getEOByTitle(ResourceMonitorEO resourceEO);
}
