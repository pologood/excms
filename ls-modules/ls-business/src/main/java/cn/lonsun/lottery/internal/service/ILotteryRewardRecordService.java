package cn.lonsun.lottery.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;
import cn.lonsun.lottery.internal.entity.LotteryRewardRecordEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

import java.text.ParseException;
import java.util.Map;

/**
 * Created by lonsun on 2017-1-11.
 */
public interface ILotteryRewardRecordService extends IMockService<LotteryRewardRecordEO> {
    Object getCountRecordByReward(LotteryRewardEO lotteryRewardEO);

    Object checkTodayUser(String name, String phone);

    Map<String,Object> draw(String name, String phone);

    Pagination getRewardRecord(QuestionsQueryVO vo) throws ParseException;
}
