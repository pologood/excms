package cn.lonsun.supervise.column.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:45
 */
public interface ICronConfDao extends IMockDao<CronConfEO> {

    /**
     * 物理删除任务
     * @param id
     */
    public void physDelEO(Long id);
}
