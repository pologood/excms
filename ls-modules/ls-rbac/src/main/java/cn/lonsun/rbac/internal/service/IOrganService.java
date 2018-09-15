package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.vo.OrganVO;

public interface IOrganService extends IMockService<OrganEO>{

	/**
	 * 根据id 修改ispublic字段
	 * @param ids
	 */
	public void updateIsPublic(Long... ids);
	
	/**
	 * 获取当前用户负责管理的单位(单位管理员)
	 *
	 * @param userId
	 * @return
	 */
	public List<OrganEO> getUnits4UnitManager(Long userId);
	
	/**
	 * 获取当前用户负责管理的单位(单位管理角色)
	 *
	 * @param userId
	 * @param indicatorId
	 * @return
	 */
	public List<OrganEO> getFLUnits4UnitManager(Long userId,Long indicatorId);
	
	/**
	 * 更新hasVirtualNode,hasOrgans,hasOrganUnits或hasFictitiousUnits
	 * 由于事务的问题，请在Controller中调用
	 * @param organId
	 */
	public void updateHasChildren4Organ(Long organId);
	
	/**
	 * 更新hasVirtualNode,hasOrgans,hasOrganUnits或hasFictitiousUnits
	 * 由于事务的问题，请在Controller中调用
	 * @param organ
	 */
	public void updateHasChildren4Organ(OrganEO organ);
	
	/**
	 * 获取组织
	 *
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Object> getOrgans(Long pageIndex,int pageSize);
	
	/**
	 * 更新排序号
	 */
	public void updateSortNum4RemoveDate();
	
	/**
	 * 获取单位organIds集合，并按sortNum进行排序
	 *
	 * @param organIds
	 * @param isContainsExternal
	 * @return
	 */
	public List<OrganEO> getOrgans(Long[] organIds,Boolean isContainsExternal);
	
	/**
	 * 获取子OrganEO集合
	 *
	 * @param parentIds
	 * @param isContainsExternal
	 * @return
	 */
	public List<OrganEO> getSubOrgans(Long[] parentIds,Boolean isContainsExternal);
	
	/**
	 * 获取子单位部门
	 *
	 * @param parentId
	 * @param parentDn
	 * @param types 需要获取的组织架构类型
	 * @param isOnlyInternal 是否仅获取本平台组织
	 * @param flag  0:为组织管理查询子组织,1：为用户管理查询子组织，2：为角色管理查询子组织
	 * @return
	 */
	public List<OrganNodeVO> getSubOrgans(Long parentId,String parentDn,String[] types,boolean isOnlyInternal, int flag);
	
	/**
	 * 获取organId在organIds的组织
	 *
	 * @param organIds
	 * @return
	 */
	public List<OrganEO> getOrgansByOrganIds(Long[] organIds);
	
	/**
	 * 根据单位ID集合删除人员和单位的关系记录
	 *
	 * @param organIds
	 */
	public void deleteByOrganIds(List<Long> organIds);
	
	
	/**
	 * 获取所有的子孙单位、部门和虚拟部门主键集合
	 *
	 * @param organId
	 * @return
	 */
	public List<Long> getDescendantOrganIds(Long organId);
	
	/**
	 * 根据Dn初始化组织架构路径（从最后一级单位开始）
	 */
	public void initOrganNamesWithDn();
	
	/**
	 * 更新单位编码
	 *
	 * @param organId
	 * @param code
	 */
	public void updateCode(Long organId,String code);
	
	/**
	 * 删除编码
	 *
	 * @param organIds
	 */
	public void deleteCodes(Long[] organIds);
	
	/**
	 * 获取有单位或部门编码的单位部门分页列表
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPagination4Code(PageQueryVO query);
	
	/**
	 * 根据部门organDn获取直属的单位
	 *
	 * @param organDn
	 * @return
	 */
	public OrganEO getUnitByOrganDn(String organDn);
	
	
	/**
	 * 获取单位organIds下名称或拼音中包含blurryNameOrPY的类型在types内的组织完整名称
	 *
	 * @param organIds
	 * @param scope
	 * @param blurryNameOrPY
	 * @return
	 */
	public List<String> getNames( Long[] organIds,int scope,String blurryNameOrPY);
	
	/**
	 * 获取单位unitIds直属的部门，其中不包括子单位的部门
	 *
	 * @param unitIds
	 * @return
	 */
	public List<OrganEO> getOrganUnits(Long[] unitIds);
	
	/**
	 * 验证名称是否可</br>
	 * type：Organ、OrganUnit、null，Organ查询组织名称，OrganUnit查询单位名称，null查询组织和单位名称</br>
	 * 1.组织/单位名称已存在;</br>
	 * 2.名称被系统占用，即有用于正在使用该名称;
	 * @param parentId
	 * @param type 
	 * @param name
	 * @return
	 */
	public boolean isNameUsable(Long parentId,String type,String name);
	
