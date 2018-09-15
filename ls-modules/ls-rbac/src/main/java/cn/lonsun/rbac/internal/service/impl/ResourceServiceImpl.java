package cn.lonsun.rbac.internal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.rbac.internal.dao.IResourceDao;
import cn.lonsun.rbac.internal.entity.ResourceEO;
import cn.lonsun.rbac.internal.service.IResourceService;

@Service("resourceService")
public class ResourceServiceImpl extends MockService<ResourceEO> implements IResourceService {

	@Autowired
	private IResourceDao resourceDao;
	@Autowired
	private IIndicatorService indicatorService;

	@Override
	public void save(ResourceEO resource) {
		//TODO 解决模糊匹配，对path进行拆分
		saveEntity(resource);
	}

	@Override
	public void update(ResourceEO resource) {
		//TODO 解决模糊匹配，对path进行拆分
		updateEntity(resource);
	}
	
	@Override
	public List<ResourceEO> getResourcesByResourceTypeId(Long resourceTypeId){
		return null;
	}

	@Override
	public List<ResourceEO> getResources(Long businessTypeId) {
		return resourceDao.getResources(businessTypeId);
	}

	@Override
	public Pagination getPagination(Long index, Integer size,
			Long businessTypeId, String subName, String subPath) {
		return resourceDao.getPagination(index, size, businessTypeId, subName, subPath);
	}

    @Override
    public void saveResourcesWithIndicatorId(Long indicatorId, List<ResourceEO> resources) {
        IndicatorEO indicator = indicatorService.getIndicatorEOById(indicatorId);
        if(null!=indicator.getUri()) {
            
            ResourceEO resource = new ResourceEO();
            resource.setIndicatorId(indicatorId);
            resource.setUri(indicator.getUri());
            resource.setIsEnable(indicator.getIsEnable());
            Long resourceId = resourceDao.save(resource);
            indicator.setResourceId(resourceId);
            
            indicatorService.updateEntity(indicator);
        }
        
        if(null!=indicatorId && resources.size()>0) {
            for(ResourceEO resourceEO : resources) {
                resourceEO.setIndicatorId(indicatorId);
                resourceEO.setIsEnable(indicator.getIsEnable());
                resourceDao.save(resourceEO);
            }
        }
        
    }

    @Override
    public void updateResourcesWithIndicatorId(Long indicatorId, List<ResourceEO> resources) {
        if(null!=indicatorId) {
            IndicatorEO indicator = indicatorService.getIndicatorEOById(indicatorId);
            
            List<ResourceEO> addList = new ArrayList<ResourceEO>();
            List<ResourceEO> deleteList = new ArrayList<ResourceEO>();
            List<ResourceEO> updateList = new ArrayList<ResourceEO>();
            
            for(ResourceEO resourceEO : resources) {
                resourceEO.setIndicatorId(indicatorId);
                resourceEO.setIsEnable(indicator.getIsEnable());
                
                if(null!=resourceEO.getResourceId()&&resourceEO.getResourceId()>0) {
                    updateList.add(resourceEO);
                }else {
                    addList.add(resourceEO);
                }
            }
            resourceDao.update(updateList);    
            
            List<ResourceEO> resourceList = getResourcesByIndicatorId(indicatorId);
            for(ResourceEO resourceEO : resources) {
                if(null!=resourceEO.getResourceId()&&resourceEO.getResourceId()>0){
                   ResourceEO rEO = resourceDao.getEntity(ResourceEO.class, resourceEO.getResourceId());
                   if(resourceList.contains(rEO)) {
                       resourceList.remove(rEO);
                   }
                }
            }
            
            if(null!=indicator.getUri()) {
                ResourceEO resource = resourceDao.getEntity(ResourceEO.class, indicator.getResourceId());
                if(null!=resource) {
                    resourceList.remove(resource);
                    
                    resource.setIsEnable(indicator.getIsEnable());
                    resource.setUri(indicator.getUri());
                    resourceDao.update(resource);
                } else {
                    ResourceEO r = new ResourceEO();
                    r.setIndicatorId(indicatorId);
                    r.setUri(indicator.getUri());
                    r.setIsEnable(indicator.getIsEnable());
                    Long resourceId = resourceDao.save(r);
                    indicator.setResourceId(resourceId);
                    
                    indicatorService.updateEntity(indicator);
                }
                
            } else {
                resourceDao.delete(ResourceEO.class, indicator.getResourceId());
            }
            
            deleteList = resourceList;
            
            resourceDao.save(addList);
            resourceDao.delete(deleteList);
        }
    }

    @Override
    public List<ResourceEO> getResourcesByIndicatorId(Long indicatorId) {
        return resourceDao.getResourcesByIndicatorId(indicatorId);
    }

    @Override
    public void saveEnable(Long indicatorId, boolean isEnable) {
        
        List<ResourceEO> rs = resourceDao.getResourcesByIndicatorId(indicatorId);
        for (ResourceEO resourceEO : rs) {
            resourceEO.setIsEnable(isEnable);
            resourceDao.update(resourceEO);
        }
        
    }

	@Override
	public boolean isUriExisted(String uri) {
		if(StringUtils.isEmpty(uri)){
			throw new NullPointerException();
		}
		return resourceDao.isUriExisted(uri);
	}

	@Override
	public List<ResourceEO> getResources(List<Long> indicatorIds) {
		if(indicatorIds==null||indicatorIds.size()<1){
			throw new NullPointerException();
		}
		return resourceDao.getResources(indicatorIds);
	}

}
