package cn.lonsun.lsrobot.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.lsrobot.entity.LsRobotParamEO;

/**
 * @author gu.fei
 * @version 2016-07-07 14:19
 */
public interface ILsRobotParamDao extends IMockDao<LsRobotParamEO> {

    /**
     * 根据站点ID获取站点配置信息
     * @param siteId
     * @return
     */
    public LsRobotParamEO getEntityBySiteId(Long siteId);
}
