package cn.lonsun.rbac.subscribe;

public interface IPersonObserver extends IObserver {
	
	/**
	 * 更新密码时执行
	 *
	 * @param obj
	 * @return
	 */
	public boolean execute4UpdatePassword(Object obj);
	
}