	/**
	 * 排序码是否可用</br>
	 *  type：Organ、OrganUnit、null，Organ查询组织排序码，OrganUnit查询单位排序码，null查询组织和单位排序码</br>
	 * 1.排序码被同级的组织/单位占用;
	 * 2.被系统锁定，即有用户正在使用该排序码;
	 * @param parentId
	 * @param type
	 * @param sortNum
	 * @return
	 */
	public boolean isSortNumUsable(Long parentId,String type,Long sortNum);
	
	/**
	 * 验证组织或单位下是否存在子节点
	 * @param organId
	 * @return
	 */
	public boolean hasOrganSuns(Long parentId);
	
	/**
	 * 保存组织/单位
	 * @param organ
	 */
	public void save(OrganEO organ);
	
	
	/**
	 * 根据主键删除组织/单位，当组织或单位下存在子节点时抛出BusinessException
	 * @param id
	 * @throws BusinessException
	 * @return 返回父组织ID，用于更新缓存
	 */
	public Long delete(Long id) throws BusinessException;
	
	/**
	 * 更新组织/单位
	 * @param organ
	 */
	public OrganEO update(Long organId,OrganEO organ) throws BusinessException;
	
	
	/**
	 * 更新hasSun字段
	 * @param organId
	 * @param hasPersons
	 */
	public void updateHasPersons(Long organId,int hasPersons);
	
	/**
	 * 更新字段hasPersons
	 * @param organId
	 */
	public void updateHasPersons(Long organId);
	
	/**
	 * 根据父组织/单位的主键获取组织/单位
	 * @param parentId
	 * @return
	 */
	public List<OrganEO> getOrgans(Long parentId);
	
	/**
	 * 获取组织/单位
	 *
	 * @param parentDn
	 * @param parentId
	 * @return
	 */
	public List<OrganEO> getLdapOrgansWithOutCache(String parentDn,Long parentId);
	
	/**
	 * 根据父组织/单位的主键获取组织/单位
	 * @param type
	 * @param parentId
	 * @return
	 */
	public List<OrganEO> getOrgans(Long parentId,String type);


	/**
	 * 根据单位的type和名称获取单位
	 * @param name
	 * @param type
	 */
	public List<OrganEO> getOrgansByTypeAndName(String name,String type);
	
	/**
	 * 根据主键到LDAP上获取对象
	 * @param parentId
	 */
	public OrganEO getLdapOrgan(Long organId);
	
	
	
	/**
	 * 根据父组织/单位的主键获取组织/单位
	 * @param parentId
	 * @return
	 */
	public List<OrganEO> getLdapOrgansWithCache(Long parentId);
	
	
	/**
	 * 根据父组织/单位的主键获取组织/单位
	 * @param type
	 * @param parentId
	 * @return
	 */
	public List<OrganEO> getLdapOrgans(Long parentId,String type);
	
	/**
	 * 获取最大排序号
	 * @param parentId
	 * @return
	 */
	public Long getMaxSortNum(Long parentId);
	
	/**
	 * 根据组织/单位ID获取所有的祖先组织和单位
	 * @param organId
	 * @return
	 */
	public List<OrganEO> getAncestors(Long organId);
	
	/**
	 * 根据部门ID获取直属单位
	 * @param organId
	 * @return
	 */
	public OrganEO getDirectlyUpLevelUnit(Long organId);
	
	/**
	 * 获取部门直属单位
	 *
	 * @param organIds
	 * @return
	 */
	public List<OrganEO> getDirrectlyUpLevelUnits(Long[] organIds);
	
	/**
	 * 获取组织或单位下子组织/单位的数量
	 * @param organId
	 * @return
	 */
	public Long getSunOrgansCount(Long organId);
	
	/**
	 * 获取单位下子人员数量
	 * @param organId
	 * @return
	 */
	public Long getSunPersonsCount(Long organId);
	
	/**
	 * 根据单位或部门的dn获取对象
	 *
	 * @param dn
	 * @return
	 */
	public OrganEO getOrganByDn(String dn);
	
	/**
	 * 根据单位或部门的dns获取对象
	 *
	 * @param dns
	 * @return
	 */
	public List<OrganEO> getOrgansByDns(List<String> dns);
	
	/**
	 * 根据单位或部门的dns获取organId集合对象
	 *
	 * @param dn
	 * @return
	 */
	public List<Long> getOrganIdsByDn(String dn);
	
	/**
	 * 获取子单位、子部门/处室和子虚拟部门列表
	 *
	 * @param organIds 父组织、单位的主键数组
	 * @param types 需要获取数据的类型
	 * @param isContainsFictitious 是否包含虚拟部门
	 * @param fuzzyName 模糊查询，针对全名和简称
	 * @return
	 */
	List<OrganEO> getSubOrgans(Long[] organIds,List<String> types,boolean isContainsFictitious,String fuzzyName);
	
