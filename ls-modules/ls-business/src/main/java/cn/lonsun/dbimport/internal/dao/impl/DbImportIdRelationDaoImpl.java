package cn.lonsun.dbimport.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.dbimport.internal.dao.IDbImportIdRelationDao;
import cn.lonsun.dbimport.internal.entity.DbImportIdRelationEO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("dbImportIdRelationDao")
public class DbImportIdRelationDaoImpl extends BaseDao<DbImportIdRelationEO> implements IDbImportIdRelationDao {

    @Override
    public List<DbImportIdRelationEO> getListByDateSort(String type) {
        String hql = "from DbImportIdRelationEO t order by t.createDate desc ";
        return getEntitiesByHql(hql, new Object[]{});
    }

    @Override
    public void deleteType(String type) {
        String sql = "delete from DbImportIdRelationEO t where t.type = ?";
        int i = super.executeUpdateByHql(sql, new Object[]{type});
    }
}
