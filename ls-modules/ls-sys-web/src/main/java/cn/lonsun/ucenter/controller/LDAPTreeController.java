package cn.lonsun.ucenter.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import cn.lonsun.ldap.internal.service.ILdapOrganService;
import cn.lonsun.ldap.internal.service.ILdapPersonService;
import cn.lonsun.ldap.internal.util.LDAPUtil;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.*;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.base.util.TreeNodeUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.vo.SimpleNodeVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Icon;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.internal.cache.InternalLdapCache;
import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.facade.ILdapFacadeService;
import cn.lonsun.rbac.internal.util.OrganNodeUtils;
import cn.lonsun.rbac.vo.Node4SaveOrUpdateVO;
import cn.lonsun.rbac.vo.RoleNodeVO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
/**
 * LDAP组织架构控制器
 *
 * @Description:
 * @author xujh
 * @date 2014年9月22日 下午2:56:08
 * @version V1.0
 */
@Controller
@RequestMapping(value = "/ldap", produces = { "application/json;charset=UTF-8" })
public class LDAPTreeController extends BaseController {
	@Autowired
	private IOrganService organService;
	@Autowired
	private IPersonService personService;
	@Autowired
	private IOrganPersonService organPersonService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IRoleAssignmentService roleAssignmentService;
	@Autowired
	private ILdapFacadeService ldapFacadeService;
	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private ILdapOrganService ldapOrganService;

	@Autowired
	private ILdapPersonService ldapPersonService;

	@Autowired
	private IUserService userService;

//	@RequestMapping("getNodesTest")
//	public void getNodesTest(){
//		String[] types = TreeNodeUtil.getNodeTypes(0);
//		WebServiceTO to = treeNodeWebService.getNodes(types, false, null);
//		System.out.println(to.getJsonData());
//	}

//	@RequestMapping("getSubNodesTest")
//	public void getSubNodesTest(){
//		String[] types = TreeNodeUtil.getNodeTypes(0);
//		WebServiceTO to = treeNodeWebService.getSubNodes(false, types, 644159L);
//		System.out.println(to.getJsonData());
//	}

	/**
	 * 为组织架构树获取子Organ
	 * @param parentId
	 * @return
	 */
	@RequestMapping("getSunOrganNodes")
	@ResponseBody
	public Object getSunOrganNodes(Long parentId) {
		return getObject(ldapFacadeService.getSubOrgans(parentId,0));
	}

	/**
	 * 为组织架构树获取子Organ
	 * @param parentId
	 * @return
	 */
	/*@RequestMapping("getTreeOrgans4UnitManager")
	@ResponseBody
	public Object getTreeOrgans4UnitManager(HttpSession session,Long parentId) {
		List<OrganNodeVO> organs = null;
		//parentId为空，表示获取当前单位
		if(parentId==null){
			Long userId = SessionUtil.getLongProperty(session, "userId");
			List<OrganEO> units = organService.getUnits4UnitManager(userId);
			if(units!=null&&units.size()>0){
				organs = new ArrayList<OrganNodeVO>(units.size());
				for(OrganEO unit:units){
					OrganNodeVO node = new OrganNodeVO();
					node.addProperties(unit, 0);
					organs.add(node);
				}
			}
		}else{
			organs = ldapFacadeService.getSubOrgans(parentId,0);
		}
		return getObject(organs);
	}*/

	/**
	 * 单位管理获取组织架构
	 * @param session
	 * @param parentId
	 * @param indicatorId
	 * @return
	 */
	@RequestMapping("getTreeOrgans4UnitManager")
	@ResponseBody
	public Object getTreeOrgans4UnitManager(Long parentId,Long siteId) {
		List<OrganNodeVO> organs = new ArrayList<OrganNodeVO>();
		//parentId为空，表示获取当前单位
		if(parentId==null){
			SiteMgrEO SiteMgrs = CacheHandler.getEntity(SiteMgrEO.class,LoginPersonUtil.getSiteId());
			if(SiteMgrs !=null && !StringUtils.isEmpty(SiteMgrs.getUnitIds())){
				//如果SiteMgrs.getUnitIds()是多个需要在处理
				List<OrganNodeVO> oNodes = organService.getOrganNodeVOs(new Long[]{Long.parseLong(SiteMgrs.getUnitIds())},0);
				if(oNodes != null && oNodes.size() >0 ){
					organs.addAll(oNodes);
				}
			}
		}else{
//			organs = ldapFacadeService.getSubOrgans(parentId,0);
			organs = organService.getSubOrgans(parentId,0);
		}
		return getObject(organs);
	}

