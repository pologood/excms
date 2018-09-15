package cn.lonsun.lottery.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryConfigEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

/**
 * Created by lonsun on 2017-1-18.
 */
public interface ILotteryConfigDao extends IMockDao<LotteryConfigEO> {
    Pagination getLotteryConfig(QuestionsQueryVO vo);
}
