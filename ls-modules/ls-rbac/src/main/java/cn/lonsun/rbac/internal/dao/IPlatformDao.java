package cn.lonsun.rbac.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.PlatformEO;

/**
 * 平台管理ORM接口
 *
 * @author xujh
 * @version 1.0
 * 2015年4月24日
 *
 */
public interface IPlatformDao extends IBaseDao<PlatformEO> {
	
	/**
	 * 验证code是否已存在
	 *
	 * @param code
	 * @param exceptId
	 * @return
	 */
	public boolean isCodeExisted(String code,Long exceptId);
	
	/**
	 * 分页查询
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPagination(PageQueryVO query);

}
