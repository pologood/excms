package cn.lonsun.dbimport.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.dbimport.internal.entity.DbImportIdRelationEO;

import java.util.List;

public interface IDbImportIdRelationService extends IBaseService<DbImportIdRelationEO> {

    public List<DbImportIdRelationEO> getListByDateSort(String type);

    public void deleteType(String type);

    public List<DbImportIdRelationEO> getByType(String type);

}
