package cn.lonsun.net.service.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.net.service.dao.IResourcesClassifyDao;
import cn.lonsun.net.service.entity.CmsResourcesClassifyEO;
import cn.lonsun.net.service.service.IResourcesClassifyService;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class ResourcesClassifyService extends BaseService<CmsResourcesClassifyEO> implements IResourcesClassifyService {

    @Autowired
    private IResourcesClassifyDao resourcesClassifyDao;

    @Override
    public List<CmsResourcesClassifyEO> getEOsBySid(Long pId) {
        return resourcesClassifyDao.getEOsBySid(pId);
    }

    @Override
    public Map<Long, Object> getMap(Long pId) {
        return resourcesClassifyDao.getMap(pId);
    }

    @Override
    public void deleteByPid(Long pId) {
        resourcesClassifyDao.deleteByPid(pId);
    }

    @Override
    public List<CmsResourcesClassifyEO> getEOs(Long cId, String type) {
        return resourcesClassifyDao.getEOs(cId,type);
    }
}
