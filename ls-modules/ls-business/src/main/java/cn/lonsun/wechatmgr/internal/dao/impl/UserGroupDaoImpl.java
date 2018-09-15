package cn.lonsun.wechatmgr.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IUserGroupDao;
import cn.lonsun.wechatmgr.internal.entity.UserGroupEO;

@Repository("userGroupDao")
public class UserGroupDaoImpl extends MockDao<UserGroupEO> implements IUserGroupDao {

	@Override
	public List<UserGroupEO> getListBySite(Long siteId) {
		String hql="from UserGroupEO where siteId=? and recordStatus='Normal'  order by groupid asc";
		return getEntitiesByHql(hql, new Object[]{siteId});
	}

	@Override
	public Pagination getpage(Long siteId, Long pageIndex, Integer pageSize) {
		String hql="from UserGroupEO where recordStatus='Normal' and siteId=?  order by groupid asc";
		return getPagination(pageIndex, pageSize, hql, new Object[]{siteId});
	}

	@Override
	public void deleteBySite(Long siteId) {
		String hql="delete UserGroupEO where siteId=?";
		executeUpdateByHql(hql, new Object[]{siteId});		
	}
	
}
