package cn.lonsun.ldap.internal.context.impl;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.ldap.internal.context.ADirContextContainer;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.exception.LDAPDisconnectionException;
import cn.lonsun.ldap.internal.service.IConfigService;
import cn.lonsun.ldap.internal.util.LDAPUtil;

/**
 * DirContext容器，该容器每次使用都获取新的上下文
 * 
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:09:00
 * @version V1.0
 */
public class DirContextContainer extends ADirContextContainer {

	// 用于存放有效的配置，可直接哪来使用
	private static ConfigEO effectiveConfig = null;
	// LDAP连接上下文
	private DirContext context = null;

	@Override
	public void init() throws LDAPDisconnectionException {
		IConfigService configService = SpringContextHolder.getBean("configService");
		DirContext context = null;
		// 使用静态的有效配置
		if (effectiveConfig != null) {
			try {
				context = LDAPUtil.getDirContext(effectiveConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 静态配置获取上下文失败后，获取新的有效的配置
		if (context == null) {
			ConfigEO config = configService.getEffectiveConfig();
			DirContextContainer.effectiveConfig = config;
			try {
				context = LDAPUtil.getDirContext(config);
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		this.context = context;

	}

	@Override
	public void release(DirContext context) {
		if (context != null) {
			// 只能释放该对象内的DirContext，即this.context
			if (!context.equals(this.context)) {
				throw new IllegalArgumentException(
						"The instance of DirContext which is argument  can not be released by this object.");
			}
			try {
				context.close();
			} catch (NamingException e) {
				e.printStackTrace();
				// 如果关闭出现异常，那么设置context引用为null，等待jvm进行回收
				context = null;
			}
		}
	}

	@Override
	public DirContext getContext() throws NamingException {
		return context;
	}

	@Override
	public void clear() {
	}

}
