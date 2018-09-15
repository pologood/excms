

package cn.lonsun.rbac.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.Person4NoticeVO;
import cn.lonsun.common.vo.PersonInfoVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.vo.PersonQueryVO;

import java.util.List;
import java.util.Map;

/**
 * 人员服务类
 *
 * @author xujh
 */
public interface IPersonService extends IMockService<PersonEO> {


    /**
     * 验证原始密码是否正确
     *
     * @return
     */
    public Boolean isOriginalPasswordExisted(Long userId, String password);

    /**
     * 为通知公告全平台发送获取人员信息，其他业务请慎用
     *
     * @return
     */
    public List<Person4NoticeVO> getPersons4Notice();

    /**
     * 验证手机号是否已存在，约定手机号格式正确
     *
     * @param personId 如果不为null，表示此personId对应的人员除外
     * @param mobile
     * @return
     */
    public boolean isMobileExisted(Long personId, String mobile);

    /**
     * 还原用户，从删除到正常状态
     *
     * @param personId
     * @return
     */
    public PersonEO restore(Long personId);

    /**
     * 获取当前系统的人员数量
     *
     * @return
     */
    public Long getCount4CurrentPlatform();

    /**
     * 获取部门/虚拟部门下人员的数量
     *
     * @param organId
     * @return
     */
    public Long getSubPersonCount(Long organId);

    /**
     * 将用户修改为宿主用户
     *
     * @param personId
     * @param srcPersonId 原宿主人员ID
     */
    public void updateToMainPerson(Long personId, Long srcPersonId);

    /**
     * 获取部门或虚拟部门下的人员
     *
     * @param parentId
     * @param parentDn
     * @return
     */
    public List<PersonNodeVO> getSubPersons(Long parentId, String parentDn);

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
    public List<PersonEO> getPersonsByOrganDn(String organDn, String name);

    /**
     * 删除单位/部门organDn下的所有人员
     *
     * @param organDn
     */
    public void deleteByOrganDn(String organDn);

    /**
     * 获取人员列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Object> getPersons(Long pageIndex, int pageSize);


    /**
     * 获取已删除的记录
     *
     * @param query
     * @return
     */
    public Pagination getDeletedPersonsPage(PersonQueryVO query);

    /**
     * 根据模糊的姓名或拼音获取完整的姓名集合
     *
     * @param organIds
     * @param blurryNameOrPY
     * @return
     */
    public List<String> getNames(Long[] organIds, String blurryNameOrPY);

    /**
     * 保存用户信息和账号信息
     *
     * @param node
     * @return
     */
    public PersonEO savePersonAndUser(PersonNodeVO node);

    /**
     * 单位管理员更新用户
     *
     * @param node
     * @return
     */
    public List<PersonEO> updatePersonAndUser(PersonNodeVO node);

    /**
     * 更新用户
     *
     * @param node
     * @return
     */
    public List<PersonEO> updatePersonAndUser4Unit(PersonNodeVO node);

    /**
     * 更新个人信息
     *
     * @param node
     * @return
     */
    public PersonEO updatePersonDetails(PersonNodeVO node);

    /**
     * Sql分页查询
     *
     * @param query
     * @param roleId
     * @param searchText
     * @return
     */
    public Pagination getPage4RoleBySql(PageQueryVO query, Long roleId, String searchText);

    /**
     * 获取角色Code获取人员信息
     *
     * @param roleCode
     * @param unitId
     * @return
     */
    public List<PersonEO> getPersonsByRoleCodeAndUnitId(String roleCode, Long unitId);

    /**
     * 根据角色编码和用户ID获取人员
     *
     * @param roleCode
     * @param userId
     * @return
     */
    public List<PersonEO> getPersonsByRoleCodeAndUserId(String roleCode, Long userId);

    /**
     * 根据角色Id获取对应的人员信息
     *
     * @param roleId
     * @return
     */
    public List<PersonEO> getPersonsByRoleId(Long roleId);

