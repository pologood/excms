package cn.lonsun.lsrobot.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.lsrobot.dao.ILsRobotParamDao;
import cn.lonsun.lsrobot.entity.LsRobotParamEO;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2016-07-07 14:22
 */
@Repository
public class LsRobotParamDao extends MockDao<LsRobotParamEO> implements ILsRobotParamDao {

    @Override
    public LsRobotParamEO getEntityBySiteId(Long siteId) {
        return this.getEntityByHql("from LsRobotParamEO where siteId = ?", new Object[]{siteId});
    }
}
