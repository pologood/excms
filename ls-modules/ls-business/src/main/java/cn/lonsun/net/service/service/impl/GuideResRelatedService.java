package cn.lonsun.net.service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.net.service.dao.IGuideResRelatedDao;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;
import cn.lonsun.net.service.service.IGuideResRelatedService;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class GuideResRelatedService extends BaseService<CmsGuideResRelatedEO> implements IGuideResRelatedService {

    @Autowired
    private IGuideResRelatedDao guideResRelatedDao;

    @Override
    public void deleteByGID(Long gid) {
        guideResRelatedDao.deleteByGID(gid);
        //刷新缓存
        CacheHandler.reload(CmsGuideResRelatedEO.class.getName());
    }
}
