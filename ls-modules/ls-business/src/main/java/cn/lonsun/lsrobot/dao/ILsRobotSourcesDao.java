package cn.lonsun.lsrobot.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.entity.LsRobotSourcesEO;
import cn.lonsun.lsrobot.vo.RobotPageVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-07-07 14:19
 */
public interface ILsRobotSourcesDao extends IMockDao<LsRobotSourcesEO> {

    public Pagination getPageEntities(RobotPageVO vo);

    public List<LsRobotSourcesEO> getEntities(RobotPageVO vo);

    public LsRobotSourcesEO getEntity(RobotPageVO vo);

    public Long getMaxSortNum();
}
