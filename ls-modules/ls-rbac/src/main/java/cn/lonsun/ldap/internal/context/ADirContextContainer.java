package cn.lonsun.ldap.internal.context;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import cn.lonsun.ldap.internal.entity.ConfigEO;

/**
 * LDAP上下文容器抽象类
 * 
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:08:19
 * @version V1.0
 */
public abstract class ADirContextContainer {

	// 当前获取上下文的配置
	protected ConfigEO currentConfig = null;

	/**
	 * 初始化上下文容器
	 */
	public abstract void init();

	/**
	 * 释放LDAP上下文
	 * 
	 * @param context
	 */
	public abstract void release(DirContext context);

	/**
	 * 清空容器
	 */
	public abstract void clear();

	/**
	 * 在上下文池中获取上下文
	 * 
	 * @return
	 * @throws NamingException
	 */
	public abstract DirContext getContext() throws NamingException;

	public ConfigEO getCurrentConfig() {
		return currentConfig;
	}

	public void setCurrentConfig(ConfigEO currentConfig) {
		this.currentConfig = currentConfig;
	}

}
