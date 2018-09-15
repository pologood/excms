package cn.lonsun.rbac.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.UserEO;

public interface IUserDao  extends IMockDao<UserEO>{
	
	/**
	 * 根据手机号获取UserEO对象
	 *
	 * @param mobile
	 * @return
	 */
	public UserEO getUserByMobile(String mobile);
	
	/**
	 * 根据userId获取密码
	 * @param userId
	 * @return
	 */
	public String getPassword(Long userId);
	
	/**
	 * 根据uid获取有效用户
	 * @param uid
	 * @return
	 */
	public UserEO getUser(String uid);
	
	/**
	 * 根据uid或手机号获取用户
	 * @param argument
	 * @return
	 */
	public UserEO getUserByUidOrMobile(String argument);
	
	/**
	 * 根据personId为User获取Dn
	 * @param personId
	 * @return
	 */
	public String getDn4User(Long personId);
	
	/**
	 * 根据uid的部分内容进行模糊查询,最多返回maxCount条数据
	 * @param subUid
	 * @param maxCount
	 * @return
	 */
	public List<UserEO> getUsersBySubUid(String subUid,Integer maxCount);

	/**
	 * 根据角色查询用户
	 *
	 * @author yy
	 * @param roleId
	 * @return
	 */
    public List<UserEO> getUsersByRoleId(Long roleId);
	
	/**
	 * 获取用户名分页
	 * @param index
	 * @param size
	 * @param organId
	 * @param subUid 模糊匹配
	 * @param orderField 排序字段
	 * @param isDesc 是否倒序
	 * @return
	 */
	public Pagination getPageByUid(Long index,Integer size,Long organId,String subUid,String orderField,boolean isDesc);
	
	/**
	 * 获取用户姓名分页
	 * @param index
	 * @param size
	 * @param organId
	 * @param name 模糊匹配
	 * @param orderField 排序字段
	 * @param isDesc 是否倒序
	 * @return
	 */
	public Pagination getPageByName(Long index,Integer size,Long organId,String name,String orderField,boolean isDesc);
	
	/**
	 * 获取用户姓名分页
	 * @param index
	 * @param size
	 * @param organId
	 * @param subUid 模糊匹配
	 * @param name 模糊匹配
	 * @param organName 模糊匹配
	 * @param orderField 排序字段
	 * @param isDesc 是否倒序
	 * @return
	 */
	public Pagination getPage(Long index, Integer size, Long organId,
			String subUid, String name,String organName,String orderField, boolean isDesc);
}
