package cn.lonsun.ldap.internal.service;

import java.util.List;

import cn.lonsun.ldap.internal.exception.NameRepeatedException;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;

public interface ILdapPersonService2 {
	
	/**
	 * 验证同一级的单位下是否存在名字相同的人员
	 * @param parentDn
	 * @param name
	 * @return
	 */
	public boolean isNameExisted(String parentDn,String name);
	
	/**
	 * 排序号是否已存在
	 * @param parentDn
	 * @param sortNum
	 * @return
	 */
	public boolean isSortNumExisted(String parentDn,Integer sortNum);
	
	/**
	 * 保存个人信息</br>
	 * @param parentDn
	 * @param person
	 * @param user
	 */
	public void save(String parentDn,PersonEO person,UserEO user);
	
	/**
	 * 删除用户
	 * 根据用户DN删除人员信息，同时会删除用户的账号信息</br>
	 * 当LDAP操作出现问题时，抛出NameRepeatedException
	 * @param dn
	 */
	public void delete(String dn);
	
	/**
	 * 更新个人信息</br>
	 * @param person
	 */
	public void update(PersonEO person);
	
	/**
	 * 更新个人排序号
	 * @param person
	 * @throws NameRepeatedException
	 */
	public void updateSortNum(String dn,Integer sortNum);
	
	/**
	 * 根据dn获取PersonEO
	 * @param dn
	 * @return
	 */
	public PersonEO getPerson(String dn);
	
	/**
	 * 根据单位DN获取单位下一级所有的PersonEO集合，并按sortNum由小到大排序
	 * @param organDN
	 * @return
	 */
	public List<PersonEO> getPersons(String organDN);
	
}
