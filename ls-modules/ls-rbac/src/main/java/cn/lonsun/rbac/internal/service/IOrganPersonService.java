package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.rbac.internal.entity.OrganPersonEO;

public interface IOrganPersonService extends IMockService<OrganPersonEO> {
	
	/**
	 * 根据单位ID集合删除人员和单位的关系记录
	 *
	 * @param organIds
	 */
	public void deleteByOrganIds(List<Long> organIds);
	
	/**
	 * 根据personId删除
	 * @param personId
	 */
	public void deleteByPersonId(Long personId);
	/**
	 * 根据organId删除
	 * @param organId
	 */
	public void deleteByOrganId(Long organId);
	
	/**
	 * 根据personId获取所有的关联关系
	 * @param personId
	 * @return
	 */
	public List<OrganPersonEO> getOrganPersonsByPersonId(Long personId);
	
	/**
	 * 根据组织Id获取
	 * @param organId
	 * @return
	 */
	public List<OrganPersonEO> getOrganPersonsByOrganId(Long organId);
	
	/**
	 * 更新person中组织ID为organId的组织名称为organName
	 * @param organId
	 * @param organName
	 * @param type
	 */
	public void updateOrganName(Long organId,String organName,String type);
}
