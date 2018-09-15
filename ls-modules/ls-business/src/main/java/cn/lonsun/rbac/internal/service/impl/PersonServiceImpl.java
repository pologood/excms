package cn.lonsun.rbac.internal.service.impl;

import cn.lonsun.common.enums.RoleCodes;
import cn.lonsun.common.sso.util.DESedeUtil;
import cn.lonsun.common.sso.util.EncryptKey;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.Person4NoticeVO;
import cn.lonsun.common.vo.PersonInfoVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Icon;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.exception.RecordsException;
import cn.lonsun.core.util.FileUtils;
import cn.lonsun.core.util.JSONHelper;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.ldap.internal.exception.NameRepeatedException;
import cn.lonsun.ldap.internal.service.ILdapPersonService;
import cn.lonsun.ldap.internal.service.ILdapUserService;
import cn.lonsun.ldap.internal.util.Constants;
import cn.lonsun.ldap.internal.util.LDAPUtil;
import cn.lonsun.ldap.internal.util.LdapOpenUtil;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.log.internal.service.ILogService;
import cn.lonsun.rbac.internal.dao.IPersonDao;
import cn.lonsun.rbac.internal.entity.*;
import cn.lonsun.rbac.internal.service.*;
import cn.lonsun.rbac.internal.vo.PersonVO;
import cn.lonsun.rbac.servlet.InitServlet;
import cn.lonsun.rbac.utils.PinYinUtil;
import cn.lonsun.rbac.utils.RoleCodeVO;
import cn.lonsun.rbac.utils.TimeCutUtil;
import cn.lonsun.rbac.vo.PersonQueryVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("personService")
public class PersonServiceImpl extends MockService<PersonEO> implements
        IPersonService {
    @Autowired
    private ILdapPersonService ldapPersonService;
    @Autowired
    private IOrganService organService;
    @Autowired
    private IPersonDao personDao;
    @Autowired
    private ILdapUserService ldapUserService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOrganPersonService organPersonService;
    @Autowired
    private IRoleAssignmentService roleAssignmentService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IIndicatorService indicatorService;
    @Autowired
    private IPersonRelationshipService personRelationshipService;
    @Autowired
    private IPlatformService platformService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ILogService logService;

    @Override
    public List<Person4NoticeVO> getPersons4Notice() {
        return personDao.getPersons4Notice();
    }

    @Override
    public Boolean isOriginalPasswordExisted(Long userId, String password) {
        boolean isExisted = false;
        if (StringUtils.isEmpty(password)) {
            return isExisted;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("password", DigestUtils.md5Hex(password));
        params.put("recordStatus", RecordStatus.Normal.toString());
        UserEO ps = userService.getEntity(UserEO.class, params);
        if (AppUtil.isEmpty(ps)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isMobileExisted(Long personId, String mobile) {
        boolean isExisted = false;
        if (StringUtils.isEmpty(mobile)) {
            return isExisted;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("recordStatus", RecordStatus.Normal.toString());
        List<PersonEO> ps = getEntities(PersonEO.class, params);
        //新增人员时验证
        if (personId == null) {
            if (ps != null && ps.size() > 0) {
                isExisted = true;
            }
        } else {//更新人员时验证
            //宿主和兼职有相同的手机号
            if (ps != null && ps.size() > 0) {
                for (PersonEO person : ps) {
                    Long pId = person.getPersonId();
                    Long srcPersonId = person.getSrcPersonId();
                    //srcPersonId 为空表示为宿主用户，否则是兼职用户
                    if (srcPersonId == null) {
                        if (pId.longValue() != personId.longValue()) {
                            isExisted = true;
                            break;
                        }
                    } else {
                        if (srcPersonId.longValue() != personId.longValue()) {
                            isExisted = true;
                            break;
                        }
                    }
                }
            }
        }

        return isExisted;
    }

    @Override
    public PersonEO restore(Long personId) {
        //1.人员
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("personId", personId);
        params.put("recordStatus", RecordStatus.Removed.toString());
        PersonEO person = getEntity(PersonEO.class, params);
        //如果人员所在的组织架构已变更，那么不允许恢复
        Long organId = person.getOrganId();
        OrganEO organ = organService.getEntity(OrganEO.class, organId);
        if (organ == null) {
            throw new BaseRunTimeException("人员所在部门已删除，恢复失败");
        }
        //如果账号已经被占用，那么自动生成新的账号
        String uid = person.getUid();
        //如果是兼职人员，先需要验证宿主人员是否已正常，如果是删除状态，那么不允许恢复
        boolean isPluralistic = person.getIsPluralistic();
        if (isPluralistic) {
            Map<String, Object> params0 = new HashMap<String, Object>();
            params0.put("srcPersonId", personId);
            params0.put("recordStatus", RecordStatus.Normal.toString());
            PersonEO src = getEntity(PersonEO.class, params);
            if (src == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "请先恢复宿主用户");
            }
        } else {
            //如果账号已经被占用，那么自动生成新的账号
            boolean isUidExisted = userService.isUidExisted(uid);
            String name = person.getName();
            if (isUidExisted) {
                if (!StringUtils.isEmpty(name)) {
                    String tempUid = PinYinUtil.cn2Spell(name.trim());
                    uid = tempUid;
                    int times = 0;
                    while (userService.isUidExisted(uid)) {
                        times++;
                        uid = tempUid + times;
                    }
                    ;
                }
            }
        }
        person.setUid(uid);
        person.setRecordStatus(RecordStatus.Normal.toString());
        updateEntity(person);
        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("userId", person.getUserId());
        UserEO user = userService.getEntity(UserEO.class, params2);
        String password = null;
        //通过对称加密算法解密desc加密的密码
        String descPassword = user.getDesPassword();
        String key = EncryptKey.getInstance().getKey();
        if (StringUtils.isEmpty(descPassword)) {
            try {
                //密码对称加密解密
                password = DESedeUtil.decrypt(descPassword, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //如果不存在可逆密码，设置为与账号相同
            password = user.getUid();
            try {
                //密码对称加密解密
                descPassword = DESedeUtil.encrypt(password, key);
                user.setDesPassword(descPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String md5Pwd = DigestUtils.md5Hex(password);
        if (RecordStatus.Removed.toString().equals(user.getRecordStatus())) {
            user.setUid(uid);
            user.setPassword(md5Pwd);
            user.setRecordStatus(RecordStatus.Normal.toString());
            userService.updateEntity(user);
        }
        //2.人员与组织关系
        //获取人员所在的单位部门所有的主键
        String dn = person.getDn();
        List<String> dns = new ArrayList<String>();
        while (!Constants.ROOT_DN.equals(dn)) {
            dn = org.apache.commons.lang3.StringUtils.substringAfter(dn, ",");
            if (!Constants.ROOT_DN.equals(dn)) {
                dns.add(dn);
            }
        }
        Map<String, Object> params3 = new HashMap<String, Object>();
        params3.put("personId", person.getPersonId());
        params3.put("organDn", dns);
        params3.put("recordStatus", RecordStatus.Removed.toString());
        List<OrganPersonEO> ops = organPersonService.getEntities(OrganPersonEO.class, params3);
        if (ops != null && ops.size() > 0) {
            for (OrganPersonEO op : ops) {
                op.setRecordStatus(RecordStatus.Normal.toString());
                organPersonService.updateEntity(op);
            }
        }
        // 给用户赋予一个初始角色
        RoleEO role = roleService.getRoleByCode(RoleCodes.public_base_user.toString());
        if (role != null) {
            roleAssignmentService.save(person.getOrganId(), user.getUserId(), new Long[]{role.getRoleId()});
        }
        if(LdapOpenUtil.isOpen) {
            //3.LDAP
            ldapPersonService.save(person, user);
        }
        //4.RTX
        Object[] objs = new Object[]{person, password};
        return person;
    }

    @Override
    public Long getCount4CurrentPlatform() {
        String platformCode = platformService.getCurrentPlatform().getCode();
        return personDao.getCountByPlatformCode(platformCode);
    }

    /**
     * 获取部门/虚拟部门下人员的数量
     *
     * @param organId
     * @return
     */
    public Long getSubPersonCount(Long organId) {
        if (organId == null) {
            throw new NullPointerException();
        }
        return personDao.getSubPersonCount(organId);
    }

    @Override
    public void updateToMainPerson(Long personId, Long srcPersonId) {
        //获取主用户
        PersonEO srcPerson = getEntity(PersonEO.class, srcPersonId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("srcPersonId", srcPersonId);
        params.put("recordStatus", RecordStatus.Normal.toString());
        //所有的兼职用户
        List<PersonEO> pluralisticPersons = getEntities(PersonEO.class, params);
        //更新主用户
        srcPerson.setSrcPersonId(personId);
        srcPerson.setIsPluralistic(true);
        //设置新的主用户信息
        PersonEO person = null;
        //更新兼职srcPersonId与isPluralistic属相
        for (PersonEO p : pluralisticPersons) {
            //主用户设置
            if (p.getPersonId().longValue() == personId.longValue()) {
                p.setSrcPersonId(null);
                p.setIsPluralistic(false);
                person = p;
            } else {
                p.setSrcPersonId(personId);
            }
        }
        //更新DB中的信息
        updateEntity(srcPerson);
        updateEntities(pluralisticPersons);
        UserEO user = userService.getEntity(UserEO.class, person.getUserId());
        user.setPersonDn(person.getDn());
        userService.updateEntity(user);
        //更新LDAP中的是否兼职属性
        String uid = user.getUid();
        String md5Pwd = user.getPassword();
        if(LdapOpenUtil.isOpen) {
            //账号信息存储在宿主下，另外，需要更新UserEO中的personDn
            ldapPersonService.updateIsPluralistic(srcPerson.getDn(), true, uid, md5Pwd);
            ldapPersonService.updateIsPluralistic(person.getDn(), false, uid, md5Pwd);
        }
        //RTX信息同步-1.删除原有的人员，2.新增人员到新的宿主单位
        //TODO 不能取到明文密码，需要rtx提供移动接口才能实现
        //更新 bigant
        Object[] objs = new Object[]{person, null};
    }

    @Override
    public List<PersonNodeVO> getSubPersons(Long parentId, String parentDn) {
        // 人员可能在ou下，所以不是ou直接返回null
        if (StringUtils.isEmpty(parentDn) || !parentDn.startsWith("ou")) {
            return null;
        }
        List<PersonNodeVO> nodes = new ArrayList<PersonNodeVO>();
//        List<PersonEO> ldapPersons = ldapPersonService.getPersons(parentDn, false);
//        if (ldapPersons != null && ldapPersons.size() > 0) {
        List<PersonEO> dbPersons = getPersons(parentId);
        if (dbPersons != null && dbPersons.size() > 0) {
            for (PersonEO person : dbPersons){
                if (person != null) {
                    PersonNodeVO node = getPersonNode(person);
                    nodes.add(node);
                }
            }
        }
//            if (dbPersons == null || dbPersons.size() < 0) {
//                throw new RecordsException();
//            } else {
//                // 通过dn映射LDPA和数据库中的person
//                Map<String, PersonEO> map = new HashMap<String, PersonEO>(dbPersons.size());
//                for (PersonEO person : dbPersons) {
//                    if (person == null) {
//                        continue;
//                    }
//                    map.put(person.getDn(), person);
//                }
//                for (PersonEO ldapPerson : ldapPersons) {
//                    if (ldapPerson == null) {
//                        continue;
//                    }
//                    PersonEO dbPerson = map.get(ldapPerson.getDn());
//                    if (dbPerson != null) {
//                        //如果是外平台人员，那么可能存在DB与LDAP中的信息不对称问题，此时需要以LDAP中的为准
//                        PersonNodeVO node = getPersonNode(dbPerson, ldapPerson);
//                        nodes.add(node);
//                    }
//                }
//            }
//        }
        return nodes;
    }


    /**
     * 构造PersonNodeVO
     *
     * @param dbPerson
     * @param ldapPerson
     * @return
     */
    private PersonNodeVO getPersonNode(PersonEO dbPerson, PersonEO ldapPerson) {
        PersonNodeVO node = new PersonNodeVO();
        BeanUtils.copyProperties(dbPerson, node);
        //LDAP中为准的数据：姓名，职务，手机，电话，办公地址，邮箱
        node.setName(ldapPerson.getName());
        node.setPositions(ldapPerson.getPositions());
        node.setMobile(ldapPerson.getMobile());
        node.setOfficePhone(ldapPerson.getOfficePhone());
        node.setOfficeAddress(ldapPerson.getOfficeAddress());
        node.setMail(ldapPerson.getMail());
        //树展示需要的辅助数据
        node.setId(dbPerson.getPersonId());
        node.setPid(dbPerson.getOrganId());
        node.setNodeType(TreeNodeVO.Type.Person.toString());
        node.setIcon(Icon.Male.getValue());
        return node;
    }

    /**
     * 根据DN查询人员
     *
     * @param organDn
     * @return
     */
    @Override
    public List<PersonEO> getPersonsByOrganDn(String organDn) {
        return personDao.getPersonsByOrganDn(organDn);
    }

    @Override
    public List<PersonEO> getPersonsByOrganDn(String organDn, String name) {
        return personDao.getPersonsByOrganDn(organDn, name);
    }

    @Override
    public void deleteByOrganDn(String organDn) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(organDn)) {
            return;
        }
        personDao.deleteByOrganDn(organDn);
    }


    @Override
    public List<Object> getPersons(Long pageIndex, int pageSize) {
        return personDao.getPersons(pageIndex, pageSize);
    }


    /**
     * 获取已删除的记录
     *
     * @param query
     * @return
     */
    @Override
    public Pagination getDeletedPersonsPage(PersonQueryVO query) {
        if (query == null) {
            query = new PersonQueryVO();
        }
        if (query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        if (query.getPageSize() <= 0 || query.getPageSize() > 100) {
            query.setPageSize(15);
        }
        return personDao.getDeletedPersonsPage(query);
    }

    @Override
    public List<String> getNames(Long[] organIds, String blurryNameOrPY) {
        String[] simpleOrganDns = null;
        if (organIds != null && organIds.length > 0) {
            List<OrganEO> organs = organService.getEntities(OrganEO.class,
                    organIds);
            if (organs != null && organs.size() > 0) {
                int size = organs.size();
                simpleOrganDns = new String[size];
                for (int i = 0; i < size; i++) {
                    OrganEO organ = organs.get(i);
                    String simpleDn = organ.getDn().split(",")[0];
                    simpleOrganDns[i] = simpleDn;
                }
            }
        }
        return personDao.getNames(simpleOrganDns, blurryNameOrPY);
    }

    @Override
    public PersonEO savePersonAndUser(PersonNodeVO node) {
        if (userService.isUidExisted(node.getUid())) {
            // uid已存在
            throw new BaseRunTimeException(TipsMode.Message.toString(), node.getUid().concat("已存在"));
        }
        // 备份明文密码
        String password = node.getPassword();
        // 新增用户
        UserEO user = null;
        String personSimpleDn = LDAPUtil.getSimpleDn();
        // 根据部门获取直属单位
        OrganEO organ = organService.getEntity(OrganEO.class, node.getOrganId());
        // 构建新建人员的DN
        String personDn = "cn=".concat(personSimpleDn).concat(",").concat(organ.getDn());
        node.setPersonDn(personDn);
        // 保存用户
        user = userService.save(node);
        OrganEO unit = organService.getUnitByOrganDn(organ.getDn());
        // 保存人员详细信息
        PersonEO person = savePerson(node, user, unit);
        // 保存组织与person之间的关系
//        List<OrganEO> organs = organService.getAncestors(organ.getParentId());
//        organs.add(organ);
//        List<OrganPersonEO> ops = new ArrayList<OrganPersonEO>(organs.size());
//        for (OrganEO o : organs) {
//            OrganPersonEO op = new OrganPersonEO();
//            op.setOrganId(o.getOrganId());
//            op.setOrganDn(o.getDn());
//            op.setPersonId(person.getPersonId());
//            ops.add(op);
//        }
//        organPersonService.saveEntities(ops);
        // 更新部门的hasPerson属性
        if (Integer.valueOf(organ.getHasPersons().toString()) == 0) {
            organ.setHasPersons(1);
            organService.updateEntity(organ);
        }
        //		// 保存用户和角色之间的关联关系
        if (!StringUtils.isEmpty(node.getRoleIds())) {
            Long[] roleIds = StringUtils.getArrayWithLong(node.getRoleIds(), ",");
            roleAssignmentService.save(person.getOrganId(), user.getUserId(), roleIds);
        }
        //		else {
        //			// 给用户赋予一个初始角色
        //			RoleEO role = roleService.getRoleByCode(RoleCodes.public_base_user.toString());
        //			if (role != null) {
        //				roleAssignmentService.save(person.getOrganId(),user.getUserId(), new Long[] { role.getRoleId() });
        //			}
        //		}
        if(LdapOpenUtil.isOpen){
            // Ldap保存
            ldapPersonService.save(person, user);
        }
        //		//保存日志
        //		logService.saveLog("[系统管理]新增用户，姓名："+person.getName(), "PersonEO", "Add");
        //记录日志
        SysLog.log("【系统管理】新增用户，姓名："+person.getName(),"PersonEO", CmsLogEO.Operation.Add.toString());
        //		// 更新用户，通知其他服务 -rtx、sendMail
        //		Object[] objs = new Object[] { person, password };
        //		PersonSubject.getInstance().saveNotify(objs);
        return person;
    }

    public PersonEO savePerson(PersonNodeVO node, UserEO user, OrganEO unit) {
        PersonEO person = new PersonEO();
        person.setSortNum(node.getSortNum());
        // 如果姓名有修改，那么验证姓名是否重复
        String name = node.getName();
        person.setName(name);
        String fullPY = PinYinUtil.cn2Spell(name);
        String simplePY = PinYinUtil.cn2FirstSpell(name);
        // 重新生成拼音
        person.setFullPy(fullPY);
        person.setSimplePy(simplePY);
        person.setDesc(node.getDesc());
        person.setJpegPhoto(node.getJpegPhoto());
        person.setJpegPhotoName(node.getJpegPhotoName());
        //		person.setBirth(node.getBirth());
        person.setPositions(node.getPositions());
        person.setOfficePhone(node.getOfficePhone());
        person.setOfficeAddress(node.getOfficeAddress());
        person.setMobile(node.getMobile());
        person.setMail(node.getMail());
        person.setDn(node.getPersonDn());
        // 设置人员所在的直属部门和单位
        person.setOrganId(node.getOrganId());
        person.setOrganName(node.getOrganName());
        person.setUnitId(unit.getOrganId());
        person.setUnitName(unit.getName());
        // 设置用户相关信息
        person.setUserId(user.getUserId());
        person.setUid(user.getUid());
        person.setMaxCapacity(node.getMaxCapacity());
        //设置平台编码
        //		PlatformEO platform = platformService.getCurrentPlatform();
        //		person.setPlatformCode(platform.getCode());
        person.setIsExternalPerson(Boolean.FALSE);
        saveEntity(person);
        return person;
    }

    @Override
    public List<PersonEO> updatePersonAndUser(PersonNodeVO node) {
        List<PersonEO> rps = new ArrayList<PersonEO>();
        // 备份明文密码
        String password = node.getPassword() == null ? null : node.getPassword() + "";
        // 更新用户
        UserEO user = userService.update(node);
        // 更新人员详细信息
        PersonEO person = updatePersonDetails(node);
        // 角色Id获取-用于更新角色赋予关系
        List<Long> roleIds = null;
        if (!StringUtils.isEmpty(node.getRoleIds())) {
            roleIds = StringUtils.getListWithLong(node.getRoleIds(), ",");
        }
        //		// 更新user与role之间的关系
        roleAssignmentService.updateAssignments(person.getOrganId(), user.getUserId(), roleIds);
        rps.add(person);
        // 兼职用户更新
        boolean isPluralistic = person.getIsPluralistic();
        // 非兼职用户，那么需要同步跟新兼职的信息
        if (!isPluralistic) {
            List<PersonEO> ps = updatePluralisticPersonDetails(person);
            if (ps != null && ps.size() > 0) {
                for (PersonEO p : ps) {
                    rps.add(p);
                }
            }
        }
        if(LdapOpenUtil.isOpen) {
            // Ldap更新
            for (PersonEO p : rps) {
                ldapPersonService.update(p, user);
            }
        }

        //记录日志
        SysLog.log("【系统管理】更新用户，姓名："+person.getName(),"PersonEO", CmsLogEO.Operation.Update.toString());
        //		//保存日志
        //		logService.saveLog("[系统管理]更新用户，姓名："+person.getName(), "PersonEO", "Update");
        //		// 更新用户，通知其他服务 -rtx、sendMail
        //		Object[] objs = new Object[] { person, password };
        //		PersonSubject.getInstance().updateNotify(objs);
        return rps;
    }

    @Override
    public List<PersonEO> updatePersonAndUser4Unit(PersonNodeVO node) {
        List<PersonEO> rps = new ArrayList<PersonEO>();
        // 备份明文密码
        String password = node.getPassword() == null ? null : node.getPassword() + "";
        // 更新人员详细信息
        PersonEO person = updatePersonDetails4Unit(node);
        // 更新用户
        UserEO user = userService.update(node);
        // 角色Id获取-用于更新角色赋予关系
        List<Long> roleIds = null;
        if (!StringUtils.isEmpty(node.getRoleIds())) {
            roleIds = StringUtils.getListWithLong(node.getRoleIds(), ",");
            // 更新user与role之间的关系
            roleAssignmentService.updateAssignments(person.getOrganId(),
                    user.getUserId(), roleIds);
        }
        rps.add(person);
        // 兼职用户更新
        boolean isPluralistic = person.getIsPluralistic();
        // 非兼职用户，那么需要同步跟新兼职的信息
        if (!isPluralistic) {
            List<PersonEO> ps = updatePluralisticPersonDetails(person);
            if (ps != null && ps.size() > 0) {
                for (PersonEO p : ps) {
                    rps.add(p);
                }
            }
        }
        if(LdapOpenUtil.isOpen) {
            // Ldap更新
            for (PersonEO p : rps) {
                ldapPersonService.update(p, user);
            }
        }
        //保存日志
        logService.saveLog("[系统管理]更新用户，姓名：" + person.getName(), "PersonEO", "Update");
        // 更新用户，通知其他服务 -rtx、sendMail
        Object[] objs = new Object[]{person, password};
        return rps;
    }

    /**
     * 更新兼职用户
     *
     * @param srcPerson
     */
    private List<PersonEO> updatePluralisticPersonDetails(PersonEO srcPerson) {
        List<PersonEO> persons = getPluralisticPersons(srcPerson.getPersonId());
        if (persons != null && persons.size() > 0) {
            for (PersonEO person : persons) {
                // 属性更新
                person.setSortNum(srcPerson.getSortNum());
                // 如果姓名有修改，那么验证姓名是否重复
                String srcName = person.getName();
                String targetName = srcPerson.getName();
                if (!srcName.equals(targetName)) {
                    boolean isNameExisted = isNameExisted(
                            srcPerson.getOrganId(), targetName);
                    if (isNameExisted) {
                        throw new BaseRunTimeException(
                                TipsMode.Message.toString(), "已存在部门下已存在人员：" + targetName);
                    } else {
                        person.setName(targetName);
                        String fullPY = PinYinUtil.cn2FirstSpell(targetName);
                        String simplePY = PinYinUtil.cn2Spell(targetName);
                        // 重新生成拼音
                        person.setFullPy(fullPY);
                        person.setSimplePy(simplePY);
                    }
                }
                person.setPositions(srcPerson.getPositions());
                person.setOfficePhone(srcPerson.getOfficePhone());
                person.setOfficeAddress(srcPerson.getOfficeAddress());
                person.setMobile(srcPerson.getMobile());
                person.setMail(srcPerson.getMail());
                updateEntity(person);
            }
        }
        return persons;
    }

    @Override
    public PersonEO updatePersonDetails(PersonNodeVO node) {
        PersonEO person = getEntity(PersonEO.class, node.getPersonId());
        person.setSortNum(node.getSortNum());
        // 如果姓名有修改，那么更新拼音信息
        String srcName = person.getName();
        String targetName = node.getName();
        if (!srcName.equals(targetName)) {
            person.setName(targetName);
            String simplePY = PinYinUtil.cn2FirstSpell(targetName);
            String fullPY = PinYinUtil.cn2Spell(targetName);
            // 重新生成拼音
            person.setFullPy(fullPY);
            person.setSimplePy(simplePY);
        }
        person.setPositions(node.getPositions());
        person.setJpegPhoto(node.getJpegPhoto());
        person.setJpegPhotoName(node.getJpegPhotoName());
        person.setDesc(node.getDesc());
        person.setOfficePhone(node.getOfficePhone());
        person.setOfficeAddress(node.getOfficeAddress());
        person.setMobile(node.getMobile());
        person.setMailSendFiles(node.getMailSendFiles());
        person.setMail(node.getMail());
        person.setMaxCapacity(node.getMaxCapacity());
        updateEntity(person);
        return person;
    }

    public PersonEO updatePersonDetails4Unit(PersonNodeVO node) {
        PersonEO person = getEntity(PersonEO.class, node.getPersonId());
        person.setSortNum(node.getSortNum());
        // 如果姓名有修改，那么更新拼音信息
        String srcName = person.getName();
        String targetName = node.getName();
        if (!srcName.equals(targetName)) {
            person.setName(targetName);
            String simplePY = PinYinUtil.cn2FirstSpell(targetName);
            String fullPY = PinYinUtil.cn2Spell(targetName);
            // 重新生成拼音
            person.setFullPy(fullPY);
            person.setSimplePy(simplePY);
            //如果姓名被修改，那么自动禁用用户
            if (!srcName.equals(targetName)) {
                node.setStatus(UserEO.STATUS.Unable.toString());
            }
        }
        person.setPositions(node.getPositions());
        person.setJpegPhoto(node.getJpegPhoto());
        person.setJpegPhotoName(node.getJpegPhotoName());
        person.setDesc(node.getDesc());
        person.setOfficePhone(node.getOfficePhone());
        person.setOfficeAddress(node.getOfficeAddress());
        person.setMobile(node.getMobile());
        person.setMailSendFiles(node.getMailSendFiles());
        person.setMail(node.getMail());
        person.setMaxCapacity(node.getMaxCapacity());
        updateEntity(person);
        return person;
    }


    @Override
    public void save(PersonEO person, UserEO user, Long[] roleIds,
                     String[] roleNames) {
        // 不可缺少的参数验证
        if (person == null || StringUtils.isEmpty(person.getName())
                || person.getOrganId() == null) {
            throw new IllegalArgumentException();
        }
        OrganEO organ = organService.getEntity(OrganEO.class,
                person.getOrganId());
        String parentDn = organ.getDn();
        // 组织下不能保存人员
        if (!parentDn.startsWith("ou")) {
            throw new BaseRunTimeException(TipsMode.Key.toString(),
                    "PersonEO.PersonStorageError");
        }
        // 验证同级单位下人员姓名是否重复
        // if (isNameExisted(person.getOrganId(), person.getName())) {
        // throw new
        // BaseRunTimeException(TipsMode.Key.toString(),"PersonEO.PersonNameRepeated");
        // }
        // 验证uid是否重复
        if (user != null && !StringUtils.isEmpty(user.getUid())) {
            boolean isUidExisted = userService.isUidExisted(user.getUid());
            if (isUidExisted) {
                throw new BaseRunTimeException(TipsMode.Key.toString(),
                        "UserEO.UidRepeated");
            }
        }
        // 通过姓名获取姓名的简拼和全拼并设置到person中
        String simplePy = PinYinUtil.cn2FirstSpell(person.getName());
        person.setSimplePy(simplePy);
        String fullPy = PinYinUtil.cn2Spell(person.getName());
        person.setFullPy(fullPy);
        if(LdapOpenUtil.isOpen) {
            // 保存到LDAP中
            ldapPersonService.saveWithNoVertiry(parentDn, person);
        }
        // 为user添加一些辅助的数据
        user.setPersonDn(person.getDn());
        user.setPersonName(person.getName());
        // 明文备份-调用后user中的会经过md5加密
        String backupPassword = user.getPassword() + "";
        Long userId = userService.save(user, person.getOrganId());
        // 保存用户和角色之间的关联关系
        if (roleIds != null && roleIds.length > 0) {
            roleAssignmentService.save(person.getOrganId(), userId, roleIds);
        } else {
            // 给用户赋予一个初始角色
            RoleEO role = roleService.getRoleByCode(RoleCodes.public_base_user
                    .toString());
            if (role != null) {
                roleAssignmentService.save(person.getOrganId(), userId,
                        new Long[]{role.getRoleId()});
            }
        }
        person.setUserId(user.getUserId());
        person.setOrganName(organ.getName());
        // 设置直属单位信息
        String unitDn = LDAPUtil.getLastLevelUnitDn(person.getDn());
        OrganEO unit = organService.getOrganByDn(unitDn);
        person.setUnitId(unit.getOrganId());
        person.setUnitName(unit.getName());
        saveEntity(person);
        // 保存组织与person之间的关系
        List<OrganEO> organs = organService.getAncestors(organ.getParentId());
        organs.add(organ);
        List<OrganPersonEO> ops = new ArrayList<OrganPersonEO>(organs.size());
        for (OrganEO o : organs) {
            OrganPersonEO op = new OrganPersonEO();
            op.setOrganId(o.getOrganId());
            op.setOrganDn(o.getDn());
            op.setPersonId(person.getPersonId());
            ops.add(op);
        }
        organPersonService.saveEntities(ops);
        if (Integer.valueOf(organ.getHasPersons().toString()) == 0) {
            organ.setHasPersons(1);
            organService.updateEntity(organ);
        }
        // 新增用户，通知其他服务
        Object[] objs = new Object[]{person, backupPassword};
    }

    @Override
    public List<PersonEO> updatePluralistic(PersonEO person, Long userId,
                                            List<Long> roleIds, List<String> roleNames) {
        // 更新person
        List<PersonEO> ps = update(person);
        //保存日志
        logService.saveLog("[系统管理]更新兼职用户，姓名：" + person.getName(), "PersonEO", "Update");
        // 更新password
        if (userId != null) {
            // 更新user与role之间的关系
            roleAssignmentService.updateAssignments(person.getOrganId(),
                    userId, roleIds);
        }
        return ps;
    }

    @Override
    public Map<String, List<Object>> deletePersons(Long id) {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        PersonEO person = getEntity(PersonEO.class, id);
        List<Object> organIds = new ArrayList<Object>();
        List<Object> dns = new ArrayList<Object>();
        List<Object> uids = new ArrayList<Object>();
        map.put("organIds", organIds);
        map.put("dns", dns);
        map.put("uids", uids);
        organIds.add(person.getOrganId());
        dns.add(person.getDn());
        // 1.如果是非兼职的person，那么需要判断是否存在兼职的情况，如果存在就先删除兼职的记录
        if (!person.getIsPluralistic()) {
            uids.add(person.getUid());
            List<PersonEO> persons = getPluralisticPersons(id);
            if (persons != null && persons.size() > 0) {
                for (PersonEO p : persons) {
                    // 删除兼职用户
                    delete(PersonEO.class, p.getPersonId());
                    organIds.add(p.getOrganId());
                    dns.add(p.getDn());
                    organPersonService.deleteByPersonId(p.getPersonId());
                    // 删除角色赋予关系
                    roleAssignmentService.delete(p.getOrganId(), p.getUserId());
                }
            }
            // 只有主person删除时才去删除关联的User
            userService.delete(UserEO.class, person.getUserId());
        }
        // 删除person
        delete(PersonEO.class, id);
        // 删除角色赋予关系
        roleAssignmentService.delete(person.getOrganId(), person.getUserId());
        // 删除人员与单位部门的关联关系
        organPersonService.deleteByPersonId(id);
        //记录日志
        SysLog.log("【系统管理】删除用户，姓名："+person.getName(),"PersonEO", CmsLogEO.Operation.Delete.toString());
        return map;
    }

    @Override
    public List<Long> deletePersons(List<Long> ids) {
        if (ids == null || ids.size() <= 0) {
            return null;
        }
        //		String names = "";
        //		for (Long id : ids) {
        //			boolean isIn = personRelationshipService.isInRelationship(id);
        //			if (isIn) {
        //				PersonEO person = getEntity(PersonEO.class, id);
        //				if (!StringUtils.isEmpty(names)) {
        //					names = names.concat(",");
        //				}
        //				names = names.concat(person.getName());
        //			}
        //		}
        //		if (!StringUtils.isEmpty(names)) {
        //			throw new BaseRunTimeException(TipsMode.Message.toString(),
        //					names.concat("已存在上下级关系，请先解除上下级关系."));
        //		}
        List<Long> organIds = new ArrayList<Long>();
        List<String> dns = new ArrayList<String>();
        List<String> uids = new ArrayList<String>();
        Iterator<Long> iterator = ids.iterator();
        while (iterator.hasNext()) {
            Long id = iterator.next();
            if (id == null) {
                continue;
            }
            Map<String, List<Object>> map = deletePersons(id);
            List<Object> targetOrganIds = map.get("organIds");
            if (targetOrganIds != null && targetOrganIds.size() > 0) {
                for (Object obj : targetOrganIds) {
                    organIds.add((Long) obj);
                }
            }
            List<Object> targetDns = map.get("dns");
            if (targetDns != null && targetDns.size() > 0) {
                for (Object obj : targetDns) {
                    dns.add(obj.toString());
                }
            }
            List<Object> targetUids = map.get("uids");
            if (targetUids != null && targetUids.size() > 0) {
                for (Object obj : targetUids) {
                    uids.add((String) obj);
                }
            }
        }
        if(LdapOpenUtil.isOpen){
            // 最后删除LDAP
            if (dns.size() > 0) {
                for (String dn : dns) {
                    ldapPersonService.delete(dn);
                }
            }
        }
        //		// 通知观察者们，我已经删除人员了，你们可以开始执行各自的业务了
        //		if (uids.size() > 0) {
        //			PersonSubject.getInstance().deleteNotify(uids);
        //		}
        return organIds;
    }

    @Override
    public Long delete(Long personId) {
        PersonEO person = getEntity(PersonEO.class, personId);
        // 非兼职的person
        if (!person.getIsPluralistic()) {
            List<PersonEO> persons = getPluralisticPersons(personId);
            if (persons != null && persons.size() > 0) {
                // 存在兼职情况，给出异常提示
                throw new BaseRunTimeException(TipsMode.Key.toString(),
                        "PersonEO.HasPluralisticRecords");
            }
        }
        if(LdapOpenUtil.isOpen) {
            // 2.删除主person
            ldapPersonService.delete(person.getDn());
        }
        delete(PersonEO.class, personId);
        organPersonService.deleteByPersonId(person.getPersonId());
        // 删除用户
        userService.delete(UserEO.class, person.getUserId());
        organService.updateHasPersons(person.getOrganId());
        // 删除人员，通知其他服务执行各自的业务，例如rtx删除人员等
        return person.getOrganId();
    }

    @Override
    public List<PersonEO> getPluralisticPersons(Long personId) {
        return personDao.getPluralisticPersons(personId);
    }

    /**
     * 组织(organId)下除了人员(personId)外是否还存在其他相同的姓名
     *
     * @param personId
     * @param name
     * @return
     */
    private boolean isNameRight(Long personId, String name) {
        return personDao.isNameRight(personId, name);
    }

    @Override
    public List<PersonEO> update(PersonEO person) {
        Long personId = person.getPersonId();
        String newName = person.getName();
        // 兼职记录信息
        List<PersonEO> pps = null;
        // 姓名是否修改
        if (!isNameRight(person.getPersonId(), person.getName())) {
            String fullPY = null;
            String simplePY = null;
            fullPY = PinYinUtil.cn2FirstSpell(newName);
            simplePY = PinYinUtil.cn2Spell(newName);
            // 重新生成拼音
            person.setFullPy(fullPY);
            person.setSimplePy(simplePY);
            // }
            // 如果是非兼职记录信息
            if (!person.getIsPluralistic()) {
                pps = getPluralisticPersons(personId);
                if (pps != null && pps.size() > 0) {
                    for (PersonEO p : pps) {
                        p.setName(newName);
                        p.setFullPy(fullPY);
                        p.setSimplePy(simplePY);
                    }
                }
            }
        }
        // 待更新的person集合
        List<PersonEO> ups = new ArrayList<PersonEO>();
        ups.add(person);
        // 添加兼职的记录信息
        if (pps != null && pps.size() > 0) {
            for (PersonEO p : pps) {
                //				p.setBirth(person.getBirth());
                p.setMobile(person.getMobile());
                p.setOfficePhone(person.getOfficePhone());
                p.setOfficeAddress(person.getOfficeAddress());
                p.setMail(person.getMail());
                if(LdapOpenUtil.isOpen) {
                    try {
                        ldapPersonService.update(p);
                    } catch (NameRepeatedException e) {
                        e.printStackTrace();
                    }
                }
                ups.add(p);
            }
        }
        if(LdapOpenUtil.isOpen) {
            ldapPersonService.update(ups);
        }
        updateEntities(ups);
        return ups;
    }

    @Override
    public Long update4move(Long personId, Long organId, boolean isRemoveRoles)
            throws BusinessException {
        // 获取person信息
        PersonEO person = getEntity(PersonEO.class, personId);
        if(person == null ){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "人员不存在");
        }
        UserEO user = userService.getEntity(UserEO.class, person.getUserId());
        if(user == null ){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "用户不存在");
        }
        Long srcOrganId = person.getOrganId();
        Long userId = person.getUserId();
        // 验证移动到的目标组织是否是person现在这个组织，如果是，那么直接返回
        if (srcOrganId.equals(organId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "不能选择本处室");
        }
        // 如果是兼职人员，那么不能移动到存在相同人员的其他部门
        // 验证是否存在该人员的兼职人员
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("organId", organId);
//        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
//        Long srcPersonId = person.getSrcPersonId();
//        if (srcPersonId != null) {
//            // 表示被移动的是兼职人员
//            params.put("srcPersonId", srcPersonId);
//        } else {
//            // 被移动的是非兼职人员
//            params.put("srcPersonId", personId);
//        }
//        PersonEO repeatedPerson = getEntity(PersonEO.class, params);
//        // 兼职用户同时还需要验证宿主是否移动到宿主单位
//        if (repeatedPerson == null && srcPersonId != null) {
//            params.remove("srcPersonId");
//            params.put("personId", srcPersonId);
//            repeatedPerson = getEntity(PersonEO.class, params);
//        }
//        if (repeatedPerson != null) {
//            throw new BaseRunTimeException(TipsMode.Message.toString(),
//                    "人员已存在，请选择其他单位/处室");
//        }
        // 设置排序号
//        person.setSortNum(getMaxSortNum(organId) + 2);
        // 补充新组织相关信息
        OrganEO organ = organService.getEntity(OrganEO.class, organId);
        if(organ == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"部门不存在");
        }
        // 设置直属单位信息
        String unitDn = LDAPUtil.getLastLevelUnitDn(organ.getDn());
        OrganEO unit = organService.getOrganByDn(unitDn);
        if (unit == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"部门直属单位不存在");
        }
        String srcUnitName = person.getUnitName();
        String toUnitName = unit.getName();
        person.setUnitId(unit.getOrganId());
        person.setUnitName(unit.getName());
        // 构建新建人员的DN
        String personSimpleDn = LDAPUtil.getSimpleDn();
        String personDn = "cn=".concat(personSimpleDn).concat(",").concat(organ.getDn());
        user.setPersonDn(personDn);
        person.setDn(personDn);
        person.setOrganId(organId);
        person.setOrganName(organ.getName());
        String srcDn = person.getDn();
        // 更新本地数据库数据
        updateEntity(person);
        userService.updateEntity(user);
        if(LdapOpenUtil.isOpen) {
            ldapPersonService.save(person, user);
            ldapPersonService.delete(srcDn);
        }
        // 如果非兼职人员已经绑定用户，那么需要同步修改用户的信息
//        if (!person.getIsPluralistic() && person.getUserId() != null) {
//            // 获取User信息
//            UserEO user = userService.getEntity(UserEO.class, person.getUserId());
//            // 更新user信息-ldap上添加新的user，本地数据库更新user的dn即可
//            user.setPersonDn(person.getDn());
//            ldapUserService.save(user);
//
//        }
        // 保存组织与person之间的关系
//        List<OrganEO> organs = organService.getAncestors(organ.getParentId());
//        if (organs == null) {
//            organs = new ArrayList<OrganEO>();
//        }
//        organs.add(organ);
//        List<OrganPersonEO> srcOps = organPersonService.getOrganPersonsByPersonId(personId);
//        List<OrganPersonEO> newOps = new ArrayList<OrganPersonEO>(organs.size());
//        for (OrganEO o : organs) {
//            Iterator<OrganPersonEO> iterator = srcOps.iterator();
//            boolean isExisted = false;
//            while (iterator.hasNext()) {
//                OrganPersonEO op = iterator.next();
//                if (o.getOrganId().equals(op.getOrganId())) {
//                    iterator.remove();
//                    isExisted = true;
//                    break;
//                }
//            }
//            if (!isExisted) {
//                OrganPersonEO newOp = new OrganPersonEO();
//                newOp.setOrganId(o.getOrganId());
//                newOp.setOrganDn(o.getDn());
//                newOp.setPersonId(person.getPersonId());
//                newOps.add(newOp);
//            }
//        }
//        organPersonService.delete(srcOps);
//        organPersonService.saveEntities(newOps);
        // 处理user和role之间的关系，删除所有的用户的所有角色，由另一个单位的单位管理员或系统管理员进行添加角色
        if (isRemoveRoles) {
            roleAssignmentService.delete(srcOrganId, person.getUserId());
//            // 给用户赋予一个初始角色
//            RoleEO role = roleService.getRoleByCode(RoleCodes.public_base_user.toString());
//            if (role != null) {
//                roleAssignmentService.save(person.getOrganId(), person.getUserId(), new Long[]{role.getRoleId()});
//            }
        } else {
            //更新关联关系中的organId
            roleAssignmentService.updateOrganId(srcOrganId, userId, organId);
        }
        //记录日志
        SysLog.log("【用户管理】移动用户，["+srcUnitName+"->"+toUnitName+"]，账号：" + person.getUid(), "PersonEO", CmsLogEO.Operation.Update.toString());
        return srcOrganId;
    }

    @Override
    public List<PersonEO> getPersons(Long organId) {
        return personDao.getPersons(organId);
    }

    @Override
    public List<PersonEO> getLdapPersons(Long organId, boolean ignorePluralistics) {
        OrganEO organ = organService.getEntity(OrganEO.class, organId);
        String parentDn = organ.getDn();
        // 人员可能在ou下，所以不是ou直接返回null
        if (!parentDn.startsWith("ou")) {
            return null;
        }
        List<PersonEO> ldapPersons = null;
        List<PersonEO> persons = new ArrayList<PersonEO>();
        ldapPersons = ldapPersonService.getPersons(parentDn, ignorePluralistics);
        if (ldapPersons != null && ldapPersons.size() > 0) {
            List<PersonEO> dbPersons = getPersons(organId);
            if (dbPersons == null || dbPersons.size() < 0) {
                throw new RecordsException();
            } else {
                // 通过dn映射LDPA和数据库中的person
                Map<String, PersonEO> map = new HashMap<String, PersonEO>(dbPersons.size());
                for (PersonEO person : dbPersons) {
                    if (person == null) {
                        continue;
                    }
                    map.put(person.getDn(), person);
                }
                for (PersonEO p : ldapPersons) {
                    if (p == null) {
                        continue;
                    }
                    PersonEO person = map.get(p.getDn());
                    if (person != null) {
                        persons.add(person);
                    }
                }
            }
        }
        return persons;
    }

    @Override
    public PersonEO savePluralisticPerson(PersonNodeVO node)
            throws BusinessException {
        PersonEO srcPerson = getEntity(PersonEO.class, node.getPersonId());
        OrganEO organ = organService.getEntity(OrganEO.class, node.getOrganId());
        // 原有记录保持不变，新增一条记录
        PersonEO person = new PersonEO();
        AppUtil.copyProperties(person, srcPerson);
        person.setPersonId(null);
        person.setOrganId(organ.getOrganId());
        String oname = organ.getName();
        person.setOrganName(oname);
        // 设置直属单位信息
        String unitDn = LDAPUtil.getLastLevelUnitDn(organ.getDn());
        OrganEO unit = organService.getOrganByDn(unitDn);
        person.setUnitId(unit.getOrganId());
        String uname = unit.getName();
        person.setUnitName(uname);
        person.setMaxCapacity(node.getMaxCapacity());
        person.setIsPluralistic(true);
        person.setDn(null);
        // 设置源personId
        if (srcPerson.getIsPluralistic()) {
            person.setSrcPersonId(srcPerson.getSrcPersonId());
        } else {
            person.setSrcPersonId(srcPerson.getPersonId());
        }
        person.setPositions(node.getPositions());
        person.setSortNum(node.getSortNum());
        person.setName(node.getName());
        String parentDn = organ.getDn();
        // 只有单位下能保存人员
        if (!parentDn.startsWith("ou")) {
            throw new BaseRunTimeException();
        }
        ldapPersonService.saveWithNoVertiry(parentDn, person);
        saveEntity(person);
        //保存日志
        logService.saveLog("[系统管理]添加兼职用户，姓名：" + person.getName(), "PersonEO", "Add");
        // 给人员赋予角色
        String ids = node.getRoleIds();
        if (!StringUtils.isEmpty(ids)) {
            // 获取角色ID数组和
            Long[] rids = null;
            if (!StringUtils.isEmpty(ids)) {
                rids = StringUtils.getArrayWithLong(ids, ",");
            }
            // 保存用户和角色之间的关系
            roleAssignmentService.save(person.getOrganId(), person.getUserId(),
                    rids);
        } else {
            // 给用户赋予一个初始角色
            RoleEO role = roleService.getRoleByCode(RoleCodes.public_base_user
                    .toString());
            if (role != null) {
                roleAssignmentService.save(person.getOrganId(),
                        person.getUserId(), new Long[]{role.getRoleId()});
            }
        }
        organ.setHasPersons(1);
        organService.updateEntity(organ);
        return person;
    }

    @Override
    public boolean isNameExisted(Long parentId, String name) {
        if (parentId == null || StringUtils.isEmpty(name)) {
            throw new NullPointerException();
        }
        OrganEO organ = organService.getEntity(OrganEO.class, parentId);
        return ldapPersonService.isNameExisted(organ.getDn(), name);
    }

    @Override
    public Long getMaxSortNum(Long organId) {
        return personDao.getMaxSortNum(organId);
    }


    @Override
    public Pagination getPage(PersonQueryVO query) {
        if (query == null) {
            query = new PersonQueryVO();
        }
        if (query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        if (query.getPageSize() <= 0 || query.getPageSize() > 100) {
            query.setPageSize(15);
        }
        return personDao.getPage(query);
    }

    /**
     * 分页查询
     *
     * @param query
     * @return
     */
    public Pagination getInfoPage(PersonQueryVO query) {
        if (query == null) {
            query = new PersonQueryVO();
        }
        if (query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        if (query.getPageSize() <= 0 || query.getPageSize() > 100) {
            query.setPageSize(15);
        }
        Long organId = query.getOrganId();
        if (organId != null) {
            OrganEO organ = organService.getEntity(OrganEO.class, organId);
            query.setDn(organ.getDn());
        }
        Pagination page = personDao.getInfoPage(query);
        List<?> data = page.getData();
        List<Object> persons = null;
        //构造人员所在的组织架构全路径以及返回的对象
        if (data != null && data.size() > 0) {
            persons = new ArrayList<Object>(data.size());
            //			 p.person_id,p.name_,p.user_id,p.uid_,p.is_pluralistic,p.dn_,u.login_times,u.last_login_date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Integer level = null;
            if (LoginPersonUtil.isRoot()) {
                level = 4;
            } else if (LoginPersonUtil.isSuperAdmin()) {
                level = 3;
            } else if (LoginPersonUtil.isSiteAdmin()) {
                level = 2;
            } else {
                level = 1;
            }
            Map<Long, List<RoleEO>> rolesMap = roleService.getRolesMap(data);
            for (Object obj : data) {
                PersonVO node = new PersonVO();
                Object[] array = (Object[]) obj;
                Long personId = array[0] == null ? null : Long.valueOf(array[0].toString());
                node.setPersonId(personId);
                String name = array[1] == null ? null : array[1].toString();
                node.setName(name);
                Long userId = array[2] == null ? null : Long.valueOf(array[2].toString());
                node.setUserId(userId);
                String uid = array[3] == null ? null : array[3].toString();
                node.setUid(uid);
                boolean isPluralistic = false;
                if (array[4] != null) {
                    if (Integer.valueOf(array[4].toString()) == 1) {
                        isPluralistic = true;
                    }
                }
                node.setIsPluralistic(isPluralistic);
                String dn = array[5] == null ? null : array[5].toString();
                node.setDn(dn);
                Integer loginTimes = array[6] == null ? Integer.valueOf(0) : Integer.valueOf(array[6].toString());
                node.setLoginTimes(loginTimes);
                String ds = array[7] == null ? null : array[7].toString();
                if (ds != null) {
                    try {
                        node.setLastLoginDate(sdf.parse(ds));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String status = array[8] == null ? null : array[8].toString();
                node.setStatus(status);
                Long srcPersonId = array[9] == null ? null : Long.valueOf(array[9].toString());
                node.setSrcPersonId(srcPersonId);


                String mobile = array[10] == null ? null : array[10].toString();
                node.setMobile(mobile);
                Long userOrganId = array[11] == null ? null : Long.valueOf(array[11].toString());
                node.setOrganId(userOrganId);
                Long unitId = array[12] == null ? null : Long.valueOf(array[12].toString());
                node.setUnitId(unitId);
//                String fullName = LDAPUtil.getFullName4OrganStartWithDirrectlyOU(dn);
                String fullName = "";
                if(unitId != null){
                    OrganEO unit = organService.getEntity(OrganEO.class,unitId);
                    fullName = unit != null?unit.getName():"";
                }
                if(userOrganId != null){
                    OrganEO organUnit = organService.getEntity(OrganEO.class,userOrganId);
                    fullName = (fullName != ""?fullName+">":"") + (organUnit != null?organUnit.getName():"");
                }
                node.setFullOranName(fullName);
                //设置删除级别
                if (userId != null && rolesMap != null) {
                    List<RoleEO> roles = rolesMap.get(userId);
                    if (roles != null && roles.size() > 0) {
                        Integer levelRole = null;
                        for (RoleEO ra : roles) {
                            if (ra.getCode().equals(RoleCodeVO.superAdmin)) {
                                levelRole = 3;
                            } else if (ra.getCode().equals(RoleCodeVO.unitAdmin)) {
                                levelRole = 2;
                            } else {
                                levelRole = 1;
                            }
                            if (levelRole != null && level != null && levelRole != 1 && level <= levelRole) {
                                node.setDelLevel(false);
                                break;
                            }
                        }
                    }
                }
                persons.add(node);
            }
            page.setData(persons);
        }
        return page;
    }

    @Override
    public Pagination getPage(PersonQueryVO query, Long[] roleIds) {
        // 分配一个角色，那么只取该角色所属单位的人员
        Long[] organIds = null;
        if (roleIds != null && roleIds.length == 1) {
            List<RoleAssignmentEO> as = roleAssignmentService
                    .getRoleAssignments(roleIds[0]);
            if (as == null || as.size() <= 0) {
                return new Pagination(null, null, query.getPageSize(), query.getPageIndex());
            } else {
                organIds = new Long[as.size()];
                for (int i = 0; i < as.size(); i++) {
                    organIds[i] = as.get(i).getOrganId();
                }
            }
        } else {
            organIds = new Long[1];
            organIds[0] = query.getOrganId();
        }
        return personDao.getPage(query, roleIds, organIds);
    }

    @Override
    public List<PersonNodeVO> getExcelResults(Long organId, Long[] roleIds,
                                              String searchText, String sortField, String sortOrder) {
        // 分配一个角色，那么只取该角色所属单位的人员
        Long[] organIds = null;
        if (roleIds != null && roleIds.length == 1) {
            List<RoleAssignmentEO> as = roleAssignmentService
                    .getRoleAssignments(roleIds[0]);
            if (as == null || as.size() <= 0) {
                return null;
            } else {
                organIds = new Long[as.size()];
                for (int i = 0; i < as.size(); i++) {
                    organIds[i] = as.get(i).getOrganId();
                }
            }
        } else {
            organIds = new Long[1];
            organIds[0] = organId;
        }
        return personDao.getExcelResults(organIds, roleIds, searchText,
                sortField, sortOrder);
    }

    @Override
    public List<Long> delete(Long[] personIds) {
        List<Long> organIds = null;
        if (personIds != null && personIds.length > 0) {
            organIds = new ArrayList<Long>();
            for (Long personId : personIds) {
                Long organId = delete(personId);
                // 如果组织ID已经存在，那么忽略
                if (!organIds.contains(organId)) {
                    organIds.add(organId);
                }
            }
        }
        return organIds;
    }

    @Override
    public PersonEO getUnpluralisticPersons(Long userId) {
        if (userId == null || userId <= 0) {
            throw new NullPointerException();
        }
        return personDao.getUnpluralisticPersons(userId);
    }

    @Override
    public List<IndicatorEO> getSysButtons(Long userId, Long parentId,
                                           String type) {
        return indicatorService.getSystemIndicators(userId, parentId, type);
    }

    @Override
    public PersonEO getPersonByUserId(Long organId, Long userId) {
        if (organId == null || userId == null) {
            throw new NullPointerException();
        }
        return personDao.getPersonByUserId(organId, userId);
    }

    /**
     * 根据组织ID和uid获取person信息
     *
     * @param organId
     * @param uid
     * @return
     */
    public PersonEO getPersonByUid(Long organId, String uid) {
        if (StringUtils.isEmpty(uid)) {
            throw new NullPointerException();
        }
        return personDao.getPersonByUid(organId, uid);
    }

    @Override
    public List<PersonEO> getPersonsByUserIds(Long[] organIds, Long[] userIds) {
        if (organIds == null || organIds.length <= 0 || userIds == null
                || userIds.length <= 0) {
            throw new NullPointerException();
        }
        // 参数不匹配异常
        if (organIds.length != userIds.length) {
            throw new IllegalArgumentException();
        }
        return personDao.getPersonsByUserIds(organIds, userIds);
    }

    @Override
    public List<PersonEO> getPersonsByRoleCodeAndUnitId(String roleCode, Long unitId) {
        List<PersonEO> persons = null;
        List<OrganEO> organUnits = organService.getOrganUnits(new Long[]{unitId});
        Long[] organUnitIds = null;
        if (organUnits != null && organUnits.size() > 0) {
            int size = organUnits.size();
            organUnitIds = new Long[size];
            for (int i = 0; i < size; i++) {
                OrganEO organ = organUnits.get(i);
                organUnitIds[i] = organ.getOrganId();
            }
            persons = personDao.getPersonsByRoleCode(roleCode, organUnitIds);
        }
        return persons;
    }

    @Override
    public List<PersonEO> getPersonsByRoleId(Long roleId) {
        if (roleId == null || roleId <= 0) {
            throw new NullPointerException();
        }
        return personDao.getPersonsByRoleId(roleId);
    }

    @Override
    public List<PersonEO> getPersonsByRoleCodeAndOrganIds(String roleCode, Long[] organIds) {
        List<PersonEO> persons = null;
        List<OrganEO> organs = organService.getOrgansByOrganIds(organIds);
        if (organs != null && organs.size() > 0) {
            List<String> organDns = new ArrayList<String>(organs.size());
            for (OrganEO organ : organs) {
                if (organ != null) {
                    organDns.add(organ.getDn());
                }
            }
            persons = personDao.getPersonsByRoleCode(roleCode, organDns);
        }
        return persons;
    }

    /**
     * 根据角色编码和用户ID获取人员
     *
     * @param roleCode
     * @param userId
     * @return
     */
    public List<PersonEO> getPersonsByRoleCodeAndUserId(String roleCode, Long userId) {
        return personDao.getPersonsByRoleCodeAndUserId(roleCode, userId);
    }

    @Override
    public List<PersonEO> getSubPersonsFromDB(Long organId, String[] statuses) {
        if (organId == null) {
            throw new NullPointerException();
        }
        return personDao.getSubPersons(organId, statuses);
    }

    /**
     * 从数据库和LDAP中获取部门下的人员，人员信息以LDAP为准
     *
     * @param organIds
     * @return
     */
    public List<PersonEO> getSubPersonsFromDBAndLDAP(Long[] organIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("organId", organIds);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return getEntities(PersonEO.class, params);
    }

    @Override
    public List<PersonEO> getSubPersons(Long[] organIds) {
        // 从数据库获取组织相关的信息
        Map<String, Object> ops = new HashMap<String, Object>();
        if (organIds == null || organIds.length <= 0) {
            ops.put("organId", null);
        } else {
            ops.put("organId", organIds);
        }

        ops.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<OrganEO> parents = organService.getEntities(OrganEO.class, ops);
        // 获取所有父节点对应的DN，通过这些DN到LDAP中获取人员信息
        List<String> dns = new ArrayList<String>();
        if (parents != null && parents.size() > 0) {
            for (OrganEO organ : parents) {
                String dn = organ.getDn();
                // 只有单位或虚拟单位才能放人，所以如果存在组织，那么过滤掉
                if (dn.startsWith("ou")) {
                    dns.add(dn);
                }
            }
        }
        // 从LDAP上读取人员信息
        List<PersonEO> persons = new ArrayList<PersonEO>();
        if (dns.size() > 0) {
            for (String dn : dns) {
                List<PersonEO> ps = ldapPersonService.getPersons(dn, false);
                if (ps != null && ps.size() > 0) {
                    persons.addAll(ps);
                }
            }
        }
        List<PersonEO> targets = new ArrayList<PersonEO>();
        // 从数据库读取人员信息对LDAP上返回的信息进行补充或去除未同步到数据库中的人员
        if (persons.size() > 0) {
            Map<String, Object> pps = new HashMap<String, Object>();
            pps.put("organId", organIds);
            pps.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<PersonEO> dbPersons = getEntities(PersonEO.class, pps);
            if (dbPersons != null && dbPersons.size() > 0) {
                Map<String, PersonEO> map = new HashMap<String, PersonEO>();
                for (PersonEO p : dbPersons) {
                    map.put(p.getDn(), p);
                }
                Iterator<PersonEO> iterator = persons.iterator();
                while (iterator.hasNext()) {
                    PersonEO p = iterator.next();
                    PersonEO dbPerson = map.get(p.getDn());
                    if (dbPerson != null) {// LDAP上有，但数据库没有，那么可能是尚未同步到数据中的数据，不显示
                        targets.add(dbPerson);
                    }
                }
            }
        }
        return targets;
    }

    public List<PersonEO> getSubPersons(Long[] organIds, String status) {
        return null;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Map<String, List<?>> getPersonsAndOrganDnsByPersonName(
            Long[] organIds, String name) {
        // 获取跟组织/单位对应的dn
        String[] dns = null;
        if (organIds != null && organIds.length > 0) {
            dns = organService.getDns(organIds);
        } else {
            dns = new String[]{cn.lonsun.ldap.internal.util.Constants.ROOT_DN};
        }
        Map<String, List<?>> map = ldapPersonService.getPersons(dns, name);
        // 如果organIds不为空，表示不是从root开始，而是固定的几个单位，此时需要把根单位也加进去
        if (organIds != null) {
            List<String> lsit = (List<String>) map.get("organDns");
            if (lsit == null) {
                lsit = new ArrayList<String>();
            }
            for (String dn : dns) {
                lsit.add(dn);
            }
        }
        // 补齐person信息
        List<PersonEO> persons = (List<PersonEO>) map.get("persons");
        List<PersonEO> newPs = null;
        if (persons != null && persons.size() > 0) {
            int psize = persons.size();
            newPs = new ArrayList<PersonEO>(psize);
            List<String> pDns = new ArrayList<String>(psize);
            // key:dn,value:person
            Map<String, Object> ps = new HashMap<String, Object>(psize);
            for (PersonEO p : persons) {
                if (p != null) {
                    pDns.add(p.getDn());
                    ps.put(p.getDn(), p);
                }
            }
            List<PersonEO> dbPersons = personDao.getPersonsByDns(pDns);
            if (dbPersons != null && dbPersons.size() > 0) {
                for (PersonEO person : dbPersons) {
                    if (ps.get(person.getDn()) != null) {
                        newPs.add(person);
                    }
                }
            }
        }
        map.put("persons", newPs);
        return map;
    }

    @Override
    public List<String> getDnsByName(String[] organDns, String name) {
        return ldapPersonService.getDns(organDns, name);
    }

    @Override
    public PersonEO savePluralisticPersonOld(PersonNodeVO node)
            throws BusinessException {
        PersonEO person = getEntity(PersonEO.class, node.getPersonId());
        OrganEO organ = organService.getEntity(OrganEO.class, node.getOrganId());
        // 原有记录保持不变，新增一条记录
        PersonEO p = new PersonEO();
        AppUtil.copyProperties(p, person);
        p.setPersonId(null);
        p.setOrganId(organ.getOrganId());
        p.setOrganName(organ.getName());
        p.setIsPluralistic(true);
        p.setDn(null);
        // 设置源personId
        if (person.getIsPluralistic()) {
            p.setSrcPersonId(person.getSrcPersonId());
        } else {
            p.setSrcPersonId(person.getPersonId());
        }
        p.setPositions(node.getPositions());
        p.setSortNum(node.getSortNum());
        p.setName(node.getName());
        String parentDn = organ.getDn();
        // 只有单位下能保存人员
        if (!parentDn.startsWith("ou")) {
            throw new BusinessException(TipsMode.Key.toString(),
                    "PersonEO.PersonStorageError");
        }
        // 保存到LDAP中
        ldapPersonService.saveWithNoVertiry(parentDn, p);
        // 设置直属单位信息
        String unitDn = LDAPUtil.getLastLevelUnitDn(p.getDn());
        OrganEO unit = organService.getOrganByDn(unitDn);
        if (unit != null) {
            p.setUnitId(unit.getOrganId());
            p.setUnitName(unit.getName());
        }
        saveEntity(p);
        RoleEO role = roleService.getRoleByCode(RoleCodes.public_base_user
                .toString());
        if (role != null) {
            roleAssignmentService.save(person.getOrganId(), person.getUserId(),
                    new Long[]{role.getRoleId()});
        }
        organService.updateHasPersons(organ.getOrganId(), 1);
        // 保存组织与person之间的关系
        List<OrganEO> organs = organService.getAncestors(organ.getParentId());
        if (organs == null) {
            organs = new ArrayList<OrganEO>();
        }
        organs.add(organ);
        List<OrganPersonEO> ops = new ArrayList<OrganPersonEO>(organs.size());
        for (OrganEO o : organs) {
            OrganPersonEO op = new OrganPersonEO();
            op.setOrganId(o.getOrganId());
            op.setOrganDn(o.getDn());
            op.setPersonId(p.getPersonId());
            ops.add(op);
        }
        organPersonService.saveEntities(ops);
        return p;
    }

    @Override
    public Pagination getPage4RoleBySql(PageQueryVO query, Long roleId,
                                        String searchText) {
        return personDao.getPage4RoleBySql(query, roleId, searchText);
    }

    //更新人员至ldap 初始密码 1234
    @Override
    public void savePersonLdap(PersonEO person) {
        try {
            // 新增用户
            UserEO user = userService.getUser(person.getUid());
            String personSimpleDn = LDAPUtil.getSimpleDn();
            // 根据部门获取直属单位
            OrganEO organ = organService.getEntity(OrganEO.class, person.getOrganId());
            // 构建新建人员的DN
            String personDn = "cn=".concat(personSimpleDn).concat(",").concat(organ.getDn());
            person.setDn(personDn);
            // 保存非兼职用户
            if (!person.getIsPluralistic()) {
                user.setPersonDn(personDn);
                userService.updateEntity(user);
            }
            userService.updateEntity(user);
            updateEntity(person);
//            // 保存组织与person之间的关系
//            List<OrganEO> organs = organService.getAncestors(organ.getParentId());
//            organs.add(organ);
//            List<OrganPersonEO> ops = new ArrayList<OrganPersonEO>(organs.size());
//            for (OrganEO o : organs) {
//                OrganPersonEO op = new OrganPersonEO();
//                op.setOrganId(o.getOrganId());
//                op.setOrganDn(o.getDn());
//                op.setPersonId(person.getPersonId());
//                ops.add(op);
//            }
//            organPersonService.saveEntities(ops);
            if(LdapOpenUtil.isOpen){
                // Ldap保存
                ldapPersonService.save(person, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException();
        }
    }

    @Override
    public void deleteLdap(String dn) {
        ldapPersonService.delete(dn);
    }

    @Override
    public List<PersonEO> getAllPersons() {
        return personDao.getAllPersons();
    }

    @Override
    public List<PersonEO> getPsersonsByUnitId(Long unitId) {
        return personDao.getPsersonsByUnitId(unitId);
    }

    @Override
    public List<PersonEO> updatePersonInfo(PersonNodeVO node) {
        List<PersonEO> rps = new ArrayList<PersonEO>();
        // 备份明文密码
        String password = node.getPassword() == null ? null : node.getPassword() + "";
        // 更新用户

        UserEO user = userService.updatePersonalInfo(node);
        // 更新人员详细信息
        PersonEO person = updatePersonDetails(node);

        rps.add(person);
        // 兼职用户更新
        boolean isPluralistic = person.getIsPluralistic();
        // 非兼职用户，那么需要同步跟新兼职的信息
        if (!isPluralistic) {
            List<PersonEO> ps = updatePluralisticPersonDetails(person);
            if (ps != null && ps.size() > 0) {
                for (PersonEO p : ps) {
                    rps.add(p);
                }
            }
        }
        if(LdapOpenUtil.isOpen){
            // Ldap更新
            for (PersonEO p : rps) {
                ldapPersonService.update(p, user);
            }
        }
        return rps;
    }

    @Override
    public PersonInfoVO getPersonInfoByUid(Long organId, String uid) {
        PersonInfoVO infoVo = new PersonInfoVO();
        PersonEO person = getPersonByUid(organId, uid);
        // 获取用于直属单位的Dn
        if (person != null) {
            infoVo.setPersonId(person.getPersonId());
            infoVo.setPersonName(person.getName());
            infoVo.setOrganId(person.getOrganId());
            infoVo.setOrganName(person.getOrganName());
            infoVo.setUserId(person.getUserId());
            infoVo.setUid(person.getUid());
            infoVo.setCoreMail(person.getMailSendFiles());
            infoVo.setStatus(PersonInfoVO.Status.SUCCESS.toString());
            String unitDn = LDAPUtil.getLastLevelUnitDn(person.getDn());
            OrganEO unit = organService.getOrganByDn(unitDn);
            if (unit != null) {
                infoVo.setUnitId(unit.getOrganId());
                infoVo.setUnitName(unit.getName());
            }
        }
        return infoVo;
    }

    @Override
    public List<PersonEO> getPersonsByLikeName(Long unitId, String name) {
        return personDao.getPersonsByLikeName(unitId, name);
    }

    @Override
    public void saveXlsPerson(PersonNodeVO node) {
        try {
            if (!userService.isUidExisted(node.getUid())) {
                // 根据部门获取直属单位
                OrganEO organ = organService.getEntity(OrganEO.class, node.getOrganId());
                if(organ != null && OrganEO.Type.OrganUnit.toString().equals(organ.getType())){
                    String personSimpleDn = LDAPUtil.getSimpleDn();
                    // 构建新建人员的DN
                    String personDn = "cn=".concat(personSimpleDn).concat(",").concat(organ.getDn());
                    node.setPersonDn(personDn);
                    node.setOrganName(organ.getName());
                    // 保存用户
                    UserEO user = userService.save(node);
                    OrganEO unit = organService.getUnitByOrganDn(organ.getDn());
                    // 保存人员详细信息
                    PersonEO person = savePerson(node, user, unit);
                    // 更新部门的hasPerson属性
                    if (Integer.valueOf(organ.getHasPersons().toString()) == 0) {
                        organ.setHasPersons(1);
                        organService.updateEntity(organ);
                    }
                    //		// 保存用户和角色之间的关联关系
                    if (!StringUtils.isEmpty(node.getRoleIds())) {
                        try{
                            Long[] roleIds = StringUtils.getArrayWithLong(node.getRoleIds(), ",");
                            roleAssignmentService.save(person.getOrganId(), user.getUserId(), roleIds);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(LdapOpenUtil.isOpen) {
                        ldapPersonService.save(person, user);
                    }
                    //记录日志
                    SysLog.log("【用户管理】导入用户，账号："+person.getUid(),"PersonEO", CmsLogEO.Operation.Add.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PersonNodeVO getPersonNode(PersonEO person)
    {
        PersonNodeVO node = new PersonNodeVO();
        BeanUtils.copyProperties(person, node);
        node.setId(person.getPersonId());
        node.setPid(person.getOrganId());
        node.setNodeType(TreeNodeVO.Type.Person.toString());
        node.setIsPluralistic(person.getIsPluralistic());
        node.setIcon(TreeNodeVO.Icon.Male.getValue());
        return node;
    }

    @Override
    public void initSimplePersonsCache() {
        List<?> persons = personDao.getPersonInfosByPlatformCode(null, null);
        if (persons != null && persons.size() > 0){
            for (Object person : persons) {
                Object[] values = (Object[]) person;
                String dn = values[8].toString().replaceAll(",", "-");
                values[8] = dn;
            }
        }
        String simplePersons = JSONHelper.toJSON(persons);
        //更新js文件
        String js = "var ALLUSERS_ARRAY= "+simplePersons;
        FileUtils.writeNew(js, InitServlet.personsJsPath);
        //更新时间戳
        TimeCutUtil.put(TimeCutUtil.CacheKey.PersonUpdateDate.toString(),new Date().getTime() + "");
    }

    @Override
    public List<PersonEO> getPersonsByDn(Long organId) {
        if(organId == null){
            return null;
        }
        OrganEO organ = organService.getEntity(OrganEO.class,organId);
        if (organ == null) {
            return null;
        }
        return personDao.getPersonsByDn(organ.getDn());
    }
}
