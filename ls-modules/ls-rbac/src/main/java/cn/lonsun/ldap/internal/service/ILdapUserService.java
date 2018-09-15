package cn.lonsun.ldap.internal.service;

import cn.lonsun.ldap.internal.exception.UidRepeatedException;
import cn.lonsun.rbac.internal.entity.UserEO;

public interface ILdapUserService {
	
	/**
	 * 根据用户名和密码获取用户
	 * @param uid
	 * @param password
	 * @param ip
	 * @return
	 */
	public boolean login(String uid,String md5Pwd,String ip);

	/**
	 * 保存用户</br>
	 * 如果用户名已存在，那么抛出UidRepeatedException
	 * @param user
	 * @throws UidRepeatedException
	 */
	public void save(UserEO user) throws UidRepeatedException;
	
	/**
	 * 更新user，只针对密码和状态
	 * @param user
	 */
	public void update(UserEO user);
	
	
	/**
	 * 根据uid删除user
	 * @param uid
	 */
	public void delete(String uid);
	
	/**
	 * 更新用户密码</br>
	 * 分两种情况：
	 * 1.用户修改自己的密码，此时需要提供原始密码password</br>
	 * 2.管理员重置密码，此时password为空即可
	 * @param uid
	 * @param md5Pwd
	 * @param md5NewPassword
	 */
	public void updatePassword(String uid,String md5Pwd,String md5NewPassword);
	
	/**
	 * 更新用户状态</br>
	 * @param user
	 */
	public void updateStatus(UserEO user);
	
	
	/**
	 * 根据用户名和密码获取用户
	 * @param uid
	 * @param md5Pwd
	 * @return
	 */
	public UserEO getUser(String uid,String md5Pwd);
	
	/**
	 * 根据人员的Dn获取用户
	 * @param uid
	 * @param password
	 * @return
	 */
	public UserEO getUser(String personDn);
	
	/**
	 * LDAP中uid是否已存在
	 * @param uid
	 * @return
	 */
	public boolean isUidExisted(String uid);
}
