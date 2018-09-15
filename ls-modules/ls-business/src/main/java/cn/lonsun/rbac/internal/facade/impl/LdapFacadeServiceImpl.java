package cn.lonsun.rbac.internal.facade.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.SimpleNodeVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Icon;
import cn.lonsun.common.vo.TreeNodeVO.Type;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.internal.service.ILdapOrganService;
import cn.lonsun.ldap.internal.service.ILdapPersonService;
import cn.lonsun.ldap.internal.util.Constants;
import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.OrganPersonEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.facade.ILdapFacadeService;
import cn.lonsun.rbac.internal.service.IOrganPersonService;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.internal.service.IPlatformService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.rbac.internal.util.OrganNodeUtils;
import cn.lonsun.rbac.utils.RoleCodeVO;
import cn.lonsun.rbac.utils.TreeNodeVOUtils;
import cn.lonsun.rbac.vo.Node4SaveOrUpdateVO;
import cn.lonsun.rbac.vo.TreeNodeCacheVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 系统管理组织人员管理服务类
 *
 * @author xujh
 * @date 2014年9月24日 上午11:24:11
 * @version V1.0
 */
@Service("ldapFacadeService")
public class LdapFacadeServiceImpl implements ILdapFacadeService {
	@Autowired
	private IPlatformService platformService;
	@Autowired
	private IOrganService organService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPersonService personService;
	@Autowired
	private IOrganPersonService organPersonService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IRoleAssignmentService roleAssignmentService;
	@Autowired
	private ILdapPersonService ldapPersonService;
	@Autowired
	private ILdapOrganService ldapOrganService;

	@Override
	public List<OrganNodeVO> getSubOrgans(Long organId, int flag) {
		List<OrganNodeVO> list = new ArrayList<OrganNodeVO>();
		List<OrganEO> organs = organService.getLdapOrgansWithCache(organId);
		if (organs != null && organs.size() > 0) {
			for (OrganEO organ : organs) {
				OrganNodeVO node = new OrganNodeVO();
				node.addProperties(organ, flag);
				list.add(node);
			}
		}
		return list;
	}


	@Override
	public List<OrganNodeVO> getUnitNodes(Long parentId,int targetType) {
		List<OrganNodeVO> list = new ArrayList<OrganNodeVO>();
		List<OrganEO> organs = organService.getSubVirtualNodesAndUnits(parentId);
		if (organs != null && organs.size() > 0) {
			for (OrganEO organ : organs) {
				list.add(OrganNodeUtils.getOrganNode(organ,targetType));
			}
		}
		return list;
	}

	@Override
	public List<PersonNodeVO> getSubPersons(Long organId) {
		List<PersonNodeVO> list = null;
		List<PersonEO> persons = personService.getLdapPersons(organId, false);
		if (persons != null && persons.size() > 0) {
			list = new ArrayList<PersonNodeVO>();
			for (PersonEO p : persons) {
				PersonNodeVO node = new PersonNodeVO();
				BeanUtils.copyProperties(p, node);
				node.setId(p.getPersonId());
				node.setPid(p.getOrganId());
				node.setNodeType(TreeNodeVO.Type.Person.toString());
				node.setIsPluralistic(p.getIsPluralistic());
				node.setIcon(Icon.Male.getValue());
				list.add(node);
			}
		}
		return list;
	}

	@Override
	public List<Object> getSubNodes(Long organId) {
		List<Object> list = new ArrayList<Object>();
		List<OrganNodeVO> organs = getSubOrgans(organId, 1);
		if (organs != null && organs.size() > 0) {
			list.addAll(organs);
		}
		// 人员必须存在与Organ之下
		if (organId != null) {
			OrganEO organ = organService.getEntity(OrganEO.class, organId);
			String dn = organ.getDn();
			// 只有当organ是部门或虚拟单位时才需要查找person
			if (!StringUtils.isEmpty(dn) && dn.startsWith("ou")) {
				List<PersonNodeVO> ps = getSubPersons(organId);
				if (ps != null && ps.size() > 0) {
					list.addAll(ps);
				}
			}
		}
		return list;
	}

