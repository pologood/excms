package cn.lonsun.rbac.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IOrganCodeDao;
import cn.lonsun.rbac.internal.entity.OrganCodeEO;

@Repository
public class OrganCodeDaoImpl extends BaseDao<OrganCodeEO> implements IOrganCodeDao {

	@Override
	public Pagination getPagination(PageQueryVO query) {
		String hql = "from OrganCodeEO oc order by oc.createDate desc";
		return getPagination(query.getPageIndex(), query.getPageSize(), hql, new Object[]{});
	}

}