	/**
	 * 为组织架构树获取子Organ
	 * @param session
	 * @param parentId
	 * @param indicatorId
	 * @return
	 */
	@RequestMapping("getTreeRoles4UnitManager")
	@ResponseBody
	public Object getTreeRoles4UnitManager(HttpSession session,Long parentId,Long indicatorId) {
		List<Object> objects = new ArrayList<Object>();
		//parentId为空，表示获取当前单位
		if(parentId==null){
			Long userId = SessionUtil.getLongProperty(session, "userId");
			List<OrganEO> units = organService.getFLUnits4UnitManager(userId,indicatorId);
			if(units!=null&&units.size()>0){
				for(OrganEO unit:units){
					OrganNodeVO node = new OrganNodeVO();
					node.addProperties(unit, 0);
					objects.add(node);
				}
			}
		}else{
			List<OrganNodeVO> organs = ldapFacadeService.getUnitNodes(parentId,4);
			if(organs!=null){
				objects.addAll(organs);
			}
			List<RoleNodeVO> roles = roleService.getRoleNodes(
					RoleEO.Type.Private.toString(), parentId);
			objects.addAll(roles);
		}
		return getObject(objects);
	}

	/**
	 * 为组织架构树获取子Organ
	 * @param session
	 * @param parentId
	 * @param indicatorId
	 * @return
	 */
	@RequestMapping("getTreePersons4UnitManager")
	@ResponseBody
	public Object getTreePersons4UnitManager(HttpSession session,Long parentId,Long indicatorId) {
		List<Object> objects = null;
		//parentId为空，表示获取当前单位
		if(parentId==null){
			Long userId = SessionUtil.getLongProperty(session, "userId");
			List<OrganEO> units = organService.getFLUnits4UnitManager(userId,indicatorId);
			if(units!=null&&units.size()>0){
				objects = new ArrayList<Object>(units.size());
				for(OrganEO unit:units){
					OrganNodeVO node = new OrganNodeVO();
					node.addProperties(unit, 0);
					objects.add(node);
				}
			}
		}else{
			objects = ldapFacadeService.getSubNodes(parentId);
		}
		return getObject(objects);
	}

	/**
	 * 系统管理选人、部门/处室和单位数据获取方法
	 * @param scope 用于描述需要获取的节点范围，scope支持0-3，0-获取所有的子节点，1-获取子单位、部门/处室和虚拟处室，2-获取子单位和部门/处室，3-获取子单位；
	 * @param organIds 第一级展示的组织/部门主键，如果为null，那么获取第一级节点，与parentId互斥-只能有一个有值；
	 * @param parentId 用于描述获取哪些单位、部门或虚拟部门的子节点,与organIds互斥-只能有一个有值；
	 * @param isContainsExternal 是否显示外平台
	 * @return
	 */
	@RequestMapping("getSubNodes")
	@ResponseBody
	public Object getSubNodes(int scope, Long[] organIds,Long parentId,@RequestParam(defaultValue="false")Boolean isContainsExternal) {
		//入参验证
		if(scope>3||scope<0){
			throw new IllegalArgumentException();
		}
		//默认为选人界面
		String[] nodeTypes = TreeNodeUtil.getNodeTypes(scope);
		List<TreeNodeVO> nodes = null;
		if(parentId!=null){
			nodes = ldapFacadeService.getSubNodes(isContainsExternal,nodeTypes, parentId,null);
		}else{
			if(organIds!=null&&organIds.length<=0){
				organIds = null;
			}
			nodes = ldapFacadeService.getNodes(nodeTypes, organIds,isContainsExternal);
		}
		//支持的节点类型
		return getObject(nodes);
	}

