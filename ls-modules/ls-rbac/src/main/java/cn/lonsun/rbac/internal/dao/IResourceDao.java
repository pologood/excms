package cn.lonsun.rbac.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.ResourceEO;

public interface IResourceDao extends IMockDao<ResourceEO> {
	
	/**
	 * 资源路径为uri的资源是否存在
	 * @param uri
	 * @return
	 */
	public boolean isUriExisted(String uri);
	
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
	 * 通过indicatorId查询资源
	 *
	 * @author yy
	 * @param indicatorId
	 * @return
	 */
    public List<ResourceEO> getResourcesByIndicatorId(Long indicatorId);
    
    /**
	 * 根据indicatorIds获取所有的资源
	 * @param indicatorIds
	 * @return
	 */
	public List<ResourceEO> getResources(List<Long> indicatorIds);

}
