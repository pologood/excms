package cn.lonsun.net.service.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface IGuideResRelatedService extends IBaseService<CmsGuideResRelatedEO> {

    public void deleteByGID(Long gid);
}
