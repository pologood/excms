package cn.lonsun.lottery.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryTypeEO;

import java.util.List;

/**
 * Created by lonsun on 2017-1-10.
 */
public interface ILotteryTypeDao extends IMockDao<LotteryTypeEO> {
    Pagination getLotteryTypeList(PageQueryVO vo);

    List<LotteryTypeEO> getAllType();
}
