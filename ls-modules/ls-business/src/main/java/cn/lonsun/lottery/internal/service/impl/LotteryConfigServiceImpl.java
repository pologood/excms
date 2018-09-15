package cn.lonsun.lottery.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryConfigDao;
import cn.lonsun.lottery.internal.entity.LotteryConfigEO;
import cn.lonsun.lottery.internal.service.ILotteryConfigService;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lonsun on 2017-1-18.
 */
@Service
public class LotteryConfigServiceImpl extends MockService<LotteryConfigEO> implements ILotteryConfigService {
    @Autowired
    private ILotteryConfigDao lotteryConfigDao;

    @Override
    public Pagination getLotteryConfig(QuestionsQueryVO vo) {
        return lotteryConfigDao.getLotteryConfig(vo);
    }
}
