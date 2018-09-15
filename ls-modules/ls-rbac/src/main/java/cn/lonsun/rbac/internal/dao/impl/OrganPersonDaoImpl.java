package cn.lonsun.rbac.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.dao.IOrganPersonDao;
import cn.lonsun.rbac.internal.entity.OrganPersonEO;

@Repository("organPersonDao")
public class OrganPersonDaoImpl extends MockDao<OrganPersonEO> implements IOrganPersonDao {
	
	@Override
	public void deleteByOrganIds(List<Long> organIds){
		StringBuffer hql = new StringBuffer("update OrganPersonEO op set op.recordStatus=? where 1=1 and");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Removed.toString());
		hql.append(" (1=0 or");
		for(Long organId:organIds){
			hql.append(" or op.organId=?");
			values.add(organId);
		}
		hql.append(") and op.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		executeUpdateByHql(hql.toString(), values.toArray());
	}

	@Override
	public List<OrganPersonEO> getOrganPersonsByPersonId(Long personId) {
		String hql = "from OrganPersonEO ou where ou.personId=? and ou.recordStatus=?";
		return getEntitiesByHql(hql, new Object[]{personId,AMockEntity.RecordStatus.Normal.toString()});
	}

	@Override
	public List<OrganPersonEO> getOrganPersonsByOrganId(Long organId) {
		String hql = "from OrganPersonEO ou where ou.organId=? and ou.recordStatus=?";
		return getEntitiesByHql(hql, new Object[]{organId,AMockEntity.RecordStatus.Normal.toString()});
	}

	@Override
	public void updateOrganName(Long organId, String organName) {
		String hql = "update PersonEO p set p.organName=? where p.organId=? and p.recordStatus=?";
		Query query = getCurrentSession().createQuery(hql);
		setParameters(new Object[]{organName,organId,AMockEntity.RecordStatus.Normal.toString()}, query);
		query.executeUpdate();
	}
}
