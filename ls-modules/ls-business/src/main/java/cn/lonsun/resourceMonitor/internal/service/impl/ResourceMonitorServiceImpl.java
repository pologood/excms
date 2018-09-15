package cn.lonsun.resourceMonitor.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.resourceMonitor.internal.dao.IResourceMonitorDao;
import cn.lonsun.resourceMonitor.internal.entity.ResourceMonitorEO;
import cn.lonsun.resourceMonitor.internal.service.IResourceMonitorService;
import cn.lonsun.resourceMonitor.vo.ResourceMonitorQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by huangxx on 2017/4/11.
 */
@Service
public class ResourceMonitorServiceImpl extends MockService<ResourceMonitorEO> implements IResourceMonitorService{

    @Autowired
    private IResourceMonitorDao resourceMonitorDao;

    @Override
    public Pagination getPageEntities(ResourceMonitorQueryVO queryVO) {
        return resourceMonitorDao.getPageEntities(queryVO);
    }

    @Override
    public Boolean isExist(ResourceMonitorEO resourceEO) {
        Boolean isExist = false;
        Long userId = LoginPersonUtil.getUserId();//获取登录账户id
        if(null != resourceEO.getResourceName()) {
            if(null != userId) {
                resourceEO.setCreateUserId(userId);
            }
            ResourceMonitorEO EO =  resourceMonitorDao.getEOByTitle(resourceEO);
            if(!AppUtil.isEmpty(EO)) {
                isExist = true;
            }
        }
        return isExist;
    }

    @Override
    public void deleteEO(Long[] ids) {
        for(Long id : ids) {
            ResourceMonitorEO resourceEO = this.getEntity(ResourceMonitorEO.class,id);
            resourceEO.setRecordStatus(AMockEntity.RecordStatus.Removed.toString());
            this.updateEntity(resourceEO);
            CacheHandler.delete(ResourceMonitorEO.class,resourceEO);
        }
    }
}
