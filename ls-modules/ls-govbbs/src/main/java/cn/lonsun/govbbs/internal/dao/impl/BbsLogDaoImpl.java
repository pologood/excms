package cn.lonsun.govbbs.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.govbbs.internal.dao.IBbsLogDao;
import cn.lonsun.govbbs.internal.entity.BbsLogEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchao on 2016/12/21.
 */
@Repository
public class BbsLogDaoImpl extends BaseDao<BbsLogEO> implements IBbsLogDao {

    @Override
    public List<BbsLogEO> getLogs(Long caseId) {
        String hql = "from BbsLogEO where caseId = ? order by createDate asc";
        return getEntitiesByHql(hql,new Object[]{caseId});
    }

    @Override
    public Long getByMemberId(Long caseId,Long memberId,String operation) {
        List<Object> values = new ArrayList<Object>();
        String hql = "from BbsLogEO where caseId = ? and memberId = ?";
        values.add(caseId);
        values.add(memberId);
        if(!StringUtils.isEmpty(operation)){
            hql +=" and operation = ?";
            values.add(operation);
        }
        return getCount(hql,values.toArray());
    }

    @Override
    public void deleteByCaseIds(Long[] caseIds) {
        String hql = "delete from BbsLogEO where caseId in (:ids)";
        getCurrentSession().createQuery(hql).setParameterList("ids", caseIds).executeUpdate();
    }
}

