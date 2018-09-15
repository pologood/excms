package cn.lonsun.ucenter.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.sso.util.DESedeUtil;
import cn.lonsun.common.sso.util.EncryptKey;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.core.util.ThreadUtil.LocalParamsKey;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.log.internal.entity.LoginHistoryEO;
import cn.lonsun.log.internal.service.ILoginHistoryService;
import cn.lonsun.rbac.cache.RolesCache;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.rbac.internal.service.impl.UserServiceImpl.LoginDescription;
import cn.lonsun.rbac.utils.AdminUidVO;
import cn.lonsun.rbac.utils.PinYinUtil;
import cn.lonsun.rbac.utils.ValidateCode;

/**
 * UserEO管理控制器
 * 
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:05:24
 * @version V1.0
 */
@Controller
@RequestMapping(value = "/user", produces = { "application/json;charset=UTF-8" })
public class UserController extends BaseController {
	
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleAssignmentService roleAssignmentService;
	@Autowired
	private ILoginHistoryService loginHistoryService;
	
	
	@RequestMapping("ucenterIndex")
	public String ucenterIndex() {
		return "/system/ucenter/index";
	}
	
	/**
	 * 退出
	 *
	 * @param session
	 * @return
	 */
	@RequestMapping("logout")
	public String logout(HttpSession session){
		session.invalidate();
		return "/app/login/login";
	}

	@RequestMapping("loginPage")
	public String loginPage() {
		return "/app/login/login";
	}
	
	@RequestMapping("adminEditPage")
	public String adminEditPage(HttpServletRequest request,String uid) {
		request.setAttribute("uid", uid);
		return "/app/mgr/admin_edit";
	}

	/**
	 * 弹窗登录
	 * 
	 * @return
	 */
	@RequestMapping("miniLoginPage")
	public String miniLogin() {
		return "/app/mgr/mini_login";
	}
	
	@RequestMapping("rootPage")
	public String rootInit(){
		return "/app/mgr/develop/root";
	}
	
