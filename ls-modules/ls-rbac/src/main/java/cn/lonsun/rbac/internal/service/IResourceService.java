package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.ResourceEO;

public interface IResourceService extends IMockService<ResourceEO>{
	
	/**
	 * 根据indicatorIds获取所有的资源
	 * @param indicatorIds
	 * @return
	 */
	public List<ResourceEO> getResources(List<Long> indicatorIds);

	/**
	 * 保存资源
	 * @param resource
	 */
	public void save(ResourceEO resource);
	
	/**
	 * 更新资源
	 * @param resource
	 */
	public void update(ResourceEO resource);
	
	/**
	 * 通过模块ID获取模块的所有资源
	 * @param businessTypeId
	 * @return
	 */
	public List<ResourceEO> getResources(Long businessTypeId);
	
	/**
	 * 根据resourceTypeId获取资源集合
	 * @param resourceTypeId
	 * @return
	 */
	public List<ResourceEO> getResourcesByResourceTypeId(Long resourceTypeId);
	
	/**
	 * 资源路径为uri的资源是否存在
	 * @param uri
	 * @return
	 */
	public boolean isUriExisted(String uri);
	
	/**
	 * 根据indicatorId获取资源集合
	 * @param indicatorId
	 * @return
	 */
	public List<ResourceEO> getResourcesByIndicatorId(Long indicatorId);
	
	/**
	 * 通过模块ID获取模块的分页资源
	 * @param index
	 * @param size
	 * @param businessTypeId   所属模块
	 * @param subName          子名称-模糊查询
	 * @param subPath		      子路径-模糊查询
	 * @return
	 */
	public Pagination getPagination(Long index,Integer size,Long businessTypeId,String subName,String subPath);

	/**
	 * 保存权限下面的资源
	 *
	 * @author yy
	 * @param indicatorId
	 * @param resources
	 */
    public void saveResourcesWithIndicatorId(Long indicatorId, List<ResourceEO> resources);

    /**
     * 修改权限下面的资源
     *
     * @author yy
     * @param indicatorId
     * @param resources
     */
    public void updateResourcesWithIndicatorId(Long indicatorId, List<ResourceEO> resources);

    /**
     * 设置禁用启用
     *
     * @author yy
     * @param indicatorId
     * @param isEnable
     */
    public void saveEnable(Long indicatorId, boolean isEnable);
}
