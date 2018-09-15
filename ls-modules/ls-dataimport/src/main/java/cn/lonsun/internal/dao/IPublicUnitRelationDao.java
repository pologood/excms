package cn.lonsun.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.internal.entity.ErrorPublicContentEO;
import cn.lonsun.internal.entity.PublicUnitRelationEO;

import java.util.List;

public interface IPublicUnitRelationDao extends IMockDao<PublicUnitRelationEO> {


    void deleteByOldId(String[] oldId);

    /**
     *
     * @param siteId
     * @param oldId
     * @return
     */
    public List<PublicUnitRelationEO> getByOldId(Long siteId, String oldId);
}
