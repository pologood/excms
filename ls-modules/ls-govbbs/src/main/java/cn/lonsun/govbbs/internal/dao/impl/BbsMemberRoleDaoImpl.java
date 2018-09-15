package cn.lonsun.govbbs.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.govbbs.internal.dao.IBbsMemberRoleDao;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 论坛会员角色组Dao实现类<br/>
 */

@Repository
public class BbsMemberRoleDaoImpl extends MockDao<BbsMemberRoleEO> implements IBbsMemberRoleDao {

    @Override
    public BbsMemberRoleEO getMemberRoleByPoints(Integer memberPoints, Long siteId) {
        StringBuffer hql = new StringBuffer("from BbsMemberRoleEO m where m.recordStatus = ? and riches <=? and siteId = ? order  by riches desc");
        return getEntityByHql(hql.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),memberPoints,siteId});
    }

    @Override
    public List<BbsMemberRoleEO> BbsMemberRoleList(Long siteId) {
        String hql = "from BbsMemberRoleEO m where m.recordStatus = 'Normal' and siteId = ? order  by riches desc";
        return getEntitiesByHql(hql,new Object[]{siteId});
    }
}
