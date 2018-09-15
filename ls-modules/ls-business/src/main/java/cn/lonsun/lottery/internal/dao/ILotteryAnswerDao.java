package cn.lonsun.lottery.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryAnswerEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */
public interface ILotteryAnswerDao  extends IMockDao<LotteryAnswerEO> {

    Pagination getAnswers(QuestionsQueryVO vo);

     List<LotteryAnswerEO> getAnswersByQuestionId(LotteryAnswerEO lotteryAnswerEO);

    List<LotteryAnswerEO> checkOption(LotteryAnswerEO lotteryAnswerEO);

    List<LotteryAnswerEO> getAnsersByQuestion(Long lotteryId);
}
