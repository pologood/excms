package cn.lonsun.lottery.internal.dao.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryTypeDao;
import cn.lonsun.lottery.internal.entity.LotteryTypeEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2017-1-10.
 */
@Repository
public class LotteryTypeDaoImpl extends MockDao<LotteryTypeEO> implements ILotteryTypeDao {
    @Override
    public Pagination getLotteryTypeList(PageQueryVO vo) {
        StringBuffer hql =new StringBuffer();
        List<Object> param =new ArrayList<Object>();
        hql.append("from LotteryTypeEO t where t.recordStatus =? and t.siteId =? ");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(LoginPersonUtil.getSiteId());
        return getPagination(vo.getPageIndex(),vo.getPageSize(),hql.toString(),param.toArray());
    }

    @Override
    public  List<LotteryTypeEO> getAllType() {
        StringBuffer hql =new StringBuffer();
        List<Object> param =new ArrayList<Object>();
        hql.append("from LotteryTypeEO t where t.recordStatus =? order by t.chance asc ");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(hql.toString(),param.toArray());
    }
}
