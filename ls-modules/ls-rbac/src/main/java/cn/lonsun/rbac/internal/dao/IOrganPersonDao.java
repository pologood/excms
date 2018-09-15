package cn.lonsun.rbac.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.rbac.internal.entity.OrganPersonEO;

/**
 * 组织人员关系DAO借口
* @Description: 
* @author xujh 
* @date 2014年9月23日 下午10:13:07
* @version V1.0
 */
public interface IOrganPersonDao extends IMockDao<OrganPersonEO> {
	
	/**
	 * 根据单位ID集合删除人员和单位的关系记录
	 *
	 * @param organIds
	 */
	public void deleteByOrganIds(List<Long> organIds);
	
	/**
	 * 根据personId获取
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
	 */
	public void updateOrganName(Long organId,String organName);

}
