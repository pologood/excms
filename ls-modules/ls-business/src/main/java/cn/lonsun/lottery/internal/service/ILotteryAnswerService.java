package cn.lonsun.lottery.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryAnswerEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */
public interface ILotteryAnswerService extends IMockService<LotteryAnswerEO> {
    Pagination getAnswers(QuestionsQueryVO vo);

    void saveAnswer(LotteryAnswerEO lotteryAnswerEO);

    List<LotteryAnswerEO> checkOption(LotteryAnswerEO lotteryAnswerEO);

    List<LotteryAnswerEO> getAnswersByQuestionId(LotteryAnswerEO lotteryAnswerEO);

    List<LotteryAnswerEO> getAnsersByQuestion(Long lotteryId);
}
