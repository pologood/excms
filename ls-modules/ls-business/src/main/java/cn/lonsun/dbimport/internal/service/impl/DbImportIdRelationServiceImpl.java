package cn.lonsun.dbimport.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.dbimport.internal.dao.IDbImportIdRelationDao;
import cn.lonsun.dbimport.internal.entity.DbImportIdRelationEO;
import cn.lonsun.dbimport.internal.service.IDbImportIdRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("dbImportIdRelationService")
public class DbImportIdRelationServiceImpl extends BaseService<DbImportIdRelationEO> implements IDbImportIdRelationService {

    @Autowired
    private IDbImportIdRelationDao dbImportIdRelationDao;

    @Override
    public List<DbImportIdRelationEO> getListByDateSort(String type) {
        return dbImportIdRelationDao.getListByDateSort(type);
    }

    public void deleteType(String type){
        dbImportIdRelationDao.deleteType(type);
    }

    public List<DbImportIdRelationEO> getByType(String type){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        return super.getEntities(DbImportIdRelationEO.class, map);
    }
}
