package cn.lonsun.wechatmgr.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.UserGroupEO;

public interface IUserGroupService extends IMockService<UserGroupEO> {

	/**
	 * 
	 * @Title: getListBySite
	 * @Description: 根据站点获取分组
	 * @param siteId
	 * @return   Parameter
	 * @return  List<UserGroupEO>   return type
	 * @throws
	 */
	List<UserGroupEO> getListBySite(Long siteId);
	/**
	 * 
	 * @Title: getpage
	 * @Description: 分页查询
	 * @param siteId
	 * @param pageIndex
	 * @param pageSize
	 * @return   Parameter
	 * @return  Pagination   return type
	 * @throws
	 */
	Pagination getpage(Long siteId,Long pageIndex,Integer pageSize);
	/**
	 * 
	 * @Title: deleteBySite
	 * @Description: 根据站点删除
	 * @param siteId   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void deleteBySite(Long siteId);
}
