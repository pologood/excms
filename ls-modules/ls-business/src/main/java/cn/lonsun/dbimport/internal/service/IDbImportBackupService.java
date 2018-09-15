package cn.lonsun.dbimport.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.dbimport.internal.entity.DbImportBackupEO;

import java.util.List;

public interface IDbImportBackupService extends IBaseService<DbImportBackupEO> {

    public List<DbImportBackupEO> getListByDateSort(String type);

}