	/**
	 * 系统管理选人、部门/处室和单位数据获取方法
	 * @param scope 用于描述需要获取的节点范围，scope支持0-3，0-获取所有的子节点，1-获取子单位、部门/处室和虚拟处室，2-获取子单位和部门/处室，3-获取子单位；
	 * @param organIds 第一级展示的组织/部门主键，如果为null，那么获取第一级节点，与parentId互斥-只能有一个有值；
	 * @param parentId 用于描述获取哪些单位、部门或虚拟部门的子节点,与organIds互斥-只能有一个有值；
	 * @return
	 */
	@RequestMapping("getNodes")
	@ResponseBody
	public Object getNodes(int scope, Long[] organIds,Long parentId,@RequestParam(defaultValue="false")Boolean isContainsExternal) {
		//入参验证
		if(scope>3||scope<0){
			throw new IllegalArgumentException();
		}
		//默认为选人界面
		String[] nodeTypes = TreeNodeUtil.getNodeTypes(scope);
		List<TreeNodeVO> nodes = null;
		if(parentId!=null){
			nodes = ldapFacadeService.getSubNodes(isContainsExternal,nodeTypes,parentId,null);
		}else{
			if(organIds!=null&&organIds.length<=0){
				organIds = null;
			}
			nodes = ldapFacadeService.getNodes(nodeTypes, organIds,isContainsExternal);
		}
		//支持的节点类型
		return getObject(nodes);
	}

	@RequestMapping("getNodesByPersonName")
	@ResponseBody
	public Object getNodesByPersonName(Long[] organIds,String name) {
		//入参验证
		List<TreeNodeVO> targetNodes = ldapFacadeService.getSubNodesByPersonName(name,organIds);
		//返回给前端的对象
		List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
		List<Long> list = new ArrayList<Long>();
		if(organIds!=null&&organIds.length>0){
			Collections.addAll(list,organIds);
		}
		if(targetNodes!=null&&targetNodes.size()>0){
			Iterator<TreeNodeVO> iterator = targetNodes.iterator();
			while(iterator.hasNext()){
				TreeNodeVO node = iterator.next();
				//第一级单位
				if(organIds!=null&&organIds.length>0){
					if(node.getType().equals(TreeNodeVO.Type.Organ.toString())){
						if(list.contains(node.getUnitId())||StringUtils.isEmpty(node.getPid())){
							nodes.add(node);
							iterator.remove();
						}
					}
				}else{
					if(StringUtils.isEmpty(node.getPid())){
						nodes.add(node);
						iterator.remove();
					}
				}
			}
		}
		getChildren(nodes,targetNodes);
		//支持的节点类型
		return getObject(nodes);
	}

	@RequestMapping("getNodesByOrganName")
	@ResponseBody
	public Object getNodesByOrganName(Long[] organIds,String name,int scope) {
		//入参验证
		String[] types = TreeNodeUtil.getNodeTypes(scope);
		List<TreeNodeVO> targetNodes = ldapFacadeService.getSubNodesByOrganName(organIds, name,types);
		//返回给前端的对象
		List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
		List<Long> list = new ArrayList<Long>();
		if(organIds!=null&&organIds.length>0){
			Collections.addAll(list,organIds);
		}
		if(targetNodes!=null&&targetNodes.size()>0){
			Iterator<TreeNodeVO> iterator = targetNodes.iterator();
			while(iterator.hasNext()){
				TreeNodeVO node = iterator.next();
				//第一级单位
				if(organIds!=null&&organIds.length>0){
					if(node.getType().equals(TreeNodeVO.Type.Organ.toString())){
						if(list.contains(node.getUnitId())||StringUtils.isEmpty(node.getPid())){
							nodes.add(node);
							iterator.remove();
						}
					}
				}else{
					if(StringUtils.isEmpty(node.getPid())){
						nodes.add(node);
						iterator.remove();
					}
				}
			}
		}
		getChildren(nodes,targetNodes);
		//支持的节点类型
		return getObject(nodes);
	}

