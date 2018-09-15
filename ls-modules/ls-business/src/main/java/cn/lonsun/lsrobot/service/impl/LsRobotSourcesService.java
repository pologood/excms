package cn.lonsun.lsrobot.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.dao.ILsRobotSourcesDao;
import cn.lonsun.lsrobot.entity.LsRobotSourcesEO;
import cn.lonsun.lsrobot.service.ILsRobotSourcesService;
import cn.lonsun.lsrobot.vo.RobotPageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-07-07 14:23
 */
@Service
public class LsRobotSourcesService extends MockService<LsRobotSourcesEO> implements ILsRobotSourcesService {

    @Autowired
    private ILsRobotSourcesDao lsRobotSourcesDao;

    @Override
    public Pagination getPageEntities(RobotPageVO vo) {
        return lsRobotSourcesDao.getPageEntities(vo);
    }

    @Override
    public List<LsRobotSourcesEO> getEntities(RobotPageVO vo) {
        return lsRobotSourcesDao.getEntities(vo);
    }

    @Override
    public LsRobotSourcesEO getEntity(RobotPageVO vo) {
        return lsRobotSourcesDao.getEntity(vo);
    }

    @Override
    public Long getMaxSortNum() {
        return lsRobotSourcesDao.getMaxSortNum();
    }
}
