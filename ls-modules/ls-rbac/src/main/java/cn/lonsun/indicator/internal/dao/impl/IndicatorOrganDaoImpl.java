package cn.lonsun.indicator.internal.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.dao.IIndicatorOrganDao;
import cn.lonsun.indicator.internal.entity.IndicatorOrganEO;

@Repository
public class IndicatorOrganDaoImpl extends BaseDao<IndicatorOrganEO> implements
		IIndicatorOrganDao {

	@Override
	public int getMaxSortNum() {
		String hql = "select max(io.sortNum) from IndicatorOrganEO io";
		Object obj = getObject(hql, null);
		int sortNum = 0;
		if(obj!=null){
			sortNum = Integer.valueOf(obj.toString());
		}
		return sortNum;
	}

	@Override
	public Pagination getPagination(PageQueryVO query) {
		StringBuffer hql = new StringBuffer("from IndicatorOrganEO io where 1=1");
		if(!StringUtils.isEmpty(query.getSortField())){
			hql.append(" order by io.").append(query.getSortField());
			if(!StringUtils.isEmpty(query.getSortOrder())){
				hql.append(" ").append(query.getSortOrder());
			}
		}
		return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), new Object[]{});
	}
}
