package cn.lonsun.publicInfo.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogUpdateDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogUpdateEO;
import cn.lonsun.publicInfo.vo.PublicCatalogUpdateQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fth on 2017/6/9.
 */
@Repository
public class PublicCatalogUpdateDaoImpl extends BaseDao<PublicCatalogUpdateEO> implements IPublicCatalogUpdateDao {

    private String getHql(PublicCatalogUpdateQueryVO queryVO, Map<String, Object> paramMap) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from PublicCatalogUpdateEO p where 1 = 1 ");
        if (null != queryVO.getOrganId()) {
            hql.append(" and organId = :organId ");
            paramMap.put("organId", queryVO.getOrganId());
        }
        if (null != queryVO.getUserId()) {
            hql.append(" and recUserIds like :userId ");
            paramMap.put("userId", "%" + SqlUtil.prepareParam4Query(queryVO.getUserId().toString()) + "%");
        }
        if (StringUtils.isNotEmpty(queryVO.getWarningType())) {
            hql.append(" and warningType = :warningType ");
            paramMap.put("warningType", queryVO.getWarningType());
        }
        if (null != queryVO.getStartLastPublishDate()) {
            hql.append(" and lastPublishDate >= :startDate ");
            paramMap.put("startDate", queryVO.getStartLastPublishDate());
        }
        if (null != queryVO.getEndLastPublishDate()) {
            hql.append(" and lastPublishDate <= :endDate ");
            paramMap.put("endDate", queryVO.getEndLastPublishDate());
        }
        hql.append(" order by p.organId,p.createDate desc ");
        return hql.toString();
    }

    @Override
    public Pagination getPagination(PublicCatalogUpdateQueryVO queryVO) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String hql = this.getHql(queryVO, paramMap);
        return this.getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql, paramMap);
    }

    @Override
    public int getCountByOrganId(PublicCatalogUpdateQueryVO queryVO) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String hql = this.getHql(queryVO, paramMap);
        Long result = this.getCount(hql, paramMap);
        return null == result ? 0 : result.intValue();
    }

    @Override
    public List<PublicCatalogUpdateEO> getList(PublicCatalogUpdateQueryVO queryVO) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String hql = this.getHql(queryVO, paramMap);
        return getEntitiesByHql(hql, paramMap);
    }

}
