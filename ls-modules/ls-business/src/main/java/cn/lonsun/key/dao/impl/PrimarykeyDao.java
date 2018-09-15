package cn.lonsun.key.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.key.dao.IPrimarykeyDao;
import cn.lonsun.key.entity.PrimarykeyEO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2016-07-25 16:57
 */
@Repository
public class PrimarykeyDao extends BaseDao<PrimarykeyEO> implements IPrimarykeyDao {

    @Override
    public PrimarykeyEO getEntityByName(String name) {
        String hql = "from PrimarykeyEO where name = ?";
        return this.getEntityByHql(hql,new Object[] {name});
    }

    @Override
    public Long getMaxKeyValue(String tableName, String key) {
        String hql = "SELECT MAX(" + key + ") FROM " + tableName;
        Object obj = this.getCurrentSession().createSQLQuery(hql).uniqueResult();
        if(null != obj) {
            return Long.parseLong(obj.toString());
        }

        return null;
    }

    @Override
    public void saveAEntity(PrimarykeyEO eo) {
        this.getCurrentSession().save(eo);
    }
}
