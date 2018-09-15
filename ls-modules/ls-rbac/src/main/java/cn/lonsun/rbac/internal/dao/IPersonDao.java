package cn.lonsun.rbac.internal.dao;

import java.util.List;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.Person4NoticeVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.vo.PersonQueryVO;

public interface IPersonDao extends IMockDao<PersonEO> {
	
	/**
	 * 为通知公告全平台发送获取人员信息，其他业务请慎用
	 *
	 * @return
	 */
	public List<Person4NoticeVO> getPersons4Notice();
	
	/**
	 * 获取部门/虚拟部门下人员的数量
	 *
	 * @param organId
	 * @return
	 */
	public Long getSubPersonCount(Long organId);
	
	/**
	 * 获取部门下的人员，用户状态为statuses中的一个，并按sortNum进行排序
	 *
	 * @param organId,不允许为空
	 * @param statuses，不允许为空
	 * @return
	 */
	public List<PersonEO> getSubPersons(Long organId, String[] statuses);
	
	/**
	 * 获取当前系统的人员数量，不包含兼职用户
	 *
	 * @param platformCode
	 * @return
	 */
	public Long getCountByPlatformCode(String platformCode);
	
	/**
	 * 根据DN查询人员
	 *
	 * @param organDn
	 * @return
	 */
	public List<PersonEO> getPersonsByOrganDn(String organDn);

	/**
	 * 根据DN查询人员
	 *
	 * @param organDn
	 * @return
	 */
	public List<PersonEO> getPersonsByOrganDn(String organDn,String name);
	
	/**
	 * 删除单位/部门organDn下的所有人员
	 *
	 * @param organDn
	 */
	public void deleteByOrganDn(String organDn);
	
	/**
	 * 获取PersonEO集合
	 *
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Object> getPersons(Long pageIndex,int pageSize);
	
	/**
	 * 根据部门单位Dn获取人员信息
	 *
	 * @param organIds
	 * @return
	 */
	public List<?> getPersonInfos(List<String> organDns);
	
	/**
	 * 根据部门单位Dn获取平台内人员信息
	 *
	 * @param organIds
	 * @param platformCode
	 * @return
	 */
	public List<?> getPersonInfosByPlatformCode(List<String> organDns,String platformCode);
	
	/**
	 * 根据模糊的姓名或拼音获取完整的姓名集合
	 *
	 * @param simpleOrganDns
	 * @param blurryNameOrPY
	 * @return
	 */
	public List<String> getNames(String[] simpleOrganDns,String blurryNameOrPY);
	
	/**
	 * sql分页查询
	 *
	 * @param query
	 * @param roleId
	 * @param searchText
	 * @return
	 */
	public Pagination getPage4RoleBySql(PageQueryVO query,Long roleId,String searchText);
	
	/**
	 * 获取角色Code获取人员信息
	 *
	 * @param roleCode
	 * @param organUnitIds
	 * @return
	 */
	public List<PersonEO> getPersonsByRoleCode(String roleCode,Long[] organUnitIds);
	
	/**
	 * 获取单位（只去直属的，去除下属单位的）或部门下角色编码为roleCode的人员信息
	 *
	 * @param roleCode
	 * @param organDns
	 * @return
	 */
	public List<PersonEO> getPersonsByRoleCode(String roleCode,List<String> organDns);
	
	/**
	 * 根据角色编码和用户ID获取人员
	 *
	 * @param roleCode
	 * @param userId
	 * @return
	 */
	public List<PersonEO> getPersonsByRoleCodeAndUserId(String roleCode,Long userId);
	
	/**
	 * 根据角色Id获取对应的人员信息
	 *
	 * @param roleId
	 * @return
	 */
	public List<PersonEO> getPersonsByRoleId(Long roleId);
	
	/**
	 * 根据userId获取非兼职的personId信息
	 * @param userId
	 * @return
	 */
	public PersonEO getUnpluralisticPersons(Long userId);
	
	/**
	 * 人员(personId)姓名是否为name
	 * @param personId
	 * @param name
	 * @return
	 */
	public boolean isNameRight(Long personId,String name);
	
	/**
	 * 获取excel导出数据
	 * @param organIds
	 * @param subUid
	 * @param name
	 * @param organName
	 * @param sortField
	 * @param sortOrder
	 * @return
	 */
	public List<PersonNodeVO> getExcelResults(Long[] organIds,
			Long[] roleIds, String searchText, String sortField, String sortOrder);
	
	/**
	 * 分页查询
	 * @param query
	 * @return
	 */
	
	public Pagination getPage(PersonQueryVO query);
	
	/**
	 * 分页查询
	 *
	 * @param query
	 * @return
	 */
	public Pagination getInfoPage(PersonQueryVO query);
	/**
	 * 分页查询
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPage(PersonQueryVO query,Long[] roleIds,Long[] organIds);
	
	
	/**
	 * 获取已删除的记录
	 *
	 * @param query
	 * @return
	 */
	public Pagination getDeletedPersonsPage(PersonQueryVO query);
	/**
	 * 根据personId获取该person在其他单位兼职的person记录
	 * @param personId
	 * @return
	 */
	public List<PersonEO> getPluralisticPersons(Long personId);
	
	/**
	 * 根据单位的id获取单位下所有的人员列表
	 * @param organId
	 */
	public List<PersonEO> getPersons(Long organId);
	
	/**
	 * 根据单位的dns获取单位下所有的人员列表
	 * @param dns
	 */
	public List<PersonEO> getPersonsByDns(List<String> dns);
	
	/**
	 * 单位organId下，sortNum是否被其他人员占用
	 * 如果sortNum被该单位的其他人员占用，那么抛出异常提示
	 * @param organId
	 * @param sortNum
	 * @return
	 */
	public boolean isSortNumExisted(Long organId,Long sortNum);
	
	/**
	 * 获取最大排序号
	 * @param organId
	 * @return
	 */
	public Long getMaxSortNum(Long organId);
	
	/**
	 * 根据组织ID和用户ID获取person信息
	 * @param organId
	 * @param userId
	 * @return
	 */
	public PersonEO getPersonByUserId(Long organId,Long userId);
	
	/**
	 * 根据组织ID和uid获取person信息，如果组织Id为空，那么默认去非兼职的人员
	 * @param organId
	 * @param uid
	 * @return
	 */
	public PersonEO getPersonByUid(Long organId,String uid);
	
	/**
	 * 获取person集合,组织ID需要和用户ID在顺序上匹配
	 * @param organIds
	 * @param userIds
	 * @return
	 */
	public List<PersonEO> getPersonsByUserIds(Long[] organIds,Long[] userIds);

	public List<PersonEO> getAllPersons();

	public List<PersonEO> getPsersonsByUnitId(Long unitId);

	/**
	 * 模糊匹配
	 * @param name
	 * @return
	 */
	public List<PersonEO> getPersonsByLikeName(Long unitId,String name);

	/**
	 * 获取dn下的所有用户
	 * @param dn
	 * @return
     */
	List<PersonEO> getPersonsByDn(String dn);
}
