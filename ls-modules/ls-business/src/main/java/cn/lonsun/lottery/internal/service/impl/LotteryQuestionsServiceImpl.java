package cn.lonsun.lottery.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryQuestionsDao;
import cn.lonsun.lottery.internal.entity.LotteryAnswerEO;
import cn.lonsun.lottery.internal.entity.LotteryQuestionsEO;
import cn.lonsun.lottery.internal.service.ILotteryAnswerService;
import cn.lonsun.lottery.internal.service.ILotteryQuestionsService;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2017-1-11.
 */
@Service
public class LotteryQuestionsServiceImpl extends MockService<LotteryQuestionsEO> implements ILotteryQuestionsService {
    @Autowired
    private ILotteryQuestionsDao lotteryQuestionsDao;
    @Autowired
    private ILotteryAnswerService lotteryAnswerService;
    @Override
    public Pagination getQuestions(QuestionsQueryVO vo) {
        return lotteryQuestionsDao.getQuestions(vo);
    }

    @Override
    public void delLotteryQuestions(Long[] ids) {
        for(Long id :  ids){
            Map<String,Object> param =new HashMap<String, Object>();
            param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            param.put("lotteryId",id);
            List<LotteryAnswerEO> list = lotteryAnswerService.getEntities(LotteryAnswerEO.class, param);
            lotteryAnswerService.delete(list);
        }

           delete(LotteryQuestionsEO.class,ids);
    }
}
