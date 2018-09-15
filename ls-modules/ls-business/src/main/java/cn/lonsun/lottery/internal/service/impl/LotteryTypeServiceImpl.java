package cn.lonsun.lottery.internal.service.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryTypeDao;
import cn.lonsun.lottery.internal.entity.LotteryTypeEO;
import cn.lonsun.lottery.internal.service.ILotteryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2017-1-10.
 */
@Service
public class LotteryTypeServiceImpl extends MockService<LotteryTypeEO> implements ILotteryTypeService {
    @Autowired
    private ILotteryTypeDao lotteryTypeDao;


    @Override
    public Pagination getLotteryTypeList(PageQueryVO vo) {
        return lotteryTypeDao.getLotteryTypeList(vo);
    }

    @Override
    public List<LotteryTypeEO> getAllType() {
        return lotteryTypeDao.getAllType();
    }
}
