package cn.lonsun.lsrobot.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.lsrobot.entity.LsRobotParamEO;

/**
 * @author gu.fei
 * @version 2016-07-07 14:19
 */
public interface ILsRobotParamService extends IMockService<LsRobotParamEO> {

    /**
     * 根据站点ID获取站点配置信息
     * @param siteId
     * @return
     */
    public LsRobotParamEO getEntityBySiteId(Long siteId);
}