	@Override
	public Node4SaveOrUpdateVO savePluralistic(PersonNodeVO node) {
		PersonEO person = null;
		try {
			person = personService.savePluralisticPerson(node);
			OrganEO organ = organService.getEntity(OrganEO.class,person.getOrganId());
			// 保存组织与person之间的关系
			List<OrganEO> organs = organService.getAncestors(organ.getParentId());
			if (organs == null) {
				organs = new ArrayList<OrganEO>();
			}
			organs.add(organ);
			List<OrganPersonEO> ops = new ArrayList<OrganPersonEO>(
					organs.size());
			for (OrganEO o : organs) {
				OrganPersonEO op = new OrganPersonEO();
				op.setOrganId(o.getOrganId());
				op.setOrganDn(o.getDn());
				op.setPersonId(person.getPersonId());
				ops.add(op);
			}
			organPersonService.saveEntities(ops);
		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BaseRunTimeException(TipsMode.Key.toString(), e.getKey());
		}
		Node4SaveOrUpdateVO n = new Node4SaveOrUpdateVO(person.getPersonId(),
				person.getOrganId(), person.getName(), false);
		n.setIcon(Icon.Male.getValue());
		n.setIsPluralistic(person.getIsPluralistic());
		return n;
	}


	@Override
	public Node4SaveOrUpdateVO updatePluralisticPerson(PersonNodeVO node) {
		PersonEO person = new PersonEO();
		BeanUtils.copyProperties(node, person);
		person.setPersonId(node.getId());
		person.setOrganId(node.getPid());
		person.setIsPluralistic(true);
		// 角色Id、名称获取
		List<Long> roleIds = null;
		if (!StringUtils.isEmpty(node.getRoleIds())) {
			String[] ids = node.getRoleIds().split(",");
			roleIds = new ArrayList<Long>(ids.length);
			for (String roleId : ids) {
				roleIds.add(Long.valueOf(roleId));
			}
		}
		List<String> roleNames = null;
		if (!StringUtils.isEmpty(node.getRoleNames())) {
			String[] names = node.getRoleNames().split(",");
			roleNames = new ArrayList<String>(names.length);
			for (String roleName : names) {
				roleNames.add(roleName);
			}
		}
		List<PersonEO> persons = personService.updatePluralistic(person,node.getUserId(), roleIds, roleNames);
		Node4SaveOrUpdateVO n = null;
		if (persons != null && persons.size() > 0) {
			for (PersonEO p : persons) {
				n = new Node4SaveOrUpdateVO(p.getPersonId(), p.getOrganId(),p.getName(), true);
				n.setIsPluralistic(p.getIsPluralistic());
				break;
			}
		}
		return n;
	}


