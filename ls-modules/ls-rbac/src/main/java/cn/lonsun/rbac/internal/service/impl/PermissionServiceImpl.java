package cn.lonsun.rbac.internal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.rbac.internal.dao.IPermissionDao;
import cn.lonsun.rbac.internal.entity.PermissionEO;
import cn.lonsun.rbac.internal.entity.ResourceEO;
import cn.lonsun.rbac.internal.service.IPermissionService;
import cn.lonsun.rbac.internal.service.IResourceService;

@Service("permissionService")
public class PermissionServiceImpl extends MockService<PermissionEO> implements IPermissionService {

    @Autowired
    private IPermissionDao permissionDao;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IIndicatorService indicatorService;
    
    @Override
    public void savePermission(Long roleId, String rights) {
        if(null==roleId||StringUtils.isEmpty(rights.trim())) {
            return;
        }
        
        String[] rightStr = rights.split(",");
        for(String str : rightStr) {
            if(!str.equals("on")) {
                List<ResourceEO> resources = resourceService.getResourcesByIndicatorId(Long.valueOf(str));
                for (ResourceEO resource : resources) {
                	PermissionEO p = new PermissionEO();
                    p.setRoleId(roleId);
                    p.setResourceId(resource.getResourceId());
                    p.setUri(resource.getUri());
                    permissionDao.save(p);
                }
            }
        }
        
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updatePermission(Long roleId, String rights) {
        if(null==roleId||roleId<=0) {
            return;
        }
        //如果为空，那么删除所有的权限
        if(StringUtils.isEmpty(rights.trim())) {
            delete(permissionDao.getPermissionByRole(roleId)); 
            return;
        }
        List<Long> addlist = new ArrayList<Long>();
        List<Long> deletelist = new ArrayList<Long>();
        List<Long> updateResourceIds = new ArrayList<Long>(); // 更新后的resourceId
        List<Long> srcResourceIds = new ArrayList<Long>(); // 更新前的resourceId
        String[] rightStr = rights.split(",");
        List<Long> indicatorIds = new ArrayList<Long>(rightStr.length);
        for(int i=0;i<rightStr.length;i++) {
        	String str = rightStr[i];
        	//TODO ?????????????????????什么意思，待问家军
            if(!str.equals("on")) {
            	indicatorIds.add(Long.valueOf(str));
            }
        }
        //获取角色新的资源
        List<ResourceEO> resources = resourceService.getResources(indicatorIds);
        Map<Long,ResourceEO> map = new HashMap<Long,ResourceEO>(resources.size());
        for (ResourceEO resource : resources) {
        	updateResourceIds.add(resource.getResourceId());
        	map.put(resource.getResourceId(), resource);
        }
        //更新前的资源
        List<PermissionEO> srcPermissions = getPermissionByRole(roleId);
        for (PermissionEO permission : srcPermissions) {
            srcResourceIds.add(permission.getResourceId());
        }
        deletelist = (List<Long>) CollectionUtils.subtract(srcResourceIds, updateResourceIds);
        addlist = (List<Long>) CollectionUtils.subtract(updateResourceIds, srcResourceIds);
        //添加新的权限
        for(Long resourceId : addlist) {
        	PermissionEO p = new PermissionEO();
            p.setRoleId(roleId);
            p.setResourceId(resourceId);
            ResourceEO r = map.get(resourceId);
            p.setUri(r.getUri());
            permissionDao.save(p);
        }
        //删除移除的权限
        for(Long resourceId : deletelist) {
            permissionDao.delete(permissionDao.getByRoleAndResource(roleId, resourceId));
        }
        
    }

    @Override
    public List<PermissionEO> getPermissionByRole(Long roleId) {
        return permissionDao.getPermissionByRole(roleId);
    }

	@Override
	public boolean hasPermission(List<Long> roleIds, String uri) {
		if(roleIds==null||roleIds.size()<=0||StringUtils.isEmpty(uri)){
			return false;
		}
		//默认都有权限访问
		boolean hasPermission = true;
		if(resourceService.isUriExisted(uri)){
			hasPermission = permissionDao.hasPermission(roleIds, uri);
		}
		return hasPermission;
	}

}