    /**
     * 获取角色Code获取人员信息
     *
     * @param roleCode
     * @param unitIds
     * @return
     */
    public List<PersonEO> getPersonsByRoleCodeAndOrganIds(String roleCode, Long[] organIds);

    /**
     * 根据userId获取非兼职的personId信息
     *
     * @param userId
     * @return
     */
    public PersonEO getUnpluralisticPersons(Long userId);

    /**
     * 分页查询
     *
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
    @Deprecated
    public Pagination getPage(PersonQueryVO query, Long[] roleIds);

    /**
     * 获取excel导出数据
     *
     * @param organId
     * @param subUid
     * @param name
     * @param organName
     * @param sortField
     * @param sortOrder
     * @return
     */
    public List<PersonNodeVO> getExcelResults(Long organId, Long[] roleIds,
                                              String searchText, String sortField, String sortOrder);

    /**
     * 保存Person
     *
     * @param person
     * @param user
     * @param roleIds
     * @param roleNames
     * @throws BusinessException
     */
    public void save(PersonEO person, UserEO user, Long[] roleIds, String[] roleNames)
        throws BusinessException;

    /**
     * 更新兼职人员和用户信息
     *
     * @param person
     * @param userId
     * @param roleIds
     * @param organNames
     * @return
     */
    public List<PersonEO> updatePluralistic(PersonEO person, Long userId, List<Long> roleIds, List<String> roleNames);

    /**
     * 根据主键删除person</br>
     * 如果是非兼职的person，那么需要判断是否存在兼职的情况，如果存在就先删除兼职的记录，然后再删除id对应的person
     *
     * @param id
     * @return 返回人员所属的组织ID，用于更新缓存
     */
    public Map<String, List<Object>> deletePersons(Long id);

    /**
     * 根据主键删除person</br>
     * 如果是非兼职的person，那么需要判断是否存在兼职的情况，如果存在就先删除兼职的记录，然后再删除id对应的person
     *
     * @param ids
     * @return 返回被删除人员所属的组织ID集合，用于更新缓存
     */
    public List<Long> deletePersons(List<Long> ids);

    /**
     * 根据主键删除person</br>
     * 如果是非兼职的person，那么需要判断是否存在兼职的情况，如果存在就抛出异常提示，否则删除id对应的person
     *
     * @param personId
     * @return 返回人员所属的组织ID，用于更新缓存
     */
    public Long delete(Long personId);

    /**
     * 根据主键删除person</br>
     * 如果是非兼职的person，那么需要判断是否存在兼职的情况，如果存在就抛出异常提示，否则删除id对应的person
     *
     * @param ids
     * @return 返回人员所属的组织ID，用于更新缓存
     */
    public List<Long> delete(Long[] personIds);

    /**
     * 根据personId获取该person在其他单位兼职的person记录
     *
     * @param personId
     * @return
     */
    public List<PersonEO> getPluralisticPersons(Long personId);

    /**
     * 更新非兼职person，返回被修改的所有对象
     *
     * @param person
     * @return
     */
    public List<PersonEO> update(PersonEO person);


    /**
     * 将人员移动到组织organId下
     *
     * @param personId
     * @param organId
     * @param isRemoveRoles 是否删除用户原有的角色
     * @return 返回源组织ID，用户更新cache
     * @throws BusinessException
     */
    public Long update4move(Long personId, Long organId, boolean isRemoveRoles) throws BusinessException;

    /**
     * 根据单位的id获取单位下所有的人员列表
     *
     * @param organId
     * @return
     */
    public List<PersonEO> getPersons(Long organId);

    /**
     * 根据单位的id获取LDAP单位下所有的人员列表，只返回LDAP和数据库中同时拥有的，未同步的需等同步后再显示
     *
     * @param organId
     * @param ignorePluralistics 是否忽略兼职人员
     * @return
     */
    public List<PersonEO> getLdapPersons(Long organId, boolean ignorePluralistics);

