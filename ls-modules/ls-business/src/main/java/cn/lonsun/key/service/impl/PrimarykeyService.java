package cn.lonsun.key.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.key.dao.IPrimarykeyDao;
import cn.lonsun.key.entity.PrimarykeyEO;
import cn.lonsun.key.service.IPrimarykeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2016-07-25 17:02
 */
@Service
public class PrimarykeyService extends BaseService<PrimarykeyEO> implements IPrimarykeyService {

    @Autowired
    private IPrimarykeyDao primarykeyDao;

    @Override
    public PrimarykeyEO getEntityByName(String name) {
        return primarykeyDao.getEntityByName(name);
    }

    @Override
    public Long getMaxKeyValue(String tableName, String key) {
        return primarykeyDao.getMaxKeyValue(tableName,key);
    }

    @Override
    public void saveAEntity(PrimarykeyEO eo) {
        primarykeyDao.saveAEntity(eo);
    }
}
