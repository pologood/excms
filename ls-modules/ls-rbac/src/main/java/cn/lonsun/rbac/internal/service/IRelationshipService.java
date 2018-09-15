package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.RelationshipEO;
import cn.lonsun.rbac.vo.RelationshipQueryVO;

/**
 * 人员上下级关系服务接口
 *  
 * @author xujh 
 * @date 2014年10月29日 下午2:40:55
 * @version V1.0
 */
public interface IRelationshipService extends IBaseService<RelationshipEO>{
	/**
	 * 是否已经绑定上下级关系
	 * @param personId
	 * @return
	 */
	public boolean isInRelationship(Long personId);
	
	/**
	 * 是否有下属
	 * @param leaderPersonId
	 * @return
	 */
	public boolean hasSubordinates(Long leaderPersonId);
	
	/**
	 * 获取下属
	 * 
	 * @param leaderPersonId
	 * @return
	 */
	public List<RelationshipEO> getSubordinates(Long leaderPersonId);
	
	/**
	 * 获取下属
	 * 
	 * @param unitIds
	 * @return
	 */
	public List<RelationshipEO> getSubordinates(List<Long> unitIds);
	
	/**
	 * 保存
	 * @param leaderPersonId
	 * @param personIds
	 * @return
	 */
	public List<RelationshipEO> saveRelationships(Long leaderPersonId,List<Long> personIds);
	
	/**
	 * 更新Relationship的下属数量
	 * @param relationshipId
	 * @return
	 */
	public Integer updateSubordinateCount(Long relationshipId);
	
	/**
	 * 根据personId人员与上级之间的关系
	 * @param personId
	 * @return
	 */
	public RelationshipEO getShipByPersonId(Long personId);
	
	/**
	 * 根据personId人员与上级之间的关系
	 * @param personIds
	 * @return
	 */
	public List<RelationshipEO> getShipsByPersonIds(Long[] personIds);
	
	/**
	 * 获取下属分页
	 * @param vo
	 * @return
	 */
	public Pagination getPage(RelationshipQueryVO vo);
	
	/**
	 * 获取所有的下级
	 * @param leaderPersonId
	 * @return
	 */
	public List<RelationshipEO> getAllSubordinates(Long leaderPersonId);
}
