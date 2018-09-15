package cn.lonsun.publicInfo.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.dao.IPublicClassDao;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.vo.PublicClassMobileVO;
import cn.lonsun.publicInfo.vo.PublicContentRetrievalVO;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zx on 2016-6-27.
 */
@Repository
public class PublicClassDaoImpl extends MockDao<PublicContentEO> implements IPublicClassDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicClassMobileVO> getPublicClassify(PublicContentRetrievalVO vo) {
        StringBuffer hql = new StringBuffer();
        hql.append("select t.id as id,t.parentId as parentId,t.name as name,t.sortNum as sortNum from PublicClassEO t where t.recordStatus = ?");
        if (null != vo.getSiteId()) {
            hql.append(" and t.siteId = " + vo.getSiteId());
        }
        return (List<PublicClassMobileVO>) this.getBeansByHql(hql.toString(), new Object[] { AMockEntity.RecordStatus.Normal.toString() },
                PublicClassMobileVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicClassEO> getChildClass(Long pid, Long siteId) {
        StringBuffer hql = new StringBuffer();
        hql.append("select t.id as id,t.parentId as parentId,t.name as name,t.sortNum as sortNum from PublicClassEO t where t.recordStatus = ?");
        if (null != siteId) {
            // hql.append(" and t.siteId = " + siteId);
        }
        if (null != pid) {
            hql.append(" and t.parentId = " + pid);
        }
        return (List<PublicClassEO>) this.getBeansByHql(hql.toString(), new Object[] { AMockEntity.RecordStatus.Normal.toString() }, PublicClassEO.class);
    }
}