package cn.lonsun.ucenter.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.util.PoiExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.RegexUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Icon;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.util.IndicatorUtil;
import cn.lonsun.ldap.internal.cache.InternalLdapCache;
import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.rbac.vo.Node4SaveOrUpdateVO;
import cn.lonsun.rbac.vo.PersonQueryVO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.system.role.internal.service.IRoleAsgService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * Person控制器
 *
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午3:57:58
 * @version V1.0
 */
@Controller
@RequestMapping(value = "/person", produces = { "application/json;charset=UTF-8" })
public class PersonController extends BaseController {

	@Autowired
	private IPersonService personService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrganService organService;
	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private IRoleAsgService roleAsgService;


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
//			if (srcOrganId != null) {
//				InternalLdapCache.getInstance().update(organId);
//				InternalLdapCache.getInstance().update(srcOrganId);
//			}
			//		//异步执行
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					personService.initSimplePersonsCache();
				}
			});
		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BaseRunTimeException(TipsMode.Key.toString(), e.getKey());
		}
		return getObject(node);
	}

	@RequestMapping("personExport")
	public String personExport(Model m){
		return "/system/ucenter/person_export";
	}

	/**
	 * 下载模版
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("downPersonxls")
	public void downPersonXls(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String dn = request.getParameter("dn");
		if(StringUtils.isEmpty(dn)){
			throw new BaseRunTimeException(TipsMode.Message.toString(),"选择组织dn不能为空");
		}
		String[] headers = new String[]{"部门ID","所属部门","排序号","用户名称","用户账号","密码","职务","手机号","办公电话","办公地址","角色IDS"};
		//查询此单位下所有部门
		List<OrganEO> organs = organService.getOrganUnitsByDn(dn);
		List<Object[]> values = null;
		if(organs != null && organs.size() > 0){
			values = new ArrayList<Object[]>();
			for(OrganEO organ:organs){
				Object[] objects = new Object[10];
				objects[0] = organ.getOrganId();
				objects[1] =getPath(organ.getOrganId());
				values.add(objects);
			}
		}
		PoiExcelUtil.exportExcel("用户导入", "用户导入模版", "xls", headers, values, response);
	}

	private String getPath(Long parentId){
		Map<String, String> map = new HashMap();
		getNameCode(parentId, "", map);
		return (String)map.get("rtxPath");
	}

	private void getNameCode(Long parentId, String name, Map<String, String> mapCode){
		if (parentId != null){
			OrganEO o = organService.getEntity(OrganEO.class, parentId);
			if(o != null){
				name = o.getName() + "/" + name;
				getNameCode(o.getParentId(), name, mapCode);
			}else{
				mapCode.put("rtxPath", name);
			}
		}else{
			mapCode.put("rtxPath", name);
		}
	}


	/**
	 * 用户管理树页面
	 *
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("userPage")
	public String userPage(Model m) {
		return "/system/ucenter/user";
	}

	/**
	 * 用户列表
	 *
	 * @param indicatorId 菜单ID
	 * @param request
	 * @return
	 */
	@RequestMapping("userListPage")
	public String userListPage(Long id,Model m) {
		m.addAttribute("organId", id);
		return "/system/ucenter/user_list";
	}


	@RequestMapping("addRoles")
	public String addRoles(String roleIds,Model m) {
		m.addAttribute("roleIds", roleIds);
		return "/system/ucenter/user_addRoles";
	}

	/**
	 * 人员编辑
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("userEditPage")
	public String userEditPage(Long organId,Long personId,Model m) {
		m.addAttribute("organId", organId);
		m.addAttribute("personId", personId);
		return "/system/ucenter/user_edit";
	}


	@RequestMapping({"getPersonTree4UnitManager"})
	@ResponseBody
	public Object getPersonTree4UnitManager(HttpSession session, Long parentId, Long indicatorId){
		List<Object> nodes = new ArrayList<Object>();
		List<OrganNodeVO> oNodes = null;
		if (parentId == null){
//			Long unitId = ContextHolderUtils.getUnitId();
			SiteMgrEO SiteMgrs = CacheHandler.getEntity(SiteMgrEO.class,LoginPersonUtil.getSiteId());
			if(SiteMgrs !=null && !StringUtils.isEmpty(SiteMgrs.getUnitIds())){
				//如果SiteMgrs.getUnitIds()是多个需要在处理
				oNodes = organService.getOrganNodeVOs(new Long[]{Long.parseLong(SiteMgrs.getUnitIds())},0);
			}
		}else{
			oNodes = organService.getSubOrgans(parentId, null,null, true, 1);

		}
		if(oNodes != null && oNodes.size()>0){
			nodes.addAll(oNodes);
		}
		if (parentId != null){
			List<PersonEO> persons = this.personService.getPersons(parentId);
			if (persons != null && persons.size() > 0) {
				for (PersonEO person : persons){
					PersonNodeVO np = getPersonNode(person);
					nodes.add(np);
				}
			}
		}
		return getObject(nodes);
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



	/**
	 * 将兼职用户更改为宿主用户
	 *
	 * @param personId
	 * @param srcPersonId 原宿主人员主键
	 * @return
	 */
	@RequestMapping("updateToMainPerson")
	@ResponseBody
	public Object updateToMainPerson(Long personId,Long srcPersonId){
		if(personId==null||srcPersonId==null){
			throw new BaseRunTimeException();
		}
		personService.updateToMainPerson(personId, srcPersonId);
		return getObject();
	}

	/**
	 * 用户管理首页
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("systemPage")
	public String systemPage(HttpServletRequest request) {
		String uid = SessionUtil.getStringProperty(request.getSession(), "uid");
		request.setAttribute("uid", uid);
		return "/app/mgr/system";
	}



	/**
	 * 用户管理树页面
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("usersPage")
	public String usersPage(HttpServletRequest request) {
		return "/app/mgr/unitmanager/user";
	}

	@RequestMapping("deletedPersonsPage")
	public String deletedPersonsPage(){
		return "/app/mgr/developer/user_list";
	}


	/**
	 * 保存人员信息和账号信息
	 *
	 * @param node
	 * @return
	 */
	@RequestMapping("savePersonAndUser")
	@ResponseBody
	public Object savePersonAndUser(PersonNodeVO node){
		checkPersonAndUser(node,true);
		//保存
		PersonEO person = personService.savePersonAndUser(node);
		//		//异步执行
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				personService.initSimplePersonsCache();
			}
		});
		//更新缓存
