package cn.lonsun.ldap.internal.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.internal.context.ADirContextContainer;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.exception.CommunicationRuntimeException;
import cn.lonsun.ldap.internal.service.IConfigService;
import cn.lonsun.rbac.internal.util.OrganDnContainer;

/**
 * LDAP工具类
 * 
 * @author xujh
 * 
 */
public class LDAPUtil {
	
	private static Logger logger = LoggerFactory.getLogger(LDAPUtil.class);
	//LDAP有效的连接配置
	public static ConfigEO effectiveConfig = null;
	//存放当前可用的LDAP
	public static Map<String,ConfigEO> effectiveConfigMap = new HashMap<String,ConfigEO>();
	//存放LDAP当前的连接数
	public static Map<String,Integer> connectionCountMap = new HashMap<String,Integer>();
	
	/*public static void init(){
		IConfigService configService = SpringContextHolder.getBean("configService");
		List<ConfigEO> configs = configService.getEntities(ConfigEO.class, new HashMap<String,Object>());
		if(configs!=null&&configs.size()>0){
			String k = "key";
			int i=0;
			for(ConfigEO config:configs){
				try {
					//如果连接成功，那么认为有效
					LDAPUtil.getDirContext(config);
					String key = k+i;
					effectiveConfigMap.put(key, config);
					connectionCountMap.put(key, 0);
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
	}*/
	
	/**
	 * 获取连接数最小的ConfigEO
	 *
	 * @return
	 */
	public static String getMinConnectionCountConfigKey(){
		String minConnectionKey = null;
		Set<String> keys = connectionCountMap.keySet();
		int minCount = 0;
		int index = 0;
		for(String key:keys){
			Integer count = connectionCountMap.get(key);
			logger.info("获取Config对应的Key:"+key+",key当前对应的连接数为："+count);
			if(index==0||count<minCount){
				minCount = count;
				minConnectionKey = key;
				logger.info("设置minConnectionKey:"+key+",key当前对应的连接数为："+count);
			}
			index++;
		}
		logger.info("返回的key为："+minConnectionKey);
		return minConnectionKey;
	}
	
	
	/**
	 * 节点类型
	 * 
	 * @author xujh
	 * 
	 */
	public enum NodeType {
		Organization(0), // 组织
		OrganizationalUnit(1), // 单位
		InetOrgPerson(2);// 人员

		private Integer value;

		private NodeType(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return this.value;
		}

	}
	
	/**
	 * 通过Dn获取直属单位开始的组织架构路径
	 *
	 * @param dn
	 * @return
	 */
	public static String getFullName4OrganStartWithDirrectlyOU(String dn){
		String fullName = "";
		String[] simpleDns = dn.split(",");
		for(String simpleDn:simpleDns){
			if(simpleDn.startsWith("cn=")){
				continue;
			}
			String name = OrganDnContainer.getName(simpleDn);
			if(StringUtils.isEmpty(fullName)){
				fullName = name;
			}else{
				fullName = name+">"+fullName;
			}
			if(simpleDn.startsWith("o=")){
				break;
			}
		}
		return fullName;
	}

	/**
	 * Dn生成工具
	 * 
	 * @param parentDn
	 * @return
	 * @throws NamingException
	 */
	public static String getSimpleDn() {
		UUID uuid = UUID.randomUUID();
		return DigestUtils.md5Hex(uuid.toString());
	}

	/**
	 * 获取Dn的父Dn
	 * 
	 * @param dn
	 * @return
	 */
	public static String getParentDn(String dn) {
		String parentDn = null;
		if (!StringUtils.isEmpty(dn)) {
			int index = dn.indexOf(",");
			parentDn = dn.substring(index + 1);
		}
		return parentDn;
	}
	
	/**
	 * 获取当前节点的simpleDn
	 *
	 * @param dn
	 * @return
	 */
	public static String getSimpleDn(String dn){
		String simpleDn = null;
		if (!StringUtils.isEmpty(dn)) {
			simpleDn = dn.split(",")[0];
		}
		return simpleDn;
	}

