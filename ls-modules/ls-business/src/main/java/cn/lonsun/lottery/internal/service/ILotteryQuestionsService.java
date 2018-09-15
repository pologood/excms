package cn.lonsun.lottery.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryQuestionsEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

/**
 * Created by lonsun on 2017-1-11.
 */
public interface ILotteryQuestionsService extends IMockService<LotteryQuestionsEO> {


    Pagination getQuestions(QuestionsQueryVO vo);

    void delLotteryQuestions(Long[] ids);
}
