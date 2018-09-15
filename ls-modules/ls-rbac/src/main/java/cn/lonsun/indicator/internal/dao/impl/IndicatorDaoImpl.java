/*
 * IconDaoImpl.java         2014年8月19日 <br/>
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

package cn.lonsun.indicator.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.dao.IIndicatorDao;
import cn.lonsun.indicator.internal.entity.IndicatorEO;

/**
 * 指示器dao
 * 
 * @date 2014年8月19日
 * @author yy
 * @version v1.0
 */
@Repository
public class IndicatorDaoImpl extends MockDao<IndicatorEO> implements
		IIndicatorDao {
	@Override
	public String getWebServiceHostById(Long indicatorId){
		String hql = "select i.webServiceHost from IndicatorEO i where i.indicatorId=? and i.recordStatus=?";
		Object obj = getObject(hql, new Object[]{indicatorId,RecordStatus.Normal.toString()});
		return obj==null?null:obj.toString();
	}
	
	@Override
	public List<IndicatorEO> getSubIndicatorByType(Long parentId,String type,Boolean isEnable){
		StringBuffer hql = new StringBuffer("from IndicatorEO where 1=1 ");
		List<Object> values = new ArrayList<Object>();
		if(parentId==null){
			hql.append(" and parentId is null");
		}else{
			hql.append(" and parentId=?");
			values.add(parentId);
		}
		if(!StringUtils.isEmpty(type)){
			hql.append(" and type=?");
			values.add(type);
		}
		if(isEnable!=null){
			hql.append(" and isEnable=?");
			values.add(isEnable);
		}
		hql.append(" and recordStatus=? order by sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}
	
	@Override
	public List<Long> getIndicators4User(Long unitId,Long userId,String[] types){
		StringBuffer sql = new StringBuffer("select distinct(ip.indicator_id)  from rbac_role_assignment ra, rbac_indicator_permission ip  where  ra.user_id = ?"); 
		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		if(unitId!=null){
			sql.append(" and ra.unit_id = ?"); 
			values.add(unitId);
		}
		sql.append(" and ra.role_id = ip.role_id");
		sql.append(" and ra.record_status=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		sql.append(" and ip.record_status=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(types!=null&&types.length>0){
			sql.append(" and (1=0 ");
			for(String type:types){
				if(!StringUtils.isEmpty(type)){
					sql.append(" or ip.indicator_type=?");
					values.add(type);
				}
			}
			sql.append(")");
		}
		List<String> fields = new ArrayList<String>();
		fields.add("indicator_id");
		List<Object> objs = getObjectsBySql(sql.toString(), values.toArray(),fields);
		List<Long> indicatorIds = null;
		if(objs!=null&&objs.size()>0){
			indicatorIds = new ArrayList<Long>(objs.size());
			for(Object obj:objs){
				if(obj!=null){
					indicatorIds.add(Long.valueOf(obj.toString()));
				}
			}
		}
		return indicatorIds;
	}
	
	/**
	 * 为开发商获取Indicators
	 *
	 * @param parentId
	 * @param type
	 * @param isOwnedByDeveloper
	 * @return
	 */
	public List<IndicatorEO> getIndicator4Developer(Long parentId,String type){
		StringBuilder hql = new StringBuilder("from IndicatorEO i where i.type=?");
		List<Object> values = new ArrayList<Object>();
		values.add(type);
		if(!type.equals(IndicatorEO.Type.Shortcut.toString())){
			hql.append(" and i.isShown4Developer=?");
			values.add(true);
		}
		if(parentId==null||parentId<0){
			hql.append(" and i.parentId is null");
		}else{
			hql.append(" and i.parentId=?");
			values.add(parentId);
		}
		hql.append(" and i.recordStatus=? order by i.sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}
	
	/**
	 * 为系统超管获取Indicators
	 *
	 * @param parentId
	 * @param type
	 * @return
	 */
	public List<IndicatorEO> getIndicator4Admin(Long parentId,String type){
		StringBuilder hql = new StringBuilder("from IndicatorEO i where i.type=?");
		List<Object> values = new ArrayList<Object>();
		values.add(type);
		if(!type.equals(IndicatorEO.Type.Shortcut.toString())){
			hql.append(" and i.isShown4Admin=?");
			values.add(true);
		}
		if(parentId==null||parentId<0){
			hql.append(" and i.parentId is null");
		}else{
			hql.append(" and i.parentId=?");
			values.add(parentId);
		}
		hql.append(" and i.recordStatus=? order by i.sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}
	
	@Override
	public List<IndicatorEO> getIndicator4DeveloperAndSuperAdmin(Long parentId,String type,boolean isOwnedByDeveloper){
		StringBuilder hql = new StringBuilder("from IndicatorEO i where i.type=?");
		List<Object> values = new ArrayList<Object>();
		values.add(type);
		if(!type.equals(IndicatorEO.Type.Shortcut.toString())){
			hql.append(" and i.isOwnedByDeveloper=?");
			values.add(isOwnedByDeveloper);
		}
		if(parentId==null||parentId<0){
			hql.append(" and i.parentId is null");
		}else{
			hql.append(" and i.parentId=?");
			values.add(parentId);
		}
		hql.append(" and i.recordStatus=? order by i.sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public Pagination getPage() {
		String hql = "from IndicatorEO t where t.recordStatus=? order by t.sortNum  ";
		return (Pagination) this.getPagination(Long.valueOf(1),
				Integer.valueOf(10), hql,
				new Object[] { AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<IndicatorEO> getAllStieInfo() {
		String hql = "from IndicatorEO i where i.recordStatus=? and i.type=? order by i.sortNum asc";
		return getEntitiesByHql(hql, new Object[]{RecordStatus.Normal.toString(),IndicatorEO.Type.CMS_Site.toString()});
	}

	@Override
	public List<IndicatorEO> getTree(Long parentId, Integer isEnable) {
		String hql = "from IndicatorEO t where t.parentId = ? and t.recordStatus=?  ";
		if (null != isEnable && isEnable == 1) {
			hql += "and t.isEnable=1 ";
		}
		hql += "order by t.sortNum ";
		return this.getEntitiesByHql(hql, new Object[] { parentId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<IndicatorEO> getMenu(Long parentId, String[] types,
			Integer isEnable) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from IndicatorEO t where t.parentId = ? and t.recordStatus=?");
		values.add(parentId);
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if (null != isEnable && isEnable == 1) {
			hql.append(" and t.isEnable=1");
		}
		if(types!=null&&types.length>0){
			hql.append(" and (t.type=?");
			values.add(types[0]);
			for(int i=1;i<types.length;i++){
				hql.append(" or t.type=?");
				values.add(types[i]);
			}
			hql.append(")");
		}
		hql.append("order by t.sortNum ");
		return this.getEntitiesByHql(hql.toString(),values.toArray());
	}

	@Override
	public List<IndicatorEO> getSuperParent() {
		String hql = "from IndicatorEO t where t.parentId = ? and t.recordStatus=? order by t.sortNum  ";
		return this.getEntitiesByHql(hql, new Object[] { 0,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<IndicatorEO> getByType(String type, Integer isEnable) {
		String hql = "from IndicatorEO t where t.type=?  and t.recordStatus=? ";
		if (null != isEnable && isEnable == 1) {
			hql += "and t.isEnable=1 ";
		}
		hql += "order by t.sortNum ";
		return getEntitiesByHql(hql, new Object[] { type,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public Integer getMaxSortNum(String type, Long parentId) {
		String hql = "select max(t.sortNum) from IndicatorEO t where  t.recordStatus=? ";
		if (parentId == null||parentId<=0) {
			hql += " and (t.parentId=" + parentId + " or t.parentId is null )";
		} else {
			hql += " and t.parentId=" + parentId;
		}
		if (type.equals(IndicatorEO.Type.Menu.toString())) {
			hql += " and t.type='" + IndicatorEO.Type.Menu.toString() + "'";
		} else if (type.equals(IndicatorEO.Type.ToolBarButton.toString())) {
			hql += " and (t.type='" + IndicatorEO.Type.ToolBarButton.toString()
					+ "' or t.type='" + IndicatorEO.Type.Other.toString()
					+ "')";
		}
		return (Integer) getObject(hql,
				new Object[] { AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<IndicatorEO> getButtonByParentId(Long parentId) {
		String hql = "from IndicatorEO t where t.parentId=? and (t.type ='ToolBarButton' or t.type ='Other')  and t.recordStatus=? order by t.sortNum ";
		return getEntitiesByHql(hql, new Object[] { parentId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<IndicatorEO> getIndicatorsByRole(Long roleId, Long parentId,
			String[] types) {
		StringBuffer hql = new StringBuffer(4);
		List<Object> list = new ArrayList<Object>();
		hql.append("select i from IndicatorEO i,IndicatorPermissionEO ip");
		hql.append(" where i.indicatorId=ip.indicatorId and ip.roleId=? and i.recordStatus=? and ip.recordStatus=?");
		list.add(roleId);
		list.add(AMockEntity.RecordStatus.Normal.toString());
		list.add(AMockEntity.RecordStatus.Normal.toString());
		if (parentId != null) {
			hql.append(" and i.parentId=?");
			list.add(parentId);
		} else {
			hql.append(" and i.parentId is null");
		}
		if (types != null && types.length > 0) {
			hql.append("(i.type=?");
			list.add(types[0]);
			for (int i = 1; i < types.length; i++) {
				hql.append(" or i.type=?");
				list.add(types[i]);
			}
			hql.append(")");
		}
		hql.append("order by i.sortNum");
		return getEntitiesByHql(hql.toString(), list.toArray());
	}

	@Override
	public List<IndicatorEO> getIndicatorsByRole(Long[] roleIds, Long parentId,String type,Boolean isShowInDesktop,Boolean isExternal) {
		StringBuffer hql = new StringBuffer(4);
		List<Object> list = new ArrayList<Object>();
		hql.append("select i from IndicatorEO i,IndicatorPermissionEO ip");
		hql.append(" where i.indicatorId=ip.indicatorId");
//		if(isShown4SystemManager!=null){
//			hql.append(" and i.isShown4SystemManager=?");
//			list.add(isShown4SystemManager);
//		}
		if(roleIds!=null&&roleIds.length>0){
			hql.append(" and (");
			for (int i = 0; i < roleIds.length; i++) {
				Long roleId = roleIds[i];
				if (i == 0) {
					hql.append(" ip.roleId=?");
				} else {
					hql.append(" or ip.roleId=?");
				}
				list.add(roleId);
			}
			hql.append(" )");
		}
		if (parentId != null && parentId > 0) {
			hql.append(" and i.parentId=?");
			list.add(parentId);
		} else {
			hql.append(" and i.parentId is null");
		}
		if (!StringUtils.isEmpty(type)) {
			hql.append(" and i.type=?");
			list.add(type);
		}
		if(isShowInDesktop!=null){
			hql.append(" and i.isShowInDesktop=? ");
			list.add(isShowInDesktop);
		}
		//外部用户是否显示，isExternal==true，表示外部用户，此时只获取外部用户可见的记录
		if(isExternal!=null&&isExternal){
			hql.append(" and i.isShown4ExternalUser =? ");
			list.add(isExternal);
		}
		hql.append(" and i.recordStatus=? and i.isEnable=? and ip.recordStatus=?");
		list.add(AMockEntity.RecordStatus.Normal.toString());
		list.add(Boolean.TRUE);
		list.add(AMockEntity.RecordStatus.Normal.toString());
		hql.append(" order by i.sortNum");
		return getEntitiesByHql(hql.toString(), list.toArray());
	}
	
	public List<IndicatorEO> getIndicators4SystemManager(Long[] roleIds,Long parentId,String type){
		StringBuffer hql = new StringBuffer(4);
		List<Object> list = new ArrayList<Object>();
		hql.append("select i from IndicatorEO i,IndicatorPermissionEO ip");
		//超级管理员不显示的，系统管理员也不显示
		hql.append(" where i.indicatorId=ip.indicatorId and i.isShown4Admin!=?");
		list.add(false);
		hql.append(" and (");
		for (int i = 0; i < roleIds.length; i++) {
			Long roleId = roleIds[i];
			if (i == 0) {
				hql.append(" ip.roleId=?");
			} else {
				hql.append(" or ip.roleId=?");
			}
			list.add(roleId);
		}
		hql.append(" )");
		if (parentId != null && parentId > 0) {
			hql.append(" and i.parentId=?");
			list.add(parentId);
		} else {
			hql.append(" and i.parentId is null");
		}
		if (!StringUtils.isEmpty(type)) {
			hql.append(" and i.type=?");
			list.add(type);
		}
		hql.append(" and i.recordStatus=? and i.isEnable=? and ip.recordStatus=?");
		list.add(AMockEntity.RecordStatus.Normal.toString());
		list.add(Boolean.TRUE);
		list.add(AMockEntity.RecordStatus.Normal.toString());
		hql.append(" order by i.sortNum");
		return getEntitiesByHql(hql.toString(), list.toArray());
	}

	@Override
	public List<IndicatorEO> getDesktopIndicators(Long[] roleIds, String[] codes) {
		StringBuffer hql = new StringBuffer(4);
		List<Object> list = new ArrayList<Object>();
		hql.append("select i from IndicatorEO i,IndicatorPermissionEO ip");
		hql.append(" where i.indicatorId=ip.indicatorId");
		hql.append(" and (");
		for (int i = 0; i < roleIds.length; i++) {
			Long roleId = roleIds[i];
			if (i == 0) {
				hql.append(" ip.roleId=?");
			} else {
				hql.append(" or ip.roleId=?");
			}
			list.add(roleId);
		}
		hql.append(") and (");
		for (int i = 0; i < codes.length; i++) {
			String code = codes[i];
			if (i == 0) {
				hql.append(" i.code=?");
			} else {
				hql.append(" or i.code=?");
			}
			list.add(code);
		}
		hql.append(") and i.recordStatus=? and ip.recordStatus=?");
		list.add(AMockEntity.RecordStatus.Normal.toString());
		list.add(AMockEntity.RecordStatus.Normal.toString());
		hql.append(" order by i.sortNum");
		return getEntitiesByHql(hql.toString(), list.toArray());
	}

	@Override
	public List<IndicatorEO> getSystemIndicators(Long parentId, String[] types,Boolean isEnable) {
		StringBuffer hql =  new StringBuffer("from IndicatorEO i where");
		List<Object> values = new ArrayList<Object>();
		if(parentId==null){
			//如果获取快捷方式，那么只取系统管理
			hql.append(" i.parentId is null and i.systemCode=?");
			values.add(SystemCodes.systemMgr.toString());
		}else{
			hql.append(" i.parentId=?");
			values.add(parentId);
		}
		if(isEnable!=null){
			hql.append(" and i.isEnable=?");
			values.add(isEnable);
		}
		if(types!=null&&types.length>0){
			hql.append(" and (i.type=?");
			values.add(types[0]);
			for(int i=1;i<types.length;i++){
				hql.append(" or i.type=?");
				values.add(types[i]);
			}
			hql.append(")");
		}
		hql.append(" and i.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public List<IndicatorEO> getAppIndicators(Long parentId, String[] types,Boolean isEnable) {
		StringBuffer hql =  new StringBuffer("from IndicatorEO i where");
		List<Object> values = new ArrayList<Object>(4);
		if(parentId==null){
			//如果获取快捷方式，那么不取系统管理
			hql.append(" i.parentId is null and i.systemCode!=?");
			values.add(SystemCodes.systemMgr.toString());
		}else{
			hql.append(" i.parentId=?");
			values.add(parentId);
		}
		if(isEnable!=null){
			hql.append(" and i.isEnable=?");
			values.add(isEnable);
		}
		//应用使用者是否可见
		hql.append(" and i.isShown4User=?");
		values.add(true);
		if(types!=null&&types.length>0){
			hql.append(" and (i.type=?");
			values.add(types[0]);
			for(int i=1;i<types.length;i++){
				hql.append(" or i.type=?");
				values.add(types[i]);
			}
			hql.append(")");
		}
		hql.append(" and i.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}
	
	@Override
	public List<IndicatorEO> getEnableIndicators(Long parentId,String[] types,Boolean isShown4Developer,Boolean isShown4Admin,Boolean isShown4SystemManager){
		StringBuffer hql =  new StringBuffer("from IndicatorEO i where 1=1");
		List<Object> values = new ArrayList<Object>(4);
		if(parentId!=null){
			hql.append(" and i.parentId=?");
			values.add(parentId);
		}
		hql.append(" and i.isEnable=?");
		values.add(true);
		if(isShown4Developer!=null){
			hql.append(" and i.isShown4Developer=?");
			values.add(isShown4Developer);
		}
		if(isShown4Admin!=null){
			hql.append(" and i.isShown4Admin=?");
			values.add(isShown4Admin);
		}
		if(isShown4SystemManager!=null){
			hql.append(" and i.isShown4SystemManager=?");
			values.add(isShown4SystemManager);
		}
		if(types!=null&&types.length>0){
			hql.append(" and (i.type=?");
			values.add(types[0]);
			for(int i=1;i<types.length;i++){
				hql.append(" or i.type=?");
				values.add(types[i]);
			}
			hql.append(")");
		}
		hql.append(" and i.recordStatus=? order by i.sortNum");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public long getCountByParentId(Long parentId,Boolean isEnable) {
		StringBuilder sb = new StringBuilder("from IndicatorEO i where i.parentId=?");
		List<Object> values =  new ArrayList<Object>(3);
		values.add(parentId);
		//为null就忽略该条件
		if(isEnable!=null){
			sb.append(" and i.isEnable=?");
			values.add(isEnable);
		}
		sb.append(" and i.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getCount(sb.toString(), values.toArray());
	}

	@Override
	public IndicatorEO getIndicator(String type, String systemCode) {
		String hql = "from IndicatorEO i where i.recordStatus=? and i.type=? and i.systemCode=?";
		return getEntityByHql(hql,new Object[]{RecordStatus.Normal.toString(),type,systemCode});
	}

	@Override
	public List<IndicatorEO> getAllShortcuts() {
		String hql = "from IndicatorEO i where i.recordStatus=? and i.type=?";
		return getEntitiesByHql(hql, new Object[]{RecordStatus.Normal.toString(),IndicatorEO.Type.Shortcut.toString()});
	}

	@Override
	public List<IndicatorEO> getIndexIndicators(Long parentId) {
		String hql = "from IndicatorEO i where i.recordStatus=? and i.parentId=?";
		return getEntitiesByHql(hql,new Object[]{RecordStatus.Normal.toString(),parentId});
	}
	@Override
	public List<IndicatorEO> getByParentIdsLike(String parentId){
		String hql = "from IndicatorEO i where i.recordStatus=? and concat(concat(',',i.parentIds),',') like ? ";
		return getEntitiesByHql(hql, new Object[]{RecordStatus.Normal.toString(), "%,"+parentId+",%" });
	}

}
