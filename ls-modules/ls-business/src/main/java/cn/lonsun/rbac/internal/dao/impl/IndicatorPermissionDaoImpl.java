/*
 * RoleDaoImpl.java         2014年8月26日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.rbac.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.dao.IIndicatorPermissionDao;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.util.LoginPersonUtil;


/**
 *  角色权限dao
 *	 
 * @date     2014年8月26日 
 * @author 	 yy 
 * @version	 v1.0  
 */
@Repository
public class IndicatorPermissionDaoImpl extends MockDao<IndicatorPermissionEO> implements IIndicatorPermissionDao {

    @Override
    public void deleteByRoleAndIndicator(Long roleId) {
        if(null == roleId) {
            return;
        }
        this.delete(getByRoleId(roleId));
    }

    @Override
    public List<IndicatorPermissionEO> getByRoleId(Long roleId) {
        String hql = "from IndicatorPermissionEO t where  t.roleId=? and t.recordStatus=?";
		if(!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
			hql += " and t.siteId=" + LoginPersonUtil.getSiteId();
		}
        return this.getEntitiesByHql(hql, new Object[]{roleId, AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public IndicatorPermissionEO getByRoleAndIndicator(Long roleId, Long right) {
        String hql = "from IndicatorPermissionEO t where  t.roleId=? and t.indicatorId=? and t.recordStatus=?";
        return this.getEntityByHql(hql, new Object[]{roleId,right,AMockEntity.RecordStatus.Normal.toString()});
    }
    
    @Override
    public void deletePermissions(Long roleId,List<Long> indicatorIds){
    	if(roleId==null||roleId<=0){
    		return;
    	}
    	StringBuilder hql = new StringBuilder("update IndicatorPermissionEO ip set ip.recordStatus=? where ip.recordStatus=? ");
    	List<Object> values = new ArrayList<Object>();
    	values.add(AMockEntity.RecordStatus.Removed.toString());
    	values.add(AMockEntity.RecordStatus.Normal.toString());
    	if(indicatorIds!=null&&indicatorIds.size()>0){
    		hql.append(" and (1=0");
    		for(Long indicatorId:indicatorIds){
    			hql.append(" or ip.indicatorId=?");
    			values.add(indicatorId);
    		}
    		hql.append(")");
    	}
		hql.append(" and ip.roleId=?");
		values.add(roleId);
    	executeUpdateByHql(hql.toString(), values.toArray());
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getIndicatorIds(Long roleId) {
		String hql = "select p.indicatorId from IndicatorPermissionEO p where p.roleId=? and p.recordStatus=?";
		List<?> object = getObjects(hql, new Object[]{roleId,AMockEntity.RecordStatus.Normal.toString()});
		return object==null?null:(List<Long>)object;
	}
}

