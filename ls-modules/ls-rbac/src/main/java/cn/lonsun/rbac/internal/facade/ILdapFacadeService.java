package cn.lonsun.rbac.internal.facade;

import java.util.List;

import cn.lonsun.common.vo.SimpleNodeVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.vo.Node4SaveOrUpdateVO;
import cn.lonsun.rbac.vo.TreeNodeCacheVO;

/**
 * 系统管理组织人员管理服务接口
 * 
 * @author xujh
 * @date 2014年9月24日 上午11:02:20
 * @version V1.0
 */
public interface ILdapFacadeService {
	
	/**
	 * 获取单位、部门/处室
	 *
	 * @param nodeTypes
	 * @param organIds
	 * @param isContainsExternal 是否包含外单位，默认为false
	 * @return
	 */
	public List<TreeNodeVO> getNodes(String[] nodeTypes, Long[] organIds,Boolean isContainsExternal);
	
	/**
	 * 获取单位、部门/处室，用于在cache中缓存
	 *
	 * @param nodeTypes
	 * @param organIds
	 * @param isContainsExternal 是否包含外单位，默认为false
	 * @return
	 */
	public List<TreeNodeCacheVO> getCacheNodes(String[] nodeTypes, Long[] organIds,Boolean isContainsExternal);
	
	/**
	 * 获取所有子单位,，并按sortNum由小到大排序
	 * @param parentId
	 * @param targetType   0-用户获取单位树,1-用户获取获取单位和部门树,
	 * 2-用户获取单位、部门和虚拟部门树,3-用户获取单位、部门、虚拟部门和人员树，4-用户获取单位和角色
	 * @return
	 */
	public List<OrganNodeVO> getUnitNodes(Long parentId,int targetType);
	
	/**
	 * 获取Organ(organId)下的子Organ<br/>
	 * @param organId
	 * @param flag,0:为组织管理查询子组织,1：为用户管理查询子组织，2：为角色管理查询子组织
	 * @return
	 */
	public List<OrganNodeVO> getSubOrgans(Long organId,int flag);
	
	/**
	 * 系统管理中获取单位角色
	 *
	 * @param unitId
	 * @return
	 */
	public List<TreeNodeVO> getNodes4Roles(Long unitId);
	/**
	 * 获取Organ(organId)下的子Person
	 * @param organId
	 * @return
	 */
	public List<PersonNodeVO> getSubPersons(Long organId);
	
	/**
	 * 获取Organ(organId)下的子Organ和Person
	 * @param organId
	 * @return
	 */
	public List<Object> getSubNodes(Long organId);
	
	/**
	 * 保存兼职人员
	 * @param node
	 * @return
	 */
	public Node4SaveOrUpdateVO savePluralistic(PersonNodeVO node);
	
	
	/**
	 * 更新兼职person
	 * @param node
	 * @return
	 */
	public Node4SaveOrUpdateVO updatePluralisticPerson(PersonNodeVO node);
	
	/**
	 * 为单位（organId）获取兼职人员（personId）信息
	 * @param personId
	 * @param organId
	 * @return
	 */
	public PersonNodeVO getPluralistic(Long personId, Long organId);
	
	/**
	 * 获取person信息
	 * @param personId
	 * @return
	 */
	public PersonNodeVO getPerson(Long personId);
	
	/**
	 * 获取初始person提供给前端
	 * @param organId
	 * @return
	 */
	public PersonNodeVO getEmptyPerson(Long organId);

	/**
	 * 获取子组织节点VO
	 *
	 * @param isContainsExternal
	 * @param nodeTypes
	 * @param parentId
	 * @return
	 */
	public List<TreeNodeVO> getSubNodes(Boolean isContainsExternal,String[] nodeTypes, Long parentId,String[] statuses);
	
	/**
	 * 根据用户姓名和所属单位主键获取所有的人员姓名
	 *
	 * @param name
	 * @param organIds
	 * @param count
	 * @return
	 */
	public List<String> getPersonNames(String name,int count, Long[] organIds);
	
	/**
	 * 根据用户姓名和所属单位主键获取人员信息
	 *
	 * @param name
	 * @param organIds
	 * @param scope
	 * @return
	 */
	public List<SimpleNodeVO> getSimpleNodes(String name,Long[] organIds,int scope);
	
	/**
	 * 获取子单位以及单位中的角色，其中只返回已经跟用户绑定过的角色
	 *
	 * @param parentId
	 * @param roleIds
	 * @return
	 */
	public List<TreeNodeVO> getNodes4Roles(Long parentId,Long[] roleIds);
	
	
	/**
	 * 根据人员姓名获取树节点
	 *
	 * @param name
	 * @param organIds
	 * @return
	 */
	public List<TreeNodeVO> getSubNodesByPersonName(String name, Long[] organIds);
	
	/**
	 * 根据组织单位名称获取树节点
	 *
	 * @param organIds
	 * @param name
	 * @param types
	 * @return
	 */
	public List<TreeNodeVO> getSubNodesByOrganName(Long[] organIds,String name,String[] types);
	
	/**
	 * 根据角色名称获取树节点
	 *
	 * @param name - 角色名称
	 * @param organIds - 单位范围限制
	 * @param roleIds  - 角色范围限制
	 * @return
	 */
	public List<TreeNodeVO> getSubNodesByRoleName(String name, Long[] organIds,Long[] roleIds);
	
	
}