	private void getChildren(List<TreeNodeVO> parents,List<TreeNodeVO> children){
		if(parents==null||parents.size()<=0||children==null||children.size()<=0){
			return;
		}
		List<TreeNodeVO> organs = new ArrayList<TreeNodeVO>();
		for(TreeNodeVO parent:parents){
			String parentId = parent.getId();
			Iterator<TreeNodeVO> iterator = children.iterator();
			while(iterator.hasNext()){
				TreeNodeVO child = iterator.next();
				if(child.getPid().equals(parentId)){
					List<TreeNodeVO> nodes = parent.getChildren();
					if(nodes==null){
						nodes = new ArrayList<TreeNodeVO>();
						parent.setChildren(nodes);
					}
					nodes.add(child);
					if(!child.getType().equals(TreeNodeVO.Type.Person.toString())){
						organs.add(child);
					}
					iterator.remove();
				}
			}
		}
		getChildren(organs,children);
	}

	@RequestMapping("getPersonNames")
	@ResponseBody
	public Object getPersonNames(Long[] organIds,String name) {
		List<String> names = ldapFacadeService.getPersonNames(name, 8,organIds);
		return getObject(names);
	}

	@RequestMapping("getSimpleNodes")
	@ResponseBody
	public Object getSimpleNodes(Long[] organIds,String name,int scope) {
		List<SimpleNodeVO> nodes = null;
		if(name!=null){
			name = name.trim();
			if(!StringUtils.isEmpty(name)){
				nodes = ldapFacadeService.getSimpleNodes(name,organIds,scope);
			}
		}
		return getObject(nodes);
	}

	/**
	 * 为组织架构树获取子节点，包括组织、单位和人员
	 *
	 * @param parentId
	 * @return
	 */
	@RequestMapping("getSunNodes")
	@ResponseBody
	public Object getSunNodes(Long parentId) {
		return getObject(ldapFacadeService.getSubNodes(parentId));
	}

	/**
	 * 角色树展示
	 *
	 * @param parentId
	 * @return
	 */
	@RequestMapping("getNodes4Roles")
	@ResponseBody
	public Object getNodes4Roles(Long parentId){
		List<TreeNodeVO> nodes = ldapFacadeService.getNodes4Roles(parentId);
		return getObject(nodes);
	}

	/**
	 * 为组织架构树获取子节点，包括组织、单位和角色
	 *
	 * @param organId
	 * @return
	 */
	@RequestMapping("getOrganRole")
	@ResponseBody
	public Object getOrganRole(Long parentId) {
		List<Object> list = new ArrayList<Object>();
		List<OrganNodeVO> organs = ldapFacadeService.getUnitNodes(parentId,4);
		if(organs!=null){
			list.addAll(organs);
		}
		if (parentId != null) {
			List<RoleNodeVO> roles = roleService.getRoleNodes(
					RoleEO.Type.Private.toString(), parentId);
			list.addAll(roles);
		}
		return getObject(list);
	}

	/**
	 * 为组织架构树获取子节点，包括组织、单位和角色
	 *
	 * @param organId
	 * @return
	 */
	@RequestMapping("getOrganRole4Unit")
	@ResponseBody
	public Object getOrganRole4Unit(Long parentId,Long organId) {
		List<Object> list = new ArrayList<Object>();
		if(parentId==null){
			OrganEO organ = organService.getEntity(OrganEO.class, organId);
			list.add(OrganNodeUtils.getOrganNode(organ, 4));
		}else{
			List<OrganNodeVO> organs = ldapFacadeService.getUnitNodes(parentId,4);
			if(organs!=null){
				list.addAll(organs);
			}
			List<RoleNodeVO> roles = roleService.getRoleNodes(
					RoleEO.Type.Private.toString(), parentId);
			list.addAll(roles);
		}
		return getObject(list);
	}


