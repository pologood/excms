package cn.lonsun.content.publicInfo.internal.service;

import cn.lonsun.content.publicInfo.vo.PublicApplyStatisticsVO;
import cn.lonsun.core.base.service.IBaseService;

/**
 * Created by lonsun on 2016-9-23.
 */
public interface IPublicInfoStatisticsService extends IBaseService {
    /**
     * 依申请公开统计
     * @param siteId
     * @return
     */
    public PublicApplyStatisticsVO getStatistics(Long orgId,Long siteId);


}
