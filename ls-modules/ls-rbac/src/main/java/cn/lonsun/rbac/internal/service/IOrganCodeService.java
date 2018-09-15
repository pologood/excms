package cn.lonsun.rbac.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganCodeEO;

/**
 * ORM接口
 *
 * @author xujh
 * @version 1.0
 * 2015年2月13日
 *
 */
public interface IOrganCodeService extends IBaseService<OrganCodeEO>{
	
	/**
	 * 获取分页
	 * 
	 * @param query
	 * @return
	 */
	public Pagination getPagination(PageQueryVO query);

}
