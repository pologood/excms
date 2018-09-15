package cn.lonsun.lottery.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;

import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */
public interface ILotteryRewardService extends IMockService<LotteryRewardEO> {
    Object getCountRewardByTypeId(Long typeId);

    Pagination getLotteryRewardList(PageQueryVO vo);

    List<LotteryRewardEO> getRewardByType(Long typeId);

    Object getSumToday();
}
