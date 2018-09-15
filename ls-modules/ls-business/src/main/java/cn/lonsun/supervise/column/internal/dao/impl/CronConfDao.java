package cn.lonsun.supervise.column.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.supervise.column.internal.dao.ICronConfDao;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:46
 */
@Repository
public class CronConfDao extends MockDao<CronConfEO> implements ICronConfDao {

    @Override
    public void physDelEO(Long id) {
        this.executeUpdateByHql("delete from CronConfEO where id = ?", new Object[]{id});
    }
}
