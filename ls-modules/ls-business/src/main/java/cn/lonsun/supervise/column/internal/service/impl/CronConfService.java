package cn.lonsun.supervise.column.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.supervise.column.internal.dao.ICronConfDao;
import cn.lonsun.supervise.column.internal.service.ICronConfService;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:48
 */
@Service
public class CronConfService extends MockService<CronConfEO> implements ICronConfService {

    @Autowired
    private ICronConfDao cronConfDao;

    @Override
    public void physDelEO(Long id) {
        cronConfDao.physDelEO(id);
    }
}
