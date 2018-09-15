package cn.lonsun.lottery.internal.dao.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.dao.ILotteryRewardDao;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */
@Repository
public class LotteryRewardDaoImpl extends MockDao<LotteryRewardEO> implements ILotteryRewardDao {
    @Override
    public Object getCountRewardByTypeId(Long typeId) {
        StringBuffer hql = new StringBuffer();
        hql.append("select count(1) from LotteryRewardEO t where t.siteId =?  and   t.recordStatus =? and t.typeId = ?");
        List<Object> param =new ArrayList<Object>();
        param.add(LoginPersonUtil.getSiteId());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(typeId);
        return getObject(hql.toString(),param.toArray());
    }

    @Override
    public Pagination getLotteryRewardList(PageQueryVO vo) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from LotteryRewardEO t where t.siteId =? and  t.recordStatus =? ");
        List<Object> param =new ArrayList<Object>();
        param.add(LoginPersonUtil.getSiteId());
        param.add(AMockEntity.RecordStatus.Normal.toString());

        return getPagination(vo.getPageIndex(),vo.getPageSize(),hql.toString(),param.toArray());
    }

    @Override
    public List<LotteryRewardEO> getRewardByType(Long typeId) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from LotteryRewardEO t where t.typeId =? and  t.recordStatus =? ");
        List<Object> param =new ArrayList<Object>();
        param.add(typeId);
        param.add(AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(hql.toString(),param.toArray());
    }
}
