package cn.lonsun.content.onlinereview.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.onlinereview.internal.dao.IReviewInfoDao;
import cn.lonsun.content.onlinereview.internal.entity.ReviewInfoEO;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;

@Repository
public class ReviewInfoDaoImpl extends BaseDao<ReviewInfoEO> implements IReviewInfoDao{

	@Override
	public Pagination getPage(ReviewQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from ReviewInfoEO where columnId = ? and siteId = ?");
		values.add(query.getColumnId());
		values.add(query.getSiteId());
		if(!StringUtils.isEmpty(query.getSearchText())){
			hql.append(" and title like ?");
			values.add("%".concat(query.getSearchText()).concat("%"));
		}
		if(query.getIssued() != null){
			hql.append(" and issued = ?");
			values.add(query.getIssued());
		}
		hql.append(" order by sortNum desc");
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray());
	}

	@Override
	public Long getMaxSortNum(Long siteId,Long columnId) {
		Long maxSortNum = null;
		StringBuffer sb = new StringBuffer(
				"select max(o.sortNum) from ReviewInfoEO as o where o.siteId = ? and o.columnId = ?");
		Query query = getCurrentSession().createQuery(sb.toString()).setParameter(0, siteId).setParameter(1, columnId);
		@SuppressWarnings("rawtypes")
		List list = query.list();
		if (list != null && list.size() > 0) {
			maxSortNum = Long.valueOf(list.get(0) == null ? "0" : list.get(0)
					.toString());
		}
		return maxSortNum;
	}

	@Override
	public ReviewInfoEO getSortReview(Long sortNum, String type) {
		String hql = "from ReviewInfoEO s ";
		if("up".equals(type)){
			hql += "where s.sortNum = (select min(t.sortNum) from ReviewInfoEO t where t.sortNum > ?)";
		}else{
			hql += "where s.sortNum = (select max(t.sortNum) from ReviewInfoEO t where t.sortNum < ?)";
		}
		return getEntityByHql(hql, new Object[]{sortNum});
	}

}
