package cn.lonsun.lottery.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.lottery.internal.dao.ILotteryConfigDao;
import cn.lonsun.lottery.internal.entity.LotteryConfigEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2017-1-18.
 */
@Repository
public class LotteryConfigDaoImpl extends MockDao<LotteryConfigEO> implements ILotteryConfigDao {
    @Override
    public Pagination getLotteryConfig(QuestionsQueryVO vo) {
        StringBuffer hql = new StringBuffer();
        hql.append("from  LotteryConfigEO t where  t.recordStatus =? ");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        hql.append("order by t.updateDate desc");

        return getPagination(vo.getPageIndex(), vo.getPageSize(), hql.toString(), param.toArray());

    }
}
