package cn.lonsun.dbimport.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.dbimport.internal.dao.IDbImportBackupDao;
import cn.lonsun.dbimport.internal.entity.DbImportBackupEO;
import cn.lonsun.dbimport.internal.service.IDbImportBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dbImportBackupService")
public class DbImportBackupServiceImpl extends BaseService<DbImportBackupEO> implements IDbImportBackupService {

    @Autowired
    private IDbImportBackupDao dbImportBackupDao;

    @Override
    public List<DbImportBackupEO> getListByDateSort(String type) {
        return dbImportBackupDao.getListByDateSort(type);
    }
}