    /**
     * 根据单位的id获取LDAP单位下所有的人员列表，只返回LDAP和数据库中同时拥有的，未同步的需等同步后再显示
     *
     * @param organIds
     * @return
     */
    public List<PersonEO> getSubPersons(Long[] organIds);

    /**
     * 获取部门下的人员，用户状态为statuses中的一个，并按sortNum进行排序
     *
     * @param organId,不允许为空
     * @param statuses，不允许为空
     * @return
     */
    public List<PersonEO> getSubPersonsFromDB(Long organId, String[] statuses);

    /**
     * 将主键为personId的人员信息拷贝一份到主键为organId的单位下，并设置为兼职人员
     *
     * @param node
     * @return PersonEO
     * @throws BusinessException
     */
    public PersonEO savePluralisticPerson(PersonNodeVO node)
        throws BusinessException;

    /**
     * 人员名称是否可用</br> 1.同单位下已存在相同的姓名;</br> 2.姓名被系统锁定,即有用户正在使用该姓名;
     *
     * @param parentId
     * @param name
     * @return
     */
    public boolean isNameExisted(Long parentId, String name);

    /**
     * 获取最大排序号，如果未取到，那么返回0
     *
     * @param organId
     * @return
     */
    public Long getMaxSortNum(Long organId);

    /**
     * 获取用户在parentId下的子内容
     *
     * @param userId
     * @param parentId
     * @param type
     * @return
     */
    public List<IndicatorEO> getSysButtons(Long userId, Long parentId, String type);

    /**
     * 根据组织ID和用户ID获取person信息
     *
     * @param organId
     * @param userId
     * @return
     */
    public PersonEO getPersonByUserId(Long organId, Long userId);

    /**
     * 根据组织ID和uid获取person信息
     *
     * @param organId
     * @param uid
     * @return
     */
    public PersonEO getPersonByUid(Long organId, String uid);

    public PersonInfoVO getPersonInfoByUid(Long organId, String uid);

    /**
     * 获取person集合,组织ID需要和用户ID在顺序上匹配
     *
     * @param organIds
     * @param userIds
     * @return
     */
    public List<PersonEO> getPersonsByUserIds(Long[] organIds, Long[] userIds);

    /**
     * 获取根节点为organIds的单位/部门下，姓名中包含了name的人员
     *
     * @param organIds
     * @param name
     * @return
     */
    public Map<String, List<?>> getPersonsAndOrganDnsByPersonName(Long[] organIds, String name);

    /**
     * 获取单位organDns下姓名中包含name的所有人员的dn集合
     *
     * @param organDns
     * @param name
     * @return
     */
    public List<String> getDnsByName(String[] organDns, String name);

    /**
     * TODO 操作信息兼职用户
     *
     * @param pn
     */
    public PersonEO savePluralisticPersonOld(PersonNodeVO pn) throws BusinessException;

    /**
     * 更新ldap用户
     *
     * @param p
     */
    public void savePersonLdap(PersonEO p);

    /**
     * 删除ldap
     *
     * @param p
     */
    public void deleteLdap(String dn);

    public List<PersonEO> getAllPersons();

    public List<PersonEO> getPsersonsByUnitId(Long unitId);

    /**
     * 桌面上修改个人信息
     *
     * @param node
     * @return
     */
    public List<PersonEO> updatePersonInfo(PersonNodeVO node);

    /**
     * 模糊匹配
     *
     * @param name
     * @return
     */
    public List<PersonEO> getPersonsByLikeName(Long unitId, String name);

    public void saveXlsPerson(PersonNodeVO person);

    /**
     * 初始化选人界面的Cache，只获取本平台的人员
     */
    public void initSimplePersonsCache();

    /**
     * 根据单位的id获取单位下所有的人员列表
     *
     * @param organId
     * @return
     */
    public List<PersonEO> getPersonsByDn(Long organId);
}