	/**
	 * 根据dn获取单位的叶子节点
	 * 
	 * @param dn
	 * @return
	 */
	public static String getLastLevelUnitDn(String dn) {
		StringBuffer unitDn = new StringBuffer();
		if (!StringUtils.isEmpty(dn)) {
			String[] subDns = dn.split(",");
			for (String subDn : subDns) {
				if (subDn.startsWith("o=")) {
					if (unitDn.length() > 0) {
						unitDn = unitDn.append(",");
					}
					unitDn = unitDn.append(subDn);
				}
			}
			unitDn.append(",").append(Constants.ROOT_DN);
		}
		return unitDn.toString();
	}

	/**
	 * 获取parentDn下节点类型为nodeType的节点数量
	 * 
	 * @param nodeType
	 * @param parentDn
	 * @return
	 * @throws NamingException
	 */
	public static int getCount(int nodeType, String parentDn)
			throws NamingException {
		// 获取LDAP容器
		DirContext dc = getDirContext();
		// 同一级下同类型节点的数量
		int count = 0;
		try {
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.OBJECT_SCOPE);
			String filter = null;
			switch (nodeType) {
			case 0:
				filter = "objectClass=organization";
				break;
			case 1:
				filter = "objectClass=organization";
				break;
			case 2:
				filter = "objectClass=organization";
				break;
			}
			// LDAP查询
			NamingEnumeration<SearchResult> ne = dc
					.search(parentDn, filter, sc);
			while (ne.hasMore()) {
				count++;
			}
			return count;
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
	 * 判断dn下是否存在子节点
	 * 
	 * @param dc
	 * @param dn
	 * @return
	 * @throws NamingException
	 */
	public static boolean hasSons(DirContext dc, String dn)
			throws NamingException {
		boolean hasSons = false;
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		NamingEnumeration<SearchResult> ne = dc.search(dn, "objectClass=*", sc);
		// 判断组织或单位下是否存在子节点
		if (ne != null && ne.hasMore()) {
			hasSons = true;
		}
		return hasSons;
	}
	
	/**
	 * 创建LDAP上下文
	 * 
	 * @param config
	 * @return
	 * @throws NamingException
	 */
	public static Object[] getDirContextNew() throws NamingException {
		String key = getMinConnectionCountConfigKey();
		ConfigEO config = effectiveConfigMap.get(key);
		DirContext dc = null;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		env.put(Context.PROVIDER_URL, config.getUrl());
//		env.put(Context.PROVIDER_URL, "ldap://61.191.61.136:22889");
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		String principal = "cn=".concat(config.getServerName()).concat(",").concat(Constants.ROOT_DN);
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, config.getPassword());
		try {
			// 初始化上下文
			dc = new InitialDirContext(env);
		} catch (NamingException e) {
			e.printStackTrace();
			//从有效的配置连接中移除
			effectiveConfigMap.remove(key);
			connectionCountMap.remove(key);
			//当没有有效的LDAP时，放弃此次请求
			if(effectiveConfigMap.size()<=0){
				
			}
			return getDirContextNew();
		}
		return new Object[]{key,dc};
	}
	
