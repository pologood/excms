package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.entity.UserEO;

/**
 * 账号服务类
 * @author Administrator
 *
 */
public interface IUserService extends IMockService<UserEO>{
	
	/**
	 * 新增账号，personDn未保存，需要另外更新
	 *
	 * @param node
	 * @return
	 */
	public UserEO save(PersonNodeVO node);
	
	/**
	 * 用户更新
	 *
	 * @param node
	 * @return
	 */
	public UserEO update(PersonNodeVO node);
	
	/**
	 * 验证ip是否允许访问系统
	 *
	 * @param user
	 * @param ip
	 * @return
	 */
	public boolean isIpAccessable(UserEO user,String ip);
	
	/**
	 * 登录
	 * @param uid
	 * @param md5Pwd
	 * @param ip
	 * @return
	 */
	public UserEO login4Developer(String uid,String md5Pwd);
	
	/**
	 * 登录
	 *
	 * @param uid
	 * @param md5Pwd
	 * @param ip
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public PersonEO login(String uid,String md5Pwd,String ip,String code) throws Exception;
	
	/**
	 * 应用管理登录
	 *
	 * @param uid
	 * @param md5Pwd
	 * @return
	 */
	public UserEO rootLogin(String uid,String md5Pwd);
	
	/**
	 * 验证用户密码是否正确
	 * @param uid
	 * @param password
	 * @return
	 */
	public boolean verifyPwd(String uid,String password);
	
	/**
	 * 获取用户userId拥有的角色
	 * @param organId
	 * @param userId
	 * @return
	 */
	public List<Long> getRoleIds(Long organId,Long userId);
	
	/**
	 * 先保存到LDAP中，然后在数据库中保存
	 * @param user
	 * @param organId
	 * @param roleIds
	 * @return
	 */
	public Long save(UserEO user,Long organId);
	
	/**
	 * 根据主键删除用户</br>
	 * 先删除LDAP上的用户信息再删除本地数据库中的用户
	 * @param id
	 */
	public void delete(Long id);
	
	/**
	 * 更新用户密码
	 * @param uid
	 * @param oldPassword
	 * @param newPassword
	 */
	public void updatePassword(String uid,String oldPassword,String newPassword);
	
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
	 * 更新用户状态
	 * @param userIds
	 * @param status
	 * @return
	 */
	public List<UserEO> updateStatus(Long[] userIds,String status);
	
	/**
	 * 根据uid的部分内容进行模糊查询,最多返回maxCount条数据</br>
	 * 如果maxCount为空或小于0，那么默认赋予10</br>
	 * 如果maxCount超过100，那么只返回100条数据
	 * @param subUid
	 * @param maxCount
	 * @return
	 */
	public List<UserEO> getUsersBySubUid(String subUid,Integer maxCount);
	
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
	 * uid是否在LDAP和数据库中都不存在
	 * @param uid
	 * @return
	 */
	public boolean isUidExisted(String uid);
	
	/**
	 * 用户名是否可用</br>
	 * 1.用户名已存在;</br>
	 * 2.用户名被系统锁定,即有用户正在使用该用户名;
	 * @param uid
	 * @return
	 */
	public boolean isUidUsable(String uid);

	/**
	 * 根据角色查询用户
	 *
	 * @author yy
	 * @param roleId
	 * @return
	 */
    public List<UserEO> getUsersByRoleId(Long roleId);

    /**
     * 给用户添加角色
     *
     * @author yy
     * @param userId
     * @param roles
     */
    public void saveUA(Long userId, List<RoleEO> roles);
    
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
	
	/**
	 * 开发商管理账号维护
	 *
	 * @param uid
	 * @param oldPwd
	 * @param newPwd
	 */
	public void saveOrUpdateDeveloper(String uid,String oldPwd,String newPwd);
	/**
	 * 系统超级管理员账号维护
	 *
	 * @param uid
	 * @param oldPwd
	 * @param newPwd
	 */
	public void saveOrUpdateSuperAdministrator(String uid,String oldPwd,String newPwd);

	/**
	 * 用户修改个人信息
	 * @param node
	 * @return
	 */
	public UserEO updatePersonalInfo(PersonNodeVO node);
}
