package cn.lonsun.rbac.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IPlatformDao;
import cn.lonsun.rbac.internal.entity.PlatformEO;

/**
 * 平台管理ORM接口实现
 *
 * @author xujh
 * @version 1.0
 * 2015年4月24日
 *
 */
@Repository
public class PlatformDaoImpl extends BaseDao<PlatformEO> implements IPlatformDao {

	@Override
	public boolean isCodeExisted(String code,Long exceptId) {
		String hql = "from PlatformEO p where p.code=?";
		Object[] values = null;
		if(exceptId!=null){
			hql = hql+"and p.id!=?";
			values = new Object[]{code,exceptId};
		}else{
			values = new Object[]{code};
		}
		return getCount(hql,values)>0?true:false;
	}

	@Override
	public Pagination getPagination(PageQueryVO query) {
		String hql = "from PlatformEO p";
		return getPagination(query.getPageIndex(), query.getPageSize(), hql, new Object[]{});
	}


}
