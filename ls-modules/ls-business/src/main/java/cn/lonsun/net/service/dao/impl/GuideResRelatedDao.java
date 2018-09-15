package cn.lonsun.net.service.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.net.service.dao.IGuideResRelatedDao;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class GuideResRelatedDao extends BaseDao<CmsGuideResRelatedEO> implements IGuideResRelatedDao {

    @Override
    public void deleteByGID(Long gid) {
        this.executeUpdateByHql("delete from CmsGuideResRelatedEO where guideId = ?", new Object[]{gid});
    }
}