	@Override
	public PersonNodeVO getPluralistic(Long personId, Long organId) {
		PersonEO person = personService.getEntity(PersonEO.class, personId);
		//本部门用户不能添加兼职到本部门
		if(person.getOrganId().longValue()==organId.longValue()){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择外部门人员到本部门兼职");
		}
		//如果人员已经在本部门兼职，那么不允许再次兼职-通过userId与organId获取
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", person.getUserId());
		params.put("organId", organId);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		List<PersonEO> ps = personService.getEntities(PersonEO.class, params);
		if(ps!=null&&ps.size()>0){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "您选择的人员已在本部门兼职，请选择其他人员");
		}
		//获取用户信息
		UserEO user = userService.getEntity(UserEO.class, person.getUserId());
		PersonNodeVO node = new PersonNodeVO();
		AppUtil.copyProperties(node, person);
		if (user != null) {
			AppUtil.copyProperties(node, user);
		}
		// 重置兼职单位需要填写的信息
		node.setSortNum(personService.getMaxSortNum(organId) + 2);
		node.setPositions(null);
		//构造宿主人员所在单位的详细信息
		String nuitName = person.getUnitName()==null?"":person.getUnitName();
		String srcPersonInfo = nuitName.concat(" > ").concat(person.getOrganName()).concat(" > ").concat(person.getName());
		node.setSrcPersonInfo(srcPersonInfo);
		return node;
	}

	@Override
	public PersonNodeVO getPerson(Long personId) {
		PersonEO person = personService.getEntity(PersonEO.class, personId);
		if(person==null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "人员已被删除，请重新加载");
		}
		PersonNodeVO node = new PersonNodeVO();
		BeanUtils.copyProperties(person, node);
		node.setId(person.getPersonId());
		node.setPid(person.getOrganId());
		node.setNodeType(TreeNodeVO.Type.Person.toString());
		node.setIsPluralistic(person.getIsPluralistic());
		UserEO user = userService.getEntity(UserEO.class,person.getUserId());
		BeanUtils.copyProperties(user, node);
		node.setIsSupportMobile(user.getIsSupportMobile());
		node.setIsCreateMSF(user.getIsCreateMSF());
		// 不返回密码
		node.setPassword(null);
		Integer level = null;
		if(LoginPersonUtil.isRoot()){
			level = 4;
		}else if(LoginPersonUtil.isSuperAdmin()){
			level = 3;
		}else if(LoginPersonUtil.isSiteAdmin()){
			level = 2;
		}else{
			level = 1;
		}
		List<RoleEO> roles = roleService.getUserRoles(person.getUserId(),person.getOrganId());
		if (roles != null && roles.size() > 0) {
			String upRoleIds = "";
			String upRoleNames = "";
			String viRoleIds = "";
			String viRoleNames = "";
			Integer levelRole = null;
			Long siteId = LoginPersonUtil.getSiteId();
			int a = 0;
			int b = 0;
			for (int i = 0; i < roles.size(); i++) {
				RoleEO ra = roles.get(i);
				Long roleId = ra.getRoleId();
				String name = ra.getName();
				if(level == 2){
					if(siteId.equals(ra.getSiteId())){
						if (a > 0) {
							upRoleIds = upRoleIds.concat(",");
							upRoleNames = upRoleNames.concat(",");
						}
						upRoleIds = upRoleIds.concat(roleId.toString());
						upRoleNames = upRoleNames.concat(name);
						a++;
					}else{
						if (b > 0) {
							viRoleIds = viRoleIds.concat(",");
							viRoleNames = viRoleNames.concat(",");
						}
						viRoleIds = viRoleIds.concat(roleId.toString());
						viRoleNames = viRoleNames.concat(name);
						b++;
					}
				}else{
					if (i > 0) {
						upRoleIds = upRoleIds.concat(",");
						upRoleNames = upRoleNames.concat(",");
					}
					upRoleIds = upRoleIds.concat(roleId.toString());
					upRoleNames = upRoleNames.concat(name);
				}
				//设置角色编辑级别
				if(ra.getCode().equals(RoleCodeVO.superAdmin)){
					levelRole = 3;
				}else if(ra.getCode().equals(RoleCodeVO.unitAdmin)){
					levelRole = 2;
				}else{
					levelRole = 1;
				}
				if (levelRole != null && level != null && levelRole != 1 && level <= levelRole) {
					node.setUpdateRole(true);
				}
			}
			node.setUpRoleIds(upRoleIds);
			node.setUpRoleNames(upRoleNames);
			node.setViRoleIds(viRoleIds);
			node.setViRoleNames(viRoleNames);
		}
		// 获取角色名称
