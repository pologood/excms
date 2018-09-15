package cn.lonsun.ldap.internal.service;

import java.util.List;

import cn.lonsun.ldap.internal.exception.NameRepeatedException;
import cn.lonsun.rbac.internal.entity.OrganEO;

public interface ILdapOrganService {
	
	
	/**
	 * 验证同一个组织或单位下类型为type的名称name是否已存在
	 * @param parentDn
	 * @param type
	 * @param name
	 * @return
	 */
	public boolean isNameExisted(String parentDn,String type,String name);
	
	/**
	 * 验证名称是否已存在 同级目录下，type类型的sortNum是否已存在，如果存在返回true
	 * 
	 * @param parentId
	 * @param type
	 * @param sortNum
	 * @return
	 */
	public boolean isSortNumExisted(String parentDn, String type, String sortNum);
	
	/**
	 * 验证组织或单位下是否存在子节点
	 * @param parentDn
	 * @return
	 */
	public boolean hasSuns(String parentDn);
	
	/**
	 * 验证组织或单位下是否存在组织或单位子节点
	 * @param parentDn
	 * @return
	 */
	public boolean hasOrganSuns(String parentDn);
	
	/**
	 * 保存组织/单位，对数据的合法性不做验证，需要在调用此方法前自行验证</br>
	 * @param simpleDn
	 * @param organ
	 */
	public void save(String simpleDn,OrganEO organ);
	
	
	/**
	 * 删除组织/单位，对dn下存在子节点不做验证，需要调用者调用前进行验证
	 * @param dn
	 */
	public void delete(String dn);
	
	
	/**
	 * 更新组织/单位，对数据的合法性不做验证，需要在调用此方法前自行验证</br>
	 * @param organ
	 */
	public void update(OrganEO organ); 
	
	/**
	 * 更新组织/单位排序号
	 * @param person
	 * @throws NameRepeatedException
	 */
	public void updateSortNum(String dn,Long sortNum) throws NameRepeatedException;
	
	/**
	 * 查询组织或单位
	 * @param dn
	 * @return
	 */
	public OrganEO getOrgan(String dn);
	
	/**
	 * 获取所有子单位,，并按sortNum由小到大排序
	 * @param dn
	 * @param platformCode
	 * @return
	 */
	public List<OrganEO> getUnits(String parentDn,String platformCode);
	
	/**
	 * 根据平台编码获取组织，如果平台编码为空，那么取所有的
	 *
	 * @param parentDn
	 * @param types 需要返回的节点类型
	 * @param platformCode
	 * @return
	 */
	public List<OrganEO> getOrgansByPlatformCode(String parentDn,String[] types,String platformCode);
	
	/**
	 * 获取所有子组织和单位,，并按sortNum由小到大排序
	 * @param dn
	 * @return
	 */
	public List<OrganEO> getOrgans(String parentDn);
	
	/**
	 * 获取所有子组织或单位，并按sortNum由小到大排序
	 * @param parentDn 
	 * @param type 获取对象类型，组织或单位
	 * @return
	 */
	public List<OrganEO> getOrgans(String parentDn,String type);
	
	/**
	 * 获取所有子组织或单位，并按sortNum由小到大排序
	 * @param parentDn 
	 * @param types 获取对象类型，组织或单位
	 * @param isContainsFictitious 是否包含虚拟单位
	 * @return
	 */
	public List<OrganEO> getOrgans(String parentDn,List<String> types,boolean isContainsFictitious);
	
	/**
	 *TODO 此方法的实现在组织名称查询时返回为null，原因尚未找到-因此暂时不支持blurryName查询
	 * 组织架构查询
	 *
	 * @param rootDns  查询的顶层节点
	 * @param types      返回的节点类型-Organ,OrganUnit,Virtual
	 * @param scope      查询范围,使用naming中的SearchControls中的范围：0,1,2
	 * @param blurryName 查询单位名称-模糊查询
	 * @return
	 */
	List<OrganEO> getOrgans(List<String> rootDns,List<String> types,int scope,String blurryName);
	
}
