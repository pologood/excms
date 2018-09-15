package cn.lonsun.rbac.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IAccessPolicyDao;
import cn.lonsun.rbac.internal.entity.AccessPolicyEO;

@Repository("accessPolicyDao")
public class AccessPolicyDaoImpl extends BaseDao<AccessPolicyEO> implements IAccessPolicyDao {
	
	@Override
	public List<AccessPolicyEO> getPolicys(String blurryIp,Boolean isEnable){
		String hql = "from AccessPolicyEO ap where ap.startIp like ?";
		blurryIp = blurryIp.concat(".%");
		Object[] values = null;
		if(isEnable==null){
			values = new Object[1];
			values[0] = blurryIp;
		}else{
			values = new Object[2];
			values[0] = blurryIp;
			hql.concat(" and ap.isEnable=?");
			values[1] = isEnable;
		}
		return getEntitiesByHql(hql, values);
	}

	@Override
	public List<AccessPolicyEO> getPolicys() {
		String hql = "from AccessPolicyEO p order by p.createDate desc";
		return getEntitiesByHql(hql, new Object[]{});
	}

	@Override
	public Pagination getPage(Long index, Integer size) {
		String hql = "from AccessPolicyEO p order by p.createDate desc";
		return getPagination(index, size, hql, new Object[]{});
	}

	@Override
	public List<AccessPolicyEO> getPolicys(boolean isEnable) {
		String hql = "from AccessPolicyEO p where p.isEnable=? order by p.createDate desc";
		return getEntitiesByHql(hql, new Object[]{isEnable});
	}

}
