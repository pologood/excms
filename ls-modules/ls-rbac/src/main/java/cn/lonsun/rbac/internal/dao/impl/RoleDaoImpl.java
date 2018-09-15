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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.rbac.internal.dao.IRoleDao;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.vo.RolePaginationQueryVO;

/**
 * 角色管理ORM接口实现
 *
 * @author xujh
 * @version 1.0 2015年1月7日
 *
 */
@Repository
public class RoleDaoImpl extends MockDao<RoleEO> implements IRoleDao {

    /**
     * 根据角色关联关系中主角色ID为roleId获取角色类型为type的角色集合
     *
     * @param roleId
     * @param type
     * @return
     */
    public List<RoleEO> getRolesByRoleRelation(List<Long> roleIds, String type) {
        String hql =
                "select r from RoleEO r,RoleRelationEO rr where r.roleId=rr.targetRoleId and rr.roleId in (:roleIds) and r.type=(:type) and rr.recordStatus=(:recordStatus1) and r.recordStatus=(:recordStatus2)";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleIds", roleIds);
        params.put("type", type);
        params.put("recordStatus1", AMockEntity.RecordStatus.Normal.toString());
        params.put("recordStatus2", AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(hql, params);
    }

    @Override
    public List<RoleEO> getRoles(Long organId, Long userId, Long businessTypeId) {
        String hql =
                "select r from RoleEO r,RoleAssignmentEO ra where r.recordStatus=? and ra.recordStatus=? and ra.organId=? and ra.userId=? and r.roleId=ra.roleId and r.businessTypeId=?";
        String status = AMockEntity.RecordStatus.Normal.toString();
        Object[] values = new Object[] { status, status, organId, userId, businessTypeId };
        return getEntitiesByHql(hql, values);
    }

    @Override
    public List<RoleEO> getRoles(Long businessTypeId) {
        String hql = "select r from RoleEO r where r.recordStatus=? and r.businessTypeId=?";
        Object[] values = new Object[] { AMockEntity.RecordStatus.Normal.toString(), businessTypeId };
        return getEntitiesByHql(hql, values);
    }

    @Override
    public List<RoleEO> getRolesByScope(String scope) {
        String hql = "select r from RoleEO r where r.recordStatus=? and r.scope=? order by roleId asc";
        Object[] values = new Object[] { AMockEntity.RecordStatus.Normal.toString(), scope };
        return getEntitiesByHql(hql, values);
    }

    @Override
    public List<RoleEO> getRoles(Long organId,Long userId,String scope) {
        String hql = "select r from RoleEO r where r.recordStatus=? and r.scope=? and r.createOrganId = ? and r.createUserId = ?";
        Object[] values = new Object[] { AMockEntity.RecordStatus.Normal.toString(), scope ,organId,userId};
        return getEntitiesByHql(hql, values);
    }

    @Override
    public List<RoleEO> getRoles(List<Long> roleIds, String[] types) {
        String hql = "from RoleEO r where r.roleId in (:roleIds) and r.type in (:types) and r.recordStatus=(:recordStatus)";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleIds", roleIds);
        params.put("types", types);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(hql, params);
    }

    @Override
    public Pagination getPagination(RolePaginationQueryVO query) {
        StringBuffer hql = new StringBuffer("from RoleEO r where 1=1");
        List<Object> values = new ArrayList<Object>();
        if (query.getIsPredefine() != null) {
            hql.append(" and r.isPredefine=? ");
            values.add(query.getIsPredefine());
        }
        if (query.getIsLocked() != null) {
            hql.append(" and r.isLocked=? ");
            values.add(query.getIsLocked());
        }
        String name = query.getName();
        if (!StringUtils.isEmpty(name)) {
            hql.append(" and r.name like ? escape '\\'");
            String target = "%" + SqlUtil.prepareParam4Query(name) + "%";
            values.add(target);
        }
        String code = query.getCode();
        if (!StringUtils.isEmpty(code)) {
            hql.append(" and r.code like ? escape '\\'");
            String target = "%" + SqlUtil.prepareParam4Query(code) + "%";
            values.add(target);
        }
        String searchStr = query.getSearchStr();
        if (!StringUtils.isEmpty(searchStr)) {
            hql.append(" and (r.name like ? escape '\\' or r.code like ? escape '\\' or r.type like ? escape '\\' or r.description like ? escape '\\')");
            String target = "%" + SqlUtil.prepareParam4Query(searchStr) + "%";
            values.add(target);
            values.add(target);
            values.add(target);
            values.add(target);
        }
        hql.append(" and r.recordStatus=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        // 排序
        String sortField = query.getSortField();
        if (!StringUtils.isEmpty(sortField)) {
            hql.append(" order by r.").append(sortField);
            String sortOrder = query.getSortOrder();
            if (!StringUtils.isEmpty(sortOrder)) {
                hql.append(" ").append(sortOrder);
            }
        }
        return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray());
    }

    @Override
    public boolean isCodeExisted(String code) {
        String hql = "from RoleEO t where t.code=? and t.recordStatus=?";
        return this.getCount(hql, new Object[] { code, AMockEntity.RecordStatus.Normal.toString() }) > 0;
    }

    @Override
    public boolean isNameExisted(String name, String type, Long organId) {
        List<Object> values = new ArrayList<Object>(4);
        StringBuffer hql = new StringBuffer("from RoleEO r where r.recordStatus=? and r.type=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(type);
        // 如果为Private,表示为某个组织或单位添加的角色
        if (RoleEO.Type.Private.toString().equals(type)) {
            hql.append(" and r.organId=?");
            values.add(organId);
        }
        hql.append(" and r.name=?");
        values.add(name);
        return this.getCount(hql.toString(), values.toArray()) > 0;
    }

    @Override
    public List<RoleEO> getRoles(String type, Long organId) {
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("from RoleEO t where t.recordStatus=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        if (!StringUtils.isEmpty(type)) {
            hql.append(" and t.type=?");
            values.add(type);
        }
        if (organId != null) {
            hql.append(" and t.organId=?");
            values.add(organId);
        }
        return this.getEntitiesByHql(hql.toString(), values.toArray());
    }

    @Override
    public List<RoleEO> getByBusinessId(Long businessTypeId) {
        String hql = "from RoleEO t where t.businessTypeId=? and t.recordStatus=?";
        return this.getEntitiesByHql(hql, new Object[] { businessTypeId, AMockEntity.RecordStatus.Normal.toString() });
    }

    @Override
    public List<RoleEO> getRoles(Long userId, String type) {
        StringBuffer hql = new StringBuffer(4);
        List<Object> list = new ArrayList<Object>();
        hql.append("select r from RoleEO r,RoleAssignmentEO a");
        hql.append(" where r.roleId=a.roleId and a.userId=? and r.recordStatus=? and a.recordStatus=?");
        list.add(userId);
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(AMockEntity.RecordStatus.Normal.toString());
        if (!StringUtils.isEmpty(type)) {
            hql.append(" and r.type=?");
            list.add(type);
        }
        return getEntitiesByHql(hql.toString(), list.toArray());
    }

    @Override
    public List<RoleEO> getRoles(Long userId, String[] types) {
        StringBuffer hql = new StringBuffer(4);
        List<Object> list = new ArrayList<Object>();
        hql.append("select r from RoleEO r,RoleAssignmentEO a");
        hql.append(" where r.roleId=a.roleId and a.userId=? and r.recordStatus=? and a.recordStatus=?");
        list.add(userId);
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(AMockEntity.RecordStatus.Normal.toString());
        if (types != null && types.length > 0) {
            hql.append(" and (");
            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                if (!StringUtils.isEmpty(type)) {
                    if (i == 0) {
                        hql.append(" r.type=?");
                    } else {
                        hql.append(" or r.type=?");
                    }
                    list.add(type);
                }
            }
            hql.append(")");
        }
        return getEntitiesByHql(hql.toString(), list.toArray());
    }

    @Override
    public List<RoleEO> getRoles(Long userId, Long organId, String[] types) {
        StringBuffer hql = new StringBuffer(4);
        List<Object> list = new ArrayList<Object>();
        hql.append("select r from RoleEO r,RoleAssignmentEO a");
        hql.append(" where r.roleId=a.roleId and a.userId=? and a.organId=? and r.recordStatus=? and a.recordStatus=?");
        list.add(userId);
        list.add(organId);
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(AMockEntity.RecordStatus.Normal.toString());
        if (types != null && types.length > 0) {
            hql.append(" and (");
            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                if (!StringUtils.isEmpty(type)) {
                    if (i == 0) {
                        hql.append(" r.type=?");
                    } else {
                        hql.append(" or r.type=?");
                    }
                    list.add(type);
                }
            }
            hql.append(")");
        }
        return getEntitiesByHql(hql.toString(), list.toArray());
    }

