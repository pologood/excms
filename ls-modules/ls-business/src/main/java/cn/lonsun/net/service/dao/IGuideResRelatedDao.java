package cn.lonsun.net.service.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IGuideResRelatedDao extends IBaseDao<CmsGuideResRelatedEO> {

    public void deleteByGID(Long gid);

}
