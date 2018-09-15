package cn.lonsun.lottery.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryConfigEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

/**
 * Created by lonsun on 2017-1-18.
 */
public interface ILotteryConfigService extends IMockService<LotteryConfigEO> {
    Pagination getLotteryConfig(QuestionsQueryVO vo);
}
