package cn.lonsun.rbac.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.AccessPolicyEO;

/**
 * Ip访问策略服务类
 * @author Administrator
 *
 */
public interface IAccessPolicyService extends IBaseService<AccessPolicyEO> {
	
	/**
	 * 验证ip是否允许访问系统
	 *
	 * @param ip
	 * @return
	 */
	public boolean isIpAccessable(String ip);
	
	/**
	 * 获取所有的策略对象
	 * @return
	 */
	public List<AccessPolicyEO> getPolicys();
	
	/**
	 * 获取分页
	 * @param index
	 * @param size
	 * @return
	 */
	public Pagination getPage(Long index,Integer size);
	
	/**
	 * 获取访问策略对象
	 * @param isEnable
	 * @return
	 */
	public List<AccessPolicyEO> getPolicys(boolean isEnable);
}
