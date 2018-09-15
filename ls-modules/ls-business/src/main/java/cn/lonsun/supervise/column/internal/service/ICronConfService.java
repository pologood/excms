package cn.lonsun.supervise.column.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:47
 */
public interface ICronConfService extends IMockService<CronConfEO> {

    /**
     * 物理删除任务
     * @param id
     */
    public void physDelEO(Long id);
}
