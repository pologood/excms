package cn.lonsun.key.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.key.entity.PrimarykeyEO;

/**
 * @author gu.fei
 * @version 2016-07-25 16:57
 */
public interface IPrimarykeyDao extends IBaseDao<PrimarykeyEO> {

    public PrimarykeyEO getEntityByName(String name);

    public Long getMaxKeyValue(String tableName,String key);

    public void saveAEntity(PrimarykeyEO eo);
}
