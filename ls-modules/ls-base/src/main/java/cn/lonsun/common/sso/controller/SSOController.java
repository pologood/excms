package cn.lonsun.common.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.base.util.SessionConcurrentMap;
import cn.lonsun.common.enums.ErrorCodes;
import cn.lonsun.common.sso.service.ISSOService;
import cn.lonsun.common.sso.util.DESedeUtil;
import cn.lonsun.common.sso.util.EncryptKey;
import cn.lonsun.common.sso.vo.UserVO;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PersonInfoVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.JSONHelper;
import cn.lonsun.core.util.SessionUtil;



/**
 * 单点登录控制器
 */
@Controller
@RequestMapping(value = "sso", produces = {"application/json;charset=UTF-8"})
public class SSOController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ISSOService ssoService;
	
	/**
	 * 应用模块登录
	 *
	 * @param uid
	 * @param token
	 * @param url
	 * @param request
	 * @return
	 */
	@RequestMapping("login")
	public String login(String uid,String token,String url,HttpServletRequest request){
		if(StringUtils.isEmpty(uid)||StringUtils.isEmpty(token)||StringUtils.isEmpty(url)){
			throw new BaseRunTimeException();
		}
		HttpSession session = request.getSession(false);
		PersonInfoVO person = null;
//    	PersonInfoVO person = ssoService.validateToken(uid, token,null);
    	//认证成功
		if(person!=null && PersonInfoVO.Status.SUCCESS.toString().equals(person.getStatus())){
			//清空session中所有的数据，部分业务在session中存放了信息，但切换单位或重新登录后需要重新加载
			SessionUtil.clear(session);
			//认证成功后将用户信息保存到Session中
            UserVO user = new UserVO();
            user.setUserId(person.getUserId());
            user.setUid(person.getUid());
            user.setOrganId(person.getOrganId());
            user.setOrganName(person.getOrganName());
            user.setPersonId(person.getPersonId());
            user.setPersonName(person.getPersonName());
            user.setUnitId(person.getUnitId());
            user.setUnitName(person.getUnitName());
            user.setCoreMail(person.getCoreMail());
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("uid", user.getUid());
            session.setAttribute("organId", user.getOrganId());
            session.setAttribute("organName", user.getOrganName());
            session.setAttribute("personId", user.getPersonId());
            session.setAttribute("personName", user.getPersonName());
            session.setAttribute("coreMail", user.getCoreMail());
            session.setAttribute("unitId", user.getUnitId());
            session.setAttribute("unitName", user.getUnitName());
		}
		return url;
	}

    /**
     * 单点登录token验证
     * @param uid
     * @param token
     * @return
     * @throws Exception
     */
	@RequestMapping("ssoValidate")
	@ResponseBody
    public Object ssoValidate(Long organId,String uid,String token,HttpServletRequest request) throws Exception{
    	if(AppUtil.isEmpty(uid) || AppUtil.isEmpty(token))
            return ajaxErr("uid或token参数错误");
    	HttpSession session = request.getSession(false);
    	//消除之前创建的临时会话session
    	if(session!=null&&!session.isNew()){
    		session.invalidate();
    	}
    	//为登陆用户创建session
    	session = request.getSession();
//    	PersonInfoVO person = ssoService.validateToken(uid, token, organId);
    	PersonInfoVO person = null;
        logger.info("person="+ JSONHelper.toJSON(person));
    	//认证成功
        if (person != null) {
        	//清空session中所有的数据，部分业务在session中存放了信息，但切换单位或重新登录后需要重新加载
			SessionUtil.clear(session);
            //认证成功后将用户信息保存到Session中
            UserVO user = new UserVO();
            user.setUserId(person.getUserId());
            user.setUid(person.getUid());
            user.setOrganId(person.getOrganId());
            user.setOrganName(person.getOrganName());
            user.setPersonId(person.getPersonId());
            user.setPersonName(person.getPersonName());
            user.setUnitId(person.getUnitId());
            user.setUnitName(person.getUnitName());
            user.setCoreMail(person.getCoreMail());
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("uid", user.getUid());
            session.setAttribute("organId", user.getOrganId());
            session.setAttribute("organName", user.getOrganName());
            session.setAttribute("personId", user.getPersonId());
            session.setAttribute("personName", user.getPersonName());
            session.setAttribute("coreMail", user.getCoreMail());
            session.setAttribute("unitId", user.getUnitId());
            session.setAttribute("unitName", user.getUnitName());
			logger.info("执行SSO单点登录成功,sessionid:" + session.getId());
			SessionConcurrentMap.SESSION_MAP.put(user.getUid(),session);
			return ajaxOk(session.getId());
        }
        return ajaxErr("您的会话已过期，请重新登录");

    }
	
	/**
	 * 用户注销
	 *
	 * @param session
	 * @param uid
	 */
	@RequestMapping("logout")
	@ResponseBody
	public Object logout(HttpServletRequest request,String uid){
		HttpSession session = request.getSession();
		logger.info("开始执行注销操作 >>> sessionid:"+session.getId());
		//默认为退出失败
		String code = ErrorCodes.LogoutFailure.toString();
		logger.info("/sso/logout >>> uid : " + uid);
		if(!AppUtil.isEmpty(uid)){
			//uid解密
			logger.info("uid解密 >>> ");
			String key = EncryptKey.getInstance().getKey();
			try {
				String target = DESedeUtil.decrypt(uid, key);
				logger.info("uid解密成功,target : " + target);
				logger.info("检查当前SESSION_MAP中是否存在此用户的session >>> ");
				//查询session
				if(SessionConcurrentMap.SESSION_MAP.containsKey(target)){
					logger.info("用户session存在,取出session并注销 >>> ");
					session = SessionConcurrentMap.SESSION_MAP.get(target);
					if(session==null){
						logger.info("session is null >>> ");
					}else{
						logger.info("session is ok >>> sessionid: " + session.getId());
					}
					//注销session
					session.invalidate();
					request.getSession().invalidate();
					//设置注销成功标记
					code = ErrorCodes.LogoutSuccess.toString();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			// 此处是取不到uid的
			String targetUid = SessionUtil.getStringProperty(session, "uid");
			logger.info("targetUid : " + targetUid);
			if(!AppUtil.isEmpty(targetUid)){

				try {
					String target = DESedeUtil.decrypt(uid, key);
					logger.info("target : " + target);
					logger.info("target.equals(targetUid) : " + String.valueOf(target.equals(targetUid)));

					if(target.equals(targetUid)){

						//注销session
						session.invalidate();
						//设置注销成功标记
						code = ErrorCodes.LogoutSuccess.toString();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/
		}
		return ajaxOk(code);
	}

	
	@RequestMapping("test")
	public void test(String uid) {
		//uid解密
		String key = EncryptKey.getInstance().getKey();
		try {
			System.out.println("待加密的uid："+uid);
			String encryptedUid = DESedeUtil.encrypt(uid, key);
			System.out.println("加密后uid："+encryptedUid);
			String decryptedUid = DESedeUtil.decrypt(encryptedUid, key);
			System.out.println("解密后uid："+decryptedUid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