//				InternalLdapCache.getInstance().update(person.getOrganId());
		Node4SaveOrUpdateVO n = new Node4SaveOrUpdateVO(person.getPersonId(),person.getOrganId(), person.getName(), false);
		n.setPositions(person.getPositions());
		n.setIcon(Icon.Male.getValue());
		n.setUserId(person.getUserId());

		return getObject(n);
	}

	/**
	 * 人员信息验证
	 *
	 * @param node
	 * @param isSave 保存或更新
	 */
	public void checkPersonAndUser(PersonNodeVO node,boolean isSave){
		//排序号合法性验证
		Long sortNum = node.getSortNum();
		if(sortNum==null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入排序号");
		}else{
			if(sortNum.longValue()<0){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "排序号不能小于0");
			}
			if(sortNum>999999){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "排序号不能大于999999");
			}
		}
		//姓名
		String name = node.getName();
		if(StringUtils.isEmpty(name)){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入姓名");
		}else{
			//去除空格
			name = name.trim();
			if(!RegexUtil.isCombinationOfChineseAndCharactersAndNumbers(name)){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "名称仅支持中文、英文数字和部分中文标点符号的组合");
			}
			int length = name.length();
			if(length<2||length>20){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "姓名长度2-20个字符");
			}
			node.setName(name);
		}
		//职务
		String positions = node.getPositions();
		if(!StringUtils.isEmpty(positions)){
			positions = positions.trim();
			int length = positions.length();
			if(length>80){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "职务长度0-80个字符");
			}
			node.setPositions(positions);
		}
		//手机号
		String mobile = node.getMobile();
		if(!StringUtils.isEmpty(mobile)){
			mobile = mobile.trim();
			if(mobile.length()>0&&mobile.length()!=11){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入正确的手机号");
			}
			if(personService.isMobileExisted(node.getPersonId(),mobile)){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "该手机号已存在，请输入新的手机号");
			}

			node.setMobile(mobile);
		}
		//电话号码长度：
		String officePhone = node.getOfficePhone();
		if(!StringUtils.isEmpty(officePhone)){
			officePhone = officePhone.trim();
			if(officePhone.length()>32){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "办公电话长度0-32个字符");
			}
			node.setOfficePhone(officePhone);
		}
		//办公地址
		String officeAddress = node.getOfficeAddress();
		if(!StringUtils.isEmpty(officeAddress)){
			officeAddress = officeAddress.trim();
			if(officeAddress.length()>80){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "办公地址长度0-80个字符");
			}
			node.setOfficeAddress(officeAddress);
		}
		//账号长度验证
		if(isSave){//更新无需验证
			String uid = node.getUid();
			if(StringUtils.isEmpty(uid)){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入账号");
			}else{
				if(node.getUid().length()<2||node.getUid().length()>50){
					throw new BaseRunTimeException(TipsMode.Message.toString(), "帐号长度2-50个字符");
				}
				String password = node.getPassword();
				if(StringUtils.isEmpty(password)||password.trim().length()<1||password.trim().length()>16){
					throw new BaseRunTimeException(TipsMode.Message.toString(), "密码长度1-16个字符");
				}
			}
		}
		//密码验证
		String password = node.getPassword();
		if(!StringUtils.isEmpty(password)){
			if(password.trim().length()<1||password.trim().length()>30){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "密码长度1-30个字符");
			}
		}
		//移动端编码
		String mobileCode = node.getMobileCode();
		if(!StringUtils.isEmpty(mobileCode)){
			mobileCode = mobileCode.trim();
			if(mobileCode.length()>80){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "移动端编码0-80个字符");
			}
		}
	}


	/**
	 * 跟新个人信息和账号信息
	 *
	 * @param node
	 * @return
	 */
	@RequestMapping("updatePersonAndUser")
	@ResponseBody
	public Object updatePersonAndUser(PersonNodeVO node){
		checkPersonAndUser(node, false);
		List<PersonEO> persons = personService.updatePersonAndUser(node);
		//		//异步执行
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				personService.initSimplePersonsCache();
			}
		});
		//更新缓存
		List<Node4SaveOrUpdateVO> nodes = new ArrayList<Node4SaveOrUpdateVO>();
		if (persons != null && persons.size() > 0) {
			for (PersonEO p : persons) {
				//更新缓存
				//				InternalLdapCache.getInstance().update(p.getOrganId());
				Node4SaveOrUpdateVO n = new Node4SaveOrUpdateVO(p.getPersonId(), p.getOrganId(), p.getName(), true);
				n.setIsPluralistic(p.getIsPluralistic());
				n.setPositions(p.getPositions());
				nodes.add(n);
			}
		}
		return getObject(nodes);
	}

	/**
	 * 跟新个人信息和账号信息
	 *
	 * @param node
	 * @return
	 */
	@RequestMapping("updatePersonAndUser4Unit")
	@ResponseBody
	public Object updatePersonAndUser4Unit(PersonNodeVO node){
		checkPersonAndUser(node, false);
		List<PersonEO> persons = personService.updatePersonAndUser4Unit(node);
		//更新缓存
		List<Node4SaveOrUpdateVO> nodes = new ArrayList<Node4SaveOrUpdateVO>();
		if (persons != null && persons.size() > 0) {
			for (PersonEO p : persons) {
				//更新缓存
				InternalLdapCache.getInstance().update(p.getOrganId());
				Node4SaveOrUpdateVO n = new Node4SaveOrUpdateVO(p.getPersonId(), p.getOrganId(), p.getName(), true);
				n.setIsPluralistic(p.getIsPluralistic());
				n.setPositions(p.getPositions());
				nodes.add(n);
			}
		}
		return getObject(nodes);
	}

	/**
	 * 人员信息验证
	 *
	 * @param node
	 */
	public void checkPersonAndUser4Update(PersonNodeVO node){
		//姓名
		if(StringUtils.isEmpty(node.getName())){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入姓名.");
		}
		//排序号合法性验证
		if(node.getSortNum()!=null&&node.getSortNum()<0){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入正确的排序号.");
		}
		//密码验证
		String password = node.getPassword();
		if(!StringUtils.isEmpty(password)){
			if(password.trim().length()<1||password.trim().length()>16){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "密码长度1-16个字符");
			}
		}
		//电话号码长度：
		String officePhone = node.getOfficePhone();
		if(!org.apache.commons.lang3.StringUtils.isEmpty(officePhone)){
			if(officePhone.trim().length()>32){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "办公电话长度0-32个字符");
			}
		}
	}


	/**
	 * 用户列表
	 *
	 * @param indicatorId 菜单ID
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("userUnitPage")
	public String userUnitPage(HttpServletResponse response,Long indicatorId,Long id, HttpSession session) throws IOException {
		if (indicatorId == null||id==null) {
			throw new NullPointerException();
		}
		/*//organId只能是单位管理员自己单位内的单位或部门ID
		Long unitId = SessionUtil.getLongProperty(session, "unitId");
		//标记用户是否有访问权限
		boolean hasPermission = false;
		if(id.longValue()!=unitId.longValue()){
			List<Long> organIds = organService.getDescendantOrganIds(unitId);
			if(organIds!=null&&organIds.size()>0){
				for(Long organId:organIds){
					if(organId.longValue()==id.longValue()){
						hasPermission = true;
						break;
					}
				}
			}
		}else{
			hasPermission = true;
		}
		if(!hasPermission){
			//throw new BaseRunTimeException(TipsMode.Message.toString(), "无权限访问");
			response.sendError(HttpStatus.FORBIDDEN.value());
		}*/
		// 查找的对象类型为ToolBarButton
		Long userId = SessionUtil.getLongProperty(session,"userId");
		String type = IndicatorEO.Type.ToolBarButton.toString();
		List<IndicatorEO> buttons = personService.getSysButtons(userId,indicatorId, type);
		if (buttons != null && buttons.size() > 0) {
			// 按钮存入session的key为menu_菜单ID
			String key = IndicatorUtil.PRE_KEY.concat(indicatorId.toString());
			session.setAttribute(key, buttons);
		}
		return "/app/mgr/unitmanager/user_list";
	}

	/**
	 * 兼职用户
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("userPluralisticPage")
	public String userPluralisticPage(HttpServletRequest request) {
		return "/app/mgr/user_pluralistic";
	}

	/**
	 * 单位管理-兼职用户
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("unitUserPluralisticPage")
	public String unitUserPluralisticPage(HttpServletRequest request) {
		return "/app/mgr/unitmanager/user_pluralistic";
	}

	/**
	 * 人员编辑
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("unitPersonEditPage")
	public ModelAndView unitPersonEditPage(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		Long unitId = SessionUtil.getLongProperty(session, "unitId");
		mav.addObject("unitId", unitId);
		mav.setViewName("/app/mgr/unitmanager/person_unit_edit");
		return mav;
	}

	/**
	 * 用户管理首页
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("personListPage")
	public String personListPage(HttpServletRequest request) {
		return "/app/mgr/user_list";
	}

	/**
	 * 用户编辑页面
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("editPage")
	public String editPage(HttpServletRequest request) {
		return "/app/mgr/user_edit";
	}

	/**
	 * 兼职用户页面
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("pluralisticPage")
	public String pluralisticPage(HttpServletRequest request) {
		return "/app/mgr/user_pluralistic";
	}

	/**
	 * 如果人员存在兼职情况，那么删除失败
	 *
	 * @param personIds
	 * @return
	 */
	@RequestMapping("deletePersonList")
	@ResponseBody
	public Object deletePerson(@RequestParam("personIds") Long[] personIds) {
		if (personIds != null && personIds.length > 0) {
			List<Long> organIds = personService.delete(personIds);
			// 更新缓存
			if (organIds != null && organIds.size() > 0) {
				for (Long organId : organIds) {
					InternalLdapCache.getInstance().update(organId);
				}
			}
		}
		return getObject();
	}

	@RequestMapping("restore")
	@ResponseBody
	public Object restore(@RequestParam(required=true)Long personId){
		PersonEO person = personService.restore(personId);
		//更新缓存
		InternalLdapCache.getInstance().update(person.getOrganId());
		return getObject();
	}

	/**
	 * 如果人员存在兼职情况，那么兼职人员也一起删除
	 *
	 * @param personIds
	 * @return
	 */
	@RequestMapping("deletePersonsList")
	@ResponseBody
	public Object deletePersonsList(@RequestParam("personIds") Long[] personIds) {
		if (personIds == null || personIds.length <= 0) {
			throw new NullPointerException();
		}
		List<Long> ids = new ArrayList<Long>(personIds.length);
		Collections.addAll(ids, personIds);
		List<Long> organIds = personService.deletePersons(ids);
		//更新父部门的hasPersons
		for(Long organId:organIds){
			organService.updateHasPersons(organId);
		}
		//异步执行
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				personService.initSimplePersonsCache();
			}
		});
		if (organIds != null && organIds.size() > 0) {
			for (Long organId : organIds) {
				organService.updateHasPersons(organId);
				//更新缓存
				//				InternalLdapCache.getInstance().update(organId);
			}
		}
		return getObject(organIds);
	}

	/**
	 * 姓名是否可用
	 *
	 * @param organId
	 * @param name
	 * @return
	 */
	@RequestMapping("isNameExisted")
	@ResponseBody
	public Object isNameExisted(Long organId, String name) {
		if (organId == null || StringUtils.isEmpty(name)) {
			throw new NullPointerException();
		}
		boolean isNameExisted = personService.isNameExisted(organId, name);
		return getObject(isNameExisted ? 0 : 1);
	}

	/**
	 * 根据姓名获取uid，如果uid已存在，那么直接
	 * @param organId
	 * @param name
	 * @return
	 */
	@RequestMapping("checkName")
	@ResponseBody
	public Object checkName(Long organId,String name){
		Map<String, String> map = new HashMap<String, String>();
		if(!StringUtils.isEmpty(name)){
			boolean isExisted = personService.isNameExisted(organId, name);
			if(isExisted){
				map.put("error", "姓名已存在");
			}else{
				map.put("ok", "");
			}
		}
		return getObject(map);
	}

	/**
	 * 获取人员分页信息
	 *
	 * @param query
	 * @param roleIds
	 * @return
	 */
	@RequestMapping("getPersonsPage")
	@ResponseBody
	public Object getPersonsPage(PersonQueryVO query) {
		// 页码与查询最多查询数据量纠正
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		Pagination page = personService.getInfoPage(query);
		return getObject(page);
	}

	/**
	 * 获取人员分页信息
	 *
	 * @param query
	 * @param roleIds
	 * @return
	 */
	@RequestMapping("getDeletedPersonsPage")
	@ResponseBody
	public Object getDeletedPersonsPage(PersonQueryVO query) {
		// 页码与查询最多查询数据量纠正
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		Pagination page = personService.getDeletedPersonsPage(query);
		return getObject(page);
	}


	/**
	 * 单位管理员获取人员分页信息
	 *
	 * @param query
	 * @param roleIds
	 * @return
	 */
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(PersonQueryVO query,HttpSession session) {
		// 页码与查询最多查询数据量纠正
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		Pagination page = personService.getInfoPage(query);
		return getObject(page);
	}

	/**
	 * 获取人员分页信息
	 *
	 * @param query
	 * @param roleId
	 * @param searchText
	 * @return
	 */
	@RequestMapping("getPage4Role")
	@ResponseBody
	public Object getPage4Role(PageQueryVO query,Long roleId,String searchText) {
		if(roleId==null||roleId<=0){
			throw new IllegalArgumentException();
		}
		// 页码与查询最多查询数据量纠正
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		Pagination page = personService.getPage4RoleBySql(query, roleId, searchText);
		return getObject(page);
	}

	/**
	 * 获取excel导出数据
	 *
	 * @param organId
	 * @param subUid
	 * @param name
	 * @param organName
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	@RequestMapping("downloadPersonInfos")
	public void downloadPersonInfos(Long organId, Long[] roleIds,
									String searchText, String sortField, String sortOrder,
									HttpServletResponse response) {
		if (organId == null) {
			throw new NullPointerException();
		}
		try {
			searchText = new String(searchText.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		List<PersonNodeVO> nodes = personService.getExcelResults(organId,roleIds, searchText, sortField, sortOrder);
		String[] titles = new String[] { "姓名", "单位名", "用户名", "登录方式", "登录次数",
				"最后登录时间", "最后登录IP" };
		List<String[]> datas = null;
		if (nodes != null && nodes.size() > 0) {
			datas = new ArrayList<String[]>(nodes.size());
			for (PersonNodeVO node : nodes) {
				Date lastLoginDate = node.getLastLoginDate();
				String date = null;
				if (lastLoginDate != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
					date = sdf.format(lastLoginDate);
				}
				// 用户名和密码登录(0)、动态令牌登录(1)、用户名和密码或动态令牌登录(2)
				Integer loginType = node.getLoginType();
				String type = null;
				switch (loginType) {
					case 0:
						type = "用户名和密码登录";
						break;
					case 1:
						type = "动态令牌登录";
						break;
					case 2:
						type = "用户名和密码或动态令牌登录";
						break;
					default:
						type = "用户名和密码登录";
						break;
				}
				String[] data = new String[] { node.getName(),
						node.getOrganName(), node.getUid(), type,
						String.valueOf(node.getLoginTimes()), date,node.getLastLoginIp() };
				datas.add(data);
			}
		}
		try {
			CSVUtils.download("用户列表", titles, datas, response);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BaseRunTimeException();
		}
	}
}
