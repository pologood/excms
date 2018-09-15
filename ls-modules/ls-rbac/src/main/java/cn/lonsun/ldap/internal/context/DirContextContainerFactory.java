package cn.lonsun.ldap.internal.context;

import cn.lonsun.ldap.internal.context.impl.DirContextPool;

/**
 * IDirContextContainer工厂，每一个类的对象都是单例
 * 
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:08:11
 * @version V1.0
 */
public class DirContextContainerFactory<T extends ADirContextContainer> {

	// 用于存储实例化的对象，key:class.simpleName,value:容器对象
	private static DirContextPool pool = new DirContextPool();

	/**
	 * 获取连接池对象
	 * 
	 * @return
	 */
	public static DirContextPool getPool() {
		return pool;
	}

//	/**
//	 * 获取目录访问上下文
//	 * 
//	 * @return
//	 * @throws NamingException
//	 */
//	public static ADirContextContainer getContainer() {
//		ADirContextContainer container = null;
//		if (DirContextPool.isPoolNormal) {
//			container = pool;
//		}
//		// container 为空，说明pool不可用，此时创建临时的上下文
//		if (container == null) {
//			container = new DirContextContainer();
//			container.init();
//		}
//		return container;
//	}
}
