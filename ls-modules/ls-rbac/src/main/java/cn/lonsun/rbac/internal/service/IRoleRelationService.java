package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.RoleRelationEO;

/**
 * 角色关系接口
 *
 * @author xujh
 * @version 1.0
 * 2015年5月27日
 *
 */
public interface IRoleRelationService extends IMockService<RoleRelationEO> {
	
	/**
	 * 保存角色关系
	 *
	 * @param roleId
	 * @param targetRoleIds
	 * @param type
	 */
	public void saveRelations(Long roleId,List<Long> targetRoleIds,String type);
	
	/**
	 * 分页查询
	 *
	 * @param roleId
	 * @param query
	 * @return
	 */
	public Pagination getPage(Long roleId,PageQueryVO query);
	
	/**
	 * 删除关系中存在角色ID为role的所有记录
	 *
	 * @param roleId
	 */
	public void deleteByRoleIdAndTargetRoleId(Long roleId);
	
	/**
	 * 获取角色roleId对应的类型为type的关联角色
	 *
	 * @param roleId
	 * @param type
	 * @return
	 */
	public List<Long> getRoleIdsByRelationType(Long roleId,String type);

}
