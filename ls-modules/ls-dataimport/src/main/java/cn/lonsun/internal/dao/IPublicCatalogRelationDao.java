package cn.lonsun.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.internal.entity.PublicCatalogRelationEO;
import cn.lonsun.internal.entity.PublicUnitRelationEO;

public interface IPublicCatalogRelationDao extends IMockDao<PublicCatalogRelationEO> {


    void deleteByOldId(String[] oldId);
}