	/**
	 * 获取子单位节点
	 *
	 * @param parentId
	 * @param types  返回的节点类型
	 * @param isContainsExternal 是否包含外单位
	 * @return
	 */
	List<OrganEO> getSubNorgans(Long parentId,String[] types,Boolean isContainsExternal);
	
	/**
	 * 组织架构查询，仅用于树查询,用于获取parentIds所有相关联的组织
	 *
	 * @param parentIds  查询的顶层节点
	 * @param types      返回的节点类型-Organ,OrganUnit,Virtual
	 * @param blurryName 查询单位名称-模糊查询
	 * @return
	 */
	List<OrganEO> getConnectedOrgans(Long[] parentIds,List<String> types,String blurryName);
	
	/**
	 *TODO 此方法的实现在组织名称查询时返回为null，原因尚未找到-因此暂时不支持blurryName查询，问题未解决前请勿使用
	 *
	 * @param parentIds  查询的顶层节点
	 * @param types      返回的节点类型-Organ,OrganUnit,Virtual
	 * @param scope      查询范围,使用naming中的SearchControls中的范围：0,1,2
	 * @param blurryName 查询单位名称-模糊查询
	 * @return
	 */
	List<OrganEO> getOrgans(Long[] parentIds,List<String> types,int scope,String blurryName);
	
	/**
	 * 获取所有子虚拟节点和单位,，并按sortNum由小到大排序
	 * @param parentId
	 * @return
	 */
	public List<OrganEO> getSubVirtualNodesAndUnits(Long parentId);
	
	/**
	 * 获取组织organIds的Dn数组
	 *
	 * @param organIds
	 * @return
	 */
	public String[] getDns(Long[] organIds);
	

    /**
     * 迁移
     * @param selectOrgan
     * @param clickOrgan
     */
	public void updateOrgansAndPerson(OrganEO selectOrgan, OrganEO clickOrgan);
	
	
	


	public List<TreeNodeVO> getUnitOrganAndUser(Long unitId);
	
	/**
	 * @param unitId 站点绑定单位id
	 * @return
	 */
	public List<TreeNodeVO> getUnitsAndPersons(Long unitId);


	/**
	 * @param unitId 站点绑定单位id
	 * @return
	 */
	public List<TreeNodeVO> getUnitsAndPersons(Long unitId,String name);
	
	/**
	 * 获取单位下所有的子组织
	 * @param unitId
	 * @return
	 */
	public List<OrganEO> getOrgansByUnitId(Long unitId);

	public List<OrganEO> getOrgansBySiteId(Long siteId);

	/**
	 * 获取单位下所有的子组织 VO
	 * @param unitId
	 * @return
	 */
	public List<OrganVO> getOrgansBySiteId(Long siteId, Boolean isRemoveTop);

	/**
	 * 获取站点下所有的子组织
	 * @param siteId 单位id
	 * @return type  null,则查询站点下所有单位、部门， Organ时 查询站点下所有单位
	 */
	public List<OrganEO> getOrgansByType(Long siteId,String type);
	
	/**
	 * 查询所有单位、人员
	 * @return
	 */
	public List<TreeNodeVO> getAllOrgans();


	/**
	 * 查询所有单位、人员
	 * @return
	 */
	public List<TreeNodeVO> getAllOrgans(String name);
	
	
	/**
	 * 查询所有单位
	 * @return
	 */
	public List<OrganEO> getUnits();
	
	

	/**
	 * 查询某单位dn下 所有单位或所有部门
	 * @return
	 */
	public List<OrganEO> getOrgansByDn(Long unitId,String type);

	/**
	 * 查询父节点
	 * @param id
	 * @return
	 */
	public List<TreeNodeVO> getParentOrgansById(Long id);

	/**
	 * 模糊匹配
	 * @param name
	 * @return
	 */
	public List<TreeNodeVO> getOrgansAndPersons(Long unitId,String name);

	/**
	 * 批量保存组织
	 * @param organs
     */
	public OrganEO saveXlsOrgan(OrganEO organ);

	/**
	 * 查询dn下所有的部门
	 * @param dn
	 * @return
     */
	List<OrganEO> getOrganUnitsByDn(String dn);

	/**
	 * 初始化组织架构cache
	 */
	public void initSimpleOrgansCache();

	/**
	 *  查询organId 下所有已经公开的单位
	 */
	List<OrganEO> getPublicOrgans(Long organId);

	/**
	 * 验证父id 下 type类型的 name 是否存在
	 * @param parentId
	 * @param type
	 * @param name
	 * @return
	 */
	public Boolean isNameExistedDB(Long parentId, String type, String name,Integer count);

	/**
	 * 查询子节点
	 * @param parentId
	 * @param i
     * @return
     */
	List<OrganNodeVO> getSubOrgans(Long parentId, int i);

	List<OrganNodeVO> getOrganNodeVOs(Long[] organIds, int flag);
}
