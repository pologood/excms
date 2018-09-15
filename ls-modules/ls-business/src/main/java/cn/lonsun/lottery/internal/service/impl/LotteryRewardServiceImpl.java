package cn.lonsun.lottery.internal.service.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryRewardDao;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;
import cn.lonsun.lottery.internal.service.ILotteryRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */
@Service
public class LotteryRewardServiceImpl extends MockService<LotteryRewardEO> implements ILotteryRewardService {
    @Autowired
    private ILotteryRewardDao lotteryRewardDao;

    @Override
    public Object getCountRewardByTypeId(Long typeId) {
        return lotteryRewardDao.getCountRewardByTypeId(typeId);
    }

    @Override
    public Pagination getLotteryRewardList(PageQueryVO vo) {
        return lotteryRewardDao.getLotteryRewardList(vo);
    }

    @Override
    public List<LotteryRewardEO> getRewardByType(Long typeId) {
        return lotteryRewardDao.getRewardByType(typeId);
    }

    @Override
    public Object getSumToday() {
        return null;
    }
}
