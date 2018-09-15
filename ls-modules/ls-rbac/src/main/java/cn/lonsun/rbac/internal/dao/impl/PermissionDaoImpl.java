package cn.lonsun.rbac.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.dao.IPermissionDao;
import cn.lonsun.rbac.internal.entity.PermissionEO;

@Repository("permissionDao")
public class PermissionDaoImpl extends MockDao<PermissionEO> implements
		IPermissionDao {
	
	@Override
	public boolean hasPermission(List<Long> roleIds,String uri){
		StringBuffer hql = new StringBuffer("select count(p) from PermissionEO p,ResourceEO r where 1=1");
		List<Object> values = new ArrayList<Object>();
		for(int i=0;i<roleIds.size();i++){
			if(i==0){
				hql.append(" and (p.roleId=?");
			}else{
				hql.append(" or p.roleId=?");
			}
			if(i==roleIds.size()-1){
				hql.append(")");
			}
			values.add(roleIds.get(i));
		}
		hql.append(" and r.uri=?");
		values.add(uri);
		hql.append(" and r.resourceId=p.resourceId");
		hql.append(" and p.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getCount(hql.toString(), values.toArray())>0?true:false;
	}

    @Override
    public List<PermissionEO> getPermissionByRole(Long roleId) {
        String hql = "from PermissionEO t where t.roleId = ? and t.recordStatus=?";
        return getEntitiesByHql(hql, new Object[] {roleId, AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public PermissionEO getByRoleAndResource(Long roleId, Long resourceId) {
        String hql = "from PermissionEO t where t.roleId = ? and t.resourceId=? and t.recordStatus=?";
        return getEntityByHql(hql, new Object[] {roleId,resourceId,AMockEntity.RecordStatus.Normal.toString()});
    }



}