	/**
	 * 获取person，personId不为空时从数据库读取人员信息，否则初始化一个新的人员给前端
	 *
	 * @param personId
	 * @param organId
	 * @return
	 */
	@RequestMapping("getPerson")
	@ResponseBody
	public Object getPerson(Long personId, Long organId) {
		PersonNodeVO node = null;
		if (personId != null) {
			node = ldapFacadeService.getPerson(personId);
		} else {
			if(organId==null){
				throw new NullPointerException();
			}
			node = ldapFacadeService.getEmptyPerson(organId);
		}
		return getObject(node);
	}

	/**
	 * 为单位（organId）获取兼职人员（personId）信息
	 * @param personId
	 * @param organId
	 * @return
	 */
	@RequestMapping("getPluralisticPerson")
	@ResponseBody
	public Object getPluralisticPerson(Long personId, Long organId) {
		if(personId==null||organId==null){
			throw new NullPointerException();
		}
		return getObject(ldapFacadeService.getPluralistic(personId, organId));
	}

	/**
	 * 更新person
	 * @param node
	 * @return
	 */
	@RequestMapping("updatePluralisticPerson")
	@ResponseBody
	public Object updatePluralisticPerson(PersonNodeVO node) {
		//更新缓存
		Node4SaveOrUpdateVO vo = ldapFacadeService.updatePluralisticPerson(node);
		InternalLdapCache.getInstance().update(vo.getPId());
		return getObject(vo);
	}

	@RequestMapping("savePluralisticPerson")
	@ResponseBody
	public Object savePluralisticPerson(PersonNodeVO node) {
		Node4SaveOrUpdateVO n = ldapFacadeService.savePluralistic(node);
		//更新缓存
		InternalLdapCache.getInstance().update(n.getPId());
		return getObject(n);
	}

	/**
	 * 人員移動
	 * @param organId
	 * @param personId
	 * @param isRemoveRoles
	 * @return
	 */
	@RequestMapping("move")
	@ResponseBody
	public Object move(Long organId, Long personId,boolean isRemoveRoles) {
		PersonNodeVO node = new PersonNodeVO();
		try {
			Long srcOrganId = personService.update4move(personId, organId,isRemoveRoles);
			//更新部门hasPersons字段
			organService.updateHasPersons(srcOrganId);
			organService.updateHasPersons(organId, Integer.valueOf(1));
			//构造返回的节点对象
			PersonEO p = personService.getEntity(PersonEO.class, personId);
			BeanUtils.copyProperties(p, node);
			node.setId(p.getPersonId());
			node.setPid(p.getOrganId());
			node.setNodeType(TreeNodeVO.Type.Person.toString());
			node.setIsPluralistic(p.getIsPluralistic());
			node.setIcon(Icon.Male.getValue());
			if (srcOrganId != null) {
				InternalLdapCache.getInstance().update(organId);
				InternalLdapCache.getInstance().update(srcOrganId);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BaseRunTimeException(TipsMode.Key.toString(), e.getKey());
		}
		return getObject(node);
	}

	/**
	 * 批量导入ldap
	 * @return
	 */
	@RequestMapping("importOrganPersons")
	@ResponseBody
	public Object importOrganPersons() {
		List<OrganEO> organs = organService.getOrgans(null);
		importOPS(organs);
		return  getObject();
	}

	private void importOPS(List<OrganEO> organs) {
		if(organs != null && organs.size() >0){
			for(OrganEO organ:organs){
				try{
					if(organ != null && !StringUtils.isEmpty(organ.getDn())){
						ldapOrganService.save(LDAPUtil.getSimpleDn(organ.getDn()).replace("o=","").replace("ou=",""), organ);
					}
					if(organ.getType().equals(OrganEO.Type.OrganUnit.toString())){
						savePersonLdap(organ.getOrganId());
					}
				}catch (Exception e){}
				List<OrganEO> list = organService.getOrgans(organ.getOrganId());
				importOPS(list);
			}
		}
	}

	//更新人员至ldap
	private void savePersonLdap(Long organId) {
		List<PersonEO> list=personService.getPersons(organId);
		if(list!=null && list.size()>0){
			for(PersonEO p:list){
				// Ldap保存
				UserEO user = userService.getEntity(UserEO.class,p.getUserId());
				if(user != null){
					ldapPersonService.save(p, user);
				}
			}
		}
	}
}
