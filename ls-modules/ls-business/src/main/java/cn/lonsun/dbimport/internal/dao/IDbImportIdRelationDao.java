package cn.lonsun.dbimport.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.dbimport.internal.entity.DbImportIdRelationEO;

import java.util.List;

public interface IDbImportIdRelationDao extends IBaseDao<DbImportIdRelationEO> {

    List<DbImportIdRelationEO> getListByDateSort(String type);

    public void deleteType(String type);

}
