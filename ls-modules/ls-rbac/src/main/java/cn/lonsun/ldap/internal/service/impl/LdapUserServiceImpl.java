package cn.lonsun.ldap.internal.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.exception.CommunicationRuntimeException;
import cn.lonsun.ldap.internal.exception.UidRepeatedException;
import cn.lonsun.ldap.internal.service.IConfigService;
import cn.lonsun.ldap.internal.service.ILdapUserService;
import cn.lonsun.ldap.internal.util.AttributeUtil;
import cn.lonsun.ldap.internal.util.Constants;
import cn.lonsun.ldap.internal.util.LDAPUtil;
import cn.lonsun.rbac.internal.entity.UserEO;

@Service("ldapUserService")
public class LdapUserServiceImpl implements ILdapUserService {
	
	private Logger logger = LoggerFactory.getLogger(LdapUserServiceImpl.class);
	@Autowired
	private IConfigService configService;
	
	private void init(){
		List<ConfigEO> configs = configService.getEntities(ConfigEO.class, new HashMap<String,Object>());
		if(configs!=null&&configs.size()>0){
			String k = "key";
			int i=0;
			for(ConfigEO config:configs){
				try {
					//如果连接成功，那么认为有效
					LDAPUtil.getDirContext(config);
					String key = k+i;
					LDAPUtil.effectiveConfigMap.put(key, config);
					LDAPUtil.connectionCountMap.put(key, 0);
					i++;
				} catch (NamingException e) {
					logger.info("LDAP不可用:"+config.getUrl());
					e.printStackTrace();
				}
			}
			//如果无可用的LDAP，那么抛出异常
			if(i<=0){
				throw new CommunicationRuntimeException();
			}
		}
	}
	
