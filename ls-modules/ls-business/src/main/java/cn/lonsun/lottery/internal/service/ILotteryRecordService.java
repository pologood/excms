package cn.lonsun.lottery.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryRecordEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;

import java.text.ParseException;

/**
 * Created by lizhi on 2017-1-15.
 */
public interface ILotteryRecordService extends IMockService<LotteryRecordEO> {
    Pagination getRecords(QuestionsQueryVO vo) throws ParseException;

    Object checkTodayUser(String name, String phone,Integer status);
}
