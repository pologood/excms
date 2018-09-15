package cn.lonsun.lottery.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;
import cn.lonsun.lottery.internal.entity.LotteryRewardRecordEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

import java.text.ParseException;

/**
 * Created by lonsun on 2017-1-11.
 */
public interface ILotteryRewardRecordDao extends IMockDao<LotteryRewardRecordEO> {
    Object getCountRecordByReward(LotteryRewardEO lotteryRewardEO);

    Object checkTodayUser(String name, String phone);

    Pagination getRewardRecord(QuestionsQueryVO vo) throws ParseException;

    Object getSumToday();
}