	@Override
	public boolean login(String uid,String md5Pwd,String ip){
		//第一次初始化
		if(LDAPUtil.effectiveConfigMap.size()<=0){
			init();
		}
		boolean isLoginSuccess = false;
		DirContext dc = null;
		String key = null;
		ConfigEO config = null;
		try {
			Object[] objs = LDAPUtil.getDirContextNew();
			key = objs[0].toString();
			dc = (DirContext)objs[1];
			config = LDAPUtil.effectiveConfigMap.get(key);
			int startCount = LDAPUtil.connectionCountMap.get(key)+1;
			LDAPUtil.connectionCountMap.put(key, startCount);
			 if(logger.isInfoEnabled()){
				//释放连接
				 logger.info("连接LDAP("+config.getUrl()+")连接,当前连接数为:"+startCount);
			 }
			// 通过uid和userPassword获取用户
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(&(objectClass=inetOrgPerson)(uid=".concat(uid).concat(")(userPassword=").concat(md5Pwd).concat("))");
			 NamingEnumeration<SearchResult> ne = dc.search(Constants.ROOT_DN, filter, sc);
			 while(ne.hasMore()){
				SearchResult result = ne.next();
				Attributes attrs = result.getAttributes();
				if(attrs!=null){
					String username = AttributeUtil.getValue("uid", attrs.get("uid"));
					if(!StringUtils.isEmpty(username)){
						isLoginSuccess = true;
					}
				}
				break;
			}
			return isLoginSuccess;
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}finally{
			int endCount = LDAPUtil.connectionCountMap.get(key)-1;
			LDAPUtil.connectionCountMap.put(key, endCount);
			if(logger.isInfoEnabled()&&config!=null){
				//释放连接
				logger.info("释放LDAP("+config.getUrl()+")连接,当前连接数为:"+endCount);
			}
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean loginNew(String uid,String md5Pwd,String ip){
		boolean isLoginSuccess = false;
		DirContext dc = null;
		try {
			dc = LDAPUtil.getDirContext();
			// 通过uid和userPassword获取用户
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(&(objectClass=inetOrgPerson)(uid=".concat(uid).concat(")(userPassword=").concat(md5Pwd).concat("))");
			 NamingEnumeration<SearchResult> ne = dc.search(Constants.ROOT_DN, filter, sc);
			 while(ne.hasMore()){
				SearchResult result = ne.next();
				Attributes attrs = result.getAttributes();
				if(attrs!=null){
					String username = AttributeUtil.getValue("uid", attrs.get("uid"));
					if(!StringUtils.isEmpty(username)){
						isLoginSuccess = true;
					}
				}
				break;
			}
			return isLoginSuccess;
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void update(UserEO user){
		String personDn = user.getPersonDn();
		if(StringUtils.isEmpty(personDn)||!personDn.startsWith("cn")){
			throw new IllegalArgumentException();
		}
		Attributes attrs = new BasicAttributes();
		
		DirContext dc = null;
		try {
			
			dc = LDAPUtil.getDirContext();
			//用户状态
			attrs.put("title",user.getStatus());
			//经过MD5加密的密码
			attrs.put("userPassword",user.getPassword()==null?null:user.getPassword());
			dc.modifyAttributes(personDn, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}finally{
			try {
				dc.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void save(UserEO user) throws UidRepeatedException{
		String personDn = user.getPersonDn();
		if(StringUtils.isEmpty(personDn)||!personDn.startsWith("cn")){
			throw new IllegalArgumentException();
		}
		//验证uid是否已存在
		boolean isUidExisted = LDAPUtil.isNodesExisted(Constants.ROOT_DN, "uid=".concat(user.getUid()), SearchControls.SUBTREE_SCOPE);
		if(isUidExisted){
			throw new UidRepeatedException();
		}
		Attributes attrs = new BasicAttributes();
		
		DirContext dc = null;
		try {
			
			dc = LDAPUtil.getDirContext();
			//用户状态
			attrs.put("title",user.getStatus());
			attrs.put("uid",user.getUid());
			//经过MD5加密的密码
			String md5Pwd = user.getPassword();
			attrs.put("userPassword",md5Pwd);
			dc.modifyAttributes(personDn, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}
	}

	@Override
	public void delete(String uid){
		Attributes attrs = new BasicAttributes();
		
		DirContext dc = null;
		try {
			
			dc = LDAPUtil.getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> ne = dc.search(Constants.ROOT_DN, "uid=".concat(uid), sc);
			//uid唯一
			while(ne.hasMore()){
				SearchResult result = ne.next();
				String personDn = result.getNameInNamespace();
				attrs.put("uid","");
				attrs.put("userPassword","");
				dc.modifyAttributes(personDn, DirContext.REPLACE_ATTRIBUTE, attrs);
				break;
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}
	}

	@Override
	public void updatePassword(String uid,String md5Pwd,String md5NewPassword){
		Attributes attrs = new BasicAttributes();
		
		DirContext dc = null;
		try {
			
			dc = LDAPUtil.getDirContext();
			// 通过uid获取用户
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "uid=".concat(uid);
			if(!StringUtils.isEmpty(md5Pwd)){
				filter = "&((uid=".concat(uid).concat(")(userPassword=").concat(md5Pwd).concat("))");
			}else{
				filter = "uid=".concat(uid);
			}
			NamingEnumeration<SearchResult> ne = dc.search(Constants.ROOT_DN, filter, sc);
			//uid唯一
			while(ne.hasMore()){
				SearchResult result = ne.next();
				String personDn = result.getNameInNamespace(); 
				attrs.put("userPassword",md5NewPassword);
				//修改密码
				dc.modifyAttributes(personDn, DirContext.REPLACE_ATTRIBUTE, attrs);
				break;
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}
	}
	
	/**
	 * 更新用户状态</br>
	 * @param uid
	 * @param status
	 */
	public void updateStatus(UserEO user){
		Attributes attrs = new BasicAttributes();
		
		DirContext dc = null;
		try {
			
			dc = LDAPUtil.getDirContext();
			// 更新用户状态
			attrs.put("title",user.getStatus());
			dc.modifyAttributes(user.getPersonDn(), DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}
	}

	@Override
	public UserEO getUser(String uid, String md5Pwd){
		
		DirContext dc = null;
		try {
			UserEO user = null;
			
			dc = LDAPUtil.getDirContext();
			// 通过uid和userPassword获取用户
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(&(uid=".concat(uid).concat(")(userPassword=").concat(md5Pwd).concat("))");
			NamingEnumeration<SearchResult> ne = dc.search(Constants.ROOT_DN, filter, sc);
			while(ne.hasMore()){
				SearchResult result = ne.next();
				user = getUser(result.getAttributes());
				break;
			}
			return user;
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}
	}
	
	@Override
	public UserEO getUser(String personDn){
		//入参验证
		if(StringUtils.isEmpty(personDn)||!personDn.startsWith("cn")){
			throw new IllegalArgumentException();
		}
		
		DirContext dc = null;
		try {
			
			dc = LDAPUtil.getDirContext();
			// 通过uid和userPassword获取用户
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.OBJECT_SCOPE);
			NamingEnumeration<SearchResult> ne = dc.search(personDn, "objectClass=*", sc);
			UserEO user = null;
			while(ne.hasMore()){
				SearchResult result = ne.next();
				user = getUser(result.getAttributes());
				break;
			}
			return user;
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		}
	}
	
	/**
	 * 通过LDAP获取的信息构建用户对象
	 * @param attrs
	 * @return
	 */
	private UserEO getUser(Attributes attrs){
		UserEO user = new UserEO();
		//用户名
		String uid = AttributeUtil.getValue("uid", attrs.get("uid"));
		user.setUid(uid);
		//密码
		String userPassword = AttributeUtil.getValue("userPassword", attrs.get("userPassword"));
		user.setPassword(userPassword);
		return user;
	}

	@Override
	public boolean isUidExisted(String uid) {
		return LDAPUtil.isNodesExisted(Constants.ROOT_DN, "uid=".concat(uid), SearchControls.SUBTREE_SCOPE);
	}

}