	/**
	 * 创建LDAP上下文
	 * 
	 * @param config
	 * @return
	 * @throws NamingException
	 */
	public static DirContext getDirContext() throws NamingException {
		DirContext dc = null;
		if (effectiveConfig == null) {
			IConfigService configService = SpringContextHolder.getBean("configService");
			ConfigEO config = configService.getEffectiveConfig();
			if(config==null){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "系统异常，请联系管理员");
			}else{
				effectiveConfig = config;
			}
		}
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		env.put(Context.PROVIDER_URL, effectiveConfig.getUrl());
//		env.put(Context.PROVIDER_URL, "ldap://61.191.61.136:22889");
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		String principal = "cn=".concat(effectiveConfig.getServerName()).concat(",").concat(Constants.ROOT_DN);
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, effectiveConfig.getPassword());
		try {
			// 初始化上下文
			dc = new InitialDirContext(env);
		} catch (NamingException e) {
			e.printStackTrace();
			dc = getNewDirContext();
		}
		return dc;
	}
	
	/**
	 * 当LDAP调用异常后，重新获取有效的配置，并初始化上下文
	 *
	 * @return
	 * @throws NamingException
	 */
	private static DirContext getNewDirContext() throws NamingException {
		IConfigService configService = SpringContextHolder.getBean("configService");
		ConfigEO config = configService.getEffectiveConfig();
		if(config==null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "系统异常，请联系管理员");
		}else{
			effectiveConfig = config;
		}
		DirContext dc = null;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		env.put(Context.PROVIDER_URL, effectiveConfig.getUrl());
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		String principal = "cn=".concat(effectiveConfig.getServerName()).concat(",").concat(Constants.ROOT_DN);
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, effectiveConfig.getPassword());
		try {
			// 初始化上下文
			dc = new InitialDirContext(env);
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		}
		return dc;
	}

	/**
	 * 创建LDAP上下文
	 * 
	 * @param config
	 * @return
	 * @throws NamingException
	 */
	public static DirContext getDirContext(ConfigEO config)
			throws NamingException {
		if (config == null) {
			throw new NullPointerException();
		}
		long startTime = new Date().getTime();
		InitialDirContext dc = null;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.PROVIDER_URL, config.getUrl());
//		env.put(Context.PROVIDER_URL, "ldap://61.191.61.136:22889");
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		String principal = "cn=".concat(config.getServerName()).concat(",").concat(Constants.ROOT_DN);
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, config.getPassword());
		try {
			System.out.println("请求url:" + config.getUrl() + ",password:"
					+ config.getPassword());
			// 初始化上下文
			dc = new InitialDirContext(env);
			System.out.println("认证成功");
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		} finally {
			System.out.println("==============用去了"
					+ (new Date().getTime() - startTime) + "ms去连接");
		}
		return dc;
	}

	/**
	 * 关闭Ldap连接
	 */
	public static void close(DirContext dc) {
		if (dc != null) {
			try {
				dc.close();
			} catch (NamingException e) {
				System.out.println("NamingException in close():" + e);
			}
		}
	}

	/**
	 * 验证连接是否有效</br>
	 * JNDI没有提供检查连接是否有效的方法，因此必须通过一次LDAP操作来确定该Context实例是可用
	 * @param dc
	 * @return
	 */
	public static boolean isContextAvailable(DirContext dc) {
		boolean isContextAvailable = true;
		// 查询条件
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.OBJECT_SCOPE);
		try {
			dc.search(Constants.ROOT_DN, "(objectclass=*)", sc);
		} catch (NamingException e) {
			e.printStackTrace();
			isContextAvailable = false;
		}
		return isContextAvailable;
	}

	/**
	 * 符合条件的节点是否存在
	 * 
	 * @param parentDn
	 * @param filter
	 * @param scope
	 * @return
	 */
	public static boolean isNodesExisted(String parentDn, String filter,
			int scope) {
		boolean isNodesExisted = false;
		// 获取LDAP上下文
		ADirContextContainer container = null;
		DirContext dc = null;
		try {
			dc = getDirContext();
			// 查询条件
			SearchControls sc = new SearchControls();
			sc.setSearchScope(scope);
			NamingEnumeration<SearchResult> ne = dc
					.search(parentDn, filter, sc);
			if (ne != null) {
				while (ne.hasMore()) {
					// 符合条件的节点是否存在
					isNodesExisted = true;
					ne.next();
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
			if(e instanceof CommunicationException || e instanceof ServiceUnavailableException) {
				throw new CommunicationRuntimeException();
			}else{
				throw new BaseRunTimeException();
			}
		} finally {
			// 释放LDAP链接上下文
			if (container != null && dc != null) {
				container.release(dc);
			}
		}
		return isNodesExisted;
	}
}