//						List<RoleAssignmentEO> ras = roleAssignmentService.getAssignments(
//								person.getOrganId(), person.getUserId());
		//		if (ras != null && ras.size() > 0) {
		//			String roleIds = "";dd
		//			String roleNames = "";
		//			for (int i = 0; i < ras.size(); i++) {
		//				RoleAssignmentEO ra = ras.get(i);
		//				if (i > 0) {
		//					roleIds = roleIds.concat(",");
		//					roleNames = roleNames.concat(",");
		//				}
		//				roleIds = roleIds.concat(ra.getRoleId().toString());
		//				roleNames = roleNames.concat(ra.getRoleName());
		//			}
		//			node.setRoleIds(roleIds);
		//			node.setRoleNames(roleNames);
		//		}
		//如果当前用户是兼职人员，那么构造宿主人员所在单位的详细信息
		if(person.getIsPluralistic()){
			PersonEO srcPerson = personService.getEntity(PersonEO.class, person.getSrcPersonId());
			String srcPersonInfo = srcPerson.getUnitName().concat(" > ").concat(srcPerson.getOrganName()).concat(" > ").concat(srcPerson.getName());
			node.setSrcPersonInfo(srcPersonInfo);
		}
		return node;
	}

	@Override
	public PersonNodeVO getEmptyPerson(Long organId) {
		PersonNodeVO node = new PersonNodeVO();
		/*String realPath = ContextHolderUtils.getRequest().getSession()
				.getServletContext()
				.getRealPath(PropertiesFilePaths.SYSTEM_ENVIRONMENT_PATH);
		PropertiesReader reader = PropertiesReader.getInstance(realPath);
		String v = reader.getValue("maxCapacity");
		node.setMaxCapacity(Integer.valueOf(v));*/
		// 排序号默认取最大排序号+2
		Long sortNum = personService.getMaxSortNum(organId);
		OrganEO organ = organService.getEntity(OrganEO.class, organId);
		if (organ == null) {
			throw new IllegalArgumentException();
		} else {
			node.setOrganName(organ.getName());
		}
		if (sortNum == null) {
			sortNum = 2L;
		} else {
			sortNum = sortNum + 2;
		}
		node.setSortNum(sortNum);
		node.setOrganId(organId);
		return node;
	}

	@Override
	public List<TreeNodeVO> getNodes4Roles(Long unitId) {
		List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
		// 获取单位节点
		List<String> types = new ArrayList<String>(1);
		types.add(TreeNodeVO.Type.Organ.toString());
		Long[] organIds = null;
		if (unitId != null && unitId > 0) {
			organIds = new Long[] { unitId };
		}
		List<OrganEO> organs = organService.getSubOrgans(organIds, types,
				false, null);
		if (organs != null && organs.size() > 0) {
			for (OrganEO organ : organs) {
				if(organ!=null){
					nodes.add(TreeNodeVOUtils.getUnitNode4Roles(organ));
				}
			}
		}
		if (unitId != null) {
			List<RoleEO> roles = roleService.getRoles(
					RoleEO.Type.Private.toString(), unitId);
			if (roles != null && roles.size() > 0) {
				for (RoleEO role : roles) {
					nodes.add(TreeNodeVOUtils.getRoleNode(unitId, role));
				}
			}
		}
		return nodes;
	}

	/**
	 * **********************选人界面信息加载**************************
	 */
	@Override
	public List<TreeNodeVO> getNodes(String[] nodeTypes, Long[] organIds,Boolean isContainsExternal) {
		//如果为空，那么默认认为不显示外单位
		if(isContainsExternal==null){
			isContainsExternal = Boolean.FALSE;
		}
		// 入参验证
		if (nodeTypes == null || nodeTypes.length <= 0) {
			// 如果参数错误，那么统一抛出IllegalArgumentException
			throw new IllegalArgumentException();
		}

		String parentDn = null;
		//范围控制
		if (organIds == null || organIds.length <= 0) {
			parentDn = Constants.ROOT_DN;
		}
		// 查询
		//如果不显示外平台，那么添加isExternalOrgan过滤
		List<OrganEO> organs = null;
		if(organIds==null||organIds.length<=0){
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("parentId", null);
//			map.put("recordStatus", RecordStatus.Normal.toString());
//			if(isContainsExternal!=null&&!isContainsExternal){
//				map.put("isExternalOrgan", Boolean.FALSE);
//			}
//			organs = organService.getEntities(OrganEO.class, map);
			organs = organService.getOrgans(null);
		}else{
			organs = organService.getOrgans(organIds, isContainsExternal);
		}
		//获取LDAP中的数据，进行对比，返回ldap和db中同时存在的节点
//		List<OrganEO> ldapOrgans = null;
//		if(parentDn!=null){
//			String platformCode = null;
////			if(!isContainsExternal){
////				platformCode = platformService.getCurrentPlatform().getCode();
////			}
//			//根据平台编码获取第一级的单位，如果platformCode不为空，那么之获取平台编码为platformCode的单位，否则获取全部
//			ldapOrgans = ldapOrganService.getOrgansByPlatformCode(parentDn, nodeTypes,platformCode);
//		}else{
//			//获取第一级需要显示的节点
//			if(organs!=null&&organs.size()>0){
//				ldapOrgans = new ArrayList<OrganEO>(organs.size());
//				for(OrganEO organ:organs){
//					ldapOrgans.add(ldapOrganService.getOrgan(organ.getDn()));
//				}
//			}
//		}
		//遍历，将ldap中的oragn存入map，key-dn
//		Map<String, OrganEO> map = new HashMap<String, OrganEO>();
//		if(ldapOrgans!=null&&ldapOrgans.size()>0){
//			for(OrganEO organ:ldapOrgans){
//				map.put(organ.getDn(), organ);
//			}
//		}
		List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>(organs.size());
		for (OrganEO organ : organs) {
			//说明ldap和db中都存在
			if(organ!=null){
				TreeNodeVO node = getOrganTreeNode(nodeTypes, organ);
				nodes.add(node);
			}
		}
//		if (organs != null && organs.size() > 0) {
//			// 将organs按sortNum从小到大排序
//			if (organs.size() > 1) {
//				Collections.sort(organs, new Comparator<OrganEO>() {
//					@Override
//					public int compare(OrganEO o1, OrganEO o2) {
//						Long o1sortNum = o1.getSortNum();
//						Long o2sortNum = o2.getSortNum();
//						int flag = 0;
//						if (o1sortNum == null) {
//							flag = 1;
//						} else if (o2sortNum == null) {
//							flag = -1;
//						} else {
//							flag = o1.getSortNum().compareTo(o2.getSortNum());
//						}
//						return flag;
//					}
//				});
//			}
//			nodes = new ArrayList<TreeNodeVO>(organs.size());
//			for (OrganEO organ : organs) {
//				OrganEO ldapOrgan = map.get(organ.getDn());
//				//说明ldap和db中都存在
//				if(ldapOrgan!=null){
//					TreeNodeVO node = getOrganTreeNode(nodeTypes, organ);
//					nodes.add(node);
//				}
//			}
//		}
		return nodes;
	}
	@Override
	public List<TreeNodeCacheVO> getCacheNodes(String[] nodeTypes, Long[] organIds,Boolean isContainsExternal){
		//如果为空，那么默认认为不显示外单位
		if(isContainsExternal==null){
			isContainsExternal = Boolean.FALSE;
		}
		// 入参验证
		if (nodeTypes == null || nodeTypes.length <= 0) {
			// 如果参数错误，那么统一抛出IllegalArgumentException
			throw new IllegalArgumentException();
		}

		String parentDn = null;
		//范围控制
		if (organIds == null || organIds.length <= 0) {
			parentDn = Constants.ROOT_DN;
		}
		// 查询
		//如果不显示外平台，那么添加isExternalOrgan过滤
		List<OrganEO> organs = organService.getOrgans(organIds, isContainsExternal);
		//获取LDAP中的数据，进行对比，返回ldap和db中同时存在的节点
		List<OrganEO> ldapOrgans = null;
		if(parentDn!=null){
			String platformCode = null;
			if(!isContainsExternal){
				platformCode = platformService.getCurrentPlatform().getCode();
			}
			//根据平台编码获取第一级的单位，如果platformCode不为空，那么之获取平台编码为platformCode的单位，否则获取全部
			ldapOrgans = ldapOrganService.getOrgansByPlatformCode(parentDn, nodeTypes,platformCode);
		}else{
			//获取第一级需要显示的节点
			if(organs!=null&&organs.size()>0){
				ldapOrgans = new ArrayList<OrganEO>(organs.size());
				for(OrganEO organ:organs){
					ldapOrgans.add(ldapOrganService.getOrgan(organ.getDn()));
				}
			}
		}
		//遍历，将ldap中的oragn存入map，key-dn
		Map<String, OrganEO> map = new HashMap<String, OrganEO>();
		if(ldapOrgans!=null&&ldapOrgans.size()>0){
			for(OrganEO organ:ldapOrgans){
				map.put(organ.getDn(), organ);
			}
		}
		List<TreeNodeCacheVO> nodes = null;
		if (organs != null && organs.size() > 0) {
			nodes = new ArrayList<TreeNodeCacheVO>(organs.size());
			for (OrganEO organ : organs) {
				OrganEO ldapOrgan = map.get(organ.getDn());
				//说明ldap和db中都存在
				if(ldapOrgan!=null){
					TreeNodeCacheVO node = getTreeNodeCache4Organ(nodeTypes, organ);
					nodes.add(node);
				}
			}
		}
		return nodes;
	}

	/**
	 ****************************选人界面获取下节点的方法************************** 
	 */
	@Override
	public List<TreeNodeVO> getSubNodes(Boolean isContainsExternal,String[] nodeTypes, Long parentId,String[] statuses) {
		// 入参验证
		if (nodeTypes == null || nodeTypes.length <= 0) {
			// 如果参数错误，那么统一抛出IllegalArgumentException
			throw new IllegalArgumentException();
		}
		Long[] parentIds = new Long[] { parentId };
		// 入参解析成自己需要的数据
		List<String> types = new ArrayList<String>(2);
		// 是否包含虚拟处室
		boolean isContainsFictitious = false;
		// 是否包含人员
		boolean isContainsPersons = false;
		for (String nodeType : nodeTypes) {
			if (nodeType.equals(Type.Organ.toString())) {// 单位
				types.add(TreeNodeVO.Type.Organ.toString());
			} else if (nodeType.equals(Type.OrganUnit.toString())) {// 部门/处室
				types.add(TreeNodeVO.Type.OrganUnit.toString());
			} else if (nodeType.equals(Type.Virtual.toString())) {// 虚拟部门/处室
				isContainsFictitious = true;
			} else {// Person人员
				isContainsPersons = true;
			}
		}
		List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
		// 从DB获取单位、部门/处室以及虚拟处室节点
		List<OrganEO> organs = organService.getSubOrgans(parentIds, types,isContainsFictitious, null);
		// 构造树展示需要的单位、处室/部门以及虚拟部门的VO
		if (organs != null && organs.size() > 0) {
			for (OrganEO organ : organs) {
				if(organ!=null){
					TreeNodeVO node = getOrganTreeNode(nodeTypes, organ);
					nodes.add(node);
				}
			}
		}
		// 获取人员节点
		if (isContainsPersons && parentId != null) {
			List<PersonEO> persons = personService.getSubPersonsFromDB(parentId,statuses);
			// 构造树展示需要的人员信息的VO
			for (PersonEO person : persons) {
				TreeNodeVO node = getPersonTreeNode(person);
				nodes.add(node);
			}
		}
		return nodes;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<TreeNodeVO> getSubNodesByPersonName(String name, Long[] organIds) {
		List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
		// 表示此次查找的主要目标是人
		// 根据根单位节点和姓名进行获取用户信息
		// 向LDAP服务获取在dns下的姓名中包含了name的人员
		Map<String, List<?>> map = personService
				.getPersonsAndOrganDnsByPersonName(organIds, name);
		List<?> organDns = map.get("organDns");
		if (organDns != null && organDns.size() > 0) {
			List<OrganEO> os = organService
					.getOrgansByDns((List<String>) organDns);
			if (os != null && os.size() > 0) {
				for (OrganEO o : os) {
					nodes.add(getOrganTreeNode(null, o));
				}
			}
		}
		List<?> persons = map.get("persons");
		if (persons != null && persons.size() > 0) {
			List<PersonEO> ps = (List<PersonEO>) persons;
			for (PersonEO p : ps) {
				nodes.add(getPersonTreeNode(p));
			}
		}
		return nodes;
	}

	@Override
	public List<TreeNodeVO> getSubNodesByOrganName(Long[] organIds,String name,String[] types){
		if (types==null||types.length<=0 || StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException();
		}
		//模糊查询获取所有匹配的组织
		List<String> list = new ArrayList<String>(types.length);
		Collections.addAll(list, types);
		List<OrganEO> organs = organService.getConnectedOrgans(organIds, list, name);
		List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
		if (organs != null && organs.size() > 0) {
			for (OrganEO organ : organs) {
				nodes.add(getOrganTreeNode(null, organ));
			}
		}
		return nodes;
	}

	@Override
	public List<TreeNodeVO> getSubNodesByRoleName(String name, Long[] organIds,
												  Long[] roleIds) {
		// 获取所有public和private类型，且角色名称中含有name字符串的角色赋予关系
		List<RoleAssignmentEO> ras = roleAssignmentService.getRoleAssignments(null, name);
		if(ras!=null&&ras.size()>0){

		}
		// 通过角色赋予关系获取对应的单位
		// 查询
		Map<String, Object> params = new HashMap<String, Object>();
		if (organIds == null || organIds.length <= 0) {
			params.put("parentId", null);
		} else {
			params.put("organId", organIds);
		}
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		List<OrganEO> organs = organService.getEntities(OrganEO.class, params);
		List<TreeNodeVO> nodes = null;
		if (organs != null && organs.size() > 0) {
			nodes = new ArrayList<TreeNodeVO>(organs.size());
			for (OrganEO organ : organs) {
				TreeNodeVO node = getOrganTreeNode(null, organ);
				nodes.add(node);
			}
		}
		return nodes;
	}

	/**
	 * 构造人员节点
	 *
	 * @param person
	 * @return
	 */
	private TreeNodeVO getPersonTreeNode(PersonEO person) {
		TreeNodeVO node = new TreeNodeVO();
		String id = PersonEO.class.getSimpleName().concat(
				person.getPersonId().toString());
		node.setId(id);
		Long organId = person.getOrganId();
		if (organId != null) {
			String pid = OrganEO.class.getSimpleName().concat(
					organId.toString());
			node.setPid(pid);
		}
		//是否外平台
		node.setIsExternalPerson(person.getIsExternalPerson());
		//平台编码
		node.setPlatformCode(person.getPlatformCode());
		node.setPersonId(person.getPersonId());
		node.setPersonName(person.getName());
		node.setUserId(person.getUserId());
		node.setName(person.getName());
		node.setOrganId(person.getOrganId());
		node.setOrganName(person.getOrganName());
		node.setUnitId(person.getUnitId());
		node.setUnitName(person.getUnitName());
		node.setMobile(person.getMobile());
		node.setDn(person.getDn());
		// 性别
		node.setIcon(Icon.Male.getValue());
		node.setType(TreeNodeVO.Type.Person.toString());
		return node;
	}

	/**
	 * 构造组织、单位和虚拟单位对应的TreeNodeVO对象
	 *
	 * @param nodeTypes
	 * @param organ
	 * @return
	 */
	private TreeNodeVO getOrganTreeNode(String[] nodeTypes, OrganEO organ) {
		TreeNodeVO node = new TreeNodeVO();
		String id = OrganEO.class.getSimpleName().concat(organ.getOrganId().toString());
		node.setId(id);
		node.setDn(organ.getDn());
		//是否外平台
		node.setIsExternalOrgan(organ.getIsExternalOrgan());
		//平台编码
		node.setPlatformCode(organ.getPlatformCode());
		Long parentId = organ.getParentId();
		if (parentId != null) {
			String pid = OrganEO.class.getSimpleName().concat(
					parentId.toString());
			node.setPid(pid);
		}
		node.setName(organ.getName());
		// 节点类型
		if (organ.getType().equals(TreeNodeVO.Type.Organ.toString())) {
			if (organ.getIsFictitious() == 1) {
				node.setType(TreeNodeVO.Type.VirtualNode.toString());
				node.setIcon(Icon.VirtualNode.getValue());
			} else {
				node.setType(TreeNodeVO.Type.Organ.toString());
				node.setIcon(Icon.Organ.getValue());
			}
			// 此处对应前端的单位
			node.setUnitId(organ.getOrganId());
			node.setUnitName(organ.getName());
		} else {
			if (organ.getIsFictitious() == 1) {
				node.setType(TreeNodeVO.Type.Virtual.toString());
				node.setIcon(Icon.Virtual.getValue());
			} else {
				node.setType(TreeNodeVO.Type.OrganUnit.toString());
				node.setIcon(Icon.OrganUnit.getValue());
			}
			// 此处对应前端的部门/处室
			node.setOrganId(organ.getOrganId());
			node.setOrganName(organ.getName());
		}
		// 是否是父节点处理
		Boolean isParent = Boolean.FALSE;
		if (nodeTypes == null) {
			isParent = Boolean.TRUE;
		} else {
			for (String nodeType : nodeTypes) {
				if (nodeType.equals(TreeNodeVO.Type.VirtualNode.toString())) {
					if (organ.getHasVirtualNodes()!=null&&organ.getHasVirtualNodes() == 1) {
						isParent = Boolean.TRUE;
						break;
					}
				}
				if (nodeType.equals(TreeNodeVO.Type.Organ.toString())) {
					if (organ.getHasOrgans() == 1) {
						isParent = Boolean.TRUE;
						break;
					}
				}
				if (nodeType.equals(TreeNodeVO.Type.OrganUnit.toString())) {
					if (organ.getHasOrganUnits() == 1) {
						isParent = Boolean.TRUE;
						break;
					}
				}
				if (nodeType.equals(TreeNodeVO.Type.Virtual.toString())) {
					if (organ.getHasFictitiousUnits() == 1) {
						isParent = Boolean.TRUE;
						break;
					}
				}
				if (nodeType.equals(TreeNodeVO.Type.Person.toString())) {
					if (organ.getHasPersons() == 1) {
						isParent = Boolean.TRUE;
						break;
					}
				}
			}
		}
		node.setIsParent(isParent);
		return node;
	}

	/**
	 * 构造组织、单位和虚拟单位对应的TreeNodeVO对象
	 *
	 * @param nodeTypes
	 * @param organ
	 * @return
	 */
	private TreeNodeCacheVO getTreeNodeCache4Organ(String[] nodeTypes, OrganEO organ) {
		TreeNodeCacheVO node = new TreeNodeCacheVO();
		String id = OrganEO.class.getSimpleName().concat(organ.getOrganId().toString());
		node.setId(id);
		node.setDn(organ.getDn());
		//是否外平台
		node.setIsExternalOrgan(organ.getIsExternalOrgan());
		//平台编码
		node.setPlatformCode(organ.getPlatformCode());
		Long parentId = organ.getParentId();
		if (parentId != null) {
			String pid = OrganEO.class.getSimpleName().concat(
					parentId.toString());
			node.setPid(pid);
		}
		node.setName(organ.getName());
		// 节点类型
		if (organ.getType().equals(TreeNodeVO.Type.Organ.toString())) {
			if (organ.getIsFictitious() == 1) {
				node.setType(TreeNodeVO.Type.VirtualNode.toString());
				node.setIcon(Icon.VirtualNode.getValue());
			} else {
				node.setType(TreeNodeVO.Type.Organ.toString());
				node.setIcon(Icon.Organ.getValue());
			}
			// 此处对应前端的单位
			node.setUnitId(organ.getOrganId());
			node.setUnitName(organ.getName());
		} else {
			if (organ.getIsFictitious() == 1) {
				node.setType(TreeNodeVO.Type.Virtual.toString());
				node.setIcon(Icon.Virtual.getValue());
			} else {
				node.setType(TreeNodeVO.Type.OrganUnit.toString());
				node.setIcon(Icon.OrganUnit.getValue());
			}
			// 此处对应前端的部门/处室
			node.setOrganId(organ.getOrganId());
			node.setOrganName(organ.getName());
		}
		//是否有单位容器
		boolean hasVirtualNode = organ.getHasVirtualNodes()==null?false:organ.getHasVirtualNodes()==1;
		node.setHasVirtualOrgan(hasVirtualNode);
		boolean hasOrgans = organ.getHasOrgans()==null?false:organ.getHasOrgans()==1;
		node.setHasOrgans(hasOrgans);
		boolean hasOrganUnits = organ.getHasOrganUnits()==null?false:organ.getHasOrganUnits()==1;
		node.setHasOrganUnits(hasOrganUnits);
		boolean hasFictitiousUnits = organ.getIsFictitious()==null?false:organ.getIsFictitious()==1;
		node.setHasFictitiousUnits(hasFictitiousUnits);
		return node;
	}

	@Override
	public List<TreeNodeVO> getNodes4Roles(Long unitId, Long[] roleIds) {
		List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
		// 获取单位节点
		List<String> types = new ArrayList<String>(1);
		types.add(TreeNodeVO.Type.Organ.toString());
		Long[] organIds = null;
		if (unitId != null && unitId > 0) {
			organIds = new Long[] { unitId };
		}
		List<OrganEO> organs = organService.getSubOrgans(organIds, types,
				false, null);
		if (organs != null && organs.size() > 0) {
			// 用于验证单位是否已绑定公共角色，如果已绑定，那么organ的isParent需要设置为true
			List<Long> ids = new ArrayList<Long>(organs.size());
			Map<String, TreeNodeVO> map = new HashMap<String, TreeNodeVO>();
			for (OrganEO organ : organs) {
				if(organ!=null){
					TreeNodeVO node = TreeNodeVOUtils.getUnitNode4Roles(organ);
					nodes.add(node);
					if (organ.getHasOrgans() == 0 && organ.getHasRoles() == 0) {
						ids.add(organ.getOrganId());
						map.put(organ.getOrganId().toString(), node);
					}
				}
			}
			// 只需要判断没有子单位和角色的单位
			if (ids.size() > 0) {
				List<Long> targets = roleAssignmentService
						.getOrganIdsWhichIsAssignedRoles(ids);
				if (targets != null && targets.size() > 0) {
					for (Long id : targets) {
						TreeNodeVO node = map.get(id.toString());
						node.setIsParent(Boolean.TRUE);
					}
				}
			}
		}
		if (unitId != null) {
			List<RoleEO> roles = roleService.getUnitUsedRoles(unitId, roleIds);
			if (roles != null && roles.size() > 0) {
				for (RoleEO role : roles) {
					nodes.add(TreeNodeVOUtils.getRoleNode(unitId, role));
				}
			}
		}
		return nodes;
	}

	@Override
	public List<String> getPersonNames(String name, int count, Long[] organIds) {
		String[] dns = null;
		if (organIds != null && organIds.length > 0) {
			dns = organService.getDns(organIds);
		} else {
			dns = new String[] { cn.lonsun.ldap.internal.util.Constants.ROOT_DN };
		}
		// 默认设置为8条
		if (count <= 0) {
			count = 8;
		}
		List<String> names = ldapPersonService.getNames(dns, name, count);
		return names;
	}

	@Override
	public List<SimpleNodeVO> getSimpleNodes(String name,Long[] organIds,int scope){
		List<String> names = null;
		if(scope==0){
			names = personService.getNames(organIds, name);
		}else{
			names = organService.getNames(organIds,scope, name);
		}
		List<SimpleNodeVO> persons = null;
		if(names!=null&&names.size()>0){
			persons = new ArrayList<SimpleNodeVO>();
			for(String n:names){
				SimpleNodeVO p = new SimpleNodeVO();
				p.setName(n);
				persons.add(p);
			}
		}
		return persons;
	}
}
