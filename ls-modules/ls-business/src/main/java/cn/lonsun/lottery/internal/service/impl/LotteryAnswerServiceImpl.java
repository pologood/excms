package cn.lonsun.lottery.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryAnswerDao;
import cn.lonsun.lottery.internal.entity.LotteryAnswerEO;
import cn.lonsun.lottery.internal.entity.LotteryQuestionsEO;
import cn.lonsun.lottery.internal.service.ILotteryAnswerService;
import cn.lonsun.lottery.internal.service.ILotteryQuestionsService;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */
@Service
public class LotteryAnswerServiceImpl extends MockService<LotteryAnswerEO> implements ILotteryAnswerService {

    @Autowired
    private ILotteryAnswerDao lotteryAnswerDao;
    @Autowired
    private ILotteryQuestionsService lotteryQuestionsService;

    @Override
    public Pagination getAnswers(QuestionsQueryVO vo) {
        return lotteryAnswerDao.getAnswers(vo);
    }

    @Override
    public void saveAnswer(LotteryAnswerEO lotteryAnswerEO) {





        if(lotteryAnswerEO.getIsTrue()==1){
            List<LotteryAnswerEO> list = lotteryAnswerDao.getAnswersByQuestionId(lotteryAnswerEO);
            if(null!=list&&list.size()>0){
                for(LotteryAnswerEO answerEO :  list){
                    answerEO.setIsTrue(0);

                }

                updateEntities(list);
            }

        }

        if(AppUtil.isEmpty( lotteryAnswerEO.getAnswerId())){
            lotteryAnswerEO.setSiteId(LoginPersonUtil.getSiteId());
            saveEntity(lotteryAnswerEO);
        }
        else {

            LotteryQuestionsEO oldquestionsEO =   lotteryQuestionsService.getEntity(LotteryQuestionsEO.class, lotteryAnswerEO.getLotteryId());
            if(null!=oldquestionsEO&&!AppUtil.isEmpty(oldquestionsEO.getAnswerId())&&lotteryAnswerEO.getIsTrue()==1&&lotteryAnswerEO.getAnswerId().intValue()!=oldquestionsEO.getAnswerId().intValue()){
                oldquestionsEO.setAnswerId(null);
                lotteryQuestionsService.updateEntity(oldquestionsEO);

            }


            updateEntity(lotteryAnswerEO);

        }
        if(lotteryAnswerEO.getIsTrue()==1){
            LotteryQuestionsEO questionsEO =   lotteryQuestionsService.getEntity(LotteryQuestionsEO.class, lotteryAnswerEO.getLotteryId());
            questionsEO.setAnswerId(lotteryAnswerEO.getAnswerId());
            lotteryQuestionsService.updateEntity(questionsEO);
        }




    }

    @Override
    public  List<LotteryAnswerEO> checkOption(LotteryAnswerEO lotteryAnswerEO) {
       return lotteryAnswerDao.checkOption(lotteryAnswerEO);
    }

    @Override
    public List<LotteryAnswerEO> getAnswersByQuestionId(LotteryAnswerEO lotteryAnswerEO) {
        return lotteryAnswerDao.getAnswersByQuestionId( lotteryAnswerEO);
    }

    @Override
    public List<LotteryAnswerEO> getAnsersByQuestion(Long lotteryId) {
        return lotteryAnswerDao.getAnsersByQuestion(lotteryId);
    }
}
