package cn.lonsun.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.internal.dao.IPublicUnitRelationDao;
import cn.lonsun.internal.entity.PublicUnitRelationEO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * 导入失败的记录
 * @author zhongjun
 */
@Repository
public class PublicUnitRelationDaoImpl extends MockDao<PublicUnitRelationEO> implements IPublicUnitRelationDao {

    @Override
    public void deleteByOldId(String[] oldId) {
        super.createQuery("delete from PublicUnitRelationEO t where t.oldUnitId in :oldUnitId")
                .setParameterList("oldUnitId", oldId).executeUpdate();
    }

    @Override
    public List<PublicUnitRelationEO> getByOldId(Long siteId, String oldId) {
        StringBuilder hql = new StringBuilder();
        hql.append("from PublicUnitRelationEO t where recordStatus = ? ");
        List values = new ArrayList();
        values.add(PublicUnitRelationEO.RecordStatus.Normal.toString());
        if(StringUtils.isNotEmpty(oldId)){
            hql.append(" and t.oldUnitId = ?");
            values.add(oldId);
        }
        if(siteId != null){
            hql.append(" and t.newUnitId in (select organId from OrganEO where siteId = ? ) ");
            values.add(oldId);
        }
        return super.getEntitiesByHql(hql.toString(), values.toArray());
    }

}
