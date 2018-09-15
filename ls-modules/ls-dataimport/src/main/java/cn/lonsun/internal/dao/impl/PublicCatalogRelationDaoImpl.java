package cn.lonsun.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.internal.dao.IPublicCatalogRelationDao;
import cn.lonsun.internal.dao.IPublicUnitRelationDao;
import cn.lonsun.internal.entity.PublicCatalogRelationEO;
import cn.lonsun.internal.service.IPublicCatalogRelationService;
import org.springframework.stereotype.Repository;


/**
 * 导入失败的记录
 * @author zhongjun
 */
@Repository
public class PublicCatalogRelationDaoImpl extends MockDao<PublicCatalogRelationEO> implements IPublicCatalogRelationDao {

    @Override
    public void deleteByOldId(String[] oldId) {
        super.createQuery("delete from PublicCatalogRelationEO t where t.oldId in :oldId")
                .setParameterList("oldId", oldId).executeUpdate();
    }

}
