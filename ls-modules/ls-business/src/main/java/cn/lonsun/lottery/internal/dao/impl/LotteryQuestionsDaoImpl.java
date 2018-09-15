package cn.lonsun.lottery.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.lottery.internal.dao.ILotteryQuestionsDao;
import cn.lonsun.lottery.internal.entity.LotteryQuestionsEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import cn.lonsun.lottery.internal.vo.QuestionsVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */

@Repository
public class LotteryQuestionsDaoImpl extends MockDao<LotteryQuestionsEO> implements ILotteryQuestionsDao {


    @Override
    public Pagination getQuestions(QuestionsQueryVO vo) {
        StringBuffer hql = new StringBuffer();
        hql.append("select  m.LOTTERY_ID as lotteryId,m.LOTTERY_TITLE as lotteryTitle,n.IS_TRUE as isTrue,n.ANSWER as answer,n.ANSWER_OPTION as answerOption from  LOTTERY_QUESTIONS m left join LOTTERY_ANSWER n on m.ANSWER_ID = n.ANSWER_ID where m.SITE_ID =?  and m.RECORD_STATUS=?  ");

        List<Object> param =new ArrayList<Object>();
        param.add(LoginPersonUtil.getSiteId());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(!AppUtil.isEmpty(vo.getSearchKey())){
            hql.append("  and m.LOTTERY_TITLE like ?");
            param.add("%"+SqlUtil.prepareParam4Query(vo.getSearchKey())+"%");
        }
        hql.append("order by m.UPDATE_DATE desc");

        return getPaginationBySql(vo.getPageIndex(),vo.getPageSize(),hql.toString(), param.toArray(), QuestionsVO.class);
    }
}
