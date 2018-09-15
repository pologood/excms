package cn.lonsun.rbac.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.AccessPolicyEO;

/**
 * 访问策略借口
 * 
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:11:52
 * @version V1.0
 */
public interface IAccessPolicyDao extends IBaseDao<AccessPolicyEO> {
	
	/**
	 * 获取IP集合
	 *
	 * @param blurryIp
	 * @param isEnable
	 * @return
	 */
	public List<AccessPolicyEO> getPolicys(String blurryIp,Boolean isEnable);

	/**
	 * 获取所有的策略对象
	 * 
	 * @return
	 */
	public List<AccessPolicyEO> getPolicys();
	
	/**
	 * 获取访问策略对象
	 * @param isEnable
	 * @return
	 */
	public List<AccessPolicyEO> getPolicys(boolean isEnable);

	/**
	 * 获取分页
	 * 
	 * @param index
	 * @param size
	 * @return
	 */
	public Pagination getPage(Long index, Integer size);

}
