package cn.lonsun.dbimport.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.dbimport.internal.entity.DbImportBackupEO;

import java.util.List;

public interface IDbImportBackupDao extends IBaseDao<DbImportBackupEO> {

    List<DbImportBackupEO> getListByDateSort(String type);

}
