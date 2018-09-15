package cn.lonsun.rbac.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.RoleRelationEO;

/**
 * 角色关联关系ORM接口
 *
 * @author xujh
 * @version 1.0
 * 2015年5月27日
 *
 */
public interface IRoleRelationDao extends IMockDao<RoleRelationEO> {
	
	/**
	 * 删除关系中存在角色ID为role的所有记录
	 *
	 * @param roleId
	 */
	public void deleteByRoleIdAndTargetRoleId(Long roleId);

	/**
	 * 分页查询
	 *
	 * @param roleId
	 * @param query
	 * @return
	 */
	public Pagination getPage(Long roleId,PageQueryVO query);
}
