package cn.lonsun.datacollect.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IDBCollectColumnDao;
import cn.lonsun.datacollect.entity.DBCollectColumnEO;
import cn.lonsun.datacollect.service.IDBCollectColumnService;
import cn.lonsun.datacollect.vo.CollectPageVO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:36
 */
@Service("dbCollectColumnService")
public class DBCollectColumnService extends MockService<DBCollectColumnEO> implements IDBCollectColumnService {

    @Autowired
    private IDBCollectColumnDao dbCollectColumnDao;

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        return dbCollectColumnDao.getPageEOs(vo);
    }

    @Override
    public List<DBCollectColumnEO> getEOsByTaskId(Long taskId) {
        return dbCollectColumnDao.getEOsByTaskId(taskId);
    }

    @Override
    public void saveEO(DBCollectColumnEO eo) {
        this.saveEntity(eo);
    }

    @Override
    public void updateEO(DBCollectColumnEO eo) {
        DBCollectColumnEO columnEO = this.getEntity(DBCollectColumnEO.class,eo.getId());
        columnEO.setFromColumn(eo.getFromColumn());
        columnEO.setToColumn(eo.getToColumn());
        columnEO.setFilterCondition(eo.getFilterCondition());
        columnEO.setDefaultValue(eo.getDefaultValue());
        this.updateEntity(columnEO);
    }

    @Override
    public void deleteEOs(Long[] ids) {
        dbCollectColumnDao.deleteEOs(ids);
    }
}
