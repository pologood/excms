package cn.lonsun.wechatmgr.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.UserGroupEO;

public interface IUserGroupDao extends IMockDao<UserGroupEO> {

	/**
	 * 
	 * @Title: getListBySite
	 * @Description: 根据站点获取用户组集合
	 * @param siteId
	 * @return   Parameter
	 * @return  List<UserGroupEO>   return type
	 * @throws
	 */
	List<UserGroupEO> getListBySite(Long siteId);
	/**
	 * 
	 * @Title: getpage
	 * @Description: 分页获取
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
