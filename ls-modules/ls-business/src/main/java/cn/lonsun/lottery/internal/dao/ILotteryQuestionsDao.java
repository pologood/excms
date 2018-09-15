package cn.lonsun.lottery.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryQuestionsEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

/**
 * Created by lonsun on 2017-1-11.
 */
public interface ILotteryQuestionsDao extends IMockDao<LotteryQuestionsEO>  {


    Pagination getQuestions(QuestionsQueryVO vo);
}
