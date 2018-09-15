package cn.lonsun.lottery.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryTypeDao;
import cn.lonsun.lottery.internal.entity.LotteryTypeEO;

import java.util.List;

/**
 * Created by lonsun on 2017-1-10.
 */
public interface ILotteryTypeService extends IMockService<LotteryTypeEO> {


    Pagination getLotteryTypeList(PageQueryVO vo);

    List<LotteryTypeEO> getAllType();
}
