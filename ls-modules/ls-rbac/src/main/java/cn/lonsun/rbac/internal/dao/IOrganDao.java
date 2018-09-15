package cn.lonsun.rbac.internal.dao;

import java.util.List;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.vo.OrganVO;

/**
 * 组织DAO借口
* @Description: 
* @author xujh 
* @date 2014年9月23日 下午10:12:53
* @version V1.0
 */
public interface IOrganDao {

	public void updateIsPublic(Long... ids);
	
	public List<Object> getOrgans(Long pageIndex,int pageSize);
	
	public void test();
	
	/**
	 * 获取单位organIds集合，并按sortNum进行排序
	 *
	 * @param organIds
	 * @param isContainsExternal
	 * @return
	 */
	public List<OrganEO> getOrgans(Long[] organIds,Boolean isContainsExternal);
	
	/**
	 * 获取OrganEO集合
	 *
	 * @param parentId
	 * @param isContainsExternal
	 * @return
	 */
	public List<OrganEO> getSubOrgans(Long[] parentIds,Boolean isContainsExternal);
	
	/**
	 * 获取organId在organIds的组织
	 *
	 * @param organIds
	 * @return
	 */
	public List<OrganEO> getOrgansByOrganIds(Long[] organIds);
	
	/**
	 * 根据单位ID集合删除单位记录
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
	 * 更新字段
	 *
	 * @param hasVirtualNode
	 * @param hasOrgans
	 * @param hasOrganUnits
	 * @param hasFictitiousUnits
	 * @param organId
	 */
	public void updateHasOrgans(Integer hasVirtualNode,Integer hasOrgans,Integer hasOrganUnits,Integer hasFictitiousUnits,Long organId);
	
	/**
	 * 更新单位编码
	 *
	 * @param organId
	 * @param code
	 */
	public void updateCode(Long organId,String code);
	
	/**
	 * 删除单位编码
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
	 * 更新单位或部门(organDn)下所有的子单位和子部门的isExternal属性
	 * @param organDn
	 * @param isExternal
	 * @return
	 */
	public int updateIsExternal(String organDn,Boolean isExternal);
	
	/**
	 * 获取所有的部门关键信息
	 *
	 * @param type      单位类型
	 * @param isFictitious  是否虚拟部门
	 * @return
	 */
	public List<?> getOrganInfos(String type,Integer isFictitious);
	
	/**
	 * 获取组织架构信息
	 *
	 * @param type
	 * @param isFictitious
	 * @param platformCode
	 * @return
	 */
	public List<?> getPersonInfosByPlatformCode(String type,Integer isFictitious,String platformCode);
	/**
	 * 获取组织架构信息
	 *
	 * @param type
	 * @param isFictitious
	 * @param platformCode
	 * @return
	 */
	public List<?> getPersonInfosByPlatformCode(Long[] organIds, String type,Integer isFictitious,String platformCode);

	/**
	 * 获取单位dns下姓名或拼音中包含blurryNameOrPY的完整姓名
	 *
	 * @param dns
	 * @param scope
	 * @param blurryNameOrPY
	 * @return
	 */
	public List<String> getNames(String[] simpleDns,int scope,String blurryNameOrPY);
	
	/**
	 * 组织架构查询，仅用于树查询,用于获取rootDns下所有相关联的组织
	 *
	 * @param rootDns  查询的顶层节点
	 * @param types      返回的节点类型-Organ,OrganUnit,Virtual
	 * @param name
	 * @return
	 */
	List<OrganEO> getConnectedOrgans(List<String> rootDns,List<String> types,String name);
	
	/**
	 * 获取单位unitIds直属的部门，其中不包括子单位的部门
	 *
	 * @param unitIds
	 * @return
	 */
	public List<OrganEO> getOrganUnits(Long[] unitIds);
	
	/**
	 * 更新组织(organId)下的hasPersons字段
	 * @param organId 
	 * @param hasPersons
	 */
	public void updateHasPersons(Long organId,Integer hasPersons);
	
	/**
	 * 根据父组织/单位的主键获取组织/单位
	 * @param parentId
	 */
	public List<OrganEO> getOrgans(Long parentId);
	
	/**
	 * 根据父组织/单位的主键获取组织/单位
	 * @param parentId
	 * @param type
	 */
	public List<OrganEO> getOrgans(Long parentId,String type);

	/**
	 * 根据单位的type和名称获取单位
	 * @param name
	 * @param type
	 */
	public List<OrganEO> getOrgansByTypeAndName(String name,String type);
	
	/**
	 * 获取最大排序号
	 * @param parentId
	 * @return
	 */
	public Long getMaxSortNum(Long parentId);
	
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
	 * 根据单位或部门的dns获取对象，并按sortNum进行排序
	 *
	 * @param dns
	 * @return
	 */
	public List<OrganEO> getOrgansByDns(List<String> dns);
	
	/**
	 * 根据dn获取所有子单位部门的主键集合
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

	public List<OrganEO> getAllOrgans(String type);

	public List<OrganEO> getOrgansByDn(String dn,String type);

	public List<OrganEO> getOrgansByType(Long siteId,String type);

	public List<OrganEO> getOrgansBySiteId(Long siteId);

	/**
	 * 获取父节点
	 * @param id
	 * @return
	 */
	public List<OrganEO> getParentOrgansById(Long id);

	List<OrganEO> getOrganUnitsByDn(String dn);

	List<OrganEO> getPublicOrgans(String dn);

	List<OrganVO> getOrganVOsByDn(String dn, Boolean isRemoveTop);
}
