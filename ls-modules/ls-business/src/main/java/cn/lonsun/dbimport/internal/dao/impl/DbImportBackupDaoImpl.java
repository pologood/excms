package cn.lonsun.dbimport.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.dbimport.internal.dao.IDbImportBackupDao;
import cn.lonsun.dbimport.internal.entity.DbImportBackupEO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("dbImportBackupDao")
public class DbImportBackupDaoImpl extends BaseDao<DbImportBackupEO> implements IDbImportBackupDao {

    @Override
    public List<DbImportBackupEO> getListByDateSort(String type) {
        String hql = "from DbImportBackupEO t order by t.createDate desc ";
        return getEntitiesByHql(hql, new Object[]{});
    }
}
