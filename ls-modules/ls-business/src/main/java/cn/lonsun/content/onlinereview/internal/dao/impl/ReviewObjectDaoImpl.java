package cn.lonsun.content.onlinereview.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.content.onlinereview.internal.dao.IReviewObjectDao;
import cn.lonsun.content.onlinereview.internal.entity.ReviewObjectEO;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;

@Repository
public class ReviewObjectDaoImpl extends BaseDao<ReviewObjectEO> implements IReviewObjectDao{

	@Override
	public Pagination getPage(ReviewQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from ReviewObjectEO where columnId = ? and siteId = ?");
		values.add(query.getColumnId());
		values.add(query.getSiteId());
		if(!StringUtils.isEmpty(query.getSearchText())){
			hql.append(" and name like ?");
			values.add("%".concat(query.getSearchText()).concat("%"));
		}
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray());
	}

	@Override
	public List<ReviewObjectEO> getReviewObjects(Long columnId, Long siteId) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from ReviewObjectEO where  isShow = 1 and columnId = ? and siteId = ?");
		values.add(columnId);
		values.add(siteId);
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

}