    @Override
    public List<RoleEO> getRoles(String roleCodePrefix) {
        String hql = "from RoleEO r where r.code like ? escape '\\' and r.recordStatus=?";
        String target = SqlUtil.prepareParam4Query(roleCodePrefix.concat("_")).concat("%");
        return getEntitiesByHql(hql, new Object[] { target, AMockEntity.RecordStatus.Normal.toString() });
    }

    @Override
    public List<RoleEO> getUnitUsedRoles(Long unitId, Long[] roleIds) {
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("from RoleEO r where r.roleId in (select distinct(ra.roleId) from RoleAssignmentEO ra where ra.unitId=? ");
        values.add(unitId);
        if (roleIds != null && roleIds.length > 0) {
            hql.append(" and (");
            int size = roleIds.length;
            for (int i = 0; i < size; i++) {
                Long roleId = roleIds[i];
                if (roleId != null) {
                    if (i == 0) {
                        hql.append(" r.roleId=?");
                    } else {
                        hql.append(" or r.roleId=?");
                    }
                    values.add(roleId);
                }
            }
            hql.append(")");
        }
        hql.append(" and ra.recordStatus=?)");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        hql.append(" and r.recordStatus=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(hql.toString(), values.toArray());
    }

    @Override
    public List<RoleEO> getCurUserRoles(Long organId, Long userId) {
        StringBuilder hql = new StringBuilder();
        hql.append("from RoleEO where createOrganId = ? and createUserId = ? and recordStatus = ? order by roleId asc");
        return getEntitiesByHql(hql.toString(), new Object[] {organId,userId,AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public List<RoleEO> getRolesBySiteId(Long siteId) {
        StringBuilder hql = new StringBuilder();
        hql.append("from RoleEO where siteId = ? and recordStatus = ? order by roleId asc");
        return getEntitiesByHql(hql.toString(), new Object[] {siteId,AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public List<RoleEO> getRolesBySiteId(Long siteId,Long organId) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder hql = new StringBuilder();
        hql.append("from RoleEO where siteId = ? and recordStatus = ? ");
        values.add(siteId);
        values.add(AMockEntity.RecordStatus.Normal.toString());
        if(null != organId) {
            hql.append(" and createOrganId = ?");
            values.add(organId);
        }
        hql.append(" order by roleId asc");
        return getEntitiesByHql(hql.toString(), values.toArray());
    }
}
