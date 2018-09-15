package cn.lonsun.rbac.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.rbac.internal.entity.RelationshipEO;

/**
 * 人员上下级关系服务辅助接口
 *  
 * @author xujh 
 * @date 2014年10月29日 下午2:40:55
 * @version V1.0
 */
public interface IPersonRelationshipService  extends IBaseService<RelationshipEO>{

	/**
	 * 是否有下属
	 * @param leaderPersonId
	 * @return
	 */
	public boolean hasSubordinates(Long leaderPersonId);
	
	/**
	 * 是否已经绑定上下级关系
	 * @param leaderPersonId
	 * @return
	 */
	public boolean isInRelationship(Long personId);
}
