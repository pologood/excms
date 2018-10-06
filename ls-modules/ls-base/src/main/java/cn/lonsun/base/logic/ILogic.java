package cn.lonsun.base.logic;

/**
 * 多种业务逻辑扩展接口，系统中所可能存在多种实现的业务逻辑，以及将来可能会有
 * 扩展或变更的业务都将使用此接口，然后通过系统新开发的开关配置项来自动切换业务
 */
public interface ILogic<T> {

	/**
	 * 执行业务
	 *
	 * @param object 
	 * @return
	 */
	public T execute(Object... object);

}