	@RequestMapping("updateAdminOrRootPwd")
	@ResponseBody
	public Object updateAdminOrRootPwd(String uid,String password){
		if(StringUtils.isEmpty(password)||password.trim().length()<6||password.trim().length()>16){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入6-16位字符");
		}
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		UserEO user = userService.getEntity(UserEO.class, params);
		user.setPassword(DigestUtils.md5Hex(password));
		try {
			String key = EncryptKey.getInstance().getKey();
			//密码对称加密解密
			String desPassword = DESedeUtil.encrypt(password, key);
			user.setDesPassword(desPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		userService.updateEntity(user);
		return getObject();
	}
	

	/**
	 * 获取验证码
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("getCode")
	@ResponseBody
	public void getCode(HttpServletRequest request, HttpServletResponse response) {
		// 设置响应的类型格式为图片格式
		response.setContentType("image/jpeg");
		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		HttpSession session = request.getSession();
		ValidateCode vCode = new ValidateCode(120, 40, 4, 10);
		session.setAttribute("webCode", vCode.getCode());
		try {
			vCode.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		String uid = null;
		if(!StringUtils.isEmpty(name)){
			try {
				name = new String(name.getBytes("ISO-8859-1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String tempUid = PinYinUtil.cn2Spell(name);
			int times = 0;
			uid = tempUid;
			while (userService.isUidExisted(uid)) {
				times++;
				uid = tempUid+times;
			};
		}
		return getObject(uid);
	}
	
	/**
	 * 获取uid
	 *
	 * @param name
	 * @return
	 */
	@RequestMapping("createUid")
	@ResponseBody
	public Object createUid(String name){
		String uid = null;
		if(!StringUtils.isEmpty(name)){
			String tempUid = PinYinUtil.cn2Spell(name.trim());
			uid = tempUid;
			int times = 0;
			while (userService.isUidExisted(uid)) {
				times++;
				uid = tempUid+times;
			};
		}
		return getObject(uid);
	}
	
	/**
	 * uid是否存在
	 *
	 * @param uid
	 * @return
	 */
	@RequestMapping("checkUid")
	@ResponseBody
	public Object checkUid(String uid){
		boolean isExisted = false;
		if(StringUtils.isEmpty(uid)){
			throw new BaseRunTimeException();
		}else{
			isExisted = userService.isUidExisted(uid);
		}
		Map<String, String> map = new HashMap<String, String>();
		if(isExisted){
			map.put("error", "用户名已存在");
		}else{
			map.put("ok", "");
		}
		return getObject(map);
	}

	/**
	 * 验证码是否正确
	 * 
	 * @param request
	 * @param code
	 */
	@RequestMapping("isCodeAvailable")
	@ResponseBody
	public Object isCodeAvailable(HttpServletRequest request, String code) {
		if (!StringUtils.isEmpty(code)) {
			String targetCode = request.getSession(false).getAttribute("code")
					.toString();
			if (!code.equals(targetCode)) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),
						"请输入正确的验证码");
			}
		} else {
			throw new BaseRunTimeException(TipsMode.Message.toString(),
					"请输入验证码");
		}
		return getObject();
	}

	/**
	 * 登录
	 * 
	 * @param uid
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public Object login(HttpServletRequest request, String uid,String password, String code) throws Exception {
		//登录日志对象
		LoginHistoryEO history = new LoginHistoryEO();
		history.setUid(uid);
		String ip = RequestUtil.getIpAddr(request);
		history.setLoginIp(ip);
		try {
			// 帐号为空
			if (StringUtils.isEmpty(uid)) {
				throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入帐号");
			}
			// 密码为空
			if (StringUtils.isEmpty(password)) {
				throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入密码");
			}
			if (!StringUtils.isEmpty(code)) {
				code = code.toLowerCase();
				Object obj = request.getSession().getAttribute("code");
				if(obj==null){
					throw new BaseRunTimeException(TipsMode.Message.toString(),"验证码已失效");
				}
				String targetCode = obj.toString().toLowerCase();
				if (StringUtils.isEmpty(targetCode) || !code.equals(targetCode)) {
					throw new BaseRunTimeException(TipsMode.Message.toString(),"请输入正确的验证码");
				}
			} else {
				throw new BaseRunTimeException(TipsMode.Message.toString(),"请输入验证码");
			}
			HttpSession session = request.getSession(true);
			//验证账号是否被禁用
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uid", uid);
			params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
			UserEO user = userService.getEntity(UserEO.class, params);
			if(user==null){
				throw new BaseRunTimeException(TipsMode.Message.toString(),"用户或密码错误");
			}else{
				String status = user.getStatus();
				//外平台用户无权限访问本系统
				if(StringUtils.isEmpty(status)||UserEO.STATUS.External.toString().equals(status)){
					throw new BaseRunTimeException(TipsMode.Message.toString(),"外平台用户无权限访问本系统");
				}
				if(StringUtils.isEmpty(status)||UserEO.STATUS.Unable.toString().equals(status)){
					throw new BaseRunTimeException(TipsMode.Message.toString(),"您的账号已被锁定，请联系管理员");
				}
			}
			//MD5加密
			String md5Pwd = DigestUtils.md5Hex(password);
			List<Long> roleIds = null;
			if(AdminUidVO.developerUid.equals(uid)||AdminUidVO.superAdminUid.equals(uid)){
				user = userService.login4Developer(uid, md5Pwd);
				session.setAttribute("userId", user.getUserId());
				session.setAttribute("uid", user.getUid());
				history.setLoginStatus(LoginHistoryEO.LoginStatus.Success.toString());
				history.setDescription(LoginDescription.LoginSueess.getValue());
			}else{
				PersonEO person = userService.login(uid, md5Pwd,RequestUtil.getIpAddr(request),code);
				//无系统管理角色，无权限访问
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", person.getUserId());
				map.put("roleType", RoleEO.Type.System.toString());
				map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
				List<RoleAssignmentEO> ras = roleAssignmentService.getEntities(RoleAssignmentEO.class, map);
				if(ras==null||ras.size()<=0){
					throw new BaseRunTimeException(TipsMode.Message.toString(), "无权访问！");
				}
				roleIds = userService.getRoleIds(person.getOrganId(),person.getUserId());
				session.setAttribute("unitId", person.getUnitId());
				session.setAttribute("organId", person.getOrganId());
				session.setAttribute("organName", person.getOrganName());
				session.setAttribute("personId", person.getPersonId());
				session.setAttribute("personName", person.getName());
				session.setAttribute("userId", person.getUserId());
				session.setAttribute("uid", person.getUid());
				history.setUnitId(person.getUnitId());
				history.setUnitName(person.getUnitName());
				history.setOrganId(person.getOrganId());
				history.setOrganName(person.getOrganName());
				history.setCreateUser(person.getName());
				history.setLoginStatus(LoginHistoryEO.LoginStatus.Success.toString());
				history.setDescription(LoginDescription.LoginSueess.getValue());
			}
			session.setAttribute("roleIds", roleIds);
			RolesCache.getInstance().put(uid, roleIds);
		} catch (Exception e) {
			String message = null;
			if (e instanceof BaseRunTimeException) {
				BaseRunTimeException exception = (BaseRunTimeException) e;
				message = exception.getTipsMessage();
			}else{
				message = "系统异常";
			}
			history.setLoginStatus(LoginHistoryEO.LoginStatus.Failure.toString());
			history.setDescription(message);
			throw e;
		}finally{
			//保存登陆日志
			loginHistoryService.saveEntity(history);
		}
		
		return getObject();
	}
	
	/**
	 * 登录
	 * 
	 * @param uid
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "miniLogin", method = RequestMethod.POST)
	@ResponseBody
	public Object miniLogin(HttpServletRequest request, String uid,String password) throws Exception {
		// 帐号为空
		if (StringUtils.isEmpty(uid)) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入帐号");
		}
		// 密码为空
		if (StringUtils.isEmpty(password)) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入密码");
		}
		//验证账号是否被禁用
		//验证账号是否被禁用
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		UserEO user = userService.getEntity(UserEO.class, params);
		if(user==null){
			throw new BaseRunTimeException(TipsMode.Message.toString(),"用户不存在");
		}else{
			String status = user.getStatus();
			if(StringUtils.isEmpty(status)||UserEO.STATUS.Unable.toString().equals(status)){
				throw new BaseRunTimeException(TipsMode.Message.toString(),"您的账号已被锁定，请联系管理员");
			}
		}
		//MD5加密
		String md5Pwd = DigestUtils.md5Hex(password);
		List<Long> roleIds = null;
		HttpSession session = request.getSession(true);
		if(AdminUidVO.developerUid.equals(uid)||AdminUidVO.superAdminUid.equals(uid)){
			user = userService.login4Developer(uid, md5Pwd);
			session.setAttribute("userId", user.getUserId());
			session.setAttribute("uid", user.getUid());
		}else{
			PersonEO person = userService.login(uid, md5Pwd,ThreadUtil.getString(LocalParamsKey.IP),null);
			roleIds = userService.getRoleIds(person.getOrganId(),person.getUserId());
			session.setAttribute("unitId", person.getUnitId());
			session.setAttribute("organId", person.getOrganId());
			session.setAttribute("organName", person.getOrganName());
			session.setAttribute("personId", person.getPersonId());
			session.setAttribute("personName", person.getName());
			session.setAttribute("userId", person.getUserId());
			session.setAttribute("uid", person.getUid());
		}
		session.setAttribute("roleIds", roleIds);
		RolesCache.getInstance().put(uid, roleIds);
		return getObject();
	}

//	/**
//	 * 系统管理退出
//	 * 
//	 * @return
//	 */
//	@RequestMapping("logout")
//	@ResponseBody
//	public Object logout(HttpServletRequest request) {
//		HttpSession session = request.getSession(false);
//		if(session!=null){
//			session.invalidate();
//			// 向LsSessionContext移除session 用于SWFUpload的session丢失问题
//			LsSessionContext.DelSession(session);
//		}
//		return getObject();
//	}

	/**
	 * 更新用户状态
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	@RequestMapping("updateStatus")
	@ResponseBody
	public Object updateStatus(@RequestParam("userIds") Long[] userIds,
			String status) {
		if (!UserEO.STATUS.Enabled.toString().equals(status)
				&& !UserEO.STATUS.Unable.toString().equals(status)) {
			throw new BaseRunTimeException();
		}
		return getObject(userService.updateStatus(userIds, status));
	}

	/**
	 * 更新用户密码，普通用户可用
	 * 
	 * @param uid
	 * @param password
	 * @param newPassword
	 * @return
	 */
	@RequestMapping("updatePassword")
	public Object updatePassword(String uid, String password, String newPassword) {
		if(StringUtils.isEmpty(password)||StringUtils.isEmpty(password)){
			throw new NullPointerException();
		}
		userService.updatePassword(uid, password, newPassword);
		return getObject();
	}

	/**
	 * 获取用户分页
	 * 
	 * @param index
	 * @param size
	 * @param organId
	 * @param name
	 * @param uid
	 * @param sortFild
	 * @param sortOrder
	 * @return
	 */
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(Long index, Integer size, Long organId, String name,
			String uid, String sortFild, String sortOrder) {
		if (index == null || index < 0) {
			index = 0L;
		}
		if (size == null || size < 0) {
			size = 15;
		}
		boolean isDesc = false;
		if ("desc".equals(sortOrder)) {
			isDesc = true;
		}
		Pagination page = null;
		if (!StringUtils.isEmpty(uid)) {
			page = userService.getPageByUid(index, size, organId, uid,
					sortFild, isDesc);
		} else {
			page = userService.getPageByName(index, size, organId, name,
					sortFild, isDesc);
		}
		return getObject(page);
	}

	@RequestMapping("getUserPage")
	@ResponseBody
	public Object getUserPage(Long pageIndex, Integer pageSize, Long organId,
			String name, String uid, String organName, String sortField,
			String sortOrder) {
		if (pageIndex == null || pageIndex < 0) {
			pageIndex = 0L;
		}
		if (pageSize == null || pageSize < 0) {
			pageSize = 15;
		}
		boolean isDesc = false;
		if ("desc".equals(sortOrder)) {
			isDesc = true;
		}
		Pagination page = userService.getPage(pageIndex, pageSize, organId,
				uid, name, organName, sortField, isDesc);
		return getObject(page);
	}

	/**
	 * 给用户添加角色
	 * 
	 * @author yy
	 * @param roleId
	 * @param users
	 * @return
	 */
	@RequestMapping("saveUA")
	@ResponseBody
	public Object saveUA(Long userId, List<RoleEO> roles) {
		userService.saveUA(userId, roles);
		return this.getObject();
	}
	
	/**
	 * 初始化厂商管理账号
	 *
	 * @param uid
	 * @param password
	 * @return
	 */
	@RequestMapping("initDeveloper")
	@ResponseBody
	public Object initDeveloper(String oldPwd,String newPwd){
		if(StringUtils.isEmpty(newPwd)){
			throw new IllegalArgumentException();
		}
		userService.saveOrUpdateDeveloper(AdminUidVO.developerUid, oldPwd, newPwd);
		return getObject();
	}
	
	/**
	 * 初始化超级管理账号
	 *
	 * @param uid
	 * @param password
	 * @return
	 */
	@RequestMapping("initSuperAdmin")
	@ResponseBody
	public Object initSuperAdmin(String oldPwd,String newPwd){
		if(StringUtils.isEmpty(newPwd)){
			throw new IllegalArgumentException();
		}
		userService.saveOrUpdateSuperAdministrator(AdminUidVO.superAdminUid, oldPwd, newPwd);
		return getObject();
	}
	
	
}
