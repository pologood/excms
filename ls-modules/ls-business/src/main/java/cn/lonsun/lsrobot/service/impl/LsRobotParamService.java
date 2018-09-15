package cn.lonsun.lsrobot.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.lsrobot.dao.ILsRobotParamDao;
import cn.lonsun.lsrobot.entity.LsRobotParamEO;
import cn.lonsun.lsrobot.service.ILsRobotParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2016-07-07 14:23
 */
@Service
public class LsRobotParamService extends MockService<LsRobotParamEO> implements ILsRobotParamService {

    @Autowired
    private ILsRobotParamDao lsRobotParamDao;

    @Override
    public LsRobotParamEO getEntityBySiteId(Long siteId) {
        return lsRobotParamDao.getEntityBySiteId(siteId);
    }
}
