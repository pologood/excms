package cn.lonsun.content.leaderwin.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import cn.lonsun.core.base.entity.AMockEntity;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.leaderwin.internal.dao.ILeaderTypeDao;
import cn.lonsun.content.leaderwin.internal.entity.LeaderTypeEO;
import cn.lonsun.content.leaderwin.vo.LeaderQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;

@Repository
public class LeaderTypeDaoImpl extends BaseDao<LeaderTypeEO> implements ILeaderTypeDao{

	@Override
	public Pagination getPage(LeaderQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from LeaderTypeEO where columnId = ? and siteId = ?");
		values.add(query.getColumnId());
		values.add(query.getSiteId());
		if(!StringUtils.isEmpty(query.getSearchText())){
			hql.append(" and title like ?");
			values.add("%".concat(query.getSearchText().trim()).concat("%"));
		}
		hql.append(" order by sortNum asc");
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray());
	}

	@Override
	public List<LeaderTypeEO> getList(Long siteId,Long columnId) {
		List<Object> values = new ArrayList<Object>();
		String hql = "from LeaderTypeEO where 1=1";
		if(siteId != null){
			hql +=" and siteId = ?";
			values.add(siteId);
		}
		if(columnId != null){
			hql +=" and columnId = ?";
			values.add(columnId);
		}
		hql +=" order by sortNum asc";
		return getEntitiesByHql(hql,values.toArray());
	}

	@Override
	public Long getMaxSortNum(Long siteId, Long columnId) {
		Long maxSortNum = null;
		StringBuffer sb = new StringBuffer("select max(o.sortNum) from LeaderTypeEO o where o.siteId = ? and o.columnId = ?");
		Query query =
				getCurrentSession().createQuery(sb.toString()).setParameter(0, siteId)
						.setParameter(1, columnId);
		@SuppressWarnings("rawtypes")
		List list = query.list();
		if (list != null && list.size() > 0) {
			maxSortNum = Long.valueOf(list.get(0) == null ? "0" : list.get(0).toString());
		}
		return maxSortNum;
	}
}
