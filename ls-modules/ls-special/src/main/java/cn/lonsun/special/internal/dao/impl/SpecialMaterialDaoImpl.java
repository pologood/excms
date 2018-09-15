package cn.lonsun.special.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.dao.ISpecialMaterialDao;
import cn.lonsun.special.internal.entity.SpecialMaterialEO;
import cn.lonsun.special.internal.vo.SpecialMaterialQueryVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doocal on 2016-10-15.
 */
@Repository
public class SpecialMaterialDaoImpl extends MockDao<SpecialMaterialEO> implements ISpecialMaterialDao {

    @Override
    public Pagination getPagination(SpecialMaterialQueryVO queryVO) {

        StringBuilder hql = new StringBuilder("from SpecialMaterialEO t where 1=1 ");
        List<Object> values = new ArrayList<Object>();
        if (null != queryVO.getSiteId()) {
            hql.append(" and t.siteId = ? ");
            values.add(queryVO.getSiteId());
        }
        if (null != queryVO.getName()) {
            hql.append(" and t.name like ? ");
            values.add("%" + queryVO.getName() + "%");
        }
        hql.append(" and t.recordStatus = ?");
        hql.append(" order by t.createDate desc ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), values.toArray());
    }
}
