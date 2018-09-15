package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.content.internal.dao.IBaseContentUpdateDao;
import cn.lonsun.content.internal.entity.BaseContentUpdateEO;
import cn.lonsun.content.vo.BaseContentUpdateQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuk on 2017/6/30.
 */
@Repository
public class BaseContentUpdateDaoImpl extends BaseDao<BaseContentUpdateEO> implements IBaseContentUpdateDao {

    private String getHql(BaseContentUpdateQueryVO queryVO, Map<String, Object> paramMap) {
        StringBuffer hql = new StringBuffer();
        hql.append(" from BaseContentUpdateEO p where 1 = 1 ");
        if (null != queryVO.getColumnId()) {
            hql.append(" and columnId = :columnId ");
            paramMap.put("columnId", queryVO.getColumnId());
        }
        if (null != queryVO.getSiteId()) {
            hql.append(" and siteId = :siteId ");
            paramMap.put("siteId", queryVO.getSiteId());
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
        hql.append(" order by p.columnId,p.createDate desc ");
        return hql.toString();
    }

    @Override
    public Pagination getPagination(BaseContentUpdateQueryVO queryVO) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String hql = this.getHql(queryVO, paramMap);
        return this.getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql, paramMap);
    }

    @Override
    public int getCountByColumnId(BaseContentUpdateQueryVO queryVO) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String hql = this.getHql(queryVO, paramMap);
        Long result = this.getCount(hql, paramMap);
        return null == result ? 0 : result.intValue();
    }

    @Override
    public List<BaseContentUpdateEO> getList(BaseContentUpdateQueryVO queryVO) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String hql = this.getHql(queryVO, paramMap);
        return getEntitiesByHql(hql, paramMap);
    }

}
