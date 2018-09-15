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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.RoleAssignmentVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IRoleAssignmentDao;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;

/**
 * 角色绑定Dao
 * 
 * @author xujh
 * 
 */
@Repository("roleAssignmentDao")
public class RoleAssignmentDaoImpl extends MockDao<RoleAssignmentEO> implements
IRoleAssignmentDao {

	/**
	 * 获取用户(userId)拥有管理菜单(indicatorId)的角色ID集合
	 *
	 * @param userId
	 * @param indicatorId
	 * @return
	 */
	public List<Long> getRoleIds(Long userId,Long indicatorId){
		String hql = "select distinct(ra.roleId) from RoleAssignmentEO ra,IndicatorPermissionEO ip where ra.roleId= ip.roleId and ra.userId=? and ip.indicatorId=? and ra.roleType=? and ra.recordStatus=? and ip.recordStatus=?";
		List<?> objects = getObjects(hql, new Object[]{userId,indicatorId,RoleEO.Type.System.toString(),RecordStatus.Normal.toString(),RecordStatus.Normal.toString()});
		List<Long> roleIds = null;
		if(objects!=null&&objects.size()>0){
			roleIds = new ArrayList<Long>(objects.size());
			for(Object object:objects){
				Long roleId = Long.valueOf(object.toString());
				roleIds.add(roleId);
			}
		}
		return roleIds;
	}

	/**
	 * 更新角色赋予关系中的organId
	 *
	 * @param srcOrganId
	 * @param userId
	 * @param newOrganId
	 */
	@Override
	public void updateOrganId(Long srcOrganId,Long userId,Long newOrganId){
		String hql = "update RoleAssignmentEO ra set ra.organId=? where ra.organId=? and ra.userId=? and ra.recordStatus=?";
		executeUpdateByHql(hql, new Object[]{newOrganId,srcOrganId,userId,RecordStatus.Normal.toString()});
	}
	/**
	 * 删除部门OrganId下的用户userId的角色赋予关系
	 *
	 * @param organId
	 * @param userId
	 */
	public void delete(Long organId,Long userId){
		String hql = "delete RoleAssignmentEO ra where ra.organId=? and ra.userId=? and ra.recordStatus=?";
		executeUpdateByHql(hql, new Object[]{organId,userId,AMockEntity.RecordStatus.Normal.toString()});
	}
	@Override
	public List<RoleAssignmentVO> getRoleAssignments(Long[] unitIds,Long[] roleIds){
		StringBuffer hql = new StringBuffer("select ra,p.name,p.organName,p.unitName,p.mobile from RoleAssignmentEO ra,PersonEO p where ra.recordStatus=? and (1=0");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		int length = unitIds.length;
		for(int i=0;i<length;i++){
			Long unitId = unitIds[i];
			if(unitId!=null){
				Long roleId = roleIds[i];
				if(roleId!=null){
					hql.append(" or (ra.unitId=? and ra.roleId=?)");
					values.add(unitId);
					values.add(roleId);
				}
			}
		}
		hql.append(")");
		hql.append(" and ra.userId=p.userId and ra.organId=p.organId");
		List<?> list = getObjects(hql.toString(), values.toArray());
		List<RoleAssignmentVO> vos = null;
		if(list!=null&&list.size()>0){
			//构造返回内容
			vos = new ArrayList<RoleAssignmentVO>(list.size());
			for(Object obj:list){
				RoleAssignmentVO vo = new RoleAssignmentVO();
				Object[] targets = (Object[])obj;
				Object target0 = targets[0];
				if(target0!=null){
					RoleAssignmentEO eo = (RoleAssignmentEO)target0;
					BeanUtils.copyProperties(eo, vo);
				}
				//p.name,p.organName,p.unitName,p.mobile
				Object target1 = targets[1];
				if(target1!=null){
					vo.setPersonName(target1.toString()); 
				}
				Object target2 = targets[2];
				if(target2!=null){
					vo.setOrganName(target2.toString()); 
				}
				Object target3 = targets[3];
				if(target3!=null){
					vo.setUnitName(target3.toString()); 
				}
				Object target4 = targets[4];
				if(target4!=null){
					vo.setMobile(target4.toString()); 
				}
				vos.add(vo);
			}
		}
		return vos;
	}

	@Override
	public List<RoleAssignmentEO> getAssignmentsByUnitIdAndRoleId(Long[] unitIds,Long[] roleIds){
		StringBuffer hql = new StringBuffer("from RoleAssignmentEO ra where ra.recordStatus=? and (1=0");
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		int length = unitIds.length;
		for(int i=0;i<length;i++){
			Long unitId = unitIds[i];
			if(unitId!=null){
				Long roleId = roleIds[i];
				if(roleId!=null){
					hql.append(" or (ra.unitId=? and ra.roleId=?)");
					values.add(unitId);
					values.add(roleId);
				}
			}
		}
		hql.append(")");
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public List<RoleAssignmentEO> getRoleAssignments(String[] types,String blurryName){
		String hql = "from RoleAssignmentEO ra where ra.roleType in (:roleTypes) and ra.roleName like (:roleName) and ra.recordStatus=(:recordStatus)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleTypes", types);
		params.put("roleName", blurryName);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql, params);
	}

	@Override
	public List<RoleAssignmentEO> getRoleAssignments(Long roleId) {
		String hql = "from RoleAssignmentEO ra where ra.roleId=? and ra.recordStatus=?";
		return getEntitiesByHql(hql, new Object[] { roleId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public Pagination getPageRoleAssignments(PageQueryVO vo, Long roleId) {
		String hql = "from RoleAssignmentEO ra where ra.roleId=? and ra.recordStatus=? order by createDate desc";
		return getPagination(vo.getPageIndex(),vo.getPageSize(),hql, new Object[]{roleId,
			AMockEntity.RecordStatus.Normal.toString()});
	}

	@Override
	public boolean hasAssignmented(Long roleId) {
		String hql = "from RoleAssignmentEO ra where ra.roleId=? and ra.recordStatus=?";
		return getCount(hql, new Object[] { roleId,
				AMockEntity.RecordStatus.Normal.toString() }) > 0;
	}

	@Override
	public List<RoleAssignmentEO> getAssignments(Long organId, Long userId) {
		String hql = "from RoleAssignmentEO ra where  ra.userId=? and ra.recordStatus=? ";
		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(organId!=null){
			hql += " and ra.organId=? "	;
			values.add(organId);
		}
		hql += " order by ra.roleAssignmentId asc";

		return getEntitiesByHql(hql, values.toArray());
	}

	@Override
	public List<RoleAssignmentEO> getAssignments(Long userId) {
		String hql = "from RoleAssignmentEO ra where ra.userId=? and ra.recordStatus=?";
		return getEntitiesByHql(hql, new Object[] { userId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<RoleAssignmentEO> getUnitAssignments(Long unitId) {
		String hql = "from RoleAssignmentEO ra where ra.roleAssignmentId in (select max(t.roleAssignmentId) from RoleAssignmentEO t where t.unitId=? and t.recordStatus=? group by t.roleId)";
		return getEntitiesByHql(hql, new Object[]{unitId,AMockEntity.RecordStatus.Normal.toString()});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getOrganIdsWhichIsAssignedRoles(List<Long> organIds) {
		StringBuffer hql = new StringBuffer("select distinct(ra.unitId) from RoleAssignmentEO ra where 1=1");
		int length = organIds.size();
		List<Object> values = new ArrayList<Object>(length+1);
		for(int i=0;i<length;i++){
			Long organId = organIds.get(i);
			if(i==0){
				hql.append(" and (ra.unitId=?");
			}else{
				hql.append(" or ra.unitId=?");
			}
			values.add(organId);
			if(i==length-1){
				hql.append(")");
			}
		}

		hql.append(" and ra.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		List<?> list = getObjects(hql.toString(),values.toArray());
		return (List<Long>)list;
	}

	@Override
	public List<RoleAssignmentEO> getAllassignments(List<?> persons) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from RoleAssignmentEO where recordStatus = ?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(persons!=null&&persons.size()>0){
			hql.append(" and (");
			for(int i=0;i<persons.size();i++){
				Object[] array = (Object[])persons.get(i);
				Long userId = array[2]==null?null:Long.valueOf(array[2].toString());
				Long userOrganId = array[11]==null?null:Long.valueOf(array[11].toString());
				if(i > 0){
					hql.append(" or ");
				}
				if(userId != null && userOrganId != null){
					hql.append("(userId = ? and organId = ?)");
					values.add(userId);
					values.add(userOrganId);
				}else{
					hql.append(" (1 = 1)");
				}
			}
			hql.append(")");
		}
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

}
