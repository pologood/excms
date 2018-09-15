/*
 * RoleServiceImpl.java         2014年8月26日 <br/>
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

package cn.lonsun.rbac.internal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.rbac.internal.dao.IRoleDao;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.rbac.internal.dao.IIndicatorPermissionDao;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.service.IIndicatorPermissionService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.util.LoginPersonUtil;


/**
 * 指示器服务类
 *	 
 * @date     2014年8月26日  
 * @author 	 yy
 * @version	 v1.0 
 */
@Service
public class IndicatorPermissionServiceImpl extends MockService<IndicatorPermissionEO> implements IIndicatorPermissionService {
    @Autowired
    private IIndicatorPermissionDao indicatorPermissionDao;
    @Autowired
    private IIndicatorService indicatorService;
    @Autowired
    private IRoleAssignmentService roleAssignmentService;
    @Autowired
    private IRoleService roleService;
    
    @Override
	public boolean hasPermission(Long organId, Long userId, Long parentId,
			String code) {
			Boolean hasPermission = false;
			//入参验证
			if(organId==null||userId==null||parentId==null||StringUtils.isEmpty(code)){
				throw new IllegalArgumentException();
			}
			//根据parentId和code获取对应的IndicatorEO集合
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentId", parentId);
			map.put("code", code);
			map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
			List<IndicatorEO> is = indicatorService.getEntities(IndicatorEO.class, map);
			if(is!=null&&is.size()>0){
				//获取用户拥有的角色
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("organId", organId);
				params.put("userId", userId);
				params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
				List<RoleAssignmentEO> ras = roleAssignmentService.getEntities(RoleAssignmentEO.class, params);
				if(ras!=null&&ras.size()>0){
					//构建权限查询参数
					List<Long> indicatorIds = new ArrayList<Long>(is.size());
					for(IndicatorEO i:is){
						indicatorIds.add(i.getIndicatorId());
					}
					List<Long> roleIds = new ArrayList<Long>(ras.size());
					for(RoleAssignmentEO ra:ras){
						roleIds.add(ra.getRoleId());
					}
					Map<String, Object> params2 = new HashMap<String, Object>();
					params2.put("indicatorId", indicatorIds);
					params2.put("roleId", roleIds);
					params2.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
					List<IndicatorPermissionEO> ips = getEntities(IndicatorPermissionEO.class, params2);
					if(ips.size()>0){
						hasPermission = true;
					}
				}
			}
			return hasPermission;
	}
    
    @Override
    public List<IndicatorPermissionEO> getByIndicatorId(Long indicatorId) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("indicatorId", indicatorId);
    	map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return getEntities(IndicatorPermissionEO.class, map);
    }

    @Override
    public void deleteByRoleAndIndicator(Long roleId) {
        indicatorPermissionDao.deleteByRoleAndIndicator(roleId);
    }

    @Override
    public void saveRoleAndIndicator(Long roleId, String rights) {
        if(null==roleId||StringUtils.isEmpty(rights.trim())) {
            return;
        }
        String[] rightStr = rights.split(",");
        List<Long> indicatorIds = new ArrayList<Long>(rightStr.length);
        for(String str : rightStr) {
            if(!str.equals("on")) {
            	indicatorIds.add(Long.valueOf(str));
            }
        }
        savePermissions(roleId,indicatorIds);
    }
    
    /**
     * 保存角色与应用、菜单以及按钮权限等的关系
     *
     * @param roleId
     * @param indicatorIds
     */
    private void savePermissions(Long roleId,List<Long> indicatorIds){
    	if(indicatorIds==null||indicatorIds.size()<=0){
    		return;
    	}
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("indicatorId", indicatorIds);
        List<IndicatorEO> indicators = indicatorService.getEntities(IndicatorEO.class, params);
        if(indicators!=null&&indicators.size()>0){
            RoleEO role = roleService.getEntity(RoleEO.class, roleId);
        	for(IndicatorEO indicator:indicators){
        		IndicatorPermissionEO p = new IndicatorPermissionEO();
                p.setIndicatorId(indicator.getIndicatorId());
                p.setIndicatorType(indicator.getType());
                p.setRoleId(roleId);
                if(!RoleEO.RoleCode.site_admin.equals(role.getType())){
                    p.setSiteId(LoginPersonUtil.getSiteId());
                }
                saveEntity(p);
        	}
        }
    }

    @Override
    public List<IndicatorPermissionEO> getByRoleId(Long roleId) {
        return indicatorPermissionDao.getByRoleId(roleId);
    }

    @Override
    public List<IndicatorPermissionEO> getByRoleIds(Long[] roleIds) {
        List<IndicatorPermissionEO> list = new ArrayList<IndicatorPermissionEO>();

        for(int i=0;i<roleIds.length;i++) {
            list.addAll(indicatorPermissionDao.getByRoleId(roleIds[i]));
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateRoleAndIndicator(Long roleId, String rights) {
        if(null==roleId||roleId<=0) {
            return;
        }
        //如果权限为空，那么删除所有的Indicator权限
        if(StringUtils.isEmpty(rights.trim())) {
            indicatorPermissionDao.deleteByRoleAndIndicator(roleId);
            return;
        }
        //新增的权限
        List<Long> addlist = new ArrayList<Long>();
        //待删除的权限
        List<Long> deletelist = new ArrayList<Long>();
        String[] rightStr = rights.split(",");
        //传入的权限主键
        List<Long> rightList = new ArrayList<Long>();
        for(String str : rightStr) {
            rightList.add(Long.valueOf(str));
        }
        //原有的权限集合
        List<IndicatorPermissionEO> oldList = indicatorPermissionDao.getByRoleId(roleId);
        List<Long> oldRights = new ArrayList<Long>();
        for (IndicatorPermissionEO indicatorPermissionEO : oldList) {
            oldRights.add(indicatorPermissionEO.getIndicatorId());
        }
        
        deletelist = (List<Long>) CollectionUtils.subtract(oldRights, rightList);
        addlist = (List<Long>) CollectionUtils.subtract(rightList, oldRights);
        savePermissions(roleId,addlist);
        if(deletelist!=null&&deletelist.size()>0){
        	indicatorPermissionDao.deletePermissions(roleId, deletelist);
        }
    }

	@Override
	public List<Long> getIndicatorIds(Long roleId) {
		if(roleId==null){
			throw new NullPointerException();
		}
		return indicatorPermissionDao.getIndicatorIds(roleId);
	}

}

