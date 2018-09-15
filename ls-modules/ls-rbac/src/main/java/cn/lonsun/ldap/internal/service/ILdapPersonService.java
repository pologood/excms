package cn.lonsun.ldap.internal.service;

import java.util.List;
import java.util.Map;

import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.ldap.internal.exception.NameRepeatedException;
import cn.lonsun.ldap.internal.exception.SortNumRepeatedException;
import cn.lonsun.ldap.vo.InetOrgPersonVO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;

public interface ILdapPersonService {
	
	/**
	 * 更细人员是否兼职属性，兼职用户新增uid和密码，原宿主用户删除uid和密码
	 *
	 * @param dn
	 * @param isPluralistic
	 * @param uid
	 * @param md5Pwd
	 */
	public void updateIsPluralistic(String dn,Boolean isPluralistic,String uid,String md5Pwd);

	/**
	 * 保存个人信息</br>
	 * 
	 * @param parentDn
	 * @param person
	 * @throws BusinessException
	 */
	public void save(String parentDn, PersonEO person) throws BusinessException;

	/**
	 * 不做任何验证，直接保存人员
	 * 
	 * @param parentDn
	 * @param person
	 */
	public void saveWithNoVertiry(String parentDn, PersonEO person);

	/**
	 * 验证同一级的单位下是否存在名字相同的人员
	 * 
	 * @param parentDn
	 * @param name
	 * @return
	 */
	public boolean isNameExisted(String parentDn, String name);

	/**
	 * 删除用户 根据用户DN删除人员信息，同时会删除用户的账号信息</br> 当LDAP操作出现问题时，抛出NameRepeatedException
	 * 
	 * @param dn
	 */
	public void delete(String dn);

	/**
	 * 更新个人信息</br> 当同一级单位下存现姓名相同的人员时，抛出NameRepeatedException</br>
	 * sortNum重复时抛出SortNumRepeatedException
	 * 
	 * @param person
	 * @throws NameRepeatedException
	 */
	public void update(PersonEO person) throws NameRepeatedException;
	
	/**
	 * Ldap保存
	 *
	 * @param person
	 * @param user
	 */
	public void save(PersonEO person,UserEO user);
	
	/**
	 * Ldap更新
	 *
	 * @param person
	 * @param user
	 */
	public void update(PersonEO person,UserEO user);

	/**
	 * 更新个人排序号
	 * 
	 * @param person
	 * @throws NameRepeatedException
	 */
	public void updateSortNum(String dn, Long sortNum)
			throws SortNumRepeatedException;

	/**
	 * 更新个人信息</br> 当同一级单位下存现姓名相同的人员时，抛出NameRepeatedException</br>
	 * 
	 * @param person
	 * @throws NameRepeatedException
	 * @throws SortNumRepeatedException
	 */
	public void update(List<PersonEO> persons);

	/**
	 * 根据dn获取PersonEO
	 * 
	 * @param dn
	 * @return
	 */
	public PersonEO getPerson(String dn);
	
	/**
	 * 根据名字获取单位organDns下所有人员的DN-模糊查询
	 * @param organDns
	 * @param name
	 * @return
	 */
	public List<String> getDns(String[] organDns,String name);
	
	/**
	 * 根据名字获取单位organDns下所有人员的DN-模糊查询
	 * @param organDns
	 * @param name
	 * @return
	 */
	public List<String> getNames(String[] organDns,String name,int count);

	/**
	 * 根据单位DN获取单位下一级所有的PersonEO集合，并按sortNum由小到大排序
	 * 
	 * @param organDN
	 * @param ignorePluralistics 是否忽略兼职人员
	 * @return
	 */
	public List<PersonEO> getPersons(String organDN, boolean ignorePluralistics);
	
	/**
	 * 根据组织Dn获取下级人员
	 *
	 * @param organDn
	 * @param searchScope  与javax.naming.directory.SearchControls保持一致
	 * @return
	 */
	public List<InetOrgPersonVO> getInetOrgPersons(String organDn,int searchScope);
	
	/**
	 * 获取部门下人员列表
	 *
	 * @param organDN
	 * @param status
	 * @return
	 */
	public List<PersonEO> getSubPersons(String organDN,String status);
	
	/**
	 * 获取organDns下姓名中包含了name的所有人员
	 *
	 * @param organDns
	 * @param name
	 * @return
	 */
	public Map<String,List<?>> getPersons(String[] organDns,String name);

	/**
	 * 排序号是否已存在
	 * 
	 * @param parentDn
	 * @param sortNum
	 * @return
	 */
	public boolean isSortNumExisted(String parentDn, Long sortNum);

}
