package cn.lonsun.ldap.internal.service;

import java.util.List;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.ldap.internal.entity.ConfigEO;

public interface IConfigService extends IMockService<ConfigEO>{
	
	/**
	 * 获取优先级最高的有效配置
	 * @return
	 */
	public ConfigEO getEffectiveConfig();
	
	/**
	 * URL除了本身之外是否还已存在
	 *
	 * @param config
	 * @return
	 */
	public boolean isUrlExistedExceptSelf(ConfigEO config);
	
	/**
	 * 获取密码
	 *
	 * @param configId
	 * @return
	 */
	public String getPassword(Long configId);
	
	/**
	 * 获取所有的配置，并按优先级进行排序
	 * @return
	 */
	public List<ConfigEO> getConfigs();
	
	/**
	 * 获取分页
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPagination(PageQueryVO query);
	
	/**
	 * 获取最大的排序号
	 *
	 * @return
	 */
	public Integer getMaxSortNum();
}
