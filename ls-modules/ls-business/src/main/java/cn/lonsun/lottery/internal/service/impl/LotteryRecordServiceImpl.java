package cn.lonsun.lottery.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryRecordDao;
import cn.lonsun.lottery.internal.entity.LotteryRecordEO;
import cn.lonsun.lottery.internal.service.ILotteryRecordService;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * Created by lizhi on 2017-1-15.
 */
@Service
public class LotteryRecordServiceImpl extends MockService<LotteryRecordEO> implements ILotteryRecordService {
    @Autowired
    private ILotteryRecordDao lotteryRecordDao;

    @Override
    public Pagination getRecords(QuestionsQueryVO vo) throws ParseException {
        return lotteryRecordDao.getRecords(vo);
    }

    @Override
    public Object checkTodayUser(String name, String phone,Integer status) {
        return lotteryRecordDao.checkTodayUser(name,phone,status);
    }
}
