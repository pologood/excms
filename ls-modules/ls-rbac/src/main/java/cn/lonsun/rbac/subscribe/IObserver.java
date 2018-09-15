package cn.lonsun.rbac.subscribe;


/**
 * 人员信息变动观察对象接口-所有需要在人员进行变动时需要对应进行相关操作的都需要实现该接口
 *
 * @author xujh
 * @version 1.0
 * 2015年3月17日
 *
 */
public interface IObserver {
	
	/**
	 * 新增时调用
	 *
	 * @param obj
	 */
	public boolean execute4Save(Object obj);
	
	/**
	 * 更新时调用
	 *
	 * @param obj
	 */
	public boolean execute4Update(Object obj);
	
	/**
	 * 删除时调用
	 *
	 * @param obj
	 */
	public boolean execute4Delete(Object obj);
	
	

}
